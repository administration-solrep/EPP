package fr.dila.solonepg.rest.client;

import javax.xml.bind.JAXBException;

import fr.dila.solonepg.rest.api.WSSpe;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePERequest;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePEResponse;
import fr.sword.xsd.solon.spe.PEDemandeType;
import fr.sword.xsd.solon.spe.PEstatut;

/**
 * Classe permettant l'envoi de la requete de demande de publication / epreuvage.
 * 
 * @author arolin
 * 
 */
public class WSSpeCaller {

	private final WSSpe	speService;

	public WSSpeCaller(WSSpe speService) {
		super();
		this.speService = speService;
	}

	public static EnvoyerPremiereDemandePERequest buildRequest(PEDemandeType peDemandeType, String jeton) {

		EnvoyerPremiereDemandePERequest enr = new EnvoyerPremiereDemandePERequest();

		enr.setTypeDemande(peDemandeType);

		return enr;

	}

	public EnvoyerPremiereDemandePEResponse envoyerDemandePublication(EnvoyerPremiereDemandePERequest request)
			throws Exception {

		// --- handle transaction
		EnvoyerPremiereDemandePEResponse response;

		response = speService.envoyerPremiereDemandePE(request);

		// --- check response
		if (response == null || response.getStatus() == null) {

			throw new WSSpeCallerException("null response to envoiDemandePublication");
		}

		PEstatut ts = response.getStatus();

		if (ts != PEstatut.OK) {
			StringBuffer sb = new StringBuffer();
			sb.append("bad response status " + TraitementStatut.OK.toString());

			try {
				sb.append("response was \n");
				sb.append(JaxBHelper.marshallToString(response, EnvoyerPremiereDemandePEResponse.class));
			} catch (JAXBException e) {
				sb.append("  could not unmarshall response");
			}
			throw new WSSpeCallerException(sb.toString());
		}

		return response;
	}

}
