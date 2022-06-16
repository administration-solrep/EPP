package webservices.test;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXB;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.dila.st.rest.helper.JaxbContextHolder;
import fr.sword.xsd.reponses.ChercherReponsesRequest;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.EnvoyerQuestionsRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsResponse;
import fr.sword.xsd.reponses.EnvoyerReponsesRequest;
import fr.sword.xsd.reponses.EnvoyerReponsesResponse;

public class HttpClientQuestionTestCase extends AbstractHttpTestCase {

	private static final String	URL					= "http://localhost:8080/reponses/site/reponses";

	public static final int		AUTH_TYPE_NONE		= 0;

	public static final int		AUTH_TYPE_BASIC		= 1;

	public static final int		AUTH_TYPE_SECRET	= 2;

	// protected List<Cookie> cookies;

	protected int				authType			= AUTH_TYPE_BASIC;

	// protected String userName="Administrator";
	//
	// protected String password;

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri(URL).build();
	}

	@Test
	public void testEnvoyerQuestion() throws Exception {
		try {
			System.out.println("**********testEnvoyerQuestion*******************");
			//
			resource = client.resource(getBaseURI());
			String encodedCredential = initAuthentification();
			// URL urlfile = ClassLoader.getSystemResource("EnvoyerQuestionsRequest2.xml");
			URL urlfile = ClassLoader.getSystemResource("EnvoyerQuestionsRequest3.xml");
			//
			EnvoyerQuestionsRequest request = (EnvoyerQuestionsRequest) JaxbContextHolder.getInstance()
					.getJaxbContext(EnvoyerQuestionsRequest.class).createUnmarshaller()
					.unmarshal((new File(urlfile.getFile())));
			//
			EnvoyerQuestionsResponse reponse = resource.path("WSQuestions").path("envoyerQuestions")
					.header("Authorization", encodedCredential).type(MediaType.TEXT_XML).accept(MediaType.TEXT_XML)
					.put(EnvoyerQuestionsResponse.class, request);
			//
			StringWriter xmlOutPut = new StringWriter();
			JAXB.marshal(reponse, xmlOutPut);
			System.out.println(xmlOutPut.toString());
			//
			assertNotNull(reponse);
		} catch (Exception e) {
			System.out.println("**********testEnvoyerQuestion ERROR*******************");
			e.printStackTrace();
		}
	}

	@Test
	public void testEnvoyerReponse() throws Exception {
		System.out.println("**********testEnvoyerReponse*******************");
		//
		try {
			resource = client.resource(getBaseURI());
			String encodedCredential = initAuthentification();
			// URL urlfile = ClassLoader.getSystemResource("EnvoyerReponsesRequest2.xml");
			URL urlfile = ClassLoader.getSystemResource("EnvoyerReponsesRequest3.xml");
			//
			EnvoyerReponsesRequest request = (EnvoyerReponsesRequest) JaxbContextHolder.getInstance()
					.getJaxbContext(EnvoyerReponsesRequest.class).createUnmarshaller()
					.unmarshal((new File(urlfile.getFile())));
			//
			EnvoyerReponsesResponse reponse = resource.path("WSReponses").path("envoyerReponses")
					.header("Authorization", encodedCredential).type(MediaType.TEXT_XML).accept(MediaType.TEXT_XML)
					.put(EnvoyerReponsesResponse.class, request);
			//
			StringWriter xmlOutPut = new StringWriter();
			JAXB.marshal(reponse, xmlOutPut);
			System.out.println(xmlOutPut.toString());
			//
			assertNotNull(reponse);
			//
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

	}

	@Test
	public void testRechercheReponse() throws Exception {
		try {
			System.out.println("**********testRechercheReponse*******************");
			//
			resource = client.resource(getBaseURI());
			String encodedCredential = initAuthentification();
			// URL urlfile = ClassLoader.getSystemResource("EnvoyerQuestionsRequest2.xml");
			URL urlfile = ClassLoader.getSystemResource("ChercherReponsesRequest.xml");
			//
			ChercherReponsesRequest request = (ChercherReponsesRequest) JaxbContextHolder.getInstance()
					.getJaxbContext(ChercherReponsesRequest.class).createUnmarshaller()
					.unmarshal((new File(urlfile.getFile())));
			//
			ChercherReponsesResponse reponse = resource.path("WSReponses").path("chercherReponses")
					.header("Authorization", encodedCredential).type(MediaType.TEXT_XML).accept(MediaType.TEXT_XML)
					.post(ChercherReponsesResponse.class, request);
			//
			StringWriter xmlOutPut = new StringWriter();
			JAXB.marshal(reponse, xmlOutPut);
			System.out.println(xmlOutPut.toString());
			//
			assertNotNull(reponse);
		} catch (Exception e) {
			System.out.println("**********testEnvoyerQuestion ERROR*******************");
			e.printStackTrace();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

}
