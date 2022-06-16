package fr.dila.solonepp.core.service;

import com.google.common.collect.Sets;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppParametreConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.dao.criteria.EvenementCriteria;
import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.NumeroVersion;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.api.domain.tablereference.Identite;
import fr.dila.solonepp.api.domain.tablereference.Mandat;
import fr.dila.solonepp.api.domain.tablereference.Organisme;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementDistributionService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.api.service.PieceJointeFichierService;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.api.service.VersionNumeroService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.api.service.evenement.AnnulerEvenementContext;
import fr.dila.solonepp.api.service.evenement.AnnulerEvenementRequest;
import fr.dila.solonepp.api.service.evenement.AnnulerEvenementResponse;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementContext;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementResponse;
import fr.dila.solonepp.core.assembler.EvenementAssembler;
import fr.dila.solonepp.core.dao.EvenementDao;
import fr.dila.solonepp.core.dto.TemplatePropertyDTOImpl;
import fr.dila.solonepp.core.filter.DocumentAlertePoseFilter;
import fr.dila.solonepp.core.validator.EvenementValidator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import fr.dila.st.api.domain.file.ExportBlob;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.xsd.solon.epp.EvenementType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.uidgen.UIDGeneratorService;
import org.nuxeo.ecm.core.uidgen.UIDSequencer;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Implémentation du service des événements.
 *
 * @author jtremeaux
 */
public class EvenementServiceImpl implements EvenementService {
    private static final String COMMUNICATION = "communication : ";
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -2149292080437023117L;
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(EvenementServiceImpl.class);

    @Override
    public DocumentModel createBareEvenement(CoreSession session) {
        DocumentModel evenementDoc = session.createDocumentModel(SolonEppConstant.EVENEMENT_DOC_TYPE);

        return evenementDoc;
    }

    @Override
    public DocumentModel getEvenement(CoreSession session, String evenementId) {
        if (StringUtils.isBlank(evenementId)) {
            throw new NuxeoException("L'identifiant de la communication doit être renseigné");
        }
        EvenementCriteria evenementCriteria = new EvenementCriteria();
        evenementCriteria.setEvenementId(evenementId);
        EvenementDao evenementDao = new EvenementDao(session);
        return evenementDao.getSingleEvenementByCriteria(evenementCriteria);
    }

    @Override
    public List<DocumentModel> findEvenementSuccessif(CoreSession session, String evenementParentId) {
        EvenementCriteria evenementCriteria = new EvenementCriteria();
        evenementCriteria.setEvenementParentId(evenementParentId);
        evenementCriteria.setOrderByIdEvenement(true);
        EvenementDao evenementDao = new EvenementDao(session);
        return evenementDao.findEvenementByCriteria(evenementCriteria);
    }

    @Override
    public DocumentModel creerEvenement(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel dossierDoc,
        boolean publie
    ) {
        LOGGER.info(session, STLogEnumImpl.CREATE_COMMUNICATION_TEC, " pour le dossier " + dossierDoc.getTitle());

        // Renseigne automatiquement l'émetteur
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        if (evenement.getEmetteur() == null) {
            if (principal.getInstitutionIdSet().size() > 1) {
                throw new NuxeoException(
                    "Emetteur est obligatoire, mais non renseigné. On tente de le récupérer via les institutions de l'utilisateur, mais elles sont multiples. L'emetteur ne peut donc pas être déterminé."
                );
            }
            evenement.setEmetteur(principal.getInstitutionId());
        }

        // Renseigne le nom du dossier automatiquement
        String dossierTitle = dossierDoc.getTitle();
        evenement.setDossier(dossierTitle);

        // Valide les données
        if (StringUtils.isBlank(evenement.getTypeEvenement())) {
            throw new NuxeoException("Type de communication est obligatoire");
        }

        if (StringUtils.isBlank(evenement.getEmetteur())) {
            throw new NuxeoException("Emetteur est obligatoire");
        }

        // Valide les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateDistribution(evenementDoc, publie);

        // Filtre les destinataires en copie de l'événement
        filterDestinataireCopie(evenementDoc);

        // Génère l'identifiant de l'événement
        UIDGeneratorService uidGeneratorService = ServiceUtil.getRequiredService(UIDGeneratorService.class);
        UIDSequencer sequencer = uidGeneratorService.getSequencer();
        String sequenceId = String.format("%05d", sequencer.getNextLong("EVT_" + dossierTitle) - 1);
        String evenementId = dossierTitle + "_" + sequenceId;

        // Renseigne le chemin et le nom du document
        evenement.setTitle(evenementId);
        evenement.setIdEvenement(evenementId);

        evenementDoc.setPathInfo(dossierDoc.getPathAsString(), evenementId);

        // Crée le document événement
        evenementDoc = session.createDocument(evenementDoc);

        // Crée un message pour l'émetteur de l'événement
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.sendMessageEmetteur(session, evenementDoc);

        // Transitionne vers l'état initial de l'événement (brouillon ou publié)
        if (!publie) {
            evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_TO_BROUILLON_FROM_INIT_TRANSITION);
        } else {
            // Publie l'événement dès sa création
            publierEvenement(session, evenementDoc);
        }

        return evenementDoc;
    }

    @Override
    public DocumentModel modifierEvenementBrouillon(
        CoreSession session,
        DocumentModel currentEvenementDoc,
        DocumentModel newEvenementDoc
    ) {
        LOGGER.info(session, STLogEnumImpl.UPDATE_COMMUNICATION_TEC, "brouillon " + currentEvenementDoc.getTitle());

        // Modifie les propriétés de l'événement
        EvenementAssembler evenementAssembler = new EvenementAssembler();
        evenementAssembler.assembleEvenementForUpdate(newEvenementDoc, currentEvenementDoc);

        // Valide les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(currentEvenementDoc);
        evenementValidator.validateEtatEvenement(
            currentEvenementDoc,
            new HashSet<>(Arrays.asList(new String[] { SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE }))
        );
        evenementValidator.validateDistribution(currentEvenementDoc, false);

        // Filtre les destinataires en copie de l'événement
        filterDestinataireCopie(currentEvenementDoc);

        // Sauvegarde le document modifié
        currentEvenementDoc = session.saveDocument(currentEvenementDoc);

        return currentEvenementDoc;
    }

    @Override
    public void publierEvenement(CoreSession session, DocumentModel evenementDoc) {
        LOGGER.info(
            session,
            STLogEnumImpl.PUBLICATION_COMMUNICATION_TEC,
            COMMUNICATION + evenementDoc.getTitle() + ", UUID: " + evenementDoc.getId()
        );

        Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        // verifie si l'evt précédent est publié
        DocumentModel evtParentDoc = null;
        String evenementParentId = evenement.getEvenementParent();
        if (StringUtils.isNotBlank(evenementParentId)) {
            evtParentDoc = getEvenement(session, evenementParentId);
            if (evtParentDoc != null) {
                Evenement evtParent = evtParentDoc.getAdapter(Evenement.class);
                if (!(evtParent.isEtatInstance() || evtParent.isEtatPublie() || evtParent.isEtatAttenteValidation())) {
                    throw new NuxeoException("La communication précédente doit être à l'état en instance ou publié");
                }
            }
        }

        // Transitionne l'événement dans l'état "publié"
        if (evenement.isEtatInit()) {
            evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_TO_PUBLIE_FROM_INIT_TRANSITION);
        } else if (evenement.isEtatBrouillon()) {
            evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_TO_PUBLIE_TRANSITION);
        } else {
            throw new NuxeoException("La communication doit être à l'état init ou brouillon pour être publié");
        }

        // Crée un message pour le destinataire et les destinataires en copie de l'événement
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.sendMessageDestinataire(session, evenementDoc);
        evenementDistributionService.sendMessageDestinataireCopie(session, evenementDoc);

        // sauvegarde de la session pour init des ACL sur les messages des destinataires et copies
        session.save();
    }

    @Override
    public void annulerEvenement(CoreSession session, AnnulerEvenementContext annulerEvenementContext) {
        AnnulerEvenementRequest annulerEvenementRequest = annulerEvenementContext.getAnnulerEvenementRequest();
        String evenementId = annulerEvenementRequest.getEvenementId();

        LOGGER.info(
            session,
            STLogEnumImpl.REQUEST_COMMUNICATION_CANCELLATION_TEC,
            COMMUNICATION + annulerEvenementRequest.toString()
        );

        // Charge l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        DocumentModel evenementDoc = evenementService.getEvenement(session, evenementId);
        if (evenementDoc == null) {
            throw new NuxeoException("Communication non trouvé: " + evenementId);
        }

        // Vérifie les données de l'événément
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);
        evenementValidator.validateEtatEvenement(
            evenementDoc,
            Sets.newHashSet(
                SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE,
                SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE
            )
        );
        evenementValidator.validateAnnuler(evenementDoc);

        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        DocumentModel versionDoc = null;
        DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());
        if (evenement.isEtatPublie()) {
            // Annule l'événement directement
            versionDoc = annulerEvenementImmediatement(session, dossierDoc, evenementDoc);
        } else {
            // Demande au destinataire l'annulation de l'événement
            versionDoc = annulerEvenementDemande(session, dossierDoc, evenementDoc);
        }

        // Renseigne les données en sortie
        AnnulerEvenementResponse annulerEvenementResponse = annulerEvenementContext.getAnnulerEvenementResponse();
        annulerEvenementResponse.setDossierDoc(session.getDocument(evenementDoc.getParentRef()));
        annulerEvenementResponse.setEvenementDoc(session.getDocument(evenementDoc.getRef()));
        annulerEvenementResponse.setVersionDoc(versionDoc);
    }

    /**
     * Annule un événement immédiatement. L'événement doit être à l'état publié.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Dossier à annuler
     * @param evenementDoc
     *            Evenement à annuler
     * @return Document de la nouvelle version publiée pour annulation
     */
    protected DocumentModel annulerEvenementImmediatement(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc
    ) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        LOGGER.info(
            session,
            STLogEnumImpl.CANCEL_COMMUNICATION_TEC,
            "Annulation imediate de la communication : " + evenement.getTitle()
        );
        // Transitionne la version publiée actuelle à l'état obsolète
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        DocumentModel currentVersionDoc = versionService.getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        if (currentVersionDoc == null) {
            throw new NuxeoException("Impossible de trouver la version active");
        }

        // Vérifie que la version active est à l'état publiée
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);
        if (!currentVersion.isEtatPublie()) {
            throw new NuxeoException("La version active doit être à l'état publié");
        }

        currentVersionDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_OBSOLETE_TRANSITION);

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final DocumentModel lastVersionDoc = versionService.getLastVersion(session, evenementDoc.getTitle());
        NumeroVersion numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, true);

        // Crée la version publiée pour annulation
        DocumentModel versionCreatedDoc = session.copy(
            currentVersionDoc.getRef(),
            currentVersionDoc.getParentRef(),
            numeroVersion.toString()
        );
        Version versionCreated = versionCreatedDoc.getAdapter(Version.class);

        // Modifie les propriétés de la version
        versionCreated.setHorodatage(Calendar.getInstance());
        versionCreated.setDateAr(null);
        versionCreated.setModeCreation(SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_ANNULATION_VALUE);
        versionCreated.setTitle(numeroVersion.toString());
        versionCreated.setNumeroVersion(numeroVersion);

        versionService.setVersionCouranteToFalse(session, evenementDoc.getTitle());
        // FEV319 Nature version ANNULEE
        versionCreated.setNature(SolonEppSchemaConstant.VERSION_NATURE_ANNULEE_PROPERTY);
        versionCreated.setVersionCourante(true);

        session.saveDocument(versionCreatedDoc);

        // Transitionne la version vers l'état publié
        versionCreatedDoc.followTransition(SolonEppLifecycleConstant.VERSION_BACK_TO_BROUILLON_TRANSITION);
        versionCreatedDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_PUBLIE_TRANSITION);

        // Transitionne l'événement à l'état annulé
        evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_TO_ANNULE_TRANSITION);

        // Synchronise les messages avec la version publiée
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterValider(
            session,
            dossierDoc,
            evenementDoc,
            versionCreatedDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        evenementDistributionService.updateMessageAfterValider(
            session,
            dossierDoc,
            evenementDoc,
            versionCreatedDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE
        );
        evenementDistributionService.updateMessageAfterValider(
            session,
            dossierDoc,
            evenementDoc,
            versionCreatedDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE
        );

        return versionCreatedDoc;
    }

    /**
     * Annule un événement en créant une nouvelle version en attente de validation "pour annulation". L'événement doit
     * être à l'état en instance.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Document dossier
     * @param evenementDoc
     *            Evenement à annuler
     * @return Document de la nouvelle version en attente de validation "pour annulation"
     */
    protected DocumentModel annulerEvenementDemande(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc
    ) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        LOGGER.info(
            session,
            STLogEnumImpl.CANCEL_COMMUNICATION_TEC,
            "Annulation de la communication : " +
            evenement.getTitle() +
            ", création d'une nouvelle version pour annulation"
        );
        // Recherche la version active
        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        DocumentModel currentVersionDoc = versionService.getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        if (currentVersionDoc == null) {
            throw new NuxeoException("Impossible de trouver la version active");
        }

        // Vérifie que la version active est à l'état publiée
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);
        if (!currentVersion.isEtatPublie()) {
            throw new NuxeoException("La version active doit être à l'état publié");
        }

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final DocumentModel lastVersionDoc = versionService.getLastVersion(session, evenementDoc.getTitle());
        NumeroVersion numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, false);

        // Crée la version publiée pour annulation
        DocumentModel versionCreatedDoc = session.copy(
            currentVersionDoc.getRef(),
            currentVersionDoc.getParentRef(),
            numeroVersion.toString()
        );
        Version versionCreated = versionCreatedDoc.getAdapter(Version.class);

        // Modifie les propriétés de la version
        versionCreated.setHorodatage(Calendar.getInstance());
        versionCreated.setDateAr(null);
        versionCreated.setModeCreation(SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE);
        versionCreated.setTitle(numeroVersion.toString());
        versionCreated.setNumeroVersion(numeroVersion);

        // FEV319 Nature version ANNULATION EN COURS
        versionCreated.setNature(SolonEppSchemaConstant.VERSION_NATURE_ANNULATION_EN_COURS_PROPERTY);
        versionCreated.setVersionCourante(false);

        session.saveDocument(versionCreatedDoc);

        // Transitionne la version vers l'état publié
        versionCreatedDoc.followTransition(SolonEppLifecycleConstant.VERSION_BACK_TO_BROUILLON_TRANSITION);
        versionCreatedDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_ATTENTE_VALIDATION_TRANSITION);

        // Transitionne l'événement vers l'état en attente de validation
        evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_TO_ATTENTE_VALIDATION_TRANSITION);

        // Synchronise les messages avec la version en attente de validation
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterDemanderValidation(
            session,
            dossierDoc,
            evenementDoc,
            versionCreatedDoc
        );

        return versionCreatedDoc;
    }

    @Override
    public void supprimerEvenement(CoreSession session, DocumentModel evenementDoc) {
        LOGGER.info(
            session,
            STLogEnumImpl.DEL_COMMUNICATION_TEC,
            COMMUNICATION + evenementDoc.getTitle() + ", UUID: " + evenementDoc.getId()
        );

        // Supprime l'événement
        DocumentRef dossierRef = evenementDoc.getParentRef();
        session.removeDocument(evenementDoc.getRef());

        // Si l'événement est le seul du dossier, supprime le dossier
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        List<DocumentModel> evenementDocList = session.getChildren(dossierRef);
        if (evenementDocList == null || evenementDocList.isEmpty()) {
            DocumentModel dossierDoc = session.getDocument(dossierRef);
            dossierService.deleteDossier(session, dossierDoc);
        }
    }

    @Override
    public void filterDestinataireCopie(DocumentModel evenementDoc) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        Set<String> destinataireCopieExclusion = new HashSet<>();
        destinataireCopieExclusion.add(evenement.getEmetteur());
        destinataireCopieExclusion.add(evenement.getDestinataire());
        List<String> filteredDestinataireCopie = new ArrayList<>();
        if (evenement.getDestinataireCopie() != null) {
            for (String destinataireCopie : evenement.getDestinataireCopie()) {
                if (!destinataireCopieExclusion.contains(destinataireCopie)) {
                    filteredDestinataireCopie.add(destinataireCopie);
                    destinataireCopieExclusion.add(destinataireCopie);
                }
            }
        }
        evenement.setDestinataireCopie(filteredDestinataireCopie);
    }

    @Override
    public void processEvenementAlerte(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel dossierDoc,
        DocumentModel versionDoc
    ) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        // L'événement n'est pas de type alerte : reporte l'état en alerte de la branche de l'événement parent
        String evenementParentTitle = evenement.getEvenementParent();
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();

        if (!evenementTypeService.isEvenementTypeAlerte(evenement.getTypeEvenement())) {
            if (StringUtils.isNotBlank(evenementParentTitle)) {
                final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
                DocumentModel evenementParentDoc = evenementService.getEvenement(session, evenementParentTitle);
                Evenement evenementParent = evenementParentDoc.getAdapter(Evenement.class);
                evenement.setBrancheAlerte(evenementParent.getBrancheAlerte());

                session.saveDocument(evenementDoc);
            }
        } else {
            // Renseigne l'état en alerte de la branche
            Version version = versionDoc.getAdapter(Version.class);
            boolean positionAlerte = version.isPositionAlerte();
            if (positionAlerte) {
                evenement.setBrancheAlerte(SolonEppSchemaConstant.EVENEMENT_BRANCHE_ALERTE_POSEE_VALUE);
            } else {
                evenement.setBrancheAlerte(SolonEppSchemaConstant.EVENEMENT_BRANCHE_ALERTE_LEVEE_VALUE);

                // Recherche l'événement parent pose de l'alerte
                DocumentAlertePoseFilter filter = new DocumentAlertePoseFilter(session, true);
                DocumentModel evenementPoseAlerteDoc = getEvenementParent(session, evenementDoc, filter);
                if (evenementPoseAlerteDoc == null) {
                    throw new NuxeoException("Erreur technique : évenement de pose de l'alerte non trouvé");
                }

                // Lève l'alerte sur l'événement parent et tous les événements successifs : interdit de lever 2x
                // l'alerte
                leveBrancheAlerte(session, evenementPoseAlerteDoc);
            }
            session.saveDocument(evenementDoc);

            // Met à jour le nombre d'alertes posées sur le dossier
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            if (positionAlerte) {
                dossier.setAlerteCount(dossier.getAlerteCount() + 1);
            } else {
                dossier.setAlerteCount(dossier.getAlerteCount() - 1);
            }

            session.saveDocument(dossierDoc);
        }
    }

    /**
     * Renseigne le champ de dénormalisation "levée alerte" sur un événement et tous ses descendants.
     *
     * @param session
     *            Session
     * @param evenementDoc
     *            Document événement
     */
    protected void leveBrancheAlerte(CoreSession session, DocumentModel evenementDoc) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        evenement.setBrancheAlerte(SolonEppSchemaConstant.EVENEMENT_BRANCHE_ALERTE_LEVEE_VALUE);
        session.saveDocument(evenementDoc);

        List<DocumentModel> evenementSuccessifDocList = findEvenementSuccessif(session, evenement.getTitle());
        for (DocumentModel evenementSuccessifDoc : evenementSuccessifDocList) {
            leveBrancheAlerte(session, evenementSuccessifDoc);
        }
    }

    /**
     * Remonte la succession des événements et retourne l'événement qui satisfait le filtre.
     *
     * @param session
     *            Session
     * @param evenementDoc
     *            Événement de départ.
     * @param filter
     *            Filtre de documents
     */
    protected DocumentModel getEvenementParent(CoreSession session, DocumentModel evenementDoc, Filter filter) {
        DocumentModel currentEvenementDoc = evenementDoc;
        String evenementParentId = null;
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        do {
            Evenement currentEvenement = currentEvenementDoc.getAdapter(Evenement.class);
            evenementParentId = currentEvenement.getEvenementParent();
            if (StringUtils.isNotBlank(evenementParentId)) {
                currentEvenementDoc = evenementService.getEvenement(session, evenementParentId);
                if (filter.accept(currentEvenementDoc)) {
                    return currentEvenementDoc;
                }
            }
        } while (StringUtils.isNotBlank(evenementParentId));

        return null;
    }

    @Override
    public void initialiserEvenement(CoreSession session, InitialiserEvenementContext initialiserEvenementContext) {
        String idEvenementPrecendent = initialiserEvenementContext
            .getInitialiserEvenementRequest()
            .getIdEvenementPrecedent();
        String typeEvenement = initialiserEvenementContext.getInitialiserEvenementRequest().getTypeEvenement();

        // ckeck request ok
        if (StringUtils.isEmpty(typeEvenement)) {
            throw new NuxeoException("Le type de communication ne peut etre vide");
        }
        if (
            StringUtils.isEmpty(idEvenementPrecendent) &&
            !SolonEppServiceLocator.getEvenementTypeService().isTypeCreateur(typeEvenement)
        ) {
            throw new NuxeoException("La communication précédente ne peut etre vide");
        }

        DocumentModel eventDocPrecedent = null;
        if (!StringUtils.isEmpty(idEvenementPrecendent)) {
            eventDocPrecedent = findEvenementByTitle(session, idEvenementPrecendent);
        }

        if (
            eventDocPrecedent == null && !SolonEppServiceLocator.getEvenementTypeService().isTypeCreateur(typeEvenement)
        ) {
            throw new NuxeoException("Communication " + idEvenementPrecendent + " non trouve");
        }

        DocumentModel lastVersionDoc = null;
        if (!StringUtils.isEmpty(idEvenementPrecendent)) {
            lastVersionDoc = SolonEppServiceLocator.getVersionService().getLastVersion(session, idEvenementPrecendent);
        }

        final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
        MetaDonneesDescriptor metaDonneesDescriptor = metaDonneesService.getEvenementType(typeEvenement);

        // creation et remap du nouvel evenement
        DocumentModel eventDoc = createBareEvenement(session);
        if (metaDonneesDescriptor.getEvenement() == null || metaDonneesDescriptor.getVersion() == null) {
            throw new NuxeoException("Metadonnées de la communication " + typeEvenement + " non trouvées");
        }

        DocumentModel dossierDoc = null;
        if (eventDocPrecedent != null) {
            dossierDoc = session.getDocument(eventDocPrecedent.getParentRef());
        }

        Map<String, PropertyDescriptor> mapProperty = metaDonneesDescriptor.getEvenement().getProperty();

        Evenement event = eventDoc.getAdapter(Evenement.class);
        event.setTypeEvenement(typeEvenement);
        EppPrincipal principal = (EppPrincipal) session.getPrincipal();
        // RG_EVT_INI_03
        event.setEmetteur(principal.getInstitutionId());

        eventDoc = metaDonneesService.remapDefaultMetaDonnees(eventDocPrecedent, eventDoc, dossierDoc, mapProperty);

        // RG_EVT_INI_04
        if (StringUtils.isNotEmpty(event.getDestinataire())) {
            // si emetteur et destinataire sont settes on met les autres en copie
            PropertyDescriptor pCopie = mapProperty.get(SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY);
            if (pCopie != null) {
                List<String> institutions = pCopie.getListInstitutions();
                institutions.remove(event.getEmetteur());
                institutions.remove(event.getDestinataire());
                event.setDestinataireCopie(institutions);
            }
        } else if (event.getDestinataireCopie() != null && !event.getDestinataireCopie().isEmpty()) {
            // si emetteur et destinataireCopie sont settes on regarde s'il reste q'une institution
            // pour la mettre en destinataire
            PropertyDescriptor pCopie = mapProperty.get(SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY);
            if (pCopie != null) {
                List<String> institutions = pCopie.getListInstitutions();
                institutions.remove(event.getEmetteur());
                institutions.removeAll(event.getDestinataireCopie());
                if (institutions.size() == 1) {
                    event.setDestinataire(institutions.get(0));
                }
            }
        }

        // creation et remap de la version
        DocumentModel versionDoc = SolonEppServiceLocator.getVersionService().createBareVersion(session);
        mapProperty = metaDonneesDescriptor.getVersion().getProperty();

        versionDoc = metaDonneesService.remapDefaultMetaDonnees(lastVersionDoc, versionDoc, dossierDoc, mapProperty);

        // Gestion des valeurs par défaut conditionnelles
        Map<String, PropertyDescriptor> mapAllProperty = new HashMap<>();
        mapAllProperty.putAll(metaDonneesDescriptor.getEvenement().getProperty());
        mapAllProperty.putAll(metaDonneesDescriptor.getVersion().getProperty());
        metaDonneesService.remapConditionnelMetaDonnees(eventDoc, versionDoc, mapAllProperty);

        // Gestion des destinataires des alertes
        Version version = versionDoc.getAdapter(Version.class);

        Evenement evt = eventDoc.getAdapter(Evenement.class);
        if (
            Arrays
                .asList(
                    EvenementType.ALERTE,
                    EvenementType.ALERTE_01,
                    EvenementType.ALERTE_02,
                    EvenementType.ALERTE_03,
                    EvenementType.ALERTE_04,
                    EvenementType.ALERTE_05,
                    EvenementType.ALERTE_06,
                    EvenementType.ALERTE_07,
                    EvenementType.ALERTE_08,
                    EvenementType.ALERTE_09,
                    EvenementType.ALERTE_10,
                    EvenementType.ALERTE_11,
                    EvenementType.ALERTE_12,
                    EvenementType.ALERTE_13,
                    EvenementType.ALERTE_14,
                    EvenementType.ALERTE_15
                )
                .contains(EvenementType.fromValue(typeEvenement)) &&
            eventDocPrecedent != null &&
            !version.isPositionAlerte()
        ) {
            Evenement lastEvt = eventDocPrecedent.getAdapter(Evenement.class);

            evt.setDestinataire(lastEvt.getEmetteur());
            evt.setDestinataireCopie(lastEvt.getDestinataireCopie());
        }

        if (EvenementType.SD_02.equals(EvenementType.fromValue(typeEvenement))) {
            Evenement lastEvt = eventDocPrecedent != null ? eventDocPrecedent.getAdapter(Evenement.class) : null;
            if (lastEvt != null) {
                evt.setDestinataire(lastEvt.getEmetteur());
            }
        } else if (EvenementType.LEX_40.equals(EvenementType.fromValue(typeEvenement))) {
            Evenement lastEvt = eventDocPrecedent != null ? eventDocPrecedent.getAdapter(Evenement.class) : null;
            if (
                lastVersionDoc != null &&
                lastEvt != null &&
                EvenementType.EVT_11.equals(EvenementType.fromValue(lastEvt.getTypeEvenement()))
            ) {
                Version lastVersion = lastVersionDoc.getAdapter(Version.class);
                version.setDateRefusASsemblee1(lastVersion.getDateRefusEngagementProcedure());
            } else {
                version.setDateRefusASsemblee1(null);
            }
        }

        InitialiserEvenementResponse initialiserEvenementResponse = initialiserEvenementContext.getInitialiserEvenementResponse();

        if (eventDocPrecedent != null) {
            initialiserEvenementResponse.setDossierDoc(session.getDocument(eventDocPrecedent.getParentRef()));
        } else {
            dossierDoc = SolonEppServiceLocator.getDossierService().createBareDossier(session);
            initialiserEvenementResponse.setDossierDoc(dossierDoc);
        }
        initialiserEvenementResponse.setEvenementDoc(eventDoc);
        initialiserEvenementResponse.setVersionDoc(versionDoc);
    }

    private DocumentModel findEvenementByTitle(CoreSession session, String idEvenement) {
        EvenementCriteria evenementCriteria = new EvenementCriteria();
        evenementCriteria.setEvenementId(idEvenement);
        EvenementDao evenementDao = new EvenementDao(session);
        return evenementDao.getSingleEvenementByCriteria(evenementCriteria);
    }

    @Override
    public List<DocumentModel> getEvenementsRacineDuDossier(CoreSession session, DocumentModel dossierDoc) {
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        EvenementCriteria evenementCriteria = new EvenementCriteria();
        evenementCriteria.setDossierId(dossier.getTitle());
        EvenementDao evenementDao = new EvenementDao(session);
        return evenementDao.getEvenementsRacine(evenementCriteria);
    }

    @Override
    public List<DocumentModel> getEvenementDossierList(CoreSession session, DocumentModel dossierDoc) {
        Dossier dossier = dossierDoc.getAdapter(Dossier.class);

        EvenementCriteria evenementCriteria = new EvenementCriteria();
        evenementCriteria.setDossierId(dossier.getTitle());
        EvenementDao evenementDao = new EvenementDao(session);
        return evenementDao.findEvenementByCriteria(evenementCriteria);
    }

    @Override
    public void envoyerMel(
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
    ) {
        // Détermine l'objet et le corps du mail
        if (StringUtils.isBlank(expediteurId)) {
            throw new NuxeoException("Pas d'expéditeur");
        }

        if (StringUtils.isBlank(destinataires)) {
            throw new NuxeoException("Pas de destinataire");
        }

        if (StringUtils.isBlank(objet)) {
            throw new NuxeoException("Pas d'objet");
        }

        if (StringUtils.isBlank(corps)) {
            throw new NuxeoException("Pas de corps du message");
        }

        // Mails destinaires
        List<Address> destinairesAddress = new ArrayList<>();
        Address address = null;
        for (String destinataire : destinataires.split(",|;| ")) {
            if (StringUtils.isNotBlank(destinataire)) {
                try {
                    address = new InternetAddress(destinataire.trim());
                } catch (AddressException e) {
                    LOGGER.error(session, EppLogEnumImpl.FAIL_ADD_ADDRESS_TEC, "Adresse : " + destinataire.trim());
                }
                destinairesAddress.add(address);
            }
        }

        if (destinairesAddress.isEmpty()) {
            throw new NuxeoException("Pas de destinataire");
        }

        // Expéditeur
        UserManager userManager = STServiceLocator.getUserManager();
        DocumentModel userDoc = userManager.getUserModel(expediteurId);
        Map<String, Object> paramMap = new HashMap<>();
        if (userDoc != null) {
            STUser user = userDoc.getAdapter(STUser.class);
            paramMap.put(
                "expediteur",
                StringUtils.trimToEmpty(user.getEmail()) +
                " (" +
                StringUtils.trimToEmpty(user.getLastName()) +
                " " +
                StringUtils.trimToEmpty(user.getFirstName()) +
                ")"
            );
        }

        // Mail en copie
        List<Address> copiesAddress = new ArrayList<>();
        if (!StringUtils.isBlank(copie)) {
            try {
                copiesAddress.add(new InternetAddress(copie.trim()));
            } catch (AddressException e) {
                LOGGER.error(
                    session,
                    EppLogEnumImpl.FAIL_ADD_ADDRESS_TEC,
                    "Erreur lors de l'ajout de l'adresse en copie : " + copie.trim()
                );
            }
        }

        paramMap.put("corps", corps);
        String mailCorps = STServiceLocator
            .getSTParametreService()
            .getParametreValue(session, SolonEppParametreConstant.MAIL_TRANSMETTRE_EVENEMENT_CORPS);

        List<TemplatePropertyDTOImpl> metaDonneesList = new ArrayList<>();
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        MetaDonneesDescriptor metaDescriptor = SolonEppServiceLocator
            .getMetaDonneesService()
            .getEvenementType(evenement.getTypeEvenement());

        EvenementMetaDonneesDescriptor evtMetaDescriptor = metaDescriptor.getEvenement();
        Map<String, PropertyDescriptor> evtPropertyMap = evtMetaDescriptor.getProperty();

        for (String property : evtPropertyMap.keySet()) {
            TemplatePropertyDTOImpl evtProperty = new TemplatePropertyDTOImpl();
            PropertyDescriptor propertyDescriptor = evtPropertyMap.get(property);
            evtProperty.setTitle(propertyDescriptor.getName());

            Object value = null;
            if (
                propertyDescriptor.getName().equals(STSchemaConstant.DUBLINCORE_TITLE_PROPERTY) ||
                propertyDescriptor.getName().equals(STSchemaConstant.DUBLINCORE_DESCRIPTION_PROPERTY)
            ) {
                value = evenementDoc.getProperty(STSchemaConstant.DUBLINCORE_SCHEMA, propertyDescriptor.getName());
            } else {
                value = evenementDoc.getProperty(SolonEppSchemaConstant.EVENEMENT_SCHEMA, propertyDescriptor.getName());
            }

            evtProperty = getEvenementParam(session, property, value);

            metaDonneesList.add(evtProperty);
        }

        VersionMetaDonneesDescriptor versionMetaDescriptor = metaDescriptor.getVersion();
        Map<String, PropertyDescriptor> versionPropertyMap = versionMetaDescriptor.getProperty();

        for (String property : versionPropertyMap.keySet()) {
            TemplatePropertyDTOImpl templateProperty = new TemplatePropertyDTOImpl();
            PropertyDescriptor propertyDescriptor = versionPropertyMap.get(property);
            templateProperty.setTitle(propertyDescriptor.getName());

            Object value = null;
            if (
                propertyDescriptor.getName().equals(STSchemaConstant.DUBLINCORE_TITLE_PROPERTY) ||
                propertyDescriptor.getName().equals(STSchemaConstant.DUBLINCORE_DESCRIPTION_PROPERTY)
            ) {
                value = versionDoc.getProperty(STSchemaConstant.DUBLINCORE_SCHEMA, propertyDescriptor.getName());
            } else {
                value = versionDoc.getProperty(SolonEppSchemaConstant.VERSION_SCHEMA, propertyDescriptor.getName());
            }

            templateProperty = getEvenementParam(session, property, value);

            metaDonneesList.add(templateProperty);
        }

        paramMap.put("metadonnees", metaDonneesList);
        paramMap.put("id_evenement", evenement.getTitle());

        // traitement des pièces jointes
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        PieceJointeFichierService pjFichierService = SolonEppServiceLocator.getPieceJointeFichierService();
        List<TemplatePropertyDTOImpl> pieceJointeDtoList = new ArrayList<>();
        List<ExportBlob> files = new ArrayList<>();
        TemplatePropertyDTOImpl templateProperty = null;
        if (pjDocList != null) {
            for (DocumentModel pjDoc : pjDocList) {
                PieceJointe pj = pjDoc.getAdapter(PieceJointe.class);
                List<DocumentModel> pjfDocList = pjFichierService.findAllPieceJointeFichier(session, pjDoc);

                for (DocumentModel pjfDoc : pjfDocList) {
                    templateProperty = new TemplatePropertyDTOImpl();
                    PieceJointeFichier pjf = pjfDoc.getAdapter(PieceJointeFichier.class);
                    String valueStr = vocService.getEntryLabel(
                        SolonEppVocabularyConstant.VOCABULARY_PIECE_JOINTE_DIRECTORY,
                        pj.getTypePieceJointe()
                    );
                    templateProperty.setTitle(valueStr);
                    templateProperty.setValue(
                        StringUtils.defaultString(pj.getNom()) +
                        " - " +
                        pjf.getSafeFilename() +
                        " - " +
                        StringUtils.defaultString(pj.getUrl())
                    );
                    files.add(new ExportBlob(pj.getNom(), pjf.getContent()));
                    pieceJointeDtoList.add(templateProperty);
                }
            }

            paramMap.put("piecesjointes", pieceJointeDtoList);
        }

        // Envoie l'email au destinataire de la notification
        final STMailService mailService = STServiceLocator.getSTMailService();
        mailService.sendTemplateHtmlMailWithAttachedFiles(
            destinairesAddress,
            copiesAddress,
            objet,
            mailCorps,
            paramMap,
            files,
            mailWorkAfterCommit
        );
    }

    @Override
    public void envoyerMel(
        CoreSession session,
        String expediteurId,
        String objet,
        String corps,
        String destinataires,
        String copie,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        DocumentModel dossierDoc,
        List<DocumentModel> pjDocList
    ) {
        envoyerMel(
            session,
            expediteurId,
            objet,
            corps,
            destinataires,
            copie,
            evenementDoc,
            versionDoc,
            dossierDoc,
            pjDocList,
            true
        );
    }

    private TemplatePropertyDTOImpl getEvenementParam(CoreSession session, String property, Object value) {
        final VocabularyService vocService = STServiceLocator.getVocabularyService();
        TemplatePropertyDTOImpl templateDto = new TemplatePropertyDTOImpl();

        TableReferenceService tableRefService = SolonEppServiceLocator.getTableReferenceService();

        String valueStr = null;

        templateDto.setTitle(ResourceHelper.getString("label.epp.metadonnee." + property));

        if (value == null) {
            templateDto.setValue("");
            return templateDto;
        }

        if (property.equals("auteur") || property.equals("co_auteur") || property.equals("coauteur")) {
            if (value instanceof String) {
                String id = (String) value;

                DocumentModel mandatDoc = tableRefService.getMandatById(session, id);
                if (mandatDoc != null) {
                    Mandat mandat = mandatDoc.getAdapter(Mandat.class);
                    String idIdentite = mandat.getIdentite();
                    DocumentModel identiteDoc = tableRefService.getIdentiteById(session, idIdentite);
                    if (identiteDoc != null) {
                        Identite identite = identiteDoc.getAdapter(Identite.class);
                        valueStr = identite.getFullName();
                    }
                }
            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> idList = (List<String>) value;
                List<String> identiteList = new ArrayList<>();

                for (String id : idList) {
                    DocumentModel mandatDoc = tableRefService.getMandatById(session, id);
                    if (mandatDoc != null) {
                        Mandat mandat = mandatDoc.getAdapter(Mandat.class);
                        String idIdentite = mandat.getIdentite();
                        DocumentModel identiteDoc = tableRefService.getIdentiteById(session, idIdentite);
                        if (identiteDoc != null) {
                            Identite identite = identiteDoc.getAdapter(Identite.class);
                            identiteList.add(identite.getFullName());
                        }
                    }
                }
                if (!identiteList.isEmpty()) {
                    valueStr = StringUtils.join(identiteList, ", ");
                }
            }
        } else if (property.equals(SolonEppSchemaConstant.DOSSIER_TYPE_LOI_PROPERTY)) {
            valueStr = vocService.getEntryLabel(SolonEppVocabularyConstant.TYPE_LOI_VOCABULARY, (String) value);
        } else if (property.equals(SolonEppSchemaConstant.DOSSIER_NATURE_LOI_PROPERTY)) {
            valueStr = vocService.getEntryLabel(SolonEppVocabularyConstant.NATURE_LOI_VOCABULARY, (String) value);
        } else if (property.equals(SolonEppSchemaConstant.VERSION_NATURE_RAPPORT_PROPERTY)) {
            valueStr = vocService.getEntryLabel(SolonEppVocabularyConstant.NATURE_RAPPORT_VOCABULARY, (String) value);
        } else if (property.equals(SolonEppSchemaConstant.DOSSIER_NIVEAU_LECTURE_PROPERTY)) {
            valueStr = vocService.getEntryLabel(SolonEppVocabularyConstant.NIVEAU_LECTURE_VOCABULARY, (String) value);
        } else if (property.equals(SolonEppSchemaConstant.VERSION_RUBRIQUE)) {
            valueStr = vocService.getEntryLabel(SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY, (String) value);
        } else if (
            property.equals(SolonEppSchemaConstant.DOSSIER_COMMISSION_SAISIE_AU_FOND_PROPERTY) ||
            property.equals(SolonEppSchemaConstant.DOSSIER_COMMISSION_SAISIE_POUR_AVIS_PROPERTY)
        ) {
            if (value instanceof String) {
                DocumentModel organismeDoc = tableRefService.getOrganismeById(session, value.toString());
                if (organismeDoc != null) {
                    valueStr = organismeDoc.getAdapter(Organisme.class).getNom();
                } else {
                    LOGGER.warn(
                        session,
                        STLogEnumImpl.FAIL_GET_EVENT_PARAM_TEC,
                        "Nom de l'organisme non trouvé - " + value
                    );
                    valueStr = "";
                }
            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> idList = (List<String>) value;
                List<String> organismeList = new ArrayList<>();

                for (String id : idList) {
                    DocumentModel organismeDoc = tableRefService.getOrganismeById(session, id);
                    if (organismeDoc != null) {
                        organismeList.add(organismeDoc.getAdapter(Organisme.class).getNom());
                    } else {
                        LOGGER.warn(
                            session,
                            STLogEnumImpl.FAIL_GET_EVENT_PARAM_TEC,
                            "Nom de l'organisme non trouvé - " + value
                        );
                    }
                }
                if (!organismeList.isEmpty()) {
                    valueStr = StringUtils.join(organismeList, ", ");
                }
            }
        } else if (property.equals(SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY)) {
            if (value instanceof String) {
                valueStr = ResourceHelper.getString((String) value);
            } else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> allDestCopie = (List<String>) value;
                valueStr = StringUtils.join(allDestCopie, ", ");
            }
        } else if (value instanceof String) {
            valueStr = ResourceHelper.getString((String) value);
        } else if (value instanceof Calendar) {
            valueStr = SolonDateConverter.DATE_DASH.format((Calendar) value);
        } else if (value instanceof Boolean) {
            if ((Boolean) value) {
                valueStr = "vrai";
            } else {
                valueStr = "faux";
            }
        } else if (value instanceof Integer) {
            valueStr = "" + value;
        } else if (value instanceof Long) {
            valueStr = "" + value.toString();
        }

        templateDto.setValue(valueStr);

        return templateDto;
    }

    @Override
    public boolean isIdEvenementValid(String idEvenement) {
        String regex = "^[0-9a-zA-Z_\\-]+$";
        return idEvenement.matches(regex);
    }
}
