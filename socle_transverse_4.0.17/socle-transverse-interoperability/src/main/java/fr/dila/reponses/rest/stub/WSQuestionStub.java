package fr.dila.reponses.rest.stub;

import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChangerEtatQuestionsRequest;
import fr.sword.xsd.reponses.ChangerEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherChangementDEtatQuestionsRequest;
import fr.sword.xsd.reponses.ChercherChangementDEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherErrataQuestionsRequest;
import fr.sword.xsd.reponses.ChercherErrataQuestionsResponse;
import fr.sword.xsd.reponses.ChercherQuestionsRequest;
import fr.sword.xsd.reponses.ChercherQuestionsResponse;
import fr.sword.xsd.reponses.EnvoyerQuestionsErrataRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsErrataResponse;
import fr.sword.xsd.reponses.EnvoyerQuestionsRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsResponse;
import fr.sword.xsd.reponses.RechercherDossierRequest;
import fr.sword.xsd.reponses.RechercherDossierResponse;

public class WSQuestionStub implements WSQuestion {

	private static final String	FILE_BASEPATH								= "fr/dila/st/rest/stub/wsquestion/";

	public static final String	CHERCHER_QUESTIONS_REQUEST					= FILE_BASEPATH
																					+ "WSquestion_chercherQuestionsRequest.xml";
	public static final String	CHERCHER_QUESTIONS_RESPONSE					= FILE_BASEPATH
																					+ "WSquestion_chercherQuestionsResponse.xml";

	public static final String	CHERCHER_ERRATA_QUESTIONS_RESPONSE			= FILE_BASEPATH
																					+ "WSquestion_chercherErrataQuestionsResponse.xml";

	public static final String	CHERCHER_CHANGEMENT_ETAT_QUESTIONS_RESPONSE	= FILE_BASEPATH
																					+ "WSquestion_chercherChangementDEtatQuestionsResponse.xml";

	public static final String	ENVOYER_QUESTIONS_REQUEST					= FILE_BASEPATH
																					+ "envoyerquestions/WSquestion_envoyerQuestionsRequest.xml";
	public static final String	ENVOYER_QUESTIONS_RESPONSE					= FILE_BASEPATH
																					+ "envoyerquestions/WSquestion_envoyerQuestionsResponse.xml";

	public static final String	ENVOYER_QUESTIONS_ERRATA_RESPONSE			= FILE_BASEPATH
																					+ "WSquestion_envoyerQuestionsErrataResponse.xml";

	public static final String	CHANGER_ETAT_QUESTION_RESPONSE				= FILE_BASEPATH
																					+ "WSquestion_changerEtatQuestionsResponse.xml";

	public static final String	RECHERCHER_DOSSIER_RESPONSE					= FILE_BASEPATH
																					+ "rechercherdossier/WSquestion_rechercherDossierResponse.xml";

	@Override
	public String test() throws Exception {
		return WSQuestion.SERVICE_NAME;
	}

	@Override
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSQuestion();
	}

	@Override
	public ChercherQuestionsResponse chercherQuestions(ChercherQuestionsRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_QUESTIONS_RESPONSE, ChercherQuestionsResponse.class);
	}

	@Override
	public ChercherErrataQuestionsResponse chercherErrataQuestions(ChercherErrataQuestionsRequest request)
			throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_ERRATA_QUESTIONS_RESPONSE,
				ChercherErrataQuestionsResponse.class);
	}

	@Override
	public ChercherChangementDEtatQuestionsResponse chercherChangementDEtatQuestions(
			ChercherChangementDEtatQuestionsRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_CHANGEMENT_ETAT_QUESTIONS_RESPONSE,
				ChercherChangementDEtatQuestionsResponse.class);
	}

	@Override
	public EnvoyerQuestionsResponse envoyerQuestions(EnvoyerQuestionsRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(ENVOYER_QUESTIONS_RESPONSE, EnvoyerQuestionsResponse.class);
	}

	@Override
	public EnvoyerQuestionsErrataResponse envoyerQuestionsErrata(EnvoyerQuestionsErrataRequest request)
			throws Exception {
		return JaxBHelper.buildRequestFromFile(ENVOYER_QUESTIONS_ERRATA_RESPONSE, EnvoyerQuestionsErrataResponse.class);
	}

	@Override
	public ChangerEtatQuestionsResponse changerEtatQuestions(ChangerEtatQuestionsRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHANGER_ETAT_QUESTION_RESPONSE, ChangerEtatQuestionsResponse.class);
	}

	@Override
	public RechercherDossierResponse rechercherDossier(RechercherDossierRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(RECHERCHER_DOSSIER_RESPONSE, RechercherDossierResponse.class);
	}

}
