/*
 * (C) Copyright 2006-2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
package org.nuxeo.connect.update.impl.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.nuxeo.common.Environment;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.connect.update.LocalPackage;
import org.nuxeo.connect.update.PackageException;
import org.nuxeo.connect.update.PackageState;
import org.nuxeo.connect.update.PackageUpdateService;
import org.nuxeo.connect.update.ValidationStatus;
import org.nuxeo.connect.update.task.Task;
import org.nuxeo.runtime.api.Framework;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public abstract class AbstractTask implements Task {

    public static final String PKG_ID = "package.id";

    public static final String PKG_NAME = "package.name";

    public static final String PKG_VERSION = "package.version";

    public static final String PKG_ROOT = "package.root";

    public static final String ENV_HOME = "env.home";

    /**
     * Set only on JBoss - the EAR root directory path
     */
    public static final String ENV_EAR = "env.ear";

    public static final String ENV_LIB = "env.lib";

    public static final String ENV_SYSLIB = "env.syslib";

    public static final String ENV_BUNDLES = "env.bundles";

    public static final String ENV_CONFIG = "env.config";

    public static final String ENV_TIMESTAMP = "sys.timestamp";

    /**
     * The host application name.
     * 
     * @see Environment#getHostApplicationName()
     */
    public static final String ENV_HOSTAPP_NAME = "env.hostapp.name";

    /**
     * The host application version
     * 
     * @see Environment#getHostApplicationVersion()
     */
    public static final String ENV_HOSTAPP_VERSION = "env.hostapp.version";

    protected boolean restart;

    protected LocalPackage pkg;

    /**
     * A map of environment key/values that can be used in XML install files as
     * variables.
     */
    protected final Map<String, String> env;

    protected AbstractTask() {
        env = new HashMap<String, String>();
        Environment nxenv = Environment.getDefault();
        File home = nxenv.getHome();
        File config = nxenv.getConfig();
        env.put(ENV_HOME, home.getAbsolutePath());
        env.put(ENV_CONFIG, config.getAbsolutePath());
        env.put(ENV_HOSTAPP_NAME, nxenv.getHostApplicationName());
        env.put(ENV_HOSTAPP_VERSION, nxenv.getHostApplicationVersion());
        if (nxenv.isJBoss()) {
            File ear = config.getParentFile();
            env.put(ENV_EAR, ear.getAbsolutePath());
            env.put(ENV_LIB, new File(ear, "lib").getAbsolutePath());
            env.put(ENV_BUNDLES, new File(ear, "bundles").getAbsolutePath());
            String v = System.getProperty("jboss.server.home.dir");
            if (v != null) {
                env.put(ENV_SYSLIB, v + "/lib");
            }
        } else {
            if (nxenv.isTomcat()) {
                String v = System.getProperty("catalina.base");
                if (v != null) {
                    env.put(ENV_SYSLIB, v + "/lib");
                }
            } else {
                env.put(ENV_SYSLIB, new File(home, "lib").getAbsolutePath());
            }
            env.put(ENV_LIB, new File(home, "lib").getAbsolutePath());
            env.put(ENV_BUNDLES, new File(home, "bundles").getAbsolutePath());
        }
        env.put(ENV_TIMESTAMP,
                new SimpleDateFormat("yyMMddHHmmss").format(new Date()));
    }

    public abstract boolean isInstallTask();

    public void initialize(LocalPackage pkg, boolean restart)
            throws PackageException {
        this.pkg = pkg;
        this.restart = restart;
        env.put(PKG_ID, pkg.getId());
        env.put(PKG_NAME, pkg.getName());
        env.put(PKG_VERSION, pkg.getVersion().toString());
        env.put(PKG_ROOT, pkg.getData().getRoot().getAbsolutePath());
    }

    /**
     * Get a file given its key in the environment map. If no key exists then
     * null is returned.
     *
     * @param key
     * @return
     */
    public File getFile(String key) {
        String val = env.get(key);
        return val == null ? null : new File(val);
    }

    public boolean isRestartRequired() {
        return restart;
    }

    public LocalPackage getPackage() {
        return pkg;
    }

    protected Map<Object, Object> createContextMap(Map<String, String> params) {
        Map<Object, Object> map = new HashMap<Object, Object>(
                System.getProperties());
        map.putAll(env);
        if (params != null && !params.isEmpty()) {
            map.putAll(params);
        }
        return map;
    }

    protected String loadParametrizedFile(File file, Map<String, String> params)
            throws IOException {
        String content = FileUtils.readFile(file);
        // replace variables.
        return StringUtils.expandVars(content, createContextMap(params));
    }

    protected void saveParams(Map<String, String> params)
            throws PackageException {
        if (params == null || params.isEmpty()) {
            return;
        }
        try {
            Properties props = new Properties();
            props.putAll(params);
            File file = pkg.getData().getEntry(LocalPackage.INSTALL_PROPERTIES);
            FileOutputStream out = new FileOutputStream(file);
            try {
                props.store(out, "user install parameters");
            } finally {
                out.close();
            }
        } catch (Throwable t) {
            throw new PackageException("Failed to save install parameters", t);
        }
    }

    public synchronized void run(Map<String, String> params)
            throws PackageException {
        PackageUpdateService service = Framework.getLocalService(PackageUpdateService.class);
        if (isInstallTask()) {
            LocalPackage oldpkg = service.getActivePackage(pkg.getName());
            if (oldpkg != null) {
                if (oldpkg.getState() == PackageState.INSTALLING) {
                    throw new PackageException(
                            "Another package with the same name is installing: "
                                    + oldpkg.getName());
                } else {
                    // uninstall it.
                    Task utask = oldpkg.getUninstallTask();
                    try {
                        utask.run(new HashMap<String, String>());
                    } catch (Throwable t) {
                        utask.rollback();
                        throw new PackageException("Failed to uninstall: "
                                + oldpkg.getId()
                                + ". Cannot continue installation of "
                                + pkg.getId(), t);
                    }
                }
            }
        }
        service.setPackageState(pkg, PackageState.INSTALLING);
        saveParams(params);
        doRun(params);
        taskDone();
    }

    protected abstract void rollbackDone() throws PackageException;

    protected abstract void taskDone() throws PackageException;

    public void rollback() throws PackageException {
        try {
            doRollback();
        } finally {
            rollbackDone();
        }
    }

    public void setRestartRequired(boolean isRestartRequired) {
        this.restart = isRestartRequired;
    }

    protected abstract void doRun(Map<String, String> params)
            throws PackageException;

    protected abstract void doRollback() throws PackageException;

    public ValidationStatus validate() throws PackageException {
        ValidationStatus status = new ValidationStatus();
        if (isInstallTask()) {
            validateInstall(status);
        }
        doValidate(status);
        return status;
    }

    public abstract void doValidate(ValidationStatus status)
            throws PackageException;

    protected LocalPackage validateInstall(ValidationStatus status)
            throws PackageException {
        PackageUpdateService pus = Framework.getLocalService(PackageUpdateService.class);
        LocalPackage oldpkg = pus.getActivePackage(pkg.getName());
        if (oldpkg != null) {
            if (oldpkg.getState() == PackageState.INSTALLING) {
                status.addWarning("A package with the same name: "
                        + oldpkg.getId()
                        + " is being installing. Try again later.");
            } else {
                status.addWarning("The package " + oldpkg.getId()
                        + " will be uninstalled!");
            }
            return oldpkg;
        }
        return null;
    }
}
