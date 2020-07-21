package fr.dila.st.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.DirContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.store.LdifLoadFilter;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.storage.sql.DatabaseH2;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.ldap.LDAPDirectory;
import org.nuxeo.ecm.directory.ldap.LDAPDirectoryFactory;
import org.nuxeo.ecm.directory.ldap.LDAPDirectoryProxy;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.test.CaseManagementTestConstants;
import fr.dila.st.core.constant.STTestConstant;
import fr.dila.st.core.ldap.MockLdapServer;
import fr.dila.st.core.service.STServiceLocator;

public abstract class STRepositoryTestCase extends SQLRepositoryTestCase {

	private static final Log		LOG								= LogFactory.getLog(STRepositoryTestCase.class);

	private int						nbOpenedSession					= 0;

	// *************************************************************
	// Mock LDAP
	// *************************************************************
	protected static MockLdapServer	SERVER;

	protected static final String	UNITE_STRUCTURELLE_DIRECTORY	= "uniteStructurelleDirectory";

	protected static final String	POSTE_DIRECTORY					= "posteDirectory";

	protected static final String	USER_DIRECTORY					= "userDirectory";

	protected static final String	GROUP_DIRECTORY					= "groupDirectory";

	public STRepositoryTestCase() {
		super();
	}

	@Override
	protected void deployRepositoryContrib() throws Exception {

		// contrib that contains missing facet declaration
		// avoid warn about missing facet that should be declared by nuxeo
		deployContrib(STTestConstant.ST_TEST_BUNDLE, "OSGI-INF/test-core-type-contrib.xml");

		super.deployRepositoryContrib();

		// deploy repository manager
		deployBundle("org.nuxeo.ecm.core.api");

		// deploy search
		deployBundle("org.nuxeo.ecm.platform.search.api");
		deployContrib("org.nuxeo.ecm.core.persistence", "OSGI-INF/persistence-service.xml");

		// deploy api and core bundles
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_API_BUNDLE);
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_CORE_BUNDLE);
		deployBundle("fr.dila.ecm.platform.routing.core");
		deployBundle("org.nuxeo.ecm.automation.core");
		deployBundle("org.nuxeo.ecm.core.schema");
		deployBundle("org.nuxeo.ecm.directory");
		deployBundle("org.nuxeo.ecm.platform.usermanager");
		deployBundle("org.nuxeo.ecm.directory.types.contrib");
		deployBundle("org.nuxeo.ecm.directory.sql");
		deployBundle("org.nuxeo.ecm.webapp.core");

		deployBundle("org.nuxeo.ecm.platform.content.template");
		deployBundle("org.nuxeo.ecm.platform.types.api");
		deployBundle("org.nuxeo.ecm.platform.types.core");
		deployBundle("org.nuxeo.ecm.platform.smart.query.jsf");

		deployBundle("fr.dila.st.api");
		deployBundle("fr.dila.st.core");

		deployContrib(STTestConstant.ST_TEST_BUNDLE, "OSGI-INF/test-st-adapter-contrib.xml");
	}

	protected static void deployLDAPServer() {
		SERVER = new MockLdapServer();
	}

	public static LDAPDirectory getLDAPDirectory(String name) throws DirectoryException {
		LDAPDirectoryFactory factory = (LDAPDirectoryFactory) Framework.getRuntime().getComponent(
				LDAPDirectoryFactory.NAME);
		return ((LDAPDirectoryProxy) factory.getDirectory(name)).getDirectory();
	}

	protected static void loadDataFromLdif(String ldif, DirContext ctx) {
		List<LdifLoadFilter> filters = new ArrayList<LdifLoadFilter>();
		LdifFileLoader loader = new LdifFileLoader(ctx, new File(ldif), filters, Thread.currentThread()
				.getContextClassLoader());
		loader.execute();
	}

	@Override
	public void openSession() throws ClientException {
		if (session == null) {
			super.openSession();
		} else {
			LOG.warn("SESSION ALREADY OPENED");
		}
	}

	@Override
	public CoreSession openSessionAs(String username) throws ClientException {
		LOG.debug("OpenSessionAs(" + username + ")");
		nbOpenedSession++;
		return super.openSessionAs(username);
	}

	@Override
	public CoreSession openSessionAs(NuxeoPrincipal principal) throws ClientException {
		LOG.debug("OpenSessionAs(" + principal + ")");
		nbOpenedSession++;
		if (nbOpenedSession > 1) {
			LOG.warn("more one session open [" + nbOpenedSession + "]");
		}
		return super.openSessionAs(principal);
	}

	@Override
	public void closeSession() {
		super.closeSession();
		session = null;
	}

	@Override
	public void closeSession(CoreSession session) {
		LOG.debug("CloseSession");
		nbOpenedSession--;
		super.closeSession(session);
	}

	public void setTestDatabase() {
		database = DatabaseH2.INSTANCE;
	}

	@Override
	public void setUp() throws Exception {
		LOG.debug("ENTER SETUP");
		setTestDatabase();
		super.setUp();
		LOG.debug("EXIT SETUP");
	}

	@Override
	public void tearDown() throws Exception {
		if (nbOpenedSession != 0) {
			LOG.error("NB NOT CLOSED SESSION = " + nbOpenedSession + " !!!");
		}
		super.tearDown();
	}

	protected void waitForAsyncEventCompletion() {
		STServiceLocator.getEventService().waitForAsyncCompletion();
	}
}
