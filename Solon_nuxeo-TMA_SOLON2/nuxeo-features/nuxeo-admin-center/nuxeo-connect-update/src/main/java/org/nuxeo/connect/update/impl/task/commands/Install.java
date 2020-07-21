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
package org.nuxeo.connect.update.impl.task.commands;

import java.io.File;
import java.util.Map;

import org.nuxeo.connect.update.PackageException;
import org.nuxeo.connect.update.PackageUpdateComponent;
import org.nuxeo.connect.update.ValidationStatus;
import org.nuxeo.connect.update.impl.task.AbstractCommand;
import org.nuxeo.connect.update.impl.task.Command;
import org.nuxeo.connect.update.impl.xml.XmlWriter;
import org.nuxeo.connect.update.task.Task;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.w3c.dom.Element;

/**
 * Deploy an OSGi bundle into the running platform. The bundle is specified
 * using the absolute bundle file path. The inverse of this command is the
 * Undeploy command.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Install extends AbstractCommand {

    public static final String ID = "install";

    protected File file;

    public Install() {
        super(ID);
    }

    public Install(File file) {
        super(ID);
        this.file = file;
    }

    @Override
    protected void doValidate(Task task, ValidationStatus status)
            throws PackageException {
        if (file == null) {
            status.addError("Invalid install syntax: No file specified");
        }
    }

    @Override
    protected Command doRun(Task task, Map<String, String> prefs)
            throws PackageException {
        BundleContext ctx = PackageUpdateComponent.getContext().getBundle().getBundleContext();
        try {
            Bundle bundle = ctx.installBundle(file.getAbsolutePath());
            if (bundle.getState() == Bundle.UNINSTALLED) {
                ctx.installBundle(file.getAbsolutePath());
            }
            bundle.start();
        } catch (BundleException e) {
            throw new PackageException("Failed to install bundle "
                    + file.getName());
        }
        return new Uninstall(file);
    }

    public void readFrom(Element element) throws PackageException {
        String v = element.getAttribute("file");
        if (v.length() > 0) {
            file = new File(v);
            guardVars.put("file", file);
        }
    }

    public void writeTo(XmlWriter writer) {
        writer.start(ID);
        if (file != null) {
            writer.attr("file", file.getAbsolutePath());
        }
        writer.end();
    }
}
