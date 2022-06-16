package fr.dila.ss.ui.bean;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.th.bean.UsersListForm;
import java.util.HashMap;
import java.util.Map;

public class SupervisionUsersListForm extends UsersListForm {

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(STSchemaConstant.USER_LAST_NAME, new FormSort(getNom()));
        map.put(STSchemaConstant.USER_FIRST_NAME, new FormSort(getPrenom()));
        map.put(STSchemaConstant.USER_USERNAME, new FormSort(getUtilisateur()));
        map.put(STSchemaConstant.USER_DATE_DERNIERE_CONNEXION, new FormSort(getDateLastConnexion()));
        return map;
    }

    public static SupervisionUsersListForm newForm() {
        return initForm(new SupervisionUsersListForm());
    }
}
