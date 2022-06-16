package fr.dila.st.api.service.organigramme;

import fr.dila.st.api.organigramme.GouvernementNode;
import java.util.List;

public interface STGouvernementService {
    /**
     * Retourne le gouvernement en cours.
     *
     * @return gouvernement
     *
     */
    GouvernementNode getCurrentGouvernement();

    /**
     * Retourne un gouvernement
     *
     * @param gouvernementId
     *            id du gouvernement
     * @return gouvernement
     *
     */
    GouvernementNode getGouvernement(String gouvernementId);

    /**
     * retourne un gouvernement
     *
     * @return gouvernement
     *
     */
    GouvernementNode getBareGouvernementModel();

    /**
     * Enregistre le gouvernement
     *
     * @param newGouvernement
     *            gouvernement
     *
     *             ClientException
     */
    void createGouvernement(GouvernementNode newGouvernement);

    /**
     * update le gouvernement
     *
     * @param gouvernement
     *
     */
    void updateGouvernement(GouvernementNode gouvernement);

    /**
     * Renvoie tous les gouvernements
     *
     * @return
     *
     */
    List<GouvernementNode> getGouvernementList();

    /**
     * Renvoie les gouvernements actifs et à venir
     *
     * @return
     * @throws ClientException
     */
    List<GouvernementNode> getActiveGouvernementList();

    /**
     * Set la date de fin du gouvernement courant à la date de début du nouveau gouvernement
     *
     * @param currentGouvernement
     * @param nextGouvernement
     *
     */
    void setDateNewGvt(String currentGouvernement, String nextGouvernement);
}
