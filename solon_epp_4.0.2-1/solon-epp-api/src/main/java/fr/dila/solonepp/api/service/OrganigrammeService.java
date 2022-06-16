package fr.dila.solonepp.api.service;

import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.user.STUser;
import java.util.List;

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
     */
    InstitutionNode getInstitution(String institutionId);

    /**
     * Retourne la liste des institutions auquelles est associé le poste.
     *
     * @param posteId
     *            Identifiant technique du poste
     * @return Liste d'institutions
     */
    List<InstitutionNode> getInstitutionParentFromPoste(String posteId);

    /**
     * Retourne la liste des utilisateurs appartenant à une institution.
     *
     * @param institutionId
     *            Identifiant technique de l'institution
     * @return Liste d'utilisateurs
     */
    List<STUser> getUserFromInstitution(String institutionId);

    /**
     * Retourne la liste des postes appartenant à une institution.
     *
     * @param institutionId
     *            Identifiant technique de l'institution
     * @return Liste de postes
     */
    List<PosteNode> getPosteFromInstitution(String institutionId);

    /**
     * Retourne la liste des utilisateurs appartenant à une institution et possédant une fonction unitaire.
     *
     * @param institutionId
     *            Identifiant technique de l'institution
     * @param baseFunctionId
     *            Identifiant technique de la fonction unitaire
     * @return Liste d'utilisateurs
     */
    List<STUser> getUserFromInstitutionAndBaseFunction(String institutionId, String baseFunctionId);

    List<InstitutionNode> getAllInstitutions();

    List<OrganigrammeNode> getFullParentList(OrganigrammeNode node);
}
