package fr.dila.st.core.service;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STTokenService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.TokenPropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.UnboundEventContext;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.tokenauth.service.TokenAuthenticationService;
import org.nuxeo.runtime.api.Framework;

/**
 * Le service de gestion des token d'authentification.
 *
 * @author slefevre
 */
public class STTokenServiceImpl implements STTokenService {
    private static final STLogger LOGGER = STLogFactory.getLog(STTokenServiceImpl.class);

    private static final String TOKEN_DIR = "authTokens";
    private static final String EVENT_TOKEN_ACQUIRED = "accountServiceTokenAcquired";

    private static final class ValidityHolder {
        private static final String DEFAULT = "02:00";
        private static final String PROPERTY_VAL_REGEX = "[0-9]{2}:[0-9]{2}";
        private static final String EXPIRATION_HOUR;

        static {
            EXPIRATION_HOUR = retrievePropValue();
        }

        public static String getExpirationHour() {
            return EXPIRATION_HOUR;
        }

        private static String retrievePropValue() {
            final String value = Framework.getProperty("solon.ngedit.token.expiration.date");
            if (StringUtils.isNotBlank(value) && value.matches(PROPERTY_VAL_REGEX)) {
                return value;
            }
            LOGGER.warn(
                STLogEnumImpl.WARNING_TEC,
                String.format(
                    "La configuration solon.ngedit.token.expiration.date n'est pas définie ou est incorrecte. Utilisation de la valeur par défaut %s.",
                    DEFAULT
                )
            );
            return DEFAULT;
        }
    }

    @Override
    public String getUserByToken(String token) {
        DocumentModel tokenData = getTokenData(token);
        if (tokenData == null) {
            return null;
        }

        // On vérifie la validité des token
        Calendar limitCreationDate = lastExpirationDate();
        if (isExpiredToken(tokenData, limitCreationDate)) {
            // token trop vieux
            return null;
        }

        // renvoie l'utilisateur associé
        return TokenPropertyUtil.getUserName(tokenData);
    }

    private Calendar lastExpirationDate() {
        final String expirationHours = ValidityHolder.getExpirationHour();

        SimpleDateFormat defaultSdf = new SimpleDateFormat("hh:mm", Locale.FRANCE);
        Calendar date = Calendar.getInstance(Locale.FRANCE);
        try {
            date.setTime(defaultSdf.parse(expirationHours));
        } catch (ParseException e) {
            throw new NuxeoException("Impossible de définir la date d'expiration des token", e);
        }

        Calendar calexpiration = Calendar.getInstance(Locale.getDefault());
        calexpiration.set(Calendar.HOUR, date.get(Calendar.HOUR));
        calexpiration.set(Calendar.MINUTE, date.get(Calendar.MINUTE));

        Calendar now = Calendar.getInstance(Locale.getDefault());

        //Si la date d'expiration du jour n'est pas encore atteinte on renvoie celle de la veille
        if (now.before(calexpiration)) {
            calexpiration.add(Calendar.DAY_OF_MONTH, -1);
        }

        return calexpiration;
    }

    private boolean isExpiredToken(DocumentModel tokenData, Calendar expirationDate) {
        Calendar creationDate = TokenPropertyUtil.getCreationDate(tokenData);
        return (expirationDate != null && (creationDate == null || creationDate.before(expirationDate)));
    }

    @Override
    public String acquireToken(NuxeoPrincipal userAccount, String comment) {
        if (userAccount == null) {
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, "Génération token : échec, utilisateur vide");
            return null;
        }
        final String user = userAccount.getName();

        TokenAuthenticationService tokenService = ServiceUtil.getRequiredService(TokenAuthenticationService.class);
        String token = tokenService.acquireToken(user, "ngedit", user, comment, "-");

        //On vérifie que le token récupéré n'est pas expiré
        if (!user.equals(getUserByToken(token))) {
            //si le token est expiré on le revoke
            revokeToken(token);

            //Puis on génère un nouveau token
            token = tokenService.acquireToken(user, "ngedit", user, comment, "-");
            fireCreatedEvent(userAccount, token, comment);
        }

        LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, String.format("Token acquit [%s] pour [%s]", token, user));

        return token;
    }

    @Override
    public void revokeToken(String token) {
        TokenAuthenticationService tokenService = ServiceUtil.getRequiredService(TokenAuthenticationService.class);
        tokenService.revokeToken(token);
    }

    @Override
    public void purgeInvalidTokens() {
        final Calendar limitCreationDate = lastExpirationDate();

        int total = -1;
        int nbremoved = -1;

        final DirectoryService directoryService = ServiceUtil.getRequiredService(DirectoryService.class);
        Session session = null;
        try {
            session = directoryService.open(TOKEN_DIR);

            final Map<String, Serializable> filter = new HashMap<>();
            final DocumentModelList dml = session.query(filter);
            total = dml.size();

            Set<String> toremove = new HashSet<>();
            for (DocumentModel doc : dml) {
                if (isExpiredToken(doc, limitCreationDate)) {
                    toremove.add(TokenPropertyUtil.getToken(doc));
                }
            }
            for (String anid : toremove) {
                session.deleteEntry(anid);
            }
            nbremoved = toremove.size();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        LOGGER.info(
            STLogEnumImpl.LOG_INFO_TEC,
            String.format("Suppression de token : %d tokens supprimés sur un nombre total de %d ", nbremoved, total)
        );
    }

    private DocumentModel getTokenData(String token) {
        DocumentModel entry = getVocData(TOKEN_DIR, token);
        if (entry == null) {
            LOGGER.debug(STLogEnumImpl.LOG_DEBUG_TEC, String.format("Aucun utilisateur lié au token '%s'.", token));
        } else {
            LOGGER.debug(STLogEnumImpl.LOG_DEBUG_TEC, String.format("Utilisateur lié au token '%s' trouvé.", token));
        }
        return entry;
    }

    private DocumentModel getVocData(String voc, String id) {
        LoginContext lc = null;
        Session session = null;

        try {
            lc = login();
            final DirectoryService directoryService = ServiceUtil.getRequiredService(DirectoryService.class);
            session = directoryService.open(voc);

            return session.getEntry(id);
        } finally {
            if (session != null) {
                session.close();
            }
            logout(lc);
        }
    }

    private LoginContext login() {
        try {
            return Framework.login();
        } catch (LoginException e) {
            throw new NuxeoException("Impossible de se connecter avec l'utilisateur", e);
        }
    }

    private void logout(LoginContext lc) {
        try {
            if (lc != null) {
                lc.logout();
            }
        } catch (LoginException e) {
            throw new NuxeoException("Impossible de déconnecter l'utilisateur", e);
        }
    }

    private void fireCreatedEvent(final NuxeoPrincipal user, final String token, final String inputUsage) {
        String usage = ObjectHelper.requireNonNullElse(inputUsage, "");
        final String comment = String.format(
            "Nouveau token généré [%s] par [%s] pour l'usage [%s].",
            token,
            user.getName(),
            usage
        );

        final EventService evtService = ServiceUtil.getRequiredService(EventService.class);
        Map<String, Serializable> props = new HashMap<>();
        props.put("category", "NuxeoAuthentication");
        props.put("comment", comment);
        EventContext ctx = new UnboundEventContext(user, props);
        evtService.fireEvent(ctx.newEvent(EVENT_TOKEN_ACQUIRED));
    }
}
