package fr.dila.st.core.operation.utils;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import javax.servlet.http.HttpServletResponse;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PathRef;

/**
 * Classe abstraite dédiée à la création de nouveaux paramètres via une
 * opération Nuxeo. La liste et le détail des paramètres sont listés dans les
 * extensions de cette classe (méthode getParametreBeans()).
 *
 * @author tlombard
 */

public abstract class AbstractCreateParametersOperation extends AbstractParametreOperation {

    protected void createParameter(CoreSession session, ParametreBean parameterBean) {
        String parameterRootPath = STServiceLocator
            .getSTParametreService()
            .getParametreFolder(session)
            .getPath()
            .toString();
        if (!session.exists(new PathRef(parameterRootPath, parameterBean.getParameterName()))) {
            DocumentModel doc = session.createDocumentModel(
                parameterRootPath,
                parameterBean.getParameterName(),
                STConstant.PARAMETRE_DOCUMENT_TYPE
            );
            DublincoreSchemaUtils.setTitle(doc, parameterBean.getParameterTitre());
            DublincoreSchemaUtils.setDescription(doc, parameterBean.getParameterDescription());
            STParametre param = doc.getAdapter(STParametre.class);
            param.setUnit(parameterBean.getParameterUnit());
            param.setValue(parameterBean.getParameterValue());
            param.setType(parameterBean.getParameterType());
            session.createDocument(doc);
        }
    }

    @OperationMethod
    public void createParametres() {
        if (!getContext().getPrincipal().isAdministrator()) {
            throw new NuxeoException(
                "Seul un administrateur Nuxeo peux créer des paramètres",
                HttpServletResponse.SC_FORBIDDEN
            );
        }
        CoreSession session = getContext().getCoreSession();

        getParametreBeans().forEach(cpb -> createParameter(session, cpb));
    }
}
