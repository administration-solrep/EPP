package fr.dila.st.core.exception;

import fr.dila.st.core.util.ResourceHelper;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.StringUtils;

public class STAuthorizationException extends STException {
    private static final long serialVersionUID = -4927611581507686889L;

    private static final String MESSAGE_KEY = "error.unauthorized";

    public STAuthorizationException(String ressourceName) {
        super(
            StringUtils.appendIfMissing(ResourceHelper.getString(MESSAGE_KEY), StringUtils.SPACE) + ressourceName,
            Status.FORBIDDEN.getStatusCode()
        );
    }
}
