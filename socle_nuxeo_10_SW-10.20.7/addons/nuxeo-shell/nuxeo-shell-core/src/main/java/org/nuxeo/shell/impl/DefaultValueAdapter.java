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
package org.nuxeo.shell.impl;

import org.nuxeo.shell.CommandRegistry;
import org.nuxeo.shell.CommandType;
import org.nuxeo.shell.Shell;
import org.nuxeo.shell.ShellException;
import org.nuxeo.shell.ValueAdapter;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class DefaultValueAdapter implements ValueAdapter {

    @SuppressWarnings("unchecked")
    public <T> T getValue(Shell shell, Class<T> type, String value) {
        if (type == CharSequence.class || type == String.class) {
            return (T) value;
        }
        if (type.isPrimitive()) {
            if (type == Boolean.TYPE) {
                return (T) Boolean.valueOf(value);
            } else if (type == Integer.TYPE) {
                return (T) Integer.valueOf(value);
            } else if (type == Float.TYPE) {
                return (T) Float.valueOf(value);
            } else if (type == Long.TYPE) {
                return (T) Long.valueOf(value);
            } else if (type == Double.TYPE) {
                return (T) Double.valueOf(value);
            } else if (type == Character.TYPE) {
                return (T) (Character.valueOf(value == null || value.length() == 0 ? '\0' : value.charAt(0)));
            }
        } else if (type == Boolean.class) {
            return (T) Boolean.valueOf(value);
        } else if (Number.class.isAssignableFrom(type)) {
            if (type == Integer.class) {
                return (T) Integer.valueOf(value);
            } else if (type == Float.class) {
                return (T) Float.valueOf(value);
            } else if (type == Long.class) {
                return (T) Long.valueOf(value);
            } else if (type == Double.class) {
                return (T) Double.valueOf(value);
            }
        } else if (type == Character.class) {
            return (T) (Character.valueOf(value == null || value.length() == 0 ? '\0' : value.charAt(0)));
        } else if (CommandType.class.isAssignableFrom(type)) {
            CommandType cmd = shell.getActiveRegistry().getCommandType(value);
            if (cmd == null) {
                throw new ShellException("Unknown command: " + value);
            }
            return (T) cmd;
        } else if (CommandRegistry.class.isAssignableFrom(type)) {
            CommandRegistry reg = shell.getRegistry(value);
            if (reg == null) {
                throw new ShellException("Unknown namespace: " + value);
            }
            return (T) reg;
        }
        return null;

    }

}
