package fr.dila.st.api.service.organigramme;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.GouvernementNode;

public interface STGouvernementService {

	/**
	 * Retourne le gouvernement en cours.
	 * 
	 * @return gouvernement
	 * @throws ClientException
	 */
	GouvernementNode getCurrentGouvernement();

	/**
	 * Retourne un gouvernement
	 * 
	 * @param gouvernementId
	 *            id du gouvernement
	 * @return gouvernement
	 * @throws ClientException
	 */
	GouvernementNode getGouvernement(String gouvernementId) throws ClientException;

	/**
	 * retourne un documentModel ayant le schema organigramme-gouvernement
	 * 
	 * @return gouvernement
	 * @throws ClientException
	 */
	GouvernementNode getBareGouvernementModel();

	/**
	 * Enregistre le gouvernement
	 * 
	 * @param newGouvernement
	 *            gouvernement
	 * @throws ClientException
	 *             ClientException
	 */
	void createGouvernement(GouvernementNode newGouvernement) throws ClientException;

	/**
	 * update le gouvernement
	 * 
	 * @param gouvernement
	 * @throws ClientException
	 */
	void updateGouvernement(GouvernementNode gouvernement) throws ClientException;

	/**
	 * Renvoie les gouvernements actifs et à venir
	 * 
	 * @return
	 * @throws ClientException
	 */
	List<GouvernementNode> getGouvernementList();

	/**
	 * Set la date de fin du gouvernement courant à la date de début du nouveau gouvernement
	 * 
	 * @param currentGouvernement
	 * @param nextGouvernement
	 * @throws ClientException
	 */
	void setDateNewGvt(String currentGouvernement, String nextGouvernement) throws ClientException;
}
