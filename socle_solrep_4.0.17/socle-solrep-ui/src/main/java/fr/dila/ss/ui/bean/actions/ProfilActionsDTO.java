package fr.dila.ss.ui.bean.actions;

import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class ProfilActionsDTO {
    private boolean isDeleteAllowed;
    private boolean isEditAllowed;

    public ProfilActionsDTO() {
        // Default constructor
    }

    public boolean getIsDeleteAllowed() {
        return isDeleteAllowed;
    }

    public void setIsDeleteAllowed(boolean isDeleteAllowed) {
        this.isDeleteAllowed = isDeleteAllowed;
    }

    public boolean getIsEditAllowed() {
        return isEditAllowed;
    }

    public void setEditAllowed(boolean isEditAllowed) {
        this.isEditAllowed = isEditAllowed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDeleteAllowed, isEditAllowed);
    }

    /**
     * Implement the equals method using the EqualsBuilder.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProfilActionsDTO)) {
            return false;
        }
        ProfilActionsDTO that = (ProfilActionsDTO) obj;
        return new EqualsBuilder()
            .append(this.isDeleteAllowed, that.isDeleteAllowed)
            .append(this.isEditAllowed, that.isEditAllowed)
            .isEquals();
    }
}
