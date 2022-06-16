package fr.dila.ss.ui.services;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service qui permet de filtrer les types d'étape de feuille de route. Prend en
 * paramètre des documents éléments du vocabulaire routing_task.
 *
 */
public interface SSRoutingTaskFilterService {
    boolean accept(CoreSession session, String routingTaskType);
}
