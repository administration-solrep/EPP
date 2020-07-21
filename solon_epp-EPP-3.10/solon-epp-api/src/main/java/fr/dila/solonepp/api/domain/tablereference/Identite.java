package fr.dila.solonepp.api.domain.tablereference;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Interface des objets métiers identité.
 * 
 * @author jtremeaux
 */
public interface Identite extends Serializable {

    /**
     * Retourne l'identifiant technique de l'enregistrement
     * 
     * @return
     */
    String getIdentifiant();

    /**
     * Renseigne l'identifiant technique de l'enregistrement
     * 
     * @param id
     */
    void setIdentifiant(String id);

    /**
     * Retourne le nom de l'identité
     * 
     * @return le nom de l'identité
     */
    String getNom();

    /**
     * Renseigne le nom de l'identité
     * 
     * @param nom le nom de l'identité
     */
    void setNom(String nom);

    /**
     * Retourne le prénom
     * 
     * @return le prénom
     */
    String getPrenom();

    /**
     * Renseigne le prénom
     * 
     * @param prenom le prénom
     */
    void setPrenom(String prenom);

    /**
     * Retourne la civilité
     * 
     * @return la civilité
     */
    String getCivilite();

    /**
     * Renseigne la civilité
     * 
     * @param civilite la civilité
     */
    void setCivilite(String civilite);

    /**
     * Retourne la dateDebut
     * 
     * @return la dateDebut
     */
    Calendar getDateDebut();

    /**
     * Renseigne la date de début
     * 
     * @param dateDebut la date de début
     */
    void setDateDebut(Calendar dateDebut);

    /**
     * Retourne la date de fin de l'identité
     * 
     * @return la date de fin de l'identité
     */
    Calendar getDateFin();

    /**
     * Renseigne la date de Fin de l'identité
     * 
     * @param dateFin date de fin de l'identité
     */

    void setDateFin(Calendar dateFin);

    /**
     * Retourne la date de naissance de l'identité
     * 
     * @return la date de naissanec
     */
    Calendar getDateNaissance();

    /**
     * Renseigne la date de naissance de l'identité
     * 
     * @param dateNaissance de l'identité
     */
    void setDateNaissance(Calendar dateNaissance);

    /**
     * Retourne le lieu de naissance de l'identité
     * 
     * @return le lieu de naissance de l'identité
     */
    String getLieuNaissance();

    /**
     * Renseigne le lieu de naissance de l'identité
     * 
     * @param lieuNaissance lieu de naissance de l'identité
     */
    void setLieuNaissance(String lieuNaissance);

    /**
     * Retourne le département de naissance de l'identité
     * 
     * @return le département de naissance de l'identité
     */
    String getDepartementNaissance();

    /**
     * Renseigne le departement de naissance de l'identité
     * 
     * @param departementNaissance le département de naissance
     */
    void setDepartementNaissance(String departementNaissance);

    /**
     * Retoune le pays de naissance de l'identité
     * 
     * @return le pays de naissance
     */
    String getPaysNaissance();

    /**
     * Renseigne le pays de naissance de l'identité
     * 
     * @param paysNaissance le pays de naissance
     */
    void setPaysNaissance(String paysNaissance);

    /**
     * Retourne le nom complet (civilite prenom nom)
     * 
     * @return
     */
    String getFullName();

    /**
     * Retourne l'identifiant technique de l'objet Acteur
     * 
     * @return l'identifiant technique de l'objet Identité
     */
    String getActeur();

    /**
     * Renseigne l'identifiant technique de l'objet Acteur
     * 
     * @param acteur l'identifiant technique de l'objet Identité
     */
    void setActeur(String acteur);

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

}
