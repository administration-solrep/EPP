package fr.dila.st.ui.bean.actions;

public class STLockActionDTO {
    private boolean currentDocIsLockActionnableByCurrentUser;

    public STLockActionDTO() {}

    public boolean getCurrentDocIsLockActionnableByCurrentUser() {
        return currentDocIsLockActionnableByCurrentUser;
    }

    public void setCurrentDocIsLockActionnableByCurrentUser(boolean currentDocIsLockActionnableByCurrentUser) {
        this.currentDocIsLockActionnableByCurrentUser = currentDocIsLockActionnableByCurrentUser;
    }
}
