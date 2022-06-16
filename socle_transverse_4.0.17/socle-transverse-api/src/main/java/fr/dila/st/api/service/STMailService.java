package fr.dila.st.api.service;

import fr.dila.st.api.domain.file.ExportBlob;
import fr.dila.st.api.user.STUser;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;

/**
 * Surcharge du MailService de Nuxeo
 *
 * @author Fabio Esposito
 */
public interface STMailService {
    /**
     * Envoie d'un mail texte à une liste d'utilisateurs.
     * @param userList Liste d'utilisateurs.
     * @param objet    Objet du mail
     * @param texte    Texte du mail
     *
     */
    void sendMailToUserList(List<STUser> userList, String objet, String texte);

    /**
     * Envoie d'un mail à une liste d'utilisateurs (au format html) avec des liens sur des dossiers en fin de texte.
     *
     * @param session
     *            Session
     * @param userList
     *            Liste d'utilisateurs
     * @param objet
     *            Objet du mail
     * @param texte
     *            Texte du mail
     * @param dossierIds
     *            Identifiants des dossiers utilisés pour les liens
     *
     */
    void sendHtmlMailToUserListWithLinkToDossiers(
        CoreSession session,
        List<STUser> userList,
        String objet,
        String texte,
        List<String> dossierIds
    );

    /**
     * Envoie un mail à l'utilisateur lors de sa création
     *
     * @param session
     *            CoreSession
     * @param userId
     *            id de l'utilisateur
     * @param password
     *            le mot de passe à envoyer
     *
     */
    void sendMailUserPasswordCreation(CoreSession session, String userId, String password);

    /**
     * Envoie un mail aux utilisateurs contenu dans la liste.
     * @param userList
     * @param objet
     * @param texte
     *
     */
    void sendMailNotificationToUserList(List<STUser> userList, String objet, String texte);

    /**
     * Envoie un mail à plusieurs destinataires à partir d'un template.
     *
     * @param emailAddress
     *            Liste des emails des destinataires
     * @param objet
     *            Objet du mail
     * @param corpsTemplate
     *            Le template du corps, les variables sont de la forme ${sender}
     * @param corpsParamMap
     *            La map contenant les variables
     *
     */
    void sendTemplateMail(
        List<String> emailAddress,
        String objet,
        String corpsTemplate,
        Map<String, Object> corpsParamMap
    );

    /**
     * Envoie un mail en HTML à plusieurs destinataires à partir d'un template.
     *
     * @param emailAddress
     *            Liste des emails des destinataires
     * @param objet
     *            Template du sujet, les variables sont de la forme ${sender}
     * @param corpsTemplate
     *            Template du corps, les variables sont de la forme ${sender}
     * @param corpsParamMap
     *            La map contenant les variables
     *
     */
    void sendTemplateHtmlMail(
        List<Address> emailAddress,
        String objet,
        String corpsTemplate,
        Map<String, Object> corpsParamMap
    );

    /**
     * Envoie un mail à un destinataire à partir d'un template.
     *
     * @param emailAddress
     *            Email du destinataire.
     * @param objet
     *            Objet du mail
     * @param corpsTemplate
     *            Le template du corps, les vraiables sont de la forme ${sender}
     * @param corpsParamMap
     *            La map contenant les variables
     *
     */
    void sendTemplateMail(String emailAddress, String objet, String corpsTemplate, Map<String, Object> corpsParamMap);

    void sendMailWithAttachment(List<String> recipientsEmail, String subject, String content, DocumentModel document);

    void sendMailWithAttachment(
        List<String> recipientsEmail,
        String mailFrom,
        String subject,
        String content,
        DocumentModel document
    );

    /**
     * Envoi un mail contenant une pièce jointe <b>dataSource</b> à des destinataires.
     *
     * @param recipientsEmail
     * @param subject
     * @param content
     * @param nomFichier
     * @param dataSource
     * @throws Exception
     */
    void sendMailWithAttachement(
        List<String> recipientsEmail,
        String subject,
        String content,
        String nomFichier,
        DataSource dataSource
    );

    /**
     * Renvoie les liens HTML sur les dossiers. Utilisés lors de l'envoi multiple de mails pour éviter de lancer
     * plusieurs fois la requête.
     *
     * @param session
     * @param dossierIds
     *
     */
    String getLinkHtmlToDossiers(CoreSession session, List<String> dossierIds);

    /**
     * Envoie un mail en HTML à plusieurs destinataires à partir d'un template avec des fichiers attachés.
     *
     * @param emailAddress
     *            Liste des emails des destinataires
     * @param copieAddress
     *            Liste des emails des copies
     * @param objetTemplate
     *            Template du sujet, les variables sont de la forme ${sender}
     * @param corpsTemplate
     *            Template du corps, les variables sont de la forme ${sender}
     * @param paramMap
     *            La map contenant les variables
     * @param files
     *            fichiers à attacher
     *
     */
    void sendTemplateHtmlMailWithAttachedFiles(
        List<Address> emailAddress,
        List<Address> copieAddress,
        String objetTemplate,
        String corpsTemplate,
        Map<String, Object> paramMap,
        List<ExportBlob> files
    );

    void sendMail(List<String> recipientsEmail, String subject, String text);

    /**
     * Sends a mail using the setting of this factory to this recipients.
     * <p>
     * The context is passed to the sessionFactory to be able to find the session. Use the default session if context is
     * not used. This template is used, replacing variables with the ones from this variables.
     */
    void sendMail(String text, String subject, String factory, Address[] recipients);

    void sendMail(String text, String subject, String factory, Address[] recipients, Map<String, Object> context);

    /**
     * Envoie un mail en HTML à plusieurs destinataires à partir d'un template.
     *
     * @param session
     * @param userList
     * @param objet
     * @param texte
     * @param params
     *
     */
    void sendTemplateHtmlMailToUserList(
        CoreSession session,
        List<STUser> userList,
        String objet,
        String texte,
        Map<String, Object> params
    );

    /**
     * Envoie un mail en HTML avec des liens sur des dossiers à plusieurs destinataires à partir d'un template.
     *
     * @param session
     * @param userList
     * @param objet
     * @param texte
     * @param params
     * @param dossierId
     *
     */
    void sendTemplateHtmlMailToUserList(
        CoreSession session,
        List<STUser> userList,
        String objet,
        String texte,
        Map<String, Object> params,
        List<String> dossierId
    );

    void sendMailWithAttachments(
        List<String> recipientsEmail,
        String formObjetMail,
        String formTexteMail,
        List<ExportBlob> listFiles
    );

    void sendMailResetPassword(CoreSession coreSession, String userLogin, String password);

    void sendMailWithAttachmentsAsBCC(
        List<String> recipients,
        String formObjetMail,
        String formTexteMail,
        List<ExportBlob> blobs
    );

    /**
     * Envoi d'un message multipart avec PJ à un groupe de copies cachées
     * @param userList
     * @param objet
     * @param texte
     * @param blobs List<AbstractBlob>
     * @param mailFrom adresse mail émettrice
     */
    void sendMailToUserListAsBCC(
        List<STUser> userList,
        String objet,
        String texte,
        List<AbstractBlob> blobs,
        String mailFrom
    );

    /**
     * Envoi d'un mail d'archivage (spécifique Réponses)
     *
     * @param documentManager
     * @param listMail
     * @param formCopieMail
     * @param formObjetMail
     * @param formTexteMail
     * @param bytes
     * @throws MessagingException
     * @throws AddressException
     */
    void sendArchiveMail(
        final CoreSession documentManager,
        final List<String> listMail,
        final Boolean formCopieMail,
        final String formObjetMail,
        final String formTexteMail,
        final byte[] bytes
    )
        throws MessagingException, AddressException;

    void sendTemplateHtmlMailWithAttachedFiles(
        List<Address> emailAddress,
        List<Address> copieAddress,
        String objetTemplate,
        String corpsTemplate,
        Map<String, Object> paramMap,
        List<ExportBlob> files,
        boolean afterCommit
    );
}
