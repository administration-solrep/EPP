package fr.dila.st.ui.enums;

public enum STUserSessionKey implements UserSessionKey {
    LAST_USER_NOTIFICATION,
    LIST_USERNAME_FROM_ENTITE,
    SEARCH_FORMS_KEY,
    USER_LIST_FORM_KEY;

    @Override
    public String getName() {
        return name();
    }
}
