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
 *     Anahide Tchertchian
 *
 * $Id$
 */

package fr.dila.cm.mailbox;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.test.CaseManagementTestConstants;

/**
 * @author Nulrich
 */
public class TestModel extends SQLRepositoryTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // deploy type contrib
        deployContrib(CaseManagementTestConstants.CASE_MANAGEMENT_CORE_BUNDLE,
                "OSGI-INF/cm-core-types-contrib.xml");
        deployBundle("fr.dila.ecm.platform.routing.core");
        openSession();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public DocumentModel createTestFolder() throws ClientException {
        // Create Test Folder
        DocumentModel folder = new DocumentModelImpl("/", "testfolder",
                "Folder");
        return session.createDocument(folder);
    }

    public void testCaseItemCreation() throws Exception {

        DocumentModel folder = createTestFolder();

        // Create the Mail Box
        DocumentModel mailbox = new DocumentModelImpl(folder, "myMailBox",
                "Mailbox");
        mailbox = session.createDocument(mailbox);

        

        

        DocumentModel envelope = new DocumentModelImpl(folder, "envelope",
                CaseConstants.CASE_TYPE);
        envelope = session.createDocument(envelope);

        // Dispatch the Envelope
        DocumentModel dispatch = new DocumentModelImpl(folder, "post",
                "CaseLink");
        dispatch.setPropertyValue("cslk:caseDocumentId", envelope.getId());
        dispatch = session.createDocument(dispatch);
    }

}
