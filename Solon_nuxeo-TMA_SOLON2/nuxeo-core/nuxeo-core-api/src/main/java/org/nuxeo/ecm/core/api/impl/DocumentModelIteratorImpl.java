/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dragos Mihalache
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.api.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelIterator;
import org.nuxeo.ecm.core.api.DocumentModelsChunk;
import org.nuxeo.ecm.core.api.Filter;

/**
 * Iterator implementation. Keeps track of the last item retrieved from the
 * underlying iterator.
 */
public class DocumentModelIteratorImpl implements DocumentModelIterator {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(DocumentModelIteratorImpl.class);

    private int chunkSize;

    private String sessionId;

    private DocumentModelsChunk dmChunk;

    private int pos = 0;

    private DocsQueryProviderDef def;

    private String type;

    private Filter filter;

    private String perm;

    /**
     * Default constructor is needed as this class is being deserialized on the
     * client side.
     */
    public DocumentModelIteratorImpl() {

    }

    /**
     * This constructor is to be called only from the server side from a
     * CoreSession (AbstractSession).
     */
    public DocumentModelIteratorImpl(CoreSession coreSession, int chunkSize,
            DocsQueryProviderDef def, String type, String perm, Filter filter)
            throws ClientException {
        sessionId = coreSession.getSessionId();

        this.chunkSize = chunkSize;

        this.def = def;
        this.type = type;
        this.perm = perm;
        this.filter = filter;

        // do not: retrieveNextChunk();
        dmChunk = coreSession.getDocsResultChunk(def, type, perm, filter, 0,
                chunkSize);
    }

    private void retrieveNextChunk() throws ClientException {
        final CoreSession coreSession = CoreInstance.getInstance().getSession(
                sessionId);
        final int offset;
        if (dmChunk == null) {
            offset = 0;
        } else {
            offset = dmChunk.lastIndex;
        }
        dmChunk = coreSession.getDocsResultChunk(def, type, perm, filter,
                offset, chunkSize);
    }

    @Override
    public boolean hasNext() {
        return dmChunk.getSize() > pos || dmChunk.hasMore;
    }

    @Override
    public DocumentModel next() {
        try {
            return nextDocument();
        } catch (ClientException e) {
            log.error("Error retrieving next element: " + e.getMessage(), e);
            return null;
        }
    }

    public DocumentModel nextDocument() throws ClientException {
        DocumentModel lastDoc;
        if (dmChunk.getSize() > pos) {
            lastDoc = dmChunk.getItem(pos++);
        } else {
            if (dmChunk.hasMore) {
                // roundtrip to get another chunk
                retrieveNextChunk();
                pos = 0;
                lastDoc = nextDocument();
            } else {
                throw new NoSuchElementException("no more elements");
            }
        }
        return lastDoc;
    }

    @Override
    public void remove() {
        dmChunk.remove(pos);
    }

    @Override
    public Iterator<DocumentModel> iterator() {
        return this;
    }

    @Override
    public long size() {
        return dmChunk.lastIndex < chunkSize ? dmChunk.getSize() : UNKNOWN_SIZE;
    }

}
