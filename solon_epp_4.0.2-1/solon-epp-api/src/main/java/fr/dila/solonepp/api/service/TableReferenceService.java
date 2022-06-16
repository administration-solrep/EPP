package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.dto.TableReferenceDTO;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer (ajout, modification, suppression, recherche de données) dans les tables de références.
 *
 * @author jtremeaux
 */
public interface TableReferenceService extends Serializable {
    // *************************************************************
    // Table de référence "Acteur".
    // *************************************************************
    /**
     * Recherche des acteusr par leur identifiant technique
     *
     * @param session
     *            Session
     * @param ids
     *            lis
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return DocumentModelList acteurs
     */
    DocumentModel getActeurById(CoreSession session, String id);

    /**
     * Recherche tous les acteurs.
     *
     * @param session
     *            Session
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return Document acteur
     */
    List<DocumentModel> findAllActeur(CoreSession session);

    /**
     * Crée un acteur.
     *
     * @param session
     *            Session
     * @param acteurDoc
     *            Document acteur à créer
     */
    DocumentModel createActeur(CoreSession session, DocumentModel acteurDoc);

    /**
     * Modifie un acteur.
     *
     * @param session
     *            Session
     * @param acteurDoc
     *            Document acteur à modifier
     */
    DocumentModel updateActeur(CoreSession session, DocumentModel acteurDoc);

    // *************************************************************
    // Table de référence "Circonscription".
    // *************************************************************
    /**
     * Recherche une circonscription par son identifiant technique.
     *
     * @param session
     *            Session
     * @param id
     *            Identifiant technique
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return Document circonscription
     */
    DocumentModel getCirconscriptionById(CoreSession session, String id);

    /**
     * Recherche toutes les circonscriptions actives.
     *
     * @param session
     *            Session
     * @param restricted
     *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
     * @return Document circonscription
     */
    List<DocumentModel> findAllCirconscription(CoreSession session, boolean restricted);

    /**
     * Crée une circonscription.
     *
     * @param session
     *            Session
     * @param circonscriptionDoc
     *            Document circonscription à créer
     */
    DocumentModel createCirconscription(CoreSession session, DocumentModel circonscriptionDoc);

    /**
     * Modifie une circonscription.
     *
     * @param session
     *            Session
     * @param circonscriptionDoc
     *            Document circonscription à modifier
     */
    DocumentModel updateCirconscription(CoreSession session, DocumentModel circonscriptionDoc);

    // *************************************************************
    // Table de référence "Identité".
    // *************************************************************
    /**
     * Recherche une identité par son identifiant technique.
     *
     * @param session
     *            Session
     * @param id
     *            Identifiant technique
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return Document identite
     */
    DocumentModel getIdentiteById(CoreSession session, String id);

    /**
     * Recherche toutes les identités actives.
     *
     * @param session
     *            Session
     * @return Document identite
     */
    List<DocumentModel> findAllIdentite(CoreSession session);

    /**
     * Recherche d'identite
     *
     * @param session
     *            Session
     * @return List de Documents les identites
     */
    List<DocumentModel> findAllIdentiteByDescription(
        CoreSession session,
        String nom,
        String prenom,
        Calendar dateNaissance
    );

    /**
     * Crée une identité.
     *
     * @param session
     *            Session
     * @param identiteDoc
     *            Document identite à créer
     */
    DocumentModel createIdentite(CoreSession session, DocumentModel identiteDoc);

    /**
     * Modifie une identité.
     *
     * @param session
     *            Session
     * @param identiteDoc
     *            Document identite à modifier
     */
    DocumentModel updateIdentite(CoreSession session, DocumentModel identiteDoc);

    // *************************************************************
    // Table de référence "Gouvernement".
    // *************************************************************
    /**
     * Recherche un gouvernement par son identifiant technique.
     *
     * @param session
     *            Session
     * @param id
     *            Identifiant technique
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return Document gouvernement
     */
    DocumentModel getGouvernementById(CoreSession session, String id);

    /**
     * Recherche tous les gouvernements actifs.
     *
     * @param session
     *            Session
     * @return Document gouvernement
     */
    List<DocumentModel> findAllGouvernement(CoreSession session);

    /**
     * Crée un gouvernement.
     *
     * @param session
     *            Session
     * @param gouvernementDoc
     *            Document gouvernement à créer
     */
    DocumentModel createGouvernement(CoreSession session, DocumentModel gouvernementDoc);

    /**
     * Modifie un gouvernement.
     *
     * @param session
     *            Session
     * @param gouvernementDoc
     *            Document gouvernement à modifier
     */
    DocumentModel updateGouvernement(CoreSession session, DocumentModel gouvernementDoc);

    // *************************************************************
    // Table de référence "Membre de groupe".
    // *************************************************************
    /**
     * Recherche un membre de groupe par son identifiant technique.
     *
     * @param session
     *            Session
     * @param id
     *            Identifiant technique
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return document membre de groupe
     */
    DocumentModel getMembreGroupeById(CoreSession session, String id);

    /**
     * Recherche tous les membreGroups actifs.
     *
     * @param session
     *            Session
     * @return document membre de groupe
     */
    List<DocumentModel> findAllMembreGroupe(CoreSession session);

    /**
     * Crée un membre de groupe.
     *
     * @param session
     *            Session
     * @param membreGroupDoc
     *            document membre de groupe à créer
     */
    DocumentModel createMembreGroupe(CoreSession session, DocumentModel membreGroupDoc);

    /**
     * Modifie un membre de groupe.
     *
     * @param session
     *            Session
     * @param membreGroupDoc
     *            document membre de groupe à modifier
     */
    DocumentModel updateMembreGroupe(CoreSession session, DocumentModel membreGroupDoc);

    // *************************************************************
    // Table de référence "Mandat".
    // *************************************************************
    /**
     * Recherche un mandat par son identifiant technique.
     *
     * @param session
     *            Session
     * @param id
     *            Identifiant technique
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return Document mandat
     */
    DocumentModel getMandatById(CoreSession session, String id);

    /**
     * Recherche tous les mandats actifs.
     *
     * @param session
     *            Session
     * @param restricted
     *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
     * @return Document mandat
     */
    List<DocumentModel> findAllMandat(CoreSession session, boolean restricted);

    /**
     * Recherche les mandats associés à un nor
     *
     * @param session
     *            Session
     * @param nor
     *            le nor désiré
     * @param activeOnly
     *            flag pour ne récupérer que les mandats actifs
     * @return List mandats associés au nor
     */
    List<DocumentModel> getMandatsByNor(CoreSession session, String nor, boolean activeOnly);

    /**
     * Crée un mandat.
     *
     * @param session
     *            Session
     * @param mandatDoc
     *            Document mandat à créer
     */
    DocumentModel createMandat(CoreSession session, DocumentModel mandatDoc);

    /**
     * Modifie un mandat.
     *
     * @param session
     *            Session
     * @param mandatDoc
     *            Document mandat à modifier
     */
    DocumentModel updateMandat(CoreSession session, DocumentModel mandatDoc);

    // *************************************************************
    // Table de référence "Ministère".
    // *************************************************************
    /**
     * Recherche un ministère par son identifiant technique.
     *
     * @param session
     *            Session
     * @param id
     *            Identifiant technique
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return Document ministère
     */
    DocumentModel getMinistereById(CoreSession session, String id);

    /**
     * Recherche tous les ministeres actifs.
     *
     * @param session
     *            Session
     * @return Document ministère
     */
    List<DocumentModel> findAllMinistere(CoreSession session);

    /**
     * Crée un ministère.
     *
     * @param session
     *            Session
     * @param ministereDoc
     *            Document ministère à créer
     */
    DocumentModel createMinistere(CoreSession session, DocumentModel ministereDoc);

    /**
     * Modifie un ministère.
     *
     * @param session
     *            Session
     * @param ministereDoc
     *            Document ministère à modifier
     */
    DocumentModel updateMinistere(CoreSession session, DocumentModel ministereDoc);

    // *************************************************************
    // Table de référence "Organisme".
    // *************************************************************
    /**
     * Recherche un organisme par son identifiant technique.
     *
     * @param session
     *            Session
     * @param id
     *            Identifiant technique
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return Document organisme
     */
    DocumentModel getOrganismeById(CoreSession session, String id);

    /**
     * Recherche tous les organismes actifs.
     *
     * @param session
     *            Session
     * @param restricted
     *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
     * @return Document organisme
     */
    List<DocumentModel> findAllOrganisme(CoreSession session, boolean restricted);

    /**
     * Crée un organisme.
     *
     * @param session
     *            Session
     * @param organismeDoc
     *            Document organisme à créer
     */
    DocumentModel createOrganisme(CoreSession session, DocumentModel organismeDoc);

    /**
     * Modifie un organisme.
     *
     * @param session
     *            Session
     * @param organismeDoc
     *            Document organisme à modifier
     */
    DocumentModel updateOrganisme(CoreSession session, DocumentModel organismeDoc);

    // *************************************************************
    // Table de référence "Période".
    // *************************************************************
    /**
     * Recherche une période par son identifiant technique.
     *
     * @param session
     *            Session
     * @param id
     *            Identifiant technique
     * @param active
     *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
     * @return Document période
     */
    DocumentModel getPeriodeById(CoreSession session, String id);

    /**
     * Recherche toutes les periodes actives.
     *
     * @param session
     *            Session
     * @param restricted
     *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
     * @return Document période
     */
    List<DocumentModel> findAllPeriode(CoreSession session, boolean restricted);

    /**
     * Crée une période.
     *
     * @param session
     *            Session
     * @param periodeDoc
     *            Document période à créer
     */
    DocumentModel createPeriode(CoreSession session, DocumentModel periodeDoc);

    /**
     * Modifie une période.
     *
     * @param session
     *            Session
     * @param periodeDoc
     *            Document période à modifier
     */
    DocumentModel updatePeriode(CoreSession session, DocumentModel periodeDoc);

    /**
     * Retourne le document racine des acteurs.
     *
     * @param session
     *            Session
     * @return Document racine des acteurs
     */
    DocumentModel getActeurRoot(CoreSession session);

    /**
     * Retourne le document racine des gouvernements.
     *
     * @param session
     *            Session
     * @return Document racine des gouvernements
     */
    DocumentModel getGouvernementRoot(CoreSession session);

    /**
     * Retourne le document racine des identites.
     *
     * @param session
     *            Session
     * @return Document racine des identites
     */
    DocumentModel getIdentiteRoot(CoreSession session);

    /**
     * Retourne le document racine des mandats.
     *
     * @param session
     *            Session
     * @return Document racine des mandats
     */
    DocumentModel getMandatRoot(CoreSession session);

    /**
     * Retourne le document racine des membreGroupes.
     *
     * @param session
     *            Session
     * @return Document racine des membreGroupes
     */
    DocumentModel getMembreGroupeRoot(CoreSession session);

    /**
     * Retourne le document racine des ministeres.
     *
     * @param session
     *            Session
     * @return Document racine des ministeres
     */
    DocumentModel getMinistereRoot(CoreSession session);

    /**
     * Retourne le document racine des organismes.
     *
     * @param session
     *            Session
     * @return Document racine des organismes
     */
    DocumentModel getOrganismeRoot(CoreSession session);

    /**
     * Retourne le document racine des periodes.
     *
     * @param session
     *            Session
     * @return Document racine des periodes
     */
    DocumentModel getPeriodeRoot(CoreSession session);

    /**
     * Retourne le document racine des circonscriptions.
     *
     * @param session
     *            la session de l'utilisateur
     * @return document racine des circonscription
     */
    DocumentModel getCirconscriptionRoot(CoreSession session);

    /**
     * Crée un nouveau document Acteur (uniquement en mémoire).
     *
     * @param session
     *            la session de l'utilisateur
     * @return un document Acteur
     */
    DocumentModel createBareActeurDoc(CoreSession session);

    /**
     * Crée un nouveau document Circonscription (uniquement en mémoire).
     *
     * @param session
     *            la session de l'utilisateur
     * @return un document Circonscription
     */
    DocumentModel createBareCirconscriptionDoc(CoreSession session);

    /**
     * Crée un nouveau document Gouvernement (uniquement en mémoire).
     *
     * @param session
     *            la session de l'utilisateur
     * @return un document Gouvernement
     */
    DocumentModel createBareGouvernementDoc(CoreSession session);

    /**
     * Crée un nouveau document Identite (uniquement en mémoire).
     *
     * @param session
     *            la session de l'utilisateur
     * @return un document IdentiteIdentite
     */
    DocumentModel createBareIdentiteDoc(CoreSession session);

    /**
     * Crée un nouveau document Mandat (uniquement en mémoire).
     *
     * @param session
     *            la session de l'utilisateur
     * @return un document Mandat
     */
    DocumentModel createBareMandatDoc(CoreSession session);

    /**
     * Crée un nouveau document MembreGroupe (uniquement en mémoire).
     *
     * @param session
     *            la session de l'utilisateur
     * @return un document MembreGroupe
     */
    DocumentModel createBareMembreGroupeDoc(CoreSession session);

    /**
     * Crée un nouveau document Ministere (uniquement en mémoire).
     *
     * @param session
     *            la session de l'utilisateur
     * @return un document Ministere
     */
    DocumentModel createBareMinistereDoc(CoreSession session);

    /**
     * Crée un nouveau document Organisme (uniquement en mémoire).
     *
     * @param session
     *            la session de l'utilisateur
     * @return un document Organisme
     */
    DocumentModel createBareOrganismeDoc(CoreSession session);

    /**
     * Crée un nouveau document Periode (uniquement en mémoire).
     *
     * @param session
     *            la session de l'utilisateur
     * @return un document Periode
     */
    DocumentModel createBarePeriodeDoc(CoreSession session);

    /**
     * Désactive tous les objets Acteur actifs.
     *
     * @param session
     *            la session utilisateur
     */
    void disableAllActeur(CoreSession session);

    /**
     * Désactive tous les objets Circonscription actifs.
     *
     * @param session
     *            la session utilisateur
     */
    void disableAllCirconscription(CoreSession session);

    /**
     * Désactive tous les objets Gouvernement actifs.
     *
     * @param session
     *            la session utilisateur
     */
    void disableAllGouvernement(CoreSession session);

    /**
     * Désactive tous les objets Identite actifs.
     *
     * @param session
     *            la session utilisateur
     */
    void disableAllIdentite(CoreSession session);

    /**
     * Désactive tous les objets Mandat actifs.
     *
     * @param session
     *            la session utilisateur
     */
    void disableAllMandat(CoreSession session);

    /**
     * Désactive tous les objets MembreGroupe actifs.
     *
     * @param session
     *            la session utilisateur
     */
    void disableAllMembreGroupe(CoreSession session);

    /**
     * Désactive tous les objets Ministere actifs.
     *
     * @param session
     *            la session utilisateur
     */
    void disableAllMinistere(CoreSession session);

    /**
     * Désactive tous les objets Organisme actifs.
     *
     * @param session
     *            la session utilisateur
     */
    void disableAllOrganisme(CoreSession session);

    /**
     * Désactive tous les objets Periode actifs.
     *
     * @param session
     *            la session utilisateur
     */
    void disableAllPeriode(CoreSession session);

    /**
     * Recherche dans les tables de reference suivant un type Recherche parmis les organes an ou senat si docType
     * organisme
     *
     * @param session
     * @param searchPattern
     * @param docType
     * @param emetteur
     * @param fullTableRef
     *            Si true, ignore les dates de début et de fin de la table de référence
     * @return
     */
    List<TableReferenceDTO> searchTableReference(
        CoreSession session,
        String searchPattern,
        String docType,
        String emetteur,
        boolean fullTableRef,
        String typeOrganisme
    );

    /**
     * Recherche dans la table de reference de type docType l'identifiant
     *
     * @param session
     * @param identifiant
     * @param docType
     * @param dateRecherche
     * @return
     */
    TableReferenceDTO findTableReferenceByIdAndType(
        CoreSession session,
        String identifiant,
        String docType,
        Calendar dateRecherche
    );

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getAttributionCommissionById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllAttributionCommission(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getNatureLoiById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllNatureLoi(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getNatureRapportById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllNatureRapport(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getTypeLoiById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllTypeLoi(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getSortAdoptionById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllSortAdoption(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getMotifIrrecevabiliteById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllMotifIrrecevabilite(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getSensAvisById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllSensAvis(CoreSession session);

    /**
     * Recupère un niveau lecture
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getNiveauLectureCodeById(CoreSession session, String id);

    /**
     * Récupère tous les niveaux lecture
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllNiveauLectureCode(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getRapportParlementById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllRapportParlement(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getResultatCmpById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllResultatCmp(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    OrganigrammeNode getInstitutionById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<InstitutionNode> findAllInsitution(CoreSession session);

    /**
     *
     * @param session
     * @param id
     * @return
     */
    DocumentModel getTypeActeById(CoreSession session, String id);

    /**
     *
     * @param session
     * @return
     */
    List<DocumentModel> findAllTypeActe(CoreSession session);

    /**
     * Recherche si le mandat existe
     *
     * @param session
     * @param id
     * @return
     */
    boolean hasMandat(CoreSession session, String id);

    /**
     * Recherche si l'organisme existe
     *
     * @param session
     * @param id
     * @return
     */
    boolean hasOrganisme(CoreSession session, String id);

    /**
     * Recherche si l'acteur existe
     *
     * @param session
     * @param id
     * @return
     */
    boolean hasActeur(CoreSession session, String id);

    /**
     * Recherche si la circonscription existe
     *
     * @param session
     * @param id
     * @return
     */
    boolean hasCirconscription(CoreSession session, String id);

    /**
     * Recherche si l'identité existe
     *
     * @param session
     * @param id
     * @return
     */
    boolean hasIdentite(CoreSession session, String id);

    /**
     * Recherche si le gouvernement existe
     *
     * @param session
     * @param id
     * @return
     */
    boolean hasGouvernement(CoreSession session, String id);

    /**
     * Recherche si le membre groupe existe
     *
     * @param session
     * @param id
     * @return
     */
    boolean hasMembreGroupe(CoreSession session, String id);

    /**
     * Recherche si le ministere existe
     *
     * @param session
     * @param id
     * @return
     */
    boolean hasMinistere(CoreSession session, String id);

    /**
     * Recherche si le période existe
     *
     * @param session
     * @param id
     * @return
     */
    boolean hasPeriode(CoreSession session, String id);

    /**
     * Recherche des ministeres attache a un gouvernement
     *
     * @param session
     * @param govId
     * @return
     */
    List<DocumentModel> findAllMinistereByGov(final CoreSession session, String govId);

    /**
     * Recherche des mandats attache a un ministere
     *
     * @param session
     * @param minId
     * @return
     */
    List<DocumentModel> findAllMandatByMin(final CoreSession session, String minId);

    /**
     * Recherche des identites attaches au mandats attache a un ministere
     *
     * @param session
     * @param minId
     * @return
     */
    List<DocumentModel> findAllIdentiteByMin(final CoreSession session, String minId);

    /**
     * Retourne les objets qui ont des enfants attache
     *
     * @param session
     * @param type
     * @param parentId
     * @return
     */
    List<String> findAllNodeHasChild(CoreSession session, String type, String parentId);

    /**
     * Recherche des mandats non attaches a aucune ministere
     *
     * @param session
     *            Session
     * @param restricted
     *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
     * @return Document mandat
     */
    List<DocumentModel> findAllMandatWithoutMin(CoreSession session);
}
