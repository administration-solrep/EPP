package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.actionable.ActionableObject;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;

/**
 *
 *
 */
public class SimpleActionableObject implements ActionableObject {
    protected String id;

    public SimpleActionableObject(String id) {
        this.id = id;
    }

    @Override
    public String getValidateOperationChainId() {
        return "simpleValidate";
    }

    @Override
    public String getRefuseOperationChainId() {
        return "simpleRefuse";
    }

    @Override
    public FeuilleRouteStep getDocumentRouteStep(CoreSession session) {
        return session.getDocument(new IdRef(id)).getAdapter(FeuilleRouteStep.class);
    }

    @Override
    public DocumentModelList getAttachedDocuments(CoreSession session) {
        return new DocumentModelListImpl();
    }
}
