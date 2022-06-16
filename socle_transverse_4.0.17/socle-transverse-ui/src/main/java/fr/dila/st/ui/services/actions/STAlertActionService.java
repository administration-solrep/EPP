package fr.dila.st.ui.services.actions;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 * Les actions relative aux alertes qui sont communes aux projets EPG et
 * Reponses
 *
 */
public interface STAlertActionService {
    /**
     * Met l'alerte en pause
     *
     * @return
     */
    String suspend(SpecificContext context);

    /**
     * renvoie vrai si l'alerte est suspendue
     */
    Boolean isSuspended(DocumentModel doc);

    /**
     * renvoie vrai si l'alerte est activée
     */
    Boolean isActivated(DocumentModel doc);

    /**
     * Rend l'alerte active.
     *
     * @return
     */
    String activate(SpecificContext context);

    /**
     * Retourne la requête courante.
     */
    Alert getCurrentAlert(DocumentModel alertDoc);

    /**
     * Supprime le document alert
     *
     */
    void delete(SpecificContext context, CoreSession session, DocumentModel doc);
}
