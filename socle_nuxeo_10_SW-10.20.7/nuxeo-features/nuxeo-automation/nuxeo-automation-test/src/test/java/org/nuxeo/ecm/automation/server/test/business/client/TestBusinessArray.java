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
 */
package org.nuxeo.ecm.automation.server.test.business.client;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;

@Operation(id = TestBusinessArray.ID, category = Constants.CAT_SERVICES, label = "", description = "")
public class TestBusinessArray {

    public static final String ID = "Business.TestBusinessArray";

    @OperationMethod
    public BusinessBean[] run() {
        BusinessBean[] businessBeans = new BusinessBean[] { new BusinessBean(), new BusinessBean() };
        return businessBeans;
    }

}
