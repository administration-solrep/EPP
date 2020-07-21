package fr.dila.solonepp.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementDistributionService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.api.service.evenement.MajInterneContext;
import fr.dila.solonepp.api.service.evenement.MajInterneRequest;
import fr.dila.solonepp.api.service.evenement.MajInterneResponse;
import fr.dila.solonepp.core.dao.MessageDao;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;

/**
 * Implémentation du service des messages.
 * 
 * @author jtremeaux
 */
public class MessageServiceImpl implements MessageService {

	/**
	 * Serial version UID
	 */
	private static final long		serialVersionUID						= -5296316627046274067L;

	/**
	 * Logger surcouche socle de log4j
	 */
	private static final STLogger	LOGGER									= STLogFactory
																					.getLog(MessageServiceImpl.class);

	private static final String		FIND_MESSAGE_BY_EVENEMENT_UUID_QUERY	= "SELECT m.ecm:uuid as id FROM "
																					+ SolonEppConstant.MESSAGE_DOC_TYPE
																					+ " AS m WHERE m."
																					+ CaseLinkConstants.CASE_DOCUMENT_ID_FIELD
																					+ " = ?";

	@Override
	public DocumentModel createBareMessage(CoreSession session, DocumentModel evenementDoc, DocumentModel mailboxDoc)
			throws ClientException {
		DocumentModel messageDoc = session.createDocumentModel(mailboxDoc.getPathAsString(), evenementDoc.getTitle(),
				SolonEppConstant.MESSAGE_DOC_TYPE);

		return messageDoc;
	}

	@Override
	public void deleteMessageByEvenementId(CoreSession session, final String evenementId) throws ClientException {

		LOGGER.info(session, EppLogEnumImpl.DEL_MESSAGE_TEC, "communication : " + evenementId);
		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				Object[] params = new Object[] { evenementId };
				DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, FIND_MESSAGE_BY_EVENEMENT_UUID_QUERY,
						params);
				for (DocumentRef messageDocRef : refs) {
					LOGGER.info(session, EppLogEnumImpl.DEL_MESSAGE_TEC, "Suppression du message: " + messageDocRef);
					session.removeDocument(messageDocRef);
				}
			}
		}.runUnrestricted();
	}

	@Override
	public DocumentModel getMessageByEvenementId(CoreSession session, String evenementId) throws ClientException {
		MessageCriteria messageCriteria = new MessageCriteria();
		messageCriteria.setEvenementId(evenementId);
		messageCriteria.setCheckReadPermission(true);

		MessageDao messageDao = new MessageDao(session, messageCriteria);
		List<DocumentModel> list = messageDao.list();
		if (list == null || list.isEmpty()) {
			return null;
		}
		return list.iterator().next();
	}

	@Override
	public List<DocumentModel> getMessagesByEvenementIdWithoutReadPerm(final CoreSession session,
			final String evenementId) throws ClientException {

		final List<DocumentModel> messagesList = new ArrayList<DocumentModel>();
		new UnrestrictedSessionRunner(session) {
			@Override
			public void run() throws ClientException {
				MessageCriteria messageCriteria = new MessageCriteria();
				messageCriteria.setEvenementId(evenementId);
				messageCriteria.setCheckReadPermission(false);

				MessageDao messageDao = new MessageDao(session, messageCriteria);
				List<DocumentModel> list = messageDao.list();
				for (DocumentModel messageDoc : list) {
					messagesList.add(messageDoc);
				}

			}
		}.runUnrestricted();
		return messagesList;
	}

	@Override
	public void followTransitionEnCours(CoreSession session, String evenementId) throws ClientException {

		LOGGER.info(session, EppLogEnumImpl.NOTIFICATION_TRANSITION_COMMUNICTION_TEC);

		DocumentModel messageDoc = getMessageByEvenementId(session, evenementId);
		if (messageDoc == null) {
			throw new ClientException("Message non trouvé");
		}

		// Vérifie le type du message
		Message message = messageDoc.getAdapter(Message.class);
		if (message.isTypeEmetteur()) {
			throw new ClientException("Il est interdit de notifier une transition sur le message de l'émetteur");
		}

		// Passage de l'état "en cours" ou "traité" à "en cours" : sans effet
		if (message.isEtatEnCours() || message.isEtatTraite()) {
			LOGGER.info(session, EppLogEnumImpl.CHANGE_MESSAGE_STATE_PROGRESS_OR_SOLVED_TO_IN_PROGRESS_TEC);
			return;
		}

		// Récupère l'événement
		DocumentModel evenementDoc = session.getDocument(new IdRef(message.getCaseDocumentId()));
		Evenement evenement = evenementDoc.getAdapter(Evenement.class);

		// Transitionne le message à l'état "en cours de traitement"
		messageDoc.followTransition(SolonEppLifecycleConstant.MESSAGE_TO_EN_COURS_TRANSITION);

		// Effectue les traitements métiers sur le messages du destinataire
		if (message.isTypeDestinataire()) {
			if (evenement.isEtatPublie()) {
				// Transitionne l'événement à l'état "en instance"
				evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_TO_INSTANCE_TRANSITION);
			}
		}
	}

	@Override
	public void followTransitionTraite(CoreSession session, String evenementId) throws ClientException {

		LOGGER.info(session, EppLogEnumImpl.NOTIFICATION_TRANSITION_COMMUNICTION_TEC);

		DocumentModel messageDoc = getMessageByEvenementId(session, evenementId);
		if (messageDoc == null) {
			throw new ClientException("Message non trouvé");
		}

		// Vérifie le type du message
		Message message = messageDoc.getAdapter(Message.class);
		// if (message.isTypeEmetteur()) {
		// throw new ClientException("Il est interdit de notifier une transition sur le message de l'émetteur");
		// }

		// Passage de l'état "en cours" ou "traité" à "en cours" : sans effet
		if (message.isEtatTraite()) {
			LOGGER.info(session, EppLogEnumImpl.CHANGE_MESSAGE_STATE_PROGRESS_OR_SOLVED_TO_IN_PROGRESS_TEC);
			return;
		}

		// maj de la date traitement
		message.setDateTraitement(Calendar.getInstance());
		session.saveDocument(messageDoc);

		// Récupère l'événement
		DocumentModel evenementDoc = session.getDocument(new IdRef(message.getCaseDocumentId()));
		Evenement evenement = evenementDoc.getAdapter(Evenement.class);

		// Transitionne le message à l'état "traité"
		messageDoc.followTransition(SolonEppLifecycleConstant.MESSAGE_TO_TRAITE_TRANSITION);

		// Effectue les traitements métiers sur le messages du destinataire
		if (message.isTypeDestinataire()) {
			// Lors du traitement du premier message, transitionne l'événement à l'état "en instance"
			if (evenement.isEtatPublie()) {
				evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_TO_INSTANCE_TRANSITION);
			}

			// Si le type d'événement nécessite un accusé de réception, accuse réception de la version en cours
			final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
			if (evenementTypeService.isDemandeAr(evenement.getTypeEvenement())) {
				String versionId = message.getActiveVersionId();
				DocumentModel versionDoc = session.getDocument(new IdRef(versionId));

				final VersionService versionService = SolonEppServiceLocator.getVersionService();
				versionService.accuserReceptionDestinataire(session, evenementDoc, versionDoc, true);
			}
		}
	}

	@Override
	public void majInterne(CoreSession session, MajInterneContext majVisaInterneContext) throws ClientException {
		MajInterneRequest request = majVisaInterneContext.getMajInterneRequest();

		final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();

		DocumentModel evenementDoc = evenementService.getEvenement(session, request.getIdEvenement());

		if (evenementDoc == null) {
			throw new ClientException("La communication n'a pas été trouvée.");
		}

		Evenement evenement = evenementDoc.getAdapter(Evenement.class);

		final DocumentModel messageDoc = getMessageByEvenementId(session, request.getIdEvenement());
		if (messageDoc != null) {

			MajInterneResponse reponse = majVisaInterneContext.getMajInterneResponse();

			Message message = messageDoc.getAdapter(Message.class);
			message.setVisaInternes(request.getInterne());

			new UnrestrictedSessionRunner(session) {
				@Override
				public void run() throws ClientException {
					session.saveDocument(messageDoc);
				}
			}.runUnrestricted();

			final DossierService dossierService = SolonEppServiceLocator.getDossierService();
			final VersionService versionService = SolonEppServiceLocator.getVersionService();

			DocumentModel versionDoc = versionService.getLastVersion(session, request.getIdEvenement());
			DocumentModel dossierDoc = dossierService.getDossier(session, evenement.getDossier());
			DocumentModel messageDocUpdated = getMessageByEvenementId(session, request.getIdEvenement());

			reponse.setDossierDoc(dossierDoc);
			reponse.setEvenementDoc(evenementDoc);
			reponse.setVersionDoc(versionDoc);
			reponse.setMessageDoc(messageDocUpdated);

			session.save();

			// Synchronise les messages avec la version en attente de validation
			final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator
					.getEvenementDistributionService();
			evenementDistributionService.updateMessageMajVisaInterne(session, dossierDoc, evenementDoc, versionDoc,
					messageDocUpdated);

		}
	}
}
