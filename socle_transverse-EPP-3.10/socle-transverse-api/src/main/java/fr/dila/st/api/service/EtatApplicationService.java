package fr.dila.st.api.service;

import java.util.Map;

import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.administration.EtatApplication;

/**
 * Service de gestion de l'état de l'application (restriction d'accès)
 * 
 * @author Fabio Esposito
 * 
 */
public interface EtatApplicationService {

	public String	RESTRICTION_ACCESS		= "RESTRICTION_ACCESS";
	public String	RESTRICTION_DESCRIPTION	= "RESTRICTION_DESCRIPTION";
	public String	AFFICHAGE_RADIO			= "AFFICHAGE_RADIO";
	public String	MESSAGE					= "MESSAGE";

	/**
	 * Restreint l'accès à l'application aux administrateurs fonctionnels
	 * 
	 * @param session
	 * @param description
	 * @throws ClientException
	 */
	void restrictAccess(CoreSession session, String description) throws ClientException;

	/**
	 * Restore l'accès normal à l'application
	 * 
	 * @param session
	 * @throws ClientException
	 */
	void restoreAccess(CoreSession session) throws ClientException;

	/**
	 * Retourne le document EtatApplication
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	EtatApplication getEtatApplicationDocument(CoreSession session) throws ClientException;

	/**
	 * Retourne RestrictionAcces
	 * 
	 * Attention à utiliser que pour la page de login car l'utilisateur n'est pas encore connecté
	 * 
	 * @return
	 * @throws ClientException
	 * @throws LoginException
	 */
	Map<String, Object> getRestrictionAccesUnrestricted() throws ClientException, LoginException;
}
