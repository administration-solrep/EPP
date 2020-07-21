package fr.dila.solonepp.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.service.evenement.AnnulerEvenementContext;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementContext;

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
     * @throws ClientException
     */
    DocumentModel createBareEvenement(CoreSession session) throws ClientException;

    /**
     * Retourne un événement par son identifiant technique.
     * Cette méthode n'échoue jamais, elle retourne null si l'événement n'existe pas.
     * 
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @return Document événement
     * @throws ClientException
     */
    DocumentModel getEvenement(CoreSession session, String evenementId) throws ClientException;
    
    /**
     * Retourne les événements successifs à un autre événement.
     * 
     * @param session Session
     * @param evenementParentId Identifiant technique de l'événement
     * @return Liste des documents événement successifs
     * @throws ClientException
     */
    List<DocumentModel> findEvenementSuccessif(CoreSession session, String evenementParentId) throws ClientException;
    
    /**
     * Crée un nouvel événement.
     * 
     * @param session Session
     * @param evenementDoc Événement à créer
     * @param dossierDoc Document dossier parent
     * @param publie État initial de l'événement: publié (sinon brouillon)
     * @return Événement nouvellement créé
     * @throws ClientException
     */
    DocumentModel creerEvenement(CoreSession session, DocumentModel evenementDoc, DocumentModel dossierDoc, boolean publie) throws ClientException;

    /**
     * Publie l'événement. L'événement est publié une seule fois lors de la transition de sa première version
     * de l'état brouillon à l'état publié.
     * La publication de l'événement entraine sa première (et unique) distribution vers le destinataire et
     * les destinataires en copie.
     * 
     * @param session Session
     * @param evenementDoc Document événement
     * @throws ClientException
     */
    void publierEvenement(CoreSession session, DocumentModel evenementDoc) throws ClientException;

    /**
     * Annule un événement (si celui-ci est à l'état PUBLIE), ou effectue une demande d'annulation
     * de l'événement (si celui-ci est à l'état EN_INSTANCE).
     * 
     * @param session Session
     * @param annulerEvenementContext Contexte d'annulation de l'événement
     * @throws ClientException
     */
    void annulerEvenement(CoreSession session, AnnulerEvenementContext annulerEvenementContext) throws ClientException;

    /**
     * Supprime un événement. L'événement est supprimé physiquement, et toutes les données liées à
     * l'événement (messages...) sont également supprimées.
     * 
     * @param session Session
     * @param evenementDoc Document évenement
     * @throws ClientException
     */
    void supprimerEvenement(CoreSession session, DocumentModel evenementDoc) throws ClientException;

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
     * @throws ClientException
     */
    DocumentModel modifierEvenementBrouillon(CoreSession session, DocumentModel currentEvenementDoc, DocumentModel newEvenementDoc) throws ClientException;

    /**
     * Effectue les traitements après la création d'un événement de type alerte.
     * 
     * @param session Session
     * @param evenementDoc Document événement créé
     * @param dossierDoc Document dossier
     * @param versionDoc Document version de l'alerte
     */
    void processEvenementAlerte(CoreSession session, DocumentModel evenementDoc, DocumentModel dossierDoc, DocumentModel versionDoc) throws ClientException;

    /**
     * Initialisation d'un evenement successif
     * @param session
     * @param initialiserEvenementContext
     * @throws ClientException 
     */
	void initialiserEvenement(CoreSession session, InitialiserEvenementContext initialiserEvenementContext) throws ClientException;

	/**
     * Retourne les évènements racines du dossier
     * 
     * @param session session
     * @param dossierDoc dossier
     * @return les évènements racines
     * @throws ClientException
     */
	List<DocumentModel> getEvenementsRacineDuDossier(CoreSession session, DocumentModel dossierDoc) throws ClientException;

    /**
     * Retourne la liste de tous les évènements du dossier
     * 
     * @param session session
     * @param dossierDoc dossier
     * @return liste de tous les évènements
     * @throws ClientException
     */    
    List<DocumentModel> getEvenementDossierList(CoreSession session, DocumentModel dossierDoc) throws ClientException;

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
     * @throws ClientException
     */
    void envoyerMel(CoreSession session, String expediteurId, String objet, String corps, String destinataire, String destinatairesCopie, DocumentModel evenementDoc, DocumentModel versionDoc, DocumentModel dossierDoc, List<DocumentModel> pjDocList) throws ClientException;

}
