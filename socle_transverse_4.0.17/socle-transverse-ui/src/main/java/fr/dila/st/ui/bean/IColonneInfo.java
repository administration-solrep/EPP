package fr.dila.st.ui.bean;

public interface IColonneInfo {
    String getLabel();

    boolean isSortable();

    boolean isVisible();

    boolean isInverseSort();

    boolean isLabelVisible();

    String getSortValue();

    String getSortName();

    String getSortId();

    String getSortOrder();

    void setSortId(String sortId);

    void setSortOrder(String sortOrder);

    void setSortValue(String sortValue);

    void setSortName(String sortName);
}
