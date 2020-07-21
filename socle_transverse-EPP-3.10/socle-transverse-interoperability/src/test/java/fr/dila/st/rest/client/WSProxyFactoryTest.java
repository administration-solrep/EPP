package fr.dila.st.rest.client;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.reponses.rest.api.WSAttribution;
import fr.dila.reponses.rest.api.WSControle;
import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.reponses.rest.api.WSReponse;
import fr.dila.st.rest.api.WSNotification;

public class WSProxyFactoryTest {

	private static WSProxyFactory	proxyFactory;
	private static WSProxyFactory	stubFactory;

	@BeforeClass
	public static void setup() {
		proxyFactory = new WSProxyFactory(null, null, null, null, null);
		Assert.assertNotNull(proxyFactory);

		stubFactory = new WSProxyFactory();
		Assert.assertNotNull(stubFactory);

	}

	@Test
	public void testGetServiceFails() {
		try {
			proxyFactory.getService(null);
			Assert.fail();
		} catch (WSProxyFactoryException e) {

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetServiceFails2() {
		try {
			proxyFactory.getService(Object.class);
			Assert.fail();
		} catch (WSProxyFactoryException e) {

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testGetServiceReponsesAsProxy() {
		try {
			WSAttribution wsAttribution = proxyFactory.getService(WSAttribution.class);
			Assert.assertNotNull(wsAttribution);

			WSControle wsControle = proxyFactory.getService(WSControle.class);
			Assert.assertNotNull(wsControle);

			WSNotification wsNotification = proxyFactory.getService(WSNotification.class);
			Assert.assertNotNull(wsNotification);

			WSQuestion wsQuestion = proxyFactory.getService(WSQuestion.class);
			Assert.assertNotNull(wsQuestion);

			WSReponse wsReponse = proxyFactory.getService(WSReponse.class);
			Assert.assertNotNull(wsReponse);

		} catch (WSProxyFactoryException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGetServiceReponsesAsStub() {
		try {
			WSAttribution wsAttribution = stubFactory.getService(WSAttribution.class);
			Assert.assertNotNull(wsAttribution);

			WSControle wsControle = stubFactory.getService(WSControle.class);
			Assert.assertNotNull(wsControle);

			WSNotification wsNotification = stubFactory.getService(WSNotification.class);
			Assert.assertNotNull(wsNotification);

			WSQuestion wsQuestion = stubFactory.getService(WSQuestion.class);
			Assert.assertNotNull(wsQuestion);

			WSReponse wsReponse = stubFactory.getService(WSReponse.class);
			Assert.assertNotNull(wsReponse);

		} catch (WSProxyFactoryException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testChangeBasePath() {

		try {
			WSProxyFactory factory = new WSProxyFactory("endpoint", "myBasePath", null, null, null);
			WSQuestion wsQuestionProxy = factory.getService(WSQuestion.class);

			if (!(wsQuestionProxy instanceof AbstractWsProxy)) {
				Assert.fail("wsQuestionProxy is not an instance of AbstractWsProxy");
			}

			AbstractWsProxy abstractWsProxy = (AbstractWsProxy) wsQuestionProxy;

			String basePath = abstractWsProxy.getBasePath();
			Assert.assertNotNull(basePath);
			Assert.assertEquals("myBasePath", basePath);

		} catch (WSProxyFactoryException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

}
