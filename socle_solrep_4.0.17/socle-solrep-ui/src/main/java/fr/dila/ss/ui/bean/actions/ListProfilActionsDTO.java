package fr.dila.ss.ui.bean.actions;

import java.util.Objects;

public class ListProfilActionsDTO {
    private boolean isCreateAllowed;

    public ListProfilActionsDTO() {
        // Default constructor
    }

    public boolean getIsCreateAllowed() {
        return isCreateAllowed;
    }

    public void setCreateAllowed(boolean isCreateAllowed) {
        this.isCreateAllowed = isCreateAllowed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isCreateAllowed);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ListProfilActionsDTO)) {
            return false;
        }
        ListProfilActionsDTO that = (ListProfilActionsDTO) obj;
        return Objects.equals(this.isCreateAllowed, that.isCreateAllowed);
    }
}
