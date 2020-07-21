package fr.dila.solonepp.rest.api;

import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epp.ChercherCorbeilleRequest;
import fr.sword.xsd.solon.epp.ChercherCorbeilleResponse;
import fr.sword.xsd.solon.epp.ChercherDossierRequest;
import fr.sword.xsd.solon.epp.ChercherDossierResponse;
import fr.sword.xsd.solon.epp.ChercherIdentiteRequest;
import fr.sword.xsd.solon.epp.ChercherIdentiteResponse;
import fr.sword.xsd.solon.epp.ChercherMandatParNORRequest;
import fr.sword.xsd.solon.epp.ChercherMandatParNORResponse;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceResponse;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesRequest;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesResponse;
import fr.sword.xsd.solon.epp.MajTableRequest;
import fr.sword.xsd.solon.epp.MajTableResponse;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.NotifierVerrouRequest;
import fr.sword.xsd.solon.epp.NotifierVerrouResponse;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJORequest;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJOResponse;

public interface WSEpp {

	static final String	SERVICE_NAME						= "WSepp";

	static final String	METHOD_CHERCHER_CORBEILLE			= "chercherCorbeille";
	static final String	METHOD_CHERCHER_DOSSIER				= "chercherDossier";
	static final String	METHOD_NOTIFIER_TRANSITION			= "notifierTransition";
	static final String	METHOD_NOTIFIER_VERROU				= "notifierVerrou";
	static final String	METHOD_CHERCHER_TABLE_REFERENCE		= "chercherTableDeReference";
	static final String	METHOD_CHERCHER_IDENTITE			= "chercherIdentite";
	static final String	METHOD_CHERCHER_MANDAT_PAR_NOR		= "chercherMandatParNOR";
	static final String	METHOD_MAJ_TABLE					= "majTable";
	static final String	METHOD_HAS_COMMUNICATION			= "hasCommunicationNonTraitees";
	static final String METHOD_TRANSMISSION_DATE_PUBLI_JO	= "transmissionDatePublicationJO";

	static final String	METHOD_TEST						= "test";
	static final String	METHOD_VERSION					= "version";

	String test() throws Exception;

	VersionResponse version() throws Exception;

	ChercherCorbeilleResponse chercherCorbeille(ChercherCorbeilleRequest request) throws Exception;

	ChercherDossierResponse chercherDossier(ChercherDossierRequest request) throws Exception;

	NotifierTransitionResponse notifierTransition(NotifierTransitionRequest request) throws Exception;

	NotifierVerrouResponse notifierVerrou(NotifierVerrouRequest request) throws Exception;

	ChercherTableDeReferenceResponse chercherTableDeReference(ChercherTableDeReferenceRequest request) throws Exception;

	ChercherIdentiteResponse chercherIdentite(ChercherIdentiteRequest request) throws Exception;

	MajTableResponse majTable(MajTableRequest request) throws Exception;

	ChercherMandatParNORResponse chercherMandatParNor(ChercherMandatParNORRequest request) throws Exception;

	HasCommunicationNonTraiteesResponse hasCommunicationNonTraitees(HasCommunicationNonTraiteesRequest request)
			throws Exception;
	
	TransmissionDatePublicationJOResponse transmissionDatePublicationJO(TransmissionDatePublicationJORequest request) throws Exception;

}
