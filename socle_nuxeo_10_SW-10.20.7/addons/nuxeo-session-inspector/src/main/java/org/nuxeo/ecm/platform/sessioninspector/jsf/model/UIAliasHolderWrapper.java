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
 *     <a href="mailto:grenard@nuxeo.com">Guillaume</a>
 */
package org.nuxeo.ecm.platform.sessioninspector.jsf.model;

import java.util.Map;

import javax.el.ValueExpression;

import org.nuxeo.ecm.platform.ui.web.binding.alias.AliasVariableMapper;
import org.nuxeo.runtime.javaagent.AgentLoader;

/**
 * @since 5.9.2
 */
public class UIAliasHolderWrapper extends UIComponentWrapper {

    protected final AliasVariableMapper mapper;

    public UIAliasHolderWrapper(String id, Object[] object) {
        super(id, object);
        mapper = (AliasVariableMapper) object[2];
    }

    @SuppressWarnings("boxing")
    public Long getAliasVariableMapperSize() {
        return AgentLoader.INSTANCE.getSizer().deepSizeOf(mapper.getVariables()) / 8;
    }

    public String getAliasId() {
        if (mapper != null) {
            return mapper.getId();
        }
        return null;
    }

    public Map<String, ValueExpression> getVariables() {
        if (mapper != null) {
            return mapper.getVariables();
        }
        return null;
    }

}
