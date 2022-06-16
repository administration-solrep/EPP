package fr.dila.st.ui.bean;

import fr.dila.st.ui.enums.SortOrder;

public class ColonneInfo implements IColonneInfo {
    private final String label;
    private final boolean sortable;
    private final boolean visible;
    private final boolean inverseSort;
    private final boolean labelVisible;

    private String sortValue;
    private String sortName;
    private String sortId;
    private String sortOrder;

    public ColonneInfo(
        String label,
        boolean isSortable,
        boolean isVisible,
        boolean inverseSort,
        boolean isLabelVisible
    ) {
        this.label = label;
        sortable = isSortable;
        visible = isVisible;
        this.inverseSort = inverseSort;
        labelVisible = isLabelVisible;
    }

    public ColonneInfo(String label, boolean isSortable, String name, SortOrder value) {
        this(label, isSortable, true, name, value, false, true, null);
    }

    public ColonneInfo(
        String label,
        boolean isSortable,
        String name,
        SortOrder value,
        boolean inverseSort,
        boolean isLabelVisible
    ) {
        this(label, isSortable, true, name, value, inverseSort, isLabelVisible, null);
    }

    public ColonneInfo(
        String label,
        boolean isSortable,
        boolean isVisible,
        String name,
        SortOrder value,
        boolean inverseSort,
        boolean isLabelVisible,
        Integer order
    ) {
        this(label, isSortable, isVisible, inverseSort, isLabelVisible);
        if (isSortable) {
            sortName = name;
            sortId = name + "Header";
            sortValue = value == null ? null : value.getValue();
            sortOrder = order == null ? null : order.toString();
        }
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isSortable() {
        return sortable;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean isInverseSort() {
        return inverseSort;
    }

    @Override
    public boolean isLabelVisible() {
        return labelVisible;
    }

    @Override
    public String getSortValue() {
        return sortValue;
    }

    @Override
    public void setSortValue(String sortValue) {
        this.sortValue = sortValue;
    }

    @Override
    public String getSortName() {
        return sortName;
    }

    @Override
    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    @Override
    public String getSortId() {
        return sortId;
    }

    @Override
    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    @Override
    public String getSortOrder() {
        return sortOrder;
    }

    @Override
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public static Builder builder(
        String label,
        boolean isSortable,
        boolean isVisible,
        boolean inverseSort,
        boolean isLabelVisible
    ) {
        return new Builder(label, isSortable, isVisible, inverseSort, isLabelVisible);
    }

    public static class Builder {
        private final String label;
        private final boolean sortable;
        private final boolean visible;
        private final boolean inverseSort;
        private final boolean labelVisible;

        private String sortValue;
        private String sortName;
        private String sortId;

        public Builder(
            String label,
            boolean isSortable,
            boolean isVisible,
            boolean inverseSort,
            boolean isLabelVisible
        ) {
            this.label = label;
            sortable = isSortable;
            visible = isVisible;
            this.inverseSort = inverseSort;
            labelVisible = isLabelVisible;
        }

        public Builder sortId(String val) {
            sortId = val;
            return this;
        }

        public Builder sortValue(SortOrder val) {
            sortValue = val == null ? null : val.getValue();
            return this;
        }

        public Builder sortName(String val) {
            sortName = val;
            return this;
        }

        public ColonneInfo build() {
            return new ColonneInfo(this);
        }
    }

    private ColonneInfo(Builder builder) {
        label = builder.label;
        sortable = builder.sortable;
        visible = builder.visible;
        inverseSort = builder.inverseSort;
        labelVisible = builder.labelVisible;
        sortId = builder.sortId;
        sortName = builder.sortName;
        sortValue = builder.sortValue;
    }

    @Override
    public String toString() {
        return "ColonneInfo [label=" + label + "]";
    }
}
