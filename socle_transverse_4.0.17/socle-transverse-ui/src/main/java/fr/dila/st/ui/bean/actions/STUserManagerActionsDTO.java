package fr.dila.st.ui.bean.actions;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class STUserManagerActionsDTO {
    private boolean isCurrentUserPermanent;

    private boolean isCreateUserAllowed;
    private boolean isDeleteUserAllowed;
    private boolean isEditUserAllowed;

    public STUserManagerActionsDTO() {
        // Default constructor
    }

    public boolean getIsCurrentUserPermanent() {
        return isCurrentUserPermanent;
    }

    public void setIsCurrentUserPermanent(boolean isCurrentUserPermanent) {
        this.isCurrentUserPermanent = isCurrentUserPermanent;
    }

    public boolean getIsCreateUserAllowed() {
        return isCreateUserAllowed;
    }

    public void setIsCreateUserAllowed(boolean isCreateUserAllowed) {
        this.isCreateUserAllowed = isCreateUserAllowed;
    }

    public boolean getIsDeleteUserAllowed() {
        return isDeleteUserAllowed;
    }

    public void setIsDeleteUserAllowed(boolean isDeleteUserAllowed) {
        this.isDeleteUserAllowed = isDeleteUserAllowed;
    }

    public boolean getIsEditUserAllowed() {
        return isEditUserAllowed;
    }

    public void setIsEditUserAllowed(boolean isEditUserAllowed) {
        this.isEditUserAllowed = isEditUserAllowed;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(isCurrentUserPermanent)
            .append(isDeleteUserAllowed)
            .append(isEditUserAllowed)
            .toHashCode();
    }

    /**
     * Implement the equals method using the EqualsBuilder.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof STUserManagerActionsDTO)) {
            return false;
        }
        STUserManagerActionsDTO that = (STUserManagerActionsDTO) obj;
        return new EqualsBuilder()
            .append(this.isCurrentUserPermanent, that.isCurrentUserPermanent)
            .append(this.isDeleteUserAllowed, that.isDeleteUserAllowed)
            .append(this.isEditUserAllowed, that.isEditUserAllowed)
            .isEquals();
    }
}
