/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *
 * $Id$
 */

package org.nuxeo.shell.cmds.completors;

import java.util.List;

import org.nuxeo.shell.Shell;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class CommandCompletor extends jline.SimpleCompletor {

    final Shell shell;

    public CommandCompletor(Shell shell) {
        super(new String[0]);
        this.shell = shell;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public int complete(String buffer, int cursor, List clist) {
        setCandidateStrings(shell.getActiveRegistry().getCommandNames());
        return super.complete(buffer, cursor, clist);
    }

}
