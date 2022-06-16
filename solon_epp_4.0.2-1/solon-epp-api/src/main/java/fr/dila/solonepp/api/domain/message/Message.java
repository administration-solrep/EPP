package fr.dila.solonepp.api.domain.message;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface des objets métiers message.
 *
 * @author jtremeaux
 */
public interface Message extends Serializable {
    // *************************************************************
    // Propriété de Nuxeo (schéma case_link)
    // *************************************************************
    /**
     * Retourne le titre du document.
     *
     * @return Titre du document
     */
    String getTitle();

    /**
     * Renseigne le titre du document.
     *
     * @param title Titre du document
     */
    void setTitle(String title);

    String getCaseDocumentId();

    void setCaseDocumentId(String caseDocumentId);

    String getSenderMailboxId();

    void setSenderMailboxId(String senderMailboxId);

    Calendar getDate();

    void setDate(Calendar date);

    String getSender();

    void setSender(String sender);

    DocumentModel getDocument();

    // *************************************************************
    // Propriétés du message
    // *************************************************************
    /**
     * Retourne le type du message (EMETTEUR, DESTINATAIRE, COPIE).
     *
     * @return Type du message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    String getMessageType();

    /**
     * Renseigne le type du message (EMETTEUR, DESTINATAIRE, COPIE).
     *
     * @param messageType Type du message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    void setMessageType(String messageType);

    /**
     * Retourne l'UUID de la version active associée au message.
     *
     * @return UUID de la version active
     */
    String getActiveVersionId();

    /**
     * Renseigne l'UUID de la version active associée au dossier.
     *
     * @param activeVersionId UUID de la version active
     */
    void setActiveVersionId(String activeVersionId);

    /**
     * Retourne la liste des corbeilles de distribution du message.
     *
     * @return Liste des corbeilles de distribution du message
     */
    List<String> getCorbeilleList();

    /**
     * Renseigne la liste des corbeilles de distribution du message.
     *
     * @param corbeilleList Liste des corbeilles de distribution du message
     */
    void setCorbeilleList(List<String> corbeilleList);

    /**
     * Date de traitement du message
     *
     * @return
     */
    Calendar getDateTraitement();

    /**
     * Date de traitement du message
     *
     * @param dateTraitement
     */
    void setDateTraitement(Calendar dateTraitement);

    /**
     * Retourne l'état du message (NON_TRAITE, EN_COURS_TRAITEMENT, TRAITE, EN_COURS_REDACTION, EN_ATTENTE_AR, EMIS, AR_RECU).
     *
     * @return état du message
     */
    String getEtatMessage();

    /**
     * Renseigne l'état du message (NON_TRAITE, EN_COURS_TRAITEMENT, TRAITE, EN_COURS_REDACTION, EN_ATTENTE_AR, EMIS, AR_RECU).
     *
     * @param etatMessage état du message
     */
    void setEtatMessage(String etatMessage);

    // *************************************************************
    // Propriétés du message de l'émetteur uniquement
    // *************************************************************
    /**
     * Retourne vrai si le destinataire doit fournir des AR pour les versions de ce message.
     *
     * @return Vrai si le destinataire doit fournir des AR pour les versions de ce message
     */
    boolean isArNecessaire();

    /**
     * Renseigne si le destinataire doit fournir des AR pour les versions de ce message.
     *
     * @param arNecessaire Vrai si le destinataire doit fournir des AR pour les versions de ce message
     */
    void setArNecessaire(boolean arNecessaire);

    /**
     * Retourne le nombre d'AR non donnés par le destinataire du message.
     *
     * @return Nombre d'AR non donnés par le destinataire du message
     */
    long getArNonDonneCount();

    /**
     * Renseigne le nombre d'AR non donnés par le destinataire du message.
     *
     * @param arNonDonneCount Nombre d'AR non donnés par le destinataire du message
     */
    void setArNonDonneCount(long arNonDonneCount);

    // *************************************************************
    // Propriétés calculées sur l'état du cycle de vie.
    // *************************************************************
    /**
     * Retourne vrai si l'événement est à l'état du cycle de vie non traité.
     *
     * @return État du cycle de vie init
     */
    boolean isEtatNonTraite();

    /**
     * Retourne vrai si l'événement est à l'état du cycle de vie en cours.
     *
     * @return État du cycle de vie en cours
     */
    boolean isEtatEnCours();

    /**
     * Retourne vrai si l'événement est à l'état du cycle de vie traité.
     *
     * @return État du cycle de vie traité
     */
    boolean isEtatTraite();

    // *************************************************************
    // Propriétés calculées sur le type de message.
    // *************************************************************
    /**
     * Retourne vrai si le message est de type émetteur.
     *
     * @return Message de type émetteur
     */
    boolean isTypeEmetteur();

    /**
     * Retourne vrai si le message est de type destinataire.
     *
     * @return Message de type destinataire
     */
    boolean isTypeDestinataire();

    /**
     * Retourne vrai si le message est de type copie.
     *
     * @return Message de type copie
     */
    boolean isTypeCopie();

    // *************************************************************
    // Visa interne SOLEX
    // *************************************************************

    void setVisaInternes(List<String> visaInternes);

    List<String> getVisaInternes();

    void setIdEvenement(String title);

    String getIdEvenement();

    void setIdDossier(String title);

    String getIdDossier();
}
