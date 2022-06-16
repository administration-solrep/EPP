package fr.dila.st.core.operation.utils;

import com.google.common.collect.ImmutableList;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.core.operation.STApplication;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.service.STServiceLocator;
import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;

/**
 * Opération pour mettre à jour l'URL de l'application au sein de la base de données
 *
 */
@Operation(
    id = UpdateUrlApplicationOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    description = UpdateUrlApplicationOperation.DESCRIPTION
)
@STVersion(version = "4.0.0", application = STApplication.EPG)
@STVersion(version = "4.0.0", application = STApplication.EPP)
public class UpdateUrlApplicationOperation extends AbstractUpdateParametersOperation {
    public static final String ID = "Parametre.Update.Url.Application";

    public static final String DESCRIPTION = "Mise à jour de l'URL de l'application en base de données";

    @Context
    private OperationContext context;

    @Override
    protected OperationContext getContext() {
        return context;
    }

    @Override
    protected List<ParametreBean> getParametreBeans() {
        String urlApplication = STServiceLocator
            .getConfigService()
            .getValue(STConfigConstants.SOLON_MAIL_URL_APPLICATION);

        return ImmutableList.of(
            new ParametreBean(
                STParametreConstant.ADRESSE_URL_APPLICATION,
                STParametreConstant.ADRESSE_URL_APPLICATION_TITRE,
                STParametreConstant.ADRESSE_URL_APPLICATION_DESC,
                STParametreConstant.ADRESSE_MEL_EMISSION_PARAMETER_NAME_UNIT,
                urlApplication
            )
        );
    }
}
