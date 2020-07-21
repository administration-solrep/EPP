package fr.dila.st.core.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.domain.user.Delegation;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.DelegationService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.UnrestrictedQueryRunner;

/**
 * Implémentation du service permettant de déléguer ses droits à d'autres utilisateurs.
 * 
 * @author jtremeaux
 */
public class DelegationServiceImpl implements DelegationService {

	/**
	 * Logger.
	 */
	private static final STLogger	LOG						= STLogFactory.getLog(DelegationServiceImpl.class);

	private static final String		QUERY_DELEGATION_ACTIVE	= new StringBuilder("SELECT d.")
																	.append(STSchemaConstant.ECM_UUID_XPATH)
																	.append(" AS id FROM ")
																	.append(STConstant.DELEGATION_DOCUMENT_TYPE)
																	.append(" AS d WHERE d.")
																	.append(STSchemaConstant.DELEGATION_SCHEMA_PREFIX)
																	.append(":")
																	.append(STSchemaConstant.DELEGATION_DESTINATAIRE_ID_PROPERTY_NAME)
																	.append(" = ? AND d.")
																	.append(STSchemaConstant.DELEGATION_SCHEMA_PREFIX)
																	.append(":")
																	.append(STSchemaConstant.DELEGATION_DATE_DEBUT_PROPERTY_NAME)
																	.append(" <= ? AND d.")
																	.append(STSchemaConstant.DELEGATION_SCHEMA_PREFIX)
																	.append(":")
																	.append(STSchemaConstant.DELEGATION_DATE_FIN_PROPERTY_NAME)
																	.append(" > ? ").toString();

	/**
	 * Default constructor
	 */
	public DelegationServiceImpl() {
		// do noting
	}

	@Override
	public DocumentModel getDelegationRoot(final CoreSession session) throws ClientException {
		final UserWorkspaceService userWorkspaceService = STServiceLocator.getUserWorkspaceService();
		final DocumentModel userWorkspaceDoc = userWorkspaceService.getCurrentUserPersonalWorkspace(session, null);

		final StringBuilder sb = new StringBuilder("SELECT * FROM ");
		sb.append(STConstant.DELEGATION_ROOT_DOCUMENT_TYPE);
		sb.append(" WHERE ");
		sb.append(STSchemaConstant.ECM_SCHEMA_PREFIX);
		sb.append(":");
		sb.append(STSchemaConstant.ECM_PARENT_ID_PROPERTY);
		sb.append(" = '");
		sb.append(userWorkspaceDoc.getId());
		sb.append("' and ecm:isProxy = 0 ");
		DocumentModelList list = session.query(sb.toString());
		if (list == null || list.isEmpty()) {
			// essai de récupération unrestricted
			final DocumentModelList listUnrestricted = new UnrestrictedQueryRunner(session, sb.toString()).findAll();
			if (listUnrestricted == null || listUnrestricted.isEmpty()) {
				throw new ClientException("Racine des délégations non trouvée");
			} else if (listUnrestricted.size() > 1) {
				throw new ClientException("Plusieurs racines des délégations trouvées");
			}
			// Le document existe bien mais les ACL ne sont pas bons
			// on re-set les ACL correspondants avant de retester la récupération
			final ACP acp = listUnrestricted.get(0).getACP();
			listUnrestricted.get(0).setACP(acp, true);
			list = session.query(sb.toString());
			if (list == null || list.isEmpty()) {
				throw new ClientException("Racine des délégations non trouvée : ACL incorrects");
			}
		} else if (list.size() > 1) {
			throw new ClientException("Plusieurs racines des délégations trouvées");
		}

		return list.get(0);
	}

	@Override
	public List<DocumentModel> findActiveDelegationForUser(final CoreSession session, final String userId)
			throws ClientException {
		final Calendar now = Calendar.getInstance();
		final Object[] params = new Object[] { userId, now, now };

		return QueryUtils.doUnrestrictedUFNXQLQueryAndFetchForDocuments(session, STConstant.DELEGATION_DOCUMENT_TYPE,
				QUERY_DELEGATION_ACTIVE, params);
	}

	@Override
	public void sendDelegationEmail(final CoreSession session, final DocumentModel delegationDoc)
			throws ClientException {
		final Delegation delegation = delegationDoc.getAdapter(Delegation.class);

		LOG.debug(STLogEnumImpl.SEND_MAIL_TEC,
				"Envoi de l'email de délégation, utilisateur source = " + delegation.getSourceId());

		// Détermine l'email au destinataire de la délégation
		final UserManager userManager = STServiceLocator.getUserManager();
		final DocumentModel destUserModel = userManager.getUserModel(delegation.getDestinataireId());
		final STUser destUser = destUserModel.getAdapter(STUser.class);
		if (StringUtils.isEmpty(destUser.getEmail())) {

			LOG.debug(STLogEnumImpl.SEND_MAIL_TEC, "Le destinataire de la délégation n'a pas d'email, pas d'envoi");

			return;
		}

		// Détermine l'origine de la délégation
		final DocumentModel sourceUserModel = userManager.getUserModel(delegation.getSourceId());
		final STUser sourceUser = sourceUserModel.getAdapter(STUser.class);
		final List<String> posteIdList = sourceUser.getPostes();
		final List<String> posteLabelList = new ArrayList<String>();

		final List<PosteNode> listNode = STServiceLocator.getSTPostesService().getPostesNodes(posteIdList);

		for (final OrganigrammeNode poste : listNode) {
			posteLabelList.add(poste.getLabel());
		}

		// Détermine l'objet du mail
		final STParametreService parametreService = STServiceLocator.getSTParametreService();
		final String mailObjet = parametreService.getParametreValue(session,
				STParametreConstant.NOTIFICATION_MAIL_DELEGATION_OBJET);

		// Détermine le corps du mail
		final STUserService userService = STServiceLocator.getSTUserService();
		final String mailTexte = parametreService.getParametreValue(session,
				STParametreConstant.NOTIFICATION_MAIL_DELEGATION_TEXTE);
		final Map<String, Object> mailTexteMap = new HashMap<String, Object>();
		mailTexteMap.put("origine", userService.getUserFullName(sourceUserModel));
		mailTexteMap.put("poste", StringUtils.join(posteLabelList, ","));
		mailTexteMap.put("profil", StringUtils.join(delegation.getProfilListId(), ","));
		mailTexteMap.put("date_debut", DateUtil.formatDDMMYYYY(delegation.getDateDebut()));
		mailTexteMap.put("date_fin", DateUtil.formatDDMMYYYY(delegation.getDateFin()));

		// Envoie l'email au destinataire de la délégation
		final STMailService mailService = STServiceLocator.getSTMailService();
		mailService.sendTemplateMail(destUser.getEmail(), mailObjet, mailTexte, mailTexteMap);
	}
}
