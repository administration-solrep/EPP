package fr.dila.solonepp.api.domain.jeton;

import java.util.Calendar;
import java.util.List;

/**
 * Implémentation de l'objet métier JetonDoc.
 *
 * @author jtremeaux
 */
public interface JetonDoc extends fr.dila.st.api.jeton.JetonDoc {
    // *************************************************************
    // Propriétés du document JetonDoc.
    // *************************************************************
    /**
     * Retourne le type de notification.
     *
     * @return Type de notification
     */
    String getNotificationType();

    /**
     * Renseigne le type de notification.
     *
     * @param notificationType Type de notification
     */
    void setNotificationType(String notificationType);

    /**
     * Retourne le nombre d'essais restants de notifications WS ( > 0 si il faut réessayer).
     *
     * @return Nombre d'essais restants de notifications WS ( > 0 si il faut réessayer)
     */
    Long getWsRetryLeft();

    /**
     * Renseigne le nombre d'essais restants de notifications WS ( > 0 si il faut réessayer).
     *
     * @param created Nombre d'essais restants de notifications WS ( > 0 si il faut réessayer)
     */
    void setWsRetryLeft(Long wsRetryLeft);

    // *************************************************************
    // Propriétés spécifiques au flux de notification des tables de référence.
    // *************************************************************
    /**
     * Retourne l'identifiant technique de l'objet de référence mis à jour.
     *
     * @return Identifiant technique de l'objet de référence mis à jour
     */
    String getObjetRefId();

    /**
     * Renseigne l'identifiant technique de l'objet de référence mis à jour.
     *
     * @param objetRefId Identifiant technique de l'objet de référence mis à jour
     */
    void setObjetRefId(String objetRefId);

    /**
     * Retourne le type de l'objet de référence mis à jour (Acteur)....
     *
     * @return Type de l'objet de référence mis à jour (Acteur)...
     */
    String getObjetRefType();

    /**
     * Renseigne le type de l'objet de référence mis à jour (Acteur)....
     *
     * @param objetRefType Type de l'objet de référence mis à jour (Acteur)...
     */
    void setObjetRefType(String objetRefType);

    // *************************************************************
    // Propriétés spécifiques au flux de notification des événements.
    // *************************************************************
    /**
     * Retourne l'identifiant technique de l'événement.
     *
     * @return Identifiant technique de l'événement
     */
    String getEvenementId();

    /**
     * Renseigne l'identifiant technique de l'événement.
     *
     * @param evenementId Identifiant technique de l'événement
     */
    void setEvenementId(String evenementId);

    /**
     * Retourne le type d'événement.
     *
     * @return Type d'événement
     */
    String getEvenementType();

    /**
     * Renseigne le type d'événement.
     *
     * @param evenementType Type d'événement
     */
    void setEvenementType(String evenementType);

    /**
     * Retourne l'état du cycle de vie de l'événement.
     *
     * @return Etat du cycle de vie de l'événement
     */
    String getEvenementLifeCycleState();

    /**
     * Renseigne l'état du cycle de vie de l'événement.
     *
     * @param objetRefId Etat du cycle de vie de l'événement
     */
    void setEvenementLifeCycleState(String evenementLifeCycleState);

    /**
     * Retourne l'identifiant technique de l'émetteur (annuaire LDAP).
     *
     * @return Identifiant technique de l'émetteur (annuaire LDAP).
     */
    String getEvenementEmetteur();

    /**
     * Renseigne l'identifiant technique de l'émetteur (annuaire LDAP).
     *
     * @param evenementEmetteur Identifiant technique de l'émetteur (annuaire LDAP).
     */
    void setEvenementEmetteur(String evenementEmetteur);

    /**
     * Retourne l'identifiant technique du destinataire (annuaire LDAP).
     *
     * @return Identifiant technique du destinataire (annuaire LDAP).
     */
    String getEvenementDestinataire();

    /**
     * Renseigne l'identifiant technique du destinataire (annuaire LDAP).
     *
     * @param evenementDestinataire Identifiant technique du destinataire (annuaire LDAP).
     */
    void setEvenementDestinataire(String evenementDestinataire);

    /**
     * Retourne l'identifiant technique des destinataires en copie (annuaire LDAP).
     *
     * @return Identifiant technique des destinataires en copie (annuaire LDAP).
     */
    List<String> getEvenementDestinataireCopie();

    /**
     * Renseigne l'identifiant technique des destinataires en copie (annuaire LDAP).
     *
     * @param evenementDestinataireCopie Identifiant technique des destinataires en copie (annuaire LDAP).
     */
    void setEvenementDestinataireCopie(List<String> evenementDestinataireCopie);

    /**
     * Retourne la présence de pièces jointes.
     *
     * @return Présence de pièces jointes
     */
    boolean isVersionPresencePieceJointe();

    /**
     * Renseigne la présence de pièces jointes.
     *
     * @param versionPresencePieceJointe Présence de pièces jointes
     */
    void setVersionPresencePieceJointe(boolean versionPresencePieceJointe);

    /**
     * Retourne l'état du cycle de vie de la version.
     *
     * @return État du cycle de vie de la version
     */
    String getVersionLifeCycleState();

    /**
     * Renseigne l'état du cycle de vie de la version.
     *
     * @param versionLifeCycleState État du cycle de vie de la version
     */
    void setVersionLifeCycleState(String versionLifeCycleState);

    /**
     * Retourne l'objet de la version.
     *
     * @return Objet de la version
     */
    String getVersionObjet();

    /**
     * Renseigne l'objet de la version.
     *
     * @param versionObjet Objet de la version
     */
    void setVersionObjet(String versionObjet);

    /**
     * Retourne la date d'horodatage de la version.
     *
     * @return Date d'horodatage de la version
     */
    Calendar getVersionHorodatage();

    /**
     * Renseigne la date d'horodatage de la version.
     *
     * @param versionObjet Date d'horodatage de la version
     */
    void setVersionHorodatage(Calendar versionHorodatage);

    /**
     * Retourne le niveau de lecture.
     *
     * @return Niveau de lecture
     */
    String getVersionNiveauLecture();

    /**
     * Renseigne le niveau de lecture.
     *
     * @param versionNiveauLecture Niveau de lecture
     */
    void setVersionNiveauLecture(String versionNiveauLecture);

    /**
     * Retourne le niveau de lecture numéro.
     *
     * @return Niveau de lecture numéro
     */
    Long getVersionNiveauLectureNumero();

    /**
     * Renseigne le niveau de lecture numéro.
     *
     * @param versionNiveauLectureNumero Niveau de lecture numéro
     */
    void setVersionNiveauLectureNumero(Long versionNiveauLectureNumero);

    /**
     * Retourne le numéro de version majeur.
     *
     * @return Numéro de version majeur
     */
    Long getVersionMajorVersion();

    /**
     * Renseigne le numéro de version majeur.
     *
     * @param versionMajorVersion Numéro de version majeur
     */
    void setVersionMajorVersion(Long versionMajorVersion);

    /**
     * Retourne le numéro de version mineur.
     *
     * @return Numéro de version mineur
     */
    Long getVersionMinorVersion();

    /**
     * Renseigne le numéro de version mineur.
     *
     * @param versionMinorVersion Numéro de version mineur
     */
    void setVersionMinorVersion(Long versionMinorVersion);

    /**
     * Retourne l'identifiant sénat (chaîne libre renseignable uniquement par le sénat).
     *
     * @return Identifiant sénat (chaîne libre renseignable uniquement par le sénat)
     */
    String getVersionSenat();

    /**
     * Renseigne l'identifiant sénat (chaîne libre renseignable uniquement par le sénat).
     *
     * @param versionSenat Identifiant sénat (chaîne libre renseignable uniquement par le sénat)
     */
    void setVersionSenat(String versionSenat);

    /**
     * Retourne l'identifiant technique du dossier.
     *
     * @return Identifiant technique du dossier
     */
    String getDossierId();

    /**
     * Renseigne l'identifiant technique du dossier.
     *
     * @param dossierId Identifiant technique du dossier
     */
    void setDossierId(String dossierId);

    /**
     * Retourne le nombre d'alertes posées sur le dossier, si supérieur à 0 le dossier est "en alerte".
     *
     * @return Nombre d'alertes posées sur le dossier, si supérieur à 0 le dossier est "en alerte"
     */
    Long getDossierAlerteCount();

    /**
     * Renseigne le nombre d'alertes posées sur le dossier, si supérieur à 0 le dossier est "en alerte".
     *
     * @param dossierAlerteCount Nombre d'alertes posées sur le dossier, si supérieur à 0 le dossier est "en alerte"
     */
    void setDossierAlerteCount(Long dossierAlerteCount);

    /**
     * Retourne la liste des corbeilles de distribution du message.
     *
     * @return Liste des corbeilles de distribution du message
     */
    List<String> getMessageCorbeilleList();

    /**
     * Renseigne la liste des corbeilles de distribution du message.
     *
     * @param messageCorbeilleList Liste des corbeilles de distribution du message
     */
    void setMessageCorbeilleList(List<String> messageCorbeilleList);

    /**
     * Retourne l'état du cycle de vie du message.
     *
     * @return Etat du cycle de vie du message
     */
    String getMessageLifeCycleState();

    /**
     * Renseigne l'état du cycle de vie du message.
     *
     * @param messageLifeCycleState Etat du cycle de vie du message
     */
    void setMessageLifeCycleState(String messageLifeCycleState);

    /**
     * Retourne le type du message (EMETTEUR, DESTINATAIRE, COPIE).
     *
     * @return Type du message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    String getMessageType();

    /**
     * Renseigne le type du message (EMETTEUR, DESTINATAIRE, COPIE).
     *
     * @param evenementType Type du message (EMETTEUR, DESTINATAIRE, COPIE)
     */
    void setMessageType(String messageType);

    /**
     * Retourne vrai si les versions de cet événement nécessitent un accusé de réception.
     *
     * @return Vrai si les versions de cet événement nécessitent un accusé de réception
     */
    boolean isMessageArNecessaire();

    /**
     * Renseigne si les versions de cet événement nécessitent un accusé de réception.
     *
     * @param messageArNecessaire Vrai si les versions de cet événement nécessitent un accusé de réception
     */
    void setMessageArNecessaire(boolean messageArNecessaire);

    /**
     * Retourne le nombre de versions qui n'ont pas encore accusé réception par le destinataire.
     *
     * @return Nombre de versions qui n'ont pas encore accusé réception par le destinataire
     */
    Long getMessageArNonDonneCount();

    /**
     * Renseigne le nombre de versions qui n'ont pas encore accusé réception par le destinataire.
     *
     * @param messageLifeCycleState Nombre de versions qui n'ont pas encore accusé réception par le destinataire
     */
    void setMessageArNonDonneCount(Long messageArNonDonneCount);
}
