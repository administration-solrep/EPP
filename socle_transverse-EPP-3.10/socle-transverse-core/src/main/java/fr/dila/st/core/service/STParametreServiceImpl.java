package fr.dila.st.core.service;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.cache.ParamRefRelationCache;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.SessionUtil;

/**
 * Implémentation du service permettant de gérer les paramètres applicatifs administrables dans l'IHM de l'application.
 * 
 * @author Fabio Esposito
 */
public class STParametreServiceImpl implements STParametreService {

	private static final String				QUERY_PARAMETRE_FOLDER	= "SELECT * FROM "
																			+ STConstant.PARAMETRE_FOLDER_DOCUMENT_TYPE;

	private static final String				QUERY_PARAMETRE			= "SELECT p.ecm:uuid AS id FROM "
																			+ STConstant.PARAMETRE_DOCUMENT_TYPE
																			+ " AS p WHERE p.ecm:name = ?";

	private ParamRefRelationCache<String>	cache;
	private String							parametreFolderDocId;
	
	private static final STLogger	LOG						= STLogFactory.getLog(STParametreServiceImpl.class);

	public STParametreServiceImpl() {
		cache = new ParamRefRelationCache<String>(QUERY_PARAMETRE);
	}

	/**
	 * charge le document associé au folder contenant les parametres. L'id du document est gardé dans
	 * parametreFolderDocId pour des chargement ulterieur évitant une requete SQL à la base.
	 */
	@Override
	public DocumentModel getParametreFolder(CoreSession session) throws ClientException {
		if (parametreFolderDocId == null) {
			DocumentModelList list = session.query(QUERY_PARAMETRE_FOLDER);
			if (list == null || list.size() <= 0) {
				throw new ClientException("Racine des parametres non trouvée");
			} else if (list.size() > 1) {
				throw new ClientException("Plusieurs racines des parametres trouvées");
			}

			DocumentModel doc = list.get(0);
			parametreFolderDocId = doc.getId();
			return doc;
		} else {
			return session.getDocument(new IdRef(parametreFolderDocId));
		}
	}

	@Override
	public String getParametreValue(CoreSession session, String id) throws ClientException {
		STParametre stParametre = getParametre(session, id);
		if (stParametre != null) {
			return stParametre.getValue();
		}

		return null;
	}

	@Override
	public STParametre getParametre(CoreSession session, String name) throws ClientException {
		DocumentModel doc = cache.retrieveDocument(session, name);
		return doc == null ? null : doc.getAdapter(STParametre.class);
	}
	
	@Override
	public String getUrlRenseignement() throws ClientException, LoginException {

		final StringBuilder url = new StringBuilder();

		LoginContext loginContext = null;
		CoreSession coreSession = null;
		try {
			loginContext = Framework.login();
			coreSession = SessionUtil.getCoreSession();
			STParametre param = getParametre(coreSession, STParametreConstant.PAGE_RENSEIGNEMENTS_ID);
			url.append(param.getValue());

		} catch (LoginException e) {
			LOG.error(STLogEnumImpl.DEFAULT, e);
		} finally {
			SessionUtil.close(coreSession);
			// logout
			if (loginContext != null) {
				loginContext.logout();
			}
		}

		return url.toString();
	}
}
