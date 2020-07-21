package fr.dila.solonepp.core.service;

import java.util.Collections;
import java.util.List;

import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.UnrestrictedCreateOrSaveDocumentRunner;

/**
 * Implémentation du service permettant de gérer les Mailbox des institutions.
 * 
 * @author jtremeaux
 */
public class MailboxInstitutionServiceImpl extends DefaultComponent implements MailboxInstitutionService {
	/**
	 * Serial ID.
	 */
	private static final long		serialVersionUID	= 1L;

	/**
	 * Logger surcouche socle de log4j
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(MailboxInstitutionServiceImpl.class);

	@Override
	public String getInstitutionMailboxId(String institutionId) {
		return IdUtils.generateId(SolonEppConstant.INSTITUTION_PREFIX + institutionId, "-", true, 50);
	}

	@Override
	public String getInstitutionIdFromMailboxId(String mailboxId) {
		return mailboxId.substring(SolonEppConstant.INSTITUTION_PREFIX.length()).toUpperCase().replaceAll("-", "_");
	}

	@Override
	public DocumentModel getMailboxInstitution(CoreSession session, String institutionId) throws ClientException {
		LOGGER.debug(session, STLogEnumImpl.GET_INSTITUTION_MAIL_BOX, "institution=<" + institutionId + ">");

		String mailboxId = getInstitutionMailboxId(institutionId);
		final MailboxService mailboxService = STServiceLocator.getMailboxService();
		Mailbox mailbox = mailboxService.getMailbox(session, mailboxId);
		if (mailbox == null) {
			LOGGER.debug(session, STLogEnumImpl.FAIL_GET_INSTITUTION_MAIL_BOX, "Pas de mailbox avec l'id=<" + mailboxId
					+ ">");
			return null;
		}

		return mailbox.getDocument();
	}

	@Override
	public DocumentModel getMailboxInstitutionUnrestricted(CoreSession session, String institutionId)
			throws ClientException {

		LOGGER.debug(session, STLogEnumImpl.GET_INSTITUTION_MAIL_BOX, "institution=<" + institutionId + ">");

		String mailboxId = getInstitutionMailboxId(institutionId);
		final MailboxService mailboxService = STServiceLocator.getMailboxService();
		Mailbox mailbox = mailboxService.getMailboxUnrestricted(session, mailboxId);
		if (mailbox == null) {
			LOGGER.debug(session, STLogEnumImpl.FAIL_GET_INSTITUTION_MAIL_BOX, "Pas de mailbox avec l'id=<" + mailboxId
					+ ">");
			return null;
		}

		return mailbox.getDocument();
	}

	/**
	 * Crée si nécessaire la Mailbox de l'institution.
	 * 
	 * @param session
	 *            session
	 * @param mailboxRoot
	 *            Racine des mailbox
	 * @param institutionId
	 *            Identifiant technique de l'institution
	 * @throws ClientException
	 */
	protected void createMailboxInstitution(CoreSession session, DocumentModel mailboxRoot, String institutionId)
			throws ClientException {
		// Si la mailbox existe déjà, on ne fait rien
		DocumentModel mailboxDoc = getMailboxInstitutionUnrestricted(session, institutionId);
		if (mailboxDoc != null) {
			return;
		}

		// Crée la Mailbox
		OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
		OrganigrammeNode institutionNode = organigrammeService.getInstitution(institutionId);
		if (institutionNode != null) {
			createMailboxInstitution(session, mailboxRoot, institutionId, institutionNode.getLabel());
		}
	}

	/**
	 * Crée si elle n'existe pas la Mailbox associée à un poste.
	 * 
	 * @param session
	 *            Session
	 * @param mailboxRoot
	 *            Racine des mailbox
	 * @param posteName
	 *            Nom de l'institution
	 * @param institutionLabel
	 *            Label de l'institution
	 * @return Mailbox Mailbox nouvellement créée
	 * @throws ClientException
	 *             ClientException
	 */
	protected Mailbox createMailboxInstitution(CoreSession session, DocumentModel mailboxRoot, String institutionId,
			String institutionLabel) throws ClientException {
		final MailboxService mailboxService = STServiceLocator.getMailboxService();
		String mailboxId = getUniqueMailboxId(session, institutionId);

		// Crée la Mailbox poste
		DocumentModel mailboxModel = session.createDocumentModel(mailboxService.getMailboxType());
		final Mailbox mailbox = mailboxModel.getAdapter(Mailbox.class);

		// Détermine le nom de l'utilisateur qui possède les Mailbox poste
		String mailboxInstitutionOwner = getMailboxInstitutionOwner();

		// Renseigne les propriétés de la Mailbox
		mailbox.setId(mailboxId);
		mailbox.setTitle(institutionLabel);
		mailbox.setOwner(mailboxInstitutionOwner);
		mailbox.setType(MailboxConstants.type.generic.name());

		// Ajoute le groupe de l'institution. Cela permet d'accéder à la mailbox via les ACL.
		String goup = SolonEppConstant.INSTITUTION_PREFIX + institutionId;
		List<String> groups = Collections.singletonList(goup);
		mailbox.setGroups(groups);

		mailboxModel.setPathInfo(mailboxRoot.getPathAsString(), IdUtils.generateId(mailbox.getTitle(), "-", true, 24));

		// Crée la Mailbox
		mailboxModel = new UnrestrictedCreateOrSaveDocumentRunner(session).createDocument(mailboxModel);

		return mailboxModel.getAdapter(Mailbox.class);
	}

	/**
	 * Renvoie un Id de mailbox unique.
	 * 
	 * @param session
	 *            session
	 * @param posteId
	 *            id du poste
	 * @return id de la mailbox
	 * @throws ClientException
	 */
	protected String getUniqueMailboxId(CoreSession session, String posteId) throws ClientException {
		final MailboxService mailboxService = STServiceLocator.getMailboxService();
		String mailboxId = getInstitutionMailboxId(posteId);

		String uniqueMailboxId = mailboxId;

		int count = 0;
		Mailbox mailbox = null;
		do {
			mailbox = mailboxService.getMailboxUnrestricted(session, uniqueMailboxId);
			if (mailbox != null) {
				if (mailboxId.length() == STConstant.MAILBOX_POSTE_ID_MAX_LENGTH) {
					mailboxId = mailboxId.substring(0, mailboxId.length() - 2);
				}
				if (count != 0 && count % 10 == 0) {
					mailboxId = mailboxId.substring(0, mailboxId.length() - 1);
				}
				uniqueMailboxId = mailboxId + "_" + count;
				count++;
			}
		} while (mailbox != null);

		return uniqueMailboxId;
	}

	/**
	 * Retourne le propriétaire des mailbox poste.
	 * 
	 * @return Propriétaire des mailbox poste
	 */
	protected String getMailboxInstitutionOwner() {
		return STConstant.NUXEO_SYSTEM_USERNAME;
	}

	@Override
	public void createAllMailboxInstitution(CoreSession session, DocumentModel mailboxRoot) throws ClientException {
		for (InstitutionsEnum institution : SolonEppSchemaConstant.INSTITUTIONS_VALUES) {
			createMailboxInstitution(session, mailboxRoot, institution.name());
		}
	}
}
