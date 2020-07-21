package fr.dila.st.api.service;

import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.domain.file.ExportBlob;
import fr.dila.st.api.user.STUser;

/**
 * Surcharge du MailService de Nuxeo
 * 
 * @author Fabio Esposito
 */
public interface STMailService {

	/**
	 * Envoie d'un mail à une liste d'utilisateurs.
	 * 
	 * @param session
	 *            Session
	 * @param userList
	 *            Liste d'utilisateurs.
	 * @param objet
	 *            Objet du mail
	 * @param texte
	 *            Texte du mail
	 * @throws ClientException
	 */
	void sendMailToUserList(CoreSession session, List<STUser> userList, String objet, String texte)
			throws ClientException;

	/**
	 * Envoie d'un mail à une liste d'utilisateurs (au format html).
	 * 
	 * @param session
	 *            Session
	 * @param userList
	 *            Liste d'utilisateurs
	 * @param objet
	 *            Objet du mail
	 * @param texte
	 *            Texte du mail
	 * @throws ClientException
	 */
	void sendHtmlMailToUserList(CoreSession session, List<STUser> userList, String objet, String texte)
			throws ClientException;

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
	 * @throws ClientException
	 */
	void sendHtmlMailToUserListWithLinkToDossiers(CoreSession session, List<STUser> userList, String objet,
			String texte, List<String> dossierIds) throws ClientException;

	/**
	 * Envoie un mail à l'utilisateur lors de sa création
	 * 
	 * @param session
	 *            CoreSession
	 * @param userId
	 *            id de l'utilisateur
	 * @param password
	 *            le mot de passe à envoyer
	 * @throws ClientException
	 */
	void sendMailUserPasswordCreation(CoreSession session, String userId, String password) throws ClientException;

	/**
	 * Envoie un mail aux utilisateurs contenu dans la liste.
	 * 
	 * @param session
	 * @param userList
	 * @param objet
	 * @param texte
	 * @throws ClientException
	 */
	void sendMailNotificationToUserList(CoreSession session, List<STUser> userList, String objet, String texte)
			throws ClientException;

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
	 * @throws ClientException
	 */
	void sendTemplateMail(List<String> emailAddress, String objet, String corpsTemplate,
			Map<String, Object> corpsParamMap) throws ClientException;

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
	 * @throws ClientException
	 */
	void sendTemplateHtmlMail(List<Address> emailAddress, String objet, String corpsTemplate,
			Map<String, Object> corpsParamMap) throws ClientException;

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
	 * @throws ClientException
	 */
	void sendTemplateMail(String emailAddress, String objet, String corpsTemplate, Map<String, Object> corpsParamMap)
			throws ClientException;

	void sendMailWithAttachment(List<String> recipientsEmail, String subject, String content, DocumentModel document)
			throws Exception;

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
	void sendMailWithAttachement(List<String> recipientsEmail, String subject, String content, String nomFichier,
			DataSource dataSource) throws Exception;

	/**
	 * Envoi un mail contenant une pièce jointe <b>dataSource</b> à des destinataires.
	 * 
	 * @param recipients
	 * @param subject
	 * @param content
	 * @param nomFichier
	 * @param dataSource
	 * @throws Exception
	 */
	void sendMailWithAttachementToRecipients(List<Address> recipients, String subject, String content,
			String nomFichier, DataSource dataSource) throws Exception;

	/**
	 * Envoi un mail contenant une pièce jointe <b>dataSource</b> à des destinataires.
	 * 
	 * @param userList
	 *            : liste de STUser
	 * @param subject
	 * @param content
	 * @param nomFichier
	 * @param dataSource
	 * 
	 * @throws Exception
	 */
	void sendMailWithAttachementToUserList(List<STUser> userList, String subject, String content, String nomFichier,
			DataSource dataSource) throws Exception;

	/**
	 * Teste si la contrib mail est chargée dans le mailService nuxeo
	 * 
	 * @return true si la contrib est ok
	 * @throws ClientException
	 */
	boolean isMailSessionConfigured() throws ClientException;

	/**
	 * Envoie un mail en HTML à plusieurs destinataires à partir d'un template avec des fichiers attachés.
	 * 
	 * @param emailAddress
	 *            Liste des emails des destinataires
	 * @param copieAddress
	 *            Liste des emails des copies
	 * @param objet
	 *            Template du sujet, les variables sont de la forme ${sender}
	 * @param corpsTemplate
	 *            Template du corps, les variables sont de la forme ${sender}
	 * @param corpsParamMap
	 *            La map contenant les variables
	 * @param files
	 *            fichiers à attacher
	 * @throws ClientException
	 */
	void sendTemplateHtmlMailWithAttachedFiles(List<Address> emailAddress, List<Address> copieAddress,
			String objetTemplate, String corpsTemplate, Map<String, Object> paramMap, List<ExportBlob> files)
			throws ClientException;

	/**
	 * Ajout l'expediteur et la date d'expedition a un mail
	 * 
	 * @param message
	 * @throws MessagingException
	 */
	void setFromAndSentDate(Message message) throws MessagingException;

	/**
	 * Sends a mail using the setting of this factory to this recipients.
	 * <p>
	 * The context is passed to the sessionFactory to be able to find the session. Use the default session if context is
	 * not used. This template is used, replacing variables with the ones from this variables.
	 */
	void sendMail(String text, String subject, String factory, Address[] recipients) throws Exception;

	void sendMail(String text, String subject, String factory, Address[] recipients, Map<String, Object> context)
			throws Exception;

	/**
	 * Envoie un mail en HTML à plusieurs destinataires à partir d'un template.
	 * 
	 * @param session
	 * @param userList
	 * @param objet
	 * @param texte
	 * @param params
	 * @throws ClientException
	 */
	void sendTemplateHtmlMailToUserList(CoreSession session, List<STUser> userList, String objet, String texte,
			Map<String, Object> params) throws ClientException;

	/**
	 * Envoie un mail en HTML avec des liens sur des dossiers à plusieurs destinataires à partir d'un template.
	 * 
	 * @param session
	 * @param userList
	 * @param objet
	 * @param texte
	 * @param params
	 * @param dossierId
	 * @throws ClientException
	 */
	void sendTemplateHtmlMailToUserList(CoreSession session, List<STUser> userList, String objet, String texte,
			Map<String, Object> params, List<String> dossierId) throws ClientException;

	/**
	 * Renvoie les liens HTML sur les dossiers. Utilisés lors de l'envoi multiple de mails pour éviter de lancer
	 * plusieurs fois la requête.
	 * 
	 * @param session
	 * @param dossierIds
	 * @param sbTexte
	 * @throws ClientException
	 */
	String getLinkHtmlToDossiers(CoreSession session, List<String> dossierIds) throws ClientException;

	void sendMailWithAttachments(List<String> recipientsEmail, String formObjetMail, String formTexteMail,
			List<ExportBlob> listFiles) throws Exception;

	void sendMailResetPassword(CoreSession coreSession, String userLogin, String password) throws ClientException;

	void sendMailWithAttachmentsAsBCC(List<String> recipients, String formObjetMail, String formTexteMail,
			List<ExportBlob> blobs);

	void sendMailToUserListAsBCC(CoreSession session, List<STUser> userList, String objet, String texte)
			throws ClientException;

}
