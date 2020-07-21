package fr.dila.st.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.automation.core.mail.Composer;
import org.nuxeo.ecm.automation.core.mail.Mailer;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.platform.mail.fetcher.PropertiesFetcher;
import org.nuxeo.ecm.platform.mail.service.MailService;
import org.nuxeo.ecm.platform.ui.web.rest.api.URLPolicyService;
import org.nuxeo.ecm.platform.url.DocumentViewImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.domain.file.ExportBlob;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.BlobDataSource;
import fr.dila.st.core.util.StringUtil;

/**
 * Service mail
 * 
 * @author Fabio Esposito
 */
public class STMailServiceImpl implements STMailService {

	public static final String MAIL_SESSION = "mailSession";

	private static final STLogger LOGGER = STLogFactory.getLog(STMailServiceImpl.class);

	private static final String LOG_SEND_OBJECT = "Envoi d'un mél ayant pour objet : ";
	private static final String LOG_SEND_TO = " aux adresses suivantes : ";
	private static final String LOG_SEND_CC = " et en copie aux adresses suivantes : ";
	private static final String LOG_SEND_BCC = " et en copie carbone aux adresses suivantes : ";
	private static final String LOG_SEND_ERROR = "Erreur lors de l'envoi d'un mél ayant pour objet : ";
	private static final String LOG_SEND_NOTIFICATION_ERROR = "Erreur d'envoi du mail de notification";
	private static final String CONTENT_FORMAT = "text/html; charset=UTF-8";

	/**
	 * Default constructor
	 */
	public STMailServiceImpl() {
		// do nothing
	}

	@Override
	public void sendMailToUserList(CoreSession session, List<STUser> userList, String objet, String texte)
			throws ClientException {
		if (!isMailSessionConfigured()) {
			return;
		}
		List<String> listeAdresse = new ArrayList<String>();
		List<Address> recipients = new ArrayList<Address>();
		if (fillRecipients(userList, recipients, listeAdresse)) {
			sendMail(recipients, listeAdresse, objet, texte);
		}
	}

	@Override
	public void sendMailToUserListAsBCC(CoreSession session, List<STUser> userList, String objet, String texte)
			throws ClientException {
		if (!isMailSessionConfigured()) {
			return;
		}
		List<String> listeAdresse = new ArrayList<String>();
		List<Address> recipients = new ArrayList<Address>();
		if (fillRecipients(userList, recipients, listeAdresse)) {
			try {
				ArrayList<Address> appMail = new ArrayList<Address>();
				ConfigService configService = STServiceLocator.getConfigService();
				appMail.add(new InternetAddress(configService.getValue(STConfigConstants.MAIL_FROM)));
				fillAndSendMessage(objet, texte, appMail, null, recipients);
			} catch (Exception e) {
				LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC,
						new Exception("[sendMailToUserListAsBCC]" + LOG_SEND_ERROR + objet
								+ " à plusieurs utilisateurs : " + StringUtils.join(listeAdresse, ","), e));
			}
		}
	}

	@Override
	public void sendMailNotificationToUserList(CoreSession session, List<STUser> userList, String objet, String texte)
			throws ClientException {
		List<Address> recipients = new ArrayList<Address>();
		List<String> listeAdresse = new ArrayList<String>();
		if (fillRecipients(userList, recipients, listeAdresse)) {
			sendMail(recipients, listeAdresse, objet, texte);
		}
	}

	@Override
	public void sendMailUserPasswordCreation(CoreSession session, String userId, String password)
			throws ClientException {
		final UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel doc = userManager.getUserModel(userId);

		// Traite uniquement les documents de type User
		String docType = doc.getType();
		if (!"user".equals(docType)) {
			return;
		}
		STUser user = doc.getAdapter(STUser.class);

		String email = user.getEmail();
		String fname = user.getFirstName();
		String lname = user.getLastName();

		STParametreService stParamService = STServiceLocator.getSTParametreService();

		String subject = stParamService.getParametreValue(session,
				STParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_OBJET);
		String message = stParamService.getParametreValue(session,
				STParametreConstant.NOTIFICATION_MAIL_CREATION_UTILISATEUR_TEXT) + "\n\n- login : %s\n"
				+ "- mot de passe : %s\n\n";

		message = String.format(message, userId, password);

		Address[] emailAddress = new Address[1];
		try {
			LOGGER.info(STLogEnumImpl.SEND_MAIL_TEC, LOG_SEND_OBJECT + subject + " à l'adresse suivante : " + email);
			emailAddress[0] = new InternetAddress(email, fname + " " + lname);
			sendMail(message, subject, MAIL_SESSION, emailAddress);
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception(
					"[sendMailUserPasswordCreation]" + LOG_SEND_ERROR + subject + " à l'adresse suivante : " + email));
			throw new ClientException("Erreur d'envoi du mail de création du mot de passe", e);
		}
	}

	@Override
	public void sendTemplateMail(List<String> emailAddress, String objet, String corpsTemplate,
			Map<String, Object> paramMap) throws ClientException {
		if (!isMailSessionConfigured() || emailAddress == null || emailAddress.isEmpty()) {
			return;
		}

		try {
			LOGGER.info(STLogEnumImpl.SEND_MAIL_TEC,
					LOG_SEND_OBJECT + objet + LOG_SEND_TO + StringUtils.join(emailAddress, ", "));
			PropertiesFetcher fetcher = getMailService().getFetcher(MAIL_SESSION);
			Mailer mailer = new Mailer();
			mailer.setConfiguration(fetcher.getProperties(new HashMap<String, Object>()));
			Composer composer = new Composer(mailer);
			Mailer.Message msg = composer.newTextMessage(corpsTemplate, paramMap);
			msg.setSubject(objet);

			for (String address : emailAddress) {
				msg.addTo(address);
			}
			setFromAndSentDate(msg);

			msg.send();
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception("[sendTemplateMail]" + LOG_SEND_ERROR + objet
					+ LOG_SEND_TO + StringUtils.join(emailAddress, ", ")));
			throw new ClientException("Erreur d'envoi du mail", e);
		}
	}

	@Override
	public void sendTemplateHtmlMail(List<Address> emailAddress, String objetTemplate, String corpsTemplate,
			Map<String, Object> paramMap) throws ClientException {
		if (!isMailSessionConfigured() || emailAddress == null || emailAddress.isEmpty()) {
			return;
		}
		try {
			String objet = StringUtil.renderFreemarker(objetTemplate, paramMap);
			String corps = StringUtil.renderFreemarker(corpsTemplate, paramMap);

			fillAndSendMessage(objet, corps, emailAddress, null, null);
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception("[sendTemplateHtmlMail]" + LOG_SEND_ERROR
					+ objetTemplate + LOG_SEND_TO + StringUtils.join(emailAddress, ", ")));
			throw new ClientException("Erreur d'envoi du mail", e);
		}
	}

	@Override
	public void sendTemplateHtmlMailWithAttachedFiles(List<Address> emailAddress, List<Address> copieAddress,
			String objetTemplate, String corpsTemplate, Map<String, Object> paramMap, List<ExportBlob> files)
			throws ClientException {
		if (!isMailSessionConfigured() || emailAddress == null || emailAddress.isEmpty()) {
			return;
		}

		try {
			String objet = StringUtil.renderFreemarker(objetTemplate, paramMap);
			String corps = StringUtil.renderFreemarker(corpsTemplate, paramMap);

			String fileName = "pieces_jointes.zip";
			DataHandler dataMime = null;
			if (!files.isEmpty()) {
				// now write the ZIP content to the output stream
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
				zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);

				// Attach the files to the message
				for (ExportBlob exportBlob : files) {
					Blob blob = exportBlob.getBlob();
					if (blob != null) {
						writeBlobToZipStream(zipOutputStream, exportBlob.getPath() + "/" + blob.getFilename(), blob);
					}
				}
				zipOutputStream.close();
				byte[] bytes = outputStream.toByteArray();

				// construct the zip body part
				DataSource dataSource = new ByteArrayDataSource(bytes, "application/zip");
				dataMime = new DataHandler(dataSource);
			}
			Multipart multipart = initMultipart(corps, dataMime, fileName);
			fillAndSendMultipartMessage(objet, multipart, emailAddress, copieAddress, null);
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception("[sendTemplateHtmlMailWithAttachedFiles]"
					+ LOG_SEND_ERROR + objetTemplate + LOG_SEND_TO + StringUtils.join(emailAddress, ", ")));
			throw new ClientException("Erreur d'envoi du mail", e);
		}
	}

	private void writeBlobToZipStream(ZipOutputStream out, String path, Blob blob) {

		// nettoyage du chemin
		path = org.nuxeo.common.utils.StringUtils.toAscii(path);
		ZipEntry entry = null;
		// write blobs
		try {
			entry = new ZipEntry(path);
			out.putNextEntry(entry);
			InputStream inputStream = null;
			try {
				inputStream = blob.getStream();
				FileUtils.copy(inputStream, out);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
				out.closeEntry();
			}
		} catch (IOException e) {
			LOGGER.error(STLogEnumImpl.FAIL_ZIP_FILE_TEC, e);
		}
	}

	@Override
	public void sendTemplateMail(String emailAddress, String objet, String corpsTemplate,
			Map<String, Object> corpsParamMap) throws ClientException {
		sendTemplateMail(Collections.singletonList(emailAddress), objet, corpsTemplate, corpsParamMap);
	}

	@Override
	public void sendHtmlMailToUserList(CoreSession session, List<STUser> userList, String objet, String texte)
			throws ClientException {
		List<Address> recipients = new ArrayList<Address>();
		List<String> listeAdresse = new ArrayList<String>();
		if (fillRecipients(userList, recipients, listeAdresse)) {
			try {
				fillAndSendMessage(objet, texte, recipients, null, null);
			} catch (Exception e) {
				LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception("[sendHtmlMailToUserList]" + LOG_SEND_ERROR
						+ objet + LOG_SEND_TO + StringUtils.join(listeAdresse, ", ")));
				throw new ClientException("Erreur d'envoi du mail du mail de notification", e);
			}
		}
	}

	@Override
	public void sendTemplateHtmlMailToUserList(CoreSession session, List<STUser> userList, String objet, String texte,
			Map<String, Object> params, List<String> dossierIdList) throws ClientException {
		List<Address> recipients = new ArrayList<Address>();
		List<String> listeAdresse = new ArrayList<String>();
		if (fillRecipients(userList, recipients, listeAdresse)) {
			if (dossierIdList != null && !dossierIdList.isEmpty()) {
				// on ajoute les liens sur les dossiers à la fin du texte
				texte = getTexteHTMLWithLinkToDossiers(session, texte, dossierIdList);
			}
			sendTemplateHtmlMail(recipients, objet, texte, params);
		}
	}

	@Override
	public void sendTemplateHtmlMailToUserList(CoreSession session, List<STUser> userList, String objet, String texte,
			Map<String, Object> params) throws ClientException {
		sendTemplateHtmlMailToUserList(session, userList, objet, texte, params, null);
	}

	@Override
	public void sendHtmlMailToUserListWithLinkToDossiers(CoreSession session, List<STUser> userList, String objet,
			String texte, List<String> dossierIds) throws ClientException {
		// renvoie le texte html avec les liens sur les dossiers
		String texteHTMLWithLinkToDossiers = getTexteHTMLWithLinkToDossiers(session, texte, dossierIds);

		// envoi le mail au format html
		sendHtmlMailToUserList(session, userList, objet, texteHTMLWithLinkToDossiers);
	}

	/**
	 * Renvoie le texte avec les liens sur les dossiers.
	 * 
	 * @param session
	 * @param texte
	 * @param dossierIds
	 * @return le texte avec les liens sur les dossiers.
	 * @throws ClientException
	 */
	protected String getTexteHTMLWithLinkToDossiers(CoreSession session, String texte, List<String> dossierIds)
			throws ClientException {
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
	public String getLinkHtmlToDossiers(CoreSession session, List<String> dossierIds) throws ClientException {
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
				throw new ClientException("Erreur de récupération de l'url de l'application dans les parametres");
			}
		} catch (ClientException e) {
			LOGGER.error(STLogEnumImpl.FAIL_GET_PARAM_TEC, e);
			// utilisation de l'url de la config
			final ConfigService configService = STServiceLocator.getConfigService();
			serverUrl = configService.getValue(STConfigConstants.SERVER_URL) + "/";
		}

		final StringBuilder sbTexteDossier = new StringBuilder();
		int cptDossierIds = 0;

		URLPolicyService service;
		try {
			service = Framework.getService(URLPolicyService.class);

			for (final String idDossier : dossierIds) {
				final DocumentModel docDossier = session.getDocument(new IdRef(idDossier));
				String url = "";

				try {
					DocumentViewImpl docView = new DocumentViewImpl(docDossier);
					// generate url
					url = service.getUrlFromDocumentView(docView, serverUrl);

				} catch (Exception e) {
					LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, e);
				}
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
		} catch (Exception e1) {
			LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, e1);
		}
		linksToDossiers.append(sbTexteDossier.toString());

		linksToDossiers.append("). ");
		return linksToDossiers.toString();
	}

	@Override
	public void sendMailWithAttachment(List<String> recipientsEmail, String subject, String content,
			DocumentModel document) throws Exception {
		try {
			// Un nom de fichier par defaut
			String fileName = "piece_jointe";
			DataHandler dataMime = null;
			// Attach the files to the message
			if (document != null) {
				BlobHolder blobHolder = document.getAdapter(BlobHolder.class);
				BlobDataSource blobDs = new BlobDataSource(blobHolder.getBlob(), blobHolder.getBlob().getFilename());
				dataMime = new DataHandler(blobDs);
				fileName = blobDs.getName();
			}
			Multipart multipart = initMultipart(content, dataMime, fileName);
			List<Address> emailAddresses = new ArrayList<Address>();
			for (String address : recipientsEmail) {
				emailAddresses.add(new InternetAddress(address));
			}
			fillAndSendMultipartMessage(subject, multipart, emailAddresses, null, null);
		} catch (MessagingException e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception("[sendMailWithAttachment]" + LOG_SEND_ERROR
					+ subject + LOG_SEND_TO + StringUtils.join(recipientsEmail, ", ")));
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, e);
		}
	}

	@Override
	public void sendMailWithAttachement(List<String> recipientsEmail, String subject, String content, String fileName,
			DataSource dataSource) throws Exception {
		try {
			DataHandler dataMime = new DataHandler(dataSource);
			Multipart multipart = initMultipart(content, dataMime, fileName);

			List<Address> emailAddresses = new ArrayList<Address>();
			for (String address : recipientsEmail) {
				emailAddresses.add(new InternetAddress(address));
			}
			fillAndSendMultipartMessage(subject, multipart, emailAddresses, null, null);
		} catch (MessagingException e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception("[sendMailWithAttachement]" + LOG_SEND_ERROR
					+ subject + LOG_SEND_TO + StringUtils.join(recipientsEmail, ", ")));
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, e);
		}
	}

	@Override
	public void sendMailWithAttachementToUserList(List<STUser> userList, String subject, String content,
			String nomFichier, DataSource dataSource) throws Exception {
		List<Address> recipients = new ArrayList<Address>();
		List<String> listeAdresse = new ArrayList<String>();
		if (fillRecipients(userList, recipients, listeAdresse)) {
			sendMailWithAttachementToRecipients(recipients, subject, content, nomFichier, dataSource);
		}
	}

	@Override
	public void sendMailWithAttachementToRecipients(List<Address> recipients, String subject, String content,
			String nomFichier, DataSource dataSource) throws Exception {
		try {
			DataHandler dataMime = new DataHandler(dataSource);
			Multipart multipart = initMultipart(content, dataMime, nomFichier);

			fillAndSendMultipartMessage(subject, multipart, recipients, null, null);
		} catch (MessagingException e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception("[sendMailWithAttachement]" + LOG_SEND_ERROR
					+ subject + LOG_SEND_TO + StringUtils.join(recipients, ", ")));
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, e);
		}
	}

	@Override
	public boolean isMailSessionConfigured() throws ClientException {
		try {
			PropertiesFetcher fetcher = getMailService().getFetcher(MAIL_SESSION);
			return fetcher != null;
		} catch (NullPointerException npe) {
			return false;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public void sendMail(final String text, final String subject, final String factory, final Address[] recipients,
			final Map<String, Object> context) throws Exception {

		List<String> addressList = new ArrayList<String>();
		List<Address> emailAddresses = new ArrayList<Address>();
		for (Address address : recipients) {
			String mail = ((InternetAddress) address).getAddress();
			addressList.add(mail);
			emailAddresses.add(address);
		}

		fillAndSendMessage(StringUtils.defaultString(subject), text, emailAddresses, null, null);
	}

	private MailService getMailService() throws Exception {
		return Framework.getService(MailService.class);
	}

	@Override
	public void setFromAndSentDate(Message message) throws MessagingException {
		message.setSentDate(Calendar.getInstance().getTime());

		final ConfigService configService = STServiceLocator.getConfigService();

		InternetAddress internetAddress = null;
		try {
			String mailFrom = configService.getValue(STConfigConstants.MAIL_FROM);
			internetAddress = new InternetAddress(mailFrom);
		} catch (Exception e) {
			// pas de mail.from dans la config
			LOGGER.warn(STLogEnumImpl.FAIL_SEND_MAIL_TEC, e);
			internetAddress = new InternetAddress("ne-pas-repondre@dila.gouv.fr");
		}

		message.setFrom(internetAddress);
	}

	@Override
	public void sendMail(String text, String subject, String factory, Address[] recipients) throws Exception {
		sendMail(text, subject, factory, recipients, null);
	}

	private void sendMail(List<Address> recipients, List<String> listeAdresse, String objet, String texte)
			throws ClientException {
		try {
			LOGGER.info(STLogEnumImpl.SEND_MAIL_TEC,
					LOG_SEND_OBJECT + objet + LOG_SEND_TO + StringUtils.join(listeAdresse, ", "));
			sendMail(texte, objet, MAIL_SESSION, recipients.toArray(new Address[recipients.size()]));
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC,
					new Exception(LOG_SEND_ERROR + objet + LOG_SEND_TO + StringUtils.join(listeAdresse, ", ")));
			throw new ClientException(LOG_SEND_NOTIFICATION_ERROR, e);
		}
	}

	/**
	 * Renseigne les listes d'adresse et de destinataires à partir d'une liste
	 * d'user
	 * 
	 * @param userList
	 *            la liste d'utilisateur source
	 * @param recipients
	 *            liste d'Address des destinataires
	 * @param listeAdresse
	 *            liste des mails des destinataires
	 * @return true s'il existe des destinataires
	 * @throws ClientException
	 */
	private boolean fillRecipients(List<STUser> userList, List<Address> recipients, List<String> listeAdresse)
			throws ClientException {
		boolean isRecipients = false;
		for (STUser user : userList) {
			String mailAdress = user.getEmail();
			if (!listeAdresse.toString().matches(".*\\b" + mailAdress + "\\b.*")) {
				listeAdresse.add(mailAdress);
				if (mailAdress != null && !"".equals(mailAdress)) {
					isRecipients = true;
					Address address;
					try {
						address = new InternetAddress(mailAdress);
					} catch (AddressException e) {
						throw new ClientException(e);
					}
					recipients.add(address);
				}
			}
		}
		return isRecipients;
	}

	private static String joinAddresses(Address[] emailAdresses) {
		StringBuffer buffer = new StringBuffer();

		for (Address address : emailAdresses) {
			buffer.append(address.toString());
			buffer.append(", ");
		}

		return buffer.toString();
	}

	/**
	 * Trace l'envoi du message avant de l'envoyer réellement.
	 * 
	 * @param message
	 *            un message
	 * @throws MessagingException
	 */
	private void send(Message message) throws MessagingException {
		StringBuffer buffer = new StringBuffer(LOG_SEND_OBJECT + message.getSubject() + LOG_SEND_TO
				+ joinAddresses(message.getRecipients(RecipientType.TO)));

		Address[] ccDest = message.getRecipients(RecipientType.CC);
		if (ccDest != null && ccDest.length > 0) {
			buffer.append(LOG_SEND_CC + joinAddresses(ccDest));
		}

		Address[] bccDest = message.getRecipients(RecipientType.BCC);
		if (bccDest != null && bccDest.length > 0) {
			buffer.append(LOG_SEND_BCC + joinAddresses(bccDest));
		}

		LOGGER.info(STLogEnumImpl.SEND_MAIL_TEC, buffer.toString());

		Transport.send(message);
	}

	/**
	 * Renseigne et envoie un message de type MimeMessage
	 * 
	 * @param subject
	 *            : objet du message
	 * @param content
	 *            : contenu du corps du message
	 * @param emailAddress
	 *            : destinataires
	 * @param emailCopy
	 *            : copies (accepte la valeur null)
	 * @throws Exception
	 */
	private void fillAndSendMessage(final String subject, final String content, final List<Address> emailAddress,
			final List<Address> emailCopy, final List<Address> emailCCI) throws Exception {
		fillAndSendMultipartOrStringMessage(subject, content, emailAddress, emailCopy, emailCCI, false);
	}

	/**
	 * Renseigne et envoie un message de type MimeMessage avec un contenu
	 * multipart
	 * 
	 * @param subject
	 *            : objet du message
	 * @param content
	 *            : contenu multipart du message
	 * @param emailAddress
	 *            : destinataires
	 * @param emailCopy
	 *            : copies (accepte la valeur null)
	 * @throws Exception
	 */
	private void fillAndSendMultipartMessage(final String subject, final Multipart content,
			final List<Address> emailAddress, final List<Address> emailCopy, final List<Address> emailCCI)
			throws Exception {
		fillAndSendMultipartOrStringMessage(subject, content, emailAddress, emailCopy, emailCCI, true);
	}

	private Message fillMessage(Session mailSession, final String subject, final Object content,
			final List<Address> emailAddress, final List<Address> emailCopy, final List<Address> emailCCI,
			final boolean isMultipart) throws MessagingException {
		Message message = new MimeMessage(mailSession);
		message.setSubject(subject);
		message.setRecipients(Message.RecipientType.TO, emailAddress.toArray(new Address[emailAddress.size()]));
		if (emailCopy != null) {
			message.setRecipients(Message.RecipientType.CC, emailCopy.toArray(new Address[emailCopy.size()]));
		}
		if (emailCCI != null) {
			message.setRecipients(Message.RecipientType.BCC, emailCCI.toArray(new Address[emailCCI.size()]));
		}
		if (isMultipart) {
			message.setContent((Multipart) content);
		} else {
			message.setContent((String) content, CONTENT_FORMAT);
		}

		setFromAndSentDate(message);
		return message;
	}

	private void fillAndSendMultipartOrStringMessage(final String subject, final Object contentPassed,
			final List<Address> emailAddress, final List<Address> emailCopy, final List<Address> emailCCI,
			final boolean isMultipart) throws Exception {
		new Thread() {
			@Override
			public void run() {
				try {

					int limit = -1;
					// valeur par défaut comme écrit dans la FEV 552, en seconde
					int delay = 30;
					ConfigService configService = STServiceLocator.getConfigService();
					try {
						limit = configService.getIntegerValue(STConfigConstants.SEND_MAIL_LIMIT);
						delay = configService.getIntegerValue(STConfigConstants.SEND_MAIL_DELAY);
					}catch(Exception e) {
						LOGGER.info(STLogEnumImpl.SEND_MAIL_TEC, "Erreur lors de la récupération du paramétrage de l'envoi de masse");
						limit = -1;
					}

					Session mailSession = getMailService().getSession(MAIL_SESSION, null);
					int nbDestinataires = 0;
					int nbCopies = 0;
					int nbCCI = 0;
					if (emailAddress != null)
						nbDestinataires = emailAddress.size();
					if (emailCopy != null)
						nbCopies = emailCopy.size();
					if (emailCCI != null)
						nbCCI = emailCCI.size();

					if (limit == -1 || nbDestinataires + nbCopies + nbCCI <= limit) {
						// cas avant FEV 552
						List<Address> appMail =  emailAddress;
						if(nbDestinataires==0) {
							appMail = new ArrayList<Address>();
							appMail.add(new InternetAddress(configService.getValue(STConfigConstants.MAIL_FROM)));
						}
						send(fillMessage(mailSession, subject, contentPassed, appMail, emailCopy, emailCCI,
								isMultipart));
					} else {
						// à partir d'ici on envoie les mails par lots comme
						// définis dans la configuration.
						// On envoie d'abord des mails par lots de
						// destinairaires directs, puis par destinataires en
						// copie puis enfin par destinataires en copies cachées
						int offset = 0;
						int end = 0;
						while (offset < nbDestinataires) {
							try {
								end = ((offset + limit ) < nbDestinataires ? offset + limit : nbDestinataires);

								Message message = new MimeMessage(mailSession);
								message.setSubject(subject);
								if (end == offset) {
									send(fillMessage(mailSession, subject, contentPassed,
											emailAddress.subList(offset, end), null, null, isMultipart));
									break;
								} else {
									send(fillMessage(mailSession, subject, contentPassed,
											emailAddress.subList(offset, end), null, null, isMultipart));
									TimeUnit.SECONDS.sleep(delay);
								}
							} catch (MessagingException e) {
								LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception(
										LOG_SEND_ERROR + subject + LOG_SEND_TO + StringUtils.join(emailAddress, ", "),
										e));
								continue;
							} finally {
								offset = end;
							}
						}

						// On envoie des mails à l'@ de l'appli et les lots en
						// copie
						if (nbCopies > 0) {
							offset = 0;
							end = 0;
							// Adresse de l'application (no-reply) en tant que
							// destinataire
							ArrayList<Address> appMail = new ArrayList<Address>();
							appMail.add(new InternetAddress(configService.getValue(STConfigConstants.MAIL_FROM)));
							while (offset < nbCopies) {
								try {
									end = ((offset + limit ) < nbCopies ? offset + limit : nbCopies);
									if (end == offset) {
										send(fillMessage(mailSession, subject, contentPassed, appMail,
												emailCopy.subList(offset, end), null, isMultipart));
										break;
									} else {
										send(fillMessage(mailSession, subject, contentPassed, appMail,
												emailCopy.subList(offset, end), null, isMultipart));
										TimeUnit.SECONDS.sleep(delay);
									}
								} catch (MessagingException e) {
									LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC,
											new Exception(LOG_SEND_ERROR + subject + LOG_SEND_TO
													+ StringUtils.join(appMail, ", ") + "et en copie "
													+ StringUtils.join(emailCopy.subList(offset, end), ", "), e));
									continue;
								} finally {
									offset = end;
								}
							}
						}
						// On envoie des mails à l'@ de l'appli et les lots en
						// copie cachée (bcc)
						if (nbCCI > 0) {
							offset = 0;
							end = 0;
							// Adresse de l'application (no-reply) en tant que
							// destinataire
							ArrayList<Address> appMail = new ArrayList<Address>();
							appMail.add(new InternetAddress(configService.getValue(STConfigConstants.MAIL_FROM)));
							while (offset < nbCCI) {
								try {
									end = ((offset + limit ) < nbCCI ? offset + limit : nbCCI );
									if (end == offset) {
										send(fillMessage(mailSession, subject, contentPassed, appMail, null,
												emailCCI.subList(offset, end), isMultipart));
										break;
									} else {
										send(fillMessage(mailSession, subject, contentPassed, appMail, null,
												emailCCI.subList(offset, end), isMultipart));
										TimeUnit.SECONDS.sleep(delay);
									}
								} catch (MessagingException e) {
									LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC,
											new Exception(LOG_SEND_ERROR + subject + LOG_SEND_TO
													+ StringUtils.join(appMail, ", ") + "et en copie cachée"
													+ StringUtils.join(emailCCI.subList(offset, end), ", "), e));
									continue;
								} finally {
									offset = end;
								}
							}
						}
					}
				} catch (Exception e) {
					LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC,e);
					LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception(
							LOG_SEND_ERROR + subject + LOG_SEND_TO + StringUtils.join(emailAddress, ", "), e));
				}
			}
		}.start();

	}

	/**
	 * Initialise une instance Multipart avec les data à envoyer
	 * 
	 * @param content
	 *            : contenu texte du multipart
	 * @param dataMime
	 *            : contenu pj du multipart
	 * @param fileName
	 *            : nom du fichier de la pj
	 * @return le multipart créé
	 * @throws MessagingException
	 */
	private Multipart initMultipart(String content, DataHandler dataMime, String fileName) throws MessagingException {
		Multipart multipart = new MimeMultipart();
		// Create and fill the first message part
		MimeBodyPart mainBodyPart = new MimeBodyPart();
		mainBodyPart.setContent(content, CONTENT_FORMAT);
		multipart.addBodyPart(mainBodyPart);
		if (dataMime != null) {
			MimeBodyPart bodyPart = new MimeBodyPart();
			bodyPart.setDataHandler(dataMime);
			bodyPart.setFileName(fileName);
			multipart.addBodyPart(bodyPart);
		}

		return multipart;
	}

	private void sendMailWithAttachments(List<String> recipientsEmail, List<String> copiesEmail, List<String> bccsEmail,
			String subject, String content, List<ExportBlob> listFiles) {
		try {
			String fileName = "pieces_jointes.zip";
			DataHandler dataMime = null;
			if (!listFiles.isEmpty()) {
				// now write the ZIP content to the output stream
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
				zipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);

				// Attach the files to the message
				for (ExportBlob exportBlob : listFiles) {
					Blob blob = exportBlob.getBlob();
					if (blob != null) {
						writeBlobToZipStream(zipOutputStream, exportBlob.getPath() + "/" + blob.getFilename(), blob);
					}
				}
				zipOutputStream.close();
				byte[] bytes = outputStream.toByteArray();

				// construct the zip body part
				DataSource dataSource = new ByteArrayDataSource(bytes, "application/zip");
				dataMime = new DataHandler(dataSource);
			}
			Multipart multipart = initMultipart(content, dataMime, fileName);
			List<Address> emailAddresses = null;
			if (recipientsEmail != null && recipientsEmail.size() > 0) {
				emailAddresses = new ArrayList<Address>();
				for (String address : recipientsEmail) {
					emailAddresses.add(new InternetAddress(address));
				}
			}

			List<Address> copiesAddresses = null;
			if (copiesEmail != null && copiesEmail.size() > 0) {
				copiesAddresses = new ArrayList<Address>();
				for (String address : copiesEmail) {
					copiesAddresses.add(new InternetAddress(address));
				}
			}

			List<Address> bccAddresses = null;
			if (bccsEmail != null && bccsEmail.size() > 0) {
				bccAddresses = new ArrayList<Address>();
				for (String address : bccsEmail) {
					bccAddresses.add(new InternetAddress(address));
				}
			}

			fillAndSendMultipartMessage(subject, multipart, emailAddresses, copiesAddresses, bccAddresses);

		} catch (MessagingException e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception("[sendMailWithAttachment]" + LOG_SEND_ERROR
					+ subject + LOG_SEND_TO + StringUtils.join(recipientsEmail, ", "), e));
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception("[sendMailWithAttachment]" + LOG_SEND_ERROR
					+ subject + LOG_SEND_TO + StringUtils.join(recipientsEmail, ", "), e));
		}
	}

	@Override
	public void sendMailWithAttachments(List<String> recipientsEmail, String subject, String content,
			List<ExportBlob> listFiles) throws Exception {
		sendMailWithAttachments(recipientsEmail, null, null, subject, content, listFiles);
	}

	@Override
	public void sendMailResetPassword(CoreSession coreSession, String userLogin, String password)
			throws ClientException {
		final UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel doc = userManager.getUserModel(userLogin);

		// Traite uniquement les documents de type User
		String docType = doc.getType();
		if (!"user".equals(docType)) {
			return;
		}
		STUser user = doc.getAdapter(STUser.class);

		String email = user.getEmail();
		String fname = user.getFirstName();
		String lname = user.getLastName();

		STParametreService stParamService = STServiceLocator.getSTParametreService();

		String subject = stParamService.getParametreValue(coreSession, STParametreConstant.OBJET_MAIL_NOUVEAU_MDP);
		String message = stParamService.getParametreValue(coreSession, STParametreConstant.TEXTE_MAIL_NOUVEAU_MDP)
				+ "\n\n- login : %s\n" + "- mot de passe : %s\n\n";

		message = String.format(message, userLogin, password);

		Address[] emailAddress = new Address[1];
		try {
			LOGGER.info(STLogEnumImpl.SEND_MAIL_TEC, LOG_SEND_OBJECT + subject + " à l'adresse suivante : " + email);
			emailAddress[0] = new InternetAddress(email, fname + " " + lname);
			sendMail(message, subject, MAIL_SESSION, emailAddress);
		} catch (Exception e) {
			LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, new Exception(
					"[sendMailResetPassword]" + LOG_SEND_ERROR + subject + " à l'adresse suivante : " + email, e));
			throw new STException("Erreur d'envoi du mail de création du mot de passe", e);
		}

	}

	@Override
	public void sendMailWithAttachmentsAsBCC(List<String> bccs, String formObjetMail, String formTexteMail,
			List<ExportBlob> blobs) {

		sendMailWithAttachments(null, null, bccs, formObjetMail, formTexteMail, blobs);

	}
}
