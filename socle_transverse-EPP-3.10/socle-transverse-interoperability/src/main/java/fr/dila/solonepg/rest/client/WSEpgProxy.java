package fr.dila.solonepg.rest.client;

import fr.dila.solonepg.rest.api.WSEpg;
import fr.dila.st.rest.client.AbstractWsProxy;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epg.AttribuerNorRequest;
import fr.sword.xsd.solon.epg.AttribuerNorResponse;
import fr.sword.xsd.solon.epg.ChercherDossierRequest;
import fr.sword.xsd.solon.epg.ChercherDossierResponse;
import fr.sword.xsd.solon.epg.ChercherModificationDossierRequest;
import fr.sword.xsd.solon.epg.ChercherModificationDossierResponse;
import fr.sword.xsd.solon.epg.CreerDossierRequest;
import fr.sword.xsd.solon.epg.CreerDossierResponse;
import fr.sword.xsd.solon.epg.DonnerAvisCERequest;
import fr.sword.xsd.solon.epg.DonnerAvisCEResponse;
import fr.sword.xsd.solon.epg.ModifierDossierCERequest;
import fr.sword.xsd.solon.epg.ModifierDossierCEResponse;
import fr.sword.xsd.solon.epg.ModifierDossierRequest;
import fr.sword.xsd.solon.epg.ModifierDossierResponse;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePEResponse;

public class WSEpgProxy extends AbstractWsProxy implements WSEpg {

	public WSEpgProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
		super(endpoint, basePath, username, password, keyAlias);
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
	public AttribuerNorResponse attribuerNor(AttribuerNorRequest request) throws Exception {
		return doPost(METHOD_NAME_ATTRIBUER_NOR, request, AttribuerNorResponse.class);
	}

	@Override
	public ChercherDossierResponse chercherDossierEpg(ChercherDossierRequest request) throws Exception {
		return doPost(METHOD_NAME_CHERCHER_DOSSIER, request, ChercherDossierResponse.class);
	}

	@Override
	public DonnerAvisCEResponse donnerAvisCE(DonnerAvisCERequest request) throws Exception {
		return doPost(METHOD_NAME_DONNER_AVIS_CE, request, DonnerAvisCEResponse.class);
	}

	@Override
	public ModifierDossierCEResponse modifierDossierCE(ModifierDossierCERequest request) throws Exception {
		return doPost(METHOD_NAME_MODIFIER_DOSSIER_CE, request, ModifierDossierCEResponse.class);
	}

	@Override
	public CreerDossierResponse creerDossier(CreerDossierRequest request) throws Exception {
		return doPost(METHOD_NAME_CREER_DOSSIER, request, CreerDossierResponse.class);
	}

	@Override
	public ModifierDossierResponse modifierDossier(ModifierDossierRequest request) throws Exception {
		return doPost(METHOD_NAME_MODIFIER_DOSSIER, request, ModifierDossierResponse.class);
	}

	@Override
	public ChercherModificationDossierResponse chercherModificationDossier(ChercherModificationDossierRequest request)
			throws Exception {
		return doPost(METHOD_NAME_CHERCHER_MODIFICATION_DOSSIER, request, ChercherModificationDossierResponse.class);
	}

	@Override
	protected String getServiceName() {
		return WSEpg.SERVICE_NAME;
	}

}
