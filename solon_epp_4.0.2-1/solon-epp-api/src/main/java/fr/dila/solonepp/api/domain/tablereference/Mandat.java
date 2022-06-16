package fr.dila.solonepp.api.domain.tablereference;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface des objets métiers mandat.
 *
 * @author jtremeaux
 */
public interface Mandat extends Serializable {
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
     * Retourne le nom du proprietaire
     * @return le nom du propriétaire
     */
    String getProprietaire();

    /**
     * Renseigne le nom du propriétaire
     * @param proprietaire nom du propriétaire
     */
    void setProprietaire(String proprietaire);

    /**
     * Retourne le type de Mandat
     * @return type de mandat
     */
    String getTypeMandat();

    /**
     * Renseigne le type de Mandat
     * @param typeMandat le type de mandat (DEPUTE, SENATEUR, POUVOIR_EXECUTIF, PRESIDENCE_REPUBLIQUE, HAUT_COMMISSAIRE,
    SECRETARIAT_ETAT, MINISTERE)
     */
    void setTypeMandat(String typeMandat);

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
     * Retourne la date de fin du mandat.
     *
     * @return la date de fin du mandat
     */
    Calendar getDateFin();

    /**
     * Renseigne la date de fin du mandat.
     *
     * @param dateFin la date de fin du mandat
     */
    void setDateFin(Calendar dateFin);

    /**
     * Retourne l'ordre protocolaire  (uniquement pour POUVOIR_EXECUTIF et sous-types)
     */
    Long getOrdreProtocolaire();

    /**
     * Renseigne l'ordre protocolaire (uniquement pour POUVOIR_EXECUTIF et sous-types)
     * @param ordreProtocolaire l'ordre protocolaire
     */
    void setOrdreProtocolaire(Long ordreProtocolaire);

    /**
     * retourne le Titre (uniquement pour POUVOIR_EXECUTIF et sous-types)
     * @return titre
     */
    String getTitre();

    /**
     * Renseigne le Titre (uniquement pour POUVOIR_EXECUTIF et sous-types)
     * @param titre
     */
    void setTitre(String titre);

    /**
     * Retourne l'identifiant technique de l'objet Identité
     * @return l'identifiant technique de l'objet Identité
     */
    String getIdentite();

    /**
     * Renseigne l'identifiant technique de l'objet Identité
     * @param identite l'identifiant technique de l'objet Identité
     */
    void setIdentite(String identite);

    /**
     * Retourne l'identifiant technique de l'objet Ministere
     * @return l'identifiant technique de l'objet Ministere
     */
    String getMinistere();

    /**
     * Renseigne l'identifiant technique de l'objet Ministere
     * @param  ministere l'identifiant technique de l'objet Ministere
     */
    void setMinistere(String ministere);

    /**
     * Retourne l'identifiant technique de l'objet Circonscription
     * @return l'identifiant technique de l'objet Circonscription
     */
    String getCirconscription();

    /**
     * Renseigne l'identifiant technique de l'objet Circonscription
     * @param  circonscription l'identifiant technique de l'objet Circonscription
     */
    void setCirconscription(String circonscription);

    /**
     * retourne l'appellation
     * @return appellation
     */
    String getAppellation();

    /**
     * Renseigne l'appellation
     * @param appellation
     */
    void setAppellation(String appellation);

    /**
     * retourne le nor associé au mandat
     */
    String getNor();

    /**
     * Renseigne le nor associé au mandat
     */
    void setNor(String nor);
}
