package fr.dila.solonepp.api.service.evenement;

import java.util.List;

/**
 * Données de la requête pour maj du visa interne
 *
 * @author asatre
 */
public class MajInterneRequest {
    private String idEvenement;
    private List<String> interne;

    public String getIdEvenement() {
        return idEvenement;
    }

    public void setIdEvenement(String idEvenement) {
        this.idEvenement = idEvenement;
    }

    public void setInterne(List<String> interne) {
        this.interne = interne;
    }

    public List<String> getInterne() {
        return interne;
    }
}
