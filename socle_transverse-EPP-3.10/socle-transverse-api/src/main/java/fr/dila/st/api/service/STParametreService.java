package fr.dila.st.api.service;

import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.parametre.STParametre;

/**
 * Service permettant de gérer les paramètres applicatifs administrables dans l'IHM de l'application.
 * 
 * @author fesposito
 * @author jtremeaux
 */
public interface STParametreService {

	/**
	 * Retourne la valeur du paramètre avec l'identifiant id.
	 * 
	 * @param session
	 *            Session
	 * @param anid
	 *            Identifiant technique du paramètre
	 * @return Valeur du paramètre
	 * @throws ClientException
	 */
	String getParametreValue(CoreSession session, String anid) throws ClientException;

	/**
	 * Retourne le document paramètre avec l'identifieant id .
	 * 
	 * @param session
	 *            Session
	 * @param anid
	 *            Identifiant technique du paramètre
	 * @return Objet métier paramètre
	 * @throws ClientException
	 */
	STParametre getParametre(CoreSession session, String anid) throws ClientException;

	/**
	 * Retourne la racine des paramètres.
	 * 
	 * @param session
	 *            Session
	 * @return Document racine des paramètres
	 * @throws ClientException
	 */
	DocumentModel getParametreFolder(CoreSession session) throws ClientException;

	/**
	 * Renvoie l'url de la page de renseignement.
	 * 
	 * @return
	 * @throws ClientException
	 * @throws LoginException
	 */
	String getUrlRenseignement() throws ClientException, LoginException;
}
