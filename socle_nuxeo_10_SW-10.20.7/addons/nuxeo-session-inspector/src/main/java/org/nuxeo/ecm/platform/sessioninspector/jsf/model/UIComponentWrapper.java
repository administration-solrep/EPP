/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Anahide Tchertchian
 */
package org.nuxeo.ecm.platform.sessioninspector.jsf.model;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.platform.ui.web.binding.alias.AliasVariableMapper;

/**
 * Wrapper for a generic UIComponent state.
 *
 * @since 5.9.2
 */
public class UIComponentWrapper {

    protected final String id;

    protected final Object[] state;

    public UIComponentWrapper(String id, Object[] object) {
        this.id = id;
        this.state = object;
    }

    public String getId() {
        return id;
    }

    public List<String> getFlatState() {
        List<String> res = new ArrayList<String>();
        if (state != null) {
            for (Object item : state) {
                if (item == null) {
                    continue;
                }
                if (item instanceof Object[]) {
                    res.addAll(new UIComponentWrapper(id, (Object[]) item).getFlatState());
                } else if (item instanceof AliasVariableMapper) {
                    AliasVariableMapper vm = (AliasVariableMapper) item;
                    res.add(String.format("AliasVariableMapper(%s, %s)", vm.getId(), vm.getVariables()));
                } else {
                    res.add(item.toString());
                }
            }
        }
        return res;
    }
}
