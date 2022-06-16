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
    id = UpdateObjetMailExportUsersOperation.ID,
    category = "Parametre",
    description = UpdateObjetMailExportUsersOperation.DESCRIPTION
)
@STVersion(version = "4.0.1", application = STApplication.EPG)
@STVersion(version = "4.0.1", application = STApplication.EPP)
@STVersion(version = "4.0.4", application = STApplication.REPONSES)
public class UpdateObjetMailExportUsersOperation extends AbstractUpdateParametersOperation {
    public static final String ID = "Parametre.Update.Objet.Mail.Export.Users";

    public static final String DESCRIPTION =
        "Mise à jour du paramètre technique de l'objet du mail pour les exports d'utilisateurs";

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
                STParametreConstant.OBJET_MAIL_EXPORT_USERS_NAME,
                STParametreConstant.OBJET_MAIL_EXPORT_USERS_NAME_TITRE,
                null,
                null,
                null
            )
        );
    }
}
