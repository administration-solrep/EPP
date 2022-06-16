package fr.sword.idl.naiad.nuxeo.feuilleroute.core.adapter;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteEvent;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.EventFirer;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
public class FeuilleRouteElementImpl implements FeuilleRouteStep {
    private static final long serialVersionUID = 1L;

    protected DocumentModel document;

    protected ElementRunner runner;

    public FeuilleRouteElementImpl(DocumentModel doc, ElementRunner runner) {
        this.document = doc;
        this.runner = runner;
    }

    @Override
    public DocumentModelList getAttachedDocuments(CoreSession session) {
        List<String> docIds = getFeuilleRoute(session).getAttachedDocuments();
        DocumentRef[] refs = docIds.stream().map(IdRef::new).filter(session::exists).toArray(DocumentRef[]::new);
        return session.getDocuments(refs);
    }

    @Override
    public void run(CoreSession session) {
        runner.run(session, this);
    }

    @Override
    public SSFeuilleRoute getFeuilleRoute(CoreSession session) {
        DocumentModel parent = document;
        while (true) {
            if (parent.hasFacet(FeuilleRouteConstant.FACET_FEUILLE_ROUTE)) {
                break;
            }
            parent = session.getParentDocument(parent.getRef());
        }
        return parent.getAdapter(SSFeuilleRoute.class);
    }

    @Override
    public DocumentModel getDocument() {
        return document;
    }

    @Override
    public String getName() {
        return DublincorePropertyUtil.getTitle(document);
    }

    @Override
    public boolean isValidated() {
        return checkLifeCycleState(ElementLifeCycleState.validated);
    }

    @Override
    public boolean isReady() {
        return checkLifeCycleState(ElementLifeCycleState.ready);
    }

    @Override
    public boolean isDone() {
        return checkLifeCycleState(ElementLifeCycleState.done);
    }

    @Override
    public boolean isDeleted() {
        return checkLifeCycleState(ElementLifeCycleState.deleted);
    }

    protected boolean checkLifeCycleState(ElementLifeCycleState state) {
        return document.getCurrentLifeCycleState().equalsIgnoreCase(state.name());
    }

    @Override
    public String getDescription() {
        return DublincorePropertyUtil.getDescription(document);
    }

    @Override
    public void setDescription(String description) {
        DublincorePropertyUtil.setDescription(document, description);
    }

    @Override
    public boolean isRunning() {
        return checkLifeCycleState(ElementLifeCycleState.running);
    }

    @Override
    public boolean isCanceled() {
        return checkLifeCycleState(ElementLifeCycleState.canceled);
    }

    @Override
    public boolean isDraft() {
        return checkLifeCycleState(ElementLifeCycleState.draft);
    }

    @Override
    public void setRunning(CoreSession session) {
        followTransition(ElementLifeCycleTransistion.toRunning, session, false);
    }

    @Override
    public void followTransition(ElementLifeCycleTransistion transition, CoreSession session, boolean recursive) {
        session.followTransition(document, transition.name());
        document = session.getDocument(document.getRef());

        if (Framework.isTestModeSet()) {
            Framework.getService(EventService.class).waitForAsyncCompletion();
        }
        if (recursive) {
            DocumentModelList children = session.getChildren(document.getRef());
            for (DocumentModel child : children) {
                FeuilleRouteElement element = child.getAdapter(FeuilleRouteElement.class);
                element.followTransition(transition, session, recursive);
            }
        }
    }

    @Override
    public void save(CoreSession session) {
        document = session.saveDocument(document);
    }

    @Override
    public void setDone(CoreSession session) {
        followTransition(ElementLifeCycleTransistion.toDone, session, false);
        EventFirer.fireEvent(session, getDocument(), null, FeuilleRouteEvent.afterStepRunning.name());
    }

    @Override
    public void setValidated(CoreSession session) {
        followTransition(ElementLifeCycleTransistion.toValidated, session, true);
    }

    @Override
    public void setReady(CoreSession session) {
        followTransition(ElementLifeCycleTransistion.toReady, session, true);
    }

    @Override
    public void validate(CoreSession session) {
        setValidated(session);
        setReadOnly(session);
    }

    @Override
    public void setReadOnly(CoreSession session) {
        SetDocumentOnReadOnlyUnrestrictedSessionRunner readOnlySetter = new SetDocumentOnReadOnlyUnrestrictedSessionRunner(
            session,
            document.getRef()
        );
        readOnlySetter.runUnrestricted();
        document.refresh();
    }

    protected class SetDocumentOnReadOnlyUnrestrictedSessionRunner extends UnrestrictedSessionRunner {

        public SetDocumentOnReadOnlyUnrestrictedSessionRunner(CoreSession session, DocumentRef ref) {
            super(session);
            this.ref = ref;
        }

        private final DocumentRef ref;

        @Override
        public void run() {
            DocumentModel doc = session.getDocument(ref);
            ACP acp = new ACPImpl();
            // add new ACL to set READ permission to everyone
            ACL routingACL = acp.getOrCreateACL(FeuilleRouteConstant.ROUTING_ACL);
            routingACL.add(new ACE(SecurityConstants.EVERYONE, SecurityConstants.READ, true));

            // block rights inheritance
            ACL inheritedACL = acp.getOrCreateACL(ACL.INHERITED_ACL);
            inheritedACL.add(new ACE(SecurityConstants.EVERYONE, SecurityConstants.EVERYTHING, false));

            ACL localACL = acp.getOrCreateACL(ACL.LOCAL_ACL);
            localACL.add(new ACE(SecurityConstants.EVERYONE, SecurityConstants.EVERYTHING, false));

            doc.setACP(acp, true);
            session.saveDocument(doc);
        }
    }

    @Override
    public boolean canValidateStep(CoreSession session) {
        return hasPermissionOnDocument(session, SecurityConstants.WRITE_LIFE_CYCLE);
    }

    protected boolean hasPermissionOnDocument(CoreSession session, String permission) {
        return session.hasPermission(document.getRef(), permission);
    }

    @Override
    public void setCanValidateStep(CoreSession session, String userOrGroup) {
        setPermissionOnDocument(session, userOrGroup, SecurityConstants.WRITE_LIFE_CYCLE);
    }

    protected void setPermissionOnDocument(CoreSession session, String userOrGroup, String permission) {
        ACP acp = new ACPImpl();
        ACL routingACL = acp.getOrCreateACL(FeuilleRouteConstant.ROUTING_ACL);
        routingACL.add(new ACE(userOrGroup, permission, true));
        document.setACP(acp, true);
        session.saveDocument(document);
    }

    @Override
    public boolean canUpdateStep(CoreSession session) {
        return hasPermissionOnDocument(session, SecurityConstants.WRITE_PROPERTIES);
    }

    @Override
    public void setCanUpdateStep(CoreSession session, String userOrGroup) {
        setPermissionOnDocument(session, userOrGroup, SecurityConstants.WRITE_PROPERTIES);
    }

    @Override
    public boolean canDeleteStep(CoreSession session) {
        return hasPermissionOnDocument(session, SecurityConstants.REMOVE);
    }

    @Override
    public void setCanDeleteStep(CoreSession session, String userOrGroup) {
        setPermissionOnDocument(session, userOrGroup, SecurityConstants.REMOVE);
    }

    @Override
    public void backToReady(CoreSession session) {
        EventFirer.fireEvent(session, getDocument(), null, FeuilleRouteEvent.beforeStepBackToReady.name());
        followTransition(ElementLifeCycleTransistion.backToReady, session, false);
        EventFirer.fireEvent(session, getDocument(), null, FeuilleRouteEvent.afterStepBackToReady.name());
    }

    @Override
    public FeuilleRouteStep undo(CoreSession session) {
        runner.undo(session, this);
        document = session.getDocument(document.getRef());
        return this;
    }

    @Override
    public boolean canUndoStep(CoreSession session) {
        GetIsParentRunningUnrestricted runner = new GetIsParentRunningUnrestricted(session);
        runner.runUnrestricted();
        return runner.isRunning();
    }

    protected class GetIsParentRunningUnrestricted extends UnrestrictedSessionRunner {

        public GetIsParentRunningUnrestricted(CoreSession session) {
            super(session);
        }

        protected boolean isRunning;

        @Override
        public void run() {
            DocumentModel parent = session.getDocument(document.getParentRef());
            FeuilleRouteElement parentElement = parent.getAdapter(FeuilleRouteElement.class);
            isRunning = parentElement.isRunning();
        }

        public boolean isRunning() {
            return isRunning;
        }
    }

    @Override
    public void cancel(CoreSession session) {
        runner.cancel(session, this);
    }

    @Override
    public void setCanceled(CoreSession session) {
        followTransition(ElementLifeCycleTransistion.toCanceled, session, false);
    }

    @Override
    public boolean isModifiable() {
        return (isDraft() || isReady() || isRunning());
    }
}
