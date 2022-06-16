package fr.dila.ss.ui.services.actions.impl;

import static fr.dila.ss.ui.services.actions.SSActionsServiceLocator.getDocumentRoutingActionService;
import static fr.dila.st.ui.services.actions.STActionsServiceLocator.getSTCorbeilleActionService;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.ui.bean.fdr.NoteEtapeFormDTO;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.actions.RouteStepNoteActionService;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.domain.comment.STComment;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.comment.api.CommentableDocument;

/**
 * WebBean permettant de gérer les notes d'étapes.
 *
 * @author jtremeaux
 */
public class RouteStepNoteActionServiceImpl implements RouteStepNoteActionService {

    @Override
    public List<DocumentModel> getCommentList(SpecificContext context) {
        DocumentModel routeStepDoc = context.getFromContextData(SSContextDataKey.STEP_DOC);
        DocumentModel doc = context.getFromContextData(SSContextDataKey.COMMENT_DOC);
        CommentableDocument commentableDoc = routeStepDoc.getAdapter(CommentableDocument.class);
        List<DocumentModel> comments = new ArrayList<>();
        if (commentableDoc != null) {
            if (SSConstant.ROUTE_STEP_DOCUMENT_TYPE.equals(doc.getType())) {
                comments = commentableDoc.getComments();
            } else if (STConstant.COMMENT_DOCUMENT_TYPE.equals(doc.getType())) {
                comments = commentableDoc.getComments(doc);
            }
        }
        return comments;
    }

    @Override
    public NoteEtapeFormDTO getNoteEtape(SpecificContext context) {
        DocumentModel commentDoc = context.getCurrentDocument();
        STComment comment = commentDoc.getAdapter(STComment.class);
        NoteEtapeFormDTO commentDto = new NoteEtapeFormDTO();
        commentDto.setCommentContent(comment.getTexte());
        return commentDto;
    }

    @Override
    public boolean isViewableNote(DocumentModel routeStepDoc) {
        // Vérifie l'état de l'étape
        final String currentLifeCycleState = routeStepDoc.getCurrentLifeCycleState();
        return (
            "init".equals(currentLifeCycleState) ||
            currentLifeCycleState.equals(FeuilleRouteElement.ElementLifeCycleState.running.name()) ||
            currentLifeCycleState.equals(FeuilleRouteElement.ElementLifeCycleState.ready.name())
        );
    }

    @Override
    public boolean isEditableNote(
        SpecificContext context,
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel routeStepDoc,
        DocumentModel currentDoc
    ) {
        // Vérifie l'état de l'étape
        if (!isViewableNote(routeStepDoc)) {
            return false;
        }

        // Vérifie si le DossierLink est chargé
        if (!getSTCorbeilleActionService().isDossierLoadedInCorbeille(context)) {
            return false;
        }

        // Vérifie si le dossier est verrouillé
        final DocumentModel currentDossierDoc = getCurrentDossierDoc(dossierDoc, currentDoc);
        return LockUtils.isLockedByCurrentUser(session, currentDossierDoc.getRef());
    }

    @Override
    public DocumentModel getCurrentDossierDoc(DocumentModel dossierDoc, DocumentModel currentDoc) {
        // Si on est sur la liste des étapes, retourne le document courant
        if (currentDoc.hasFacet(SSConstant.ROUTABLE_FACET)) {
            return currentDoc;
        }

        // Sinon on est sur l'écran de détail, retourne le dossier stocké dans
        // ce WebBean
        return dossierDoc;
    }

    @Override
    public int getCommentNumber(DocumentModel routeStepDoc) {
        CommentableDocument routeStep = routeStepDoc.getAdapter(CommentableDocument.class);

        int total = 0;

        for (DocumentModel comment : routeStep.getComments()) {
            total += 1 + getSubCommentsNumber(routeStep, comment);
        }

        return total;
    }

    private int getSubCommentsNumber(CommentableDocument routeStep, DocumentModel parentCommentDoc) {
        List<DocumentModel> children = routeStep.getComments(parentCommentDoc);
        if (children.isEmpty()) {
            return 0;
        }
        int total = 0;

        for (DocumentModel child : children) {
            total += 1 + getSubCommentsNumber(routeStep, child);
        }

        return total;
    }

    @Override
    public void initCreateSharedNote(
        SpecificContext context,
        CoreSession session,
        DocumentModel currentDocument,
        List<DocumentModel> currentDocumentSelection
    ) {
        ArrayList<DocumentModel> checkedRouteSteps = new ArrayList<>(currentDocumentSelection);

        ArrayList<DocumentModel> noteRouteSteps = new ArrayList<>();

        // Vérification des étapes: si on ne peut pas faire de note partagée
        // dessus, alors elle n'est pas prise en compte
        // et on avertit l'utilisateur
        String message = null;
        for (DocumentModel docModel : checkedRouteSteps) {
            if (!getDocumentRoutingActionService().isEditableRouteElement(context)) {
                message = "feedback.ss.sharedNote.cannot.create.step";
            } else {
                noteRouteSteps.add(docModel);
            }
        }

        if (CollectionUtils.isNotEmpty(currentDocumentSelection)) {
            currentDocumentSelection.clear();
        }

        // Si aucune étape ne permet la création d'une note d'étape partagée, on
        // ne fait rien mais on prévient.
        if (noteRouteSteps.isEmpty() && message != null) {
            context.getMessageQueue().addWarnToQueue("feedback.ss.sharedNote.cannot.create");
            return;
        }

        if (message != null) {
            context.getMessageQueue().addWarnToQueue(message);
        }
    }
}
