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
import org.nuxeo.connect.update.ValidationStatus;
import org.nuxeo.connect.update.impl.task.AbstractCommand;
import org.nuxeo.connect.update.impl.task.Command;
import org.nuxeo.connect.update.impl.xml.XmlWriter;
import org.nuxeo.connect.update.task.Task;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.reload.ReloadService;
import org.w3c.dom.Element;

/**
 * Install bundle, flush any application cache and perform Nuxeo preprocessing
 * on the bundle.
 *
 * The inverse of this command is Undeploy.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class Undeploy extends AbstractCommand {

    public static final String ID = "undeploy";

    protected File file;

    public Undeploy() {
        super(ID);
    }

    public Undeploy(File file) {
        super(ID);
        this.file = file;
    }

    @Override
    protected void doValidate(Task task, ValidationStatus status)
            throws PackageException {
        // do nothing
    }

    @Override
    protected Command doRun(Task task, Map<String, String> prefs)
            throws PackageException {
        try {
            new Uninstall(file).doRun(task, prefs);
            // TODO is this really needed - anyway a complete flush is made
            // after an install/uninstall - see CommandsTask.doRun
            Framework.getLocalService(ReloadService.class).reloadRepository();
        } catch (Exception e) {
            throw new PackageException("Failed to undeploy bundle " + file, e);
        }
        return new Deploy(file);
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
