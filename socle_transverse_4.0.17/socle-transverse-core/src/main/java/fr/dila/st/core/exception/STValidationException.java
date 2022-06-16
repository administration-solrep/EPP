package fr.dila.st.core.exception;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import fr.dila.st.core.util.ResourceHelper;

public class STValidationException extends STException {
    private static final long serialVersionUID = -5322971687753457595L;

    private static final int STATUS_CODE = BAD_REQUEST.getStatusCode();

    public STValidationException(String messageKey) {
        super(ResourceHelper.getString(messageKey), STATUS_CODE);
    }

    public STValidationException(String messageKey, Object... arguments) {
        super(ResourceHelper.getString(messageKey, arguments), STATUS_CODE);
    }

    public STValidationException(String messageKey, Throwable cause) {
        super(ResourceHelper.getString(messageKey), cause, STATUS_CODE);
    }
}
