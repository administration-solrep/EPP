package fr.dila.cm.mailbox;

import fr.dila.st.core.test.STFeature;
import java.util.Arrays;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author Anahide Tchertchian
 */

@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
public class TestMailbox {
    private static final String MAILBOX = "mailbox";

    @Inject
    private CoreSession session;

    @Test
    public void testMailboxCreation() throws Exception {
        createMailboxDoc();

        Mailbox mb = getMailbox();
        Assert.assertEquals("mailboxid", mb.getId());
        Assert.assertEquals("mailbox title", mb.getTitle());
        Assert.assertEquals("mb description", mb.getDescription());
        Assert.assertEquals("personal", mb.getType());

        Assert.assertEquals("mbowner", mb.getOwner());
        Assert.assertEquals(Arrays.asList(new String[] { "group1", "group2" }), mb.getGroups());

        Assert.assertEquals(Arrays.asList(new String[] { "mbowner", "group1", "group2" }), mb.getAllUsersAndGroups());
    }

    @Test
    public void testMailboxEdition() throws Exception {
        createMailboxDoc();

        Mailbox mb = getMailbox();
        mb.setId("newid");
        mb.setTitle("new mailbox title");
        mb.setDescription("new mb description");
        mb.setType(MailboxConstants.type.generic);

        mb.setOwner("newmbowner");
        mb.setGroups(Arrays.asList(new String[] { "group1", "group2", "group3" }));

        session.saveDocument(mb.getDocument());

        mb = getMailbox();
        Assert.assertEquals("newid", mb.getId());
        Assert.assertEquals("new mailbox title", mb.getTitle());
        Assert.assertEquals("new mb description", mb.getDescription());
        Assert.assertEquals("generic", mb.getType());

        Assert.assertEquals("newmbowner", mb.getOwner());

        Assert.assertEquals(Arrays.asList(new String[] { "group1", "group2", "group3" }), mb.getGroups());

        Assert.assertEquals(
            Arrays.asList(new String[] { "newmbowner", "group1", "group2", "group3" }),
            mb.getAllUsersAndGroups()
        );
    }

    protected DocumentModel getBareMailboxDoc() throws Exception {
        DocumentModel mailbox = session.createDocumentModel(MailboxConstants.MAILBOX_DOCUMENT_TYPE);
        mailbox.setPathInfo(session.getRootDocument().getPathAsString(), MAILBOX);
        return mailbox;
    }

    protected Mailbox getMailbox() {
        DocumentRef docRef = new PathRef("/" + MAILBOX);
        return session.getDocument(docRef).getAdapter(Mailbox.class);
    }

    protected DocumentModel createMailboxDoc() throws Exception {
        DocumentModel doc = getBareMailboxDoc();
        Mailbox mb = doc.getAdapter(Mailbox.class);

        mb.setId("mailboxid");
        mb.setTitle("mailbox title");
        mb.setDescription("mb description");
        mb.setType(MailboxConstants.type.personal);

        mb.setOwner("mbowner");
        mb.setGroups(Arrays.asList(new String[] { "group1", "group2" }));

        return session.createDocument(mb.getDocument());
    }
}
