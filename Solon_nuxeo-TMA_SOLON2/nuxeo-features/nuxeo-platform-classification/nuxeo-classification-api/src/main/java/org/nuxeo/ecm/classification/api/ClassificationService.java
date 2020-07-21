/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     ldoguin
 *
 * $Id$
 */

package org.nuxeo.ecm.classification.api;

import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Classification service is used to register classifiable Document Types.
 *
 * @author ldoguin
 */
public interface ClassificationService {

    /**
     * This will return only the document types that was contributed as
     * classifiable. It is recommanded to use the facet Classifiable instead of
     * the contribution.
     *
     * @return the list of registered Document Types as String
     */
    @Deprecated
    List<String> getClassifiableDocumentTypes();

    /**
     * If this type of document is classifiable. It is recommanded to use
     * {@link #isClassifiable(DocumentModel)} and to add the facet Classifiable
     * to the classifiable document instead of use the contribution.
     *
     * @param docType
     * @return true if the given doc type is registered.
     */
    @Deprecated
    boolean isClassifiable(String docType);

    /**
     * If this document is classifiable
     *
     * @param doc
     * @return
     */
    boolean isClassifiable(DocumentModel doc);

}
