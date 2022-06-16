package fr.dila.st.ui.core.auth;

import fr.dila.st.core.service.STServiceLocator;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.api.login.UserIdentificationInfo;
import org.nuxeo.ecm.platform.ui.web.auth.interfaces.NuxeoAuthenticationPlugin;

/**
 * Manage authent by token
 *
 * Based on org.nuxeo.ecm.platform.ui.web.auth.token.TokenAuthenticator
 * implementation new implementation to add test on token validity
 *
 * @author SLE
 *
 */
public class TokenAuthenticator implements NuxeoAuthenticationPlugin {
    private static final Log LOG = LogFactory.getLog(TokenAuthenticator.class);

    protected static final String TOKEN_HEADER = "X-Authentication-Token";

    protected static final String TOKEN_PARAM = "token";

    public TokenAuthenticator() {
        super();
    }

    @Override
    public Boolean handleLoginPrompt(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String baseURL) {
        try {
            httpResponse.setStatus(401);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().println("{\"status\": 401, \"message\":\"Accès non-autorisé : token invalide.\"}");
        } catch (IOException e) {
            LOG.error("token : send error response", e);
        }
        return Boolean.TRUE;
    }

    @Override
    public UserIdentificationInfo handleRetrieveIdentity(
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse
    ) {
        String token = getTokenFromRequest(httpRequest);

        if (token == null) {
            LOG.debug(String.format("Found no '%s' header in the request.", TOKEN_HEADER));
            return null;
        }

        String user = getUserByToken(token);
        if (user == null) {
            LOG.debug(
                String.format("No user bound to the token '%s' (maybe it has been revoked), returning null.", token)
            );
            return null;
        }

        return new UserIdentificationInfo(user, token);
    }

    private String getTokenFromRequest(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader(TOKEN_HEADER);

        if (token == null) {
            token = httpRequest.getParameter(TOKEN_PARAM);
        }

        return token;
    }

    public Boolean needLoginPrompt(HttpServletRequest httpRequest) {
        return Boolean.TRUE;
    }

    @Override
    public void initPlugin(Map<String, String> parameters) {
        // do nothing
    }

    public List<String> getUnAuthenticatedURLPrefix() {
        return null;
    }

    protected String getUserByToken(String token) {
        return STServiceLocator.getTokenService().getUserByToken(token);
    }
}
