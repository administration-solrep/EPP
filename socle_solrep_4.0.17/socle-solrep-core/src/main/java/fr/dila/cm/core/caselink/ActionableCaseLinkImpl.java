package fr.dila.cm.core.caselink;

import fr.dila.cm.caselink.ActionableCaseLink;
import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.core.caselink.STDossierLinkImpl;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.helper.ActionableValidator;
//import static fr.dila.cm.caselink.CaseLinkConstants.REFUSAL_OPERATION_CHAIN_ID;
//import static fr.dila.cm.caselink.CaseLinkConstants.TASK_TYPE_FIELD;
//import static fr.dila.cm.caselink.CaseLinkConstants.VALIDATION_OPERATION_CHAIN_ID;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;

/**
 * @author <a href="mailto:mcedica@nuxeo.com">Mariana Cedica</a>
 */
public class ActionableCaseLinkImpl extends STDossierLinkImpl implements ActionableCaseLink {
    public static final String VALIDATION_OPERATION_CHAIN_ID = "acslk:validationOperationChainId";

    public static final String REFUSAL_OPERATION_CHAIN_ID = "acslk:refusalOperationChainId";

    public static final String AUTOMATIC_VALIDATION_FIELD = "acslk:automaticValidation";

    public ActionableCaseLinkImpl(DocumentModel doc) {
        super(doc);
    }

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void validate(CoreSession session) {
        setDone(session);
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                Map<String, Serializable> map = new HashMap<String, Serializable>();
                map.put(CaseConstants.OPERATION_CASE_LINK_KEY, ActionableCaseLinkImpl.this);
                ActionableValidator validator = new ActionableValidator(ActionableCaseLinkImpl.this, session, map);
                validator.validate();
            }
        }
        .runUnrestricted();
    }

    @Override
    public void refuse(CoreSession session) {
        setDone(session);
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                Map<String, Serializable> map = new HashMap<String, Serializable>();
                map.put(CaseConstants.OPERATION_CASE_LINK_KEY, ActionableCaseLinkImpl.this);
                ActionableValidator validator = new ActionableValidator(ActionableCaseLinkImpl.this, session, map);
                validator.refuse();
            }
        }
        .runUnrestricted();
    }

    @Override
    public String getRefuseOperationChainId() {
        return getPropertyValue(REFUSAL_OPERATION_CHAIN_ID);
    }

    @Override
    public String getValidateOperationChainId() {
        return getPropertyValue(VALIDATION_OPERATION_CHAIN_ID);
    }

    @Override
    public FeuilleRouteStep getDocumentRouteStep(CoreSession session) {
        String stepId = getPropertyValue(CaseLinkConstants.STEP_DOCUMENT_ID_FIELD);
        return session.getDocument(new IdRef(stepId)).getAdapter(FeuilleRouteStep.class);
    }

    @Override
    public DocumentModelList getAttachedDocuments(CoreSession session) {
        STDossier kase = getDossier(session, STDossier.class);
        DocumentModelList result = new DocumentModelListImpl();
        result.add(kase.getDocument());
        return result;
    }

    @Override
    public void setRefuseOperationChainId(String refuseChainId) {
        document.setPropertyValue(REFUSAL_OPERATION_CHAIN_ID, refuseChainId);
    }

    @Override
    public void setValidateOperationChainId(String validateChainId) {
        document.setPropertyValue(VALIDATION_OPERATION_CHAIN_ID, validateChainId);
    }

    @Override
    public void setStepId(String id) {
        document.setPropertyValue(CaseLinkConstants.STEP_DOCUMENT_ID_FIELD, id);
    }

    @Override
    public String getStepId() {
        return getPropertyValue(CaseLinkConstants.STEP_DOCUMENT_ID_FIELD);
    }

    @Override
    public boolean isTodo() {
        return document.getCurrentLifeCycleState().equals(STDossierLink.CaseLinkState.todo.name());
    }

    @Override
    public boolean isDone() {
        return document.getCurrentLifeCycleState().equals(STDossierLink.CaseLinkState.done.name());
    }

    @Override
    public void setDone(CoreSession session) {
        session.followTransition(document.getRef(), STDossierLink.CaseLinkTransistion.toDone.name());
    }

    @Override
    protected String getDossierLinkSchema() {
        throw new UnsupportedOperationException("should not be called");
    }
}
