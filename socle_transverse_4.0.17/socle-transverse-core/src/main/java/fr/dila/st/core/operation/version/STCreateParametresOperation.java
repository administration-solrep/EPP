package fr.dila.st.core.operation.version;

import static fr.dila.st.api.constant.STParametreConstant.CORPS_SUCCESS_MAIL_EXPORT_NAME_DESCRIPTION;
import static fr.dila.st.api.constant.STParametreConstant.CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME;
import static fr.dila.st.api.constant.STParametreConstant.CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME_TITRE;
import static fr.dila.st.api.constant.STParametreConstant.CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME_UNIT;
import static fr.dila.st.api.constant.STParametreConstant.CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME_VALUE;
import static fr.dila.st.api.constant.STParametreConstant.OBJET_MAIL_EXPORT_USERS_NAME;
import static fr.dila.st.api.constant.STParametreConstant.OBJET_MAIL_EXPORT_USERS_NAME_DESCRIPTION;
import static fr.dila.st.api.constant.STParametreConstant.OBJET_MAIL_EXPORT_USERS_NAME_TITRE;
import static fr.dila.st.api.constant.STParametreConstant.OBJET_MAIL_EXPORT_USERS_NAME_UNIT;
import static fr.dila.st.api.constant.STParametreConstant.OBJET_MAIL_EXPORT_USERS_NAME_VALUE;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.operation.STApplication;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.operation.utils.AbstractCreateParametersOperation;
import fr.dila.st.core.operation.utils.ParametreBean;
import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;

/**
 * Operation pour ajouter des nouveaux paramètres dans EPG
 *
 */
@STVersion(version = "4.0.0", application = STApplication.EPG)
@STVersion(version = "4.0.0", application = STApplication.EPP)
@STVersion(version = "4.0.3", application = STApplication.REPONSES)
@Operation(id = STCreateParametresOperation.ID, category = STConstant.PARAMETRE_DOCUMENT_TYPE)
public class STCreateParametresOperation extends AbstractCreateParametersOperation {
    public static final String ID = "ST.Parametre.Creation";

    @Context
    private OperationContext context;

    public STCreateParametresOperation() {}

    @Override
    protected List<ParametreBean> getParametreBeans() {
        return ImmutableList.of(
            new ParametreBean(
                OBJET_MAIL_EXPORT_USERS_NAME,
                OBJET_MAIL_EXPORT_USERS_NAME_TITRE,
                OBJET_MAIL_EXPORT_USERS_NAME_DESCRIPTION,
                OBJET_MAIL_EXPORT_USERS_NAME_UNIT,
                OBJET_MAIL_EXPORT_USERS_NAME_VALUE
            ),
            new ParametreBean(
                CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME,
                CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME_TITRE,
                CORPS_SUCCESS_MAIL_EXPORT_NAME_DESCRIPTION,
                CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME_UNIT,
                CORPS_SUCCESS_MAIL_EXPORT_USERS_NAME_VALUE
            )
        );
    }

    @Override
    protected OperationContext getContext() {
        return context;
    }
}
