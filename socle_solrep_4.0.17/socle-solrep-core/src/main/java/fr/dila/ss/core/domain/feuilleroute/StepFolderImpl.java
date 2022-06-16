package fr.dila.ss.core.domain.feuilleroute;

import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.adapter.FeuilleRouteStepsContainerImpl;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation des documents de type conteneur d'étape de feuille de route.
 *
 * @author jtremeaux
 */
public class StepFolderImpl extends FeuilleRouteStepsContainerImpl implements StepFolder {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -2464999080273631053L;

    /**
     * Constructeur de StepFolderImpl.
     *
     * @param doc Document
     * @param runner Runner
     */
    public StepFolderImpl(DocumentModel doc, ElementRunner runner) {
        super(doc, runner);
    }

    @Override
    public String getExecution() {
        return FeuilleRouteStepFolderSchemaUtil.getExecution(document);
    }

    @Override
    public void setExecution(FeuilleRouteExecutionType execution) {
        FeuilleRouteStepFolderSchemaUtil.setExecution(document, execution);
    }

    @Override
    public boolean isSerial() {
        return FeuilleRouteExecutionType.serial.name().equals(getExecution());
    }

    @Override
    public boolean isParallel() {
        return FeuilleRouteExecutionType.parallel.name().equals(getExecution());
    }
}
