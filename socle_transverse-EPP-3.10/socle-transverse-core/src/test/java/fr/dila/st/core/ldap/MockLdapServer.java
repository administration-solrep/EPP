package fr.dila.st.core.ldap;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.directory.server.core.configuration.Configuration;
import org.apache.directory.server.core.configuration.MutablePartitionConfiguration;
import org.apache.directory.server.core.configuration.MutableStartupConfiguration;
import org.apache.directory.server.core.jndi.CoreContextFactory;
import org.apache.directory.server.core.partition.PartitionNexus;
import org.nuxeo.ecm.directory.ldap.ContextProvider;

/**
 * An embedded LDAP test server, complete with test data for running the unit tests against.
 * 
 * @author Luke Taylor
 * @version $Id: LdapTestServer.java 1496 2006-05-23 13:38:33Z benalex $
 */
public class MockLdapServer implements ContextProvider {
	// ~ Instance fields
	// ================================================================================================
	private static final Log			log	= LogFactory.getLog(MockLdapServer.class);

	private DirContext					serverContext;

	// Move the working dir to the temp directory
	private File						workingDir;

	private MutableStartupConfiguration	cfg;

	// ~ Constructors
	// ===================================================================================================

	/**
	 * Starts up and configures ApacheDS.
	 */
	public MockLdapServer() {
		try {
			workingDir = File.createTempFile("apacheds-work-", ".tmp", new File(System.getProperty("java.io.tmpdir")));
			workingDir.delete();
			workingDir.mkdirs();
		} catch (IOException e) {
			log.error(e);
		}
		startLdapServer();
	}

	// ~ Methods
	// ========================================================================================================

	public void createGroup(String cn, String ou, String[] memberDns) {
		Attributes group = new BasicAttributes("cn", cn);
		Attribute members = new BasicAttribute("member");
		Attribute orgUnit = new BasicAttribute("ou", ou);

		for (String memberDn : memberDns) {
			members.add(memberDn);
		}

		Attribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("top");
		objectClass.add("groupOfNames");

		group.put(objectClass);
		group.put(members);
		group.put(orgUnit);

		try {
			serverContext.createSubcontext("cn=" + cn + ",ou=groups", group);
		} catch (NameAlreadyBoundException ignore) {
			// System.out.println(" group " + cn + " already exists.");
		} catch (NamingException ne) {
			log.error("Failed to create group", ne);
		}
	}

	public void createManagerUser() {
		Attributes user = new BasicAttributes("cn", "manager", true);
		user.put("userPassword", "secret");

		Attribute objectClass = new BasicAttribute("objectClass");
		user.put(objectClass);
		objectClass.add("top");
		objectClass.add("person");
		objectClass.add("organizationalPerson");
		objectClass.add("inetOrgPerson");
		user.put("sn", "Manager");
		user.put("cn", "manager");

		try {
			serverContext.createSubcontext("cn=manager", user);
		} catch (NameAlreadyBoundException ignore) {
			log.warn("Manager user already exists.");
		} catch (NamingException ne) {
			log.error("Failed to create manager user", ne);
		}
	}

	public void createOu(String name) {
		Attributes ou = new BasicAttributes("ou", name);
		Attribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("top");
		objectClass.add("organizationalUnit");
		ou.put(objectClass);

		try {
			serverContext.createSubcontext("ou=" + name, ou);
		} catch (NameAlreadyBoundException ignore) {
			log.warn("ou " + name + " already exists.");
		} catch (NamingException ne) {
			log.error("Failed to create ou: ", ne);
		}
	}

	public void createUser(String uid, String cn, String password) {
		Attributes user = new BasicAttributes("uid", uid);
		user.put("cn", cn);
		user.put("userPassword", password);

		Attribute objectClass = new BasicAttribute("objectClass");
		user.put(objectClass);
		objectClass.add("top");
		objectClass.add("person");
		objectClass.add("organizationalPerson");
		objectClass.add("inetOrgPerson");
		user.put("sn", uid);

		try {
			serverContext.createSubcontext("uid=" + uid + ",ou=people", user);
		} catch (NameAlreadyBoundException ignore) {
			// System.out.println(" user " + uid + " already exists.");
		} catch (NamingException ne) {
			System.err.println("Failed to create user.");
			ne.printStackTrace();
		}
	}

	public Configuration getConfiguration() {
		return cfg;
	}

	public DirContext getContext() {
		// ensure the context server is not closed
		startLdapServer();
		return serverContext;
	}

	private void initConfiguration() throws NamingException {
		// Create the partition for the tests
		MutablePartitionConfiguration testPartition = new MutablePartitionConfiguration();
		testPartition.setId("NuxeoTestLdapServer");
		testPartition.setSuffix("dc=dila,dc=fr");

		BasicAttributes attributes = new BasicAttributes();
		BasicAttribute objectClass = new BasicAttribute("objectClass");
		objectClass.add("top");
		objectClass.add("domain");
		objectClass.add("extensibleObject");
		attributes.put(objectClass);
		testPartition.setContextEntry(attributes);

		Set<Object> indexedAttrs = new HashSet<Object>();
		indexedAttrs.add("objectClass");
		indexedAttrs.add("uid");
		indexedAttrs.add("cn");
		indexedAttrs.add("ou");
		indexedAttrs.add("uniqueMember");

		testPartition.setIndexedAttributes(indexedAttrs);

		Set<MutablePartitionConfiguration> partitions = new HashSet<MutablePartitionConfiguration>();
		partitions.add(testPartition);

		cfg.setPartitionConfigurations(partitions);
	}

	public void startLdapServer() {
		cfg = new MutableStartupConfiguration();
		cfg.setWorkingDirectory(workingDir);

		log.debug("Working directory is " + workingDir.getAbsolutePath());

		Properties env = new Properties();

		env.setProperty(Context.PROVIDER_URL, "dc=dila,dc=fr");
		env.setProperty(Context.INITIAL_CONTEXT_FACTORY, CoreContextFactory.class.getName());
		env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");
		env.setProperty(Context.SECURITY_PRINCIPAL, PartitionNexus.ADMIN_PRINCIPAL);
		env.setProperty(Context.SECURITY_CREDENTIALS, PartitionNexus.ADMIN_PASSWORD);

		try {
			initConfiguration();
			env.putAll(cfg.toJndiEnvironment());
			serverContext = new InitialDirContext(env);
		} catch (NamingException e) {
			log.error("Failed to start Apache DS: ", e);
		}
		// Initialise les schémas
		// initSchema(); TODO apacheDS reporte serverContext.getSchema("") non implémenté :(
	}

	/**
	 * Charge les schémas du projet Réponses dans l'annuaire LDAP.
	 */
	public void initSchema() {
		try {
			DirContext schema = serverContext.getSchema("");

			Attributes attrs = new BasicAttributes(true);
			attrs.put("NUMERICOID", "1.1.1.2");
			attrs.put("NAME", "dateDebut");
			attrs.put("DESC", "date de début de validité");
			attrs.put("EQUALITY", "generalizedTimeMatch");
			attrs.put("ORDERING", "generalizedTimeOrderingMatch");
			attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.24");
			schema.createSubcontext("AttributeDefinition/dateDebut", attrs);

			attrs = new BasicAttributes(true);
			attrs.put("NUMERICOID", "1.1.1.3");
			attrs.put("NAME", "dateFin");
			attrs.put("DESC", "date de fin de validité");
			attrs.put("EQUALITY", "generalizedTimeMatch");
			attrs.put("ORDERING", "generalizedTimeOrderingMatch");
			attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.24");
			schema.createSubcontext("AttributeDefinition/dateFin", attrs);

			attrs = new BasicAttributes(true);
			attrs.put("NUMERICOID", "1.1.1.6");
			attrs.put("NAME", "label");
			attrs.put("DESC", "label");
			attrs.put("SUP", "name");
			schema.createSubcontext("AttributeDefinition/label", attrs);

			attrs = new BasicAttributes(true);
			attrs.put("NUMERICOID", "1.3");
			attrs.put("NAME", "poste");
			attrs.put("SUP", "groupOfUniqueNames");
			attrs.put("STRUCTURAL", "true");

			Attribute must = new BasicAttribute("MUST");
			must.add("dateDebut");
			must.add("label");
			attrs.put(must);

			Attribute may = new BasicAttribute("MAY");
			may.add("dateFin");
			may.add("uniqueMember");

			schema.createSubcontext("AttributeDefinition/poste", attrs);

			// attrs.put("NUMERICOID", "1.1.1");
			// attrs.put("NAME", "rattachement");
			// attrs.put("EQUALITY", "distinguishedNameMatch");
			// attrs.put("SYNTAX", "1.3.6.1.4.1.1466.115.121.1.12");

			// schema.createSubcontext("AttributeDefinition/rattachement", attrs);
		} catch (NamingException e) {
			log.error("Impossible d'initialiser le schéma");
		}
	}
}
