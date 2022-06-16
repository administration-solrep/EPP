package fr.dila.st.core.operation.utils;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.core.operation.STApplication;
import fr.dila.st.core.operation.STVersion;
import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;

@Operation(
    id = STUpdateParametresOperation.ID,
    category = "Parametre",
    description = STUpdateParametresOperation.DESCRIPTION
)
@STVersion(version = "4.0.0", application = STApplication.EPG)
@STVersion(version = "4.0.0", application = STApplication.EPP)
@STVersion(version = "4.0.4", application = STApplication.REPONSES)
public class STUpdateParametresOperation extends AbstractUpdateParametersOperation {
    public static final String ID = "Parametre.Update";

    public static final String DESCRIPTION = "Mise à jour des paramètres techniques";

    @Context
    private OperationContext context;

    @Override
    protected OperationContext getContext() {
        return context;
    }

    @Override
    protected List<ParametreBean> getParametreBeans() {
        return ImmutableList.of(
            new ParametreBean(
                STParametreConstant.PAGE_RENSEIGNEMENTS_ID,
                null,
                null,
                null,
                STParametreConstant.PAGE_RENSEIGNEMENTS_VALUE
            )
        );
    }
}
