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
package org.nuxeo.connect.update.commands;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.connect.update.task.Task;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class TestCopyUninstallValidation extends TestCopy {

    @Override
    protected void installDone(Task task, Throwable error) throws Exception {
        super.installDone(task, error);
        // modify the target file so that uninstall fails
        FileUtils.writeFile(getTargetFile(), "modified file");
    }

    @Override
    protected void uninstallDone(Task task, Throwable error) throws Exception {
        if (error != null) {
            error.printStackTrace();
            fail("Unexpected Rollback on uninstall Task");
        }
        // since we modified the file the file should be still there (and not
        // deleted by the uninstall)
        assertTrue(getTargetFile().isFile());
        assertEquals("modified file", FileUtils.readFile(getTargetFile()));
    }

}
