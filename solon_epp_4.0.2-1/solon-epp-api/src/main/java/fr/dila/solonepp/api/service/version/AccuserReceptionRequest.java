package fr.dila.solonepp.api.service.version;

import fr.dila.solonepp.api.domain.evenement.NumeroVersion;

/**
 * Données de la requête pour accuser réception d'une version.
 *
 * @author jtremeaux
 */
public class AccuserReceptionRequest {
    /**
     * Identifiant technique de l'événement à accuser réception.
     */
    private String evenementId;

    /**
     * Numéro de la version à accuser réception.
     */
    private NumeroVersion numeroVersion;

    /**
     * Getter de evenementId.
     *
     * @return evenementId
     */
    public String getEvenementId() {
        return evenementId;
    }

    /**
     * Setter de evenementId.
     *
     * @param evenementId evenementId
     */
    public void setEvenementId(String evenementId) {
        this.evenementId = evenementId;
    }

    /**
     * Getter de numeroVersion.
     *
     * @return numeroVersion
     */
    public NumeroVersion getNumeroVersion() {
        return numeroVersion;
    }

    /**
     * Setter de numeroVersion.
     *
     * @param numeroVersion numeroVersion
     */
    public void setNumeroVersion(NumeroVersion numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AccuserReceptionRequest: ")
            .append("{evenementId: ")
            .append(evenementId)
            .append(", numeroVersion: ")
            .append(numeroVersion)
            .append("}");
        return sb.toString();
    }
}
