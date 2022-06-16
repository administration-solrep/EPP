package fr.sword.idl.naiad.nuxeo.feuilleroute.core.adapter;

import fr.dila.ss.api.constant.SSConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner.ParallelRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner.SerialRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner.StepElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

/**
 * Provides {@link FeuilleRoute} for a {@link DocumentModel}.
 *
 *
 */
public class FeuilleRouteAdapterFactory implements DocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> itf) {
        String type = doc.getType();
        if (doc.hasFacet(FeuilleRouteConstant.FACET_FEUILLE_ROUTE)) {
            FeuilleRouteExecutionType executionType = getExecutionType(doc, type);
            return new FeuilleRouteImpl(doc, initRunnerForExec(executionType));
        } else if (doc.hasFacet(FeuilleRouteConstant.FACET_FEUILLE_ROUTE_STEP)) {
            return new FeuilleRouteElementImpl(doc, new StepElementRunner());
        } else if (SSConstant.STEP_FOLDER_DOCUMENT_TYPE.equalsIgnoreCase(type)) {
            FeuilleRouteExecutionType executionType = getExecutionType(doc, type);
            return new FeuilleRouteStepsContainerImpl(doc, initRunnerForExec(executionType));
        }
        return null;
    }

    protected FeuilleRouteExecutionType getExecutionType(DocumentModel doc, String type) {
        FeuilleRouteExecutionType executionType = FeuilleRouteExecutionType.valueOf(
            FeuilleRouteStepFolderSchemaUtil.getExecution(doc)
        );
        return executionType;
    }

    protected ElementRunner initRunnerForExec(FeuilleRouteExecutionType executionType) {
        switch (executionType) {
            case serial:
                return new SerialRunner();
            case parallel:
                return new ParallelRunner();
        }
        throw new NuxeoException(String.format("executionType [%s] not managed", executionType));
    }

    protected Object getProperty(DocumentModel doc, String xpath) {
        return doc.getPropertyValue(xpath);
    }
}
