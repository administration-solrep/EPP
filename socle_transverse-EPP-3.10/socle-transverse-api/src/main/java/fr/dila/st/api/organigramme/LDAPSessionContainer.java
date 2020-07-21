package fr.dila.st.api.organigramme;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.directory.ldap.LDAPSession;

/**
 * Conteneur de session LDAP afin de reutiliser les mêmes pour eviter les bind/unbind a repétition
 * 
 * 
 * @author asatre
 * 
 */
public interface LDAPSessionContainer {

	LDAPSession getSessionGouvernement() throws ClientException;

	LDAPSession getSessionEntite() throws ClientException;

	LDAPSession getSessionUniteStructurelle() throws ClientException;

	LDAPSession getSessionPoste() throws ClientException;

	LDAPSession getSessionUser() throws ClientException;

	LDAPSession getSessionFromType(OrganigrammeType type) throws ClientException;

	void closeAll() throws ClientException;

	void invalidateAllCaches() throws ClientException;

	LDAPSession getSession(String directory) throws ClientException;
}
