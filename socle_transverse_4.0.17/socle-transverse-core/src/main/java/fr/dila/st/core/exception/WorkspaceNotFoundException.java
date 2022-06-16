package fr.dila.st.core.exception;

public class WorkspaceNotFoundException extends STException {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -7676141381462852463L;

    public WorkspaceNotFoundException() {
        super();
    }

    public WorkspaceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkspaceNotFoundException(String message) {
        super(message);
    }

    public WorkspaceNotFoundException(Throwable cause) {
        super(cause);
    }
}
