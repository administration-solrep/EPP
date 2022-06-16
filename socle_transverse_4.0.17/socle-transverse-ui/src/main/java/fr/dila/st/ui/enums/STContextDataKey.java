package fr.dila.st.ui.enums;

import com.sun.jersey.core.header.FormDataContentDisposition;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.ui.bean.GestionAccesDTO;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.bean.actions.DossierLockActionDTO;
import fr.dila.st.ui.bean.actions.STLockActionDTO;
import fr.dila.st.ui.bean.actions.STUserManagerActionsDTO;
import fr.dila.st.ui.th.bean.EntiteForm;
import fr.dila.st.ui.th.bean.GouvernementForm;
import fr.dila.st.ui.th.bean.MailForm;
import fr.dila.st.ui.th.bean.PaginationForm;
import fr.dila.st.ui.th.bean.PosteForm;
import fr.dila.st.ui.th.bean.PosteWsForm;
import fr.dila.st.ui.th.bean.TransmettreParMelForm;
import fr.dila.st.ui.th.bean.UniteStructurelleForm;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UserSearchForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModelList;

public enum STContextDataKey implements ContextDataKey {
    ACTIVATE_FILTER(Boolean.class),
    BREADCRUMB_BASE_LEVEL(Integer.class),
    BREADCRUMB_BASE_URL(String.class),
    COMPLETE_KEY(String.class),
    CORBEILLE_ID(String.class),
    CURRENT_DOSSIER_LINK(String.class),
    CURRENT_TAB(String.class),
    DIRECTION_ID(String.class),
    DOSSIER_ID(String.class),
    DOSSIER_LOCK_ACTIONS(DossierLockActionDTO.class, "dossierLockActions"),
    DOSSIER_STATE(STDossier.DossierState.class),
    ENTITE_FORM(EntiteForm.class),
    ENTITE_NODE_ID(String.class),
    EVENEMENT_ID(String.class),
    FILE_CONTENT(InputStream.class),
    FILE_DETAILS(FormDataContentDisposition.class),
    FILE_NAME(String.class),
    FORM(PaginationForm.class, "form"),
    GESTION_ACCES(GestionAccesDTO.class),
    GET_FULL_USER(Boolean.class),
    GVT_FORM(GouvernementForm.class),
    ID(String.class),
    IDS(Collection.class),
    INPUT(String.class),
    IS_ACTION_MASS(Boolean.class),
    IS_PASSWORD_RESET(Boolean.class, "isPasswordReset"),
    LOCK_ACTIONS(STLockActionDTO.class, "stLockActions"),
    MAILBOX_MAP(Map.class),
    MAIL_FORM(MailForm.class),
    MINISTERE_ID(String.class),
    MODE_CREATION(String.class),
    NEW_USER_PASSWORD(String.class),
    OPEN_NODE(Set.class),
    ORGANIGRAMME_NODE(OrganigrammeElementDTO.class, "node"),
    PAGINATION_FORM(PaginationForm.class),
    POSTE_FORM(PosteForm.class),
    POSTE_WS_FORM(PosteWsForm.class),
    PROFILES(DocumentModelList.class),
    PROFILE_ID(String.class),
    PROFILE_LISTING_MODE(String.class),
    RECIPIENT_ID(List.class),
    SELECTION_FILTER(String.class),
    SHOW_DEACTIVATED(Boolean.class),
    SORT_ORDER(SortOrder.class),
    TRANSMETTRE_PAR_MEL_FORM(TransmettreParMelForm.class),
    TYPE(OrganigrammeType.class),
    TYPE_SELECTION(String.class),
    UNITE_STRUCTURELLE_FORM(UniteStructurelleForm.class),
    USERS(List.class),
    USERS_LIST_FORM(UsersListForm.class),
    USER_ACTIONS(STUserManagerActionsDTO.class),
    USER_CREATION(Boolean.class),
    USER_EMAIL(String.class),
    USER_FORM(UserForm.class),
    USER_ID(String.class),
    USER_SEARCH_FORM(UserSearchForm.class);

    private final Class<?> valueType;
    private final String specificKey;

    STContextDataKey(Class<?> valueType) {
        this(valueType, null);
    }

    STContextDataKey(Class<?> valueType, String specificKey) {
        this.valueType = valueType;
        this.specificKey = specificKey;
    }

    @Override
    public String getName() {
        return StringUtils.defaultIfBlank(specificKey, name());
    }

    @Override
    public Class<?> getValueType() {
        return valueType;
    }
}
