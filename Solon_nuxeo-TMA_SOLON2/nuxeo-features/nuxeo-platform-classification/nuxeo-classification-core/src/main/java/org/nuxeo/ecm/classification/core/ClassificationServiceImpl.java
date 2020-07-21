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

package org.nuxeo.ecm.classification.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.classification.api.ClassificationConstants;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class ClassificationServiceImpl extends DefaultComponent implements
        ClassificationService {
    public static final String NAME = "org.nuxeo.ecm.classification.core.ClassificationService";

    public static final String TYPES_XP = "types";

    private static final Log log = LogFactory.getLog(ClassificationServiceImpl.class);

    private static List<String> typeList;

    @Override
    public void activate(ComponentContext context) {
        typeList = new LinkedList<String>();
    }

    @Override
    public void deactivate(ComponentContext context) {
        typeList = new LinkedList<String>();
    }

    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
        if (extensionPoint.equals(TYPES_XP)) {
            ClassificationDescriptor classificationDesc = (ClassificationDescriptor) contribution;
            String typeName = classificationDesc.getType();
            if (classificationDesc.isEnabled()) {
                typeList.add(typeName);
            } else {
                if (typeList.contains(typeName)) {
                    typeList.remove(typeName);
                }
            }
        } else {
            log.error("Extension point " + extensionPoint + "is unknown");
        }
    }

    public List<String> getClassifiableDocumentTypes() {
        return typeList;
    }

    public boolean isClassifiable(String docType) {
        if(typeList.contains(docType)) {
            return true;
        }
        try {
            SchemaManager schemaManager = Framework.getService(SchemaManager.class);
            Set<String> facets = schemaManager.getDocumentType(docType).getFacets();
            return facets.contains(ClassificationConstants.CLASSIFIABLE_FACET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isClassifiable(DocumentModel doc) {
        return doc.hasFacet(ClassificationConstants.CLASSIFIABLE_FACET)
                || typeList.contains(doc.getType());
    }

}
