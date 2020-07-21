package fr.dila.st.api.constant;

/**
 * Constantes des schémas du socle transverse.
 * 
 * @author jtremeaux
 */
public final class STWebserviceConstant {
	// *************************************************************
	// Type de webservices existant : reponse
	// *************************************************************

	public static final String	CHERCHER_QUESTIONS										= "WsChercherQuestions";

	public static final String	CHERCHER_REPONSES										= "WsChercherReponses";

	public static final String	CHERCHER_ERRATA_QUESTIONS								= "WsChercherErrataQuestions";

	public static final String	CHERCHER_ERRATA_REPONSES								= "WsChercherErrataReponses";

	public static final String	CHERCHER_CHANGEMENT_ETAT_QUESTION						= "WsChercherChangementDEtatQuestions";

	public static final String	CHERCHER_ATTRIBUTIONS									= "WsChercherAttributions";

	public static final String	CHERCHER_ATTRIBUTIONS_DATE								= "WsChercherAttributionsDate";

	public static final String	ENVOYER_QUESTIONS										= "WsEnvoyerQuestions";

	public static final String	ENVOYER_REPONSES										= "WsEnvoyerReponses";

	public static final String	ENVOYER_REPONSES_ERRATA									= "WsEnvoyerReponsesErrata";

	public static final String	ENVOYER_QUESTIONS_ERRATA								= "WsEnvoyerQuestionsErrata";

	public static final String	ENVOYER_CHANGEMENT_ETAT									= "WsChangerEtatQuestions";

	public static final String	RECHERCHER_DOSSIER										= "WsRechercherDossier";

	public static final String	CHERCHER_RETOUR_PUBLICATION								= "WsChercherRetourPublication";

	public static final String	CONTROLE_PUBLICATION									= "WsControlePublication";

	// *************************************************************
	// Type de webservices existant : solonepg
	// *************************************************************

	public static final String	ATTRIBUER_NOR											= "WsEpgAttribuerNor";

	public static final String	CHERCHER_DOSSIER										= "WsEpgChercherDossier";

	public static final String	DONNER_AVIS_CE											= "WsEpgDonnerAvisCE";

	public static final String	CHERCHER_MODIFICATION_DOSSIER							= "WsEpgChercherModificationDossier";

	public static final String	MODIFIER_DOSSIER_CE										= "WsEpgModifierDossierCE";

	public static final String	RECHERCHER_DOSSIER_EPG									= "WsEpgRechercherDossier";

	public static final String	ENVOYER_PREMIERE_DEMANDE_PE								= "WsSpeEnvoyerPremiereDemandePE";

	public static final String	ENVOYER_DEMANDE_SUIVANTE_PE								= "WsSpeEnvoyerDemandeSuivantePE";

	public static final String	ENVOYER_RETOUR_PE										= "WsSpeEnvoyerRetourPE";


	// *************************************************************
	// GROUPES && PROFILS
	// *************************************************************

	public static final String	PROFIL_AN												= "AnEditor";

	public static final String	PROFIL_SENAT											= "SenatEditor";

	public static final String	CREER_DOSSIER_AVIS										= "WsEpgCreerDossierAvis";

	public static final String	MODIFIER_DOSSIER_AVIS									= "WsEpgModifierDossierAvis";

	public static final String	CREER_DOSSIER_DECRET_PR_IND								= "WsEpgCreerDossierDecretPrInd";

	public static final String	MODIFIER_DOSSIER_DECRET_PR_IND							= "WsEpgModifierDossierDecretPrInd";

	public static final String	CREER_DOSSIER_INFOS_PARL								= "WsEpgCreerDossierInfosParlementaires";

	public static final String	MODIFIER_DOSSIER_INFOS_PARL								= "WsEpgModifierDossierInfosParlementaires";

	/**
	 * utility class
	 */
	private STWebserviceConstant() {
		// do nothing
	}
}
