package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.domain.evenement.NumeroVersion;
import fr.dila.solonepp.api.dto.VersionSelectionDTO;
import fr.dila.solonepp.api.service.version.AccuserReceptionContext;
import fr.dila.solonepp.api.service.version.ValiderVersionContext;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les versions.
 *
 * @author jtremeaux
 * @author sly
 */
public interface VersionService extends Serializable {
    /**
     * Crée un nouveau document version (uniquement en mémoire).
     *
     * @param session Session
     * @return Nouveau document version
     */
    DocumentModel createBareVersion(CoreSession session);

    /**
     * Retourne la liste des versions visibles d'un événement pour un utilisateur, triées par numéro de version décroissant.
     *
     * @param session Session
     * @param evenementDoc document de l'événement
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     * @return Liste de documents versions
     */
    List<DocumentModel> findVersionVisible(CoreSession session, DocumentModel evenementDoc, String messageType);

    /**
     * Retourne une version spécifique d'un événement en tenant compte des droits de l'utilisateur.
     * Si la version n'est pas visible pour l'utilisateur, retourne null.
     *
     * @param session Session
     * @param evenementDoc document de l'événement
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     * @param numeroVersion Numéro de la version demandée
     * @return Document de la version spécifiée
     */
    DocumentModel getVersionVisible(
        CoreSession session,
        DocumentModel evenementDoc,
        String messageType,
        NumeroVersion numeroVersion
    );

    /**
     * Retourne la version active d'un événement en fonction des droits de l'utilisateur.
     *
     * @param session Session
     * @param evenementDoc Document de l'événement
     * @param messageType Type de message (EMETTEUR, DESTINATAIRE, COPIE)
     * @return Document de la version active
     */
    DocumentModel getVersionActive(CoreSession session, DocumentModel evenementDoc, String messageType);

    /**
     * Retourne la version brouillon d'un événement ultérieure à la version spécifiée.
     *
     * @param session Session
     * @param versionDoc Document version
     * @return Document de la version brouillon
     */
    DocumentModel getVersionBrouillonUlterieure(CoreSession session, DocumentModel versionDoc);

    /**
     * Retourne la dernière version, quels que soit son état et les droits de l'utilisateur.
     * Attention, cette méthode retourne toutes les versions, y compris les versions OBSOLETE,
     * REJETE, ABANDONNE.
     *
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @return Document version
     */
    DocumentModel getLastVersion(CoreSession session, String evenementId);

    /**
     * Retourne la dernière version dont le numéro est différent du numéro de version spécifié,
     * quels que soit son état et les droits de l'utilisateur.
     *
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @param numeroVersion Le numéro de version doit être différent de ce numéro
     * @return Document version
     */
    DocumentModel getLastVersionNotEquals(CoreSession session, String evenementId, NumeroVersion numeroVersion);

    /**
     * Crée la version initiale d'un événement à l'état brouillon.
     *
     * @param session Session
     * @param versionDoc Document version à créer
     * @param evenementDoc Document événement parent
     * @return Document version nouvellement créé
     */
    DocumentModel creerVersionBrouillonInitiale(
        CoreSession session,
        final DocumentModel versionDoc,
        final DocumentModel evenementDoc
    );

    /**
     * Crée une nouvelle version brouillon d'un événement pour complétion.
     *
     * @param session Session
     * @param versionDoc Document version à créer
     * @param evenementDoc Document événement parent
     * @param publie État initial de l'événement: publié (sinon brouillon)
     * @return Document version nouvellement créé
     */
    DocumentModel creerVersionBrouillonPourCompletion(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    );

    /**
     * Crée une nouvelle version brouillon d'un événement pour rectification.
     *
     * @param session Session
     * @param versionDoc Document version à créer
     * @param evenementDoc Document événement parent
     * @param publie État initial de l'événement: publié (sinon brouillon)
     * @return Document version nouvellement créé
     */
    DocumentModel creerVersionBrouillonPourRectification(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    );

    /**
     * Modifie une version brouillon existante.
     * Cette méthode de crée pas de nouvelle version.
     * Seule la version brouillon initiale peut être modifiée.
     *
     * @param session Session
     * @param currentVersionDoc Document version actuelle (à modifier)
     * @param newVersionDoc Document version portant les modifications à effectuer
     * @param evenementDoc Document événement parent
     * @return Version modifiée
     */
    DocumentModel modifierVersionBrouillonInitiale(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    );

    /**
     * Modifie une version brouillon existante.
     * Cette méthode de crée pas de nouvelle version.
     * Seule la version brouillon pour complétion peut être modifiée.
     *
     * @param session Session
     * @param currentVersionDoc Document version actuelle (à modifier)
     * @param newVersionDoc Document version portant les modifications à effectuer
     * @param evenementDoc Document événement parent
     * @return Version modifiée
     */
    DocumentModel modifierVersionBrouillonPourCompletion(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    );

    /**
     * Modifie une version brouillon existante.
     * Cette méthode de crée pas de nouvelle version.
     * Seule la version brouillon pour rectification peut être modifiée.
     *
     * @param session Session
     * @param currentVersionDoc Document version actuelle (à modifier)
     * @param newVersionDoc Document version portant les modifications à effectuer
     * @param evenementDoc Document événement parent
     * @return Version modifiée
     */
    DocumentModel modifierVersionBrouillonPourRectification(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    );

    /**
     * Supprime une version d'un événement.
     * Seule la dernière version à l'état brouillon peut être supprimée.
     *
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @param majorVersion Numéro de version majeur
     * @param minorVersion Numéro de version mineur
     */
    void supprimerVersionBrouillon(CoreSession session, String evenementId, Long majorVersion, Long minorVersion);

    /**
     * Supprime une version d'un événement.
     * Seule la dernière version à l'état brouillon peut être supprimée.
     *
     * @param session Session
     * @param evenementDoc Evénement à supprimer
     * @param versionDoc Version à supprimer
     */
    void supprimerVersionBrouillon(CoreSession session, DocumentModel evenementDoc, DocumentModel versionDoc);

    /**
     * Crée la version initiale directement à l'état publié.
     *
     * @param session Session
     * @param versionDoc Document version
     * @param dossierDoc Document dossier parent
     * @param evenementDoc Document événement parent
     * @return Document version publiée
     */
    DocumentModel publierVersionInitiale(
        CoreSession session,
        DocumentModel versionDoc,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc
    );

    /**
     * Publie la version brouillon actuelle
     * La version publiée doit être la version brouillon initiale.
     *
     * @param session Session
     * @param currentVersionDoc Document version actuelle (à publier)
     * @param newVersionDoc Document version portant les modifications à effectuer
     * @param dossierDoc Document dossier parent
     * @param evenementDoc Document événement parent
     * @return Document version publiée
     */
    DocumentModel publierVersionBrouillonInitiale(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc
    );

    /**
     * Publie une nouvelle version "pour complétion".
     * Cette version rend la version publiée précédente obsolète.
     * La version publiée doit être une version brouillon "pour complétion", ou une version publiée.
     *
     * @param session Session
     * @param currentVersionDoc Document version actuelle (à publier)
     * @param newVersionDoc Document version portant les modifications à effectuer
     * @param dossierDoc Document dossier parent
     * @param evenementDoc Document événement parent
     * @return Document version publiée
     */
    DocumentModel publierVersionPourCompletion(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc
    );

    /**
     * Publie une nouvelle version "pour rectification".
     * Cette version rend la version publiée précédente obsolète.
     * La version publiée doit être une version brouillon "pour pour rectification", ou une version publiée.
     *
     * @param session Session
     * @param currentVersionDoc Document version actuelle (à publier)
     * @param newVersionDoc Document version portant les modifications à effectuer
     * @param dossierDoc Document dossier parent
     * @param evenementDoc Document événement parent
     * @param modeDelta Publication en mode delta
     * @return Document version publiée
     */
    DocumentModel publierVersionPourRectification(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        boolean modeDelta
    );

    /**
     * Reporte les modification lors d'une complétion en mode delta sur un brouillon.
     * Cela déplace (renomme) l'ancienne version brouillon après la version qui vient d'être publiée.
     *
     * @param session Session
     * @param currentVersionDoc Document version actuelle (à publier)
     * @param newVersionDoc Document version portant les modifications à effectuer
     * @param evenementDoc Document événement parent
     * @return Document version publiée
     */
    DocumentModel reporterVersionBrouillonPourCompletion(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    );

    /**
     * Reporte les modification lors d'une rectification en mode delta sur un brouillon.
     * Cela déplace (renomme) l'ancienne version brouillon après la version qui vient d'être publiée.
     *
     * @param session Session
     * @param currentVersionDoc Document version actuelle (à publier)
     * @param newVersionDoc Document version portant les modifications à effectuer
     * @param evenementDoc Document événement parent
     * @return Document version publiée
     */
    DocumentModel reporterVersionBrouillonPourRectification(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    );

    /**
     * Accuse réception d'une version spécifiée ou de la version active.
     *
     * @param session Session
     * @param accuserReceptionContext Contexte de l'accusé de réception
     */
    void accuserReceptionDestinataire(CoreSession session, AccuserReceptionContext accuserReceptionContext);

    /**
     * Accuse réception de la version.
     *
     * @param session Session
     * @param evenementDoc Document événement
     * @param versionDoc Document version dont on accuse réception
     * @param checkArVersionAnterieure Vérifie si toutes les versions antérieures ont un accusé de réception
     */
    void accuserReceptionDestinataire(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        boolean checkArVersionAnterieure
    );

    /**
     * Valide / rejette la rectification d'une version.
     * Opération effectuée par le destinataire de l'événement.
     *
     * @param session Session
     * @param validerVersionContext Contexte de validation de version
     */
    void validerVersionDestinataire(CoreSession session, ValiderVersionContext validerVersionContext);

    /**
     * Abandonne la rectification d'une version.
     * Opération effectuée par l'émetteur de l'événement.
     *
     * @param session Session
     * @param validerVersionContext Contexte de validation de version
     */
    void validerVersionEmetteur(CoreSession session, ValiderVersionContext validerVersionContext);

    /**
     * retourne la liste des versions visibles par l'utilisateur
     * @param session
     * @param evenementDoc
     * @param messageType
     * @return
     */
    List<VersionSelectionDTO> findVersionSelectionnable(
        CoreSession session,
        DocumentModel evenementDoc,
        String messageType
    );

    /**
     * recherche de version par UUID
     * @param documentManager
     * @param id
     * @return
     */
    DocumentModel findVersionByUIID(CoreSession session, String id);

    /**
     * recherche la version publiée précedente pour comparaison
     *
     * @param session
     * @param evenementDoc
     * @param currentVersionDoc
     * @return
     */
    DocumentModel getVersionToCompare(CoreSession session, DocumentModel evenementDoc, DocumentModel currentVersionDoc);

    /**
     * Recherche la version courante (versionCourante = true dans la table version)
     *
     * @param session
     * @param evenementId
     * @return
     */
    DocumentModel getVersionCourante(CoreSession session, String evenementId);

    /**
     * Met la version courante (s'il y en a une) à false
     * @param session
     * @param evenementId
     */
    void setVersionCouranteToFalse(CoreSession session, String evenementId);
}
