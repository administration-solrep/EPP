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
package org.nuxeo.shell;

import java.util.List;
import java.util.Map;

import jline.Completor;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public interface CommandType extends Comparable<CommandType> {

    Class<?> getCommandClass();

    String getHelp();

    String getName();

    String[] getAliases();

    List<Token> getArguments();

    Map<String, Token> getParameters();

    String getSyntax();

    Runnable newInstance(Shell shell, String... line)
            throws ShellException;

    Completor getLastTokenCompletor(Shell shell, String... line);

    interface Setter {
        Class<?> getType();

        void set(Object obj, Object value) throws ShellException;
    }

    class Token implements Comparable<Token> {

        public String name;

        public int index = -1;

        public String help;

        // for params required means value is required
        public boolean isRequired;

        public Setter setter;

        public Class<? extends Completor> completor;

        public boolean isArgument() {
            return index > -1;
        }

        public int compareTo(Token o) {
            return index - o.index;
        }

    }

}
