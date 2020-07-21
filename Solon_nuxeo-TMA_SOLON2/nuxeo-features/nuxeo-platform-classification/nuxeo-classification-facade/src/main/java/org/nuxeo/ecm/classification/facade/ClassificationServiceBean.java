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

package org.nuxeo.ecm.classification.facade;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

@Stateless
@Local(ClassificationServiceLocal.class)
@Remote(ClassificationService.class)
public class ClassificationServiceBean implements ClassificationServiceLocal {

    private ClassificationService service;

    @PostConstruct
    public void postConstruct() {
        try {
            service = Framework.getLocalService(ClassificationService.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getClassifiableDocumentTypes() {
        return service.getClassifiableDocumentTypes();
    }

    public boolean isClassifiable(String docType) {
        return service.isClassifiable(docType);
    }

    public boolean isClassifiable(DocumentModel doc) {
        return service.isClassifiable(doc);
    }

}
