/*
 * (C) Copyright 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
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
package org.nuxeo.ecm.automation.core.impl.adapters;

import java.net.MalformedURLException;
import java.net.URL;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.TypeAdaptException;
import org.nuxeo.ecm.automation.TypeAdapter;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 */
public class StringToURL implements TypeAdapter {

    @Override
    public Object getAdaptedValue(OperationContext ctx, Object objectToAdapt) throws TypeAdaptException {
        try {
            String content = (String) objectToAdapt;
            return new URL(content);
        } catch (MalformedURLException e) {
            throw new TypeAdaptException(e);
        }
    }

}
