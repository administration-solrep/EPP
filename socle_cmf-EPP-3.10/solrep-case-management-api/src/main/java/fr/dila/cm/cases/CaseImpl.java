/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: MailEnvelopeImpl.java 58167 2008-10-20 15:37:24Z atchertchian $
 */

package fr.dila.cm.cases;

import static fr.dila.cm.cases.CaseConstants.CASE_SCHEMA;
import static fr.dila.cm.cases.CaseConstants.MAILBOX_DOCUMENTS_ID_TYPE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.exception.CaseManagementRuntimeException;

/**
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public class CaseImpl implements Case {

    private static final long serialVersionUID = 6160682333116646611L;

    boolean firstDocumentFlag;

    Boolean incoming;

    protected final DocumentModel document;

    protected final HasParticipants recipientsAdapter;

    public CaseImpl(DocumentModel envelope,
            HasParticipants recipientsAdapter) {
        document = envelope;
        this.recipientsAdapter = recipientsAdapter;
    }

    public DocumentModel getDocument() {
        return document;
    }

    @SuppressWarnings("unchecked")
    protected List<String> getItemsId() {
        List<String> emailIds;
        try {
            emailIds = (List<String>) document.getProperty(CASE_SCHEMA, MAILBOX_DOCUMENTS_ID_TYPE);
        } catch (ClientException e) {
            throw new CaseManagementRuntimeException(e);
        }
        if (emailIds == null) {
            return new ArrayList<String>();
        }
        return emailIds;
    }

    protected void saveItemsId(CoreSession session, List<String> itemsId) {
        try {
            document.setProperty(CASE_SCHEMA,
                    MAILBOX_DOCUMENTS_ID_TYPE,
                    itemsId);
            session.saveDocument(document);
        } catch (ClientException e) {
            throw new CaseManagementRuntimeException(e);
        }
    }

    public void save(CoreSession session) {
        try {
            session.saveDocument(document);
        } catch (ClientException e) {
            throw new CaseManagementRuntimeException(e);
        }
    }

    protected CoreSession getDocumentSession() {
        CoreSession session = CoreInstance.getInstance().getSession(
                document.getSessionId());
        return session;
    }

    protected CoreSession getCoreSession() throws Exception {
        RepositoryManager mgr = Framework.getService(RepositoryManager.class);
        if (mgr == null) {
            throw new ClientException("Cannot find RepostoryManager");
        }
        Repository repo = mgr.getRepository(document.getRepositoryName());
        return repo.open();
    }

    public boolean isDraft() throws ClientException {
        return CaseLifeCycleConstants.STATE_DRAFT.equals(document.getCurrentLifeCycleState());
    }

    public boolean isEmpty() throws ClientException {
       return getItemsId().isEmpty();
    }

    public void addInitialExternalParticipants(
            Map<String, List<String>> recipients) {
        recipientsAdapter.addInitialExternalParticipants(recipients);
    }

    public void addInitialInternalParticipants(
            Map<String, List<String>> recipients) {
        recipientsAdapter.addInitialInternalParticipants(recipients);
    }

    public void addParticipants(Map<String, List<String>> recipients) {
        recipientsAdapter.addParticipants(recipients);
    }

    public Map<String, List<String>> getAllParticipants() {
        return recipientsAdapter.getAllParticipants();
    }

    public Map<String, List<String>> getInitialExternalParticipants() {
        return recipientsAdapter.getInitialExternalParticipants();
    }

    public Map<String, List<String>> getInitialInternalParticipants() {
        return recipientsAdapter.getInitialInternalParticipants();
    }
}
