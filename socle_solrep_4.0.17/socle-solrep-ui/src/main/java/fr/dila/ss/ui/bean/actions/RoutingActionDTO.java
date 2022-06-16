package fr.dila.ss.ui.bean.actions;

/**
 * @author olejacques
 *
 */
public class RoutingActionDTO {
    private boolean isFeuilleRouteVisible;

    private boolean hasRelatedRoute;

    private boolean isEditableEtapeObligatoire;

    private boolean isEditableNote;

    private boolean isEditableRouteElement;

    private boolean isRouteFolder;

    private boolean isSerialStepFolder;

    private boolean isCurrentRouteLockedByCurrentUser;

    private boolean isFeuilleRouteUpdatable;

    private boolean isStepEditable;

    private boolean isStepDeletable;

    private boolean isFolderDeletable;

    private boolean isFeuilleRouteInstance;

    private boolean isModeleFeuilleRoute;

    private boolean isStepCopied;

    private boolean isStepPourInitialisation;

    public RoutingActionDTO() {
        // Default constructor
    }

    public boolean getIsFeuilleRouteVisible() {
        return isFeuilleRouteVisible;
    }

    public void setIsFeuilleRouteVisible(boolean isFeuilleRouteVisible) {
        this.isFeuilleRouteVisible = isFeuilleRouteVisible;
    }

    public boolean getIsSerialStepFolder() {
        return isSerialStepFolder;
    }

    public void setIsSerialStepFolder(boolean isSerialStepFolder) {
        this.isSerialStepFolder = isSerialStepFolder;
    }

    public boolean getIsEditableNote() {
        return isEditableNote;
    }

    public void setIsEditableNote(boolean isEditableNote) {
        this.isEditableNote = isEditableNote;
    }

    public boolean getIsRouteFolder() {
        return isRouteFolder;
    }

    public void setIsRouteFolder(boolean isRouteFolder) {
        this.isRouteFolder = isRouteFolder;
    }

    public boolean getIsEditableEtapeObligatoire() {
        return isEditableEtapeObligatoire;
    }

    public void setIsEditableEtapeObligatoire(boolean isEditableEtapeObligatoire) {
        this.isEditableEtapeObligatoire = isEditableEtapeObligatoire;
    }

    public boolean getIsEditableRouteElement() {
        return isEditableRouteElement;
    }

    public void setIsEditableRouteElement(boolean isEditableRouteElement) {
        this.isEditableRouteElement = isEditableRouteElement;
    }

    public boolean getHasRelatedRoute() {
        return hasRelatedRoute;
    }

    public void setHasRelatedRoute(boolean hasRelatedRoute) {
        this.hasRelatedRoute = hasRelatedRoute;
    }

    public boolean getIsCurrentRouteLockedByCurrentUser() {
        return isCurrentRouteLockedByCurrentUser;
    }

    public void setIsCurrentRouteLockedByCurrentUser(boolean isCurrentRouteLockedByCurrentUser) {
        this.isCurrentRouteLockedByCurrentUser = isCurrentRouteLockedByCurrentUser;
    }

    public boolean getIsFeuilleRouteUpdatable() {
        return isFeuilleRouteUpdatable;
    }

    public void setIsFeuilleRouteUpdatable(boolean isFeuilleRouteUpdatable) {
        this.isFeuilleRouteUpdatable = isFeuilleRouteUpdatable;
    }

    public boolean getIsStepEditable() {
        return isStepEditable;
    }

    public void setStepEditable(boolean isStepEditable) {
        this.isStepEditable = isStepEditable;
    }

    public boolean getIsStepDeletable() {
        return isStepDeletable;
    }

    public void setStepDeletable(boolean isStepDeletable) {
        this.isStepDeletable = isStepDeletable;
    }

    public boolean getIsFolderDeletable() {
        return isFolderDeletable;
    }

    public void setFolderDeletable(boolean isFolderDeletable) {
        this.isFolderDeletable = isFolderDeletable;
    }

    public boolean getIsFeuilleRouteInstance() {
        return isFeuilleRouteInstance;
    }

    public void setIsFeuilleRouteInstance(boolean isFeuilleRouteInstance) {
        this.isFeuilleRouteInstance = isFeuilleRouteInstance;
    }

    public boolean getIsModeleFeuilleRoute() {
        return isModeleFeuilleRoute;
    }

    public void setIsModeleFeuilleRoute(boolean isModeleFeuilleRoute) {
        this.isModeleFeuilleRoute = isModeleFeuilleRoute;
    }

    public boolean getIsStepCopied() {
        return isStepCopied;
    }

    public void setIsStepCopied(boolean isStepCopied) {
        this.isStepCopied = isStepCopied;
    }

    public boolean getIsStepPourInitialisation() {
        return isStepPourInitialisation;
    }

    public void setIsStepPourInitialisation(boolean isStepPourInitialisation) {
        this.isStepPourInitialisation = isStepPourInitialisation;
    }
}
