package fr.dila.st.core.mail;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.work.WorkStatus;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.MailSessionHelper;
import fr.dila.st.core.util.STMailHelper;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.transientstore.api.TransientStore;
import org.nuxeo.ecm.core.transientstore.api.TransientStoreService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        STServiceLocator.class,
        Transport.class,
        STMailHelper.class,
        MailSessionHelper.class,
        MimeMessage.class,
        Message.class,
        SendMailWork.class,
        CoreInstance.class,
        ServiceUtil.class,
        Framework.class
    }
)
@PowerMockIgnore("javax.management.*")
public class SendMailWorkTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    // Pas d'attente pour les besoins du test
    private static final int MAIL_DELAY = 0;

    private static final int MAIL_LIMIT = 50;

    private static final String SENDER_MAIL = "sender@mail.fr";
    private static final String SOLON_MAIL_PREFIX_BODY = "*** test ***";
    private static final String SOLON_MAIL_PREFIX_OBJECT = "[test]";

    @Mock
    private ConfigService mockConfigService;

    @Mock
    CloseableCoreSession mockCloseableCoreSession;

    @Mock
    NuxeoPrincipal mockPrincipal;

    @Mock
    RepositoryManager mockRepositoryManager;

    @Mock
    JournalSendMail mockJournalSendMail;

    @Mock
    JournalService journalService;

    @Mock
    RepositoryManager repoManager;

    @Mock
    TransientStoreService transientStoreService;

    @Mock
    TransientStore transientStore;

    @Mock
    UserManager userManager;

    private String subject;
    private String content;

    private List<Address> mailsTo;
    private List<Address> mailsCc;
    private List<Address> mailsCci;

    @Captor
    private ArgumentCaptor<Message> messageArgumentCaptor;

    @Before
    public void before() throws Exception {
        mockStatic(STServiceLocator.class);
        when(STServiceLocator.getConfigService()).thenReturn(mockConfigService);

        when(mockConfigService.getIntegerValue(STConfigConstants.SEND_MAIL_LIMIT)).thenReturn(MAIL_LIMIT);
        when(mockConfigService.getIntegerValue(STConfigConstants.SEND_MAIL_DELAY)).thenReturn(MAIL_DELAY);
        when(mockConfigService.getValue(STConfigConstants.SOLON_MAIL_PREFIX_BODY, ""))
            .thenReturn(SOLON_MAIL_PREFIX_BODY);
        when(mockConfigService.getValue(STConfigConstants.SOLON_MAIL_PREFIX_OBJECT, ""))
            .thenReturn(SOLON_MAIL_PREFIX_OBJECT);

        mockStatic(CoreInstance.class);
        String repositoryName = "repoName";
        when(STServiceLocator.getRepositoryManager()).thenReturn(mockRepositoryManager);
        when(mockRepositoryManager.getDefaultRepositoryName()).thenReturn(repositoryName);
        when(mockJournalSendMail.getUsername()).thenReturn("user");
        when(CoreInstance.openCoreSession(repositoryName, mockPrincipal)).thenReturn(mockCloseableCoreSession);
        when(STServiceLocator.getJournalService()).thenReturn(journalService);

        mockStatic(ServiceUtil.class);
        when(ServiceUtil.getRequiredService(RepositoryManager.class)).thenReturn(repoManager);
        when(repoManager.getDefaultRepositoryName()).thenReturn("default");

        when(STServiceLocator.getUserManager()).thenReturn(userManager);
        when(userManager.getPrincipal("user")).thenReturn(mockPrincipal);

        // Désactivation de l'envoi réel des mails !
        mockStatic(Transport.class);

        mockStatic(STMailHelper.class);
        mockStatic(MailSessionHelper.class);
        when(MailSessionHelper.getMailSession()).thenReturn(null);
        when(STMailHelper.retrieveSenderMailFromConfig()).thenReturn(new InternetAddress(SENDER_MAIL));

        mockStatic(Framework.class);
        when(Framework.getService(TransientStoreService.class)).thenReturn(transientStoreService);
        when(transientStoreService.getStore(anyString())).thenReturn(transientStore);
        when(transientStore.getBlobs(anyString())).thenReturn(Collections.emptyList());
    }

    /**
     * Initialisation du worker : cas simple où il y a peu de destinataires.
     *
     * @return SendMailWork initialisé
     */
    private SendMailWork initSimpleWorker() throws AddressException {
        subject = "subject";
        content = "content";

        mailsTo = new ArrayList<>();
        mailsTo.add(new InternetAddress("to1@mail.com"));
        mailsTo.add(new InternetAddress("to2@mail.com"));

        mailsCc = new ArrayList<>();
        mailsCc.add(new InternetAddress("cc1@mail.com"));
        mailsCc.add(new InternetAddress("cc2@mail.com"));

        mailsCci = new ArrayList<>();
        mailsCci.add(new InternetAddress("cci1@mail.com"));
        mailsCci.add(new InternetAddress("cci2@mail.com"));

        return new SendMailWork(
            subject,
            content,
            new MailAddress(new InternetAddress(SENDER_MAIL), mailsTo, mailsCc, mailsCci),
            new JournalSendMail()
        );
    }

    /**
     * Initialisation du worker : cas complexe avec beaucoup de destinataires.
     *
     * @return SendMailWork initialisé
     */
    private SendMailWork initComplexWorker() throws AddressException {
        subject = "subject";
        content = "content";

        mailsTo = new ArrayList<>();
        for (int i = 0; i < 199; i++) {
            mailsTo.add(new InternetAddress("to" + i + "@mail.com"));
        }

        mailsCc = new ArrayList<>();
        for (int i = 0; i < 199; i++) {
            mailsCc.add(new InternetAddress("cc" + i + "@mail.com"));
        }

        mailsCci = new ArrayList<>();
        for (int i = 0; i < 199; i++) {
            mailsCci.add(new InternetAddress("cci" + i + "@mail.com"));
        }

        return new SendMailWork(
            subject,
            content,
            new MailAddress(new InternetAddress(SENDER_MAIL), mailsTo, mailsCc, mailsCci),
            new JournalSendMail()
        );
    }

    /**
     * La classe doit être sérialisable
     * @throws AddressException, IOException, ClassNotFoundException
     *
     */
    @Test
    public void testMustBeSerialisable() throws AddressException, IOException, ClassNotFoundException {
        SendMailWork sendMailWork = initComplexWorker();
        //On set le transient store pour s'assurer que l'attribut reste transient
        sendMailWork.getTransientStore();

        // Si l'objet n'est pas sérialisable une exception sera remontée et le test en échec
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        assertThatCode(
                () -> {
                    new ObjectOutputStream(baos).writeObject(sendMailWork);
                }
            )
            .doesNotThrowAnyException();

        // On désérialise l'objet pour s'assurer qu'il est bien égale à ce qui a été sérialisé
        InputStream bais = new ByteArrayInputStream(baos.toByteArray());
        SendMailWork sendMailWorkUnserialized = (SendMailWork) new ObjectInputStream(bais).readObject();

        assertThat(sendMailWorkUnserialized).isEqualTo(sendMailWork);
    }

    /**
     * Le constructeur doit correctement initialiser les champs
     *
     * @throws AddressException
     */
    @Test
    public void test_constructor_checkFields() throws AddressException {
        SendMailWork sendMailWork = initSimpleWorker();

        assertThat(sendMailWork.getStatus()).isEqualTo(WorkStatus.PENDING.getStatus());

        assertFields(sendMailWork);
    }

    /**
     * Envoi de mails en one-shot lorsque le nombre de destinataires total est
     * inférieur à la limite.
     */
    @Test
    public void test_work_fewRecipients() throws MessagingException, IOException {
        SendMailWork sendMailWork = initSimpleWorker();

        sendMailWork.work();

        assertThat(sendMailWork.getStatus()).isEqualTo(WorkStatus.DONE.getStatus());
        assertConfig(sendMailWork);
        assertFields(sendMailWork);

        PowerMockito.verifyStatic();
        Transport.send(messageArgumentCaptor.capture());

        List<Message> capturedMessages = messageArgumentCaptor.getAllValues();
        assertMessageForSimpleWorker(capturedMessages.get(0));
    }

    /**
     * Envoi de mails lorsque le nombre de destinataires total est
     * supérieur à la limite -> il doit y avoir un découpage
     */
    @Test
    public void test_work_manyRecipients() throws MessagingException, IOException {
        SendMailWork sendMailWork = initComplexWorker();

        sendMailWork.work();

        assertThat(sendMailWork.getStatus()).isEqualTo(WorkStatus.DONE.getStatus());
        assertConfig(sendMailWork);
        assertFields(sendMailWork);

        PowerMockito.verifyStatic(Mockito.times(12));
        Transport.send(messageArgumentCaptor.capture());

        List<Message> capturedMessages = messageArgumentCaptor.getAllValues();
        assertMessageForComplexWorker(capturedMessages);
    }

    private void assertMessageForSimpleWorker(Message capturedMessage) throws MessagingException, IOException {
        assertThat(capturedMessage).isNotNull();
        assertThat(capturedMessage.getSubject()).isEqualTo(SOLON_MAIL_PREFIX_OBJECT + subject);
        assertThat(capturedMessage.getContent()).isEqualTo(SOLON_MAIL_PREFIX_BODY + content);
        assertThat(capturedMessage.getFrom()[0]).isEqualTo(new InternetAddress(SENDER_MAIL));
        assertThat(capturedMessage.getRecipients(RecipientType.TO)).containsExactlyInAnyOrderElementsOf(mailsTo);
        assertThat(capturedMessage.getRecipients(RecipientType.CC)).containsExactlyInAnyOrderElementsOf(mailsCc);
        assertThat(capturedMessage.getRecipients(RecipientType.BCC)).containsExactlyInAnyOrderElementsOf(mailsCci);
    }

    private void assertMessageForComplexWorker(List<Message> capturedMessages) throws MessagingException, IOException {
        Set<Address> addressesTo = new HashSet<>(); // ensemble des destinataires en to
        List<Address> addressesCc = new ArrayList<>(); // ensemble des destinataires en cc
        List<Address> addressesCci = new ArrayList<>(); // ensemble des destinataires en cci
        for (Message msg : capturedMessages) {
            assertThat(msg).isNotNull();
            assertThat(msg.getSubject()).isEqualTo(SOLON_MAIL_PREFIX_OBJECT + subject);
            assertThat(msg.getContent()).isEqualTo(SOLON_MAIL_PREFIX_BODY + content);
            assertThat(msg.getFrom()[0]).isEqualTo(new InternetAddress(SENDER_MAIL));

            if (msg.getRecipients(RecipientType.TO) != null) {
                CollectionUtils.addAll(addressesTo, msg.getRecipients(RecipientType.TO));
            }
            if (msg.getRecipients(RecipientType.CC) != null) {
                CollectionUtils.addAll(addressesCc, msg.getRecipients(RecipientType.CC));
            }
            if (msg.getRecipients(RecipientType.BCC) != null) {
                CollectionUtils.addAll(addressesCci, msg.getRecipients(RecipientType.BCC));
            }
        }
        List<Address> expectedTo = new ArrayList<>();
        expectedTo.addAll(mailsTo);
        expectedTo.add(new InternetAddress(SENDER_MAIL));
        assertThat(addressesTo).containsExactlyInAnyOrderElementsOf(expectedTo);
        assertThat(addressesCc).containsExactlyInAnyOrderElementsOf(mailsCc);
        assertThat(addressesCci).containsExactlyInAnyOrderElementsOf(mailsCci);
    }

    private void assertConfig(SendMailWork sendMailWork) {
        assertThat(sendMailWork.getLimit()).isEqualTo(MAIL_LIMIT);
        assertThat(sendMailWork.getDelay()).isEqualTo(MAIL_DELAY);
    }

    private void assertFields(SendMailWork sendMailWork) throws AddressException {
        Assertions.assertThat(sendMailWork.getSubject()).isEqualTo(subject);
        Assertions.assertThat(sendMailWork.getContent()).isEqualTo(content);
        assertThat(sendMailWork.getMailAddress().getMailFrom()).isEqualTo(new InternetAddress(SENDER_MAIL));
        Assertions.assertThat(sendMailWork.getMailAddress().getMailsTo()).containsExactlyElementsOf(mailsTo);
        Assertions.assertThat(sendMailWork.getMailAddress().getMailsCc()).containsExactlyElementsOf(mailsCc);
        Assertions.assertThat(sendMailWork.getMailAddress().getMailsCci()).containsExactlyElementsOf(mailsCci);
    }
}
