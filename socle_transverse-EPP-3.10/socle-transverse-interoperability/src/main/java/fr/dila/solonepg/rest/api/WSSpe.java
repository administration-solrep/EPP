package fr.dila.solonepg.rest.api;

import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePERequest;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePEResponse;
import fr.sword.xsd.solon.spe.EnvoyerRetourPERequest;
import fr.sword.xsd.solon.spe.EnvoyerRetourPEResponse;

public interface WSSpe {

	public static final String	SERVICE_NAME							= "WSspe";

	public static final String	METHOD_NAME_TEST						= "test";
	public static final String	METHOD_NAME_VERSION						= "version";

	public static final String	METHOD_NAME_ENVOYER_PREMIERE_DEMANDE_PE	= "envoyerPremiereDemandePE";
	public static final String	METHOD_NAME_ENVOYER_RETOUR_PE			= "envoyerRetourPE";

	public String test() throws Exception;

	public VersionResponse version() throws Exception;

	EnvoyerPremiereDemandePEResponse envoyerPremiereDemandePE(EnvoyerPremiereDemandePERequest request) throws Exception;

	EnvoyerRetourPEResponse envoyerRetourPE(EnvoyerRetourPERequest request) throws Exception;

}
