package fr.dila.ss.core.override.auth;

import static fr.dila.st.core.service.STServiceLocator.getUserManager;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.ERROR_AUTHENTICATION_FAILED;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_ERROR;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.core.override.auth.STAuthenticationFilter;
import fr.dila.st.core.util.ResourceHelper;
import java.security.Principal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.ui.web.auth.CachableUserIdentificationInfo;
import org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants;
import org.nuxeo.runtime.api.Framework;

public class SSAuthenticationFilter extends STAuthenticationFilter {

    @Override
    protected Principal doAuthenticate(
        CachableUserIdentificationInfo cachableUserIdent,
        HttpServletRequest httpRequest
    ) {
        String userName = cachableUserIdent.getUserInfo().getUserName();
        NuxeoPrincipal principal = Framework.doPrivileged(() -> getUserManager().getPrincipal(userName));
        if (
            principal != null &&
            !principal.isAdministrator() &&
            !principal.isMemberOf(STBaseFunctionConstant.INTERFACE_ACCESS)
        ) {
            httpRequest.setAttribute(LOGIN_ERROR, ERROR_AUTHENTICATION_FAILED);
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                session.setAttribute(
                    NXAuthConstants.LOGIN_ERROR_MESSAGE,
                    ResourceHelper.getString("login.interface.access.denied")
                );
                return null;
            }
        }

        return super.doAuthenticate(cachableUserIdent, httpRequest);
    }
}
