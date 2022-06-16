package fr.dila.ss.api.service;

import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.st.api.service.JournalService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface SSJournalService extends JournalService {
    /**
     * Journalise une action de catégorie feuille de route sur une étape
     *
     * @param session
     * @param etape
     *            STRouteStep : l'étape sur laquelle l'action a été effectuée
     * @param dossierDoc
     *            DocumentModel : le dossier sur laquelle l'action a été effectuée
     * @param eventName
     *            String : le nom de l'évènement déclenché pour la journalisation
     * @param comment
     *            String : commentaire utilisé pour la journalisation
     */
    void journaliserActionEtapeFDR(
        CoreSession session,
        SSRouteStep etape,
        DocumentModel dossierDoc,
        String eventName,
        String comment
    );
}
