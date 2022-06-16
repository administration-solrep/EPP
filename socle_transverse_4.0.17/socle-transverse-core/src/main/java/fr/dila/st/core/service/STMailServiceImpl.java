package fr.dila.st.core.service;

import static java.lang.String.format;

import fr.dila.st.api.constant.MediaType;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.domain.file.ExportBlob;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.mail.JournalSendMail;
import fr.dila.st.core.mail.MailAddress;
import fr.dila.st.core.mail.SendMailWork;
import fr.dila.st.core.util.MailSessionHelper;
import fr.dila.st.core.util.STMailHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.core.util.StringEscapeHelper;
import fr.dila.st.core.util.StringHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.nuxeo.ecm.automation.core.mail.Composer;
import org.nuxeo.ecm.automation.core.mail.Mailer;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;
import org.nuxeo.ecm.core.transientstore.api.TransientStore;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Service mail
 *
 * @author Fabio Esposito
 */
public class STMailServiceImpl implements STMailService {
    private static final String SEND_MAIL_WITH_ATTACHMENT = "[sendMailWithAttachment]";

    private final String solonMailPrefixObject = StringEscapeUtils.unescapeHtml4(
        STServiceLocator.getConfigService().getValue(STConfigConstants.SOLON_MAIL_PREFIX_OBJECT, "")
    );

    private final String solonMailPrefixBody = StringEscapeUtils.unescapeHtml4(
        STServiceLocator.getConfigService().getValue(STConfigConstants.SOLON_MAIL_PREFIX_BODY, "")
    );

    private final String solonMailPrefixFrom = STServiceLocator
        .getConfigService()
        .getValue(STConfigConstants.SOLON_MAIL_PREFIX_FROM, "");

    public static final String MAIL_SESSION = "mailSession";

    public static final String CONTENT_FORMAT = "text/html; charset=UTF-8";

    protected static final String USER_SEND_MAIL_PASSWORD_TEMPLATE =
        "<br/><br/>- login : %s<br/>- mot de passe : %s<br/>";

    private static final STLogger LOGGER = STLogFactory.getLog(STMailServiceImpl.class);

    public static final String LOG_SEND_OBJECT_TO_SINGULAR =
        "Envoi d'un mél ayant pour objet : %s à l'adresse suivante : %s";
    public static final String LOG_SEND_OBJECT_TO_PLURAL =
        "Envoi d'un mél ayant pour objet : %s aux adresses suivantes : %s";
    public static final String LOG_SEND_ERROR_TO_SINGULAR =
        "Erreur lors de l'envoi d'un mél ayant pour objet : %s à l'adresse suivante : %s";
    public static final String LOG_SEND_ERROR_TO_PLURAL =
        "Erreur lors de l'envoi d'un mél ayant pour objet : %s aux adresses suivantes : %s";

    private static final String ERROR_ENVOI_MAIL = "Erreur d'envoi du mail";

    /**
     * Default constructor
     */
    public STMailServiceImpl() {
        // do nothing
    }

    @Override
    public void sendMailToUserList(List<STUser> userList, String objet, String texte) {
        if (!MailSessionHelper.isMailSessionConfigured()) {
            return;
        }
        List<String> listeAdresse = new ArrayList<>();
        List<Address> recipients = new ArrayList<>();
        if (STMailHelper.fillRecipients(userList, recipients, listeAdresse)) {
            sendMail(recipients, listeAdresse, objet, texte);
        }
    }

    @Override
    public void sendMailToUserListAsBCC(
        List<STUser> userList,
        String objet,
        String texte,
        List<AbstractBlob> blobs,
        String mailFrom
    ) {
        if (!MailSessionHelper.isMailSessionConfigured()) {
            return;
        }
        List<String> listeAdresse = new ArrayList<>();
        List<Address> recipients = new ArrayList<>();

        if (STMailHelper.fillRecipients(userList, recipients, listeAdresse)) {
            try {
                ArrayList<Address> appMail = new ArrayList<>();
                Address adMailFrom = new InternetAddress(mailFrom);
                appMail.add(adMailFrom);

                fillAndSendMultipartMessage(
                    objet,
                    texte,
                    new MailAddress(adMailFrom, appMail, null, recipients),
                    blobs,
                    new JournalSendMail()
                );
            } catch (Exception e) {
                LOGGER.error(
                    STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                    new Exception(
                        "[sendMailToUserListAsBCC]" +
                        format(LOG_SEND_ERROR_TO_PLURAL, objet, StringUtils.join(listeAdresse, ",")),
                        e
                    )
                );
            }
        }
    }

    @Override
    public void sendMailNotificationToUserList(List<STUser> userList, String objet, String texte) {
        List<Address> recipients = new ArrayList<>();
        List<String> listeAdresse = new ArrayList<>();
        if (STMailHelper.fillRecipients(userList, recipients, listeAdresse)) {
            sendMail(recipients, listeAdresse, objet, texte);
        }
    }

    @Override
    public void sendMailUserPasswordCreation(CoreSession session, String userId, String password) {
        final UserManager userManager = STServiceLocator.getUserManager();
        DocumentModel doc = userManager.getUserModel(userId);

        // Traite uniquement les documents de type User
        String docType = doc.getType();
        if (!"user".equals(docType)) {
            return;
        }
        STUser user = doc.getAdapter(STUser.class);

        String email = user.getEmail();

        STParametreService stParamService = STServiceLocator.getSTParametreService();

        String subject = stParamService.getParametreValue(
            session,
            STParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_OBJET
        );
        String message =
            stParamService.getParametreValue(session, STParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_TEXT) +
            USER_SEND_MAIL_PASSWORD_TEMPLATE;

        message = format(message, userId, password);

        Address[] emailAddress = new Address[1];
        try {
            LOGGER.info(STLogEnumImpl.SEND_MAIL_TEC, format(LOG_SEND_OBJECT_TO_SINGULAR, subject, email));
            emailAddress[0] = new InternetAddress(email, user.getFullName());
            sendMail(message, subject, MAIL_SESSION, emailAddress);
        } catch (Exception e) {
            LOGGER.error(
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                new Exception("[sendMailUserPasswordCreation]" + format(LOG_SEND_ERROR_TO_SINGULAR, subject, email))
            );
            throw new NuxeoException("Erreur d'envoi du mail de création du mot de passe", e);
        }
    }

    @Override
    public void sendTemplateMail(
        List<String> emailAddress,
        String objet,
        String corpsTemplate,
        Map<String, Object> paramMap
    ) {
        if (!MailSessionHelper.isMailSessionConfigured() || CollectionUtils.isEmpty(emailAddress)) {
            return;
        }

        try {
            LOGGER.info(
                STLogEnumImpl.SEND_MAIL_TEC,
                format(LOG_SEND_OBJECT_TO_PLURAL, objet, StringUtils.join(emailAddress, ", "))
            );
            Composer composer = new Composer();
            Mailer.Message msg = composer.newTextMessage(solonMailPrefixBody + corpsTemplate, paramMap);
            msg.setSubject(solonMailPrefixObject + objet);

            for (String address : emailAddress) {
                msg.addTo(address);
            }
            msg.setFrom(STMailHelper.retrieveSenderMailFromConfig());
            STMailHelper.setSentDate(msg);

            msg.send();
        } catch (Exception e) {
            LOGGER.error(
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                new Exception(
                    "[sendTemplateMail]" + format(LOG_SEND_ERROR_TO_PLURAL, objet, StringUtils.join(emailAddress, ", "))
                )
            );
            throw new NuxeoException(ERROR_ENVOI_MAIL, e);
        }
    }

    @Override
    public void sendTemplateHtmlMail(
        List<Address> emailAddress,
        String objetTemplate,
        String corpsTemplate,
        Map<String, Object> paramMap
    ) {
        if (!MailSessionHelper.isMailSessionConfigured() || CollectionUtils.isEmpty(emailAddress)) {
            return;
        }
        String objet = StringHelper.renderFreemarker(objetTemplate, paramMap);
        String corps = StringHelper.renderFreemarker(corpsTemplate, paramMap);

        fillAndSendMessage(objet, corps, emailAddress, null, null, null, new JournalSendMail());
    }

    @Override
    public void sendTemplateHtmlMailWithAttachedFiles(
        List<Address> emailAddress,
        List<Address> copieAddress,
        String objetTemplate,
        String corpsTemplate,
        Map<String, Object> paramMap,
        List<ExportBlob> files
    ) {
        sendTemplateHtmlMailWithAttachedFiles(
            emailAddress,
            copieAddress,
            objetTemplate,
            corpsTemplate,
            paramMap,
            files,
            true
        );
    }

    @Override
    public void sendTemplateHtmlMailWithAttachedFiles(
        List<Address> emailAddress,
        List<Address> copieAddress,
        String objetTemplate,
        String corpsTemplate,
        Map<String, Object> paramMap,
        List<ExportBlob> files,
        boolean afterCommit
    ) {
        if (!MailSessionHelper.isMailSessionConfigured() || CollectionUtils.isEmpty(emailAddress)) {
            return;
        }

        try {
            String objet = StringHelper.renderFreemarker(objetTemplate, paramMap);
            String corps = StringHelper.renderFreemarker(corpsTemplate, paramMap);

            List<AbstractBlob> blobs = CollectionUtils.isEmpty(files)
                ? null
                : STMailHelper.initAbstractBlobListWithZip(files);

            Address mailFrom = STMailHelper.retrieveSenderMailFromConfig();
            fillAndSendMultipartMessage(
                objet,
                corps,
                new MailAddress(mailFrom, emailAddress, copieAddress, null),
                blobs,
                new JournalSendMail(),
                afterCommit
            );
        } catch (Exception e) {
            LOGGER.error(
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                new Exception(
                    "[sendTemplateHtmlMailWithAttachedFiles]" +
                    format(LOG_SEND_ERROR_TO_PLURAL, objetTemplate, StringUtils.join(emailAddress, ", "))
                )
            );
            throw new NuxeoException(ERROR_ENVOI_MAIL, e);
        }
    }

    @Override
    public void sendTemplateMail(
        String emailAddress,
        String objet,
        String corpsTemplate,
        Map<String, Object> corpsParamMap
    ) {
        sendTemplateMail(Collections.singletonList(emailAddress), objet, corpsTemplate, corpsParamMap);
    }

    /**
     * Envoie d'un mail à une liste d'utilisateurs (au format html).
     *
     * @param userList Liste d'utilisateurs
     * @param objet    Objet du mail
     * @param texte    Texte du mail
     *
     */
    private void sendHtmlMailToUserList(List<STUser> userList, String objet, String texte) {
        List<Address> recipients = new ArrayList<>();
        List<String> listeAdresse = new ArrayList<>();
        if (STMailHelper.fillRecipients(userList, recipients, listeAdresse)) {
            fillAndSendMessage(objet, texte, recipients, null, null, null, new JournalSendMail());
        }
    }

    @Override
    public void sendTemplateHtmlMailToUserList(
        CoreSession session,
        List<STUser> userList,
        String objet,
        String texte,
        Map<String, Object> params,
        List<String> dossierIdList
    ) {
        List<Address> recipients = new ArrayList<>();
        List<String> listeAdresse = new ArrayList<>();
        if (STMailHelper.fillRecipients(userList, recipients, listeAdresse)) {
            if (CollectionUtils.isNotEmpty(dossierIdList)) {
                // on ajoute les liens sur les dossiers à la fin du texte
                texte = getTexteHTMLWithLinkToDossiers(session, texte, dossierIdList);
            }
            sendTemplateHtmlMail(recipients, objet, texte, params);
        }
    }

    @Override
    public void sendTemplateHtmlMailToUserList(
        CoreSession session,
        List<STUser> userList,
        String objet,
        String texte,
        Map<String, Object> params
    ) {
        sendTemplateHtmlMailToUserList(session, userList, objet, texte, params, null);
    }

    @Override
    public void sendHtmlMailToUserListWithLinkToDossiers(
        CoreSession session,
        List<STUser> userList,
        String objet,
        String texte,
        List<String> dossierIds
    ) {
        if (CollectionUtils.isNotEmpty(userList)) {
            // renvoie le texte html avec les liens sur les dossiers
            String texteHTMLWithLinkToDossiers = getTexteHTMLWithLinkToDossiers(session, texte, dossierIds);

            // envoi le mail au format html
            sendHtmlMailToUserList(userList, objet, texteHTMLWithLinkToDossiers);
        }
    }

    /**
     * Renvoie le texte avec les liens sur les dossiers.
     *
     * @param session
     * @param texte
     * @param dossierIds
     * @return le texte avec les liens sur les dossiers.
     */
    protected String getTexteHTMLWithLinkToDossiers(CoreSession session, String texte, List<String> dossierIds) {
        final StringBuilder sbTexte = new StringBuilder();

        sbTexte.append("<html>");
        sbTexte.append("<head>");
        sbTexte.append("</head>");
        sbTexte.append("<body>");
        // récupère le corps du texte
        sbTexte.append(texte);
        // récupère les liens sur les dossiers
        sbTexte.append(getLinkHtmlToDossiers(session, dossierIds));

        sbTexte.append("</body>");
        sbTexte.append("</html>");

        // renvoie le texte
        return sbTexte.toString();
    }

    @Override
    public String getLinkHtmlToDossiers(CoreSession session, List<String> dossierIds) {
        StringBuilder linksToDossiers = new StringBuilder();
        linksToDossiers.append(" (");

        // ajoute les liens sur les dossiers à la fin du texte

        STParametreService paramService = STServiceLocator.getSTParametreService();
        String serverUrl;
        try {
            serverUrl = paramService.getParametreValue(session, STParametreConstant.ADRESSE_URL_APPLICATION);
            if (serverUrl != null) {
                // suppression de login.jsp si present
                serverUrl = serverUrl.replace("login.jsp", "");
                if (!serverUrl.endsWith("/")) {
                    serverUrl += "/";
                }
            } else {
                throw new NuxeoException("Erreur de récupération de l'url de l'application dans les parametres");
            }
        } catch (NuxeoException e) {
            LOGGER.error(STLogEnumImpl.FAIL_GET_PARAM_TEC, e);
            // utilisation de l'url de la config
            final ConfigService configService = STServiceLocator.getConfigService();
            serverUrl = configService.getValue(STConfigConstants.SERVER_URL) + "/";
        }

        final StringBuilder sbTexteDossier = new StringBuilder();
        int cptDossierIds = 0;

        try {
            for (final String idDossier : dossierIds) {
                final DocumentModel docDossier = session.getDocument(new IdRef(idDossier));

                if(docDossier.hasSchema(STSchemaConstant.FEUILLE_ROUTE_SCHEMA)) {
                	// FDR -> url du modele
                	String url = serverUrl.concat(String.join("/", "admin", "fdr" , "modele", "modification?id=" + idDossier + "#main_content "));
                    final String title = docDossier.getTitle();
                    sbTexteDossier.append("<a href=\"");
                    sbTexteDossier.append(url);
                    sbTexteDossier.append("\" target=\"_blank\">");
                    sbTexteDossier.append(title);
                    sbTexteDossier.append("</a>");
                    if (++cptDossierIds < dossierIds.size()) {
                        sbTexteDossier.append(", ");
                    }
                } else {
                	// Dossier
                	String url = serverUrl.concat(String.join("/", "dossier", idDossier, "parapheur "));
                    final String title = docDossier.getTitle();
                    sbTexteDossier.append("<a href=\"");
                    sbTexteDossier.append(url);
                    sbTexteDossier.append("\" target=\"_blank\">");
                    sbTexteDossier.append(title);
                    sbTexteDossier.append("</a>");
                    if (++cptDossierIds < dossierIds.size()) {
                        sbTexteDossier.append(", ");
                    }
                }                
            }
        } catch (Exception e1) {
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, e1);
        }
        linksToDossiers.append(sbTexteDossier.toString());

        linksToDossiers.append("). ");
        return linksToDossiers.toString();
    }

    @Override
    public void sendMailWithAttachment(
        List<String> recipientsEmail,
        String subject,
        String content,
        DocumentModel document
    ) {
        this.sendMailWithAttachment(
                recipientsEmail,
                STMailHelper.retrieveSenderMailFromConfig(),
                subject,
                content,
                document
            );
    }

    @Override
    public void sendMailWithAttachment(
        List<String> recipientsEmail,
        String mailFrom,
        String subject,
        String content,
        DocumentModel document
    ) {
        try {
            this.sendMailWithAttachment(recipientsEmail, new InternetAddress(mailFrom), subject, content, document);
        } catch (MessagingException e) {
            throw new NuxeoException("erreur envoie mail", e);
        }
    }

    private void sendMailWithAttachment(
        List<String> recipientsEmail,
        Address mailFrom,
        String subject,
        String content,
        DocumentModel document
    ) {
        try {
            fillAndSendMultipartMessage(
                subject,
                content,
                new MailAddress(mailFrom, STMailHelper.toMailAdresses(recipientsEmail), null, null),
                STMailHelper.initAbstractBlobList(document),
                new JournalSendMail()
            );
        } catch (IOException e) {
            LOGGER.error(
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                new Exception(
                    SEND_MAIL_WITH_ATTACHMENT +
                    format(LOG_SEND_ERROR_TO_PLURAL, subject, StringUtils.join(recipientsEmail, ", "))
                )
            );
            LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, e);
        }
    }

    @Override
    public void sendMailWithAttachement(
        List<String> recipientsEmail,
        String subject,
        String content,
        String fileName,
        DataSource dataSource
    ) {
        try {
            Address mailFrom = STMailHelper.retrieveSenderMailFromConfig();
            fillAndSendMultipartMessage(
                subject,
                content,
                new MailAddress(mailFrom, STMailHelper.toMailAdresses(recipientsEmail), null, null),
                STMailHelper.initAbstractBlobList(fileName, dataSource),
                new JournalSendMail()
            );
        } catch (IOException e) {
            LOGGER.error(
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                new Exception(
                    "[sendMailWithAttachement]" +
                    format(LOG_SEND_ERROR_TO_PLURAL, subject, StringUtils.join(recipientsEmail, ", "))
                )
            );
            LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, e);
        }
    }

    @Override
    public void sendMail(
        final String text,
        final String subject,
        final String factory,
        final Address[] recipients,
        final Map<String, Object> context
    ) {
        fillAndSendMessage(
            StringUtils.defaultString(subject),
            text,
            Arrays.asList(recipients),
            null,
            null,
            null,
            new JournalSendMail()
        );
    }

    @Override
    public void sendMail(List<String> recipientsEmail, String subject, String text) {
        sendMail(
            text,
            subject,
            MAIL_SESSION,
            STMailHelper.toMailAdresses(recipientsEmail).toArray(new Address[0]),
            null
        );
    }

    @Override
    public void sendMail(String text, String subject, String factory, Address[] recipients) {
        sendMail(text, subject, factory, recipients, null);
    }

    private void sendMail(List<Address> recipients, List<String> listeAdresse, String objet, String texte) {
        LOGGER.info(
            STLogEnumImpl.SEND_MAIL_TEC,
            format(LOG_SEND_OBJECT_TO_PLURAL, objet, StringUtils.join(listeAdresse, ", "))
        );
        sendMail(texte, objet, MAIL_SESSION, recipients.toArray(new Address[0]));
    }

    /**
     * Renseigne et envoie un message de type MimeMessage
     *
     * @param subject      : objet du message
     * @param content      : contenu du corps du message
     * @param emailAddress : destinataires
     * @param emailCopy    : copies (accepte la valeur null)
     * @throws AddressException
     * @throws Exception
     */
    private void fillAndSendMessage(
        final String subject,
        final String content,
        final List<Address> emailAddress,
        final List<Address> emailCopy,
        final List<Address> emailCCI,
        List<AbstractBlob> blobs,
        final JournalSendMail journalSendMail
    ) {
        Address mailFrom = STMailHelper.retrieveSenderMailFromConfig();
        fillAndSendMultipartOrStringMessage(
            subject,
            content,
            new MailAddress(mailFrom, emailAddress, emailCopy, emailCCI),
            blobs,
            journalSendMail
        );
    }

    /**
     * Renseigne et envoie un message de type MimeMessage avec un contenu multipart
     *
     * @param subject      : objet du message
     * @param content      : contenu multipart du message
     * @param emailAddress : destinataires
     * @param emailCopy    : copies (accepte la valeur null)
     * @param blobs liste des fichiers à joindre au mail (liste d'AbstractBlobs)
     */
    private void fillAndSendMultipartMessage(
        final String subject,
        final String content,
        final MailAddress mailAddress,
        List<AbstractBlob> blobs,
        final JournalSendMail journalSendMail
    ) {
        fillAndSendMultipartOrStringMessage(subject, content, mailAddress, blobs, journalSendMail);
    }

    private void fillAndSendMultipartMessage(
        final String subject,
        final String content,
        final MailAddress mailAddress,
        List<AbstractBlob> blobs,
        final JournalSendMail journalSendMail,
        boolean afterCommit
    ) {
        fillAndSendMultipartOrStringMessage(subject, content, mailAddress, blobs, journalSendMail, afterCommit);
    }

    private void fillAndSendMultipartOrStringMessage(
        final String subject,
        final String content,
        final MailAddress mailAddress,
        List<AbstractBlob> blobs,
        final JournalSendMail journalSendMail,
        boolean afterCommit
    ) {
        SendMailWork mailWork = new SendMailWork(
            subject,
            StringEscapeHelper.decodeSpecialCharactersMail(content),
            mailAddress,
            journalSendMail
        );
        //On récupère le transient store via le work
        TransientStore store = mailWork.getTransientStore();
        List<Blob> blobsList = blobs != null ? new ArrayList<>(blobs) : Collections.emptyList();
        store.putBlobs(mailWork.getId(), blobsList);

        WorkManager workManager = Framework.getService(WorkManager.class);
        workManager.schedule(mailWork, afterCommit);
    }

    private void fillAndSendMultipartOrStringMessage(
        final String subject,
        final String content,
        final MailAddress mailAddress,
        List<AbstractBlob> blobs,
        final JournalSendMail journalSendMail
    ) {
        fillAndSendMultipartOrStringMessage(subject, content, mailAddress, blobs, journalSendMail, true);
    }

    private void sendMailWithAttachments(
        List<String> recipientsEmail,
        List<String> copiesEmail,
        List<String> bccsEmail,
        String subject,
        String content,
        List<ExportBlob> listFiles
    ) {
        try {
            List<AbstractBlob> blobs = null;

            if (!listFiles.isEmpty()) {
                blobs = STMailHelper.initAbstractBlobListWithZip(listFiles);
            }
            List<Address> emailAddresses = STMailHelper.toMailAdresses(recipientsEmail);
            List<Address> copiesAddresses = STMailHelper.toMailAdresses(copiesEmail);
            List<Address> bccAddresses = STMailHelper.toMailAdresses(bccsEmail);

            InternetAddress mailFrom = STMailHelper.retrieveSenderMailFromConfig();

            fillAndSendMultipartMessage(
                subject,
                content,
                new MailAddress(mailFrom, emailAddresses, copiesAddresses, bccAddresses),
                blobs,
                new JournalSendMail()
            );
        } catch (Exception e) {
            LOGGER.error(
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                new Exception(
                    SEND_MAIL_WITH_ATTACHMENT +
                    format(LOG_SEND_ERROR_TO_PLURAL, subject, StringUtils.join(recipientsEmail, ", ")),
                    e
                )
            );
        }
    }

    @Override
    public void sendMailWithAttachments(
        List<String> recipientsEmail,
        String subject,
        String content,
        List<ExportBlob> listFiles
    ) {
        sendMailWithAttachments(recipientsEmail, null, null, subject, content, listFiles);
    }

    @Override
    public void sendMailResetPassword(CoreSession coreSession, String userLogin, String password) {
        final UserManager userManager = STServiceLocator.getUserManager();
        DocumentModel doc = userManager.getUserModel(userLogin);

        // Traite uniquement les documents de type User
        String docType = doc.getType();
        if (!"user".equals(docType)) {
            return;
        }
        STUser user = doc.getAdapter(STUser.class);

        String email = user.getEmail();

        STParametreService stParamService = STServiceLocator.getSTParametreService();

        String subject = stParamService.getParametreValue(coreSession, STParametreConstant.OBJET_MAIL_NOUVEAU_MDP);
        String message =
            stParamService.getParametreValue(coreSession, STParametreConstant.TEXTE_MAIL_NOUVEAU_MDP) +
            USER_SEND_MAIL_PASSWORD_TEMPLATE;

        message = format(message, userLogin, password);

        Address[] emailAddress = new Address[1];
        try {
            LOGGER.info(STLogEnumImpl.SEND_MAIL_TEC, format(LOG_SEND_OBJECT_TO_SINGULAR, subject, email));
            emailAddress[0] = new InternetAddress(email, user.getFullName());
            sendMail(message, subject, MAIL_SESSION, emailAddress);
        } catch (Exception e) {
            LOGGER.error(
                STLogEnumImpl.FAIL_SEND_MAIL_TEC,
                new Exception("[sendMailResetPassword]" + format(LOG_SEND_ERROR_TO_SINGULAR, subject, email), e)
            );
            throw new STException("Erreur d'envoi du mail de création du mot de passe", e);
        }
    }

    @Override
    public void sendMailWithAttachmentsAsBCC(
        List<String> bccs,
        String formObjetMail,
        String formTexteMail,
        List<ExportBlob> blobs
    ) {
        sendMailWithAttachments(null, null, bccs, formObjetMail, formTexteMail, blobs);
    }

    @Override
    public void sendArchiveMail(
        final CoreSession documentManager,
        final List<String> listMail,
        final Boolean formCopieMail,
        final String formObjetMail,
        final String formTexteMail,
        final byte[] bytes
    )
        throws MessagingException {
        // construct the pdf body part
        final String nomFichier =
            "export_" +
            getMailArchiveSuffix() +
            SolonDateConverter.DATE_DASH.formatNow() +
            "." +
            MediaType.APPLICATION_ZIP.extension();

        final DataSource dataSource = new ByteArrayDataSource(bytes, MediaType.APPLICATION_ZIP.mime());

        final MimeBodyPart pdfBodyPart = new MimeBodyPart();
        pdfBodyPart.setDataHandler(new DataHandler(dataSource));
        pdfBodyPart.setFileName(nomFichier);

        // construct the text body part
        final MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setContent(solonMailPrefixBody + formTexteMail, CONTENT_FORMAT);

        // construct the mime multi part
        final MimeMultipart mimeMultipart = new MimeMultipart();
        mimeMultipart.addBodyPart(textBodyPart);
        mimeMultipart.addBodyPart(pdfBodyPart);

        final Session session = MailSessionHelper.getMailSession();

        // create the sender addresses
        final InternetAddress iaSender = new InternetAddress(
            solonMailPrefixFrom + session.getProperty(STConfigConstants.MAIL_FROM)
        );

        // construct the mime message
        final Message mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(iaSender);
        mimeMessage.setSubject(solonMailPrefixObject + formObjetMail);
        mimeMessage.setContent(mimeMultipart);

        // destinataire en copie
        if (Boolean.TRUE.equals(formCopieMail)) {
            final NuxeoPrincipal userName = documentManager.getPrincipal();
            final String userMail = STServiceLocator.getOrganigrammeService().getMailFromUsername(userName.getName());
            if (!userMail.isEmpty()) {
                try {
                    final InternetAddress iaRecipient = new InternetAddress(userMail);
                    mimeMessage.addRecipient(Message.RecipientType.TO, iaRecipient);
                } catch (AddressException e) {
                    LOGGER.warn(STLogEnumImpl.FAIL_GET_MAIL_TEC, e);
                }
            }
        }

        // Destinaires choisis
        for (final String destMail : listMail) {
            try {
                final InternetAddress iaRecipient = new InternetAddress(destMail);
                mimeMessage.addRecipient(Message.RecipientType.TO, iaRecipient);
            } catch (AddressException e) {
                LOGGER.warn(STLogEnumImpl.FAIL_GET_MAIL_TEC, e);
            }
        }

        mimeMessage.setFrom(STMailHelper.retrieveSenderMailFromConfig());

        STMailHelper.setSentDate(mimeMessage);
        Transport.send(mimeMessage);
    }

    protected String getMailArchiveSuffix() {
        return StringUtils.EMPTY;
    }
}
