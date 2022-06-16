package fr.dila.st.ui.th.enums;

public enum TransverseTopMenu {
    SEARCH("/recherche", "menu.recherche.title", null),
    ADMIN("/admin", "menu.admin.title", null);

    private final String url;
    private final String titleKey;
    private final TransverseTopMenu[] childs;

    private TransverseTopMenu(String url, String titleKey, TransverseTopMenu[] childs) {
        this.url = url;
        this.titleKey = titleKey;
        this.childs = childs;
    }

    public String getUrl() {
        return url;
    }

    public TransverseTopMenu[] getChilds() {
        return childs;
    }

    public String getTitleKey() {
        return titleKey;
    }
}
