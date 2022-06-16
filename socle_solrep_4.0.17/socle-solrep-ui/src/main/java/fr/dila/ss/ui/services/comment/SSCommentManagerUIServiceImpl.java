package fr.dila.ss.ui.services.comment;

import static fr.dila.ss.ui.enums.SSContextDataKey.ROUTE_STEP_IDS;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.core.util.CommentUtils;
import fr.dila.ss.ui.bean.fdr.NoteEtapeFormDTO;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.domain.comment.STComment;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;
import org.nuxeo.ecm.platform.comment.api.Comments;
import org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants;
import org.nuxeo.ecm.platform.comment.workflow.utils.FollowTransitionUnrestricted;

public class SSCommentManagerUIServiceImpl implements SSCommentManagerUIService {
    private static final STLogger LOGGER = STLogFactory.getLog(SSCommentManagerUIServiceImpl.class);
    private static final String EMPTY_COMMENT_ID_ERROR_MSG = "L'id du commentaire est vide";

    @Override
    public void addComment(SpecificContext context) {
        NoteEtapeFormDTO noteEtapeFormDTO = context.getFromContextData(SSContextDataKey.NOTE_ETAPE_FORM);

        addComment(context, context.getCurrentDocument(), noteEtapeFormDTO);
    }

    @Override
    public void addCommentRunningStep(SpecificContext context) {
        NoteEtapeFormDTO noteEtapeFormDTO = context.getFromContextData(SSContextDataKey.NOTE_ETAPE_FORM);
        String dossierLinkId = noteEtapeFormDTO.getDossierLinkId();
        CoreSession session = context.getSession();

        if (StringUtils.isBlank(dossierLinkId)) { // Si on a pas de dossierLinkId, on tente de le récupérer
            STDossierLink dossierLink = SSActionsServiceLocator
                .getSSCorbeilleActionService()
                .getCurrentDossierLink(context);
            if (dossierLink != null) {
                dossierLinkId = dossierLink.getId();
            }
        }
        if (StringUtils.isNotBlank(dossierLinkId)) {
            String routeStepId = getRouteStepId(session, dossierLinkId);
            addComment(context, routeStepId, noteEtapeFormDTO);
        } else { // Dans le cas où on arrive pas malgré tout à récupérer le dossierLinkId on ajoute la note d'étape sur toutes les étapes en cours
            STDossier dossier = context.getCurrentDocument().getAdapter(STDossier.class);
            List<DocumentModel> runningSteps = SSServiceLocator
                .getSSFeuilleRouteService()
                .getRunningSteps(session, dossier.getLastDocumentRoute());
            runningSteps.forEach(routeStep -> addComment(context, routeStep, noteEtapeFormDTO));
        }
    }

    @Override
    public void addCommentFromDossierLinks(SpecificContext context) {
        NoteEtapeFormDTO noteEtapeFormDTO = context.getFromContextData(SSContextDataKey.NOTE_ETAPE_FORM);
        List<String> dossierLinkIds = noteEtapeFormDTO.getDossierLinkIds();

        if (CollectionUtils.isEmpty(dossierLinkIds)) {
            throw new STValidationException("feedback.ss.comment.error.empty.dossiers.list");
        } else {
            CoreSession session = context.getSession();

            dossierLinkIds
                .stream()
                .map(id -> getRouteStepId(session, id))
                .distinct() // cas où l'étape de la fdr est identique entre plusieurs dossiers (allotissement)
                .forEach(routeStepId -> addComment(context, routeStepId, noteEtapeFormDTO));
        }
    }

    private static String getRouteStepId(CoreSession session, String dossierLinkId) {
        DocumentModel dossierLinkDoc = getDocumentModel(session, dossierLinkId);
        return dossierLinkDoc.getAdapter(STDossierLink.class).getRoutingTaskId();
    }

    @Override
    public void addSharedComment(SpecificContext context) {
        CoreSession session = context.getSession();

        try {
            List<DocumentModel> docsToComment = getDocsToComment(context);
            if (CollectionUtils.isEmpty(docsToComment)) {
                throw new NuxeoException(ResourceHelper.getString("feedback.ss.comment.error.empty.doclist"));
            }

            DocumentModel firstDm = docsToComment.remove(0);
            CommentableDocument commentableDoc = firstDm.getAdapter(CommentableDocument.class);

            String commentContent = context.getFromContextData(SSContextDataKey.COMMENT_CONTENT);
            DocumentModel newComment = CommentUtils.initComment(session, commentContent);

            DocumentModel createdComment = commentableDoc.addComment(newComment);

            // Rattachement aux autres commentableDocs
            docsToComment.forEach(docToComment -> setRouteStepLinkToComments(session, docToComment, createdComment));

            validateComment(session, createdComment);
        } catch (NuxeoException e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_COM_TEC, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_COM_TEC, e);
            throw new NuxeoException(e);
        }
    }

    @Override
    public void updateComment(SpecificContext context) {
        NoteEtapeFormDTO noteEtapeFormDTO = context.getFromContextData(SSContextDataKey.NOTE_ETAPE_FORM);

        String commentId = noteEtapeFormDTO.getCommentId();
        if (StringUtils.isBlank(commentId)) {
            throw new NuxeoException(EMPTY_COMMENT_ID_ERROR_MSG);
        }

        String commentContent = noteEtapeFormDTO.getCommentContent();
        if (StringUtils.isBlank(commentContent)) {
            throw new STValidationException("feedback.ss.comment.error.empty.content");
        }

        CoreSession session = context.getSession();
        try {
            DocumentModel comment = getDocumentModel(session, commentId);

            if (!session.exists(comment.getRef())) {
                throw new NuxeoException("Le document correspondant au commentaire n'existe pas : " + comment.getId());
            }
            setCommentData(context, comment, noteEtapeFormDTO);

            LOGGER.info(session, STLogEnumImpl.UPDATE_COM_TEC, comment);
            SSServiceLocator.getCommentManager().updateComment(session, commentId, Comments.newComment(comment));
        } catch (NuxeoException e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_UPDATE_COM_TEC, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_UPDATE_COM_TEC, e);
            throw new NuxeoException(e);
        }
    }

    protected void setCommentData(
        SpecificContext context,
        DocumentModel commentDoc,
        NoteEtapeFormDTO noteEtapeFormDTO
    ) {
        STComment comment = commentDoc.getAdapter(STComment.class);
        comment.setTexte(noteEtapeFormDTO.getCommentContent());
        comment.setModifiedDate(Calendar.getInstance());
        context.getSession().saveDocument(commentDoc);
    }

    private void addComment(SpecificContext context, String routeStepId, NoteEtapeFormDTO noteDto) {
        addComment(context, getDocumentModel(context.getSession(), routeStepId), noteDto);
    }

    private void addComment(SpecificContext context, DocumentModel docToComment, NoteEtapeFormDTO noteDto) {
        if (StringUtils.isBlank(noteDto.getCommentContent())) {
            throw new STValidationException("feedback.ss.comment.error.empty.content");
        }

        addComment(
            context,
            docToComment,
            CommentUtils.initComment(context.getSession(), noteDto.getCommentContent()),
            noteDto
        );
    }

    protected void addComment(
        SpecificContext context,
        DocumentModel docToComment,
        DocumentModel commentDoc,
        NoteEtapeFormDTO noteDto
    ) {
        DocumentModel newComment = buildNewComment(context, docToComment, commentDoc, noteDto);
        validateComment(context.getSession(), newComment);
    }

    protected static DocumentModel buildNewComment(
        SpecificContext context,
        DocumentModel docToComment,
        DocumentModel comment,
        NoteEtapeFormDTO noteDto
    ) {
        CommentableDocument commentableDoc = Optional
            .ofNullable(docToComment)
            .map(doc -> doc.getAdapter(CommentableDocument.class))
            .orElseThrow(() -> new NuxeoException(ResourceHelper.getString("feedback.comment.document.null")));

        return Optional
            .ofNullable(noteDto.getCommentParentId())
            .filter(StringUtils::isNotBlank)
            .map(id -> getDocumentModel(context.getSession(), id))
            .map(parentCommentDoc -> commentableDoc.addComment(parentCommentDoc, comment))
            .orElseGet(() -> commentableDoc.addComment(comment));
    }

    protected static DocumentModel getDocumentModel(CoreSession session, String id) {
        return session.getDocument(new IdRef(id));
    }

    private static void setRouteStepLinkToComments(
        CoreSession session,
        DocumentModel docToComment,
        DocumentModel newComment
    ) {
        List<String> commentIds = PropertyUtil.getStringListProperty(
            docToComment,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY
        );
        commentIds.add(newComment.getId());
        docToComment.setProperty(
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY,
            commentIds
        );
        docToComment.getAdapter(SSRouteStep.class).save(session);
    }

    protected static void validateComment(CoreSession session, DocumentModel comment) {
        if (CommentsConstants.COMMENT_LIFECYCLE.equals(comment.getLifeCyclePolicy())) {
            new FollowTransitionUnrestricted(session, comment.getRef(), CommentsConstants.TRANSITION_TO_PUBLISHED_STATE)
            .runUnrestricted();
        }
    }

    private static List<DocumentModel> getDocsToComment(SpecificContext context) {
        List<String> routeStepIds = context.getFromContextData(ROUTE_STEP_IDS);
        return routeStepIds
            .stream()
            .map(id -> getDocumentModel(context.getSession(), id))
            .filter(routeStepDoc -> !isRouteStepDone(routeStepDoc))
            .collect(Collectors.toList());
    }

    private static boolean isRouteStepDone(DocumentModel routeStepDoc) {
        return routeStepDoc.getAdapter(FeuilleRouteElement.class).isDone();
    }

    @Override
    public void deleteComment(SpecificContext context) {
        CoreSession session = context.getSession();

        String commentId = context.getFromContextData(SSContextDataKey.COMMENT_ID);
        if (StringUtils.isBlank(commentId)) {
            LOGGER.error(session, STLogEnumImpl.DEL_COMMENT_TEC, EMPTY_COMMENT_ID_ERROR_MSG);
            throw new NuxeoException(EMPTY_COMMENT_ID_ERROR_MSG);
        }

        try {
            DocumentModel comment = getDocumentModel(session, commentId);

            if (!session.exists(comment.getRef())) {
                throw new NuxeoException("Le document correspondant au commentaire n'existe pas : " + comment.getId());
            }

            // Pour chacune des étapes de premier niveau du dossier courant
            SSRouteStep routeStep = context.getCurrentDocument().getAdapter(SSRouteStep.class);
            String fdrId = routeStep.getFeuilleRoute(session).getDocument().getId();

            List<DocumentModel> firstLevelSteps = SSServiceLocator.getSSFeuilleRouteService().getSteps(session, fdrId);

            exploreListAndRemoveLinks(commentId, firstLevelSteps, session);

            LOGGER.info(session, STLogEnumImpl.DEL_COMMENT_TEC, comment);
            SSServiceLocator.getCommentManager().deleteComment(session, commentId);
        } catch (NuxeoException e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_DEL_COM_TEC, e);
            throw e;
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_DEL_COM_TEC, e);
            throw new NuxeoException(e);
        }
    }

    private static void exploreListAndRemoveLinks(String commentId, List<DocumentModel> stepList, CoreSession session) {
        for (DocumentModel stepDocModel : stepList) {
            if (SSConstant.STEP_FOLDER_DOCUMENT_TYPE.equals(stepDocModel.getType())) {
                // Etape parallèle
                final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
                final DocumentModelList children = documentRoutingService.getOrderedRouteElement(
                    stepDocModel.getId(),
                    session
                );
                exploreListAndRemoveLinks(commentId, children, session);
            } else {
                // Etape non imbriquée, cas classique
                deleteCommentLink(commentId, stepDocModel, session);
            }
        }
    }

    private static void deleteCommentLink(String commentId, DocumentModel stepDocModel, CoreSession session) {
        List<String> commentDocIds = PropertyUtil.getStringListProperty(
            stepDocModel,
            STSchemaConstant.ROUTING_TASK_SCHEMA,
            STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY
        );

        if (commentDocIds.contains(commentId)) {
            commentDocIds.remove(commentId);
            stepDocModel.setProperty(
                STSchemaConstant.ROUTING_TASK_SCHEMA,
                STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY,
                commentDocIds
            );
            stepDocModel.getAdapter(SSRouteStep.class).save(session);
            session.save();
        }
    }

    @Override
    public boolean isVisible(SpecificContext context) {
        DocumentModel commentModel = context.getFromContextData(SSContextDataKey.COMMENT_DOC);
        STComment comment = commentModel.getAdapter(STComment.class);
        return comment != null;
    }

    private boolean isInAuthorPoste(STComment comment, NuxeoPrincipal principal) {
        return (
            comment != null &&
            STServiceLocator.getSTPostesService().haveAnyCommonPoste(comment.getAuthor(), principal.getName())
        );
    }

    @Override
    public boolean isInAuthorPoste(SpecificContext context) {
        DocumentModel commentDoc = context.getFromContextData(SSContextDataKey.COMMENT_DOC);
        return (
            commentDoc != null &&
            isInAuthorPoste(commentDoc.getAdapter(STComment.class), context.getSession().getPrincipal())
        );
    }

    @Override
    public boolean isAuthor(SpecificContext context) {
        DocumentModel commentDoc = context.getFromContextData(SSContextDataKey.COMMENT_DOC);
        return (
            commentDoc != null &&
            StringUtils.equals(
                commentDoc.getAdapter(STComment.class).getAuthor(),
                context.getSession().getPrincipal().getName()
            )
        );
    }
}
