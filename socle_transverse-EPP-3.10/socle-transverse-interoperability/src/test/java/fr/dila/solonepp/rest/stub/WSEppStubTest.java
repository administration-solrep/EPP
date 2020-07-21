package fr.dila.solonepp.rest.stub;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epp.ChercherCorbeilleRequest;
import fr.sword.xsd.solon.epp.ChercherCorbeilleResponse;
import fr.sword.xsd.solon.epp.ChercherDossierRequest;
import fr.sword.xsd.solon.epp.ChercherDossierResponse;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceResponse;
import fr.sword.xsd.solon.epp.MajTableRequest;
import fr.sword.xsd.solon.epp.MajTableResponse;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.NotifierVerrouRequest;
import fr.sword.xsd.solon.epp.NotifierVerrouResponse;

public class WSEppStubTest {

	private static WSEppStub	stub;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stub = new WSEppStub();
		Assert.assertNotNull(stub);
	}

	@Test
	public void testTest() throws Exception {

		String expected = WSEpp.SERVICE_NAME;
		String actual = stub.test();

		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testVersion() throws Exception {
		VersionResponse response = stub.version();
		Assert.assertNotNull(response);
	}

	@Test
	public void testChercherCorbeille() {

		try {
			ChercherCorbeilleRequest request = JaxBHelper.buildRequestFromFile(WSEppStub.CHERCHER_CORBEILLE_REQUEST,
					ChercherCorbeilleRequest.class);
			Assert.assertNotNull(request);

			ChercherCorbeilleResponse response = stub.chercherCorbeille(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testChercherDossier() {
		try {
			ChercherDossierRequest request = JaxBHelper.buildRequestFromFile(WSEppStub.CHERCHER_DOSSIER_REQUEST,
					ChercherDossierRequest.class);
			Assert.assertNotNull(request);

			ChercherDossierResponse response = stub.chercherDossier(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testChercherTableDeReference() {
		try {
			ChercherTableDeReferenceRequest request = JaxBHelper.buildRequestFromFile(
					WSEppStub.CHERCHER_TABLE_REFERENCE_REQUEST, ChercherTableDeReferenceRequest.class);
			Assert.assertNotNull(request);

			ChercherTableDeReferenceResponse response = stub.chercherTableDeReference(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testNotifierTransition() {
		try {
			NotifierTransitionRequest request = JaxBHelper.buildRequestFromFile(WSEppStub.NOTIFIER_TRANSITION_REQUEST,
					NotifierTransitionRequest.class);
			Assert.assertNotNull(request);

			NotifierTransitionResponse response = stub.notifierTransition(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testNotifierVerrou() {
		try {

			NotifierVerrouRequest request = JaxBHelper.buildRequestFromFile(WSEppStub.NOTIFIER_VERROU_REQUEST,
					NotifierVerrouRequest.class);
			Assert.assertNotNull(request);

			NotifierVerrouResponse response = stub.notifierVerrou(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testMajTable() throws Exception {

		MajTableRequest request = JaxBHelper.buildRequestFromFile(WSEppStub.MAJ_TABLE_REQUEST, MajTableRequest.class);
		Assert.assertNotNull(request);

		MajTableResponse response = stub.majTable(request);
		Assert.assertNotNull(response);

	}

}
