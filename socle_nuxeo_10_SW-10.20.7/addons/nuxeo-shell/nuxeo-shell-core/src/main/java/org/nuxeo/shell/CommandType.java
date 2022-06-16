/*
 * (C) Copyright 2006-2010 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 */
public interface CommandType extends Comparable<CommandType> {

    Class<?> getCommandClass();

    String getHelp();

    String getName();

    String[] getAliases();

    List<Token> getArguments();

    Map<String, Token> getParameters();

    String getSyntax();

    Runnable newInstance(Shell shell, String... line) throws ShellException;

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
