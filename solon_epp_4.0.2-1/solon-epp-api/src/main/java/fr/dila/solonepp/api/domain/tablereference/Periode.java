package fr.dila.solonepp.api.domain.tablereference;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface des objets métiers période.
 *
 * @author jtremeaux
 */
public interface Periode extends Serializable {
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
     * Retourne le type de période (PERIODE, LEGISLATURE)
     * @return le type de période (PERIODE, LEGISLATURE)
     */
    String getTypePeriode();

    /**
     * Renseigne le type de période (PERIODE, LEGISLATURE)
     * @param typePeriode le type de période   (PERIODE, LEGISLATURE)
     */
    void setTypePeriode(String typePeriode);

    /**
     * Retourne le propriétaire de l'objet
     * @return le propriétaire de l'objet
     */
    String getProprietaire();

    /**
     * Renseigne le propriétaire de l'objet
     * @param proprietaire le propriétaire de l'objet
     */
    void setProprietaire(String proprietaire);

    /**
     * Retourne le numéro de la période
     * @return le numéro de la période
     */
    String getNumero();

    /**
     * Renseigne le numéro de la période
     * @param numero le numéro de la période
     */
    void setNumero(String numero);

    /**
     * Retourne la date de début de la période
     * @return la date de début de la période
     */
    Calendar getDateDebut();

    /**
     * Renseigne la date de début de la période
     * @param dateDebut la date de début de la période
     */
    void setDateDebut(Calendar dateDebut);

    /**
     * Retourne la date de fin de la période
     * @return la date de fin de la période
     */
    Calendar getDateFin();

    /**
     * Renseigne la date de fin de la période
     * @param dateFin la date de fin de la période
     */
    void setDateFin(Calendar dateFin);
}
