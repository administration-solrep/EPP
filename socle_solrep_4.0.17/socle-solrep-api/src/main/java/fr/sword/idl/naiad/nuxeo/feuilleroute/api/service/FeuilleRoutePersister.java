package fr.sword.idl.naiad.nuxeo.feuilleroute.api.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * The DocumentRoutingPersister is responsible creating a folder structure to
 * persist {@link FeuilleRoute} instance, persisting new {@link FeuilleRoute}
 * instances and creating {@link FeuilleRoute} model from {@link FeuilleRoute}
 * instance.
 *
 */
public interface FeuilleRoutePersister {
    /**
     * Get or create the parent folder for a {@link FeuilleRoute} route
     * instance.
     *
     * @param document The {@link FeuilleRoute} model from which the instance
     *            will be created. Its metadata may be used when creating the
     *            parent.
     * @return The parent folder in which the {@link FeuilleRoute} will be
     *         persisted.
     */
    DocumentModel getParentFolderForDocumentRouteInstance(DocumentModel document, CoreSession session);

    /**
     * Creates a blank {@link FeuilleRoute} instance from a model.
     *
     * @param model the model
     * @return The created {@link FeuilleRoute}
     */
    DocumentModel createDocumentRouteInstanceFromDocumentRouteModel(DocumentModel model, CoreSession session);

    /**
     *
     * @param routeInstance
     * @param parentFolder
     * @return
     */
    DocumentModel saveDocumentRouteInstanceAsNewModel(
        DocumentModel routeInstance,
        DocumentModel parentFolder,
        CoreSession session
    );

    /**
     * Will get, and create if it does not exists the root document in which
     * {@link FeuilleRoute} structure will be created.
     *
     * @param session The session use to get or create the document.
     *
     * @return The root of the {@link FeuilleRoute} structure.
     */
    DocumentModel getOrCreateRootOfDocumentRouteInstanceStructure(CoreSession session);

    /**
     * Returns a folder in which new model, created from an instance of route
     * will be stored.
     *
     * @param session the session of the user
     * @param instance the instance that will be persisted as new model.
     */
    DocumentModel getParentFolderForNewModel(CoreSession session, DocumentModel instance);

    /**
     * Return the new name of a model when it is created from an instance.
     *
     * @return the new name
     */
    String getNewModelName(DocumentModel instance);
}
