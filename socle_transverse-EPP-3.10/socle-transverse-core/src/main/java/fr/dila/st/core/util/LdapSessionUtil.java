package fr.dila.st.core.util;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.ldap.LDAPDirectory;
import org.nuxeo.ecm.directory.ldap.LDAPDirectoryFactory;
import org.nuxeo.ecm.directory.ldap.LDAPDirectoryProxy;
import org.nuxeo.ecm.directory.ldap.LDAPSession;
import org.nuxeo.ecm.platform.uidgen.UIDSequencer;
import org.nuxeo.ecm.platform.uidgen.service.ServiceHelper;
import org.nuxeo.ecm.platform.uidgen.service.UIDGeneratorService;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.organigramme.OrganigrammeType;

/**
 * Classe utilitaire sur les sessions LDAP.
 * 
 * @author jtremeaux
 */
public final class LdapSessionUtil {

	private static final int	ID_LDAP_DEFAULT	= 60000000;

	/**
	 * Utility class
	 */
	private LdapSessionUtil() {
		// do nothing
	}

	/**
	 * Retourne le LDAPDirectory.
	 * 
	 * @param directoryName
	 *            Nom du directory
	 * @return le LDAPDirectory
	 * @throws DirectoryException
	 */
	protected static LDAPDirectory getLdapDirectory(String directoryName) throws DirectoryException {
		LDAPDirectoryFactory factory = (LDAPDirectoryFactory) Framework.getRuntime().getComponent(
				LDAPDirectoryFactory.NAME);
		return ((LDAPDirectoryProxy) factory.getDirectory(directoryName)).getDirectory();
	}

	/**
	 * Ouvre une nouvelle session sur le repository et retourne la session. /!\ Attention /!\ La session doit
	 * impérativement être fermée dans un bloc finally !
	 * 
	 * @param directoryName
	 *            Nom du répertoire à ouvrir
	 * @return Nouvelle session
	 * @throws ClientException
	 */
	public static LDAPSession getLdapSession(String directoryName) throws ClientException {
		LDAPDirectory ldapDir = getLdapDirectory(directoryName);
		LDAPSession session = (LDAPSession) ldapDir.getSession();

		return session;
	}

	/**
	 * Ferme la session ouverte avec la méthode getLdapSession ci-dessus.
	 * 
	 * @param session
	 *            Session
	 * @throws ClientException
	 */
	public static void close(Session session) throws ClientException {
		if (session != null) {
			session.close();
		}
	}

	/**
	 * Retourne un id issu d'une séquence pour l'organigramme.
	 * 
	 * @return
	 * @throws DirectoryException
	 */
	public static String getNextId(LDAPSession session) throws DirectoryException {
		// récupération d'un id unique
		final UIDGeneratorService uidGeneratorService = ServiceHelper.getUIDGeneratorService();
		UIDSequencer sequencer = uidGeneratorService.getSequencer();
		String entiteId = null;
		DocumentModel result = null;
		do {
			entiteId = String.valueOf(ID_LDAP_DEFAULT + sequencer.getNext("ORGANIGRAMME_SEQUENCER"));
			result = session.getEntry(entiteId);
		} while (result != null);
		return entiteId;
	}

	/**
	 * Ouvre une nouvelle session sur le repository et retourne la session. /!\ Attention /!\ La session doit
	 * impérativement être fermée dans un bloc finally !
	 * 
	 */
	public static LDAPSession getSessionFromType(OrganigrammeType type) throws ClientException {
		if (OrganigrammeType.GOUVERNEMENT.equals(type)) {
			return getLdapSession(STConstant.ORGANIGRAMME_GOUVERNEMENT_DIR);
		} else if (OrganigrammeType.DIRECTION.equals(type) || OrganigrammeType.UNITE_STRUCTURELLE.equals(type)) {
			return getLdapSession(STConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_DIR);
		} else if (OrganigrammeType.POSTE.equals(type)) {
			return getLdapSession(STConstant.ORGANIGRAMME_POSTE_DIR);
		} else if (OrganigrammeType.MINISTERE.equals(type)) {
			return getLdapSession(STConstant.ORGANIGRAMME_ENTITE_DIR);
		}

		return null;
	}

	public static String getElementNameFromDn(String distinguishedName) {
		String groupName = null;
		if (distinguishedName != null) {
			groupName = distinguishedName.substring(distinguishedName.indexOf('=') + 1, distinguishedName.indexOf(','));
		}
		return groupName;
	}

	public static OrganigrammeType getElementTypeFromDn(String distinguishedName) {
		OrganigrammeType oType = null;
		if (distinguishedName != null) {
			if (distinguishedName.contains("ou=poste")) {
				oType = OrganigrammeType.POSTE;
			} else if (distinguishedName.contains("ou=uniteStructurelle")) {
				oType = OrganigrammeType.UNITE_STRUCTURELLE;
			} else if (distinguishedName.contains("ou=entite")) {
				oType = OrganigrammeType.MINISTERE;
			} else if (distinguishedName.contains("ou=gouvernement")) {
				oType = OrganigrammeType.GOUVERNEMENT;
			}
		}
		return oType;
	}
}
