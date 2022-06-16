package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRoutePersister;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.helper.TreeHelper;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteInstanceSchemaUtil;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.FacetFilter;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.schema.FacetNames;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;

/**
 * The default persister. It persists the {@link FeuilleRoute} in a tree
 * hierarchy ressembling the current date. New model created from instance are
 * stored in the personal workspace of the user.
 *
 *
 */
public class FeuilleRouteTreePersister implements FeuilleRoutePersister {
    private static final Log log = LogFactory.getLog(FeuilleRouteTreePersister.class);

    @Override
    public DocumentModel getParentFolderForDocumentRouteInstance(DocumentModel document, CoreSession session) {
        return TreeHelper.getOrCreateDateTreeFolder(
            session,
            getOrCreateRootOfDocumentRouteInstanceStructure(session),
            new Date(),
            "Folder"
        );
    }

    @Override
    public DocumentModel createDocumentRouteInstanceFromDocumentRouteModel(DocumentModel model, CoreSession session) {
        DocumentModel parent = getParentFolderForDocumentRouteInstance(model, session);
        DocumentModel result = session.copy(model.getRef(), parent.getRef(), null);
        // -- SPL remove copied ACLS
        // la mise a jour de nuxeo-core-storage : entraine la copie des acls sur les documents
        // copies
        ACP acp = result.getACP();
        for (ACL acl : acp.getACLs()) {
            acp.removeACL(acl.getName());
        }
        result.setACP(acp, true);
        // -- SPL
        // using the ref, the value of the attached document might not been
        // saved on the model

        FeuilleRouteInstanceSchemaUtil.setAttachedDocuments(
            result,
            FeuilleRouteInstanceSchemaUtil.getAttachedDocuments(model)
        );
        return session.saveDocument(result);
    }

    @Override
    public DocumentModel saveDocumentRouteInstanceAsNewModel(
        DocumentModel routeInstance,
        DocumentModel parentFolder,
        CoreSession session
    ) {
        return session.copy(routeInstance.getRef(), parentFolder.getRef(), null);
    }

    @Override
    public DocumentModel getOrCreateRootOfDocumentRouteInstanceStructure(CoreSession session) {
        DocumentModel root;
        root = getDocumentRouteInstancesStructure(session);
        if (root == null) {
            root = createDocumentRouteInstancesStructure(session);
        }
        return root;
    }

    protected DocumentModel createDocumentRouteInstancesStructure(CoreSession session) {
        FacetFilter facetFilter = new FacetFilter(FacetNames.HIDDEN_IN_NAVIGATION, false);
        DocumentModel defaultDomain = session
            .getChildren(session.getRootDocument().getRef(), null, null, facetFilter, null)
            .get(0);
        DocumentModel root = session.createDocumentModel(
            defaultDomain.getPathAsString(),
            FeuilleRouteConstant.FEUILLE_ROUTE_INSTANCES_ROOT_ID,
            FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_INSTANCES_ROOT
        );
        DublincorePropertyUtil.setTitle(root, FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_INSTANCES_ROOT);
        root = session.createDocument(root);
        ACP acp = session.getACP(root.getRef());
        ACL acl = acp.getOrCreateACL(ACL.LOCAL_ACL);
        acl.addAll(getACEs());
        session.setACP(root.getRef(), acp, true);
        return root;
    }

    /**
     * @return
     */
    protected List<ACE> getACEs() {
        List<ACE> aces = new ArrayList<ACE>();
        for (String group : getUserManager().getAdministratorsGroups()) {
            aces.add(new ACE(group, SecurityConstants.EVERYTHING, true));
        }
        aces.add(new ACE(FeuilleRouteConstant.ROUTE_MANAGERS_GROUP_NAME, SecurityConstants.READ_WRITE, true));
        aces.add(new ACE(SecurityConstants.EVERYONE, SecurityConstants.EVERYTHING, false));
        return aces;
    }

    protected UserManager getUserManager() {
        return ServiceUtil.getRequiredService(UserManager.class);
    }

    protected DocumentModel getDocumentRouteInstancesStructure(CoreSession session) {
        DocumentModelList res = session.query(
            String.format("SELECT * from %s", FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_INSTANCES_ROOT)
        );
        if (res == null || res.isEmpty()) {
            return null;
        }
        if (res.size() > 1) {
            if (log.isWarnEnabled()) {
                log.warn("More han one DocumentRouteInstanceRoot found:");
                for (DocumentModel model : res) {
                    log.warn(" - " + model.getName() + ", " + model.getPathAsString());
                }
            }
        }
        return res.get(0);
    }

    @Override
    public DocumentModel getParentFolderForNewModel(CoreSession session, DocumentModel instance) {
        UserWorkspaceService service = ServiceUtil.getRequiredService(UserWorkspaceService.class);
        return service.getCurrentUserPersonalWorkspace(session);
    }

    @Override
    public String getNewModelName(DocumentModel instance) {
        return "(COPY) " + DublincorePropertyUtil.getTitle(instance);
    }
}
