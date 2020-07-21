package fr.dila.solonepp.api.domain.tablereference;

import java.io.Serializable;

/**
 * Interface des objets métiers acteur.
 * 
 * @author jtremeaux
 */
public interface Acteur extends Serializable {
    
    /**
     * Retourne l'identifiant technique de l'enregistrement.
     * 
     * @return Identifiant technique
     */
    String getIdentifiant();
    
    
    /**
     * Renseigne l'identifiant technique de l'enregistrement.
     * 
     * @param identifiant Identifiant technique
     */
    void setIdentifiant(String identifiant);
}
