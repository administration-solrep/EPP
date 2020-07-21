package fr.dila.st.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.STRepositoryTestCase;
import fr.dila.st.core.helper.ParameterTestHelper;

public class TestSTParameterService extends STRepositoryTestCase {

	private static final Log	LOG	= LogFactory.getLog(TestSTParameterService.class);

	public void testService() throws ClientException {

		final STParametreService paramService = STServiceLocator.getSTParametreService();
		assertNotNull(paramService);
		assertTrue(paramService instanceof STParametreServiceImpl);

		openSession();

		// no parameter folder existing
		try {
			paramService.getParametreFolder(session);
			fail("No param folder : should raise exception");
		} catch (ClientException e) {
			LOG.info(e.getMessage());
			assertTrue(e.getMessage().contains("Racine des parametres non trouvée"));
		}

		// create a parameter folder
		DocumentModel doc = session.createDocumentModel(STConstant.PARAMETRE_FOLDER_DOCUMENT_TYPE);
		doc = session.createDocument(doc);
		session.save();

		assertEquals(doc.getId(), paramService.getParametreFolder(session).getId());

		closeSession();

	}

	public void testParamAccess() throws ClientException {
		openSession();

		final STParametreService paramService = STServiceLocator.getSTParametreService();
		final String paramName = "mon-param";
		final String paramValue = "ma valeur";

		assertNull(paramService.getParametre(session, paramName));

		STParametre createdParameter = ParameterTestHelper.changeOrCreateParammeter(session, paramName, paramValue);
		session.save();

		STParametre retrievedParameter = paramService.getParametre(session, paramName);
		assertEquals(createdParameter.getDocument().getId(), retrievedParameter.getDocument().getId());

		String value = paramService.getParametreValue(session, paramName);
		assertEquals(paramValue, value);

		closeSession();
	}

}
