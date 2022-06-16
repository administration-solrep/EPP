package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRoutePersister;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.helper.CreateNewRouteInstanceUnrestricted;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.runtime.api.Framework;

/**
 *
 *
 */
public class FeuilleRouteServiceImpl implements FeuilleRouteService {
    private static final String AVAILABLE_ROUTES_QUERY = String.format(
        "Select * from %s",
        FeuilleRouteConstant.TYPE_FEUILLE_ROUTE
    );

    private static final String ORDERED_CHILDREN_QUERY =
        "SELECT * FROM Document WHERE" +
        " ecm:parentId = '%s' AND ecm:isCheckedInVersion  = 0 AND " +
        "ecm:mixinType != 'HiddenInNavigation' AND " +
        "ecm:isTrashed = 0 ORDER BY ecm:pos";

    /// param <states> <attachedDocId>
    private static final String RELATED_ROUTE_QUERY_FMT =
        "SELECT * FROM " +
        FeuilleRouteConstant.TYPE_FEUILLE_ROUTE +
        " WHERE %s " +
        FeuilleRouteConstant.XPATH_FROUT_INSTANCE_ATTACHDOCIDS +
        " IN ('%s')";

    private FeuilleRoutePersister persister;

    public FeuilleRouteServiceImpl() {
        // do nothing
    }

    @Override
    public FeuilleRoute createNewInstance(
        FeuilleRoute model,
        List<String> docIds,
        CoreSession session,
        boolean startInstance
    ) {
        return CreateNewRouteInstanceUnrestricted.create(session, model, docIds, startInstance, persister);
    }

    @Override
    public FeuilleRoute createNewInstance(
        FeuilleRoute model,
        String documentId,
        CoreSession session,
        boolean startInstance
    ) {
        return createNewInstance(model, Collections.singletonList(documentId), session, startInstance);
    }

    @Override
    public FeuilleRoute createNewInstance(FeuilleRoute model, List<String> documentIds, CoreSession session) {
        return createNewInstance(model, documentIds, session, true);
    }

    @Override
    public FeuilleRoute createNewInstance(FeuilleRoute model, String documentId, CoreSession session) {
        return createNewInstance(model, Collections.singletonList(documentId), session, true);
    }

    @Override
    public List<FeuilleRoute> getAvailableDocumentRouteModel(CoreSession session) {
        DocumentModelList list = session.query(AVAILABLE_ROUTES_QUERY);
        List<FeuilleRoute> routes = new ArrayList<FeuilleRoute>();
        for (DocumentModel model : list) {
            routes.add(model.getAdapter(FeuilleRoute.class));
        }
        return routes;
    }

    @Override
    public FeuilleRoute validateRouteModel(final FeuilleRoute routeModel, CoreSession userSession) {
        checkLockedByCurrentUser(routeModel, userSession);

        new UnrestrictedSessionRunner(userSession) {

            @Override
            public void run() {
                FeuilleRoute route = session
                    .getDocument(routeModel.getDocument().getRef())
                    .getAdapter(FeuilleRoute.class);
                route.validate(session);
            }
        }
        .runUnrestricted();
        return userSession.getDocument(routeModel.getDocument().getRef()).getAdapter(FeuilleRoute.class);
    }

    public List<FeuilleRoute> getDocumentRoutesForAttachedDocument(CoreSession session, String attachedDocId) {
        List<FeuilleRouteElement.ElementLifeCycleState> states = new ArrayList<FeuilleRouteElement.ElementLifeCycleState>();
        states.add(FeuilleRouteElement.ElementLifeCycleState.ready);
        states.add(FeuilleRouteElement.ElementLifeCycleState.running);
        return getDocumentRoutesForAttachedDocument(session, attachedDocId, states);
    }

    @Override
    public List<FeuilleRoute> getDocumentRoutesForAttachedDocument(
        CoreSession session,
        String attachedDocId,
        List<FeuilleRouteElement.ElementLifeCycleState> states
    ) {
        DocumentModelList list = null;
        StringBuilder statesString = new StringBuilder();
        if (states != null && !states.isEmpty()) {
            statesString.append(" ").append(CommonSchemaConstant.XPATH_ECM_LIFECYCLE).append(" IN (");
            for (FeuilleRouteElement.ElementLifeCycleState state : states) {
                statesString.append("'" + state.name() + "',");
            }
            statesString.deleteCharAt(statesString.length() - 1);
            statesString.append(") AND");
        }
        String relatedRoutesQuery = String.format(RELATED_ROUTE_QUERY_FMT, statesString.toString(), attachedDocId);
        list = session.query(relatedRoutesQuery);
        List<FeuilleRoute> routes = new ArrayList<>();
        for (DocumentModel model : list) {
            routes.add(model.getAdapter(FeuilleRoute.class));
        }
        return routes;
    }

    //	@Override
    //	public boolean canUserCreateRoute(NuxeoPrincipal currentUser) {
    //		return currentUser.getGroups().contains(FeuilleRouteConstant.ROUTE_MANAGERS_GROUP_NAME);
    //	}
    //
    //	@Override
    //	public boolean canUserModifyRoute(NuxeoPrincipal currentUser) {
    //		return currentUser.getGroups().contains(FeuilleRouteConstant.ROUTE_MANAGERS_GROUP_NAME);
    //	}
    //
    //	@Override
    //	public boolean canUserValidateRoute(NuxeoPrincipal currentUser) {
    //		return currentUser.getGroups().contains(FeuilleRouteConstant.ROUTE_MANAGERS_GROUP_NAME);
    //	}

    @Override
    public void addRouteElementToRoute(
        DocumentRef parentDocumentRef,
        int idx,
        FeuilleRouteElement routeElement,
        CoreSession session
    ) {
        FeuilleRoute route = getParentRouteModel(parentDocumentRef, session);
        checkLockedByCurrentUser(route, session);
        DocumentModelList children = session.query(
            String.format(ORDERED_CHILDREN_QUERY, session.getDocument(parentDocumentRef).getId())
        );
        DocumentModel sourceDoc;
        try {
            sourceDoc = children.get(idx);
            addRouteElementToRoute(parentDocumentRef, sourceDoc.getName(), routeElement, session);
        } catch (IndexOutOfBoundsException e) {
            addRouteElementToRoute(parentDocumentRef, null, routeElement, session);
        }
    }

    @Override
    public void addRouteElementToRoute(
        DocumentRef parentDocumentRef,
        String sourceName,
        FeuilleRouteElement routeElement,
        CoreSession session
    ) {
        FeuilleRoute parentRoute = getParentRouteModel(parentDocumentRef, session);
        checkLockedByCurrentUser(parentRoute, session);
        PathSegmentService pss = Framework.getService(PathSegmentService.class);
        DocumentModel docRouteElement = routeElement.getDocument();
        DocumentModel parentDocument = session.getDocument(parentDocumentRef);
        docRouteElement.setPathInfo(parentDocument.getPathAsString(), pss.generatePathSegment(docRouteElement));
        String lifecycleState = parentDocument
                .getCurrentLifeCycleState()
                .equals(FeuilleRouteElement.ElementLifeCycleState.draft.name())
            ? FeuilleRouteElement.ElementLifeCycleState.draft.name()
            : FeuilleRouteElement.ElementLifeCycleState.ready.name();
        docRouteElement.putContextData(LifeCycleConstants.INITIAL_LIFECYCLE_STATE_OPTION_NAME, lifecycleState);
        docRouteElement = session.createDocument(docRouteElement);
        session.orderBefore(parentDocumentRef, docRouteElement.getName(), sourceName);
        session.save(); // the new document will be queried later on
    }

    @Override
    public void removeRouteElement(FeuilleRouteElement routeElement, CoreSession session) {
        FeuilleRoute parentRoute = routeElement.getFeuilleRoute(session);
        checkLockedByCurrentUser(parentRoute, session);
        session.removeDocument(routeElement.getDocument().getRef());
        session.save(); // the document will be queried later on
    }

    @Override
    public DocumentModelList getOrderedRouteElement(String routeElementId, CoreSession session) {
        String query = String.format(ORDERED_CHILDREN_QUERY, routeElementId);
        DocumentModelList orderedChildren = session.query(query);
        return orderedChildren;
    }

    @Override
    public void lockDocumentRoute(FeuilleRoute routeModel, CoreSession session) {
        final DocumentRef docRef = routeModel.getDocument().getRef();
        if (LockUtils.isLockedByAnotherUser(session, docRef)) {
            throw new FeuilleRouteAlreadyLockedException();
        }
        LockUtils.lockIfNeeded(session, docRef);
    }

    @Override
    public void unlockDocumentRoute(FeuilleRoute routeModel, CoreSession session) {
        final DocumentRef docRef = routeModel.getDocument().getRef();
        if (!LockUtils.isLockedByCurrentUser(session, docRef)) {
            throw new FeuilleRouteNotLockedException();
        }
        LockUtils.unlockDocument(session, docRef);
    }

    @Override
    public void updateRouteElement(FeuilleRouteElement routeElement, CoreSession session) {
        checkLockedByCurrentUser(routeElement.getFeuilleRoute(session), session);
        routeElement.save(session);
    }

    private FeuilleRoute getParentRouteModel(DocumentRef documentRef, CoreSession session) {
        DocumentModel parentDoc = session.getDocument(documentRef);
        if (parentDoc.hasFacet(FeuilleRouteConstant.FACET_FEUILLE_ROUTE)) {
            return parentDoc.getAdapter(FeuilleRoute.class);
        }
        FeuilleRouteElement rElement = parentDoc.getAdapter(FeuilleRouteElement.class);
        return rElement.getFeuilleRoute(session);
    }

    @Override
    public FeuilleRoute saveRouteAsNewModel(FeuilleRoute instance, CoreSession session) {
        DocumentModel instanceModel = instance.getDocument();
        DocumentModel parent = persister.getParentFolderForNewModel(session, instanceModel);
        String newName = persister.getNewModelName(instanceModel);
        DocumentModel newmodel = session.copy(instanceModel.getRef(), parent.getRef(), newName);
        newmodel.setPropertyValue("dc:title", newName);
        FeuilleRoute newRoute = newmodel.getAdapter(FeuilleRoute.class);
        if (!newRoute.isDraft()) {
            newRoute.followTransition(FeuilleRouteElement.ElementLifeCycleTransistion.toDraft, session, false);
        }
        newRoute.setAttachedDocuments(new ArrayList<String>());
        newRoute.save(session);
        return newRoute;
    }

    public FeuilleRoutePersister getPersister() {
        return persister;
    }

    public void setPersister(FeuilleRoutePersister persister) {
        this.persister = persister;
    }

    @Override
    public String getOrCreateRootOfRouteInstancePath(CoreSession session) {
        return getPersister().getOrCreateRootOfDocumentRouteInstanceStructure(session).getPathAsString();
    }

    private void checkLockedByCurrentUser(FeuilleRoute route, CoreSession session) {
        final DocumentRef docRef = route.getDocument().getRef();
        checkLockedByCurrentUser(docRef, session);
    }

    private void checkLockedByCurrentUser(DocumentRef routeRef, CoreSession session) {
        if (!LockUtils.isLockedByCurrentUser(session, routeRef)) {
            throw new FeuilleRouteNotLockedException();
        }
    }
}
