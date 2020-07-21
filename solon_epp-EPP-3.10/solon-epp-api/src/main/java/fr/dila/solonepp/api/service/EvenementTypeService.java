package fr.dila.solonepp.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;

/**
 * Service permettant de gérer les types d'événements.
 * 
 * @author jtremeaux
 */
public interface EvenementTypeService extends Serializable {

    /**
     * Retourne la description d'un type d'événement.
     * 
     * @param evenementType Type d'événement
     * @return Description du type d'événement
     * @throws ClientException
     */
    EvenementTypeDescriptor getEvenementType(String evenementType) throws ClientException;

    /**
     * Détermine si un type d'événement permet de créer des dossiers.
     * 
     * @param evenementType Type d'événement
     * @return Vrai si le type d'événement permet de créer des dossiers
     * @throws ClientException
     */
    boolean isTypeCreateur(String evenementType) throws ClientException;

    /**
     * Détermine si un type d'événement peut succéder à un autre événement.
     * 
     * @param evenementType Type d'événement
     * @return Vrai si le type d'événement peut succéder à un autre événement
     * @throws ClientException
     */
    boolean isTypeSuccessif(String evenementType) throws ClientException;

    /**
     * Détermine si les versions de ce type d'événement nécessite un accusé de réception.
     * 
     * @param evenementType Type d'événement
     * @return Vrai si les versions de ce type d'événement nécessite un accusé de réception
     * @throws ClientException
     */
    boolean isDemandeAr(String evenementType) throws ClientException;
    
    /**
     * Détermine si l'émetteur est autorisé pour ce type d'événement.
     * 
     * @param evenementType Type d'événement
     * @param emetteur Emetteur
     * @return Émetteur autorisé
     * @throws ClientException
     */
    boolean isEmetteurAutorise(String evenementType, String emetteur) throws ClientException;

    /**
     * Détermine si le destinataire est autorisé pour ce type d'événement.
     * 
     * @param evenementType Type d'événement
     * @param destinataire Destinataire
     * @return Destinataire autorisé
     * @throws ClientException
     */
    boolean isDestinataireAutorise(String evenementType, String destinataire) throws ClientException;

    /**
     * Détermine si le destinataire en copie est obligatoire pour ce type d'événement.
     * 
     * @param evenementType Type d'événement
     * @return Destinataire obligatoire
     * @throws ClientException
     */
    boolean isDestinataireCopieObligatoire(String evenementType) throws ClientException;

    /**
     * Détermine si le destinataire en copie est autorisé pour ce type d'événement.
     * 
     * @param evenementType Type d'événement
     * @param destinataireCopie Destinataire
     * @return Destinataire autorisé
     * @throws ClientException
     */
    boolean isDestinataireCopieAutorise(String evenementType, String destinataireCopie) throws ClientException;

    /**
     * Détermine si un type de pièce jointe est obligatoire.
     * 
     * @param evenementType Type d'événement
     * @param pieceJointeType Type de pièce jointe
     * @return Vrai si le type de pièce jointe est obligatoire
     * @throws ClientException
     */
    boolean isPieceJointeObligatoire(String evenementType, String pieceJointeType) throws ClientException;

    /**
     * Détermine si un type d'événement peut être créé à l'état brouillon (pour initialisation).
     * 
     * @param evenementType Type d'événement
     * @throws ClientException
     */
    boolean isCreerBrouillon(String evenementType) throws ClientException;
    
    /**
     * Détermine si un type d'événement peut faire l'objet d'une complétion.
     * 
     * @param evenementType Type d'événement
     * @throws ClientException
     */
    boolean isCompleter(String evenementType) throws ClientException;
    
    /**
     * Détermine si un type d'événement peut faire l'objet d'une rectification.
     * 
     * @param evenementType Type d'événement
     * @throws ClientException
     */
    boolean isRectifier(String evenementType) throws ClientException;
    
    /**
     * Détermine si un type d'événement peut faire l'objet d'une annulation.
     * 
     * @param evenementType Type d'événement
     * @throws ClientException
     */
    boolean isAnnuler(String evenementType) throws ClientException;

    /**
     * Retourne la liste des événements créateur.
     * 
     * @return liste des événements créateur
     */
    List<EvenementTypeDescriptor> findEvenementTypeCreateur();
    
    /**
     * Retourne la liste des types d'événements d'une catégorie donnée.
     * 
     * @param categoryId Identifiant technique de la catégorie
     * @return Liste d'événements
     */
    List<EvenementTypeDescriptor> findEvenementByCategory(String categoryId);

    /**
     * retourne la liste des evenements successif possibles a un typeEvenement
     * @param evenementType
     * @return
     * @throws ClientException 
     */
    List<EvenementTypeDescriptor> findEvenementTypeSuccessif(String evenementType) throws ClientException;

    /**
     * retourne la liste des evenements successif possibles a un typeEvenement dont l'utilisateur peut être emetteur
     * @param session
     * @param evenementType
     * @return
     * @throws ClientException
     */
    List<EvenementTypeDescriptor> findEvenementTypeSuccessif(CoreSession session, String evenementType) throws ClientException;
    
    /**
     * retourne la liste des evenements successifs 
     * @return
     * @throws ClientException
     */
    List<EvenementTypeDescriptor> findEvenementTypeSuccessif() throws ClientException;
    
    /**
     * Retourne la liste de tous les types d'événements.
     *  
     * @return liste des événements
     */
    List<EvenementTypeDescriptor> findEvenementType();
    
    /**
     * retourne la liste des evenements successifs possibles provenant de la même procédure a un typeEvenement 
     * @param evenementType
     * @return
     * @throws ClientException 
     */    
    List<EvenementTypeDescriptor> findEvenementTypeSuccessifWithSameProcedure(String evenementType);

    /**
     * test is un evenment est de type alerte
     * 
     * @param evenementType
     * @return
     */
    boolean isEvenementTypeAlerte(String evenementType);

    /**
     * test is un evenment est de type generique
     * 
     * @param evenementType
     * @return
     */
    boolean isEvenementTypeGenerique(String evenementType);

}
