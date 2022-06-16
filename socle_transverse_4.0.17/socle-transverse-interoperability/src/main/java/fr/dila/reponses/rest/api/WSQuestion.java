package fr.dila.reponses.rest.api;

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

/**
 * 
 * @author fbarmes
 * 
 */
public interface WSQuestion {

	public static final String	SERVICE_NAME									= "WSquestion";

	public static final String	METHOD_NAME_TEST								= "test";
	public static final String	METHOD_NAME_VERSION								= "version";

	public static final String	METHOD_NAME_CHERCHER_QUESTIONS					= "chercherQuestions";
	public static final String	METHOD_NAME_CHERCHER_ERRATA_QUESTIONS			= "chercherErrataQuestions";
	public static final String	METHOD_NAME_CHERCHER_CHANGEMENT_ETAT_QUESTIONS	= "chercherChangementDEtatQuestions";
	public static final String	METHOD_NAME_ENVOYER_QUESTIONS					= "envoyerQuestions";
	public static final String	METHOD_NAME_ENVOYER_QUESTIONS_ERRATA			= "envoyerQuestionsErrata";
	public static final String	METHOD_NAME_CHANGER_ETAT_QUESTION				= "changerEtatQuestions";
	public static final String	METHOD_NAME_RECHERCHER_DOSSIER					= "rechercherDossier";

	public String test() throws Exception;

	public VersionResponse version() throws Exception;

	public ChercherQuestionsResponse chercherQuestions(ChercherQuestionsRequest request) throws Exception;

	public ChercherErrataQuestionsResponse chercherErrataQuestions(ChercherErrataQuestionsRequest request)
			throws Exception;

	public ChercherChangementDEtatQuestionsResponse chercherChangementDEtatQuestions(
			ChercherChangementDEtatQuestionsRequest request) throws Exception;

	public EnvoyerQuestionsResponse envoyerQuestions(EnvoyerQuestionsRequest request) throws Exception;

	public EnvoyerQuestionsErrataResponse envoyerQuestionsErrata(EnvoyerQuestionsErrataRequest request)
			throws Exception;

	public ChangerEtatQuestionsResponse changerEtatQuestions(ChangerEtatQuestionsRequest request) throws Exception;

	public RechercherDossierResponse rechercherDossier(RechercherDossierRequest request) throws Exception;

}
