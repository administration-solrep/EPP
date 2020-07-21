/*
 * (C) Copyright 2006-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     bstefanescu
 */
package org.nuxeo.connect.update.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.nuxeo.common.Environment;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.common.utils.ZipUtils;
import org.nuxeo.connect.update.LocalPackage;
import org.nuxeo.connect.update.PackageException;
import org.nuxeo.connect.update.PackageState;
import org.nuxeo.connect.update.task.Task;

/**
 *
 * The file nxserver/data/packages/.packages is storing the state of all local
 * features
 *
 * Each local package have a corresponding directory in
 * nxserver/data/features/store which is named: <package_uid> ("id-version")
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class PackagePersistence {

    private static final String FEATURES_DIR = "packages";

    protected final File root;

    protected final File store;

    protected final File temp;

    protected final Random random = new Random();

    protected Map<String, Integer> states;

    public PackagePersistence() throws IOException {
        // check if we should use a custom dataDir - useful for offline update
        String dataDir = System.getProperty("org.nuxeo.connect.update.dataDir");
        if (dataDir != null) {
            root = new File(new File(dataDir), FEATURES_DIR);
        } else {
            root = new File(Environment.getDefault().getData(), FEATURES_DIR);
        }
        root.mkdirs();
        store = new File(root, "store");
        store.mkdirs();
        temp = new File(root, "tmp");
        temp.mkdirs();
        states = new LinkedHashMap<String, Integer>();
        states = loadStates();
    }

    public File getRoot() {
        return root;
    }

    public synchronized Map<String, Integer> getStates() {
        return new HashMap<String, Integer>(states);
    }

    protected Map<String, Integer> loadStates() throws IOException {
        Map<String, Integer> result = new HashMap<String, Integer>();
        File file = new File(root, ".packages");
        if (file.isFile()) {
            List<String> lines = FileUtils.readLines(file);
            for (String line : lines) {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                int i = line.indexOf('=');
                String key = line.substring(0, i).trim();
                Integer val = null;
                try {
                    val = Integer.valueOf(line.substring(i + 1).trim());
                } catch (NumberFormatException e) { // silently ignore
                    val = new Integer(PackageState.REMOTE);
                }
                result.put(key, val);
            }
        }
        return result;
    }

    protected void writeStates(Map<String, Integer> states) throws IOException {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, Integer> entry : states.entrySet()) {
            buf.append(entry.getKey()).append('=').append(
                    entry.getValue().toString()).append("\n");
        }
        File file = new File(root, ".packages");
        FileUtils.writeFile(file, buf.toString());
    }

    public LocalPackage getPackage(String id) throws PackageException {
        File file = new File(store, id);
        if (file.isDirectory()) {
            return new LocalPackageImpl(file, getState(id));
        }
        return null;
    }

    public synchronized LocalPackage addPackage(File file)
            throws PackageException {
        if (file.isDirectory()) {
            return addPackageFromDir(file);
        } else if (file.isFile()) {
            File tmp = newTempDir(file.getName());
            try {
                ZipUtils.unzip(file, tmp);
                return addPackageFromDir(tmp);
            } catch (IOException e) {
                throw new PackageException("Faild to unzip package: "
                        + file.getName());
            } finally {
                if (tmp.isDirectory()) { // should never happen
                    FileUtils.deleteTree(tmp);
                }
            }
        } else {
            throw new PackageException("Not a file: " + file);
        }
    }

    protected LocalPackage addPackageFromDir(File file) throws PackageException {
        LocalPackageImpl pkg = new LocalPackageImpl(file,
                PackageState.DOWNLOADED);
        File dir = new File(store, pkg.getId());
        if (dir.exists()) {
            if (pkg.getId().endsWith("-0.0.0-SNAPSHOT")) {
                // this is a special case - reload a studio snapshot package
                // 1. first we need to uninstall the existing package
                LocalPackage oldpkg = getPackage(pkg.getId());
                if (oldpkg.getState() >= PackageState.INSTALLED) {
                    Task utask = oldpkg.getUninstallTask();
                    try {
                        utask.run(new HashMap<String, String>());
                    } catch (Throwable t) {
                        utask.rollback();
                        throw new PackageException(
                                "Failed to uninstall snapshot. Abort reloading: "
                                        + pkg.getId(), t);
                    }
                }
                // 2. remove the package data
                FileUtils.deleteTree(dir);
            } else {
                throw new PackageException("Package " + pkg.getId()
                        + " already exists");
            }
        }
        file.renameTo(dir);
        pkg.data.setRoot(dir);
        updateState(pkg.getId(), pkg.getState());
        return pkg;
    }

    public synchronized int getState(String featureId) throws PackageException {
        Integer state = states.get(featureId);
        if (state == null) {
            return 0;
        }
        return state;
    }

    /**
     * Get the local package having the given name and which is in either one of
     * the following states:
     * <ul>
     * <li> {@link PackageState#INSTALLING}
     * <li> {@link PackageState#INSTALLED}
     * <li> {@link PackageState#STARTED}
     * </ul>
     *
     * @param name
     * @return
     */
    public LocalPackage getActivePackage(String name) throws PackageException {
        String pkgId = getActivePackageId(name);
        if (pkgId == null) {
            return null;
        }
        return getPackage(pkgId);
    }

    public synchronized String getActivePackageId(String name) {
        name = name + '-';
        for (Map.Entry<String, Integer> entry : states.entrySet()) {
            if (entry.getKey().startsWith(name)
                    && entry.getValue() >= PackageState.INSTALLING) {
                return entry.getKey();
            }
        }
        return null;
    }

    public synchronized List<LocalPackage> getPackages()
            throws PackageException {
        File[] list = store.listFiles();
        if (list != null) {
            List<LocalPackage> pkgs = new ArrayList<LocalPackage>(list.length);
            for (File file : list) {
                pkgs.add(new LocalPackageImpl(file, getState(file.getName())));
            }
            return pkgs;
        }
        return new ArrayList<LocalPackage>();
    }

    public synchronized void removePackage(String id) {
        states.remove(id);
        File file = new File(store, id);
        if (file.isDirectory()) {
            FileUtils.deleteTree(file);
        }
    }

    public synchronized void updateState(String id, int state)
            throws PackageException {
        states.put(id, state);
        try {
            writeStates(states);
        } catch (IOException e) {
            throw new PackageException("Failed to write package states", e);
        }
    }

    protected File newTempDir(String id) {
        File tmp = new File(temp, id + "-" + random.nextInt());
        synchronized (temp) {
            // FIXME: logic error here - while doesn't loop!
            while (tmp.exists()) {
                return newTempDir(id);
            }
            tmp.mkdirs();
        }
        return tmp;
    }
}
