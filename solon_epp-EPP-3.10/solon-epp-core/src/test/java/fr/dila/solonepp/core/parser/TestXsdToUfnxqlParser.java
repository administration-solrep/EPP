package fr.dila.solonepp.core.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.mailbox.ParticipantsList;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.core.SolonEppRepositoryTestCase;

public class TestXsdToUfnxqlParser extends SolonEppRepositoryTestCase {

	private static final String	UNFXQL_RESULT	= " SELECT m.ecm:uuid as id FROM Message AS m  WHERE (m.cslk:idDossier ILIKE ?) AND  m.ecm:parentId = ? ";

	private static final String	MAILBOX			= "mailbox";

	@Override
	public void setUp() throws Exception {
		super.setUp();
		deployContrib("fr.dila.solonepp.core", "OSGI-INF/service/mailbox-framework.xml");
		deployContrib("fr.dila.solonepp.core", "OSGI-INF/service/mailbox-institution-framework.xml");
		// deployContrib("fr.dila.st.core", "OSGI-INF/querymaker-contrib.xml");
		deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-querymaker-contrib.xml");

	}

	/**
	 * Test du filtre des destinataires en copie.
	 * 
	 * @throws Exception
	 */
	public void testParser() throws Exception {
		openSession();

		List<Object> listObj = new ArrayList<Object>();
		listObj.add("TEST");
		// create "GOUVERNEMENT" mailbox
		createMailboxDoc();
		session.save();
		closeSession();
		openSession();

		String unxqlQuery = new XsdToUfnxqlParser().parse("msg:id_dossier ILIKE ? ", listObj, session, "gouvernement");
		Assert.assertEquals(unxqlQuery, UNFXQL_RESULT);

		closeSession();

	}

	protected DocumentModel getBareMailboxDoc() throws Exception {
		DocumentModel mailbox = session.createDocumentModel(MailboxConstants.MAILBOX_DOCUMENT_TYPE);
		mailbox.setPathInfo(session.getRootDocument().getPathAsString(), MAILBOX);
		return mailbox;
	}

	protected DocumentModel createMailboxDoc() throws Exception {
		DocumentModel doc = getBareMailboxDoc();
		Mailbox mb = doc.getAdapter(Mailbox.class);

		mb.setId(IdUtils.generateId(SolonEppConstant.INSTITUTION_PREFIX + "gouvernement", "-", true, 50));

		mb.setTitle("mailbox title");
		mb.setDescription("mb description");
		mb.setType(MailboxConstants.type.personal.name());

		mb.setProfiles(Arrays.asList(new String[] { "profile1" }));

		mb.setOwner("mbowner");
		mb.setUsers(Arrays.asList(new String[] { "toto", "titi" }));
		mb.setNotifiedUsers(Arrays.asList(new String[] { "toto" }));
		mb.setGroups(Arrays.asList(new String[] { "group1", "group2" }));

		mb.setFavorites(Arrays.asList(new String[] { "fav1", "fav2" }));

		ParticipantsList ml = mb.getParticipantListTemplate();
		ml.setId("mlid");
		ml.setTitle("ml title");
		ml.setDescription("ml description");
		ml.setMailboxIds(Arrays.asList(new String[] { "mb1", "mb2" }));
		mb.addParticipantList(ml);

		return session.createDocument(mb.getDocument());
	}

}
