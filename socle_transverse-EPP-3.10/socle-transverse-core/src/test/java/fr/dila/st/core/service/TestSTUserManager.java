package fr.dila.st.core.service;

import java.util.HashMap;
import java.util.List;

import javax.naming.directory.DirContext;

import org.junit.Test;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.ldap.LDAPSession;
import org.nuxeo.ecm.platform.usermanager.UserManager.MatchType;
import org.nuxeo.ecm.platform.usermanager.UserManagerDescriptor;

import fr.dila.st.core.STRepositoryTestCase;

public class TestSTUserManager extends STRepositoryTestCase {
	Boolean						ldapInitialized;
	STUserManagerImpl			userManager;
	UserManagerDescriptor		userManagerDescriptor;

	private static final String	USER_DIRECTORY	= "userLdapDirectory";

	private static final String	GROUP_DIRECTORY	= "groupLdapDirectory";

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	public void initialize() {
		initializeLDAP();
		userManager = new STUserManagerImpl();
		userManagerDescriptor = new UserManagerDescriptor();
		userManagerDescriptor.userSearchFields = new HashMap<String, MatchType>();
		userManagerDescriptor.userSearchFields.put("username", MatchType.SUBSTRING);
		userManagerDescriptor.userSearchFields.put("firstName", MatchType.SUBSTRING);
		userManagerDescriptor.userSearchFields.put("lastName", MatchType.SUBSTRING);
		userManagerDescriptor.userSearchFields.put("title", MatchType.SUBSTRING);
		userManagerDescriptor.userDirectoryName = "userLdapDirectory";
		userManagerDescriptor.groupDirectoryName = "groupLdapDirectory";
		userManager.setConfiguration(userManagerDescriptor);

	}

	public void initializeLDAP() {
		try {
			deployRepositoryContrib();
			ldapInitialized = true;
		} catch (Exception e) {
			ldapInitialized = false;
		}
	}

	@Override
	protected void deployRepositoryContrib() throws Exception {
		super.deployRepositoryContrib();

		deployContrib("fr.dila.st.core.test", "ldap/TypeService.xml");
		deployContrib("fr.dila.st.core.test", "ldap/DirectoryService.xml");
		deployContrib("fr.dila.st.core.test", "ldap/DirectoryTypes.xml");
		deployContrib("fr.dila.st.core.test", "ldap/LDAPDirectoryFactory.xml");
		deployContrib("fr.dila.st.core.test", "ldap/default-ldap-users-directory-bundle.xml");
		deployContrib("fr.dila.st.core.test", "ldap/default-ldap-groups-directory-bundle.xml");
		deployContrib("fr.dila.st.core.test", "ldap/test-event-contrib.xml");

		super.deployLDAPServer();
		getLDAPDirectory(USER_DIRECTORY).setTestServer(SERVER);
		getLDAPDirectory(GROUP_DIRECTORY).setTestServer(SERVER);

		LDAPSession session = (LDAPSession) getLDAPDirectory(USER_DIRECTORY).getSession();
		try {
			DirContext ctx = session.getContext();

			loadDataFromLdif("test-ldap.ldif", ctx);
		} finally {
			session.close();
		}
	}

	/*
	 * Test qui a pour objectif de vérifier qu'on trouve bien des utilisateurs lorsqu'on lance une recherche avec un
	 * critère vide complet de l'utilisateur
	 */
	@Test
	public void testGetUserSuggestionsVide() throws ClientException {
		initialize();
		if (ldapInitialized) {
			String inputTest = "";
			List<DocumentModel> docModelsRetournes = userManager.searchUsers(inputTest);
			assertTrue("Aucun utilisateur n'a été trouvé par la recherche", docModelsRetournes.size() > 0);
		} else {
			fail("Le ldap n'a pas pu être initialisé");
		}
	}

}
