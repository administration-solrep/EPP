package fr.dila.ss.ui.services.impl;

import static fr.dila.ss.ui.enums.SSContextDataKey.STEP_DOC;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.DossierDistributionService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.EditionEtapeFdrDTO;
import fr.dila.ss.ui.bean.actions.NoteEtapeActionDTO;
import fr.dila.ss.ui.bean.actions.RoutingActionDTO;
import fr.dila.ss.ui.bean.fdr.ContainerDTO;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.bean.fdr.EtapeDTO;
import fr.dila.ss.ui.bean.fdr.EtatEtapeDTO;
import fr.dila.ss.ui.bean.fdr.FdrTableDTO;
import fr.dila.ss.ui.bean.fdr.NoteEtapeDTO;
import fr.dila.ss.ui.enums.EtapeLifeCycle;
import fr.dila.ss.ui.enums.EtapeValidationStatus;
import fr.dila.ss.ui.enums.FeuilleRouteEtapeOrder;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.RouteStepNoteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.actions.SSDocumentRoutingActionService;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STLifeCycleConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.comment.api.Comment;
import org.nuxeo.ecm.platform.comment.api.Comments;

public class SSFeuilleRouteUIServiceImpl implements SSFeuilleRouteUIService {
    private static final STLogger LOG = STLogFactory.getLog(SSFeuilleRouteUIServiceImpl.class);

    protected DocumentRoutingService getDocumentRoutingService() {
        return SSServiceLocator.getDocumentRoutingService();
    }

    @Override
    public FdrTableDTO getFeuilleRouteDTO(SpecificContext context) {
        FdrTableDTO dto = new FdrTableDTO();
        String id = context.getFromContextData(STContextDataKey.ID);
        DocumentModel relatedRouteDocumentModel = context.getSession().getDocument(new IdRef(id));

        context.setCurrentDocument(relatedRouteDocumentModel);
        //Construit les éléments enfant + récupération du niveau max
        dto.setTotalNbLevel(
            processElementsInFolder(context, relatedRouteDocumentModel, dto, false, 0, null, new ArrayList<>())
        );

        return dto;
    }

    protected Integer processElementsInFolder(
        SpecificContext context,
        DocumentModel relatedRouteDocumentModel,
        FdrTableDTO table,
        boolean parentIsStripped,
        int level,
        ContainerDTO parentFolder,
        List<String> parentsIds
    ) {
        CoreSession session = context.getSession();
        DocumentModelList children = session.getChildren(context.getCurrentDocument().getRef());
        boolean first = true;
        Integer maxDepth = 0;

        Integer nbChild = 0;
        Integer folderPosition = 1;
        int nbLigne = 1;
        for (DocumentModel child : children) {
            if (!STLifeCycleConstant.DELETED_STATE.equals(child.getCurrentLifeCycleState())) {
                boolean isStripped = isStripped(parentIsStripped, nbLigne, level);
                context.setCurrentDocument(child);
                if (child.isFolder() && CollectionUtils.isNotEmpty(session.getChildren(child.getRef()))) {
                    ContainerDTO folder = new ContainerDTO();
                    folder.setLevel(level + 1);
                    if (first) {
                        folder.setParent(parentFolder);
                    }
                    folder.setId(child.getRef().toString());
                    folder.setPosition(folderPosition);

                    parentsIds.add("branch-" + folder.getId());

                    maxDepth =
                        Math.max(
                            maxDepth,
                            processElementsInFolder(
                                context,
                                relatedRouteDocumentModel,
                                table,
                                isStripped,
                                level + 1,
                                folder,
                                parentsIds
                            ) +
                            1
                        );
                    parentsIds.remove(parentsIds.size() - 1);
                    folder.setDepth(maxDepth);
                    nbChild += folder.getNbChilds();
                    //On détermine si on est sur une branche parallele
                    FeuilleRouteExecutionType typeFolder = FeuilleRouteStepFolderSchemaUtil.getExecutionType(child);
                    folder.setIsParallel(FeuilleRouteExecutionType.parallel.equals(typeFolder));

                    context.setCurrentDocument(child);
                    context.putInContextData(SSContextDataKey.FEUILLE_ROUTE, relatedRouteDocumentModel);
                    initStepFolderActionsDTO(context);
                    folder.setActions(context.getActions(getFolderActions(folder)));

                    folderPosition++;
                } else if (!child.isFolder()) {
                    nbChild++;
                    EtapeDTO etape = createStepDTO(context, relatedRouteDocumentModel, level, isStripped, parentsIds);
                    if (first) {
                        etape.setParent(parentFolder);
                    }
                    //On ajoute l'étape dans le tableau
                    table.getLines().add(etape);

                    //Si on a des notes dans notre étape on rajoute une ligne au parent
                    if (CollectionUtils.isNotEmpty(etape.getNotes())) {
                        nbChild++;
                    }
                }
                first = false;
                nbLigne++;
            }
        }
        if (parentFolder != null) {
            parentFolder.setNbChilds(nbChild);
        }

        return maxDepth;
    }

    /**
     * Permet de savoir si on a besoin de griser la ligne dans le tableau
     *
     * @param parentIsStripped : la valeur du parent (grisé ou non)
     * @param nbLigne : Le numéro de la ligne
     * @param level : le niveau de la ligne (permet de savoir s'il s'agit d'un enfant)
     * @return <code>True</<code> s'il faut griser la ligne
     */
    private boolean isStripped(boolean parentIsStripped, int nbLigne, int level) {
        if (level == 0) {
            return nbLigne % 2 == 0;
        } else {
            return parentIsStripped;
        }
    }

    private SSActionCategory getFolderActions(ContainerDTO folder) {
        if (folder.getIsParallel()) {
            return SSActionCategory.STEP_FOLDER_PARALLEL_ACTIONS_LIST;
        } else {
            return SSActionCategory.STEP_FOLDER_SERIAL_ACTIONS_LIST;
        }
    }

    @Override
    public void initStepFolderActionsDTO(SpecificContext context) {
        DocumentModel feuilleRouteDoc = context.getFromContextData(SSContextDataKey.FEUILLE_ROUTE);
        Objects.requireNonNull(feuilleRouteDoc, "un document model de feuille de route est attendu");

        SSDocumentRoutingActionService documentRoutingActionService = SSActionsServiceLocator.getDocumentRoutingActionService();
        RoutingActionDTO routingActionDTO = new RoutingActionDTO();
        routingActionDTO.setIsEditableRouteElement(documentRoutingActionService.isEditableRouteElement(context));
        routingActionDTO.setFolderDeletable(documentRoutingActionService.canFolderBeDeleted(context));
        routingActionDTO.setIsCurrentRouteLockedByCurrentUser(
            documentRoutingActionService.isCurrentRouteLockedByCurrentUser(context.getSession(), feuilleRouteDoc)
        );
        routingActionDTO.setIsFeuilleRouteUpdatable(
            documentRoutingActionService.isFeuilleRouteUpdatable(context, context.getSession(), feuilleRouteDoc)
        );

        context.putInContextData("folderActions", routingActionDTO);
    }

    protected EtapeDTO createStepDTO(
        SpecificContext context,
        DocumentModel relatedRouteDocumentModel,
        Integer level,
        boolean isStripped,
        List<String> parentsIds
    ) {
        CoreSession session = context.getSession();
        DocumentModel child = context.getCurrentDocument();

        EtapeDTO etape = docToEtapeDto(child);

        etape.setDepth(level);
        SSRouteStep etapeAdapter = child.getAdapter(SSRouteStep.class);
        if (etapeAdapter.isObligatoireMinistere()) {
            etape.setObligatoire(EtapeDTO.MINISTERE);
        } else if (etapeAdapter.isObligatoireSGG()) {
            etape.setObligatoire(EtapeDTO.SGG);
        }

        //Si le label ministere est vide on va chercher depuis l'ID mailbox
        if (StringUtils.isBlank(etape.getMinistere()) && etape.getMailBoxId() != null) {
            etape.setMinistere(getMinisteresEditionFromMailboxId(etape.getMailBoxId()));
        }

        //Si le label poste est vide on va chercher depuis l'ID mailbox
        if (StringUtils.isBlank(etape.getPoste()) && etape.getMailBoxId() != null) {
            etape.setPoste(getMailboxTitleFromId(etape.getMailBoxId(), session));
        }

        etape.setIsStripped(isStripped);

        VocabularyService vocService = ServiceUtil.getRequiredService(VocabularyService.class);
        etape.setAction(vocService.getEntryLabel("cm_routing_task_type", etape.getAction()));

        etape.setEtat(
            getEtatEtape(child.getCurrentLifeCycleState(), etapeAdapter.getValidationStatus(), etapeAdapter.getType())
        );

        etape.getParentsId().addAll(parentsIds);

        initEtapeActionsDTO(context, relatedRouteDocumentModel, child);

        List<Action> actions = context.getActions(SSActionCategory.STEP_ACTIONS_LIST);
        etape.setActions(actions);

        etape.setNotes(getNotesFromDoc(child, context));

        return etape;
    }

    protected EtapeDTO docToEtapeDto(DocumentModel doc) {
        return MapDoc2Bean.docToBean(doc, EtapeDTO.class);
    }

    @Override
    public void initEtapeActionsDTO(SpecificContext context) {
        DocumentModel feuilleRouteDoc = context.getFromContextData(SSContextDataKey.FEUILLE_ROUTE);
        DocumentModel etapeDoc = context.getFromContextData(SSContextDataKey.STEP_DOC);
        initEtapeActionsDTO(context, feuilleRouteDoc, etapeDoc);
    }

    protected void initEtapeActionsDTO(SpecificContext context, DocumentModel feuilleRouteDoc, DocumentModel etapeDoc) {
        CoreSession session = context.getSession();
        context.putInContextData(SSContextDataKey.FEUILLE_ROUTE, feuilleRouteDoc);
        context.putInContextData(STEP_DOC, etapeDoc);

        SSDocumentRoutingActionService documentRoutingActionService = SSActionsServiceLocator.getDocumentRoutingActionService();
        RouteStepNoteActionService routeStepNoteActionService = SSActionsServiceLocator.getRouteStepNoteActionService();
        RoutingActionDTO routingActionDTO = context.getFromContextData(SSContextDataKey.ETAPE_ACTIONS);
        routingActionDTO = ObjectHelper.requireNonNullElseGet(routingActionDTO, RoutingActionDTO::new);
        routingActionDTO.setIsEditableRouteElement(documentRoutingActionService.isEditableRouteElement(context));
        routingActionDTO.setIsEditableNote(
            routeStepNoteActionService.isEditableNote(context, session, feuilleRouteDoc, feuilleRouteDoc, etapeDoc)
        );
        routingActionDTO.setIsRouteFolder(documentRoutingActionService.isRouteFolder(etapeDoc));
        routingActionDTO.setIsEditableEtapeObligatoire(
            getDocumentRoutingService().isEtapeObligatoireUpdater(session, feuilleRouteDoc)
        );
        routingActionDTO.setIsSerialStepFolder(documentRoutingActionService.isSerialStepFolder(context));
        routingActionDTO.setStepEditable(documentRoutingActionService.canEditStep(context));
        routingActionDTO.setStepDeletable(documentRoutingActionService.canStepBeDeleted(context));
        routingActionDTO.setIsCurrentRouteLockedByCurrentUser(
            documentRoutingActionService.isCurrentRouteLockedByCurrentUser(context.getSession(), feuilleRouteDoc)
        );
        routingActionDTO.setIsFeuilleRouteUpdatable(
            documentRoutingActionService.isFeuilleRouteUpdatable(context, context.getSession(), feuilleRouteDoc)
        );
        routingActionDTO.setIsFeuilleRouteInstance(
            feuilleRouteDoc.getAdapter(SSFeuilleRoute.class).isFeuilleRouteInstance()
        );
        routingActionDTO.setIsModeleFeuilleRoute(
            documentRoutingActionService.isModeleFeuilleRoute(context, feuilleRouteDoc)
        );
        routingActionDTO.setIsStepCopied(documentRoutingActionService.isStepCopied(context));
        routingActionDTO.setIsStepPourInitialisation(documentRoutingActionService.isStepInitialisation(context));

        context.putInContextData(SSContextDataKey.ETAPE_ACTIONS, routingActionDTO);
    }

    EtatEtapeDTO getEtatEtape(
        final String currentLifecycleState,
        final String validationStatus,
        final String typeEtape
    ) {
        if ("done".equals(currentLifecycleState)) {
            EtapeValidationStatus validationStatusEnum = getValidationStatut(validationStatus, typeEtape);
            return new EtatEtapeDTO(validationStatusEnum.getLabelKey(), validationStatusEnum.getIcon());
        } else {
            EtapeLifeCycle lifeCycle = EtapeLifeCycle.valueOf(currentLifecycleState.toUpperCase());
            return new EtatEtapeDTO(lifeCycle.getLabelKey(), lifeCycle.getIcon());
        }
    }

    // Override dans EPG et Reponses pour récupérer les statut de validation
    protected EtapeValidationStatus getValidationStatut(final String validationStatus, final String typeEtape) {
        throw new NuxeoException("Statut de validation des étapes non accessible");
    }

    List<NoteEtapeDTO> getNotesFromDoc(DocumentModel doc, SpecificContext context) {
        return getNotesFromDoc(doc, doc, context);
    }

    private List<NoteEtapeDTO> getNotesFromDoc(DocumentModel doc, DocumentModel routeStep, SpecificContext context) {
        context.putInContextData(SSContextDataKey.COMMENT_DOC, doc);
        context.putInContextData(SSContextDataKey.STEP_DOC, routeStep);
        List<DocumentModel> comments = SSActionsServiceLocator.getRouteStepNoteActionService().getCommentList(context);
        return comments
            .stream()
            .map(comment -> createNoteEtape(comment, routeStep, context))
            .collect(Collectors.toList());
    }

    private NoteEtapeDTO createNoteEtape(DocumentModel commentDoc, DocumentModel routeStep, SpecificContext context) {
        NoteEtapeDTO note = new NoteEtapeDTO();
        Comment comment = Comments.newComment(commentDoc);
        note.setId(comment.getId());
        note.setAuteur(comment.getAuthor());
        Instant date = comment.getModificationDate() != null
            ? comment.getModificationDate()
            : comment.getCreationDate();
        if (date != null) {
            note.setDate(Date.from(date));
        }
        note.setContent(comment.getText());
        note.setParentId(comment.getParentId());

        context.putInContextData(SSContextDataKey.COMMENT_DOC, commentDoc);
        NoteEtapeActionDTO noteEtapeActionDTO = new NoteEtapeActionDTO();
        noteEtapeActionDTO.setIsNoteAuthorOrHasSamePoste(
            SSUIServiceLocator.getSSCommentManagerUIService().isInAuthorPoste(context)
        );
        noteEtapeActionDTO.setNoteAuthor(SSUIServiceLocator.getSSCommentManagerUIService().isAuthor(context));
        context.putInContextData(SSContextDataKey.NOTE_ETAPE_ACTIONS, noteEtapeActionDTO);

        List<Action> noteActions = context.getActions(SSActionCategory.ROUTE_STEP_NOTE_ACTIONS);
        note.setActions(noteActions);

        //On charge les réponses
        note.setReponses(getNotesFromDoc(commentDoc, routeStep, context));

        return note;
    }

    /**
     * Retourne le titre de la mailbox poste en fonction de son identifiant technique.
     *
     * @param mailboxId Identifiant technique de la mailbox (ex: "poste-1234")
     * @return Titre de la mailbox (ex: "Agents BDC")
     */
    protected String getMailboxTitleFromId(String mailboxId, CoreSession session) {
        // Étapes sans destinataire
        if (StringUtils.isEmpty(mailboxId)) {
            return "";
        }

        // Recherche le titre de la Mailbox
        final MailboxService mailboxService = STServiceLocator.getMailboxService();
        try {
            return mailboxService.getMailboxTitle(session, mailboxId);
        } catch (Exception e) {
            LOG.warn(session, STLogEnumImpl.DEFAULT, e);
            throw new NuxeoException("**nom du poste inconnu**");
        }
    }

    /**
     * Retourne l'edition des ministères en fonction de l'identifiant technique de la mailbox.
     *
     * @param mailboxId Identifiant technique de la mailbox (ex: "poste-1234")
     * @return Edition des ministères
     */
    protected String getMinisteresEditionFromMailboxId(String mailboxId) {
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        return mailboxPosteService.getMinisteresEditionFromMailboxId(mailboxId);
    }

    @Override
    public void addEtapes(SpecificContext context) {
        CoreSession session = context.getSession();
        CreationEtapeDTO creationEtapeDTO = context.getFromContextData(SSContextDataKey.CREATION_ETAPE_DTO);
        DocumentModel routeElementDoc = session.getDocument(new IdRef(creationEtapeDTO.getIdBranche()));
        FeuilleRouteElement relatedRouteElement = routeElementDoc.getAdapter(FeuilleRouteElement.class);
        FeuilleRoute feuilleRoute = relatedRouteElement.getFeuilleRoute(session);
        DocumentModel feuilleRouteDoc = feuilleRoute.getDocument();
        context.putInContextData(SSContextDataKey.FEUILLE_ROUTE, feuilleRouteDoc);
        FeuilleRouteEtapeOrder order = creationEtapeDTO.getTypeAjoutEnum();

        if (!canAddEtapes(context, order, feuilleRoute.getDocument(), routeElementDoc)) {
            throw new STAuthorizationException("L'accès à l'ajout d'un bloc d'étapes n'est pas autorisé");
        }
        saveEtapes(context, feuilleRouteDoc);
    }

    protected void saveEtapes(SpecificContext context, DocumentModel feuilleRouteDoc) {
        CoreSession session = context.getSession();
        SSDocumentRoutingActionService documentRoutingActionService = SSActionsServiceLocator.getDocumentRoutingActionService();
        // Si l'élément est un modele alors on utilise la fdr
        // Sinon on utilise le dossier pour la journalisation
        if (documentRoutingActionService.isModeleFeuilleRoute(context, feuilleRouteDoc)) {
            context.setCurrentDocument(feuilleRouteDoc);
        } else {
            DossierDistributionService dossierDistributionService = SSServiceLocator.getDossierDistributionService();
            DocumentModel dossierDoc = dossierDistributionService.getDossierFromDocumentRouteId(
                session,
                feuilleRouteDoc.getId()
            );
            context.setCurrentDocument(dossierDoc);
        }
        documentRoutingActionService.saveRouteElementMass(context);
    }

    protected boolean canAddEtapes(
        SpecificContext context,
        FeuilleRouteEtapeOrder order,
        DocumentModel feuilleRouteDoc,
        DocumentModel elementDoc
    ) {
        boolean canAdd = false;

        context.setCurrentDocument(elementDoc);
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP.equals(elementDoc.getType())) {
            initEtapeActionsDTO(context, feuilleRouteDoc, elementDoc);
            if (order == FeuilleRouteEtapeOrder.AFTER) {
                canAdd = context.getAction(SSActionEnum.ADD_STEP_AFTER) != null;
            } else if (order == FeuilleRouteEtapeOrder.BEFORE) {
                canAdd = context.getAction(SSActionEnum.ADD_STEP_BEFORE) != null;
            }
        } else if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(elementDoc.getType())) {
            initStepFolderActionsDTO(context);
            if (order == FeuilleRouteEtapeOrder.AFTER) {
                canAdd = context.getAction(SSActionEnum.ADD_STEP_AFTER_BRANCH) != null;
            } else if (order == FeuilleRouteEtapeOrder.BEFORE) {
                canAdd = context.getAction(SSActionEnum.ADD_STEP_BEFORE_BRANCH) != null;
            } else if (order == FeuilleRouteEtapeOrder.IN) {
                canAdd = context.getAction(SSActionEnum.ADD_BRANCH) != null;
            }
        }

        return canAdd;
    }

    @Override
    public EtapeDTO getEtapeDTO(SpecificContext context) {
        DocumentModel stepDoc = context.getCurrentDocument();
        SSRouteStep step = stepDoc.getAdapter(SSRouteStep.class);
        EtapeDTO etapeDto = docToEtapeDto(stepDoc);
        String mailboxId = step.getDistributionMailboxId();
        etapeDto.setPosteId(StringUtils.isNotEmpty(mailboxId) ? mailboxId.substring(6) : "");
        etapeDto.setPoste(getMailboxTitleFromId(mailboxId, context.getSession()));

        return etapeDto;
    }

    @Override
    public EtapeDTO saveEtape(SpecificContext context) {
        CoreSession session = context.getSession();
        EditionEtapeFdrDTO editstep = context.getFromContextData(SSContextDataKey.EDITION_ETAPE_FDR_DTO);
        EtapeDTO etape = new EtapeDTO();
        etape.setAction(editstep.getTypeEtape());
        String mailboxId = "poste-" + editstep.getDestinataire();
        etape.setMailBoxId(mailboxId);
        etape.setPoste(getMailboxTitleFromId(mailboxId, session));
        etape.setMinistere(getMinisteresEditionFromMailboxId(mailboxId));
        etape.setDeadLine(NumberUtils.isParsable(editstep.getEcheance()) ? Long.parseLong(editstep.getEcheance()) : 0);
        etape.setValAuto(editstep.getValAuto());
        etape.setObligatoire(editstep.getObligatoire());

        DocumentModel etapeDoc = session.getDocument(new IdRef(editstep.getStepId()));
        MapDoc2Bean.beanToDoc(etape, etapeDoc, true);
        session.saveDocument(etapeDoc);

        context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString("fdr.form.updatestep.success"));

        SSRouteStep routeStep = etapeDoc.getAdapter(SSRouteStep.class);
        FeuilleRoute feuilleRoute = routeStep.getFeuilleRoute(session);
        context.setCurrentDocument(etapeDoc);
        context.putInContextData(SSContextDataKey.FEUILLE_ROUTE, feuilleRoute.getDocument());
        initStepFolderActionsDTO(context);
        return createStepDTO(context, feuilleRoute.getDocument(), editstep.getTotalNbLevel(), true, new ArrayList<>());
    }

    @Override
    public List<String> getNextStepLabels(SpecificContext context, String id) {
        List<String> etapesAVenir = SSServiceLocator
            .getSSFeuilleRouteService()
            .getEtapesAVenir(context.getSession(), id);
        return CollectionUtils.isNotEmpty(etapesAVenir) ? etapesAVenir : Collections.singletonList("Aucune");
    }

    @Override
    public List<String> getCurrentStepLabel(SpecificContext context) {
        List<STDossierLink> dossierLinks = SSActionsServiceLocator
            .getSSCorbeilleActionService()
            .getCurrentDossierLinks(context);
        return dossierLinks.stream().map(dl -> getStepLabelFromDossierLink(context, dl)).collect(Collectors.toList());
    }

    private String getStepLabelFromDossierLink(SpecificContext context, STDossierLink dossierLink) {
        StringBuilder builder = new StringBuilder();

        if (dossierLink != null) {
            builder.append(
                SSServiceLocator.getSSFeuilleRouteService().getCurrentStep(context.getSession(), dossierLink)
            );
            // S'il y a plus d'une étape en cours, on indique qu'il s'agit d'une
            // étape parallèle
            if (
                STServiceLocator
                    .getCorbeilleService()
                    .findDossierLink(context.getSession(), context.getCurrentDocument().getId())
                    .size() >
                1
            ) {
                builder.append(StringUtils.SPACE);
                builder.append(ResourceHelper.getString("dossier.consult.step.actual.parallele"));
            }
        }
        return builder.toString();
    }

    @Override
    public Date getLastStepDate(SpecificContext context, String id) {
        return SSServiceLocator.getSSFeuilleRouteService().getLastStepDate(context.getSession(), id);
    }
}
