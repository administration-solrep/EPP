/*
 * (C) Copyright 2017 Nuxeo (http://nuxeo.com/) and others.
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
 *     Funsho David
 *     Kevin Leturc
 */
package org.nuxeo.ecm.core.versioning;

import java.util.function.BiPredicate;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * A versioning policy filter for automatic versioning system.
 *
 * @since 9.1
 */
public interface VersioningPolicyFilter extends BiPredicate<DocumentModel, DocumentModel> {

    /**
     * @param previousDocument the document before modification, could be null if event is aboutToCreate
     * @param currentDocument the document after modification
     * @return whether or not this filter matches the current context, if all policy's filters match, then apply policy
     */
    @Override
    boolean test(DocumentModel previousDocument, DocumentModel currentDocument);

}
