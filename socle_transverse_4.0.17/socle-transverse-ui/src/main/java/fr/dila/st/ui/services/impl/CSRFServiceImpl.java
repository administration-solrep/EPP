package fr.dila.st.ui.services.impl;

import fr.dila.st.ui.services.CSRFService;
import java.security.SecureRandom;
import java.util.Random;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.nuxeo.ecm.webengine.model.WebContext;

public class CSRFServiceImpl implements CSRFService {
    /**
     * Session attribute in which token is stored.
     *
     * @since 10.3
     */
    public static final String CSRF_TOKEN_ATTRIBUTE = "NuxeoCSRFToken";

    private static final int CSRF_TOKEN_LENGTH = 40;

    protected static final Random RANDOM = new SecureRandom();

    @Override
    public String generateToken(WebContext ctx) {
        //Si la session n'existe pas on la crée
        HttpSession session = ctx.getRequest().getSession(true);
        String token = (String) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        if (token == null) {
            token = generateNewToken();
            ctx.getRequest().getSession().setAttribute(CSRF_TOKEN_ATTRIBUTE, token);
        }
        return token;
    }

    protected String generateNewToken() {
        return RandomStringUtils.random(CSRF_TOKEN_LENGTH, 0, 0, true, true, null, RANDOM);
    }
}
