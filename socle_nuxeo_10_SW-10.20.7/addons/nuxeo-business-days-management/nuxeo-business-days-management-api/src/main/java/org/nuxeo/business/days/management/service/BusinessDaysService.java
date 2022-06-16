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
 *     <a href="mailto:nulrich@nuxeo.com">Nicolas Ulrich</a>
 *
 */

package org.nuxeo.business.days.management.service;

import java.util.Date;

/**
 * @author Nicolas Ulrich
 */
public interface BusinessDaysService {

    /**
     * <p>
     * Return the limit date for the given label. The labels are declared using the extension point "limitDate" of the
     * component "org.nuxeo.business.days.management.BusinessDaysService".
     * </p>
     *
     * @param label
     * @param from
     * @return Return the limit date. If the label is unknow, return null.
     */
    Date getLimitDate(String label, Date from);

}
