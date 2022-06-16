/**
 * 
 */
package webservices.test;

import junit.framework.TestCase;

import org.apache.commons.codec.binary.Base64;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import fr.sword.xsd.reponses.Auteur;
import fr.sword.xsd.reponses.Civilite;
import fr.sword.xsd.reponses.IndexationAn;
import fr.sword.xsd.reponses.Ministre;
import fr.sword.xsd.reponses.Question;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.QuestionType;

/**
 * @author admin
 * 
 */
public abstract class AbstractHttpTestCase extends TestCase {

	protected Client				client;
	protected WebResource			resource;
	//
	protected Auteur				auteur;
	protected Ministre				ministre;
	protected QuestionSource		questionSource;
	protected QuestionType			questionType;
	protected IndexationAn			indexationAn;

	//
	protected String				authentication;
	//
	// protected final static String username = "Administrator";
	// protected final static String password = "Administrator";
	protected final static String	username	= "adminsgg";
	protected final static String	password	= "adminsgg";

	/**
	 * 
	 */
	@Override
	protected void setUp() throws Exception {
		ClientConfig cc = new DefaultClientConfig();
		client = Client.create(cc);
		//
		auteur = new Auteur();
		auteur.setCivilite(Civilite.M);
		auteur.setGrpPol("UMP");
		auteur.setIdMandat("10");
		auteur.setNom("nom auteur");
		auteur.setPrenom("prenom auteur");
		//
		ministre = new Ministre();
		ministre.setId(1);
		ministre.setIntituleMinistere("IntituleMin");
		ministre.setTitreJo("TitreJo");
		//
		questionSource = QuestionSource.AN;
		questionType = QuestionType.QE;
		//
		indexationAn = new IndexationAn();
		// indexationAn.setRubrique("Rubrique");
		// indexationAn.setRubriqueTa("RubriqueTa");
		//
		// texteQuestion = new Texte();
		// texteQuestion.setAuteurDev("AuteurDev");
		// texteQuestion.setMin("MinTexte");
		// texteQuestion.setContent("Texte de la question");
	}

	/**
	 * 
	 * @return
	 */
	protected String initAuthentification() {
		String encodedCredential = new String(Base64.encodeBase64((username + ":" + password).getBytes()));
		authentication = "Basic " + encodedCredential;
		return authentication;
	}

	/**
	 * 
	 * @return
	 */
	protected Question getQuestion(String numeroQuestion, String idQuestion) {
		Question question = new Question();
		// question.setNumeroQuestion(numeroQuestion);
		// question.setId(idQuestion);
		// question.setAuteur(auteur);
		// question.setIndexationAn(indexationAn);
		// question.setMinistre(ministre);
		// question.setTexte(texteQuestion);
		// question.setSource(questionSource);
		// question.setType(questionType);
		return question;
	}
}
