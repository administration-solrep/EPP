package fr.dila.solonepp.core.parser;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.core.SolonEppFeature;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonEppFeature.class)
public class TestXsdToUfnxqlParser {
    private static final String UNFXQL_RESULT =
        " SELECT m.ecm:uuid as id FROM Message AS m  WHERE (m.cslk:idDossier ILIKE ?) AND  m.ecm:parentId = ? ";

    private static final String MAILBOX = "mailbox";

    @Inject
    private CoreSession session;

    /**
     * Test du filtre des destinataires en copie.
     *
     * @throws Exception
     */
    @Test
    public void testParser() throws Exception {
        List<Object> listObj = new ArrayList<Object>();
        listObj.add("TEST");
        // create "GOUVERNEMENT" mailbox
        createMailboxDoc();
        session.save();

        String unxqlQuery = new XsdToUfnxqlParser()
        .parse("msg:id_dossier ILIKE ? ", listObj, session, "gouvernement", false);
        Assert.assertEquals(unxqlQuery, UNFXQL_RESULT);
    }

    protected DocumentModel getBareMailboxDoc() throws Exception {
        DocumentModel mailbox = session.createDocumentModel(MailboxConstants.MAILBOX_DOCUMENT_TYPE);
        mailbox.setPathInfo(session.getRootDocument().getPathAsString(), MAILBOX);
        return mailbox;
    }

    protected DocumentModel createMailboxDoc() throws Exception {
        DocumentModel doc = getBareMailboxDoc();
        Mailbox mb = doc.getAdapter(Mailbox.class);

        mb.setId(IdUtils.generateId(SolonEppConstant.INSTITUTION_PREFIX + "gouvernement", "-", true, 50));

        mb.setTitle("mailbox title");
        mb.setDescription("mb description");
        mb.setType(MailboxConstants.type.personal);

        mb.setOwner("mbowner");
        mb.setGroups(Arrays.asList(new String[] { "group1", "group2" }));

        return session.createDocument(mb.getDocument());
    }
}
