package fr.dila.solonepp.api.domain.tablereference;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface des objets métiers membre de groupe.
 * 
 * @author jtremeaux
 */
public interface MembreGroupe extends Serializable {
    
    
    /**
     * Retourne l'identifiant technique de l'enregistrement
     * @return
     */
    String getIdentifiant();
    
    
    /**
     * Renseigne l'identifiant technique de l'enregistrement
     * @param id
     */
    void setIdentifiant(String id);
    
    /**
     * Retourne l'identifiant technique de l'objet Mandat
     * @return identite l'identifiant technique de l'objet Mandat
     */
    String getMandat();

    /**
     * Retourne l'identifiant technique de l'objet Mandat
     * @param mandat identite l'identifiant technique de l'objet Mandat
     */
    void setMandat(String mandat);

    /**
     * Retourne  Date de début de l'adhésion au groupe
     * @return Date de début de l'adhésion au groupe
     */
    Calendar getDateDebut();


    /**
     * Renseigne date de début de l'adhésion au groupe
     * @return dateDebut Date de début de l'adhésion au groupe
     */
    void setDateDebut(Calendar dateDebut);

    /**
     * Retourne  Date de fin de l'adhésion au groupe
     * @return Date de fin de l'adhésion au groupe
     */
    Calendar getDateFin();

    /**
     * Renseigne date de fin de l'adhésion au groupe
     * @return dateFin Date de fin de l'adhésion au groupe
     */
    void setDateFin(Calendar dateFin);

    /**
     * Retourne  l'identifiant technique de l'organisme 
     * @return l'identifiant technique de l'organisme
     */
    String getOrganisme();
    
    /**
     * Renseigne l'identifiant technique de l'organisme
     * @param organisme l'identifiant technique de l'organisme
     */
    void setOrganisme(String organisme);


}
