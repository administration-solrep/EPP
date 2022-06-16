package fr.dila.solonepp.api.domain.tablereference;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface des objets métiers organisme.
 *
 * @author jtremeaux
 */
public interface Organisme extends Serializable {
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
     * Retourne le nom de l'organisme
     * @return le nom de l'organisme
     */
    String getNom();

    /**
     * Renseigne le nom de l'organisme
     * @param nom le nom de l'organisme
     */
    void setNom(String nom);

    /**
     * Retourne le type de l'organisme (OEP, Organe sénat, organe A.N., groupe sénat, groupe A.N.)
     * @return le type de l'organisme (OEP, Organe sénat, organe A.N., groupe sénat, groupe A.N.)
     */
    String getTypeOrganisme();

    /**
     * Renseigne le type de l'organisme (OEP, Organe sénat, organe A.N., groupe sénat, groupe A.N.)
     * @param typeOrganisme le type de l'organisme (OEP, Organe sénat, organe A.N., groupe sénat, groupe A.N.)
     */
    void setTypeOrganisme(String typeOrganisme);

    /**
     * Retourne le propriétaire de l'organisme (GOUVERNEMENT, AN, SENAT)
     * @return le propriétaire de l'organisme (GOUVERNEMENT, AN, SENAT)
     */
    String getProprietaire();

    /**
     * Renseigne le propriétaire de l'organisme (GOUVERNEMENT, AN, SENAT)
     * @param proprietaire le propriétaire de l'organisme (GOUVERNEMENT, AN, SENAT)
     */
    void setProprietaire(String proprietaire);

    /**
     * Retourne la date de création de l'organisme
     * @return la date de création de l'organisme
     */
    Calendar getDateDebut();

    /**
     * Renseigne la date de création de l'organisme
     * @param dateDebut la date de création de l'organisme
     */
    void setDateDebut(Calendar dateDebut);

    /**
     * Retoure la date de fin de l'organisme
     * @return la date de fin de l'organisme
     */
    Calendar getDateFin();

    /**
     * Renseigne la date de fin de l'organisme
     * @param dateFin la date de fin de l'organisme
     */
    void setDateFin(Calendar dateFin);

    /**
     * Retoure l'identifiant Commun de l'organisme
     * @return l'identifiant commun de l'organisme
     */
    String getIdCommun();

    /**
     * Renseigne l'identifiant commun de l'organisme
     * @param idCommun
     */
    void setIdCommun(String idCommun);

    /**
     * Retoure la base legale
     * @return la base legale
     */
    String getBaseLegale();

    /**
     * @param la base Legale
     */
    void setBaseLegale(String BaseLegale);
}
