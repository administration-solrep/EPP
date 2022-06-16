package fr.dila.ss.api.feuilleroute;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.io.Serializable;

/**
 * Interface des feuilles de route commune à Réponses et SOLON EPG.
 *
 * @author jtremeaux
 */
public interface SSFeuilleRoute extends FeuilleRoute, Serializable {
    // *************************************************************
    // Propriétés du document.
    // *************************************************************
    /**
     * Retourne le titre de l'événement.
     *
     * @return Titre de l'événement
     */
    String getTitle();

    /**
     * Renseigne le titre de l'événement.
     *
     * @param title
     *            Titre de l'événement
     */
    void setTitle(String title);

    /**
     * Renseigne le créateur du document.
     *
     * @param creator
     *            Créateur
     */
    void setCreator(String creator);

    // *************************************************************
    // Propriétés de la feuille de route (socle transverse).
    // *************************************************************
    /**
     * Retourne l'attribut feuille de route par défaut.
     *
     * @return Feuille de route par défaut
     */
    boolean isFeuilleRouteDefaut();

    /**
     * Renseigne l'attribut feuille de route par défaut.
     *
     * @param feuilleRouteDefaut
     *            Feuille de route par défaut
     */
    void setFeuilleRouteDefaut(boolean feuilleRouteDefaut);

    /**
     * Retourne l'identifiant technique du ministère.
     *
     * @return Identifiant technique du ministère
     */
    String getMinistere();

    /**
     * Renseigne l'identifiant technique du ministère.
     *
     * @param ministere
     *            Identifiant technique du ministère
     */
    void setMinistere(String ministere);

    /**
     * Retourne l'état de demande de validation de la feuille de route.
     *
     * @return État de demande de validation
     */
    boolean isDemandeValidation();

    /**
     * Renseigne l'état de demande de validation de la feuille de route.
     *
     * @param demandeValidation État
     *            de demande de validation
     */
    void setDemandeValidation(boolean demandeValidation);

    /**
     * Retourne le type de creation a l'origine de la feuille de route
     *
     */
    String getTypeCreation();

    /**
     * Renseigne l'information sur l'origine de la création de la feuille de route
     *
     * @param typeCreation Nom du type de creation
     */
    void setTypeCreation(String typeCreation);

    // *************************************************************
    // Propriétés calculées.
    // *************************************************************
    /**
     * Retourne vrai si la feuille de route est un modèle.
     *
     */
    boolean isModel();
}
