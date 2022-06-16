package fr.dila.epp.ui.services.impl;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.COMMUNICATION_METADONNEES_MAP;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_EVENEMENT_FOR_CREATION;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_MESSAGE;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_TYPE_EVENEMENT;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_TYPE_EVENEMENT_SUCCESSIF;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_VERSION;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_VERSION_FOR_CREATION;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.SKIP_LOCK;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.MODE_CREATION;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.PIECE_JOINTE_LIST;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.PUBLIER;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.TYPE_EVENEMENT;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.LAYOUT_MODE_COMPLETER;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.LAYOUT_MODE_CREER;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.LAYOUT_MODE_RECTIFIER;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.TYPE_PIECE_JOINTE;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static java.util.Optional.ofNullable;

import fr.dila.epp.ui.enumeration.MetadonneeMapperEnum;
import fr.dila.epp.ui.helper.CorbeilleProviderHelper;
import fr.dila.epp.ui.services.EvenementUIService;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.solonepp.api.service.VersionCreationService;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementContext;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementRequest;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementResponse;
import fr.dila.solonepp.api.service.version.CreerVersionContext;
import fr.dila.solonepp.api.service.version.CreerVersionRequest;
import fr.dila.solonepp.api.service.version.CreerVersionResponse;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.core.validator.EvenementValidator;
import fr.dila.solonepp.core.validator.PieceJointeFichierValidator;
import fr.dila.solonepp.core.validator.PieceJointeValidator;
import fr.dila.solonepp.core.validator.VersionValidator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.VocabularyServiceImpl;
import fr.dila.st.core.util.MD5Util;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SHA512Util;
import fr.dila.st.core.util.StringHelper;
import fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.server.jaxrs.batch.Batch;
import org.nuxeo.ecm.automation.server.jaxrs.batch.BatchManager;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.transaction.TransactionHelper;

public class EvenementUIServiceImpl implements EvenementUIService {
    private static final String PIECE_JOINTE_FILE_SUFFIX = "-file";
    private static final String PIECE_JOINTE_URL_SUFFIX = "-url";
    private static final String PIECE_JOINTE_UPLOAD_BATCH_ID_SUFFIX = "-uploadBatchId";
    private static final String PIECE_JOINTE_TITRE_SUFFIX = "-titre";
    private static final String COMPLETER_META_OBLIGATOIRES = "label.epp.meta.obligatoires.param";
    private static final String EVENEMENT_CANT_LOCK = "evenement.cant.lock";
    private static final String EVENEMENT_COMPLETER_OK = "evenement.completer.ok";
    private static final String EVENEMENT_CREER_OK = "evenement.create.ok";
    private static final String EVENEMENT_LOCK_LOST = "evenement.lock.lost";
    private static final String EVENEMENT_MODIFIER_OK = "evenement.modifier.ok";
    private static final String EVENEMENT_PUBLIER_OK = "evenement.publier.ok";
    private static final String EVENEMENT_RECTIFIER_ATTENTE_VALIDATION = "evenement.rectifier.attente.validation";
    private static final String EVENEMENT_RECTIFIER_OK = "evenement.rectifier.ok";
    private static final String PUBLICATION_EPG_KO = "publication.epg.ko";
    private static final String TYPE_EVENEMENT_EVT45 = "EVT45";

    private static final Pattern META_KEY_SUFFIXES_PATTERN = Pattern.compile(
        PIECE_JOINTE_TITRE_SUFFIX + "|" + PIECE_JOINTE_URL_SUFFIX + "|" + PIECE_JOINTE_UPLOAD_BATCH_ID_SUFFIX
    );

    private static final STLogger LOGGER = STLogFactory.getLog(EvenementUIServiceImpl.class);

    @Override
    public void consulterEvenement(SpecificContext context) {
        String id = context.getFromContextData(ID);
        DocumentModel messageDoc = context.getSession().getDocument(new IdRef(id));
        Message message = messageDoc.getAdapter(Message.class);
        context.setCurrentDocument(message.getCaseDocumentId());
        context.putInContextData(CURRENT_MESSAGE, message);
        DocumentModel currentVersionDoc = SolonEppActionsServiceLocator
            .getCorbeilleActionService()
            .getSelectedVersion(context);
        context.putInContextData(
            CURRENT_VERSION,
            ofNullable(currentVersionDoc).map(dm -> dm.getAdapter(Version.class)).orElse(null)
        );

        // chargement des pièces jointes
        final PieceJointeService pjService = SolonEppServiceLocator.getPieceJointeService();
        List<DocumentModel> pjDocList = new ArrayList<>();

        if (currentVersionDoc != null) {
            pjDocList = pjService.getVersionPieceJointeListWithFichier(context.getSession(), currentVersionDoc);
        }

        context.putInContextData(
            PIECE_JOINTE_LIST,
            pjDocList.stream().map(dm -> dm.getAdapter(PieceJointe.class)).collect(Collectors.toList())
        );
    }

    @Override
    public void creerEvenement(SpecificContext context) {
        String typeEvenement = context.getFromContextData(TYPE_EVENEMENT);
        String idMessage = context.getFromContextData(ID);

        InitialiserEvenementContext initialiserEvenementContext = new InitialiserEvenementContext();
        InitialiserEvenementRequest initialiserEvenementRequest = initialiserEvenementContext.getInitialiserEvenementRequest();
        initialiserEvenementRequest.setTypeEvenement(typeEvenement);

        DocumentModel messageParentDoc = null;

        if (StringUtils.isNotEmpty(idMessage)) {
            messageParentDoc = context.getSession().getDocument(new IdRef(idMessage));
            Message message = messageParentDoc.getAdapter(Message.class);
            context.putInContextData(CURRENT_MESSAGE, message);
            if (StringUtils.isNotEmpty(checkLockMessage(context))) {
                return;
            }
            initialiserEvenementRequest.setIdEvenementPrecedent(message.getIdEvenement());
        }

        // initialisation des métadonnees de l'evenement
        SolonEppServiceLocator
            .getEvenementService()
            .initialiserEvenement(context.getSession(), initialiserEvenementContext);

        InitialiserEvenementResponse initialiserEvenementResponse = initialiserEvenementContext.getInitialiserEvenementResponse();

        context.setCurrentDocument(initialiserEvenementResponse.getEvenementDoc());
        DocumentModel currentVersionDoc = initialiserEvenementResponse.getVersionDoc();
        context.putInContextData(CURRENT_VERSION, currentVersionDoc.getAdapter(Version.class));

        List<PieceJointe> pieceJointeList = new ArrayList<>();

        EvenementTypeDescriptor descriptor = SolonEppServiceLocator
            .getEvenementTypeService()
            .getEvenementType(typeEvenement);
        if (descriptor != null) {
            Map<String, PieceJointeDescriptor> map = descriptor.getPieceJointe();
            if (map != null) {
                for (PieceJointeDescriptor pieceJointeDescriptor : map.values()) {
                    if (pieceJointeDescriptor != null && pieceJointeDescriptor.isInitToOne()) {
                        // On initialise ce container avec une valeur
                        addPieceJointe(
                            pieceJointeDescriptor.getType(),
                            typeEvenement,
                            pieceJointeList,
                            context.getSession()
                        );
                    }
                }
            }
        }

        if (messageParentDoc != null) {
            unlockCurrentMessage(context);
        }

        context.putInContextData(PIECE_JOINTE_LIST, pieceJointeList);
    }

    private PieceJointe addPieceJointe(
        String pieceJointeType,
        String typeEvenement,
        List<PieceJointe> pieceJointeList,
        CoreSession session
    ) {
        String titlePieceJointe = "";
        EvenementTypeDescriptor descriptor = SolonEppServiceLocator
            .getEvenementTypeService()
            .getEvenementType(typeEvenement);
        if (descriptor != null) {
            Map<String, PieceJointeDescriptor> map = descriptor.getPieceJointe();
            if (map != null) {
                PieceJointeDescriptor pieceJointeDescriptor = map.get(pieceJointeType);
                if (pieceJointeDescriptor != null) {
                    if (
                        !pieceJointeDescriptor.isMultivalue() &&
                        CollectionUtils.isEmpty(getListPieceJointe(pieceJointeType, pieceJointeList))
                    ) {
                        return null;
                    }
                    titlePieceJointe = pieceJointeDescriptor.getLabel();
                }
            }
        }

        if (StringUtils.isBlank(titlePieceJointe)) {
            titlePieceJointe =
                STServiceLocator
                    .getVocabularyService()
                    .getEntryLabel(SolonEppVocabularyConstant.VOCABULARY_PIECE_JOINTE_DIRECTORY, pieceJointeType);
            if (VocabularyServiceImpl.UNKNOWN_ENTRY.equals(titlePieceJointe)) {
                titlePieceJointe = pieceJointeType;
            }
        }

        titlePieceJointe = titlePieceJointe.replace("(s)", "");
        DocumentModel doc = SolonEppServiceLocator.getPieceJointeService().createBarePieceJointe(session);
        PieceJointe pj = doc.getAdapter(PieceJointe.class);
        pj.setNom(titlePieceJointe);
        pj.setTypePieceJointe(pieceJointeType);
        pieceJointeList.add(pj);

        return pj;
    }

    private List<PieceJointe> getListPieceJointe(String pieceJointeType, List<PieceJointe> pieceJointeList) {
        return pieceJointeList
            .stream()
            .filter(pj -> pieceJointeType.equals(pj.getTypePieceJointe()))
            .collect(Collectors.toList());
    }

    @Override
    public String saveCreerEvenement(SpecificContext context) {
        creerEvenement(context);
        applyModifications(context);

        // validation des metas required
        if (!checkMetadonnees(context)) {
            return null;
        }

        String idMessage = "";
        try {
            boolean publier = BooleanUtils.toBooleanDefaultIfNull(context.getFromContextData(PUBLIER), false);
            DocumentModel currentEvenementForCreation = context.getFromContextData(CURRENT_EVENEMENT_FOR_CREATION);
            DocumentModel currentVersionForCreation = context.getFromContextData(CURRENT_VERSION_FOR_CREATION);

            CreerVersionContext creerVersionContext = new CreerVersionContext();
            creerVersionContext.setPublie(publier);

            CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            creerVersionRequest.setEvenementDoc(currentEvenementForCreation);
            creerVersionRequest.setVersionDoc(currentVersionForCreation);

            // Initialisation du dossier
            Version version = currentVersionForCreation.getAdapter(Version.class);
            CoreSession session = context.getSession();

            DocumentModel dossierDoc = SolonEppServiceLocator.getDossierService().createBareDossier(session);
            Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            Evenement event = currentEvenementForCreation.getAdapter(Evenement.class);

            if (StringUtils.isNotBlank(version.getNor())) {
                dossier.setTitle(version.getNor());
                event.setDossier(version.getNor());
            } else {
                dossier.setTitle(event.getDossier());
                version.setNor(event.getDossier());
            }

            creerVersionRequest.setDossierDoc(dossierDoc);

            List<PieceJointe> pieceJointeList = context.getFromContextData(PIECE_JOINTE_LIST);
            creerVersionRequest
                .getPieceJointeDocList()
                .addAll(pieceJointeList.stream().map(PieceJointe::getDocument).collect(Collectors.toList()));

            checkPieceJointe(
                creerVersionRequest.getPieceJointeDocList(),
                publier,
                currentEvenementForCreation.getAdapter(Evenement.class)
            );

            VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
            versionCreationService.createVersion(session, creerVersionContext);

            idMessage =
                ofNullable(
                        SolonEppServiceLocator
                            .getMessageService()
                            .getMessageByEvenementId(
                                session,
                                currentEvenementForCreation.getAdapter(Evenement.class).getIdEvenement()
                            )
                    )
                    .map(DocumentModel::getId)
                    .orElse("");

            String message = null;
            String typeEvenement = context.getFromContextData(CURRENT_TYPE_EVENEMENT);
            // Vérification pour la FEV 549 : Informations parlementaires si l'événement a été publié quand c'est la
            // demande de l'utilisateur sinon un message d'erreur est affiché ainsi que le message de création du
            // brouillon
            if (TYPE_EVENEMENT_EVT45.equals(typeEvenement) && publier) {
                if (creerVersionContext.isPublier()) {
                    message = ResourceHelper.getString(EVENEMENT_PUBLIER_OK);
                } else {
                    message = ResourceHelper.getString(EVENEMENT_CREER_OK);
                    context.getMessageQueue().addErrorToQueue(ResourceHelper.getString(PUBLICATION_EPG_KO));
                }
            } else {
                if (publier) {
                    message = ResourceHelper.getString(EVENEMENT_PUBLIER_OK);
                } else {
                    message = ResourceHelper.getString(EVENEMENT_CREER_OK);
                }
            }
            context.getMessageQueue().addSuccessToQueue(message);
        } catch (NuxeoException e) {
            LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_CREATE_COMM_TEC, e);
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();
        }
        return idMessage;
    }

    @Override
    public void modifierEvenement(SpecificContext context) {
        consulterEvenement(context);

        Version currentVersion = context.getFromContextData(CURRENT_VERSION);

        String modeCreation = currentVersion.getModeCreation();
        if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_INIT_VALUE.equals(modeCreation)) {
            context.putInContextData(MODE_CREATION, LAYOUT_MODE_CREER);
        } else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(modeCreation)) {
            context.putInContextData(MODE_CREATION, LAYOUT_MODE_COMPLETER);
        } else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(modeCreation)) {
            context.putInContextData(MODE_CREATION, LAYOUT_MODE_RECTIFIER);
        }
    }

    @Override
    public void saveModifierEvenement(SpecificContext context) {
        consulterEvenement(context);
        applyModifications(context);

        // validation des metas required
        if (!checkMetadonnees(context)) {
            return;
        }

        try {
            // check verrou
            if (StringUtils.isNotEmpty(checkLockMessage(context))) {
                final String message = ResourceHelper.getString(EVENEMENT_LOCK_LOST);
                context.getMessageQueue().addErrorToQueue(message);
                return;
            }

            final CreerVersionContext creerVersionContext = new CreerVersionContext();
            final CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            DocumentModel currentEvenementForCreation = context.getFromContextData(CURRENT_EVENEMENT_FOR_CREATION);
            DocumentModel currentVersionForCreation = context.getFromContextData(CURRENT_VERSION_FOR_CREATION);
            creerVersionRequest.setEvenementDoc(currentEvenementForCreation);
            creerVersionRequest.setVersionDoc(currentVersionForCreation);

            List<PieceJointe> pieceJointeList = context.getFromContextData(PIECE_JOINTE_LIST);
            creerVersionRequest
                .getPieceJointeDocList()
                .addAll(pieceJointeList.stream().map(PieceJointe::getDocument).collect(Collectors.toList()));

            boolean publier = BooleanUtils.toBooleanDefaultIfNull(context.getFromContextData(PUBLIER), false);
            checkPieceJointe(
                creerVersionRequest.getPieceJointeDocList(),
                publier,
                currentEvenementForCreation.getAdapter(Evenement.class)
            );

            final Version version = currentVersionForCreation.getAdapter(Version.class);
            final String modeCreation = version.getModeCreation();
            final CoreSession session = context.getSession();
            final VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
            if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_INIT_VALUE.equals(modeCreation)) {
                final DocumentModel dossierDoc = session.getDocument(currentEvenementForCreation.getParentRef());
                creerVersionRequest.setDossierDoc(dossierDoc);
                if (publier) {
                    creerVersionContext.setPublie(true);
                }
                versionCreationService.createVersion(session, creerVersionContext);
            } else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(modeCreation)) {
                if (publier) {
                    versionCreationService.completerPublier(session, creerVersionContext);
                } else {
                    versionCreationService.completerBrouillon(session, creerVersionContext);
                }
            } else if (
                SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(modeCreation)
            ) {
                if (publier) {
                    versionCreationService.rectifierPublier(session, creerVersionContext);
                } else {
                    versionCreationService.rectifierBrouillon(session, creerVersionContext);
                }
            }

            context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString(EVENEMENT_MODIFIER_OK));

            unlockCurrentMessage(context);
        } catch (NuxeoException e) {
            LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_UPDATE_COMM_TEC, e);
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();
        }
    }

    @Override
    public void saveCompleterRectifierEvenement(SpecificContext context) {
        consulterEvenement(context);
        applyModifications(context);

        DocumentModel currentEvenementForCreation = context.getFromContextData(CURRENT_EVENEMENT_FOR_CREATION);
        boolean completer = SolonEppConstant.VERSION_ACTION_COMPLETER.equals(context.getFromContextData(MODE_CREATION));

        // validation des metas required sauf si complétion de EVT45
        if (
            (
                !completer ||
                !TYPE_EVENEMENT_EVT45.equals(currentEvenementForCreation.getAdapter(Evenement.class).getTypeEvenement())
            ) &&
            !checkMetadonnees(context)
        ) {
            return;
        }

        try {
            // check verrou
            if (StringUtils.isNotEmpty(checkLockMessage(context))) {
                final String message = ResourceHelper.getString(EVENEMENT_LOCK_LOST);
                context.getMessageQueue().addErrorToQueue(message);
                return;
            }

            CreerVersionContext creerVersionContext = new CreerVersionContext();
            CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            DocumentModel currentVersionForCreation = context.getFromContextData(CURRENT_VERSION_FOR_CREATION);
            creerVersionRequest.setEvenementDoc(currentEvenementForCreation);
            creerVersionRequest.setVersionDoc(currentVersionForCreation);

            List<PieceJointe> pieceJointeList = context.getFromContextData(PIECE_JOINTE_LIST);
            creerVersionRequest
                .getPieceJointeDocList()
                .addAll(pieceJointeList.stream().map(PieceJointe::getDocument).collect(Collectors.toList()));

            boolean publier = BooleanUtils.toBooleanDefaultIfNull(context.getFromContextData(PUBLIER), false);
            checkPieceJointe(
                creerVersionRequest.getPieceJointeDocList(),
                publier,
                currentEvenementForCreation.getAdapter(Evenement.class)
            );

            CoreSession session = context.getSession();
            CreerVersionResponse creerVersionResponse = null;
            Evenement event = null;
            VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
            if (completer) {
                if (publier) {
                    versionCreationService.completerPublier(session, creerVersionContext);
                } else {
                    versionCreationService.completerBrouillon(session, creerVersionContext);
                }
            } else {
                if (publier) {
                    creerVersionResponse = versionCreationService.rectifierPublier(session, creerVersionContext);
                } else {
                    creerVersionResponse = versionCreationService.rectifierBrouillon(session, creerVersionContext);
                }
                event = creerVersionResponse.getEvenementDoc().getAdapter(Evenement.class);
            }

            context
                .getMessageQueue()
                .addSuccessToQueue(
                    ResourceHelper.getString(
                        completer
                            ? EVENEMENT_COMPLETER_OK
                            : event.isEtatAttenteValidation()
                                ? EVENEMENT_RECTIFIER_ATTENTE_VALIDATION
                                : EVENEMENT_RECTIFIER_OK
                    )
                );

            unlockCurrentMessage(context);
        } catch (NuxeoException e) {
            LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_UPDATE_COMM_TEC, e);
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();
        }
    }

    private static void applyModifications(SpecificContext context) {
        Evenement currentEvenementForCreation = ofNullable(context.getCurrentDocument())
            .map(dm -> dm.getAdapter(Evenement.class))
            .orElse(null);

        Version currentVersionForCreation = context.getFromContextData(CURRENT_VERSION);
        Map<String, Object> metadonneesMap = context.getFromContextData(COMMUNICATION_METADONNEES_MAP);

        metadonneesMap
            .keySet()
            .stream()
            .map(CommunicationMetadonneeEnum::fromString)
            .map(MetadonneeMapperEnum::getMapperFromCommunicationField)
            .filter(Objects::nonNull)
            .forEach(
                mapper ->
                    mapper.invokeSetter(
                        currentEvenementForCreation,
                        currentVersionForCreation,
                        StringHelper.removeNullStrings(metadonneesMap.get(mapper.getField().getName()))
                    )
            );
        List<PieceJointe> pieceJointes = updatePiecesJointes(
            context,
            context.getFromContextData(PIECE_JOINTE_LIST),
            metadonneesMap
        );
        if (currentEvenementForCreation != null) {
            pieceJointes.addAll(createPiecesJointes(context, currentEvenementForCreation, metadonneesMap));

            context.putInContextData(CURRENT_EVENEMENT_FOR_CREATION, currentEvenementForCreation.getDocument());
            context.putInContextData(CURRENT_VERSION_FOR_CREATION, currentVersionForCreation.getDocument());
            context.putInContextData(CURRENT_TYPE_EVENEMENT, currentEvenementForCreation.getTypeEvenement());
        }
        context.putInContextData(PIECE_JOINTE_LIST, pieceJointes);
    }

    private static List<PieceJointe> updatePiecesJointes(
        SpecificContext context,
        List<PieceJointe> piecesJointes,
        Map<String, Object> metadonneesMap
    ) {
        return piecesJointes
            .stream()
            .map(pj -> updateOrDeletePieceJointe(context, pj, metadonneesMap))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private static PieceJointe updateOrDeletePieceJointe(
        SpecificContext context,
        PieceJointe pieceJointe,
        Map<String, Object> metadonneesMap
    ) {
        Map<String, Object> metadonneesPJ = metadonneesMap
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().startsWith(pieceJointe.getTitle()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        PieceJointe updatedPieceJointe = null;
        if (MapUtils.isNotEmpty(metadonneesPJ)) {
            updatedPieceJointe = updatePieceJointe(context, pieceJointe, metadonneesPJ);
        }
        return updatedPieceJointe;
    }

    /**
     * Met à jour une pièce jointe
     */
    private static PieceJointe updatePieceJointe(
        SpecificContext context,
        PieceJointe pieceJointe,
        Map<String, Object> metadonneesPJ
    ) {
        String pjTitle = pieceJointe.getTitle();
        if (metadonneesPJ.get(pjTitle + PIECE_JOINTE_TITRE_SUFFIX) != null) {
            pieceJointe.setNom((String) metadonneesPJ.get(pjTitle + PIECE_JOINTE_TITRE_SUFFIX));
        }
        if (metadonneesPJ.get(pjTitle + PIECE_JOINTE_URL_SUFFIX) != null) {
            pieceJointe.setUrl((String) metadonneesPJ.get(pjTitle + PIECE_JOINTE_URL_SUFFIX));
        }
        // Garder uniquement les fichiers qui n'ont pas été supprimés
        List<String> idFichiers = metadonneesPJ
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().contains(PIECE_JOINTE_FILE_SUFFIX))
            .map(entry -> (String) entry.getValue())
            .collect(Collectors.toList());
        pieceJointe.setPieceJointeFichierDocList(
            pieceJointe
                .getPieceJointeFichierDocList()
                .stream()
                .filter(dm -> idFichiers.contains(dm.getId()))
                .collect(Collectors.toList())
        );
        // Ajouter les nouveaux fichiers
        addUploadedFichiersToPieceJointe(
            pieceJointe,
            (String) metadonneesPJ.get(pjTitle + PIECE_JOINTE_UPLOAD_BATCH_ID_SUFFIX),
            context.getSession()
        );
        return pieceJointe;
    }

    /**
     * Ajoute les nouveaux fichiers de l'upload batch à la pièce jointe
     */
    private static void addUploadedFichiersToPieceJointe(
        PieceJointe pieceJointe,
        String uploadBatchId,
        CoreSession session
    ) {
        if (StringUtils.isNotBlank(uploadBatchId)) {
            BatchManager batchManager = ServiceUtil.getRequiredService(BatchManager.class);
            Batch batch = batchManager.getBatch(uploadBatchId);
            if (batch != null) {
                List<DocumentModel> pieceJointeFichierDocList = ObjectHelper.requireNonNullElseGet(
                    pieceJointe.getPieceJointeFichierDocList(),
                    ArrayList::new
                );
                pieceJointeFichierDocList.addAll(
                    batch
                        .getBlobs()
                        .stream()
                        .map(blob -> convertBlobToPieceJointeFichierDoc(blob, session))
                        .collect(Collectors.toList())
                );
                pieceJointe.setPieceJointeFichierDocList(pieceJointeFichierDocList);
            }
        }
    }

    private static List<PieceJointe> createPiecesJointes(
        SpecificContext context,
        Evenement evenement,
        Map<String, Object> metadonneesMap
    ) {
        return SolonEppServiceLocator
            .getEvenementTypeService()
            .getEvenementType(evenement.getTypeEvenement())
            .getPieceJointe()
            .keySet()
            .stream()
            .flatMap(typePJ -> createPiecesJointesForType(context, typePJ, metadonneesMap).stream())
            .collect(Collectors.toList());
    }

    /**
     * Crée les nouvelles pièces jointes d'un même type
     */
    private static List<PieceJointe> createPiecesJointesForType(
        SpecificContext context,
        String typePJ,
        Map<String, Object> metadonneesMap
    ) {
        return metadonneesMap
            .keySet()
            .stream()
            .filter(key -> key.startsWith(typePJ))
            .map(key -> META_KEY_SUFFIXES_PATTERN.matcher(key).replaceAll(StringUtils.EMPTY))
            .distinct()
            .map(key -> createPieceJointe(context, typePJ, metadonneesMap, key))
            .collect(Collectors.toList());
    }

    private static PieceJointe createPieceJointe(
        SpecificContext context,
        String typePJ,
        Map<String, Object> metadonneesMap,
        String metaKey
    ) {
        CoreSession session = context.getSession();
        PieceJointe pieceJointe = SolonEppServiceLocator
            .getPieceJointeService()
            .createBarePieceJointe(session)
            .getAdapter(PieceJointe.class);
        pieceJointe.setTypePieceJointe(typePJ);
        pieceJointe.setNom((String) metadonneesMap.get(metaKey + PIECE_JOINTE_TITRE_SUFFIX));
        pieceJointe.setUrl((String) metadonneesMap.get(metaKey + PIECE_JOINTE_URL_SUFFIX));
        addUploadedFichiersToPieceJointe(
            pieceJointe,
            (String) metadonneesMap.get(metaKey + PIECE_JOINTE_UPLOAD_BATCH_ID_SUFFIX),
            session
        );
        return pieceJointe;
    }

    private static DocumentModel convertBlobToPieceJointeFichierDoc(Blob blob, CoreSession session) {
        try {
            blob.setDigest(MD5Util.getMD5Hash(blob.getByteArray()));
            PieceJointeFichierValidator pieceJointeFichierValidator = new PieceJointeFichierValidator();
            pieceJointeFichierValidator.validatePieceJointeFichierFileName(blob.getFilename());
            DocumentModel pieceJointeFichierDoc = SolonEppServiceLocator
                .getPieceJointeFichierService()
                .createBarePieceJointeFichier(session);
            PieceJointeFichier pieceJointeFichier = pieceJointeFichierDoc.getAdapter(PieceJointeFichier.class);
            pieceJointeFichier.setContent(blob);
            pieceJointeFichier.setFilename(blob.getFilename());
            pieceJointeFichier.setDigestSha512(SHA512Util.getSHA512Hash(blob.getByteArray()));
            return pieceJointeFichierDoc;
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    private boolean checkMetadonnees(SpecificContext context) {
        DocumentModel currentEvenementForCreation = context.getFromContextData(CURRENT_EVENEMENT_FOR_CREATION);
        DocumentModel currentVersionForCreation = context.getFromContextData(CURRENT_VERSION_FOR_CREATION);
        boolean publier = BooleanUtils.toBooleanDefaultIfNull(context.getFromContextData(PUBLIER), false);
        boolean metadonneesValides = true;

        try {
            final EvenementValidator evtValidator = new EvenementValidator(context.getSession());
            evtValidator.validateMetaObligatoire(currentEvenementForCreation, publier);

            if (publier) {
                final VersionValidator versionValidator = new VersionValidator();
                versionValidator.validateMetaObligatoire(currentVersionForCreation, currentEvenementForCreation);

                // check piece jointe
                String type = StringUtils.defaultIfBlank(
                    context.getFromContextData(CURRENT_TYPE_EVENEMENT),
                    context.getFromContextData(CURRENT_TYPE_EVENEMENT_SUCCESSIF)
                );
                final EvenementTypeDescriptor descriptor = SolonEppServiceLocator
                    .getEvenementTypeService()
                    .getEvenementType(type);
                List<String> missingAttachmentFileLabel = new ArrayList<>();
                for (Entry<String, PieceJointeDescriptor> entry : descriptor.getPieceJointe().entrySet()) {
                    if (entry.getValue().isObligatoire() && !hasPieceJointeOfType(context, entry.getKey())) {
                        context.putInContextData(TYPE_PIECE_JOINTE, entry.getValue().getType());
                        missingAttachmentFileLabel.add(
                            SolonEppActionsServiceLocator.getMetadonneesActionService().getPieceJointeType(context)
                        );
                        metadonneesValides = false;
                    }
                }
                if (!metadonneesValides) {
                    final String message = ResourceHelper.getString(
                        COMPLETER_META_OBLIGATOIRES,
                        missingAttachmentFileLabel.stream().collect(Collectors.joining(", "))
                    );
                    context.getMessageQueue().addErrorToQueue(message);
                }
            }
        } catch (NuxeoException e) {
            LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_GET_META_DONNEE_TEC, e);
            final String message = ResourceHelper.getString("label.epp.meta.obligatoires");
            context.getMessageQueue().addErrorToQueue(message);
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            metadonneesValides = false;
        }
        return metadonneesValides;
    }

    /**
     * Lock le message courant si cela est possible sinon affiche la popup de forçage du verrou
     *
     * @param context
     * @return
     */
    @Override
    public String checkLockMessage(SpecificContext context) {
        // check verrou
        final String locker = getMessageLocker(context);
        if (locker != null) {
            if (!isCurrentUserLocker(context, locker)) {
                return ResourceHelper.getString("evenement.lock.already.lock.user", locker);
            }
        } else if (!lockCurrentMessage(context)) {
            final String message = ResourceHelper.getString(EVENEMENT_CANT_LOCK);
            context.getMessageQueue().addErrorToQueue(message);
        }
        return StringUtils.EMPTY;
    }

    /**
     * Renvoie l'utilisateur qui a le verrou sur l'événement
     *
     * @param context
     * @return
     */
    public String getMessageLocker(SpecificContext context) {
        String locker = null;
        final STLockService lockService = STServiceLocator.getSTLockService();

        final Message message = context.getFromContextData(CURRENT_MESSAGE);

        final Lock lockDetail = lockService.getLockDetails(context.getSession(), message.getDocument());

        if (lockDetail != null) {
            locker = lockDetail.getOwner();
        }

        return locker;
    }

    /**
     * Renvoie true si l'utilisateur courant possède le verrou sur l'événement courant
     *
     * @param context
     * @param locker
     * @return
     */
    private boolean isCurrentUserLocker(SpecificContext context, String locker) {
        EppPrincipal eppPrincipal = (EppPrincipal) context.getSession().getPrincipal();
        return locker != null && eppPrincipal.getName().equals(locker);
    }

    /**
     * Lock le message courant
     *
     * @param context
     * @return
     */
    private boolean lockCurrentMessage(SpecificContext context) {
        EppPrincipal eppPrincipal = (EppPrincipal) context.getSession().getPrincipal();
        final STLockService lockService = STServiceLocator.getSTLockService();
        final Message message = context.getFromContextData(CURRENT_MESSAGE);
        if (
            message != null &&
            lockService.isLockActionnableByUser(context.getSession(), message.getDocument(), eppPrincipal)
        ) {
            return lockService.lockDoc(context.getSession(), message.getDocument());
        }
        return false;
    }

    /**
     * Unlock le message courant
     *
     * @param context
     * @return
     */
    @Override
    public boolean unlockCurrentMessage(SpecificContext context) {
        CoreSession session = context.getSession();
        EppPrincipal eppPrincipal = (EppPrincipal) session.getPrincipal();
        final STLockService lockService = STServiceLocator.getSTLockService();
        final Message message = context.getFromContextData(CURRENT_MESSAGE);
        final DocumentModel messageDoc = message.getDocument();
        if (
            message != null &&
            lockService.getLockDetails(session, messageDoc) != null &&
            lockService.isLockActionnableByUser(session, messageDoc, eppPrincipal)
        ) {
            return lockService.unlockDoc(session, messageDoc);
        }
        return false;
    }

    /**
     * Retourne true si la liste contient des pièces jointes du type renseigné
     *
     * @param context
     * @param type
     * @return
     */
    private boolean hasPieceJointeOfType(SpecificContext context, String type) {
        List<PieceJointe> pieceJointeList = context.getFromContextData(PIECE_JOINTE_LIST);
        return pieceJointeList.stream().anyMatch(pieceJointe -> pieceJointe.getTypePieceJointe().equals(type));
    }

    /**
     * Valide les pièces jointes
     *
     * @param listDocumentModel
     * @param publie
     * @param evenement
     */
    private void checkPieceJointe(
        final List<DocumentModel> listDocumentModel,
        final boolean publie,
        Evenement evenement
    ) {
        final PieceJointeValidator pieceJointeValidator = new PieceJointeValidator();
        pieceJointeValidator.validatePiecesJointes(listDocumentModel, !publie, evenement.getTypeEvenement());
    }

    @Override
    public void publierEvenement(SpecificContext context) {
        try {
            CreerVersionContext creerVersionContext = new CreerVersionContext();
            CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            creerVersionContext.setPublie(true);

            String idMessage = context.getFromContextData(ID);
            DocumentModel messageDoc = context.getSession().getDocument(new IdRef(idMessage));
            DocumentModel evenementDoc = context
                .getSession()
                .getDocument(new IdRef(messageDoc.getAdapter(Message.class).getCaseDocumentId()));
            creerVersionRequest.setEvenementDoc(evenementDoc);

            CoreSession session = context.getSession();
            DocumentModel versionDoc = SolonEppServiceLocator
                .getVersionService()
                .getVersionActive(session, evenementDoc, SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE);
            creerVersionRequest.setVersionDoc(versionDoc);

            DocumentModel dossierDoc = session.getDocument(evenementDoc.getParentRef());
            creerVersionRequest.setDossierDoc(dossierDoc);

            // check meta
            EvenementValidator evenementValidator = new EvenementValidator(session);
            evenementValidator.validateMetaObligatoire(evenementDoc, true);
            VersionValidator versionValidator = new VersionValidator();
            versionValidator.validateMetaObligatoire(versionDoc, evenementDoc);

            // chargement des pièces jointes
            PieceJointeService pjService = SolonEppServiceLocator.getPieceJointeService();
            List<DocumentModel> pjDocList = pjService.getVersionPieceJointeListWithFichier(session, versionDoc);
            creerVersionRequest.getPieceJointeDocList().addAll(pjDocList);
            checkPieceJointe(
                creerVersionRequest.getPieceJointeDocList(),
                true,
                evenementDoc.getAdapter(Evenement.class)
            );

            VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
            Version version = versionDoc.getAdapter(Version.class);
            if (
                SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(
                    version.getModeCreation()
                )
            ) {
                versionCreationService.completerPublier(session, creerVersionContext);
            } else if (
                SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(
                    version.getModeCreation()
                )
            ) {
                versionCreationService.rectifierPublier(session, creerVersionContext);
            } else {
                versionCreationService.createVersion(session, creerVersionContext);
            }

            context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString(EVENEMENT_PUBLIER_OK));
        } catch (NuxeoException e) {
            LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_UPDATE_COMM_TEC, "Echec de publication", e);
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();
        }
    }

    @Override
    public void forceUnlockCurrentMessage(SpecificContext context) {
        String idMessage = context.getFromContextData(ID);
        Boolean skipLock = context.getFromContextData(SKIP_LOCK);
        CoreSession session = context.getSession();
        DocumentModel messageDoc = session.getDocument(new IdRef(idMessage));
        Message message = messageDoc.getAdapter(Message.class);
        context.putInContextData(CURRENT_MESSAGE, message);

        final STLockService lockService = STServiceLocator.getSTLockService();
        
        if(Boolean.TRUE.equals(skipLock)) {
            lockService.unlockDocUnrestricted(session, message.getDocument());
        } else {
            final STMailService mailService = STServiceLocator.getSTMailService();
            final UserManager userManager = STServiceLocator.getUserManager();
            
	        final Lock lockDetails = lockService.getLockDetails(session, message.getDocument());
	        if (lockDetails != null && !lockDetails.getOwner().equals(session.getPrincipal().getName())) {
	            final String locker = lockDetails.getOwner();
	            lockService.unlockDocUnrestricted(session, message.getDocument());
	            final DocumentModel userDoc = userManager.getUserModel(locker);
	            if (userDoc != null) {
	                // Send mail to user
	                final STUser user = userDoc.getAdapter(STUser.class);
	                final List<STUser> userList = Arrays.asList(user);
	                mailService.sendMailToUserList(
	                    userList,
	                    ResourceHelper.getString("evenement.verrou.mail.titre"),
	                    ResourceHelper.getString("evenement.verrou.mail.objet", message.getTitle())
	                );
	            }
	            lockCurrentMessage(context);
	        }
        }
    }

    @Override
    public String getEtatMessage(SpecificContext context) {
        Message message = context.getFromContextData(CURRENT_MESSAGE);
        return message != null ? CorbeilleProviderHelper.getEtatMessage(message, context.getSession()).toString() : "";
    }

    @Override
    public String getEtatEvenement(SpecificContext context) {
        Evenement evenement = context.getCurrentDocument().getAdapter(Evenement.class);
        Version version = context.getFromContextData(CURRENT_VERSION);
        return CorbeilleProviderHelper
            .getEtatEvenement(evenement, ofNullable(version).map(Version::getModeCreation).orElse(""))
            .name();
    }
}
