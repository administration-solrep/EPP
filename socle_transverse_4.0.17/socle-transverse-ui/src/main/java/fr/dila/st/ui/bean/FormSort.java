package fr.dila.st.ui.bean;

import fr.dila.st.ui.enums.SortOrder;

public class FormSort {
    private SortOrder sortOrder;

    private Integer priority;

    public FormSort() {}

    public FormSort(SortOrder sortOrder) {
        this(sortOrder, null);
    }

    public FormSort(SortOrder sortOrder, Integer priority) {
        this.sortOrder = sortOrder;
        this.priority = priority;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (priority == null ? 0 : priority.hashCode());
        result = prime * result + (sortOrder == null ? 0 : sortOrder.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FormSort other = (FormSort) obj;
        if (priority == null) {
            if (other.priority != null) {
                return false;
            }
        } else if (!priority.equals(other.priority)) {
            return false;
        }
        if (sortOrder != other.sortOrder) {
            return false;
        }
        return true;
    }
}
