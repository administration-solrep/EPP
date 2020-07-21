package fr.dila.st.core.service;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.util.SessionUtil;

/**
 * Service de gestion de l'état de l'application (restriction d'accès)
 * 
 * @author Fabio Esposito
 * 
 */
public class EtatApplicationServiceImpl implements EtatApplicationService {

	private static final String	GET_ETAT_APP_DOC_QUERY	= "select * from EtatApplication where "
																+ STSchemaConstant.ECM_ISPROXY_XPATH + " = 0";

	private String				idEtatApplication		= null;

	/**
	 * Default constructor
	 */
	public EtatApplicationServiceImpl() {
		// do nothing
	}

	@Override
	public EtatApplication getEtatApplicationDocument(CoreSession session) throws ClientException {

		DocumentModel etatApplication;

		if (idEtatApplication == null) {
			DocumentModelList etatApplicationList = session.query(GET_ETAT_APP_DOC_QUERY);
			if (etatApplicationList == null || etatApplicationList.size() != 1) {
				throw new ClientException(
						"Le document état application n'existe pas ou est présent en plus d'un exemplaire");
			}
			idEtatApplication = etatApplicationList.get(0).getId();
			etatApplication = etatApplicationList.get(0);
		} else {
			etatApplication = session.getDocument(new IdRef(idEtatApplication));
		}
		return etatApplication.getAdapter(EtatApplication.class);
	}

	@Override
	public void restrictAccess(CoreSession session, String description) throws ClientException {

		EtatApplication etatApplication = getEtatApplicationDocument(session);

		etatApplication.setRestrictionAcces(true);
		etatApplication.setDescriptionRestriction(description);

		session.saveDocument(etatApplication.getDocument());
	}

	@Override
	public void restoreAccess(CoreSession session) throws ClientException {
		EtatApplication etatApplication = getEtatApplicationDocument(session);

		etatApplication.setRestrictionAcces(false);
		etatApplication.setDescriptionRestriction("");

		session.saveDocument(etatApplication.getDocument());
	}

	/**
	 * Retourne RestrictionAcces
	 * 
	 * Attention à utiliser que pour la page de login car l'utilisateur n'est pas encore connecté
	 * 
	 * @return
	 * @throws ClientException
	 * @throws LoginException
	 */
	@Override
	public Map<String, Object> getRestrictionAccesUnrestricted() throws ClientException, LoginException {

		CoreSession session = null;
		LoginContext loginContext = null;
		try {
			loginContext = Framework.login();
			session = SessionUtil.getCoreSession();
			TransactionHelper.startTransaction();
			final EtatApplication etat = getEtatApplicationDocument(session);

			final Map<String, Object> result = new HashMap<String, Object>();
			result.put(RESTRICTION_ACCESS, etat.getRestrictionAcces());
			final String description = etat.getDescriptionRestriction();
			if (description == null) {
				result.put(RESTRICTION_DESCRIPTION, null);
			} else {
				result.put(RESTRICTION_DESCRIPTION, description.replaceAll("<script", ""));
			}

			final String message = etat.getMessage();
			if (message == null) {
				result.put(MESSAGE, null);
			} else {
				result.put(MESSAGE, message.replaceAll("<script", ""));
			}			
			result.put(AFFICHAGE_RADIO, etat.getAffichage());
			return result;
		} finally {
			TransactionHelper.commitOrRollbackTransaction();
			SessionUtil.close(session);
			// logout
			if (loginContext != null) {
				loginContext.logout();
			}
		}
	}

}
