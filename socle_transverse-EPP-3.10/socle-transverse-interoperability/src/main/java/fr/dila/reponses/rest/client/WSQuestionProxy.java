package fr.dila.reponses.rest.client;

import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.st.rest.client.AbstractWsProxy;
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

public class WSQuestionProxy extends AbstractWsProxy implements WSQuestion {

	public WSQuestionProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
		super(endpoint, basePath, username, password, keyAlias);
	}

	@Override
	protected String getServiceName() {
		return WSQuestion.SERVICE_NAME;
	}

	@Override
	public String test() throws Exception {
		return doGet(METHOD_NAME_TEST, String.class);
	}

	@Override
	public VersionResponse version() throws Exception {
		return doGet(METHOD_NAME_VERSION, VersionResponse.class);
	}

	@Override
	public ChercherQuestionsResponse chercherQuestions(ChercherQuestionsRequest request) throws Exception {
		return doPost(WSQuestion.METHOD_NAME_CHERCHER_QUESTIONS, request, ChercherQuestionsResponse.class);
	}

	@Override
	public ChercherErrataQuestionsResponse chercherErrataQuestions(ChercherErrataQuestionsRequest request)
			throws Exception {
		return doPost(METHOD_NAME_CHERCHER_ERRATA_QUESTIONS, request, ChercherErrataQuestionsResponse.class);
	}

	@Override
	public ChercherChangementDEtatQuestionsResponse chercherChangementDEtatQuestions(
			ChercherChangementDEtatQuestionsRequest request) throws Exception {
		return doPost(METHOD_NAME_CHERCHER_CHANGEMENT_ETAT_QUESTIONS, request,
				ChercherChangementDEtatQuestionsResponse.class);
	}

	@Override
	public EnvoyerQuestionsResponse envoyerQuestions(EnvoyerQuestionsRequest request) throws Exception {
		return doPost(METHOD_NAME_ENVOYER_QUESTIONS, request, EnvoyerQuestionsResponse.class);
	}

	@Override
	public EnvoyerQuestionsErrataResponse envoyerQuestionsErrata(EnvoyerQuestionsErrataRequest request)
			throws Exception {
		return doPost(METHOD_NAME_ENVOYER_QUESTIONS_ERRATA, request, EnvoyerQuestionsErrataResponse.class);
	}

	@Override
	public ChangerEtatQuestionsResponse changerEtatQuestions(ChangerEtatQuestionsRequest request) throws Exception {
		return doPost(METHOD_NAME_CHANGER_ETAT_QUESTION, request, ChangerEtatQuestionsResponse.class);
	}

	@Override
	public RechercherDossierResponse rechercherDossier(RechercherDossierRequest request) throws Exception {
		return doPost(METHOD_NAME_RECHERCHER_DOSSIER, request, RechercherDossierResponse.class);
	}

}
