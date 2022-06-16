package fr.dila.st.ui.core.auth;

import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.ERROR_BRUTEFORCE_WAITING;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.ERROR_CONNECTION_FAILED;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.ERROR_USERNAME_MISSING;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_CONNECTION_FAILED;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_ERROR;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_FAILED;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_MISSING;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_WAIT;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.START_PAGE_SAVE_KEY;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.utils.URLUtils;
import fr.dila.st.ui.utils.XssSanitizerUtils;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.URIUtils;
import org.nuxeo.ecm.platform.ui.web.auth.interfaces.NuxeoAuthenticationPluginLogoutExtension;
import org.nuxeo.ecm.platform.ui.web.auth.service.PluggableAuthenticationService;
import org.nuxeo.ecm.webengine.login.WebEngineFormAuthenticator;
import org.nuxeo.runtime.api.Framework;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncodingException;

/**
 * Gestion d'une authentification depuis un webengine
 * @author SPL
 *
 */
public class SolonWebengineFormAuth
    extends WebEngineFormAuthenticator
    implements NuxeoAuthenticationPluginLogoutExtension {
    private static final String PARAM_FAILED = "failed";

    private static final Log LOG = LogFactory.getLog(SolonWebengineFormAuth.class);

    private static final String LOGIN_PAGE_KEY = "LoginPageKey";
    public static final String EDITION_AUTH_LINK = "/ngedit/auth/token";
    public static final String IS_EDIT_AUTH_KEY = "IsEditAuth";
    private static final String X_FORWARDED_HOST = "x-forwarded-host";
    private static final String APP_CONTEXT_PATH = "site/app-ui";
    private static final String SLASH = "/";
    private static final String MAIN_CONTENT = "#main_content";
    private static final int RANDOM_STRING_LENGTH = 32;
    private static final int RANDOM_STRING_MIN_INDEX = 97; //a
    private static final int RANDOM_STRING_MAX_INDEX = 122; //z

    private String loginPage = "/login";

    /**
     * Default constructor
     */
    public SolonWebengineFormAuth() {
        super();
    }

    @Override
    public void initPlugin(Map<String, String> parameters) {
        super.initPlugin(parameters);
        if (parameters.get(LOGIN_PAGE_KEY) != null) {
            loginPage = parameters.get(LOGIN_PAGE_KEY);
        }
    }

    @Override
    public Boolean handleLoginPrompt(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String baseURL) {
        try {
            LOG.debug("Forward to Login Screen");
            Map<String, String> parameters = new HashMap<>();
            String redirectUrl = URLUtils.generateContextPath(baseURL, getLoginPage(), httpRequest) + getLoginPage();

            Enumeration<String> paramNames = httpRequest.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = XssSanitizerUtils.stripXSS(paramNames.nextElement());
                if (StringUtils.isNotBlank(name)) {
                    String value = httpRequest.getParameter(name);
                    parameters.put(encodeValue(name), encodeValue(XssSanitizerUtils.stripXSS(value)));
                }
            }

            // avoid resending the password in clear !!!
            parameters.remove(passwordKey);
            parameters.remove("forceAnonymousLogin");
            redirectUrl = URIUtils.addParametersToURIQuery(redirectUrl, parameters);

            HttpSession session = httpRequest.getSession(false);
            if (session != null && session.getAttribute(START_PAGE_SAVE_KEY) != null) {
                String startPage = (String) session.getAttribute(START_PAGE_SAVE_KEY);

                //On vérifie l'état de l'URL sauvegardée et on supprime site/app-ui/ si on vient d'un RP ou LB
                if (
                    StringUtils.isNotBlank(httpRequest.getHeader(X_FORWARDED_HOST)) &&
                    (startPage.startsWith(APP_CONTEXT_PATH) || startPage.startsWith(SLASH + APP_CONTEXT_PATH))
                ) {
                    startPage = startPage.substring(APP_CONTEXT_PATH.length() + 1);
                    session.setAttribute(START_PAGE_SAVE_KEY, startPage);
                }

                if (startPage.endsWith(EDITION_AUTH_LINK)) {
                    session.setAttribute(IS_EDIT_AUTH_KEY, true);
                }
            }

            httpResponse.sendRedirect(redirectUrl + MAIN_CONTENT);
        } catch (IOException | EncodingException e) {
            LOG.error(e, e);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    protected String getLoginPathInfo(HttpServletRequest request) {
        return "home";
    }

    protected String getLoginPage() {
        return loginPage;
    }

    @Override
    public Boolean handleLogout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            //Redirection du logout sur la page de login
            final PluggableAuthenticationService service = (PluggableAuthenticationService) Framework
                .getRuntime()
                .getComponent(PluggableAuthenticationService.NAME);
            final String baseUrl = service.getBaseURL(httpRequest);
            String redirectUrl = URLUtils.generateContextPath(baseUrl, getLoginPage(), httpRequest) + getLoginPage();
            httpResponse.sendRedirect(redirectUrl + MAIN_CONTENT);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to redirect logout", e);
            return false;
        }
    }

    @Override
    public boolean onError(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = getLoginPathInfo(request);
            if (path == null) { // this should never happens
                return false;
            }
            // ajax request
            if (request.getParameter("caller") != null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed");
            } else { // normal request
                Map<String, String> parameters = new HashMap<>();
                String loginError = (String) request.getAttribute(LOGIN_ERROR);
                if (loginError != null) {
                    if (ERROR_USERNAME_MISSING.equals(loginError)) {
                        parameters.put(LOGIN_MISSING, "true");
                    } else if (ERROR_CONNECTION_FAILED.equals(loginError)) {
                        parameters.put(LOGIN_CONNECTION_FAILED, "true");
                        parameters.put(LOGIN_FAILED, "true"); // compat
                    } else if (ERROR_BRUTEFORCE_WAITING.equals(loginError)) {
                        parameters.put(LOGIN_WAIT, "true");
                    } else {
                        parameters.put(LOGIN_FAILED, "true");
                    }
                }

                String redirectUrl;
                String username = request.getParameter("username");
                if (username.contains("\n")) {
                    username = "";
                }

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                if (StringUtils.isNotBlank(username) && STServiceLocator.getSTUserService().isMigratedUser(username)) {
                    // Redirection vers la page de demande de nouveau mot de passe
                    redirectUrl = "reinitMdp?username=" + encodeValue(username);
                } else {
                    redirectUrl = path + "?" + PARAM_FAILED + "=true";
                    redirectUrl = URIUtils.addParametersToURIQuery(redirectUrl, parameters);
                }

                response.sendRedirect(
                    redirectUrl +
                    "&r=" +
                    encodeValue(
                        RandomStringUtils.random(
                            RANDOM_STRING_LENGTH,
                            RANDOM_STRING_MIN_INDEX,
                            RANDOM_STRING_MAX_INDEX,
                            true,
                            true,
                            null,
                            new SecureRandom()
                        )
                    )
                );
            }
        } catch (IOException | EncodingException e) {
            LOG.error(e);
            return false;
        }
        return true;
    }

    @Override
    public boolean onSuccess(HttpServletRequest request, HttpServletResponse response) {
        //Permet de ne pas être rediriger automatiquement sur le home et utiliser l'URL demandée initialement
        return false;
    }

    private String encodeValue(String value) throws EncodingException {
        return ESAPI.encoder().encodeForURL(value);
    }
}
