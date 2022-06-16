package fr.dila.st.ui.bean;

import java.util.List;

public class Menu {
    private final String url;
    private final String titleKey;
    private final Menu[] childs;
    private Boolean isCurrent = false;

    public Menu(String url, String titleKey, Menu[] childs) {
        this.url = url;
        this.titleKey = titleKey;
        this.childs = childs;
    }

    public Menu(String url, String titleKey) {
        this(url, titleKey, null);
    }

    public String getUrl() {
        return url;
    }

    public Menu[] getChilds() {
        return childs;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public void getPathToCurrent(List<Menu> pathToCurrentMenu) {
        if (isCurrent) {
            pathToCurrentMenu.add(this);
        } else {
            if (childs != null && childs.length > 0) {
                for (Menu menu : childs) {
                    menu.getPathToCurrent(pathToCurrentMenu);
                }
                //Notre menu est current que si un de ces enfant est current
                if (!pathToCurrentMenu.isEmpty()) {
                    pathToCurrentMenu.add(this);
                }
            }
        }
    }
}
