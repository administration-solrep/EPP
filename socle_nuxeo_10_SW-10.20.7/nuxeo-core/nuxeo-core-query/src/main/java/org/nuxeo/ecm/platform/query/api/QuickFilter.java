/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Funsho David
 */

package org.nuxeo.ecm.platform.query.api;

import org.nuxeo.ecm.core.api.SortInfo;

import java.util.List;

/**
 * @author Funsho David
 * @since 8.4
 */
public interface QuickFilter {

    String getName();

    String getClause();

    List<SortInfo> getSortInfos();

    void setName(String name);

    void setClause(String clause);

    QuickFilter clone();

}
