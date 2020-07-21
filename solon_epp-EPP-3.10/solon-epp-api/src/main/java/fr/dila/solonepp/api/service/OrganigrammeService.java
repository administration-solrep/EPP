package fr.dila.solonepp.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.user.STUser;

/**
 * Implémentation du service d'organigramme pour SOLON EPP.
 * 
 * @author jtremeaux
 */
public interface OrganigrammeService extends fr.dila.st.api.service.organigramme.OrganigrammeService {

	/**
	 * Retourne un document entité par son identifiant technique.
	 * 
	 * @param institutionId
	 *            Identifiant technique de l'institution
	 * @return Document institution
	 * @throws ClientException
	 */
	InstitutionNode getInstitution(String institutionId) throws ClientException;

	/**
	 * Retourne la liste des institutions auquelles est associé le poste.
	 * 
	 * @param posteId
	 *            Identifiant technique du poste
	 * @return Liste d'institutions
	 * @throws ClientException
	 */
	List<InstitutionNode> getInstitutionParentFromPoste(String posteId) throws ClientException;

	/**
	 * Retourne la liste des utilisateurs appartenant à une institution.
	 * 
	 * @param institutionId
	 *            Identifiant technique de l'institution
	 * @return Liste d'utilisateurs
	 * @throws ClientException
	 */
	List<STUser> getUserFromInstitution(String institutionId) throws ClientException;

	/**
	 * Retourne la liste des postes appartenant à une institution.
	 * 
	 * @param institutionId
	 *            Identifiant technique de l'institution
	 * @return Liste de postes
	 * @throws ClientException
	 */
	List<PosteNode> getPosteFromInstitution(String institutionId) throws ClientException;

	/**
	 * Retourne la liste des utilisateurs appartenant à une institution et possédant une fonction unitaire.
	 * 
	 * @param institutionId
	 *            Identifiant technique de l'institution
	 * @param baseFunctionId
	 *            Identifiant technique de la fonction unitaire
	 * @return Liste d'utilisateurs
	 * @throws ClientException
	 */
	List<STUser> getUserFromInstitutionAndBaseFunction(String institutionId, String baseFunctionId)
			throws ClientException;

	List<InstitutionNode> getAllInstitutions() throws ClientException;
}
