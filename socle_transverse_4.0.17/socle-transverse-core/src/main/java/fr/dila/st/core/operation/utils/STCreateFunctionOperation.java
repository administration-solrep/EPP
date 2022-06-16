package fr.dila.st.core.operation.utils;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.operation.STApplication;
import fr.dila.st.core.operation.STVersion;
import java.util.Collections;
import java.util.List;
import org.nuxeo.ecm.automation.core.annotations.Operation;

/**
 * Operation pour ajouter des nouvelles fonctions dans EPG
 *
 */
@STVersion(version = "4.0.1", application = STApplication.EPG)
@STVersion(version = "4.0.4", application = STApplication.REPONSES)
@Operation(id = STCreateFunctionOperation.ID, category = STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR)
public class STCreateFunctionOperation extends AbstractCreateFunctionOperation {
    public static final String ID = "ST.Function.Creation";

    @Override
    protected List<FunctionBean> getFunctions() {
        return ImmutableList.of(
            new FunctionBean(
                STBaseFunctionConstant.DATE_DERNIERE_CONNEXION_ALL_USERS_VIEW,
                "Donne le droit de voir les dates de dernières connexions de tous les utilisateurs",
                Collections.singletonList(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME)
            ),
            new FunctionBean(
                STBaseFunctionConstant.DATE_DERNIERE_CONNEXION_USER_FROM_MINISTERE_VIEW,
                "Donne le droit de voir les dates de dernières connexions des utilisateurs appartenant au même ministère de l'utilisateur courant",
                Collections.singletonList(STBaseFunctionConstant.ADMIN_MINISTERIEL_GROUP_NAME)
            )
        );
    }
}
