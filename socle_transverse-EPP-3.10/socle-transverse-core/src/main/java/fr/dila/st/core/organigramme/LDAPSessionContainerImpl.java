package fr.dila.st.core.organigramme;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.directory.ldap.LDAPDirectory;
import org.nuxeo.ecm.directory.ldap.LDAPSession;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.organigramme.LDAPSessionContainer;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.util.LdapSessionUtil;

/**
 * Conteneur de session LDAP afin de reutiliser les mêmes pour eviter les bind/unbind a repétition
 * 
 * 
 * @author asatre
 * 
 */
public class LDAPSessionContainerImpl implements LDAPSessionContainer {

	private Map<String, LDAPSession>	mapLDAPSession;

	public LDAPSessionContainerImpl() {
		mapLDAPSession = new HashMap<String, LDAPSession>();
	}

	@Override
	public LDAPSession getSessionGouvernement() throws ClientException {
		return getSession(STConstant.ORGANIGRAMME_GOUVERNEMENT_DIR);
	}

	@Override
	public LDAPSession getSessionEntite() throws ClientException {
		return getSession(STConstant.ORGANIGRAMME_ENTITE_DIR);
	}

	@Override
	public LDAPSession getSessionUniteStructurelle() throws ClientException {
		return getSession(STConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_DIR);
	}

	@Override
	public LDAPSession getSessionPoste() throws ClientException {
		return getSession(STConstant.ORGANIGRAMME_POSTE_DIR);
	}

	@Override
	public LDAPSession getSessionUser() throws ClientException {
		return getSession(STConstant.ORGANIGRAMME_USER_DIR);
	}

	@Override
	public LDAPSession getSessionFromType(OrganigrammeType type) throws ClientException {
		if (type.equals(OrganigrammeType.GOUVERNEMENT)) {
			return getSessionGouvernement();
		} else if (type.equals(OrganigrammeType.DIRECTION) || type.equals(OrganigrammeType.UNITE_STRUCTURELLE)) {
			return getSessionUniteStructurelle();
		} else if (type.equals(OrganigrammeType.POSTE)) {
			return getSessionPoste();
		} else if (type.equals(OrganigrammeType.MINISTERE)) {
			return getSessionEntite();
		} else {
			return null;
		}
	}

	@Override
	public void closeAll() throws ClientException {
		Set<String> keySet = new HashSet<String>(mapLDAPSession.keySet());
		for (String key : keySet) {
			LdapSessionUtil.close(mapLDAPSession.remove(key));
		}
	}

	@Override
	public void invalidateAllCaches() throws ClientException {
		for (String key : mapLDAPSession.keySet()) {
			LDAPDirectory ldapDir = (LDAPDirectory) mapLDAPSession.get(key).getDirectory();
			ldapDir.invalidateCaches();
		}
	}

	@Override
	public LDAPSession getSession(String directory) throws ClientException {
		if (mapLDAPSession.get(directory) == null) {
			mapLDAPSession.put(directory, LdapSessionUtil.getLdapSession(directory));
		}
		return mapLDAPSession.get(directory);
	}
}
