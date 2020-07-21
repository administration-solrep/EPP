package fr.dila.solonepg.rest.stub;

import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.solonepg.rest.api.WSEpg;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epg.AttribuerNorRequest;
import fr.sword.xsd.solon.epg.AttribuerNorResponse;
import fr.sword.xsd.solon.epg.ChercherDossierRequest;
import fr.sword.xsd.solon.epg.ChercherDossierResponse;
import fr.sword.xsd.solon.epg.DonnerAvisCERequest;
import fr.sword.xsd.solon.epg.DonnerAvisCEResponse;
import fr.sword.xsd.solon.epg.ModifierDossierCERequest;
import fr.sword.xsd.solon.epg.ModifierDossierCEResponse;
import fr.sword.xsd.solon.epg.CreerDossierRequest;
import fr.sword.xsd.solon.epg.CreerDossierResponse;
import fr.sword.xsd.solon.epg.ModifierDossierRequest;
import fr.sword.xsd.solon.epg.ModifierDossierResponse;
import fr.sword.xsd.solon.epg.ChercherModificationDossierRequest;
import fr.sword.xsd.solon.epg.ChercherModificationDossierResponse;

public class WSEpgStub implements WSEpg {

	private static final String	FILE_BASEPATH												= "fr/dila/st/rest/stub/epg/wsepg/";

	public static final String	ATTRIBUER_NOR_REQUEST										= FILE_BASEPATH
																									+ "attribuernor/WSepg_attribuerNorRequest.xml";
	public static final String	ATTRIBUER_NOR_RESPONSE										= FILE_BASEPATH
																									+ "attribuernor/WSepg_attribuerNorResponse.xml";

	public static final String	CHERCHER_DOSSIER_REQUEST									= FILE_BASEPATH
																									+ "chercherdossier/WSepg_chercherDossierRequest.xml";
	public static final String	CHERCHER_DOSSIER_REQUEST_BYNOR								= FILE_BASEPATH
																									+ "chercherdossier/WSepg_chercherDossierRequest_byNor.xml";
	public static final String	CHERCHER_DOSSIER_RESPONSE									= FILE_BASEPATH
																									+ "chercherdossier/WSepg_chercherDossierResponse.xml";

	public static final String	RECHERCHER_DOSSIER_REQUEST									= FILE_BASEPATH
																									+ "rechercherDossier/WSepg_rechercherDossierRequest.xml";
	public static final String	RECHERCHER_DOSSIER_RESPONSE									= FILE_BASEPATH
																									+ "rechercherDossier/WSepg_rechercherDossierResponse.xml";

	public static final String	DONNER_AVIS_CE_REQUEST										= FILE_BASEPATH
																									+ "donneravisce/WSepg_donnerAvisCERequest.xml";
	public static final String	DONNER_AVIS_CE_RESPONSE										= FILE_BASEPATH
																									+ "donneravisce/WSepg_donnerAvisCEResponse.xml";

	public static final String	MODIFIER_DOSSIER_CE_REQUEST									= FILE_BASEPATH
																									+ "modifierDossierCE/WSepg_modifierDossierCERequest.xml";
	public static final String	MODIFIER_DOSSIER_CE_RESPONSE								= FILE_BASEPATH
																									+ "modifierDossierCE/WSepg_modifierDossierCEResponse.xml";

	public static final String	CREER_DOSSIER_REQUEST										= FILE_BASEPATH
																									+ "creerDossier/WSepg_creerDossierRequest.xml";
	public static final String	CREER_DOSSIER_RESPONSE										= FILE_BASEPATH
																									+ "creerDossier/WSepg_creerDossierResponse.xml";

	public static final String	MODIFIER_DOSSIER_REQUEST									= FILE_BASEPATH
																									+ "modifierDossier/WSepg_modifierDossierRequest.xml";
	public static final String	MODIFIER_DOSSIER_RESPONSE									= FILE_BASEPATH
																									+ "modifierDossier/WSepg_modifierDossierResponse.xml";

	public static final String	CHERCHER_MODIFICATION_DOSSIER_REQUEST						= FILE_BASEPATH
																									+ "chercherModificationDossier/WSepg_chercherModificationDossierRequest.xml";
	public static final String	CHERCHER_MODIFICATION_DOSSIER_RESPONSE_PIECE_COMPLEMENTAIRE	= FILE_BASEPATH
																									+ "chercherModificationDossier/WSepg_chercherModificationDossierResponse_PIECE_COMPLEMENTAIRE.xml";

	@Override
	public String test() throws Exception {
		return SERVICE_NAME;
	}

	@Override
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSsolonEpg();
	}

	@Override
	public AttribuerNorResponse attribuerNor(AttribuerNorRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(ATTRIBUER_NOR_RESPONSE, AttribuerNorResponse.class);
	}

	@Override
	public ChercherDossierResponse chercherDossierEpg(ChercherDossierRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_DOSSIER_RESPONSE, ChercherDossierResponse.class);
	}

	@Override
	public DonnerAvisCEResponse donnerAvisCE(DonnerAvisCERequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(DONNER_AVIS_CE_RESPONSE, DonnerAvisCEResponse.class);
	}

	@Override
	public ModifierDossierCEResponse modifierDossierCE(ModifierDossierCERequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(MODIFIER_DOSSIER_CE_RESPONSE, ModifierDossierCEResponse.class);
	}

	@Override
	public CreerDossierResponse creerDossier(CreerDossierRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CREER_DOSSIER_RESPONSE, CreerDossierResponse.class);
	}

	@Override
	public ModifierDossierResponse modifierDossier(ModifierDossierRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(MODIFIER_DOSSIER_RESPONSE, ModifierDossierResponse.class);
	}

	@Override
	public ChercherModificationDossierResponse chercherModificationDossier(ChercherModificationDossierRequest request)
			throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_MODIFICATION_DOSSIER_RESPONSE_PIECE_COMPLEMENTAIRE,
				ChercherModificationDossierResponse.class);
	}

}
