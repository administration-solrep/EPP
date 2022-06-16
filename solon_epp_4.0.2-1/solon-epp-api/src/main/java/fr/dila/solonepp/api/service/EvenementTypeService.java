package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

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
     */
    EvenementTypeDescriptor getEvenementType(String evenementType);

    /**
     * Détermine si un type d'événement permet de créer des dossiers.
     *
     * @param evenementType Type d'événement
     * @return Vrai si le type d'événement permet de créer des dossiers
     */
    boolean isTypeCreateur(String evenementType);

    /**
     * Détermine si un type d'événement peut succéder à un autre événement.
     *
     * @param evenementType Type d'événement
     * @return Vrai si le type d'événement peut succéder à un autre événement
     */
    boolean isTypeSuccessif(String evenementType);

    /**
     * Détermine si les versions de ce type d'événement nécessite un accusé de réception.
     *
     * @param evenementType Type d'événement
     * @return Vrai si les versions de ce type d'événement nécessite un accusé de réception
     */
    boolean isDemandeAr(String evenementType);

    /**
     * Détermine si l'émetteur est autorisé pour ce type d'événement.
     *
     * @param evenementType Type d'événement
     * @param emetteur Emetteur
     * @return Émetteur autorisé
     */
    boolean isEmetteurAutorise(String evenementType, String emetteur);

    /**
     * Détermine si le destinataire est autorisé pour ce type d'événement.
     *
     * @param evenementType Type d'événement
     * @param destinataire Destinataire
     * @return Destinataire autorisé
     */
    boolean isDestinataireAutorise(String evenementType, String destinataire);

    /**
     * Détermine si le destinataire en copie est obligatoire pour ce type d'événement.
     *
     * @param evenementType Type d'événement
     * @return Destinataire obligatoire
     */
    boolean isDestinataireCopieObligatoire(String evenementType);

    /**
     * Détermine si le destinataire en copie est autorisé pour ce type d'événement.
     *
     * @param evenementType Type d'événement
     * @param destinataireCopie Destinataire
     * @return Destinataire autorisé
     */
    boolean isDestinataireCopieAutorise(String evenementType, String destinataireCopie);

    /**
     * Détermine si un type de pièce jointe est obligatoire.
     *
     * @param evenementType Type d'événement
     * @param pieceJointeType Type de pièce jointe
     * @return Vrai si le type de pièce jointe est obligatoire
     */
    boolean isPieceJointeObligatoire(String evenementType, String pieceJointeType);

    /**
     * Détermine si un type d'événement peut être créé à l'état brouillon (pour initialisation).
     *
     * @param evenementType Type d'événement
     */
    boolean isCreerBrouillon(String evenementType);

    /**
     * Détermine si un type d'événement peut faire l'objet d'une complétion.
     *
     * @param evenementType Type d'événement
     */
    boolean isCompleter(String evenementType);

    /**
     * Détermine si un type d'événement peut faire l'objet d'une rectification.
     *
     * @param evenementType Type d'événement
     */
    boolean isRectifier(String evenementType);

    /**
     * Détermine si un type d'événement peut faire l'objet d'une annulation.
     *
     * @param evenementType Type d'événement
     */
    boolean isAnnuler(String evenementType);

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
     */
    List<EvenementTypeDescriptor> findEvenementTypeSuccessif(String evenementType);

    /**
     * retourne la liste des evenements successif possibles a un typeEvenement dont l'utilisateur peut être emetteur
     * @param session
     * @param evenementType
     * @return
     */
    List<EvenementTypeDescriptor> findEvenementTypeSuccessif(CoreSession session, String evenementType);

    /**
     * retourne la liste des evenements successifs
     * @return
     */
    List<EvenementTypeDescriptor> findEvenementTypeSuccessif();

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
