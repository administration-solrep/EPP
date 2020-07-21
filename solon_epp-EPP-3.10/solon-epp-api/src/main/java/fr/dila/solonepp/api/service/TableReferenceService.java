package fr.dila.solonepp.api.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.dto.TableReferenceDTO;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;

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
	 * @throws ClientException
	 */
	DocumentModel getActeurById(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche tous les acteurs.
	 * 
	 * @param session
	 *            Session
	 * @param active
	 *            Recherche uniquement parmi les enregistrements actifs (sinon actifs ET inactifs)
	 * @return Document acteur
	 * @throws ClientException
	 */
	List<DocumentModel> findAllActeur(CoreSession session) throws ClientException;

	/**
	 * Crée un acteur.
	 * 
	 * @param session
	 *            Session
	 * @param acteurDoc
	 *            Document acteur à créer
	 * @throws ClientException
	 */
	DocumentModel createActeur(CoreSession session, DocumentModel acteurDoc) throws ClientException;

	/**
	 * Modifie un acteur.
	 * 
	 * @param session
	 *            Session
	 * @param acteurDoc
	 *            Document acteur à modifier
	 * @throws ClientException
	 */
	DocumentModel updateActeur(CoreSession session, DocumentModel acteurDoc) throws ClientException;

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
	 * @throws ClientException
	 */
	DocumentModel getCirconscriptionById(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche toutes les circonscriptions actives.
	 * 
	 * @param session
	 *            Session
	 * @param restricted
	 *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
	 * @return Document circonscription
	 * @throws ClientException
	 */
	List<DocumentModel> findAllCirconscription(CoreSession session, boolean restricted) throws ClientException;

	/**
	 * Crée une circonscription.
	 * 
	 * @param session
	 *            Session
	 * @param circonscriptionDoc
	 *            Document circonscription à créer
	 * @throws ClientException
	 */
	DocumentModel createCirconscription(CoreSession session, DocumentModel circonscriptionDoc) throws ClientException;

	/**
	 * Modifie une circonscription.
	 * 
	 * @param session
	 *            Session
	 * @param circonscriptionDoc
	 *            Document circonscription à modifier
	 * @throws ClientException
	 */
	DocumentModel updateCirconscription(CoreSession session, DocumentModel circonscriptionDoc) throws ClientException;

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
	 * @throws ClientException
	 */
	DocumentModel getIdentiteById(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche toutes les identités actives.
	 * 
	 * @param session
	 *            Session
	 * @return Document identite
	 * @throws ClientException
	 */
	List<DocumentModel> findAllIdentite(CoreSession session) throws ClientException;

	/**
	 * Recherche d'identite
	 * 
	 * @param session
	 *            Session
	 * @return List de Documents les identites
	 * @throws ClientException
	 */
	List<DocumentModel> findAllIdentiteByDescription(CoreSession session, String nom, String prenom,
			Calendar dateNaissance) throws ClientException;

	/**
	 * Crée une identité.
	 * 
	 * @param session
	 *            Session
	 * @param identiteDoc
	 *            Document identite à créer
	 * @throws ClientException
	 */
	DocumentModel createIdentite(CoreSession session, DocumentModel identiteDoc) throws ClientException;

	/**
	 * Modifie une identité.
	 * 
	 * @param session
	 *            Session
	 * @param identiteDoc
	 *            Document identite à modifier
	 * @throws ClientException
	 */
	DocumentModel updateIdentite(CoreSession session, DocumentModel identiteDoc) throws ClientException;

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
	 * @throws ClientException
	 */
	DocumentModel getGouvernementById(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche tous les gouvernements actifs.
	 * 
	 * @param session
	 *            Session
	 * @return Document gouvernement
	 * @throws ClientException
	 */
	List<DocumentModel> findAllGouvernement(CoreSession session) throws ClientException;

	/**
	 * Crée un gouvernement.
	 * 
	 * @param session
	 *            Session
	 * @param gouvernementDoc
	 *            Document gouvernement à créer
	 * @throws ClientException
	 */
	DocumentModel createGouvernement(CoreSession session, DocumentModel gouvernementDoc) throws ClientException;

	/**
	 * Modifie un gouvernement.
	 * 
	 * @param session
	 *            Session
	 * @param gouvernementDoc
	 *            Document gouvernement à modifier
	 * @throws ClientException
	 */
	DocumentModel updateGouvernement(CoreSession session, DocumentModel gouvernementDoc) throws ClientException;

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
	 * @throws ClientException
	 */
	DocumentModel getMembreGroupeById(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche tous les membreGroups actifs.
	 * 
	 * @param session
	 *            Session
	 * @return document membre de groupe
	 * @throws ClientException
	 */
	List<DocumentModel> findAllMembreGroupe(CoreSession session) throws ClientException;

	/**
	 * Crée un membre de groupe.
	 * 
	 * @param session
	 *            Session
	 * @param membreGroupDoc
	 *            document membre de groupe à créer
	 * @throws ClientException
	 */
	DocumentModel createMembreGroupe(CoreSession session, DocumentModel membreGroupDoc) throws ClientException;

	/**
	 * Modifie un membre de groupe.
	 * 
	 * @param session
	 *            Session
	 * @param membreGroupDoc
	 *            document membre de groupe à modifier
	 * @throws ClientException
	 */
	DocumentModel updateMembreGroupe(CoreSession session, DocumentModel membreGroupDoc) throws ClientException;

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
	 * @throws ClientException
	 */
	DocumentModel getMandatById(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche tous les mandats actifs.
	 * 
	 * @param session
	 *            Session
	 * @param restricted
	 *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
	 * @return Document mandat
	 * @throws ClientException
	 */
	List<DocumentModel> findAllMandat(CoreSession session, boolean restricted) throws ClientException;

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
	 * @throws ClientException
	 */
	List<DocumentModel> getMandatsByNor(CoreSession session, String nor, boolean activeOnly) throws ClientException;

	/**
	 * Crée un mandat.
	 * 
	 * @param session
	 *            Session
	 * @param mandatDoc
	 *            Document mandat à créer
	 * @throws ClientException
	 */
	DocumentModel createMandat(CoreSession session, DocumentModel mandatDoc) throws ClientException;

	/**
	 * Modifie un mandat.
	 * 
	 * @param session
	 *            Session
	 * @param mandatDoc
	 *            Document mandat à modifier
	 * @throws ClientException
	 */
	DocumentModel updateMandat(CoreSession session, DocumentModel mandatDoc) throws ClientException;

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
	 * @throws ClientException
	 */
	DocumentModel getMinistereById(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche tous les ministeres actifs.
	 * 
	 * @param session
	 *            Session
	 * @return Document ministère
	 * @throws ClientException
	 */
	List<DocumentModel> findAllMinistere(CoreSession session) throws ClientException;

	/**
	 * Crée un ministère.
	 * 
	 * @param session
	 *            Session
	 * @param ministereDoc
	 *            Document ministère à créer
	 * @throws ClientException
	 */
	DocumentModel createMinistere(CoreSession session, DocumentModel ministereDoc) throws ClientException;

	/**
	 * Modifie un ministère.
	 * 
	 * @param session
	 *            Session
	 * @param ministereDoc
	 *            Document ministère à modifier
	 * @throws ClientException
	 */
	DocumentModel updateMinistere(CoreSession session, DocumentModel ministereDoc) throws ClientException;

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
	 * @throws ClientException
	 */
	DocumentModel getOrganismeById(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche tous les organismes actifs.
	 * 
	 * @param session
	 *            Session
	 * @param restricted
	 *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
	 * @return Document organisme
	 * @throws ClientException
	 */
	List<DocumentModel> findAllOrganisme(CoreSession session, boolean restricted) throws ClientException;

	/**
	 * Crée un organisme.
	 * 
	 * @param session
	 *            Session
	 * @param organismeDoc
	 *            Document organisme à créer
	 * @throws ClientException
	 */
	DocumentModel createOrganisme(CoreSession session, DocumentModel organismeDoc) throws ClientException;

	/**
	 * Modifie un organisme.
	 * 
	 * @param session
	 *            Session
	 * @param organismeDoc
	 *            Document organisme à modifier
	 * @throws ClientException
	 */
	DocumentModel updateOrganisme(CoreSession session, DocumentModel organismeDoc) throws ClientException;

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
	 * @throws ClientException
	 */
	DocumentModel getPeriodeById(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche toutes les periodes actives.
	 * 
	 * @param session
	 *            Session
	 * @param restricted
	 *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
	 * @return Document période
	 * @throws ClientException
	 */
	List<DocumentModel> findAllPeriode(CoreSession session, boolean restricted) throws ClientException;

	/**
	 * Crée une période.
	 * 
	 * @param session
	 *            Session
	 * @param periodeDoc
	 *            Document période à créer
	 * @throws ClientException
	 */
	DocumentModel createPeriode(CoreSession session, DocumentModel periodeDoc) throws ClientException;

	/**
	 * Modifie une période.
	 * 
	 * @param session
	 *            Session
	 * @param periodeDoc
	 *            Document période à modifier
	 * @throws ClientException
	 */
	DocumentModel updatePeriode(CoreSession session, DocumentModel periodeDoc) throws ClientException;

	/**
	 * Retourne le document racine des acteurs.
	 * 
	 * @param session
	 *            Session
	 * @return Document racine des acteurs
	 * @throws ClientException
	 */
	DocumentModel getActeurRoot(CoreSession session) throws ClientException;

	/**
	 * Retourne le document racine des gouvernements.
	 * 
	 * @param session
	 *            Session
	 * @return Document racine des gouvernements
	 * @throws ClientException
	 */
	DocumentModel getGouvernementRoot(CoreSession session) throws ClientException;

	/**
	 * Retourne le document racine des identites.
	 * 
	 * @param session
	 *            Session
	 * @return Document racine des identites
	 * @throws ClientException
	 */
	DocumentModel getIdentiteRoot(CoreSession session) throws ClientException;

	/**
	 * Retourne le document racine des mandats.
	 * 
	 * @param session
	 *            Session
	 * @return Document racine des mandats
	 * @throws ClientException
	 */
	DocumentModel getMandatRoot(CoreSession session) throws ClientException;

	/**
	 * Retourne le document racine des membreGroupes.
	 * 
	 * @param session
	 *            Session
	 * @return Document racine des membreGroupes
	 * @throws ClientException
	 */
	DocumentModel getMembreGroupeRoot(CoreSession session) throws ClientException;

	/**
	 * Retourne le document racine des ministeres.
	 * 
	 * @param session
	 *            Session
	 * @return Document racine des ministeres
	 * @throws ClientException
	 */
	DocumentModel getMinistereRoot(CoreSession session) throws ClientException;

	/**
	 * Retourne le document racine des organismes.
	 * 
	 * @param session
	 *            Session
	 * @return Document racine des organismes
	 * @throws ClientException
	 */
	DocumentModel getOrganismeRoot(CoreSession session) throws ClientException;

	/**
	 * Retourne le document racine des periodes.
	 * 
	 * @param session
	 *            Session
	 * @return Document racine des periodes
	 * @throws ClientException
	 */
	DocumentModel getPeriodeRoot(CoreSession session) throws ClientException;

	/**
	 * Retourne le document racine des circonscriptions.
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return document racine des circonscription
	 * @throws ClientException
	 */
	DocumentModel getCirconscriptionRoot(CoreSession session) throws ClientException;

	/**
	 * Crée un nouveau document Acteur (uniquement en mémoire).
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return un document Acteur
	 * @throws ClientException
	 */
	DocumentModel createBareActeurDoc(CoreSession session) throws ClientException;

	/**
	 * Crée un nouveau document Circonscription (uniquement en mémoire).
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return un document Circonscription
	 * @throws ClientException
	 */
	DocumentModel createBareCirconscriptionDoc(CoreSession session) throws ClientException;

	/**
	 * Crée un nouveau document Gouvernement (uniquement en mémoire).
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return un document Gouvernement
	 * @throws ClientException
	 */
	DocumentModel createBareGouvernementDoc(CoreSession session) throws ClientException;

	/**
	 * Crée un nouveau document Identite (uniquement en mémoire).
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return un document IdentiteIdentite
	 * @throws ClientException
	 */
	DocumentModel createBareIdentiteDoc(CoreSession session) throws ClientException;

	/**
	 * Crée un nouveau document Mandat (uniquement en mémoire).
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return un document Mandat
	 * @throws ClientException
	 */
	DocumentModel createBareMandatDoc(CoreSession session) throws ClientException;

	/**
	 * Crée un nouveau document MembreGroupe (uniquement en mémoire).
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return un document MembreGroupe
	 * @throws ClientException
	 */
	DocumentModel createBareMembreGroupeDoc(CoreSession session) throws ClientException;

	/**
	 * Crée un nouveau document Ministere (uniquement en mémoire).
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return un document Ministere
	 * @throws ClientException
	 */
	DocumentModel createBareMinistereDoc(CoreSession session) throws ClientException;

	/**
	 * Crée un nouveau document Organisme (uniquement en mémoire).
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return un document Organisme
	 * @throws ClientException
	 */
	DocumentModel createBareOrganismeDoc(CoreSession session) throws ClientException;

	/**
	 * Crée un nouveau document Periode (uniquement en mémoire).
	 * 
	 * @param session
	 *            la session de l'utilisateur
	 * @return un document Periode
	 * @throws ClientException
	 */
	DocumentModel createBarePeriodeDoc(CoreSession session) throws ClientException;

	/**
	 * Désactive tous les objets Acteur actifs.
	 * 
	 * @param session
	 *            la session utilisateur
	 * @throws ClientException
	 */
	void disableAllActeur(CoreSession session) throws ClientException;

	/**
	 * Désactive tous les objets Circonscription actifs.
	 * 
	 * @param session
	 *            la session utilisateur
	 * @throws ClientException
	 */
	void disableAllCirconscription(CoreSession session) throws ClientException;

	/**
	 * Désactive tous les objets Gouvernement actifs.
	 * 
	 * @param session
	 *            la session utilisateur
	 * @throws ClientException
	 */
	void disableAllGouvernement(CoreSession session) throws ClientException;

	/**
	 * Désactive tous les objets Identite actifs.
	 * 
	 * @param session
	 *            la session utilisateur
	 * @throws ClientException
	 */
	void disableAllIdentite(CoreSession session) throws ClientException;

	/**
	 * Désactive tous les objets Mandat actifs.
	 * 
	 * @param session
	 *            la session utilisateur
	 * @throws ClientException
	 */
	void disableAllMandat(CoreSession session) throws ClientException;

	/**
	 * Désactive tous les objets MembreGroupe actifs.
	 * 
	 * @param session
	 *            la session utilisateur
	 * @throws ClientException
	 */
	void disableAllMembreGroupe(CoreSession session) throws ClientException;

	/**
	 * Désactive tous les objets Ministere actifs.
	 * 
	 * @param session
	 *            la session utilisateur
	 * @throws ClientException
	 */
	void disableAllMinistere(CoreSession session) throws ClientException;

	/**
	 * Désactive tous les objets Organisme actifs.
	 * 
	 * @param session
	 *            la session utilisateur
	 * @throws ClientException
	 */
	void disableAllOrganisme(CoreSession session) throws ClientException;

	/**
	 * Désactive tous les objets Periode actifs.
	 * 
	 * @param session
	 *            la session utilisateur
	 * @throws ClientException
	 */
	void disableAllPeriode(CoreSession session) throws ClientException;

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
	 * @throws ClientException
	 */
	List<TableReferenceDTO> searchTableReference(CoreSession session, String searchPattern, String docType,
			String emetteur, boolean fullTableRef, String typeOrganisme) throws ClientException;

	/**
	 * Recherche dans la table de reference de type docType l'identifiant
	 * 
	 * @param session
	 * @param identifiant
	 * @param docType
	 * @param dateRecherche
	 * @return
	 * @throws ClientException
	 */
	TableReferenceDTO findTableReferenceByIdAndType(CoreSession session, String identifiant, String docType,
			Calendar dateRecherche) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getAttributionCommissionById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllAttributionCommission(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getNatureLoiById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllNatureLoi(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getNatureRapportById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllNatureRapport(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getTypeLoiById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllTypeLoi(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getSortAdoptionById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllSortAdoption(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getMotifIrrecevabiliteById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllMotifIrrecevabilite(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getSensAvisById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllSensAvis(CoreSession session) throws ClientException;

	/**
	 * Recupère un niveau lecture
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getNiveauLectureCodeById(CoreSession session, String id) throws ClientException;

	/**
	 * Récupère tous les niveaux lecture
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllNiveauLectureCode(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getRapportParlementById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllRapportParlement(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getResultatCmpById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllResultatCmp(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getTypeFusionById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllTypeFusion(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	OrganigrammeNode getInstitutionById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<InstitutionNode> findAllInsitution(CoreSession session) throws ClientException;

	/**
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getTypeActeById(CoreSession session, String id) throws ClientException;

	/**
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllTypeActe(CoreSession session) throws ClientException;

	/**
	 * Recherche si le mandat existe
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	boolean hasMandat(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche si l'organisme existe
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	boolean hasOrganisme(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche si l'acteur existe
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	boolean hasActeur(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche si la circonscription existe
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	boolean hasCirconscription(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche si l'identité existe
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	boolean hasIdentite(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche si le gouvernement existe
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	boolean hasGouvernement(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche si le membre groupe existe
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	boolean hasMembreGroupe(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche si le ministere existe
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	boolean hasMinistere(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche si le période existe
	 * 
	 * @param session
	 * @param id
	 * @return
	 * @throws ClientException
	 */
	boolean hasPeriode(CoreSession session, String id) throws ClientException;

	/**
	 * Recherche des ministeres attache a un gouvernement
	 * 
	 * @param session
	 * @param govId
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllMinistereByGov(final CoreSession session, String govId) throws ClientException;

	/**
	 * Recherche des mandats attache a un ministere
	 * 
	 * @param session
	 * @param minId
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllMandatByMin(final CoreSession session, String minId) throws ClientException;

	/**
	 * Recherche des identites attaches au mandats attache a un ministere
	 * 
	 * @param session
	 * @param minId
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findAllIdentiteByMin(final CoreSession session, String minId) throws ClientException;

	/**
	 * Retourne les objets qui ont des enfants attache
	 * 
	 * @param session
	 * @param type
	 * @param parentId
	 * @return
	 * @throws ClientException
	 */
	List<String> findAllNodeHasChild(CoreSession session, String type, String parentId) throws ClientException;

	/**
	 * Recherche des mandats non attaches a aucune ministere
	 * 
	 * @param session
	 *            Session
	 * @param restricted
	 *            Si la recherche se restreint aux objets dont l'institution de l'utilisateur est propriétaire
	 * @return Document mandat
	 * @throws ClientException
	 */
	List<DocumentModel> findAllMandatWithoutMin(CoreSession session) throws ClientException;
}
