package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.service.evenement.AnnulerEvenementContext;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementContext;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les événements.
 *
 * @author jtremeaux
 */
public interface EvenementService extends Serializable {
    /**
     * Crée un nouveau document événement (uniquement en mémoire).
     *
     * @param session Session
     * @return Nouveau document événement
     */
    DocumentModel createBareEvenement(CoreSession session);

    /**
     * Retourne un événement par son identifiant technique.
     * Cette méthode n'échoue jamais, elle retourne null si l'événement n'existe pas.
     *
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @return Document événement
     */
    DocumentModel getEvenement(CoreSession session, String evenementId);

    /**
     * Retourne les événements successifs à un autre événement.
     *
     * @param session Session
     * @param evenementParentId Identifiant technique de l'événement
     * @return Liste des documents événement successifs
     */
    List<DocumentModel> findEvenementSuccessif(CoreSession session, String evenementParentId);

    /**
     * Crée un nouvel événement.
     *
     * @param session Session
     * @param evenementDoc Événement à créer
     * @param dossierDoc Document dossier parent
     * @param publie État initial de l'événement: publié (sinon brouillon)
     * @return Événement nouvellement créé
     */
    DocumentModel creerEvenement(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel dossierDoc,
        boolean publie
    );

    /**
     * Publie l'événement. L'événement est publié une seule fois lors de la transition de sa première version
     * de l'état brouillon à l'état publié.
     * La publication de l'événement entraine sa première (et unique) distribution vers le destinataire et
     * les destinataires en copie.
     *
     * @param session Session
     * @param evenementDoc Document événement
     */
    void publierEvenement(CoreSession session, DocumentModel evenementDoc);

    /**
     * Annule un événement (si celui-ci est à l'état PUBLIE), ou effectue une demande d'annulation
     * de l'événement (si celui-ci est à l'état EN_INSTANCE).
     *
     * @param session Session
     * @param annulerEvenementContext Contexte d'annulation de l'événement
     */
    void annulerEvenement(CoreSession session, AnnulerEvenementContext annulerEvenementContext);

    /**
     * Supprime un événement. L'événement est supprimé physiquement, et toutes les données liées à
     * l'événement (messages...) sont également supprimées.
     *
     * @param session Session
     * @param evenementDoc Document évenement
     */
    void supprimerEvenement(CoreSession session, DocumentModel evenementDoc);

    /**
     * Filtre les destinataires en copie de l'événement.
     * Retire de la liste des destinataires en copie l'émetteur le destinataire et les doublons.
     *
     * @param evenementDoc Evenement
     */
    void filterDestinataireCopie(DocumentModel evenementDoc);

    /**
     * Modifie les données d'un événement à l'état brouillon.
     *
     * @param session Session
     * @param currentEvenementDoc Document événement actuel (à modifier)
     * @param newEvenementDoc Document événement portant les modifications à effectuer
     * @return Événément modifié
     */
    DocumentModel modifierEvenementBrouillon(
        CoreSession session,
        DocumentModel currentEvenementDoc,
        DocumentModel newEvenementDoc
    );

    /**
     * Effectue les traitements après la création d'un événement de type alerte.
     *
     * @param session Session
     * @param evenementDoc Document événement créé
     * @param dossierDoc Document dossier
     * @param versionDoc Document version de l'alerte
     */
    void processEvenementAlerte(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel dossierDoc,
        DocumentModel versionDoc
    );

    /**
     * Initialisation d'un evenement successif
     * @param session
     * @param initialiserEvenementContext
     */
    void initialiserEvenement(CoreSession session, InitialiserEvenementContext initialiserEvenementContext);

    /**
     * Retourne les évènements racines du dossier
     *
     * @param session session
     * @param dossierDoc dossier
     * @return les évènements racines
     */
    List<DocumentModel> getEvenementsRacineDuDossier(CoreSession session, DocumentModel dossierDoc);

    /**
     * Retourne la liste de tous les évènements du dossier
     *
     * @param session session
     * @param dossierDoc dossier
     * @return liste de tous les évènements
     */
    List<DocumentModel> getEvenementDossierList(CoreSession session, DocumentModel dossierDoc);

    /**
     * Envoie d'une communication par mail avec les pièces jointes
     * @param session
     * @param expediteurId Identifiant de l'utilisateur expéditeur
     * @param objet
     * @param corps
     * @param destinataire
     * @param destinatairesCopie
     * @param evenementDoc
     * @param versionDoc
     * @param dossierDoc
     * @param pjDocList
     */
    void envoyerMel(
        CoreSession session,
        String expediteurId,
        String objet,
        String corps,
        String destinataire,
        String destinatairesCopie,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        DocumentModel dossierDoc,
        List<DocumentModel> pjDocList
    );

    /**
     * Vérifie la validité d'un identifiant d'évènement.
     *
     * @param idEvenement
     * @return
     */
    boolean isIdEvenementValid(String idEvenement);

    void envoyerMel(
        CoreSession session,
        String expediteurId,
        String objet,
        String corps,
        String destinataires,
        String copie,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        DocumentModel dossierDoc,
        List<DocumentModel> pjDocList,
        boolean mailWorkAfterCommit
    );
}
