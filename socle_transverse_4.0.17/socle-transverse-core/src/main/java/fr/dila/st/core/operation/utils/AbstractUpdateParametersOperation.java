package fr.dila.st.core.operation.utils;

import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.FAIL_GET_DOCUMENT_TEC;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import javax.servlet.http.HttpServletResponse;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PathRef;

public abstract class AbstractUpdateParametersOperation extends AbstractParametreOperation {
    private static final STLogger LOGGER = STLogFactory.getLog(AbstractUpdateParametersOperation.class);

    protected void updateParametre(CoreSession session, ParametreBean parametreBean) {
        String parameterRootPath = STServiceLocator
            .getSTParametreService()
            .getParametreFolder(session)
            .getPath()
            .toString();
        DocumentRef pathRef = new PathRef(parameterRootPath, parametreBean.getParameterName());
        if (!session.exists(pathRef)) {
            LOGGER.warn(FAIL_GET_DOCUMENT_TEC, pathRef.toString());
            return;
        }

        DocumentModel doc = session.getDocument(pathRef);
        if (parametreBean.getParameterTitre() != null) {
            DublincoreSchemaUtils.setTitle(doc, parametreBean.getParameterTitre());
        }
        if (parametreBean.getParameterDescription() != null) {
            DublincoreSchemaUtils.setDescription(doc, parametreBean.getParameterDescription());
        }
        STParametre param = doc.getAdapter(STParametre.class);
        if (parametreBean.getParameterUnit() != null) {
            param.setUnit(parametreBean.getParameterUnit());
        }
        if (parametreBean.getParameterValue() != null) {
            param.setValue(parametreBean.getParameterValue());
        }
        if (parametreBean.getParameterType() != null) {
            param.setType(parametreBean.getParameterType());
        }
        session.saveDocument(doc);
    }

    @OperationMethod
    public void updateParametres() {
        if (!getContext().getPrincipal().isAdministrator()) {
            throw new NuxeoException(
                "Seul un administrateur Nuxeo peux créer des paramètres",
                HttpServletResponse.SC_FORBIDDEN
            );
        }
        CoreSession session = getContext().getCoreSession();

        getParametreBeans().forEach(cpb -> updateParametre(session, cpb));
    }
}
