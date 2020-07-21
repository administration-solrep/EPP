package fr.dila.solonepp.rest.management;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.tablereference.Acteur;
import fr.dila.solonepp.api.domain.tablereference.Circonscription;
import fr.dila.solonepp.api.domain.tablereference.Gouvernement;
import fr.dila.solonepp.api.domain.tablereference.Identite;
import fr.dila.solonepp.api.domain.tablereference.MembreGroupe;
import fr.dila.solonepp.api.domain.tablereference.Ministere;
import fr.dila.solonepp.api.domain.tablereference.Periode;
import fr.dila.solonepp.api.exception.DossierNotFoundException;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.CorbeilleService;
import fr.dila.solonepp.api.service.CorbeilleTypeService;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import fr.dila.solonepp.core.assembler.ws.CorbeilleNodeAssembler;
import fr.dila.solonepp.core.assembler.ws.DossierAssembler;
import fr.dila.solonepp.core.assembler.ws.EvenementAssembler;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.dila.solonepp.core.assembler.ws.VocabulaireReferenceAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.ActionObjetReference;
import fr.sword.xsd.solon.epp.ChercherCorbeilleRequest;
import fr.sword.xsd.solon.epp.ChercherCorbeilleResponse;
import fr.sword.xsd.solon.epp.ChercherDossierRequest;
import fr.sword.xsd.solon.epp.ChercherDossierResponse;
import fr.sword.xsd.solon.epp.ChercherIdentiteRequest;
import fr.sword.xsd.solon.epp.ChercherIdentiteRequest.DescriptifRequete;
import fr.sword.xsd.solon.epp.ChercherIdentiteResponse;
import fr.sword.xsd.solon.epp.ChercherIdentiteResponse.DescriptifResultat;
import fr.sword.xsd.solon.epp.ChercherMandatParNORRequest;
import fr.sword.xsd.solon.epp.ChercherMandatParNORResponse;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceResponse;
import fr.sword.xsd.solon.epp.Corbeille;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EtatMessage;
import fr.sword.xsd.solon.epp.EtatVerrou;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesRequest;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesResponse;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesResponse.CorbeilleInfos;
import fr.sword.xsd.solon.epp.MajTableRequest;
import fr.sword.xsd.solon.epp.MajTableResponse;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.NotifierVerrouRequest;
import fr.sword.xsd.solon.epp.NotifierVerrouResponse;
import fr.sword.xsd.solon.epp.ObjetContainer;
import fr.sword.xsd.solon.epp.ObjetType;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.Section;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJORequest;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJOResponse;
import fr.sword.xsd.solon.epp.TypeMandat;

/**
 * Permet de gerer toutes les operations sur le webservice WSepp.
 * 
 * @author sly
 */
public class EppDelegate {

	protected CoreSession	session;

	/**
	 * Constructeur de EppDelegate.
	 * 
	 * @param session
	 *            Session
	 */
	public EppDelegate(CoreSession session) {
		this.session = session;
	}

	/**
	 * Retourne l'arborescence des corbeilles de l'institution.
	 * 
	 * @param request
	 *            Requête
	 * @return Réponse
	 * @throws ClientException
	 */
	public ChercherCorbeilleResponse chercherCorbeille(ChercherCorbeilleRequest request) throws ClientException {
		final CorbeilleTypeService corbeilleTypeService = SolonEppServiceLocator.getCorbeilleTypeService();

		// Recherche l'arborescence des corbeilles
		EppPrincipal principal = (EppPrincipal) session.getPrincipal();
		List<CorbeilleNode> corbeilleNodeList = corbeilleTypeService.getCorbeilleInstitutionTree(principal
				.getInstitutionId());

		CorbeilleNodeAssembler corbeilleNodeAssembler = new CorbeilleNodeAssembler();
		ChercherCorbeilleResponse response = corbeilleNodeAssembler.assembleCorbeilleNodeTree(corbeilleNodeList);

		// Recherche le nombre de message dans chaque corbeille
		List<Corbeille> corbeilleList = new ArrayList<Corbeille>();
		corbeilleList.addAll(response.getCorbeille());
		for (Section section : response.getSection()) {
			corbeilleList.addAll(section.getCorbeille());
		}

		// final CorbeilleService corbeilleService = SolonEppServiceLocator.getCorbeilleService();
		for (Corbeille corbeille : corbeilleList) {
			// FIXME : utile le count ? pas pour SOLEX ni pour MGPP...
			// long messageCount = corbeilleService.findMessageCount(session, corbeille.getIdCorbeille());
			// corbeille.setNombreEvenementCourant((int) messageCount);
			corbeille.setNombreEvenementCourant(0);
		}

		response.setStatut(TraitementStatut.OK);
		return response;
	}

	public MajTableResponse majTable(MajTableRequest request) throws ClientException {
		MajTableResponse response = new MajTableResponse();

		// Validité de la requete
		ActionObjetReference actionObjetReference = request.getAction();
		if (actionObjetReference == null) {
			throw new ClientException("Le champ action est obligatoire.");
		}

		ObjetContainer objetContainer = request.getObjetContainer();
		if (objetContainer == null) {
			throw new ClientException("Le champ objet_container est obligatoire.");
		}

		ObjetType objetType = objetContainer.getType();
		if (objetType == null) {
			throw new ClientException("L'attribut type est obligatoire.");
		}

		// Initialisation de la réponse
		ObjetContainer objetContainerReponse = new ObjetContainer();
		response.setObjetContainer(objetContainerReponse);
		objetContainerReponse.setType(objetType);

		// Traitement de la requete
		boolean creation = actionObjetReference.equals(ActionObjetReference.AJOUTER);
		boolean modification = actionObjetReference.equals(ActionObjetReference.MODIFIER);
		boolean renouvellement = actionObjetReference.equals(ActionObjetReference.RENOUVELER);

		TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();

		if (objetType.equals(ObjetType.ACTEUR)) {

			List<fr.sword.xsd.solon.epp.Acteur> acteurs = objetContainer.getActeur();
			DocumentModel acteurDocReponse = null;
			if (renouvellement) {
				tableReferenceService.disableAllActeur(session);
			}
			for (fr.sword.xsd.solon.epp.Acteur acteur : acteurs) {
				DocumentModel acteurDoc = tableReferenceService.createBareActeurDoc(session);
				acteurDoc = TableReferenceAssembler.toActeurDoc(session, acteur, acteurDoc);
				if (creation || renouvellement) {
					acteurDocReponse = tableReferenceService.createActeur(session, acteurDoc);
				} else if (modification) {
					acteurDocReponse = tableReferenceService.updateActeur(session, acteurDoc);
				}
				if (acteurDocReponse == null) {
					throw new ClientException("Impossible de créer l'objet Acteur");
				} else {
					objetContainerReponse.getActeur().add(
							TableReferenceAssembler.toActeurXsd(acteurDocReponse.getAdapter(Acteur.class)));
				}
			}
		} else if (objetType.equals(ObjetType.CIRCONSCRIPTION)) {
			List<fr.sword.xsd.solon.epp.Circonscription> circonscriptions = objetContainer.getCirconscription();
			DocumentModel circonscriptionDocReponse = null;
			if (renouvellement) {
				tableReferenceService.disableAllCirconscription(session);
			}
			for (fr.sword.xsd.solon.epp.Circonscription circonscription : circonscriptions) {
				DocumentModel circonscriptionDoc = tableReferenceService.createBareCirconscriptionDoc(session);
				circonscriptionDoc = TableReferenceAssembler.toCirconscriptionDoc(session, circonscription,
						circonscriptionDoc);
				if (creation || renouvellement) {
					circonscriptionDocReponse = tableReferenceService
							.createCirconscription(session, circonscriptionDoc);
				} else if (modification) {
					circonscriptionDocReponse = tableReferenceService
							.updateCirconscription(session, circonscriptionDoc);
				}
				if (circonscriptionDocReponse == null) {
					throw new ClientException("Impossible de créer l'objet Circonscription");
				} else {
					objetContainerReponse.getCirconscription().add(
							TableReferenceAssembler.toCirconscriptionXsd(circonscriptionDocReponse
									.getAdapter(Circonscription.class)));
				}
			}

		} else if (objetType.equals(ObjetType.GOUVERNEMENT)) {

			List<fr.sword.xsd.solon.epp.Gouvernement> gouvernements = objetContainer.getGouvernement();
			DocumentModel gouvernementDocReponse = null;
			if (renouvellement) {
				tableReferenceService.disableAllGouvernement(session);
			}
			for (fr.sword.xsd.solon.epp.Gouvernement gouvernement : gouvernements) {
				DocumentModel gouvernementDoc = tableReferenceService.createBareGouvernementDoc(session);
				gouvernementDoc = TableReferenceAssembler.toGouvernementDoc(session, gouvernement, gouvernementDoc);
				if (creation || renouvellement) {
					gouvernementDocReponse = tableReferenceService.createGouvernement(session, gouvernementDoc);
				} else if (modification) {
					gouvernementDocReponse = tableReferenceService.updateGouvernement(session, gouvernementDoc);
				}
				if (gouvernementDocReponse == null) {
					throw new ClientException("Impossible de créer l'objet Gouvernement");
				} else {
					objetContainerReponse.getGouvernement().add(
							TableReferenceAssembler.toGouvernementXsd(gouvernementDocReponse
									.getAdapter(Gouvernement.class)));
				}

			}

		} else if (objetType.equals(ObjetType.IDENTITE)) {

			List<fr.sword.xsd.solon.epp.Identite> identites = objetContainer.getIdentite();
			DocumentModel identiteDocReponse = null;
			if (renouvellement) {
				tableReferenceService.disableAllIdentite(session);
			}
			for (fr.sword.xsd.solon.epp.Identite identite : identites) {
				DocumentModel identiteDoc = tableReferenceService.createBareIdentiteDoc(session);
				identiteDoc = TableReferenceAssembler.toIdentiteDoc(session, identite, identiteDoc);
				if (creation || renouvellement) {
					identiteDocReponse = tableReferenceService.createIdentite(session, identiteDoc);
				} else if (modification) {
					identiteDocReponse = tableReferenceService.updateIdentite(session, identiteDoc);
				}
				if (identiteDocReponse == null) {
					throw new ClientException("Impossible de créer l'objet Identite");
				} else {
					objetContainerReponse.getIdentite().add(
							TableReferenceAssembler.toIdentiteXsd(identiteDocReponse.getAdapter(Identite.class)));
				}
			}

		} else if (objetType.equals(ObjetType.MANDAT)) {

			List<fr.sword.xsd.solon.epp.Mandat> mandats = objetContainer.getMandat();
			DocumentModel mandatDocReponse = null;
			if (renouvellement) {
				tableReferenceService.disableAllMandat(session);
			}
			for (fr.sword.xsd.solon.epp.Mandat mandat : mandats) {
				// Les mandats de type haut-commissaire, secrétariat d'état ou ministère doivent avoir un appellation
				if (StringUtils.isBlank(mandat.getAppellation())
						&& (mandat.getType() == TypeMandat.HAUT_COMMISSAIRE
								|| mandat.getType() == TypeMandat.SECRETARIAT_ETAT || mandat.getType() == TypeMandat.MINISTERE)) {
					throw new ClientException("Les mandats de type " + mandat.getType().name()
							+ " doivent avoir une appellation");
				}

				DocumentModel mandatDoc = tableReferenceService.createBareMandatDoc(session);
				mandatDoc = TableReferenceAssembler.toMandatDoc(session, mandat, mandatDoc);
				if (creation || renouvellement) {
					mandatDocReponse = tableReferenceService.createMandat(session, mandatDoc);
				} else if (modification) {
					mandatDocReponse = tableReferenceService.updateMandat(session, mandatDoc);
				}
				if (mandatDocReponse == null) {
					throw new ClientException("Impossible de créer l'objet Mandat");
				} else {
					objetContainerReponse.getMandat().add(
							TableReferenceAssembler.toMandatXsd(session, mandatDocReponse));
				}
			}

		} else if (objetType.equals(ObjetType.MEMBRE_GROUPE)) {
			if (renouvellement) {
				tableReferenceService.disableAllMembreGroupe(session);
			}
			List<fr.sword.xsd.solon.epp.MembreGroupe> membreGroupes = objetContainer.getMembreGroupe();
			DocumentModel membreGroupeDocReponse = null;
			for (fr.sword.xsd.solon.epp.MembreGroupe membreGroupe : membreGroupes) {
				DocumentModel membreGroupeDoc = tableReferenceService.createBareMembreGroupeDoc(session);
				membreGroupeDoc = TableReferenceAssembler.toMembreGroupeDoc(session, membreGroupe, membreGroupeDoc);
				if (creation || renouvellement) {
					membreGroupeDocReponse = tableReferenceService.createMembreGroupe(session, membreGroupeDoc);
				} else if (modification) {
					membreGroupeDocReponse = tableReferenceService.updateMembreGroupe(session, membreGroupeDoc);
				}
				if (membreGroupeDocReponse == null) {
					throw new ClientException("Impossible de créer l'objet MembreGroupe");
				} else {
					objetContainerReponse.getMembreGroupe().add(
							TableReferenceAssembler.toMembreGroupeXsd(membreGroupeDocReponse
									.getAdapter(MembreGroupe.class)));
				}
			}

		} else if (objetType.equals(ObjetType.MINISTERE)) {

			List<fr.sword.xsd.solon.epp.Ministere> ministeres = objetContainer.getMinistere();
			DocumentModel ministereDocReponse = null;
			if (renouvellement) {
				tableReferenceService.disableAllMinistere(session);
			}
			for (fr.sword.xsd.solon.epp.Ministere ministere : ministeres) {
				DocumentModel ministereDoc = tableReferenceService.createBareMinistereDoc(session);
				ministereDoc = TableReferenceAssembler.toMinistereDoc(session, ministere, ministereDoc);
				if (creation || renouvellement) {
					ministereDocReponse = tableReferenceService.createMinistere(session, ministereDoc);
				} else if (modification) {
					ministereDocReponse = tableReferenceService.updateMinistere(session, ministereDoc);
				}
				if (ministereDocReponse == null) {
					throw new ClientException("Impossible de créer l'objet Ministere");
				} else {
					objetContainerReponse.getMinistere().add(
							TableReferenceAssembler.toMinistereXsd(ministereDocReponse.getAdapter(Ministere.class)));
				}
			}

		} else if (objetType.equals(ObjetType.ORGANISME)) {

			List<fr.sword.xsd.solon.epp.Organisme> organismes = objetContainer.getOrganisme();
			DocumentModel organismeDocReponse = null;
			if (renouvellement) {
				tableReferenceService.disableAllOrganisme(session);
			}
			for (fr.sword.xsd.solon.epp.Organisme organisme : organismes) {
				DocumentModel organismeDoc = tableReferenceService.createBareOrganismeDoc(session);
				organismeDoc = TableReferenceAssembler.toOrganismeDoc(session, organisme, organismeDoc);
				if (creation || renouvellement) {
					organismeDocReponse = tableReferenceService.createOrganisme(session, organismeDoc);
				} else if (modification) {
					organismeDocReponse = tableReferenceService.updateOrganisme(session, organismeDoc);
				}
				if (organismeDocReponse == null) {
					throw new ClientException("Impossible de créer l'objet Organisme");
				} else {
					objetContainerReponse.getOrganisme().add(
							TableReferenceAssembler.toOrganismeXsd(organismeDocReponse));
				}
			}

		} else if (objetType.equals(ObjetType.PERIODE)) {

			List<fr.sword.xsd.solon.epp.Periode> periodes = objetContainer.getPeriode();
			DocumentModel periodeDocReponse = null;
			if (renouvellement) {
				tableReferenceService.disableAllPeriode(session);
			}
			for (fr.sword.xsd.solon.epp.Periode periode : periodes) {
				DocumentModel periodeDoc = tableReferenceService.createBarePeriodeDoc(session);
				periodeDoc = TableReferenceAssembler.toPeriodeDoc(session, periode, periodeDoc);
				if (creation || renouvellement) {
					periodeDocReponse = tableReferenceService.createPeriode(session, periodeDoc);
				} else if (modification) {
					periodeDocReponse = tableReferenceService.updatePeriode(session, periodeDoc);
				}
				if (periodeDocReponse == null) {
					throw new ClientException("Impossible de créer l'objet Periode");
				} else {
					objetContainerReponse.getPeriode().add(
							TableReferenceAssembler.toPeriodeXsd(periodeDocReponse.getAdapter(Periode.class)));
				}

			}

		}
		response.setStatut(TraitementStatut.OK);

		return response;
	}

	public ChercherTableDeReferenceResponse chercherTableDeReference(ChercherTableDeReferenceRequest request)
			throws ClientException {
		ChercherTableDeReferenceResponse response = new ChercherTableDeReferenceResponse();

		// Type d'objet
		ObjetType objetType = request.getTypeObjet();
		if (objetType == null) {
			throw new ClientException("Le champ ''Type d''objet'' est obligatoire.");
		}

		// idObjet
		List<String> idObjets = request.getIdObjet();
		boolean rechercheParId = !idObjets.isEmpty();
		String parentId = request.getParentId();
		// Recherche des actifs uniquement (gestion du choice)
		Calendar now = Calendar.getInstance();
		ObjetContainer objetResponse = new ObjetContainer();
		objetResponse.setType(objetType);
		TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
		if (rechercheParId) {
			if (objetType.equals(ObjetType.ACTEUR)) {
				for (String idObjet : idObjets) {
					DocumentModel acteurModel = tableReferenceService.getActeurById(session, idObjet);
					if (acteurModel != null) {
						Acteur acteur = acteurModel.getAdapter(Acteur.class);
						objetResponse.getActeur().add(TableReferenceAssembler.toActeurXsd(acteur));
					}
				}
			} else if (objetType.equals(ObjetType.CIRCONSCRIPTION)) {
				for (String idObjet : idObjets) {
					DocumentModel circonscriptionModel = tableReferenceService.getCirconscriptionById(session, idObjet);
					if (circonscriptionModel != null) {
						Circonscription circonscription = circonscriptionModel.getAdapter(Circonscription.class);
						objetResponse.getCirconscription().add(
								TableReferenceAssembler.toCirconscriptionXsd(circonscription));
					}
				}

			} else if (objetType.equals(ObjetType.GOUVERNEMENT)) {
				for (String idObjet : idObjets) {
					DocumentModel gouvernementModel = tableReferenceService.getGouvernementById(session, idObjet);
					if (gouvernementModel != null) {
						Gouvernement gouvernement = gouvernementModel.getAdapter(Gouvernement.class);
						objetResponse.getGouvernement().add(TableReferenceAssembler.toGouvernementXsd(gouvernement));
					}
				}
			} else if (objetType.equals(ObjetType.IDENTITE)) {
				for (String idObjet : idObjets) {
					DocumentModel identiteModel = tableReferenceService.getIdentiteById(session, idObjet);
					if (identiteModel != null) {
						Identite identite = identiteModel.getAdapter(Identite.class);
						objetResponse.getIdentite().add(TableReferenceAssembler.toIdentiteXsd(identite));
					}
				}
			} else if (objetType.equals(ObjetType.MANDAT)) {
				for (String idObjet : idObjets) {
					DocumentModel mandatModel = tableReferenceService.getMandatById(session, idObjet);
					if (mandatModel != null) {
						Mandat mandat = TableReferenceAssembler.toMandatXsd(session, mandatModel);
						objetResponse.getMandat().add(mandat);
					}
				}

			} else if (objetType.equals(ObjetType.MEMBRE_GROUPE)) {
				for (String idObjet : idObjets) {
					DocumentModel membreGroupeModel = tableReferenceService.getMembreGroupeById(session, idObjet);
					if (membreGroupeModel != null) {
						MembreGroupe membreGroupe = membreGroupeModel.getAdapter(MembreGroupe.class);
						objetResponse.getMembreGroupe().add(TableReferenceAssembler.toMembreGroupeXsd(membreGroupe));
					}
				}
			} else if (objetType.equals(ObjetType.MINISTERE)) {
				for (String idObjet : idObjets) {
					DocumentModel ministereModel = tableReferenceService.getMinistereById(session, idObjet);
					if (ministereModel != null) {
						Ministere ministere = ministereModel.getAdapter(Ministere.class);
						objetResponse.getMinistere().add(TableReferenceAssembler.toMinistereXsd(ministere));
					}
				}
			} else if (objetType.equals(ObjetType.ORGANISME)) {
				for (String idObjet : idObjets) {
					DocumentModel organismeModel = tableReferenceService.getOrganismeById(session, idObjet);
					if (organismeModel != null) {
						objetResponse.getOrganisme().add(TableReferenceAssembler.toOrganismeXsd(organismeModel));
					}
				}
			} else if (objetType.equals(ObjetType.PERIODE)) {
				for (String idObjet : idObjets) {
					DocumentModel periodeModel = tableReferenceService.getPeriodeById(session, idObjet);
					if (periodeModel != null) {
						Periode periode = periodeModel.getAdapter(Periode.class);
						objetResponse.getPeriode().add(TableReferenceAssembler.toPeriodeXsd(periode));
					}
				}
			} else if (objetType.equals(ObjetType.ATTRIBUTION_COMMISSION)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getAttributionCommissionById(session, idObjet);
					if (docModel != null) {
						objetResponse.getAttributionCommission().add(
								VocabulaireReferenceAssembler.toAttributionCommissionReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.NATURE_LOI)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getNatureLoiById(session, idObjet);
					if (docModel != null) {
						objetResponse.getNatureLoi().add(
								VocabulaireReferenceAssembler.toNatureLoiReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.NATURE_RAPPORT)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getNatureRapportById(session, idObjet);
					if (docModel != null) {
						objetResponse.getNatureRapport().add(
								VocabulaireReferenceAssembler.toNatureRapportReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.TYPE_LOI)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getTypeLoiById(session, idObjet);
					if (docModel != null) {
						objetResponse.getTypeLoi().add(VocabulaireReferenceAssembler.toTypeLoiReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.SORT_ADOPTION)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getSortAdoptionById(session, idObjet);
					if (docModel != null) {
						objetResponse.getSortAdoption().add(
								VocabulaireReferenceAssembler.toSortAdoptionReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.MOTIF_IRRECEVABILITE)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getMotifIrrecevabiliteById(session, idObjet);
					if (docModel != null) {
						objetResponse.getMotifIrrecevabilite().add(
								VocabulaireReferenceAssembler.toMotifIrrecevabiliteReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.SENS_AVIS)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getSensAvisById(session, idObjet);
					if (docModel != null) {
						objetResponse.getSensAvis().add(VocabulaireReferenceAssembler.toSensAvisReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.NIVEAU_LECTURE_CODE)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getNiveauLectureCodeById(session, idObjet);
					if (docModel != null) {
						objetResponse.getNiveauLectureCode().add(
								VocabulaireReferenceAssembler.toNiveauLectureCodeReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.RAPPORT_PARLEMENT)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getRapportParlementById(session, idObjet);
					if (docModel != null) {
						objetResponse.getRapportParlement().add(
								VocabulaireReferenceAssembler.toRapportParlementReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.RESULTAT_CMP)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getRapportParlementById(session, idObjet);
					if (docModel != null) {
						objetResponse.getResultatCmp().add(
								VocabulaireReferenceAssembler.toResultatCmpReferenceXsd(docModel));
					}
				}
			} else if (objetType.equals(ObjetType.TYPE_FUSION)) {
				// Do nothing
			} else if (objetType.equals(ObjetType.INSTITUTION)) {
				for (String idObjet : idObjets) {
					OrganigrammeNode node = tableReferenceService.getInstitutionById(session, idObjet);
					if (node != null) {
						objetResponse.getInstitution().add(TableReferenceAssembler.toInstitutionReferenceXsd(node));
					}
				}
			} else if (objetType.equals(ObjetType.TYPE_ACTE)) {
				for (String idObjet : idObjets) {
					DocumentModel docModel = tableReferenceService.getTypeActeById(session, idObjet);
					if (docModel != null) {
						objetResponse.getTypeActe().add(VocabulaireReferenceAssembler.toTypeActeReferenceXsd(docModel));
					}
				}
			} else {
				throw new ClientException("ObjetType incorrect");
			}
			response.setObjetContainer(objetResponse);
			response.setStatut(TraitementStatut.OK);

			return response;
		} else {
			// RECHERCHE PAR TYPE
			if (objetType.equals(ObjetType.ACTEUR)) {

				List<DocumentModel> acteurModelList = tableReferenceService.findAllActeur(session);
				for (DocumentModel acteurModel : acteurModelList) {
					Acteur acteur = acteurModel.getAdapter(Acteur.class);
					if (acteur != null) {
						objetResponse.getActeur().add(TableReferenceAssembler.toActeurXsd(acteur));
					}
				}

			} else if (objetType.equals(ObjetType.CIRCONSCRIPTION)) {

				List<DocumentModel> circonscriptionModelList = tableReferenceService.findAllCirconscription(session,
						false);
				for (DocumentModel circonscriptionModel : circonscriptionModelList) {
					Circonscription circonscription = circonscriptionModel.getAdapter(Circonscription.class);
					if (circonscription != null
							&& (!request.isActifsUniquement() || circonscription.getDateFin() == null || circonscription
									.getDateFin() != null && circonscription.getDateFin().after(now))) {
						objetResponse.getCirconscription().add(
								TableReferenceAssembler.toCirconscriptionXsd(circonscription));
					}
				}

			} else if (objetType.equals(ObjetType.GOUVERNEMENT)) {

				List<DocumentModel> gouvernementModelList = tableReferenceService.findAllGouvernement(session);
				List<String> hasChild = tableReferenceService.findAllNodeHasChild(session,
						ObjetType.GOUVERNEMENT.value(), parentId);
				for (DocumentModel gouvernementModel : gouvernementModelList) {
					Gouvernement gouvernement = gouvernementModel.getAdapter(Gouvernement.class);
					if (gouvernement != null
							&& (!request.isActifsUniquement() || gouvernement.getDateFin() == null || gouvernement
									.getDateFin() != null && gouvernement.getDateFin().after(now))) {
						if (hasChild.contains(gouvernement.getIdentifiant())) {
							gouvernement.setMinistereAttache(true);
						}
						objetResponse.getGouvernement().add(TableReferenceAssembler.toGouvernementXsd(gouvernement));
					}
				}

			} else if (objetType.equals(ObjetType.IDENTITE)) {

				List<DocumentModel> identiteModelList = tableReferenceService.findAllIdentite(session);
				for (DocumentModel identiteModel : identiteModelList) {
					Identite identite = identiteModel.getAdapter(Identite.class);
					if (identite != null
							&& (!request.isActifsUniquement() || identite.getDateFin() == null || identite.getDateFin() != null
									&& identite.getDateFin().after(now))) {
						objetResponse.getIdentite().add(TableReferenceAssembler.toIdentiteXsd(identite));
					}
				}

			} else if (objetType.equals(ObjetType.MANDAT)) {
				List<DocumentModel> mandatModelList = null;
				if (parentId != null && parentId.equals("Mandats sans Ministeres")) {
					mandatModelList = tableReferenceService.findAllMandatWithoutMin(session);
				} else {
					mandatModelList = (parentId != null) ? tableReferenceService.findAllMandatByMin(session, parentId)
							: tableReferenceService.findAllMandat(session, false);
				}
				for (DocumentModel mandatModel : mandatModelList) {
					Mandat mandat = TableReferenceAssembler.toMandatXsd(session, mandatModel);
					if (mandat != null
							&& (!request.isActifsUniquement() || mandat.getDateFin() == null || mandat.getDateFin() != null
									&& mandat.getDateFin().toGregorianCalendar().after(now))) {
						objetResponse.getMandat().add(mandat);
					}
				}

			} else if (objetType.equals(ObjetType.MEMBRE_GROUPE)) {

				List<DocumentModel> membreGroupeModelList = tableReferenceService.findAllMembreGroupe(session);
				for (DocumentModel membreGroupeModel : membreGroupeModelList) {
					MembreGroupe membreGroupe = membreGroupeModel.getAdapter(MembreGroupe.class);
					if (membreGroupe != null
							&& (!request.isActifsUniquement() || membreGroupe.getDateFin() == null || membreGroupe
									.getDateFin() != null && membreGroupe.getDateFin().after(now))) {
						objetResponse.getMembreGroupe().add(TableReferenceAssembler.toMembreGroupeXsd(membreGroupe));
					}
				}

			} else if (objetType.equals(ObjetType.MINISTERE)) {
				List<DocumentModel> ministereModelList = (parentId != null) ? tableReferenceService
						.findAllMinistereByGov(session, parentId) : tableReferenceService.findAllMinistere(session);
				List<String> hasChild = tableReferenceService.findAllNodeHasChild(session, ObjetType.MINISTERE.value(),
						parentId);
				for (DocumentModel ministereModel : ministereModelList) {
					Ministere ministere = ministereModel.getAdapter(Ministere.class);
					if (ministere != null
							&& (!request.isActifsUniquement() || ministere.getDateFin() == null || ministere
									.getDateFin() != null && ministere.getDateFin().after(now))) {
						if (hasChild.contains(ministere.getIdentifiant())) {
							ministere.setMandatAttache(true);
						}
						objetResponse.getMinistere().add(TableReferenceAssembler.toMinistereXsd(ministere));
					}
				}

			} else if (objetType.equals(ObjetType.ORGANISME)) {

				List<DocumentModel> organismeModelList = tableReferenceService.findAllOrganisme(session, false);
				for (DocumentModel organismeModel : organismeModelList) {
					Organisme organisme = TableReferenceAssembler.toOrganismeXsd(organismeModel);
					if (organisme != null
							&& (!request.isActifsUniquement() || organisme.getDateFin() == null || organisme
									.getDateFin() != null && organisme.getDateFin().toGregorianCalendar().after(now))) {
						objetResponse.getOrganisme().add(organisme);
					}
				}

			} else if (objetType.equals(ObjetType.PERIODE)) {
				List<DocumentModel> periodeModelList = tableReferenceService.findAllPeriode(session, false);
				for (DocumentModel periodeModel : periodeModelList) {
					Periode periode = periodeModel.getAdapter(Periode.class);
					if (periode != null
							&& (!request.isActifsUniquement() || periode.getDateFin() == null || periode.getDateFin() != null
									&& periode.getDateFin().after(now))) {
						objetResponse.getPeriode().add(TableReferenceAssembler.toPeriodeXsd(periode));
					}
				}
			} else if (objetType.equals(ObjetType.ATTRIBUTION_COMMISSION)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllAttributionCommission(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getAttributionCommission().add(
							VocabulaireReferenceAssembler.toAttributionCommissionReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.NATURE_LOI)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllNatureLoi(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getNatureLoi().add(VocabulaireReferenceAssembler.toNatureLoiReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.NATURE_RAPPORT)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllNatureRapport(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getNatureRapport().add(
							VocabulaireReferenceAssembler.toNatureRapportReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.TYPE_LOI)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllTypeLoi(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getTypeLoi().add(VocabulaireReferenceAssembler.toTypeLoiReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.SORT_ADOPTION)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllSortAdoption(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getSortAdoption().add(
							VocabulaireReferenceAssembler.toSortAdoptionReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.MOTIF_IRRECEVABILITE)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllMotifIrrecevabilite(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getMotifIrrecevabilite().add(
							VocabulaireReferenceAssembler.toMotifIrrecevabiliteReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.SENS_AVIS)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllSensAvis(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getSensAvis().add(VocabulaireReferenceAssembler.toSensAvisReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.NIVEAU_LECTURE_CODE)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllNiveauLectureCode(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getNiveauLectureCode().add(
							VocabulaireReferenceAssembler.toNiveauLectureCodeReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.RAPPORT_PARLEMENT)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllRapportParlement(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getRapportParlement().add(
							VocabulaireReferenceAssembler.toRapportParlementReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.RESULTAT_CMP)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllResultatCmp(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getResultatCmp().add(
							VocabulaireReferenceAssembler.toResultatCmpReferenceXsd(docModel));
				}
			} else if (objetType.equals(ObjetType.TYPE_FUSION)) {
				// Do nothing
			} else if (objetType.equals(ObjetType.INSTITUTION)) {
				List<InstitutionNode> nodeList = tableReferenceService.findAllInsitution(session);
				for (OrganigrammeNode node : nodeList) {
					objetResponse.getInstitution().add(TableReferenceAssembler.toInstitutionReferenceXsd(node));
				}
			} else if (objetType.equals(ObjetType.TYPE_ACTE)) {
				List<DocumentModel> docModelList = tableReferenceService.findAllTypeActe(session);
				for (DocumentModel docModel : docModelList) {
					objetResponse.getTypeActe().add(VocabulaireReferenceAssembler.toTypeActeReferenceXsd(docModel));
				}
			} else {
				throw new ClientException("ObjetType incorrect");
			}

			response.setObjetContainer(objetResponse);
			response.setStatut(TraitementStatut.OK);
			return response;
		}
	}

	public ChercherIdentiteResponse chercherIdentite(ChercherIdentiteRequest request) throws ClientException {
		ChercherIdentiteResponse response = new ChercherIdentiteResponse();
		response.setStatut(TraitementStatut.OK);
		TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
		for (DescriptifRequete descriptif : request.getDescriptifRequete()) {
			DescriptifResultat descReponse = new DescriptifResultat();
			response.getDescriptifResultat().add(descReponse);
			if (descriptif.getNom() == null || descriptif.getPrenom() == null || descriptif.getDateNaissance() == null) {
				continue;
			}
			List<DocumentModel> identitesModels = tableReferenceService.findAllIdentiteByDescription(session,
					descriptif.getNom(), descriptif.getPrenom(), descriptif.getDateNaissance().toGregorianCalendar());
			if (!identitesModels.isEmpty()) {
				Identite identite = identitesModels.get(0).getAdapter(Identite.class);
				descReponse.getIdentites().add(TableReferenceAssembler.toIdentiteXsd(identite));
			}

		}
		return response;
	}

	/**
	 * Notifie la transition d'un message du destinataire.
	 * 
	 * @param request
	 *            Requête
	 * @return Réponse
	 * @throws ClientException
	 */
	public NotifierTransitionResponse notifierTransition(NotifierTransitionRequest request) throws ClientException {
		NotifierTransitionResponse response = new NotifierTransitionResponse();

		String evenementId = request.getIdEvenement();
		EtatMessage etatMessage = request.getEtat();
		final MessageService messageService = SolonEppServiceLocator.getMessageService();
		if (EtatMessage.EN_COURS_TRAITEMENT.equals(etatMessage)) {
			messageService.followTransitionEnCours(session, evenementId);
		} else if (EtatMessage.TRAITE.equals(etatMessage)) {
			messageService.followTransitionTraite(session, evenementId);
		} else {
			throw new ClientException("Interdiction de transitionner dans l'état " + etatMessage);
		}

		response.setIdEvenement(evenementId);
		response.setStatut(TraitementStatut.OK);

		return response;
	}

	/**
	 * Retourne les informations du dossier.
	 * 
	 * @param request
	 *            Requête
	 * @return Réponse
	 * @throws ClientException
	 */
	public ChercherDossierResponse chercherDossier(ChercherDossierRequest request) throws ClientException {

		final EvenementService evtService = SolonEppServiceLocator.getEvenementService();
		final VersionService versionService = SolonEppServiceLocator.getVersionService();
		final PieceJointeService pjService = SolonEppServiceLocator.getPieceJointeService();
		final MessageService messageService = SolonEppServiceLocator.getMessageService();
		final DossierService dossierService = SolonEppServiceLocator.getDossierService();

		ChercherDossierResponse response = new ChercherDossierResponse();
		List<String> dossierIdList = request.getIdDossier();

		List<DocumentModel> dossiersDocsList = new ArrayList<DocumentModel>();
		DocumentModel dossierDocTmp;
		List<DocumentModel> evtList;
		boolean hasDossier = false;
		for (String dossierId : dossierIdList) {
			dossierDocTmp = dossierService.getDossier(session, dossierId);
			if (dossierDocTmp != null) {

				hasDossier = true;
				DossierAssembler dossierAssembler = new DossierAssembler(session);
				fr.sword.xsd.solon.epp.Dossier dossier = dossierAssembler.assembleDossierToXsd(session, dossierDocTmp);

				dossiersDocsList.add(dossierDocTmp);
				evtList = evtService.getEvenementDossierList(session, dossierDocTmp);

				for (DocumentModel evtDoc : evtList) {
					Evenement evt = evtDoc.getAdapter(Evenement.class);
					// versionService.getVersionActive(session, evtDoc, "");
					DocumentModel messageDoc = messageService.getMessageByEvenementId(session, evt.getTitle());
					if (messageDoc == null) {
						// La communication n'est pas visible pour l'utilisateur appelant le WS
						continue;
					}
					fr.dila.solonepp.api.domain.message.Message message = messageDoc
							.getAdapter(fr.dila.solonepp.api.domain.message.Message.class);
					DocumentModel versionDoc = versionService.getVersionActive(session, evtDoc,
							message.getMessageType());

					List<DocumentModel> pjList = pjService.getVersionPieceJointeList(session, versionDoc);

					EvenementAssembler evenementAssembler = new EvenementAssembler(session);
					EppEvtContainer eppEvtContainer = evenementAssembler.assembleEvenementToXsd(dossierDocTmp, evtDoc,
							versionDoc, pjList, message.getMessageType());
					dossier.getEvenement().add(eppEvtContainer);
				}

				response.getDossier().add(dossier);
			}
		}
		if (hasDossier) {
			response.setStatut(TraitementStatut.OK);
		} else {
			response.setStatut(TraitementStatut.KO);
			throw new DossierNotFoundException("Pas de dossiers trouvés pour les identifiants : "
					+ StringUtil.join(dossierIdList, ",", "'"));
		}
		return response;
	}

	/**
	 * Verrouille ou déverrouille l'événement et retourne les infos sur le verrou
	 * 
	 * @param request
	 * @return
	 * @throws ClientException
	 */
	public NotifierVerrouResponse notifierVerrou(NotifierVerrouRequest request) throws ClientException {
		NotifierVerrouResponse response = new NotifierVerrouResponse();

		EppPrincipal principal = (EppPrincipal) session.getPrincipal();

		String idEvt = request.getIdEvenement();
		response.setIdEvenement(idEvt);

		if (StringUtils.isBlank(idEvt)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur("Id communication non valide");
			return response;
		}

		final STLockService lockService = STServiceLocator.getSTLockService();
		final STMailService mailService = STServiceLocator.getSTMailService();
		final UserManager userManager = STServiceLocator.getUserManager();
		final MessageService messageService = SolonEppServiceLocator.getMessageService();

		DocumentModel messageDoc = messageService.getMessageByEvenementId(session, idEvt);
		EtatVerrou etat = request.getActionVerrou();

		String locker = null;
		Map<String, String> lockDetails = lockService.getLockDetails(session, messageDoc);
		if (lockDetails != null) {
			locker = lockDetails.get(STLockService.LOCKER);
		}
		if (request.isForcerVerrou()) {
			if (lockDetails != null && !lockDetails.get(STLockService.LOCKER).equals(principal.getName())) {
				lockService.unlockDocUnrestricted(session, messageDoc);
				DocumentModel userDoc = userManager.getUserModel(locker);
				if (userDoc != null) {
					// Send mail to user
					STUser user = userDoc.getAdapter(STUser.class);
					List<STUser> userList = new ArrayList<STUser>();
					userList.add(user);
					mailService.sendMailToUserList(session, userList, "Notifier Verrou",
							"Vous avez perdu votre verrou sur la communication : " + idEvt);
				}
			}
		}

		if (etat.equals(EtatVerrou.VERROUILLER)) {
			if (!lockService.lockDoc(session, messageDoc)) {
				response.setUtilisateur(locker);
			} else {
				response.setUtilisateur(session.getPrincipal().getName());
			}
		} else if (etat.equals(EtatVerrou.DEVERROUILLER)) {
			if (!lockService.unlockDoc(session, messageDoc)) {
				response.setUtilisateur(locker);
			}
		} else {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur("Etat verrou non valide");
			return response;
		}

		response.setStatut(TraitementStatut.OK);
		return response;
	}

	public ChercherMandatParNORResponse chercherMandatParNor(ChercherMandatParNORRequest request)
			throws ClientException {
		final ChercherMandatParNORResponse response = new ChercherMandatParNORResponse();
		final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
		response.setStatut(TraitementStatut.OK);
		// Initialisation de la réponse
		ObjetContainer objetContainerResponse = new ObjetContainer();
		response.setObjetContainer(objetContainerResponse);
		objetContainerResponse.setType(ObjetType.MANDAT);
		String nor = request.getNor();
		if (StringUtils.isBlank(nor)) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur("Le paramètre nor doit être renseigné pour pouvoir rechercher par nor.");
		} else {
			List<DocumentModel> mandatsDoc = tableReferenceService.getMandatsByNor(session, nor, true);
			for (DocumentModel mandatDoc : mandatsDoc) {
				Mandat mandat = TableReferenceAssembler.toMandatXsd(session, mandatDoc);
				objetContainerResponse.getMandat().add(mandat);
			}
		}

		return response;
	}

	public HasCommunicationNonTraiteesResponse hasCommunication(HasCommunicationNonTraiteesRequest request)
			throws ClientException {
		final HasCommunicationNonTraiteesResponse response = new HasCommunicationNonTraiteesResponse();
		final CorbeilleService corbeilleService = SolonEppServiceLocator.getCorbeilleService();

		List<String> idCorbeilles = request.getIdCorbeilles();

		if (idCorbeilles.isEmpty()) {
			final CorbeilleTypeService corbeilleTypeService = SolonEppServiceLocator.getCorbeilleTypeService();
			EppPrincipal principal = (EppPrincipal) session.getPrincipal();
			List<CorbeilleNode> corbeilleList = corbeilleTypeService.getCorbeilleInstitutionTree(principal
					.getInstitutionId());
			for (CorbeilleNode node : corbeilleList) {
				setCorbeilleInfos(node, corbeilleService, response);
			}
		} else {
			for (String idCorbeille : idCorbeilles) {
				CorbeilleInfos corbeilleInfos = new CorbeilleInfos();
				corbeilleInfos.setIdCorbeille(idCorbeille);
				corbeilleInfos.setHasNonTraitees(corbeilleService.hasCommunicationNonTraites(session, idCorbeille));
				response.getCorbeilleInfos().add(corbeilleInfos);
			}
		}
		response.setStatut(TraitementStatut.OK);
		return response;
	}

	/**
	 * Récupère les corbeilles sans distinctions des sections de manière récursive Le service est passé en paramètre
	 * pour éviter de le récupérer à chaque appel
	 * 
	 * @param node
	 * @param corbeilleService
	 * @param response
	 * @throws ClientException
	 */
	private void setCorbeilleInfos(CorbeilleNode node, CorbeilleService corbeilleService,
			HasCommunicationNonTraiteesResponse response) throws ClientException {
		if (node.isTypeCorbeille()) {
			CorbeilleInfos corbeilleInfos = new CorbeilleInfos();
			corbeilleInfos.setIdCorbeille(node.getName());
			corbeilleInfos.setHasNonTraitees(corbeilleService.hasCommunicationNonTraites(session, node.getName()));
			response.getCorbeilleInfos().add(corbeilleInfos);
		} else if (node.isTypeSection()) {
			for (CorbeilleNode child : node.getCorbeilleNodeList()) {
				setCorbeilleInfos(child, corbeilleService, response);
			}
		}
	}

	public TransmissionDatePublicationJOResponse transmissionDatePublicationJO(
			final TransmissionDatePublicationJORequest request) throws ClientException {
		final TransmissionDatePublicationJOResponse response = new TransmissionDatePublicationJOResponse();
		response.setStatut(TraitementStatut.OK);
		final DossierService dossierService = SolonEppServiceLocator.getDossierService();
		final String idDossier = request.getIdDossier();
		final XMLGregorianCalendar dpXMLCalendar = request.getDatePublication();
		if (dpXMLCalendar == null) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur("La date de publication ne doit pas être nulle.");
			return response;
		}
		final Calendar datePublication = dpXMLCalendar.toGregorianCalendar();
		
		final EppPrincipal principal = (EppPrincipal) session.getPrincipal();
		if (!principal.getInstitutionIdSet().contains(InstitutionsEnum.DILA.name())) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur("Utilisateur non autorisé à utiliser ce webservice.");
			return response;
		}
		
		// récupération de la communication
		final DocumentModel dossierDoc = dossierService.getDossier(session, idDossier);
		if (dossierDoc == null) {
			response.setStatut(TraitementStatut.KO);
			response.setMessageErreur(String.format("Dossier inconnu pour l'id : %s", idDossier));
			return response;
		}
		final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
		// Maj de la date publication
		dossier.setDatePublication(datePublication);
		session.saveDocument(dossier.getDocument());
		
		return response;
	}
}
