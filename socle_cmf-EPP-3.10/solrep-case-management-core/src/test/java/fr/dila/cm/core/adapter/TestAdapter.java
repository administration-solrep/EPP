/**
 *
 */
package fr.dila.cm.core.adapter;

import java.util.UUID;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.cm.cases.Case;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;


/**
 * @author arussel
 *
 */
public class TestAdapter extends SQLRepositoryTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployBundle("fr.dila.cm.core");
        deployBundle("fr.dila.ecm.platform.routing.core");
        openSession();
    }

    @Override
    public void tearDown() throws Exception {
        closeSession();
        super.tearDown();
    }

    protected DocumentModel createDocument(String type) throws ClientException {
        DocumentModel model = session.createDocumentModel("/",
                UUID.randomUUID().toString(), type);
        return session.createDocument(model);
    }

    public void testGetCaseAdapter() throws ClientException {
        DocumentModel doc = createDocument(CaseConstants.CASE_TYPE);
        assertNotNull(doc);
        Case mailEnvelope = doc.getAdapter(Case.class);
        assertNotNull(mailEnvelope);
    }

    public void testGetMailboxAdapter() throws ClientException {
        DocumentModel doc = createDocument(MailboxConstants.MAILBOX_DOCUMENT_TYPE);
        assertNotNull(doc);
        Mailbox mailbox = doc.getAdapter(Mailbox.class);
        assertNotNull(mailbox);
    }

    public void testGetCaseLinktAdapter() throws ClientException {
        DocumentModel doc = createDocument(CaseLinkConstants.CASE_LINK_DOCUMENT_TYPE);
        assertNotNull(doc);
        CaseLink post = doc.getAdapter(CaseLink.class);
        assertNotNull(post);
    }

}
