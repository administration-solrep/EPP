package fr.dila.ss.core.runner;

import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

public class ValidateDueDossierLinkUnrestricted extends UnrestrictedSessionRunner {
    protected BatchLoggerModel batchLoggerModel;
    protected Long nbError;

    /**
     * Constructeur de ValidateDueCaseLinkUnrestricted.
     *
     * @param session
     *            Session.
     */
    public ValidateDueDossierLinkUnrestricted(CoreSession session, BatchLoggerModel batchLoggerModel, Long errorCount) {
        super(session);
        this.batchLoggerModel = batchLoggerModel;
        this.nbError = errorCount;
    }

    /**
     * Recherche et valide automatiquement les DossierLink.
     * Envoi un mail d'information aux utilisateurs concernés pour chaque DossierLink.
     */
    @Override
    public void run() {
        SSFeuilleRouteService feuilleRouteService = SSServiceLocator.getSSFeuilleRouteService();
        feuilleRouteService.doAutomaticValidationBatch(session, batchLoggerModel, nbError);
        session.save();
    }
}
