package fr.dila.solonepp.core.service;

import com.google.common.collect.Sets;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.VersionCriteria;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.NumeroVersion;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.dto.VersionSelectionDTO;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementDistributionService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.VersionNumeroService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.api.service.version.AccuserReceptionContext;
import fr.dila.solonepp.api.service.version.AccuserReceptionRequest;
import fr.dila.solonepp.api.service.version.AccuserReceptionResponse;
import fr.dila.solonepp.api.service.version.ValiderVersionContext;
import fr.dila.solonepp.api.service.version.ValiderVersionRequest;
import fr.dila.solonepp.api.service.version.ValiderVersionResponse;
import fr.dila.solonepp.core.assembler.VersionAssembler;
import fr.dila.solonepp.core.dao.VersionDao;
import fr.dila.solonepp.core.validator.EvenementValidator;
import fr.dila.solonepp.core.validator.VersionValidator;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Implémentation du service des versions.
 *
 * @author jtremeaux
 */
public class VersionServiceImpl implements VersionService {
    private static final String VERSION_MINEUR_INVALIDE =
        "Version brouillon en attente de validation trouvée avec un numéro de version mineur invalide: ";

    private static final String VERSION_MAJEUR_INVALIDE =
        "Version brouillon en attente de validation trouvée avec un numéro de version majeur invalide: ";

    private static final String VERSION_NON_TROUVE = "Version non trouvée";

    private static final String COMMUNICATION_NON_TROUVE = "Communication non trouvé: ";

    private static final String VERSION_ACTUELLE_INTROUVABLE = "Impossible de trouver la version publiée actuelle";

    private static final String ETAT = " à l'état ";

    private static final String VERSION_A_MODIFIER_ETAT_BROUILLON =
        "La version à modifier doit être à l'état brouillon";

    private static final String POUR_RECTIFICATION_COMMUNICATION = "Pour rectification : communication ";

    private static final String POUR_COMPLETION_COMMUNICATION = "Pour complétion : communication ";

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(VersionServiceImpl.class);

    @Override
    public DocumentModel createBareVersion(CoreSession session) {
        return session.createDocumentModel(SolonEppConstant.VERSION_DOC_TYPE);
    }

    protected List<DocumentModel> findVersionVisible(
        CoreSession session,
        DocumentModel evenementDoc,
        String messageType,
        NumeroVersion numeroVersion
    ) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(evenement.getTitle());
        versionCriteria.setOrderVersionDesc(true);
        versionCriteria.setNumeroVersionEquals(numeroVersion);

        List<String> currentLifeCycleStateIn = findVisibleLifeCycleStates(messageType);
        versionCriteria.setCurrentLifeCycleStateIn(currentLifeCycleStateIn);

        VersionDao versionDao = new VersionDao(session);
        return versionDao.findVersionByCriteria(versionCriteria);
    }

    /**
     * liste des lifecycle en fonction du type de message
     *
     * @param messageType
     * @return
     */
    private List<String> findVisibleLifeCycleStates(String messageType) {
        List<String> currentLifeCycleStateIn = new ArrayList<>();
        if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE.equals(messageType)) {
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_BROUILLON_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_PUBLIE_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_ATTENTE_VALIDATION_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_OBSOLETE_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_REJETE_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_ABANDONNE_STATE);
        } else if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE.equals(messageType)) {
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_PUBLIE_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_ATTENTE_VALIDATION_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_OBSOLETE_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_REJETE_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_ABANDONNE_STATE);
        } else if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE.equals(messageType)) {
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_PUBLIE_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_OBSOLETE_STATE);
        } else {
            throw new NuxeoException("Type de message inconnu: " + messageType);
        }
        return currentLifeCycleStateIn;
    }

    @Override
    public List<VersionSelectionDTO> findVersionSelectionnable(
        CoreSession session,
        DocumentModel evenementDoc,
        String messageType
    ) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(evenement.getTitle());
        versionCriteria.setOrderVersionDesc(true);

        List<String> currentLifeCycleStateIn = findVisibleLifeCycleStates(messageType);
        versionCriteria.setCurrentLifeCycleStateIn(currentLifeCycleStateIn);

        VersionDao versionDao = new VersionDao(session);
        return versionDao.findVersionSelectionnable(versionCriteria);
    }

    @Override
    public List<DocumentModel> findVersionVisible(CoreSession session, DocumentModel evenementDoc, String messageType) {
        return findVersionVisible(session, evenementDoc, messageType, null);
    }

    @Override
    public DocumentModel getVersionActive(CoreSession session, DocumentModel evenementDoc, String messageType) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(evenement.getTitle());
        versionCriteria.setOrderVersionDesc(true);

        List<String> currentLifeCycleStateIn = new ArrayList<>();
        if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE.equals(messageType)) {
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_BROUILLON_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_PUBLIE_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_ATTENTE_VALIDATION_STATE);
        } else if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE.equals(messageType)) {
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_PUBLIE_STATE);
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_ATTENTE_VALIDATION_STATE);
        } else if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE.equals(messageType)) {
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_PUBLIE_STATE);
        } else if (messageType == null) {
            currentLifeCycleStateIn.add(SolonEppLifecycleConstant.VERSION_PUBLIE_STATE);
        } else {
            throw new NuxeoException("Type de message inconnu: " + messageType);
        }
        versionCriteria.setCurrentLifeCycleStateIn(currentLifeCycleStateIn);

        VersionDao versionDao = new VersionDao(session);
        List<DocumentModel> versionDocList = versionDao.findVersionByCriteria(versionCriteria);
        if (versionDocList == null || versionDocList.isEmpty()) {
            return null;
        }

        return versionDocList.iterator().next();
    }

    @Override
    public DocumentModel getVersionBrouillonUlterieure(CoreSession session, DocumentModel versionDoc) {
        Version version = versionDoc.getAdapter(Version.class);
        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(version.getEvenement());
        versionCriteria.setOrderVersionDesc(true);
        versionCriteria.setCurrentLifeCycleState(SolonEppLifecycleConstant.VERSION_BROUILLON_STATE);
        versionCriteria.setMajorVersionEquals(version.getNumeroVersion().getMajorVersion());
        versionCriteria.setMinorVersionStrictlyGreater(version.getNumeroVersion().getMinorVersion());

        VersionDao versionDao = new VersionDao(session);
        List<DocumentModel> versionDocList = versionDao.findVersionByCriteria(versionCriteria);
        if (versionDocList == null || versionDocList.isEmpty()) {
            return null;
        }

        return versionDocList.iterator().next();
    }

    @Override
    public DocumentModel getVersionVisible(
        CoreSession session,
        DocumentModel evenementDoc,
        String messageType,
        NumeroVersion numeroVersion
    ) {
        List<DocumentModel> versionVisibleDocList = findVersionVisible(
            session,
            evenementDoc,
            messageType,
            numeroVersion
        );
        if (CollectionUtils.isEmpty(versionVisibleDocList)) {
            return null;
        }
        return versionVisibleDocList.iterator().next();
    }

    @Override
    public DocumentModel getLastVersion(CoreSession session, String evenementId) {
        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(evenementId);
        versionCriteria.setOrderVersionDesc(true);

        VersionDao versionDao = new VersionDao(session);
        return versionDao.getSingleVersionByCriteria(versionCriteria);
    }

    @Override
    public DocumentModel getVersionCourante(CoreSession session, String evenementId) {
        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(evenementId);
        versionCriteria.setVersionCourante(true);

        VersionDao versionDao = new VersionDao(session);
        return versionDao.getSingleVersionByCriteria(versionCriteria);
    }

    @Override
    public DocumentModel getLastVersionNotEquals(CoreSession session, String evenementId, NumeroVersion numeroVersion) {
        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(evenementId);
        versionCriteria.setNumeroVersionNotEquals(numeroVersion);
        versionCriteria.setOrderVersionDesc(true);

        VersionDao versionDao = new VersionDao(session);
        return versionDao.getSingleVersionByCriteria(versionCriteria);
    }

    @Override
    public void setVersionCouranteToFalse(CoreSession session, String evenementId) {
        DocumentModel versionDoc = getVersionCourante(session, evenementId);
        if (versionDoc != null) {
            Version version = versionDoc.getAdapter(Version.class);
            version.setVersionCourante(false);
            session.saveDocument(versionDoc);
        }
    }

    private DocumentModel getVersion(CoreSession session, String evenementId, Long majorVersion, Long minorVersion) {
        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(evenementId);
        versionCriteria.setNumeroVersionEquals(new NumeroVersion(majorVersion, minorVersion));
        versionCriteria.setOrderVersionDesc(true);

        VersionDao versionDao = new VersionDao(session);
        return versionDao.getSingleVersionByCriteria(versionCriteria);
    }

    /**
     * Retourne la liste des versions antérieures au numéro version précisé et n'ayant pas de date AR
     *
     * @param session
     * @param evenementId
     * @param majorVersion
     * @param minorVersion
     * @return
     */
    private List<DocumentModel> getPreviousVersionWithoutAR(
        CoreSession session,
        String evenementId,
        Long majorVersion,
        Long minorVersion
    ) {
        List<DocumentModel> resultList = new ArrayList<>();

        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(evenementId);
        versionCriteria.setOrderVersionDesc(true);
        versionCriteria.setDateArNull(true);
        versionCriteria.setNumeroVersionNotEquals(new NumeroVersion(majorVersion, minorVersion));

        VersionDao versionDao = new VersionDao(session);
        List<DocumentModel> versionDocList = versionDao.findVersionByCriteria(versionCriteria);

        for (DocumentModel document : versionDocList) {
            Version version = document.getAdapter(Version.class);
            if (
                (version.getMajorVersion().equals(majorVersion) && version.getMinorVersion() < minorVersion) ||
                version.getMajorVersion() < majorVersion
            ) {
                resultList.add(document);
            }
        }

        return resultList;
    }

    @Override
    public DocumentModel creerVersionBrouillonInitiale(
        CoreSession session,
        final DocumentModel versionDoc,
        final DocumentModel evenementDoc
    ) {
        LOGGER.info(session, EppLogEnumImpl.CREATE_VERSION_TEC, "communication " + evenementDoc.getTitle());

        Version version = versionDoc.getAdapter(Version.class);

        // Valide les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);
        evenementValidator.validateEtatEvenement(
            evenementDoc,
            Collections.singleton(SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE)
        );
        evenementValidator.validateCreerBrouillon(evenementDoc);

        // Renseigne le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        NumeroVersion numeroVersion = versionNumeroService.getFirstNumeroVersion(false);
        version.setNumeroVersion(numeroVersion);

        // Initialise les propriétés de la version
        version.setHorodatage(Calendar.getInstance());
        version.setDateAr(null);
        version.setModeCreation(SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_INIT_VALUE);
        version.setTitle(numeroVersion.toString());
        version.setPieceJointePresente(false);
        versionDoc.setPathInfo(evenementDoc.getPathAsString(), numeroVersion.toString());

        // Renseigne le nom du document événement automatiquement
        version.setEvenement(evenementDoc.getTitle());

        // Crée le document version
        final DocumentModel versionCreatedDoc = session.createDocument(versionDoc);

        // Transitionne vers l'état initial de la version (brouillon ou publié)
        versionCreatedDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_BROUILLON_FROM_INIT_TRANSITION);

        // Synchronise le message de l'émetteur avec la version brouillon
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterCreerBrouillon(session, evenementDoc, versionCreatedDoc);

        return versionCreatedDoc;
    }

    @Override
    public DocumentModel creerVersionBrouillonPourCompletion(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.CREATE_VERSION_TEC,
            POUR_COMPLETION_COMMUNICATION + evenementDoc.getTitle() + " version " + currentVersion.getNumeroVersion()
        );

        // Valide les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);
        evenementValidator.validateEtatEvenement(
            evenementDoc,
            Sets.newHashSet(
                SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE,
                SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE
            )
        );
        evenementValidator.validateCompleter(evenementDoc);

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final DocumentModel lastVersionDoc = getLastVersion(session, evenementDoc.getTitle());
        NumeroVersion numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, false);

        // Copie la version
        DocumentModel versionCreatedDoc = session.copy(
            currentVersionDoc.getRef(),
            currentVersionDoc.getParentRef(),
            numeroVersion.toString()
        );
        Version versionCreated = versionCreatedDoc.getAdapter(Version.class);

        // Modifie les propriétés de la version
        versionCreated.setHorodatage(Calendar.getInstance());
        // EVT45 : conserver l'accusé de réception de la version précédente
        if ("EVT45".equals(evenementDoc.getAdapter(Evenement.class).getTypeEvenement())) {
            versionCreated.setDateAr(currentVersion.getDateAr());
        } else {
            versionCreated.setDateAr(null);
        }
        versionCreated.setModeCreation(SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE);
        // FEV319 Nature version COMPLETEE
        versionCreated.setNature(SolonEppSchemaConstant.VERSION_NATURE_COMPLETEE_PROPERTY);
        versionCreated.setVersionCourante(false);
        versionCreated.setTitle(numeroVersion.toString());
        versionCreated.setNumeroVersion(numeroVersion);

        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, versionCreatedDoc, evenementDoc, false, true);

        compareVersion(versionCreatedDoc, currentVersionDoc);

        // Persiste les modifications de la version
        session.saveDocument(versionCreatedDoc);

        // Transitionne vers l'état brouillon
        versionCreatedDoc.followTransition(SolonEppLifecycleConstant.VERSION_BACK_TO_BROUILLON_TRANSITION);

        // Synchronise le message de l'émetteur avec la version créée
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterCreerBrouillon(session, evenementDoc, versionCreatedDoc);

        return versionCreatedDoc;
    }

    @Override
    public DocumentModel creerVersionBrouillonPourRectification(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.CREATE_VERSION_TEC,
            POUR_RECTIFICATION_COMMUNICATION + evenementDoc.getTitle() + " version " + currentVersion.getNumeroVersion()
        );

        // Valide les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);
        evenementValidator.validateEtatEvenement(
            evenementDoc,
            Sets.newHashSet(
                SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE,
                SolonEppLifecycleConstant.EVENEMENT_INSTANCE_STATE
            )
        );
        evenementValidator.validateRectifier(evenementDoc);

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final DocumentModel lastVersionDoc = getLastVersion(session, evenementDoc.getTitle());
        NumeroVersion numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, false);

        // Copie la version
        DocumentModel versionCreatedDoc = session.copy(
            currentVersionDoc.getRef(),
            currentVersionDoc.getParentRef(),
            numeroVersion.toString()
        );
        Version versionCreated = versionCreatedDoc.getAdapter(Version.class);

        // Modifie les propriétés de la version
        versionCreated.setHorodatage(Calendar.getInstance());
        versionCreated.setDateAr(null);
        versionCreated.setModeCreation(SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE);
        // FEV319 Nature version RECTIFICATION
        versionCreated.setNature(SolonEppSchemaConstant.VERSION_NATURE_RECTIFIEE_PROPERTY);
        versionCreated.setVersionCourante(false);
        versionCreated.setTitle(numeroVersion.toString());
        versionCreated.setNumeroVersion(numeroVersion);

        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, versionCreatedDoc, evenementDoc, false, false);

        compareVersion(versionCreatedDoc, currentVersionDoc);

        // Persiste les modifications de la version
        session.saveDocument(versionCreatedDoc);

        // Transitionne vers l'état brouillon
        versionCreatedDoc.followTransition(SolonEppLifecycleConstant.VERSION_BACK_TO_BROUILLON_TRANSITION);

        // Synchronise le message de l'émetteur avec la version créée
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterCreerBrouillon(session, evenementDoc, versionCreatedDoc);

        return versionCreatedDoc;
    }

    @Override
    public DocumentModel modifierVersionBrouillonInitiale(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.UPDATE_VERSION_TEC,
            "Brouillon initial : Version : " +
            currentVersion.getNumeroVersion() +
            "  communication: " +
            evenementDoc.getTitle()
        );

        // Vérifie que la version est à l'état brouillon
        if (!currentVersion.isEtatBrouillon()) {
            throw new NuxeoException(VERSION_A_MODIFIER_ETAT_BROUILLON);
        }

        // Vérifie que c'est la version brouillon initiale
        if (
            !SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_INIT_VALUE.equals(currentVersion.getModeCreation())
        ) {
            throw new NuxeoException("Seule la version brouillon initiale peut être modifiée");
        }

        // Valide les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);
        evenementValidator.validateEtatEvenement(
            evenementDoc,
            Collections.singleton(SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE)
        );

        // Modifie les propriétés de la version
        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, currentVersionDoc, evenementDoc, true, false);

        // maj de l'horodatage
        currentVersionDoc.getAdapter(Version.class).setHorodatage(Calendar.getInstance());

        // Sauvegarde le document version modifié
        currentVersionDoc = session.saveDocument(currentVersionDoc);

        // Synchronise la date des messages avec la version
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterEnregister(session, evenementDoc, currentVersionDoc);

        return currentVersionDoc;
    }

    @Override
    public DocumentModel modifierVersionBrouillonPourCompletion(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.UPDATE_VERSION_TEC,
            "Brouillon pour complétion : Version : " +
            currentVersion.getNumeroVersion() +
            "  Communication : " +
            evenementDoc.getTitle()
        );
        // Vérifie que la version est à l'état brouillon
        if (!currentVersion.isEtatBrouillon()) {
            throw new NuxeoException(VERSION_A_MODIFIER_ETAT_BROUILLON);
        }

        // Vérifie que la version est une version brouillon pour complétion
        if (
            !SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(
                currentVersion.getModeCreation()
            )
        ) {
            throw new NuxeoException("Seule la version brouillon pour complétion peut être modifiée");
        }

        // Vérifie les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);

        // Modifie les propriétés de la version
        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, currentVersionDoc, evenementDoc, false, true);

        // maj de l'horodatage
        currentVersionDoc.getAdapter(Version.class).setHorodatage(Calendar.getInstance());

        compareVersion(
            currentVersionDoc,
            getVersionActive(session, evenementDoc, SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE)
        );

        // Sauvegarde le document version modifié
        currentVersionDoc = session.saveDocument(currentVersionDoc);

        // Synchronise la date des messages avec la version
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterEnregister(session, evenementDoc, currentVersionDoc);

        return currentVersionDoc;
    }

    @Override
    public DocumentModel modifierVersionBrouillonPourRectification(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.UPDATE_VERSION_TEC,
            "Brouillon pour rectification : Version : " +
            currentVersion.getNumeroVersion() +
            " Communication: " +
            evenementDoc.getTitle()
        );

        // Vérifie que la version est à l'état brouillon
        if (!currentVersion.isEtatBrouillon()) {
            throw new NuxeoException(VERSION_A_MODIFIER_ETAT_BROUILLON);
        }

        // Vérifie que la version est une version brouillon pour rectification
        if (
            !SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(
                currentVersion.getModeCreation()
            )
        ) {
            throw new NuxeoException("Seule la version brouillon pour rectification peut être modifiée");
        }

        // Vérifie les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);

        // Modifie les propriétés de la version
        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, currentVersionDoc, evenementDoc, false, false);

        // maj de l'horodatage
        currentVersionDoc.getAdapter(Version.class).setHorodatage(Calendar.getInstance());

        compareVersion(
            currentVersionDoc,
            getVersionActive(session, evenementDoc, SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE)
        );

        // Sauvegarde le document version modifié
        currentVersionDoc = session.saveDocument(currentVersionDoc);

        // Synchronise la date des messages avec la version
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterEnregister(session, evenementDoc, currentVersionDoc);

        return currentVersionDoc;
    }

    @Override
    public DocumentModel publierVersionInitiale(
        CoreSession session,
        final DocumentModel versionDoc,
        DocumentModel dossierDoc,
        final DocumentModel evenementDoc
    ) {
        LOGGER.info(session, EppLogEnumImpl.PUBLISH_VERSION_TEC, "communication " + evenementDoc.getTitle());

        Version version = versionDoc.getAdapter(Version.class);

        // Valide les données de l'événement
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);
        evenementValidator.validateEtatEvenement(
            evenementDoc,
            Sets.newHashSet(
                SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE,
                SolonEppLifecycleConstant.EVENEMENT_PUBLIE_STATE
            )
        );

        // Renseigne le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        NumeroVersion numeroVersion = versionNumeroService.getFirstNumeroVersion(true);
        version.setNumeroVersion(numeroVersion);

        // Initialise les propriétés de la version
        version.setHorodatage(Calendar.getInstance());
        version.setDateAr(null);
        version.setModeCreation(SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_INIT_VALUE);
        version.setTitle(numeroVersion.toString());
        version.setPieceJointePresente(false);
        versionDoc.setPathInfo(evenementDoc.getPathAsString(), numeroVersion.toString());

        // FEV319
        version.setVersionCourante(true);

        // Renseigne le nom du document événement automatiquement
        version.setEvenement(evenementDoc.getTitle());

        // Valide les données de la version
        VersionValidator versionValidator = new VersionValidator();
        versionValidator.validateMetaObligatoire(versionDoc, evenementDoc);

        // Crée le document version
        final DocumentModel versionCreatedDoc = session.createDocument(versionDoc);

        // Transitionne la version vers l'état publié
        versionCreatedDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_PUBLIE_FROM_INIT_TRANSITION);

        // Traite les événements de type alerte
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        evenementService.processEvenementAlerte(session, evenementDoc, dossierDoc, versionCreatedDoc);

        // Synchronise les messages avec la version publiée
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterPublier(session, dossierDoc, evenementDoc, versionCreatedDoc);

        return versionCreatedDoc;
    }

    @Override
    public DocumentModel publierVersionBrouillonInitiale(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.PUBLISH_VERSION_TEC,
            "Version" + currentVersion.getNumeroVersion() + " de la communication " + evenementDoc.getTitle()
        );

        // Vérifie les données de l'événément
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);
        evenementValidator.validateEtatEvenement(
            evenementDoc,
            Collections.singleton(SolonEppLifecycleConstant.EVENEMENT_BROUILLON_STATE)
        );

        // Vérifie que la version est à l'état brouillon
        if (!currentVersion.isEtatBrouillon()) {
            throw new NuxeoException("Seule une version à l'état brouillon peut être publiée");
        }

        // Vérifie que la version est une version brouillon initiale
        if (
            !SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_INIT_VALUE.equals(currentVersion.getModeCreation())
        ) {
            throw new NuxeoException("Seule la version brouillon initiale peut être publiée");
        }

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final DocumentModel lastVersionDoc = getLastVersion(session, evenementDoc.getTitle());
        NumeroVersion numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, true);

        // Modifie les propriétés de la version
        currentVersion.setHorodatage(Calendar.getInstance());
        currentVersion.setDateAr(null);
        currentVersion.setModeCreation(SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_INIT_VALUE);
        currentVersion.setTitle(numeroVersion.toString());
        currentVersion.setNumeroVersion(numeroVersion);

        // FEV319
        currentVersion.setVersionCourante(true);

        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, currentVersionDoc, evenementDoc, true, false);

        // Valide les nouvelles données de la version
        VersionValidator versionValidator = new VersionValidator();
        versionValidator.validateMetaObligatoire(currentVersionDoc, evenementDoc);

        // Persiste les modifications de la version
        session.saveDocument(currentVersionDoc);

        // Renomme le document version brouillon avec le nom de la version publiée
        currentVersionDoc =
            session.move(currentVersionDoc.getRef(), currentVersionDoc.getParentRef(), numeroVersion.toString());

        // Transitionne vers l'état publié
        currentVersionDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_PUBLIE_TRANSITION);

        // Synchronise les messages avec la version publiée
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterPublier(session, dossierDoc, evenementDoc, currentVersionDoc);

        return currentVersionDoc;
    }

    @Override
    public DocumentModel publierVersionPourCompletion(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.PUBLISH_VERSION_TEC,
            POUR_COMPLETION_COMMUNICATION + evenementDoc.getTitle()
        );

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
        evenementValidator.validateCompleter(evenementDoc);

        if (currentVersion.isEtatBrouillon()) {
            // Publication d'une version brouillon : vérifie que la version est une version brouillon pour complétion
            if (
                !SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(
                    currentVersion.getModeCreation()
                )
            ) {
                throw new NuxeoException("Seule la version brouillon pour complétion peut être modifiée");
            }
        } else if (!(currentVersion.isEtatPublie() || currentVersion.isEtatAttenteValidation())) {
            throw new NuxeoException(
                "Impossible de publier pour complétion la version: " +
                currentVersion.getTitle() +
                ETAT +
                currentVersionDoc.getCurrentLifeCycleState()
            );
        }

        // Transitionne la version publiée actuelle à l'état obsolète
        DocumentModel currentPublieVersion = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE
        );
        if (currentPublieVersion == null) {
            throw new NuxeoException(VERSION_ACTUELLE_INTROUVABLE);
        }
        currentPublieVersion.followTransition(SolonEppLifecycleConstant.VERSION_TO_OBSOLETE_TRANSITION);

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final DocumentModel lastVersionDoc = getLastVersion(session, evenementDoc.getTitle());
        NumeroVersion numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, true);

        // Copie la version
        DocumentModel versionPublieeDoc = null;
        if (currentVersion.isEtatBrouillon()) {
            // Renomme le document version brouillon avec le nom de la version publiée
            versionPublieeDoc =
                session.move(currentVersionDoc.getRef(), currentVersionDoc.getParentRef(), numeroVersion.toString());
        } else {
            versionPublieeDoc =
                session.copy(currentVersionDoc.getRef(), currentVersionDoc.getParentRef(), numeroVersion.toString());

            versionPublieeDoc.followTransition(SolonEppLifecycleConstant.VERSION_BACK_TO_BROUILLON_TRANSITION);
        }
        Version versionCreated = versionPublieeDoc.getAdapter(Version.class);

        // Modifie les propriétés de la version
        versionCreated.setHorodatage(Calendar.getInstance());
        versionCreated.setDateAr(null);
        versionCreated.setModeCreation(SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_COMPLETION_VALUE);
        // FEV319 Nature version COMPLETEE
        versionCreated.setNature(SolonEppSchemaConstant.VERSION_NATURE_COMPLETEE_PROPERTY);
        // FEV319 Cette version devient la version courante
        setVersionCouranteToFalse(session, evenementDoc.getTitle());
        versionCreated.setVersionCourante(true);

        versionCreated.setTitle(numeroVersion.toString());
        versionCreated.setNumeroVersion(numeroVersion);

        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, versionPublieeDoc, evenementDoc, false, true);

        // Valide les nouvelles données de la version sauf pour les JO-01
        if ("EVT45".equals(evenementDoc.getAdapter(Evenement.class).getTypeEvenement())) {
            VersionValidator versionValidator = new VersionValidator();
            versionValidator.validateMetaObligatoire(versionPublieeDoc, evenementDoc);
            // EVT45 : conserver l'accusé de réception de la version précédente
            versionCreated.setDateAr(currentVersion.getDateAr());
        }

        compareVersion(versionPublieeDoc, currentPublieVersion);

        // Persiste les modifications de la version
        session.saveDocument(versionPublieeDoc);

        // Transitionne vers l'état publié
        versionPublieeDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_PUBLIE_TRANSITION);

        // Synchronise les messages avec la version publiée
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.updateMessageAfterPublier(session, dossierDoc, evenementDoc, versionPublieeDoc);

        return versionPublieeDoc;
    }

    @Override
    public DocumentModel publierVersionPourRectification(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        boolean modeDelta
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.PUBLISH_VERSION_TEC,
            POUR_RECTIFICATION_COMMUNICATION + evenementDoc.getTitle()
        );

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
        evenementValidator.validateRectifier(evenementDoc);

        // Vérifie que la version est à l'état brouillon
        if (currentVersion.isEtatBrouillon()) {
            // Vérifie que la version est une version brouillon initiale
            if (
                !SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(
                    currentVersion.getModeCreation()
                )
            ) {
                throw new NuxeoException("Seule la version brouillon pour rectification peut être modifiée");
            }
        } else if (!(currentVersion.isEtatPublie() || currentVersion.isEtatAttenteValidation())) {
            throw new NuxeoException(
                "Impossible de publier pour rectification la version: " +
                currentVersion.getTitle() +
                ETAT +
                currentVersionDoc.getCurrentLifeCycleState()
            );
        }

        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        DocumentModel currentPublieVersion = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE
        );
        if (evenement.isEtatPublie()) {
            // Transitionne la version publiée actuelle à l'état obsolète
            if (currentPublieVersion == null) {
                throw new NuxeoException(VERSION_ACTUELLE_INTROUVABLE);
            }
            currentPublieVersion.followTransition(SolonEppLifecycleConstant.VERSION_TO_OBSOLETE_TRANSITION);
        }

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final DocumentModel lastVersionDoc = getLastVersion(session, evenementDoc.getTitle());
        NumeroVersion numeroVersion = null;

        // Copie la version
        DocumentModel versionPublieeDoc = null;
        if (evenement.isEtatPublie()) {
            numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, true);
        } else {
            if (currentVersion.isEtatBrouillon()) {
                numeroVersion = currentVersion.getNumeroVersion();
            } else {
                numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, false);
            }
        }

        if (currentVersion.isEtatBrouillon()) {
            if (evenement.isEtatPublie()) {
                // Renomme le document version brouillon avec le nom de la version publiée
                versionPublieeDoc =
                    session.move(
                        currentVersionDoc.getRef(),
                        currentVersionDoc.getParentRef(),
                        numeroVersion.toString()
                    );
            } else {
                versionPublieeDoc = currentVersionDoc;
            }
        } else {
            versionPublieeDoc =
                session.copy(currentVersionDoc.getRef(), currentVersionDoc.getParentRef(), numeroVersion.toString());

            versionPublieeDoc.followTransition(SolonEppLifecycleConstant.VERSION_BACK_TO_BROUILLON_TRANSITION);
        }
        Version versionPubliee = versionPublieeDoc.getAdapter(Version.class);
        versionPubliee.setHorodatage(Calendar.getInstance());

        // Modifie les propriétés de la version
        if (evenement.isEtatPublie()) {
            versionPubliee.setModeCreation(SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_RECTIFICATION_VALUE);
            // FEV319 Nature version RECTIFICATION
            versionPubliee.setNature(SolonEppSchemaConstant.VERSION_NATURE_RECTIFIEE_PROPERTY);
            // FEV319 Cette version devient la version courante, mise a false de la proprieté version courante sur
            // l'ancienne courante
            setVersionCouranteToFalse(session, evenementDoc.getTitle());
            versionPubliee.setVersionCourante(true);
        } else {
            if (modeDelta) {
                versionPubliee.setModeCreation(
                    SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DELTA_DEMANDE_RECTIFICATION_VALUE
                );
            } else {
                versionPubliee.setModeCreation(
                    SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE
                );
            }
            // FEV319 Nature version RECTIFICATION_EN_COURS
            versionPubliee.setNature(SolonEppSchemaConstant.VERSION_NATURE_RECTIFICATION_EN_COURS_PROPERTY);
            // FEV319 Cette version n'est pas la version courante(version mineur)
            versionPubliee.setVersionCourante(false);
        }

        currentVersion.setHorodatage(Calendar.getInstance());
        versionPubliee.setDateAr(null);
        versionPubliee.setTitle(numeroVersion.toString());
        versionPubliee.setNumeroVersion(numeroVersion);

        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, versionPublieeDoc, evenementDoc, false, false);

        // Valide les nouvelles données de la version
        VersionValidator versionValidator = new VersionValidator();
        versionValidator.validateMetaObligatoire(versionPublieeDoc, evenementDoc);

        compareVersion(versionPublieeDoc, currentPublieVersion);

        // Persiste les modifications de la version
        session.saveDocument(versionPublieeDoc);

        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        if (evenement.isEtatPublie()) {
            // Transitionne la version vers l'état publié
            versionPublieeDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_PUBLIE_TRANSITION);

            // Synchronise les messages avec la version publiée / en cours de validation
            evenementDistributionService.updateMessageAfterPublier(
                session,
                dossierDoc,
                evenementDoc,
                versionPublieeDoc
            );
        } else {
            // Transitionne la version vers l'état en attente de validation
            versionPublieeDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_ATTENTE_VALIDATION_TRANSITION);

            // Transitionne l'événement vers l'état en attente de validation
            evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_TO_ATTENTE_VALIDATION_TRANSITION);

            // Synchronise les messages avec la version publiée / en cours de validation
            evenementDistributionService.updateMessageAfterDemanderValidation(
                session,
                dossierDoc,
                evenementDoc,
                versionPublieeDoc
            );
        }

        return versionPublieeDoc;
    }

    @Override
    public DocumentModel reporterVersionBrouillonPourCompletion(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.POSTPONE_VERSION_TEC,
            POUR_COMPLETION_COMMUNICATION + evenementDoc.getTitle()
        );

        if (currentVersion.isEtatBrouillon()) {
            // Publication d'une version brouillon : vérifie que la version est une version brouillon pour complétion /
            // rectification
            if (
                !(
                    SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(
                        currentVersion.getModeCreation()
                    ) ||
                    !SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(
                        currentVersion.getModeCreation()
                    )
                )
            ) {
                throw new NuxeoException(
                    "Seule la version brouillon pour complétion ou rectification peut être modifiée"
                );
            }
        } else {
            throw new NuxeoException(
                "Impossible de reporter les modifications sur la version : " +
                currentVersion.getTitle() +
                ETAT +
                currentVersionDoc.getCurrentLifeCycleState()
            );
        }

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final DocumentModel lastVersionDoc = getLastVersion(session, evenementDoc.getTitle());
        NumeroVersion numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, false);

        // Renomme le document version brouillon avec le nom du nouveau numéro de version
        DocumentModel versionRenomeeDoc = session.move(
            currentVersionDoc.getRef(),
            currentVersionDoc.getParentRef(),
            numeroVersion.toString()
        );
        Version versionRenomee = versionRenomeeDoc.getAdapter(Version.class);

        // Modifie les propriétés de la version
        versionRenomee.setHorodatage(Calendar.getInstance());
        versionRenomee.setDateAr(null);
        versionRenomee.setTitle(numeroVersion.toString());
        versionRenomee.setNumeroVersion(numeroVersion);

        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, versionRenomeeDoc, evenementDoc, false, true);

        // Persiste les modifications de la version
        session.saveDocument(versionRenomeeDoc);

        session.save();

        return versionRenomeeDoc;
    }

    @Override
    public DocumentModel reporterVersionBrouillonPourRectification(
        CoreSession session,
        DocumentModel currentVersionDoc,
        DocumentModel newVersionDoc,
        DocumentModel evenementDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.POSTPONE_VERSION_TEC,
            POUR_RECTIFICATION_COMMUNICATION + evenementDoc.getTitle()
        );

        if (currentVersion.isEtatBrouillon()) {
            // Publication d'une version brouillon : vérifie que la version est une version brouillon pour complétion /
            // rectification
            if (
                !(
                    SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(
                        currentVersion.getModeCreation()
                    ) ||
                    SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(
                        currentVersion.getModeCreation()
                    )
                )
            ) {
                throw new NuxeoException(
                    "Seule la version brouillon pour complétion ou rectification peut être modifiée"
                );
            }
        } else {
            throw new NuxeoException(
                "Impossible de reporter les modifications sur la version : " +
                currentVersion.getTitle() +
                ETAT +
                currentVersionDoc.getCurrentLifeCycleState()
            );
        }

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final DocumentModel lastVersionDoc = getLastVersion(session, evenementDoc.getTitle());
        NumeroVersion numeroVersion = versionNumeroService.getNextNumeroVersion(lastVersionDoc, false);

        // Renomme le document version brouillon avec le nom du nouveau numéro de version
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        DocumentModel versionRenommeeDoc = null;
        if (evenement.isEtatPublie()) {
            versionRenommeeDoc =
                session.move(currentVersionDoc.getRef(), currentVersionDoc.getParentRef(), numeroVersion.toString());
        } else if (evenement.isEtatAttenteValidation()) {
            versionRenommeeDoc =
                session.copy(currentVersionDoc.getRef(), currentVersionDoc.getParentRef(), numeroVersion.toString());
        } else {
            throw new NuxeoException(
                "Etat de la communication " +
                evenement.getTitle() +
                " interdit: " +
                evenementDoc.getCurrentLifeCycleState()
            );
        }
        Version versionRenommee = versionRenommeeDoc.getAdapter(Version.class);

        // Modifie les propriétés de la version
        versionRenommee.setHorodatage(Calendar.getInstance());
        versionRenommee.setDateAr(null);
        versionRenommee.setTitle(numeroVersion.toString());
        versionRenommee.setNumeroVersion(numeroVersion);

        VersionAssembler versionAssembler = new VersionAssembler(session);
        versionAssembler.assembleVersionForUpdate(newVersionDoc, versionRenommeeDoc, evenementDoc, false, false);

        // Persiste les modifications de la version
        versionRenommeeDoc = session.saveDocument(versionRenommeeDoc);

        // Si l'événement est en attente de validation, on a 2 versions brouillon concurrentes dont une bloquée
        if (evenement.isEtatAttenteValidation()) {
            versionRenommeeDoc.followTransition(
                SolonEppLifecycleConstant.VERSION_TO_BROUILLON_ATTENTE_VALIDATION_TRANSITION
            );
        }

        session.save();

        return versionRenommeeDoc;
    }

    @Override
    public void supprimerVersionBrouillon(
        CoreSession session,
        String evenementId,
        Long majorVersion,
        Long minorVersion
    ) {
        // Charge l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        DocumentModel evenementDoc = evenementService.getEvenement(session, evenementId);
        if (evenementDoc == null) {
            throw new NuxeoException(COMMUNICATION_NON_TROUVE + evenementId);
        }

        // Récupère la version spécifiée de l'événement
        DocumentModel versionDoc = getVersion(session, evenementId, majorVersion, minorVersion);
        if (versionDoc == null) {
            throw new NuxeoException(VERSION_NON_TROUVE);
        }

        supprimerVersionBrouillon(session, evenementDoc, versionDoc);
    }

    @Override
    public void supprimerVersionBrouillon(CoreSession session, DocumentModel evenementDoc, DocumentModel versionDoc) {
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();

        LOGGER.info(session, EppLogEnumImpl.REMOVE_VERSION_TEC, "communication: " + evenementDoc.getTitle());

        // Vérifie les données de l'événément
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);

        // Vérifie que la version demandée est à l'état brouillon
        Version version = versionDoc.getAdapter(Version.class);
        if (!version.isEtatBrouillon()) {
            throw new NuxeoException("La version doit être à l'état brouillon");
        }
        NumeroVersion numeroVersionActuelle = version.getNumeroVersion();

        // Supprime le document version
        session.removeDocument(versionDoc.getRef());
        session.save();

        DocumentModel versionAnterieureDoc = getLastVersionNotEquals(
            session,
            evenementDoc.getTitle(),
            numeroVersionActuelle
        );
        if (versionAnterieureDoc != null) {
            // Synchronise le message de l'émetteur avec la version antérieure
            final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
            evenementDistributionService.updateMessageAfterSupprimerBrouillon(
                session,
                evenementDoc,
                versionAnterieureDoc
            );
        } else {
            // Si la version supprimée est la seule de l'événement, l'événement est supprimé
            evenementService.supprimerEvenement(session, evenementDoc);
        }
    }

    @Override
    public void accuserReceptionDestinataire(CoreSession session, AccuserReceptionContext accuserReceptionContext) {
        AccuserReceptionRequest accuserReceptionRequest = accuserReceptionContext.getAccuserReceptionRequest();
        String evenementId = accuserReceptionRequest.getEvenementId();

        LOGGER.info(
            session,
            EppLogEnumImpl.ACKNOWLEDGMENT_VERSION_TEC,
            "communication : " + accuserReceptionRequest.toString()
        );

        // Charge l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        DocumentModel evenementDoc = evenementService.getEvenement(session, evenementId);
        if (evenementDoc == null) {
            throw new NuxeoException(COMMUNICATION_NON_TROUVE + evenementId);
        }

        DocumentModel versionDoc = null;
        NumeroVersion numeroVersion = accuserReceptionRequest.getNumeroVersion();
        if (numeroVersion != null) {
            // Récupère la version spécifiée de l'événement
            versionDoc =
                getVersion(session, evenementId, numeroVersion.getMajorVersion(), numeroVersion.getMinorVersion());
        } else {
            // Récupère la version active de l'événement
            versionDoc =
                getVersionActive(
                    session,
                    evenementDoc,
                    SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE
                );
        }

        if (versionDoc == null) {
            throw new NuxeoException(VERSION_NON_TROUVE);
        }

        Version version = versionDoc.getAdapter(Version.class);

        // Vérifie que la version demandée est à l'état publié
        if (version.isEtatBrouillon()) {
            throw new NuxeoException("Vous n'avez pas le droit d'accuser réception de cette version");
        }

        // Accuse réception de la version
        accuserReceptionDestinataire(session, evenementDoc, versionDoc, false);

        // Renseigne les données en sortie
        AccuserReceptionResponse accuserReceptionResponse = accuserReceptionContext.getAccuserReceptionResponse();
        accuserReceptionResponse.setDossierDoc(session.getDocument(evenementDoc.getParentRef()));
        accuserReceptionResponse.setEvenementDoc(session.getDocument(evenementDoc.getRef()));
        accuserReceptionResponse.setVersionDoc(session.getDocument(versionDoc.getRef()));
    }

    @Override
    public void accuserReceptionDestinataire(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel versionDoc,
        boolean checkArVersionAnterieure
    ) {
        Version version = versionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.ACKNOWLEDGMENT_VERSION_TEC,
            "Par destinataire : version: " + version.getTitle()
        );

        // Seul le destinataire peut accuser réception
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        EppPrincipal eppPrincipal = (EppPrincipal) session.getPrincipal();
        if (!evenement.getDestinataire().equals(eppPrincipal.getInstitutionId())) {
            throw new NuxeoException("Seul le destinataire peut accuser réception de la version");
        }

        // Accuse réception d'une version une seule fois
        if (version.getDateAr() != null) {
            LOGGER.info(session, EppLogEnumImpl.FAIL_ACKNOWLEDGMENT_VERSION_FONC);
            return;
        }

        // Renseigne la date d'accusé de réception
        final Calendar dateAr = Calendar.getInstance();
        version.setDateAr(dateAr);
        session.saveDocument(versionDoc);

        // TCH: mantis 0051657 : [SEN] accuser réception sur plusieurs versions
        // rensiegner AR pour toutes les versions precedentes
        List<DocumentModel> prevVersionsList = getPreviousVersionWithoutAR(
            session,
            evenement.getTitle(),
            version.getMajorVersion(),
            version.getMinorVersion()
        );
        prevVersionsList.forEach(
            versionModel -> {
                Version currentVersion = versionModel.getAdapter(Version.class);
                if (currentVersion.getDateAr() == null) {
                    currentVersion.setDateAr(dateAr);
                    session.saveDocument(versionModel);
                }
            }
        );

        session.save();

        // Met à jour le message de l'émetteur pour accuser réception
        DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.accuserReceptionMessageEmetteur(
            session,
            dossierDoc,
            evenementDoc,
            versionDoc,
            checkArVersionAnterieure
        );

        // Crée les notifications pour l'accusé de réception
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.createNotificationEvenement(
            session,
            evenementDoc,
            SolonEppSchemaConstant.JETON_DOC_NOTIFICATION_ACCUSER_RECEPTION_VALUE
        );
    }

    @Override
    public void validerVersionDestinataire(CoreSession session, ValiderVersionContext validerVersionContext) {
        ValiderVersionRequest validerVersionRequest = validerVersionContext.getValiderVersionRequest();
        String evenementId = validerVersionRequest.getEvenementId();

        LOGGER.info(
            session,
            EppLogEnumImpl.VALIDATE_VERSION_TEC,
            "Par destinataire : communication : " + validerVersionRequest.toString()
        );

        // Charge l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        DocumentModel evenementDoc = evenementService.getEvenement(session, evenementId);
        if (evenementDoc == null) {
            throw new NuxeoException(COMMUNICATION_NON_TROUVE + evenementId);
        }

        // Vérifie les données de l'événément
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionDestinataire(evenementDoc);
        evenementValidator.validateEtatEvenement(
            evenementDoc,
            Collections.singleton(SolonEppLifecycleConstant.EVENEMENT_ATTENTE_VALIDATION_STATE)
        );

        // Récupère la version active de l'événement
        DocumentModel versionDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE
        );
        if (versionDoc == null) {
            throw new NuxeoException(VERSION_NON_TROUVE);
        }

        // Vérifie que la version demandée est à l'état en attente de validation
        Version version = versionDoc.getAdapter(Version.class);
        if (!version.isEtatAttenteValidation()) {
            throw new NuxeoException("Vous n'avez pas le droit de valider cette version");
        }

        // Valide la version
        DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());
        if (validerVersionRequest.isAccepter()) {
            validerVersionDestinataireAccepter(session, dossierDoc, evenementDoc, versionDoc);
        } else {
            validerVersionDestinataireRejeter(session, dossierDoc, evenementDoc, versionDoc);
        }

        // Renseigne les données en sortie
        ValiderVersionResponse validerVersionResponse = validerVersionContext.getValiderVersionResponse();
        validerVersionResponse.setDossierDoc(session.getDocument(evenementDoc.getParentRef()));
        validerVersionResponse.setEvenementDoc(session.getDocument(evenementDoc.getRef()));

        // Renseigne la nouvelle version active après l'acceptation / rejet
        DocumentModel versionActiveDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE
        );
        validerVersionResponse.setVersionDoc(versionActiveDoc);
    }

    /**
     * Accepte la demande de rectification / annulation d'une version. Cela crée une nouvelle version publiée, et passe
     * l'ancienne version publiée à l'état obsolète. La version actuelle (en attente de validation) reste telle quelle.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Document dossier
     * @param evenementDoc
     *            Document événement
     * @param currentVersionDoc
     *            Document version en cours (en attente de validation)
     */
    protected void validerVersionDestinataireAccepter(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        DocumentModel currentVersionDoc
    ) {
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.VALIDATE_VERSION_TEC,
            "Par destinataire : communication : " +
            evenementDoc.getTitle() +
            ", version " +
            currentVersion.getTitle() +
            " : version acceptée"
        );

        // Transitionne la version publiée actuelle à l'état obsolète
        DocumentModel currentPublieVersionDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE
        );
        if (currentPublieVersionDoc == null) {
            throw new NuxeoException(VERSION_ACTUELLE_INTROUVABLE);
        }
        currentPublieVersionDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_OBSOLETE_TRANSITION);
        boolean versionPourAnnulation = SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(
            currentVersion.getModeCreation()
        );

        // Incrémente le numéro de version
        final VersionNumeroService versionNumeroService = SolonEppServiceLocator.getVersionNumeroService();
        final NumeroVersion currentNumeroVersion = currentVersion.getNumeroVersion();
        NumeroVersion numeroVersion = versionNumeroService.getNextNumeroVersion(currentVersionDoc, true);

        // Renomme le document de la version en attente de validation avec le titre de la version publiée
        DocumentModel versionPublieeDoc = session.move(
            currentVersionDoc.getRef(),
            currentVersionDoc.getParentRef(),
            numeroVersion.toString()
        );
        Version versionPubliee = versionPublieeDoc.getAdapter(Version.class);

        // FEV319 Cette version devient la version courante, mise a false du flag version courante sur la précédante
        setVersionCouranteToFalse(session, evenementDoc.getTitle());
        versionPubliee.setVersionCourante(true);

        // Modifie les propriétés de la version
        if (!versionPourAnnulation) {
            versionPubliee.setModeCreation(
                SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_VALIDATION_RECTIFICATION_VALUE
            );
            // FEV319 Nature version RECTIFIEE
            versionPubliee.setNature(SolonEppSchemaConstant.VERSION_NATURE_RECTIFIEE_PROPERTY);
        } else {
            versionPubliee.setModeCreation(
                SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_VALIDATION_ANNULATION_VALUE
            );
            // FEV319 Nature version ANNULEE
            versionPubliee.setNature(SolonEppSchemaConstant.VERSION_NATURE_ANNULEE_PROPERTY);
        }
        versionPubliee.setTitle(numeroVersion.toString());
        versionPubliee.setNumeroVersion(numeroVersion);
        session.saveDocument(versionPublieeDoc);

        // Transitionne la version vers l'état publié
        versionPublieeDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_PUBLIE_TRANSITION);

        if (!versionPourAnnulation) {
            // Version pour rectification : l'événement retourne à l'état en instance
            evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_BACK_TO_INSTANCE_TRANSITION);
        } else {
            // Version pour annulation : l'événement passe à l'état annulé
            evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_TO_ANNULE_TRANSITION);
        }

        // Accuse réception de la version
        accuserReceptionDestinataire(session, evenementDoc, versionPublieeDoc, false);

        // Reporte la version bloquée en rectification delta si elle existe
        DocumentModel currentVersionAttenteValidationDoc = getVersionBrouillonAttenteValidation(session, evenementDoc);
        if (currentVersionAttenteValidationDoc != null) {
            Version currentVersionAttenteValidation = currentVersionAttenteValidationDoc.getAdapter(Version.class);
            NumeroVersion brouillonEnAttenteValidationNumeroVersion = currentVersionAttenteValidation.getNumeroVersion();
            if (
                !brouillonEnAttenteValidationNumeroVersion
                    .getMajorVersion()
                    .equals(currentNumeroVersion.getMajorVersion())
            ) {
                throw new NuxeoException(VERSION_MAJEUR_INVALIDE + brouillonEnAttenteValidationNumeroVersion);
            }
            if (brouillonEnAttenteValidationNumeroVersion.getMinorVersion() <= currentNumeroVersion.getMinorVersion()) {
                throw new NuxeoException(VERSION_MINEUR_INVALIDE + brouillonEnAttenteValidationNumeroVersion);
            }

            // Incrémente le numéro de version
            NumeroVersion numeroVersionVersionBrouillonRenommee = versionNumeroService.getNextNumeroVersion(
                numeroVersion,
                false
            );

            // Renomme le document de la version en brouillon en attente de validation avec le titre de la version
            // renommée
            DocumentModel versionBrouillonRenommeeDoc = session.move(
                currentVersionAttenteValidationDoc.getRef(),
                currentVersionAttenteValidationDoc.getParentRef(),
                numeroVersionVersionBrouillonRenommee.toString()
            );
            Version versionBrouillonRenommee = versionBrouillonRenommeeDoc.getAdapter(Version.class);

            // Modifie les propriétés de la version
            versionBrouillonRenommee.setTitle(numeroVersionVersionBrouillonRenommee.toString());
            versionBrouillonRenommee.setNumeroVersion(numeroVersionVersionBrouillonRenommee);
            session.saveDocument(versionBrouillonRenommeeDoc);

            // Repasse la version à l'état brouillon
            session.followTransition(
                versionBrouillonRenommeeDoc.getRef(),
                SolonEppLifecycleConstant.VERSION_BACK_TO_BROUILLON_TRANSITION
            );

            // Supprime l'ancienne version brouillon
            Evenement evenement = evenementDoc.getAdapter(Evenement.class);
            VersionDao versionDao = new VersionDao(session);
            VersionCriteria versionCriteria = new VersionCriteria();
            versionCriteria.setEvenementId(evenement.getTitle());
            versionCriteria.setCurrentLifeCycleState(SolonEppLifecycleConstant.VERSION_BROUILLON_STATE);
            versionCriteria.setMajorVersionEquals(brouillonEnAttenteValidationNumeroVersion.getMajorVersion());
            DocumentModel oldVersionBrouillonDoc = versionDao.getSingleVersionByCriteria(versionCriteria);
            if (oldVersionBrouillonDoc == null) {
                throw new NuxeoException(
                    "Ancienne version brouillon de la communication " + evenement.getTitle() + " non trouvée"
                );
            }
            session.removeDocument(oldVersionBrouillonDoc.getRef());
            session.save();
        }

        // Synchronise le message de l'émetteur avec la version publiée ou brouillon reportée
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        DocumentModel currentEmetteurVersionDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        evenementDistributionService.updateMessageAfterValider(
            session,
            dossierDoc,
            evenementDoc,
            currentEmetteurVersionDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );

        // Synchronise le message du destinataire et de la copie avec la version publiée
        evenementDistributionService.updateMessageAfterValider(
            session,
            dossierDoc,
            evenementDoc,
            versionPublieeDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE
        );
        evenementDistributionService.updateMessageAfterValider(
            session,
            dossierDoc,
            evenementDoc,
            versionPublieeDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE
        );
    }

    /**
     * Rejette la demande de rectification d'une version. La version actuelle passe à l'état rejeté, l'ancienne version
     * (avec un numéro inférieur) redevient la version active.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Document dossier
     * @param evenementDoc
     *            Document événement
     * @param currentVersionDoc
     *            Document version en cours (en attente de validation)
     */
    protected void validerVersionDestinataireRejeter(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        DocumentModel currentVersionDoc
    ) {
        Version version = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.INVALIDATE_VERSION_TEC,
            "communication : " + evenementDoc.getTitle() + ", version: " + version.getTitle() + " : version rejetée"
        );

        // Récupère la version publiée actuelle
        DocumentModel currentPublieVersionDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE
        );
        if (currentPublieVersionDoc == null) {
            throw new NuxeoException(VERSION_ACTUELLE_INTROUVABLE);
        }

        // Transitionne la version vers l'état rejeté
        currentVersionDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_REJETE_TRANSITION);

        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        // FEV319 Nature version RECTIFICATION_REJETEE / ANNULATION_REJETEE
        if (
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(
                currentVersion.getModeCreation()
            )
        ) {
            currentVersion.setNature(SolonEppSchemaConstant.VERSION_NATURE_ANNULATION_REJETEE_PROPERTY);
        } else if (
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE.equals(
                currentVersion.getModeCreation()
            )
        ) {
            currentVersion.setNature(SolonEppSchemaConstant.VERSION_NATURE_RECTIFICATION_REJETEE_PROPERTY);
        }
        session.saveDocument(currentVersionDoc);

        // Supprime la version bloquée en rectification delta si elle existe
        DocumentModel currentVersionAttenteValidationDoc = getVersionBrouillonAttenteValidation(session, evenementDoc);
        if (currentVersionAttenteValidationDoc != null) {
            Version currentVersionAttenteValidation = currentVersionAttenteValidationDoc.getAdapter(Version.class);
            NumeroVersion brouillonEnAttenteValidationNumeroVersion = currentVersionAttenteValidation.getNumeroVersion();
            NumeroVersion currentNumeroVersion = currentVersion.getNumeroVersion();
            if (
                !brouillonEnAttenteValidationNumeroVersion
                    .getMajorVersion()
                    .equals(currentNumeroVersion.getMajorVersion())
            ) {
                throw new NuxeoException(VERSION_MAJEUR_INVALIDE + brouillonEnAttenteValidationNumeroVersion);
            }
            if (brouillonEnAttenteValidationNumeroVersion.getMinorVersion() <= currentNumeroVersion.getMinorVersion()) {
                throw new NuxeoException(VERSION_MINEUR_INVALIDE + brouillonEnAttenteValidationNumeroVersion);
            }
            session.removeDocument(currentVersionAttenteValidationDoc.getRef());
        }

        // Transitionne l'événement vers l'état en instance où il était avant la demande de rectification
        evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_BACK_TO_INSTANCE_TRANSITION);

        // Accuse réception de la version
        accuserReceptionDestinataire(session, evenementDoc, currentVersionDoc, false);

        // Synchronise le message de l'émetteur avec la version publiée ou brouillon antérieure
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        DocumentModel currentEmetteurVersionDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        evenementDistributionService.updateMessageAfterRejeter(
            session,
            dossierDoc,
            evenementDoc,
            currentEmetteurVersionDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );

        // Synchronise le message du destinataire avec la version publiée antérieure
        evenementDistributionService.updateMessageAfterRejeter(
            session,
            dossierDoc,
            evenementDoc,
            currentPublieVersionDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE
        );
    }

    @Override
    public void validerVersionEmetteur(CoreSession session, ValiderVersionContext validerVersionContext) {
        ValiderVersionRequest validerVersionRequest = validerVersionContext.getValiderVersionRequest();
        String evenementId = validerVersionRequest.getEvenementId();

        LOGGER.info(
            session,
            EppLogEnumImpl.VALIDATE_VERSION_TEC,
            "Par emetteur : " + validerVersionRequest.toString() + ", version validée"
        );

        // Charge l'événement
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        DocumentModel evenementDoc = evenementService.getEvenement(session, evenementId);
        if (evenementDoc == null) {
            throw new NuxeoException(COMMUNICATION_NON_TROUVE + evenementId);
        }

        // Vérifie les données de l'événément
        EvenementValidator evenementValidator = new EvenementValidator(session);
        evenementValidator.validateInstitutionEmettrice(evenementDoc);
        evenementValidator.validateEtatEvenement(
            evenementDoc,
            Collections.singleton(SolonEppLifecycleConstant.EVENEMENT_ATTENTE_VALIDATION_STATE)
        );

        // Récupère la version active de l'événement
        DocumentModel versionDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        if (versionDoc == null) {
            throw new NuxeoException(VERSION_NON_TROUVE);
        }

        // Vérifie que la version demandée est à l'état en attente de validation
        Version version = versionDoc.getAdapter(Version.class);
        if (!version.isEtatAttenteValidation()) {
            throw new NuxeoException("Vous n'avez pas le droit de valider cette version");
        }

        // Valide la version
        DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());
        validerVersionEmetteurAbandonner(session, dossierDoc, evenementDoc, versionDoc);

        // Renseigne les données en sortie
        ValiderVersionResponse validerVersionResponse = validerVersionContext.getValiderVersionResponse();
        validerVersionResponse.setDossierDoc(session.getDocument(evenementDoc.getParentRef()));
        validerVersionResponse.setEvenementDoc(session.getDocument(evenementDoc.getRef()));

        // Renseigne la nouvelle version active après l'acceptation / rejet
        DocumentModel versionActiveDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        validerVersionResponse.setVersionDoc(versionActiveDoc);
    }

    /**
     * Abandonne la demande de rectification d'une version. La version actuelle passe à l'état abandonné, l'ancienne
     * version (avec un numéro inférieur) redevient la version active.
     *
     * @param session
     *            Session
     * @param dossierDoc
     *            Document dossier
     * @param evenementDoc
     *            Document événement
     * @param currentVersionDoc
     *            Document version en cours (en attente de validation)
     */
    protected void validerVersionEmetteurAbandonner(
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel evenementDoc,
        DocumentModel currentVersionDoc
    ) {
        Version version = currentVersionDoc.getAdapter(Version.class);

        LOGGER.info(
            session,
            EppLogEnumImpl.INVALIDATE_VERSION_TEC,
            "Par emetteur : " + evenementDoc.getTitle() + ", version: " + version.getTitle() + " : abandonnée"
        );

        // Récupère la version publiée actuelle
        DocumentModel currentPublieVersionDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_COPIE_VALUE
        );
        if (currentPublieVersionDoc == null) {
            throw new NuxeoException(VERSION_ACTUELLE_INTROUVABLE);
        }

        // Transitionne la version vers l'état publié
        currentVersionDoc.followTransition(SolonEppLifecycleConstant.VERSION_TO_ABANDONNE_TRANSITION);

        Version currentVersion = currentVersionDoc.getAdapter(Version.class);

        // FEV319 Nature version RECTIFICATION_ABANDONNEE / ANNULATION_ABANDONNEE
        if (
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(
                currentVersion.getModeCreation()
            )
        ) {
            currentVersion.setNature(SolonEppSchemaConstant.VERSION_NATURE_ANNULATION_ABANDONNEE_PROPERTY);
            session.saveDocument(currentVersion.getDocument());
        } else if (
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE.equals(
                currentVersion.getModeCreation()
            )
        ) {
            currentVersion.setNature(SolonEppSchemaConstant.VERSION_NATURE_RECTIFICATION_ABANDONNEE_PROPERTY);
            session.saveDocument(currentVersion.getDocument());
        }

        // Supprime la version bloquée en rectification delta si elle existe
        DocumentModel currentVersionAttenteValidationDoc = getVersionBrouillonAttenteValidation(session, evenementDoc);
        if (currentVersionAttenteValidationDoc != null) {
            Version currentVersionAttenteValidation = currentVersionAttenteValidationDoc.getAdapter(Version.class);
            NumeroVersion brouillonEnAttenteValidationNumeroVersion = currentVersionAttenteValidation.getNumeroVersion();
            NumeroVersion currentNumeroVersion = currentVersion.getNumeroVersion();
            if (
                !brouillonEnAttenteValidationNumeroVersion
                    .getMajorVersion()
                    .equals(currentNumeroVersion.getMajorVersion())
            ) {
                throw new NuxeoException(VERSION_MAJEUR_INVALIDE + brouillonEnAttenteValidationNumeroVersion);
            }
            if (brouillonEnAttenteValidationNumeroVersion.getMinorVersion() <= currentNumeroVersion.getMinorVersion()) {
                throw new NuxeoException(VERSION_MINEUR_INVALIDE + brouillonEnAttenteValidationNumeroVersion);
            }
            session.removeDocument(currentVersionAttenteValidationDoc.getRef());
        }

        // Transitionne l'événement vers l'état en instance où il était avant la demande de rectification
        evenementDoc.followTransition(SolonEppLifecycleConstant.EVENEMENT_BACK_TO_INSTANCE_TRANSITION);

        // Met à jour le message de l'émetteur pour annuler la demande d'accusé de réception
        final EvenementDistributionService evenementDistributionService = SolonEppServiceLocator.getEvenementDistributionService();
        evenementDistributionService.accuserReceptionMessageEmetteur(
            session,
            dossierDoc,
            evenementDoc,
            currentPublieVersionDoc,
            false
        );

        // Synchronise le message de l'émetteur avec la version publiée ou brouillon antérieure
        DocumentModel currentEmetteurVersionDoc = getVersionActive(
            session,
            evenementDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );
        evenementDistributionService.updateMessageAfterAbandonner(
            session,
            dossierDoc,
            evenementDoc,
            currentEmetteurVersionDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE
        );

        // Synchronise le message du destinataire avec la version publiée antérieure
        evenementDistributionService.updateMessageAfterAbandonner(
            session,
            dossierDoc,
            evenementDoc,
            currentPublieVersionDoc,
            SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE
        );
    }

    /**
     * Retourne la version brouillon en attente de validation d'un événement.
     *
     * @param session
     *            Session
     * @param evenementDoc
     *            Document événement
     * @return Document version
     */
    protected DocumentModel getVersionBrouillonAttenteValidation(CoreSession session, DocumentModel evenementDoc) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);
        VersionDao versionDao = new VersionDao(session);
        VersionCriteria versionCriteria = new VersionCriteria();
        versionCriteria.setEvenementId(evenement.getTitle());
        versionCriteria.setCurrentLifeCycleState(SolonEppLifecycleConstant.VERSION_BROUILLON_ATTENTE_VALIDATION_STATE);

        return versionDao.getSingleVersionByCriteria(versionCriteria);
    }

    @Override
    public DocumentModel findVersionByUIID(CoreSession session, String id) {
        return session.getDocument(new IdRef(id));
    }

    /**
     * Compare 2 versions et renseigne la liste des propriétés modifiées.
     *
     * @param newVersionDoc
     * @param versionToCompareDoc
     */
    private void compareVersion(DocumentModel newVersionDoc, DocumentModel versionToCompareDoc) {
        if (newVersionDoc == null || versionToCompareDoc == null) {
            return;
        }

        List<String> modifiedMetaList = new ArrayList<>();

        Version newVersion = newVersionDoc.getAdapter(Version.class);

        // Compare les propriétés du schéma Version
        Map<String, Object> newVersionProperties = newVersionDoc.getProperties(SolonEppSchemaConstant.VERSION_SCHEMA);
        Map<String, Object> versionToCompareProperties = versionToCompareDoc.getProperties(
            SolonEppSchemaConstant.VERSION_SCHEMA
        );

        for (Entry<String, Object> property : newVersionProperties.entrySet()) {
            if (!property.getKey().equals(SolonEppSchemaConstant.VERSION_MODIFIED_META_LIST_PROPERTY)) {
                Object newVersionPropertyValue = property.getValue();
                Object versionToCompareValue = versionToCompareProperties.get(property.getKey());

                if (SolonEppSchemaConstant.VERSION_COAUTEUR_XPATH.equals(property.getKey())) {
                    newVersionPropertyValue = removeNullElementsFromList(newVersionPropertyValue);
                    versionToCompareValue = removeNullElementsFromList(versionToCompareValue);
                }

                if (!equalsProperty(newVersionPropertyValue, versionToCompareValue)) {
                    modifiedMetaList.add(property.getKey());
                }
            }
        }

        // Compare le commentaire
        String property =
            STSchemaConstant.DUBLINCORE_SCHEMA_PREFIX + ":" + STSchemaConstant.DUBLINCORE_DESCRIPTION_PROPERTY;
        Object newVersionPropertyValue = DublincoreSchemaUtils.getDescription(newVersionDoc);
        Object versionToCompareValue = DublincoreSchemaUtils.getDescription(versionToCompareDoc);
        if (!equalsProperty(newVersionPropertyValue, versionToCompareValue)) {
            modifiedMetaList.add(property);
        }

        newVersion.setModifiedMetaList(modifiedMetaList);
    }

    /**
     * Retourne true si 2 propriétés sont égales
     *
     * @param propertyValue1
     * @param propertyValue2
     * @return
     */
    private boolean equalsProperty(Object propertyValue1, Object propertyValue2) {
        if (propertyValue1 instanceof String) {
            if (StringUtils.isBlank((String) propertyValue1)) {
                if (StringUtils.isBlank((String) propertyValue2)) {
                    return true;
                }
            } else {
                if (propertyValue2 != null) {
                    return ((String) propertyValue1).trim().equals(((String) propertyValue2).trim());
                }
            }
        }

        if (propertyValue1 instanceof GregorianCalendar) {
            if (propertyValue2 != null) {
                long v1 = ((GregorianCalendar) propertyValue1).getTimeInMillis();
                long v2 = ((GregorianCalendar) propertyValue2).getTimeInMillis();
                return v1 == v2;
            } else {
                return false;
            }
        }

        return Objects.equals(propertyValue1, propertyValue2);
    }

    @Override
    public DocumentModel getVersionToCompare(
        CoreSession session,
        DocumentModel evenementDoc,
        DocumentModel currentVersionDoc
    ) {
        Evenement evenement = evenementDoc.getAdapter(Evenement.class);

        if (StringUtils.isBlank(evenement.getTitle())) {
            return null;
        }
        MessageService messageService = SolonEppServiceLocator.getMessageService();
        DocumentModel messageDoc = messageService.getMessageByEvenementId(session, evenement.getTitle());

        VersionService versionService = SolonEppServiceLocator.getVersionService();
        List<DocumentModel> versionList = Optional
            .ofNullable(messageDoc)
            .map(d -> messageDoc.getAdapter(Message.class))
            .map(Message::getMessageType)
            .map(t -> versionService.findVersionVisible(session, evenement.getDocument(), t))
            .map(ArrayList::new)
            .orElseGet(ArrayList::new);

        DocumentModel versionToCompareDoc = null;
        boolean currentVersionFound = false;
        Version currentVersion = currentVersionDoc.getAdapter(Version.class);
        for (DocumentModel versionDoc : versionList) {
            Version version = versionDoc.getAdapter(Version.class);
            if (!currentVersionFound) {
                NumeroVersion numVersion = version.getNumeroVersion();
                if (numVersion.equals(currentVersion.getNumeroVersion())) {
                    currentVersionFound = true;
                }
            } else {
                if (
                    version
                        .getDocument()
                        .getCurrentLifeCycleState()
                        .equals(SolonEppLifecycleConstant.VERSION_PUBLIE_STATE) ||
                    version
                        .getDocument()
                        .getCurrentLifeCycleState()
                        .equals(SolonEppLifecycleConstant.VERSION_OBSOLETE_STATE) ||
                    version
                        .getDocument()
                        .getCurrentLifeCycleState()
                        .equals(SolonEppLifecycleConstant.VERSION_ATTENTE_VALIDATION_STATE)
                ) {
                    // version a comparer
                    versionToCompareDoc = version.getDocument();
                    break;
                }
            }
        }

        return versionToCompareDoc;
    }

    private static Object removeNullElementsFromList(Object result) {
        if (result instanceof List) {
            List<String> list = (List<String>) result;
            list.removeIf(Objects::isNull);
            if (CollectionUtils.isEmpty(list)) {
                result = null;
            } else {
                result = list;
            }
        }
        return result;
    }
}
