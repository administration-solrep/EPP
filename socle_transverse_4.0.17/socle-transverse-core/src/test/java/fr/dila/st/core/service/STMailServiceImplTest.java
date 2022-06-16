package fr.dila.st.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import com.google.common.collect.Lists;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.mail.MailAddress;
import fr.dila.st.core.mail.SendMailWork;
import fr.dila.st.core.util.MailSessionHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.transientstore.api.TransientStore;
import org.nuxeo.ecm.core.transientstore.api.TransientStoreService;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ Framework.class, MailSessionHelper.class, STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class STMailServiceImplTest {
    private static final String APP_URL = "http://www.test.com";
    private static final String MAIL_FROM = "from@mail.com";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private WorkManager mockWorkManager;

    @Mock
    private CoreSession mockSession;

    @Mock
    private STParametreService mockSTParametreService;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    TransientStoreService transientStoreService;

    @Mock
    TransientStore transientStore;

    @Mock
    private UserManager mockUserManager;

    @Mock
    private DocumentModel mockDocModelUser1;

    @Mock
    private DocumentModel mockDocModelUser2;

    @Mock
    private DocumentModel mockDocModelUser3;

    @Mock
    private STUser mockSTUser1;

    @Mock
    private STUser mockSTUser2;

    @Mock
    private STUser mockSTUser3;

    private static final String USER_MAIL_FORMAT = "user%s@mail.com";
    private static final String USER_ID_FORMAT = "user%s";

    private static final String USER1_MAIL = String.format(USER_MAIL_FORMAT, 1);
    private static final String USER2_MAIL = String.format(USER_MAIL_FORMAT, 2);
    private static final String USER3_MAIL = String.format(USER_MAIL_FORMAT, 3);

    private static final String USER1_ID = String.format(USER_ID_FORMAT, 1);
    private static final String USER2_ID = String.format(USER_ID_FORMAT, 2);
    private static final String USER3_ID = String.format(USER_ID_FORMAT, 3);

    private List<Address> listAddresses;

    private static final String ACTUAL_OBJET = "mon vrai objet de mail";
    private static final String ACTUAL_TEXTE = "mon vrai texte de mail";

    @Mock
    private DocumentModel mockDocModelDossier1;

    @Mock
    private DocumentModel mockDocModelDossier2;

    @Mock
    private DocumentModel mockDocModelDossier3;

    private static final String DOSSIER_ID_PREFIX = "dossierId";
    private static final String DOSSIER_TITLE_PREFIX = "titre dossier ";

    private static final String DOSSIER_ID1 = DOSSIER_ID_PREFIX + "1";
    private static final String DOSSIER_TITLE1 = DOSSIER_TITLE_PREFIX + "1";
    private static final String DOSSIER_ID2 = DOSSIER_ID_PREFIX + "2";
    private static final String DOSSIER_TITLE2 = DOSSIER_TITLE_PREFIX + "2";
    private static final String DOSSIER_ID3 = DOSSIER_ID_PREFIX + "3";
    private static final String DOSSIER_TITLE3 = DOSSIER_TITLE_PREFIX + "3";

    private static final List<String> listDossierIds = Lists.newArrayList(DOSSIER_ID1, DOSSIER_ID2, DOSSIER_ID3);
    private static final List<String> listDossierTitles = Lists.newArrayList(
        DOSSIER_TITLE1,
        DOSSIER_TITLE2,
        DOSSIER_TITLE3
    );

    private static final String USER_PASSWORD = "myPassword";

    private STMailServiceImpl service;

    @Before
    public void before() {
        mockStatic(Framework.class);
        when(Framework.getService(WorkManager.class)).thenReturn(mockWorkManager);

        when(Framework.getService(TransientStoreService.class)).thenReturn(transientStoreService);
        when(transientStoreService.getStore(anyString())).thenReturn(transientStore);
        when(transientStore.getBlobs(anyString())).thenReturn(Collections.emptyList());

        mockStatic(STServiceLocator.class);
        when(STServiceLocator.getSTParametreService()).thenReturn(mockSTParametreService);

        when(mockSTParametreService.getParametreValue(mockSession, STParametreConstant.ADRESSE_URL_APPLICATION))
            .thenReturn(APP_URL);
        when(
                mockSTParametreService.getParametreValue(
                    mockSession,
                    STParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_OBJET
                )
            )
            .thenReturn(ACTUAL_OBJET);
        when(
                mockSTParametreService.getParametreValue(
                    mockSession,
                    STParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_TEXT
                )
            )
            .thenReturn(ACTUAL_TEXTE);
        when(STServiceLocator.getUserManager()).thenReturn(mockUserManager);
        when(STServiceLocator.getConfigService()).thenReturn(mockConfigService);
        when(mockConfigService.getValue(STConfigConstants.MAIL_FROM)).thenReturn(MAIL_FROM);

        when(mockUserManager.getUserModel(USER1_ID)).thenReturn(mockDocModelUser1);
        when(mockUserManager.getUserModel(USER2_ID)).thenReturn(mockDocModelUser2);
        when(mockUserManager.getUserModel(USER3_ID)).thenReturn(mockDocModelUser3);

        when(mockDocModelUser1.getType()).thenReturn("user");
        when(mockDocModelUser2.getType()).thenReturn("user");
        when(mockDocModelUser3.getType()).thenReturn("user");

        when(mockDocModelUser1.getAdapter(STUser.class)).thenReturn(mockSTUser1);
        when(mockDocModelUser2.getAdapter(STUser.class)).thenReturn(mockSTUser2);
        when(mockDocModelUser3.getAdapter(STUser.class)).thenReturn(mockSTUser3);

        when(mockSTUser1.getEmail()).thenReturn(USER1_MAIL);
        when(mockSTUser2.getEmail()).thenReturn(USER2_MAIL);
        when(mockSTUser3.getEmail()).thenReturn(USER3_MAIL);

        mockStatic(MailSessionHelper.class);
        when(MailSessionHelper.isMailSessionConfigured()).thenReturn(true);

        when(mockSession.getDocument(new IdRef(DOSSIER_ID1))).thenReturn(mockDocModelDossier1);
        when(mockSession.getDocument(new IdRef(DOSSIER_ID2))).thenReturn(mockDocModelDossier2);
        when(mockSession.getDocument(new IdRef(DOSSIER_ID3))).thenReturn(mockDocModelDossier3);

        when(mockDocModelDossier1.getTitle()).thenReturn(DOSSIER_TITLE1);
        when(mockDocModelDossier2.getTitle()).thenReturn(DOSSIER_TITLE2);
        when(mockDocModelDossier3.getTitle()).thenReturn(DOSSIER_TITLE3);

        service = new STMailServiceImpl();

        listAddresses =
            Stream
                .of(USER1_MAIL, USER2_MAIL, USER3_MAIL)
                .map(
                    mailStr -> {
                        try {
                            return new InternetAddress(mailStr);
                        } catch (AddressException e) {
                            return new InternetAddress();
                        }
                    }
                )
                .collect(Collectors.toList());
    }

    @Test
    public void test_sendMailToUserList_checkWork() throws AddressException {
        service.sendMailToUserList(initListSTUser(), ACTUAL_OBJET, ACTUAL_TEXTE);

        assertSendMailWork(
            captureSendMailWork(),
            new SendMailWork(
                ACTUAL_OBJET,
                ACTUAL_TEXTE,
                new MailAddress(new InternetAddress(MAIL_FROM), listAddresses, null, null),
                null
            ),
            true,
            false
        );
    }

    @Test
    public void test_sendHtmlMailToUserListWithLinkToDossiers_checkWork() throws AddressException {
        service.sendHtmlMailToUserListWithLinkToDossiers(
            mockSession,
            initListSTUser(),
            ACTUAL_OBJET,
            ACTUAL_TEXTE,
            listDossierIds
        );

        SendMailWork actualSendMailWork = captureSendMailWork();
        assertSendMailWork(
            actualSendMailWork,
            new SendMailWork(
                ACTUAL_OBJET,
                ACTUAL_TEXTE,
                new MailAddress(new InternetAddress(MAIL_FROM), listAddresses, null, null),
                null
            ),
            true,
            true
        );

        // Vérification de la présence de liens corrects
        for (int i = 0; i < listDossierIds.size(); i++) {
            assertThat(actualSendMailWork.getContent())
                .asString()
                .contains(
                    StringUtils.join(
                        "<a href=\"",
                        APP_URL,
                        "/dossier/",
                        listDossierIds.get(i),
                        "/parapheur \" target=\"_blank\">",
                        listDossierTitles.get(i),
                        "</a>"
                    )
                );
        }
    }

    @Test
    public void test_sendMailUserPasswordCreation_checkWork() throws AddressException {
        service.sendMailUserPasswordCreation(mockSession, USER1_ID, USER_PASSWORD);

        List<Address> oneAddress = new ArrayList<>();
        oneAddress.add(new InternetAddress(USER1_MAIL));

        SendMailWork actualSendMailWork = captureSendMailWork();
        assertSendMailWork(
            actualSendMailWork,
            new SendMailWork(
                ACTUAL_OBJET,
                ACTUAL_TEXTE,
                new MailAddress(new InternetAddress(MAIL_FROM), oneAddress, null, null),
                null
            ),
            false,
            false
        );
    }

    @Test
    public void test_sendMailToUserListAsBCC_checkWork() throws AddressException {
        service.sendMailToUserListAsBCC(initListSTUser(), ACTUAL_OBJET, ACTUAL_TEXTE, null, MAIL_FROM);

        SendMailWork actualSendMailWork = captureSendMailWork();
        assertSendMailWork(
            actualSendMailWork,
            new SendMailWork(
                ACTUAL_OBJET,
                ACTUAL_TEXTE,
                new MailAddress(new InternetAddress(MAIL_FROM), null, null, listAddresses),
                null
            ),
            false,
            false
        );
    }

    @Test
    public void test_decodeSpecialCharacters() throws AddressException {
        //  Lo'bjet du mail n'est pas encodé, on doit le transférer tel quel
        String receivedObjet = "test &é~\"#'{([-|è`\\_ç^à@)]°=}+^¨$¤£€ù%*µ &bsol; &percnt;";

        // Corps du mail tel qu'il serait reçu du front (TinyMCE)
        String receivedTexte =
            "caract&egrave;res sp&eacute;ciaux : &amp;&eacute;~\"#'{([-|&egrave;`&bsol;_&ccedil;^&agrave;@)]&deg;=}+^&uml;$&curren;&pound;&euro;&ugrave;&percnt;*&micro; &amp;bsol; &amp;percnt;";
        // Tel qu'on devrait l'envoyer pour être bien interprété dans Outlook
        String expectedTexte =
            "caract&egrave;res sp&eacute;ciaux : &amp;&eacute;~\"#'{([-|&egrave;`\\_&ccedil;^&agrave;@)]&deg;=}+^&uml;$&curren;&pound;&euro;&ugrave;%*&micro; &amp;bsol; &amp;percnt;";

        service.sendMailToUserListAsBCC(initListSTUser(), receivedObjet, receivedTexte, null, MAIL_FROM);

        SendMailWork actualSendMailWork = captureSendMailWork();
        assertSendMailWork(
            actualSendMailWork,
            new SendMailWork(
                receivedObjet,
                expectedTexte,
                new MailAddress(new InternetAddress(MAIL_FROM), null, null, listAddresses),
                null
            ),
            true,
            false
        );
    }

    private List<STUser> initListSTUser() {
        List<STUser> list = new ArrayList<>();
        list.add(mockSTUser1);
        list.add(mockSTUser2);
        list.add(mockSTUser3);
        return list;
    }

    private void assertSendMailWork(
        SendMailWork actualSendMailWork,
        SendMailWork expected,
        boolean checkContent,
        boolean html
    ) {
        assertThat(actualSendMailWork.getSubject()).isEqualTo(expected.getSubject());

        if (checkContent) {
            if (!html) {
                assertThat(actualSendMailWork.getContent()).isEqualTo(expected.getContent());
            } else {
                // Html content
                assertThat(actualSendMailWork.getContent()).asString().contains((String) expected.getContent());
                assertThat(actualSendMailWork.getContent())
                    .asString()
                    .containsSequence("<html>", "<head>", "</head>", "<body>");
                assertThat(actualSendMailWork.getContent()).asString().containsSequence("</body>", "</html>");
            }
        }

        if (CollectionUtils.isNotEmpty(expected.getMailAddress().getMailsTo())) {
            assertThat(actualSendMailWork.getMailAddress().getMailsTo())
                .containsExactlyElementsOf(expected.getMailAddress().getMailsTo());
        }

        if (CollectionUtils.isEmpty(expected.getMailAddress().getMailsCc())) {
            assertThat(expected.getMailAddress().getMailsCc()).isEmpty();
        } else {
            assertThat(actualSendMailWork.getMailAddress().getMailsCc())
                .containsExactlyElementsOf(expected.getMailAddress().getMailsCc());
        }
        if (CollectionUtils.isEmpty(expected.getMailAddress().getMailsCci())) {
            assertThat(expected.getMailAddress().getMailsCci()).isEmpty();
        } else {
            assertThat(actualSendMailWork.getMailAddress().getMailsCci())
                .containsExactlyElementsOf(expected.getMailAddress().getMailsCci());
        }
    }

    private SendMailWork captureSendMailWork() {
        ArgumentCaptor<SendMailWork> sendMailWorkArgumentCaptor = ArgumentCaptor.forClass(SendMailWork.class);

        Mockito.verify(mockWorkManager).schedule(sendMailWorkArgumentCaptor.capture(), Mockito.eq(true));

        SendMailWork actualSendMailWork = sendMailWorkArgumentCaptor.getValue();
        return actualSendMailWork;
    }
}
