package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

public class TreeDTO<T> {
    private List<T> childs = new ArrayList<>();

    public List<T> getChilds() {
        return childs;
    }

    public void setChilds(List<T> childs) {
        this.childs = childs;
    }
}
