package fr.dila.st.api.exception;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import org.nuxeo.ecm.core.api.NuxeoException;

public class UserNotFoundException extends NuxeoException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String username) {
        super(String.format("L'utilisateur [%s] n'existe pas", username), SC_NOT_FOUND);
    }
}
