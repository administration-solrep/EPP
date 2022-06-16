package fr.dila.solonepg.rest.api;

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
import fr.sword.xsd.solon.epg.ChercherModificationDossierResponse;
import fr.sword.xsd.solon.epg.ChercherModificationDossierRequest;

public interface WSEpg {

	static final String	SERVICE_NAME								= "WSepg";

	static final String	METHOD_NAME_TEST							= "test";
	static final String	METHOD_NAME_VERSION							= "version";

	static final String	METHOD_NAME_ATTRIBUER_NOR					= "attribuerNor";
	static final String	METHOD_NAME_CHERCHER_DOSSIER				= "chercherDossier";
	static final String	METHOD_NAME_DONNER_AVIS_CE					= "donnerAvisCE";
	static final String	METHOD_NAME_MODIFIER_DOSSIER_CE				= "modifierDossierCE";
	static final String	METHOD_NAME_CREER_DOSSIER					= "creerDossier";
	static final String	METHOD_NAME_MODIFIER_DOSSIER				= "modifierDossier";
	static final String	METHOD_NAME_CHERCHER_MODIFICATION_DOSSIER	= "chercherModificationDossier";

	String test() throws Exception;

	VersionResponse version() throws Exception;

	AttribuerNorResponse attribuerNor(AttribuerNorRequest request) throws Exception;

	ChercherDossierResponse chercherDossierEpg(ChercherDossierRequest request) throws Exception;

	DonnerAvisCEResponse donnerAvisCE(DonnerAvisCERequest request) throws Exception;

	ModifierDossierCEResponse modifierDossierCE(ModifierDossierCERequest request) throws Exception;

	CreerDossierResponse creerDossier(CreerDossierRequest request) throws Exception;

	ModifierDossierResponse modifierDossier(ModifierDossierRequest request) throws Exception;

	ChercherModificationDossierResponse chercherModificationDossier(ChercherModificationDossierRequest request)
			throws Exception;

}
