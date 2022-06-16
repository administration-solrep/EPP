package fr.dila.st.core.service;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.test.STFeature;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author Anahide Tchertchian
 */
@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
public class TestMailboxManagementService {
    private static final String USER = "user";
    private static final String DEFAULT_PATH = "/case-management/mailbox-root";

    @Inject
    private CoreFeature coreFeature;

    @Inject
    private MailboxService correspMailboxService;

    @Test
    public void testDefaultPersonalMailboxCreation() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            UserManager userManager = ServiceUtil.getRequiredService(UserManager.class);
            DocumentModel userModel;

            userModel = userManager.getUserModel("toto");
            correspMailboxService.createPersonalMailboxes(session, userModel);
            List<Mailbox> mailboxes = correspMailboxService.getUserMailboxes(session);
            Assert.assertTrue(mailboxes.isEmpty());

            userModel = userManager.getUserModel(USER);
            correspMailboxService.createPersonalMailboxes(session, userModel);
            mailboxes = correspMailboxService.getUserMailboxes(session);
            Assert.assertFalse(mailboxes.isEmpty());
            Assert.assertEquals(1, mailboxes.size());

            Mailbox mb = mailboxes.get(0);
            Assert.assertEquals("user-user", mb.getId());
            Assert.assertEquals("userf userl", mb.getTitle());
            Assert.assertEquals(MailboxConstants.type.personal.name(), mb.getType());
            Assert.assertEquals(USER, mb.getOwner());
        }
    }

    @Test
    public void testGetUserPersonalMailboxId() throws Exception {
        String totoMbId = correspMailboxService.getUserPersonalMailboxId("toto");
        Assert.assertEquals("user-toto", totoMbId);

        String userMbId = correspMailboxService.getUserPersonalMailboxId(USER);
        Assert.assertNotNull(userMbId);
        Assert.assertEquals("user-user", userMbId);
    }

    @Test
    public void testGetMailbox() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            createMailbox(session);
        }

        // log as given user and check he still got access
        try (CloseableCoreSession session = coreFeature.openCoreSession(USER)) {
            Mailbox mb = correspMailboxService.getMailbox(session, "test");
            Assert.assertEquals("Test", mb.getTitle());
        }
    }

    @Test
    public void testHasMailbox() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            createMailbox(session);

            Assert.assertTrue(correspMailboxService.hasMailbox(session, "test"));
            Assert.assertFalse(correspMailboxService.hasMailbox(session, "foo"));
        }
    }

    @Test
    public void testGetMailboxes() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            UserManager userManager = ServiceUtil.getRequiredService(UserManager.class);
            DocumentModel userModel = userManager.getUserModel(USER);
            correspMailboxService.createPersonalMailboxes(session, userModel);

            // Create an other mailbox
            createMailbox(session);

            List<Mailbox> mailboxes = correspMailboxService.getUserMailboxes(session);
            Assert.assertFalse(mailboxes.isEmpty());
            Assert.assertEquals(2, mailboxes.size());

            Mailbox mbPerso;
            Mailbox mbGeneric;
            if (MailboxConstants.type.personal.name().equals(mailboxes.get(0).getType())) {
                mbPerso = mailboxes.get(0);
                mbGeneric = mailboxes.get(1);
            } else {
                mbPerso = mailboxes.get(1);
                mbGeneric = mailboxes.get(0);
            }

            Assert.assertEquals("user-user", mbPerso.getId());
            Assert.assertEquals("userf userl", mbPerso.getTitle());
            Assert.assertEquals(MailboxConstants.type.personal.name(), mbPerso.getType());
            Assert.assertEquals(USER, mbPerso.getOwner());

            Assert.assertEquals("test", mbGeneric.getId());
            Assert.assertEquals("Test", mbGeneric.getTitle());
            Assert.assertEquals(MailboxConstants.type.generic.name(), mbGeneric.getType());
        }

        // log as given user and check he still got access
        try (CloseableCoreSession session = coreFeature.openCoreSession(USER)) {
            List<Mailbox> mailboxes = correspMailboxService.getUserMailboxes(session);
            Assert.assertEquals(2, mailboxes.size());
        }
    }

    private void createMailbox(CoreSession session) {
        // create a mailbox with given user, and check it's retrieved correctly
        DocumentModel mailboxModel = session.createDocumentModel(MailboxConstants.MAILBOX_DOCUMENT_TYPE);
        Mailbox newMailbox = mailboxModel.getAdapter(Mailbox.class);
        // set users
        newMailbox.setId("test");
        newMailbox.setTitle("Test");
        newMailbox.setType(MailboxConstants.type.generic);
        newMailbox.setOwner(USER);

        // create doc
        mailboxModel = newMailbox.getDocument();
        mailboxModel.setPathInfo(DEFAULT_PATH, newMailbox.getId());
        session.createDocument(mailboxModel);
        // save to make it available to other sessions
        session.save();
    }
}
