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
 *     Nuxeo - initial API and implementation
 *
 * $Id$
 */

package org.nuxeo.ecm.core.api.blobholder;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

public class TestBlobHolderAdapterService extends NXRuntimeTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("org.nuxeo.ecm.core.api");
    }

    public void testService() throws Exception {
        BlobHolderAdapterService bhas = Framework.getLocalService(BlobHolderAdapterService.class);
        assertNotNull(bhas);
    }

    public void testContrib() throws Exception {
        assertSame(0, BlobHolderAdapterComponent.getFactoryNames().size());
        deployContrib("org.nuxeo.ecm.core.facade.tests","blob-holder-adapters-test-contrib.xml");
        assertSame(1, BlobHolderAdapterComponent.getFactoryNames().size());

        BlobHolderAdapterService bhas = Framework.getLocalService(BlobHolderAdapterService.class);
        assertNotNull(bhas);

        DocumentModel doc = new DocumentModelImpl("Test");
        BlobHolder bh = bhas.getBlobHolderAdapter(doc);

        assertNotNull(bh);

        assertTrue(bh.getFilePath().startsWith("Test"));
        assertEquals("Test", bh.getBlob().getString());
    }

}
