package fr.dila.st.ui.enums;

import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_LEVEL;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_URL;
import static fr.dila.st.ui.enums.STContextDataKey.CURRENT_DOSSIER_LINK;
import static fr.dila.st.ui.enums.STContextDataKey.DOSSIER_ID;
import static fr.dila.st.ui.enums.STContextDataKey.DOSSIER_LOCK_ACTIONS;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_CONTENT;
import static fr.dila.st.ui.enums.STContextDataKey.FILE_DETAILS;
import static fr.dila.st.ui.enums.STContextDataKey.GVT_FORM;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static fr.dila.st.ui.enums.STContextDataKey.LOCK_ACTIONS;
import static fr.dila.st.ui.enums.STContextDataKey.NEW_USER_PASSWORD;
import static fr.dila.st.ui.enums.STContextDataKey.POSTE_FORM;
import static fr.dila.st.ui.enums.STContextDataKey.PROFILES;
import static fr.dila.st.ui.enums.STContextDataKey.PROFILE_ID;
import static fr.dila.st.ui.enums.STContextDataKey.PROFILE_LISTING_MODE;
import static fr.dila.st.ui.enums.STContextDataKey.RECIPIENT_ID;
import static fr.dila.st.ui.enums.STContextDataKey.SHOW_DEACTIVATED;
import static fr.dila.st.ui.enums.STContextDataKey.SORT_ORDER;
import static fr.dila.st.ui.enums.STContextDataKey.UNITE_STRUCTURELLE_FORM;
import static fr.dila.st.ui.enums.STContextDataKey.USERS;
import static fr.dila.st.ui.enums.STContextDataKey.USERS_LIST_FORM;
import static fr.dila.st.ui.enums.STContextDataKey.USER_ACTIONS;
import static fr.dila.st.ui.enums.STContextDataKey.USER_CREATION;
import static fr.dila.st.ui.enums.STContextDataKey.USER_EMAIL;
import static fr.dila.st.ui.enums.STContextDataKey.USER_FORM;
import static fr.dila.st.ui.enums.STContextDataKey.USER_ID;
import static fr.dila.st.ui.enums.STContextDataKey.USER_SEARCH_FORM;
import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jersey.core.header.FormDataContentDisposition;
import fr.dila.st.core.AbstractTestSortableEnum;
import fr.dila.st.ui.bean.actions.DossierLockActionDTO;
import fr.dila.st.ui.bean.actions.STLockActionDTO;
import fr.dila.st.ui.bean.actions.STUserManagerActionsDTO;
import fr.dila.st.ui.th.bean.GouvernementForm;
import fr.dila.st.ui.th.bean.PosteForm;
import fr.dila.st.ui.th.bean.UniteStructurelleForm;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UserSearchForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;
import org.nuxeo.ecm.core.api.DocumentModelList;

public class STContextDataKeyTest extends AbstractTestSortableEnum<STContextDataKey> {

    @Override
    protected Class<STContextDataKey> getEnumClass() {
        return STContextDataKey.class;
    }

    @Test
    public void getName() {
        assertThat(BREADCRUMB_BASE_LEVEL.getName()).isEqualTo("BREADCRUMB_BASE_LEVEL");
        assertThat(BREADCRUMB_BASE_URL.getName()).isEqualTo("BREADCRUMB_BASE_URL");
        assertThat(CURRENT_DOSSIER_LINK.getName()).isEqualTo("CURRENT_DOSSIER_LINK");
        assertThat(DOSSIER_ID.getName()).isEqualTo("DOSSIER_ID");
        assertThat(DOSSIER_LOCK_ACTIONS.getName()).isEqualTo("dossierLockActions");
        assertThat(FILE_CONTENT.getName()).isEqualTo("FILE_CONTENT");
        assertThat(FILE_DETAILS.getName()).isEqualTo("FILE_DETAILS");
        assertThat(GVT_FORM.getName()).isEqualTo("GVT_FORM");
        assertThat(ID.getName()).isEqualTo("ID");
        assertThat(LOCK_ACTIONS.getName()).isEqualTo("stLockActions");
        assertThat(NEW_USER_PASSWORD.getName()).isEqualTo("NEW_USER_PASSWORD");
        assertThat(POSTE_FORM.getName()).isEqualTo("POSTE_FORM");
        assertThat(PROFILES.getName()).isEqualTo("PROFILES");
        assertThat(PROFILE_ID.getName()).isEqualTo("PROFILE_ID");
        assertThat(PROFILE_LISTING_MODE.getName()).isEqualTo("PROFILE_LISTING_MODE");
        assertThat(SORT_ORDER.getName()).isEqualTo("SORT_ORDER");
        assertThat(UNITE_STRUCTURELLE_FORM.getName()).isEqualTo("UNITE_STRUCTURELLE_FORM");
        assertThat(USERS.getName()).isEqualTo("USERS");
        assertThat(USERS_LIST_FORM.getName()).isEqualTo("USERS_LIST_FORM");
        assertThat(USER_ACTIONS.getName()).isEqualTo("USER_ACTIONS");
        assertThat(USER_CREATION.getName()).isEqualTo("USER_CREATION");
        assertThat(USER_EMAIL.getName()).isEqualTo("USER_EMAIL");
        assertThat(USER_FORM.getName()).isEqualTo("USER_FORM");
        assertThat(USER_ID.getName()).isEqualTo("USER_ID");
        assertThat(USER_SEARCH_FORM.getName()).isEqualTo("USER_SEARCH_FORM");
        assertThat(SHOW_DEACTIVATED.getName()).isEqualTo("SHOW_DEACTIVATED");
        assertThat(RECIPIENT_ID.getName()).isEqualTo("RECIPIENT_ID");
    }

    @Test
    public void getTypeValue() {
        assertThat(BREADCRUMB_BASE_LEVEL.getValueType()).isEqualTo(Integer.class);
        assertThat(BREADCRUMB_BASE_URL.getValueType()).isEqualTo(String.class);
        assertThat(CURRENT_DOSSIER_LINK.getValueType()).isEqualTo(String.class);
        assertThat(DOSSIER_ID.getValueType()).isEqualTo(String.class);
        assertThat(DOSSIER_LOCK_ACTIONS.getValueType()).isEqualTo(DossierLockActionDTO.class);
        assertThat(FILE_CONTENT.getValueType()).isEqualTo(InputStream.class);
        assertThat(FILE_DETAILS.getValueType()).isEqualTo(FormDataContentDisposition.class);
        assertThat(GVT_FORM.getValueType()).isEqualTo(GouvernementForm.class);
        assertThat(ID.getValueType()).isEqualTo(String.class);
        assertThat(LOCK_ACTIONS.getValueType()).isEqualTo(STLockActionDTO.class);
        assertThat(NEW_USER_PASSWORD.getValueType()).isEqualTo(String.class);
        assertThat(POSTE_FORM.getValueType()).isEqualTo(PosteForm.class);
        assertThat(PROFILES.getValueType()).isEqualTo(DocumentModelList.class);
        assertThat(PROFILE_ID.getValueType()).isEqualTo(String.class);
        assertThat(PROFILE_LISTING_MODE.getValueType()).isEqualTo(String.class);
        assertThat(SORT_ORDER.getValueType()).isEqualTo(SortOrder.class);
        assertThat(UNITE_STRUCTURELLE_FORM.getValueType()).isEqualTo(UniteStructurelleForm.class);
        assertThat(USERS.getValueType()).isEqualTo(List.class);
        assertThat(USERS_LIST_FORM.getValueType()).isEqualTo(UsersListForm.class);
        assertThat(USER_ACTIONS.getValueType()).isEqualTo(STUserManagerActionsDTO.class);
        assertThat(USER_CREATION.getValueType()).isEqualTo(Boolean.class);
        assertThat(USER_EMAIL.getValueType()).isEqualTo(String.class);
        assertThat(USER_FORM.getValueType()).isEqualTo(UserForm.class);
        assertThat(USER_ID.getValueType()).isEqualTo(String.class);
        assertThat(USER_SEARCH_FORM.getValueType()).isEqualTo(UserSearchForm.class);
        assertThat(SHOW_DEACTIVATED.getValueType()).isEqualTo(Boolean.class);
        assertThat(RECIPIENT_ID.getValueType()).isEqualTo(List.class);
    }
}
