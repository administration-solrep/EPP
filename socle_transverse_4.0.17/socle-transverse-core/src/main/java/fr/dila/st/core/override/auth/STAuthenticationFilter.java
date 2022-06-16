package fr.dila.st.core.override.auth;

import static fr.dila.st.core.service.STServiceLocator.getEtatApplicationService;
import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.util.ResourceHelper;
import java.io.IOException;
import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.ui.web.auth.NuxeoAuthenticationFilter;
import fr.dila.st.core.service.STServiceLocator;
import org.nuxeo.ecm.platform.api.login.UserIdentificationInfo;

public class STAuthenticationFilter extends NuxeoAuthenticationFilter {
    private static final Log LOG = LogFactory.getLog(STAuthenticationFilter.class);

    @Override
    protected boolean performAdditionalVerifications(Principal principal) {
        return (
            super.performAdditionalVerifications(principal) &&
            !getEtatApplicationService().isApplicationRestricted((NuxeoPrincipal) principal)
        );
    }

    @Override
    protected void buildForbiddenResponse(HttpServletResponse resp) {
        super.buildForbiddenResponse(resp);
        if (getRequiredService(EtatApplicationService.class).isApplicationRestricted()) {
            try {
                resp.getWriter().write(ResourceHelper.getString("login.auth.restricted"));
            } catch (IOException e) {
                LOG.error("Unable to write in HttpServletResponse", e);
            }
        }
    }

    @Override
    protected boolean logAuthenticationAttempt(UserIdentificationInfo userInfo, boolean success) {
        if (byPassAuthenticationLog) {
            return true;
        }
        String userName = userInfo.getUserName();
        if (userName == null || userName.length() == 0) {
            userName = userInfo.getToken();
        }

        String eventId;
        String comment;
        if (success) {
            eventId = "loginSuccess";
            comment = userName + " successfully logged in using " + userInfo.getAuthPluginName() + " authentication";
            LOG.info(comment);
            loginCount.inc();
        } else {
            eventId = "loginFailed";
            comment = userName + " failed to authenticate using " + userInfo.getAuthPluginName() + " authentication";
        }

        if (
                !STServiceLocator
                        .getEtatApplicationService()
                        .isApplicationRestricted(STServiceLocator.getUserManager().getPrincipal(userName))
        ) {
            return sendAuthenticationEvent(userInfo, eventId, comment);
        } else {
            return false;
        }
    }
}
