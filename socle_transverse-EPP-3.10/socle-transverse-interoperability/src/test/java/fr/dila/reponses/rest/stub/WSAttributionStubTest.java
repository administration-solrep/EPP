package fr.dila.reponses.rest.stub;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.sword.xsd.reponses.ChercherAttributionsResponse;
import fr.sword.xsd.reponses.ChercherLegislaturesResponse;
import fr.sword.xsd.reponses.ChercherMembresGouvernementResponse;

public class WSAttributionStubTest {

	private static WSAttributionStub	stub;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stub = new WSAttributionStub();
	}

	@Test
	public void testChercherAttributions() {
		try {
			ChercherAttributionsResponse response = stub.chercherAttributions(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testChercherMembresGouvernement() {
		try {
			ChercherMembresGouvernementResponse response = stub.chercherMembresGouvernement(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testChercherLegislatures() {
		try {
			ChercherLegislaturesResponse response = stub.chercherLegislatures();
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
