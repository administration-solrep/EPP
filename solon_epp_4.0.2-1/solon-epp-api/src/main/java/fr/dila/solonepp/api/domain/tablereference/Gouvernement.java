package fr.dila.solonepp.api.domain.tablereference;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface des objets métiers gouvernement.
 *
 * @author jtremeaux
 */
public interface Gouvernement extends Serializable {
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
     * Retourne l'appellation du gouvernement
     * @return l'appellation du gouvernement
     */
    String getAppellation();

    /**
     * Renseigne l'appellation du gouvernement
     * @param appellation Appellation du gouvernement
     */
    void setAppellation(String appellation);

    /**
     * Retourne la date de début du gouvernement
     * @return la date de début du gouvernement
     */
    Calendar getDateDebut();

    /**
     * Renseigne la date de début du gouvernement
     * @param dateDebut date de début du gouvernement
     */
    void setDateDebut(Calendar dateDebut);

    /**
     * Retourne la date de fin du gouvernement
     * @return la date de fin du gouvernement
     */
    Calendar getDateFin();

    /**
     * Renseigne la date de fin du gouvernement
     * @param dateFin la date de fin du gouvernement
     */
    void setDateFin(Calendar dateFin);

    /**
     * Retourne true si le gouvernement a des ministeres attache
     * @return
     */
    Boolean isMinistereAttache();

    /**
     * Renseigne la presence de ministere attache
     * @param ministereAttache
     */
    void setMinistereAttache(Boolean ministereAttache);
}
