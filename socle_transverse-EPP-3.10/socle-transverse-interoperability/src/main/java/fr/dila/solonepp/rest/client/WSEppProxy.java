package fr.dila.solonepp.rest.client;

import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.st.rest.client.AbstractWsProxy;
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

public class WSEppProxy extends AbstractWsProxy implements WSEpp {

	public WSEppProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
		super(endpoint, basePath, username, password, keyAlias);
	}

	@Override
	public String test() throws Exception {
		return doGet(METHOD_TEST, String.class);
	}

	@Override
	public VersionResponse version() throws Exception {
		return doGet(METHOD_VERSION, VersionResponse.class);
	}

	@Override
	public ChercherCorbeilleResponse chercherCorbeille(ChercherCorbeilleRequest request) throws Exception {
		return doPost(METHOD_CHERCHER_CORBEILLE, request, ChercherCorbeilleResponse.class);
	}

	@Override
	public ChercherDossierResponse chercherDossier(ChercherDossierRequest request) throws Exception {
		return doPost(METHOD_CHERCHER_DOSSIER, request, ChercherDossierResponse.class);
	}

	@Override
	public NotifierTransitionResponse notifierTransition(NotifierTransitionRequest request) throws Exception {
		return doPost(METHOD_NOTIFIER_TRANSITION, request, NotifierTransitionResponse.class);
	}

	@Override
	public NotifierVerrouResponse notifierVerrou(NotifierVerrouRequest request) throws Exception {
		return doPost(METHOD_NOTIFIER_VERROU, request, NotifierVerrouResponse.class);
	}

	@Override
	public ChercherTableDeReferenceResponse chercherTableDeReference(ChercherTableDeReferenceRequest request)
			throws Exception {
		return doPost(METHOD_CHERCHER_TABLE_REFERENCE, request, ChercherTableDeReferenceResponse.class);
	}

	@Override
	public MajTableResponse majTable(MajTableRequest request) throws Exception {
		return doPost(METHOD_MAJ_TABLE, request, MajTableResponse.class);
	}

	@Override
	public ChercherIdentiteResponse chercherIdentite(ChercherIdentiteRequest request) throws Exception {
		return doPost(METHOD_CHERCHER_IDENTITE, request, ChercherIdentiteResponse.class);
	}

	@Override
	protected String getServiceName() {
		return WSEpp.SERVICE_NAME;
	}

	@Override
	public ChercherMandatParNORResponse chercherMandatParNor(ChercherMandatParNORRequest request) throws Exception {
		return doPost(METHOD_CHERCHER_MANDAT_PAR_NOR, request, ChercherMandatParNORResponse.class);
	}

	@Override
	public HasCommunicationNonTraiteesResponse hasCommunicationNonTraitees(HasCommunicationNonTraiteesRequest request)
			throws Exception {
		return doPost(METHOD_HAS_COMMUNICATION, request, HasCommunicationNonTraiteesResponse.class);
	}
	
	@Override
	public TransmissionDatePublicationJOResponse transmissionDatePublicationJO(TransmissionDatePublicationJORequest request)
			throws Exception {
		return doPost(METHOD_TRANSMISSION_DATE_PUBLI_JO, request, TransmissionDatePublicationJOResponse.class);
	}
}
