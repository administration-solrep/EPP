/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.storage.sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.NXCore;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.EventTransactionListener;
import org.nuxeo.ecm.core.model.Repository;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.nuxeo.runtime.transaction.TransactionRuntimeException;

public class TestSQLRepositoryJTAJCA extends TXSQLRepositoryTestCase {

    /**
     * Test that connection sharing allows use of several sessions at the same
     * time.
     */
    public void testSessionSharing() throws Exception {
        if (!hasPoolingConfig()) {
            return;
        }

        Repository repo = NXCore.getRepositoryService().getRepositoryManager().getRepository(REPOSITORY_NAME);
        assertEquals(1, repo.getActiveSessionsCount());

        CoreSession session2 = openSessionAs(SecurityConstants.ADMINISTRATOR);
        assertEquals(1, repo.getActiveSessionsCount());
        try {
            DocumentModel doc = new DocumentModelImpl("/", "doc", "Document");
            doc = session.createDocument(doc);
            session.save();
            // check that this is immediately seen from other connection
            // (underlying ManagedConnection is the same)
            assertTrue(session2.exists(new PathRef("/doc")));
        } finally {
            closeSession(session2);
        }
        assertEquals(1, repo.getActiveSessionsCount());
    }

    /**
     * Test that a commit implicitly does a save.
     */
    public void testSaveOnCommit() throws Exception {
        if (!hasPoolingConfig()) {
            return;
        }

        // first transaction
        DocumentModel doc = new DocumentModelImpl("/", "doc", "Document");
        doc = session.createDocument(doc);
        // let commit do an implicit save
        TransactionHelper.commitOrRollbackTransaction(); // release cx
        TransactionHelper.startTransaction();
        openSession(); // reopen cx and hold open

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    TransactionHelper.startTransaction();
                    CoreSession session2;
                    session2 = openSessionAs(SecurityConstants.ADMINISTRATOR);
                    try {
                        assertTrue(session2.exists(new PathRef("/doc")));
                    } finally {
                        closeSession(session2);
                        TransactionHelper.commitOrRollbackTransaction();
                    }
                } catch (Exception e) {
                    fail(e.toString());
                }
            }
        };
        t.start();
        t.join();
    }

    /**
     * Test that the TransactionalCoreSessionWrapper does its job.
     */
    public void testRollbackOnException() throws Exception {
        if (!(database instanceof DatabaseH2)) {
            // no pooling conf available
            return;
        }

        assertTrue(TransactionHelper.isTransactionActive());
        try {
            session.getDocument(new PathRef("/nosuchdoc"));
            fail("Missing document should throw");
        } catch (Exception e) {
            // ok
        }
        // tx still active because CoreSession.getDocument is marked
        // @NoRollbackOnException
        assertTrue(TransactionHelper.isTransactionActive());
        closeSession();
        TransactionHelper.commitOrRollbackTransaction();

        TransactionHelper.startTransaction();
        openSession();
        assertTrue(TransactionHelper.isTransactionActive());
        DocumentModel doc = new DocumentModelImpl("/nosuchdir", "doc", "Document");
        try {
            session.createDocument(doc);
            fail("Missing parent should throw");
        } catch (Exception e) {
            // ok
        }
        // tx not active anymore because CoreSession.createDocument is not
        // marked @NoRollbackOnException
        assertTrue(TransactionHelper.isTransactionMarkedRollback());
    }

    protected static class HelperEventTransactionListener implements EventTransactionListener {
        public boolean committed;

        @Override
        public void transactionStarted() {
        }

        @Override
        public void transactionCommitted() {
            committed = true;
        }

        @Override
        public void transactionRollbacked() {
        }
    }

    public void testAfterCompletion() {
        EventService eventService = Framework.getLocalService(EventService.class);
        HelperEventTransactionListener listener = new HelperEventTransactionListener();
        eventService.addTransactionListener(listener);
        assertFalse(listener.committed);
        TransactionHelper.commitOrRollbackTransaction();
        assertTrue(listener.committed);
    }

    protected static final Log log = LogFactory.getLog(TestSQLRepositoryJTAJCA.class);

    /**
     * Testing that if 2 modifications are done at the same time on the same
     * document on 2 separate transactions, one is rejected
     * (TransactionRuntimeException)
     */
    public void XXX_TODO_testConcurrentModification() throws Exception {
        if (!hasPoolingConfig()) {
            return;
        }
        // first transaction
        DocumentModel doc = session.createDocumentModel("/", "doc", "Note");
        doc.getProperty("dc:title").setValue("initial");
        doc = session.createDocument(doc);
        // let commit do an implicit save
        closeSession(session);
        TransactionHelper.commitOrRollbackTransaction(); // release cx

        final DocumentRef ref = new PathRef("/doc");
        TransactionHelper.startTransaction();
        openSession();
        doc = session.getDocument(ref);
        doc.getProperty("dc:title").setValue("first");
        session.saveDocument(doc);
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    TransactionHelper.startTransaction();
                    CoreSession session2;
                    session2 = openSessionAs(SecurityConstants.ADMINISTRATOR);
                    try {
                        DocumentModel doc = session2.getDocument(ref);
                        doc.getProperty("dc:title").setValue("second update");
                        session2.saveDocument(doc);
                    } catch (Exception e) {
                        log.error("Catched error while setting title", e);
                    } finally {
                        TransactionHelper.commitOrRollbackTransaction();
                        closeSession(session2);
                    }
                } catch (Exception e) {
                    fail(e.toString());
                }
            }
        };
        t.start();
        t.join();
        try {
            TransactionHelper.commitOrRollbackTransaction(); // release cx
            fail("expected TransactionRuntimeException");
        } catch (TransactionRuntimeException e) {
            // expected
        }
    }
}
