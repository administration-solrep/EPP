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

package fr.dila.cm.service;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.cm.test.CaseManagementRepositoryTestCase;

/**
 * @author Nicolas Ulrich
 */
public class TestCaseManagementDocumentTypeService extends
        CaseManagementRepositoryTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();

        /*
         * deployContrib(
         * CorrespondenceTestConstants.CASEMANAGEMENT_CORE_TEST_BUNDLE,
         * "test-distribution-type-with-error-corresp-contrib.xml");
         */
        openSession();
    }

    public void testGetAllProperty() throws ClientException {
        assertEquals("Case",
                correspDocumentTypeService.getCaseType());
        assertEquals("CaseLink",
                correspDocumentTypeService.getCaseLinkType());
    }

}
