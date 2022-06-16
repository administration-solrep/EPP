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

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class CompositeValueAdapter implements ValueAdapter {

    protected List<ValueAdapter> adapters = new ArrayList<ValueAdapter>();

    public List<ValueAdapter> getAdapters() {
        return adapters;
    }

    public void addAdapter(ValueAdapter adapter) {
        adapters.add(adapter);
    }

    public void removeAdapter(ValueAdapter adapter) {
        adapters.remove(adapter);
    }

    public <T> T getValue(Shell shell, Class<T> type, String value) {
        for (ValueAdapter adapter : adapters) {
            T result = adapter.getValue(shell, type, value);
            if (result != null) {
                return result;
            }
        }
        throw new ShellException("Unknown type adapter for: " + type);
    }

}
