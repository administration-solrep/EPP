package fr.dila.solonepp.api.domain.tablereference;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface des objets métiers ministère.
 *
 * @author jtremeaux
 */
public interface Ministere extends Serializable {
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
     * Retourne le nom du ministere
     * @return le nom du ministere
     */
    String getNom();

    /**
     * Renseigne le nom du ministere
     * @param nom le nom du ministere
     */
    void setNom(String nom);

    /**
     * Retourne le libellé du ministere
     * @return le libellé du ministere
     */
    String getLibelleMinistre();

    /**
     * Renseigne le libellé du ministere
     * @param libelleMinistre le libellé du ministere
     */
    void setLibelleMinistre(String libelleMinistre);

    /**
     * Retourne l'appellation du ministère
     * @return l'appellation du ministère
     */
    String getAppellation();

    /**
     * Renseigne l'appellation du ministère
     * @param appellation l'appellation du ministère
     */
    void setAppellation(String appellation);

    /**
     * Retourne la date de création du ministère
     * @return la date de création du ministère
     */
    Calendar getDateDebut();

    /**
     * Renseigne la date de création du ministère.
     *
     * @param dateDebut la date de création du ministère
     */
    void setDateDebut(Calendar dateDebut);

    /**
     * Retourne la date de fin de l'enregistrement (suppression logique).
     *
     * @return Date de fin de l'enregistrement (suppression logique)
     */
    Calendar getDateFin();

    /**
     * Renseigne la date de fin de l'enregistrement (suppression logique).
     *
     * @param dateFin Date de fin de l'enregistrement (suppression logique)
     */
    void setDateFin(Calendar dateFin);

    /**
     * Retourne l'Edition
     * @return l'édition
     */
    String getEdition();

    /**
     * Renseigne l'édition
     * @param edition l'édition
     */
    void setEdition(String edition);

    /**
     * Retourne l'identifiant du gouvernement du ministere
     * @return l'identifiant du gouvernmeent du ministere
     */
    String getGouvernement();

    /**
     * Renseigne l'identifiant du gouvernement du ministere
     * @param gouvernement l'identifiant du gouvernement du ministere
     */
    void setGouvernement(String gouvernement);

    /**
     * Retourne true si le ministere a des mandats attache
     * @return
     */
    Boolean isMandatAttache();

    /**
     * Renseigne la presence de mandats attache
     * @param mandatAttache
     */
    void setMandatAttache(Boolean mandatAttache);
}
