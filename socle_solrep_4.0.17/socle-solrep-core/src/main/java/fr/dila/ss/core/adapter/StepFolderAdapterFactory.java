package fr.dila.ss.core.adapter;

import fr.dila.ss.core.domain.feuilleroute.StepFolderImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner.ParallelRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner.SerialRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Adapteur de DocumentModel vers StepFolder.
 *
 * @author jtremeaux
 */
public class StepFolderAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public StepFolderAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentType(doc, FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER);

        FeuilleRouteExecutionType executionType = FeuilleRouteStepFolderSchemaUtil.getExecutionType(doc);
        switch (executionType) {
            case serial:
                return new StepFolderImpl(doc, new SerialRunner());
            case parallel:
                return new StepFolderImpl(doc, new ParallelRunner());
            default:
                throw new NuxeoException(
                    "Le type d'exécution du doc " + doc.getId() + " est inconnu : " + executionType
                );
        }
    }
}
