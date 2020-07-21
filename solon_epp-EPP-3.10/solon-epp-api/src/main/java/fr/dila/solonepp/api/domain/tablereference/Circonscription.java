package fr.dila.solonepp.api.domain.tablereference;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface des objets métiers circonscription.
 * 
 * @author jtremeaux
 */
public interface Circonscription extends Serializable {

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
    
    /**
     * Retourne l'institution propriétaire de la  circonscription.
     * 
     * @return Institution propriétaire de la  circonscription
     */
    String getProprietaire();

    /**
     * Renseigne l'institution propriétaire de la circonscription
     * 
     * @param proprietaire Institution propriétaire de la circonscription
     */
    void setProprietaire(String proprietaire);

    /**
     * Retourne le nom de la circonscription
     * @return Nom de la circonscription
     */
    String getNom();

    /**
     * Renseigne le nom de la circonscription
     * @param nom
     */
    void setNom(String nom);

    /**
     * Retourne la date de début du mandat.
     * 
     * @return Date de début du mandat
     */
    Calendar getDateDebut();

    /**
     * Renseigne la date de début du mandat.
     * 
     * @param dateDebut la date de début du mandat
     */
    void setDateDebut(Calendar dateDebut);

    /**
     * Retourne la date de fin.
     * 
     * @return Date de fin
     */
    Calendar getDateFin();

    /**
     * Renseigne la date de fin.
     * 
     * @param dateFin Date de fin
     */
    void setDateFin(Calendar dateFin);

}
