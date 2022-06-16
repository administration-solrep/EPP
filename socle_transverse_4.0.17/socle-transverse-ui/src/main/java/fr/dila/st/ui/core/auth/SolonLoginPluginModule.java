package fr.dila.st.ui.core.auth;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.api.login.UserIdentificationInfo;
import org.nuxeo.ecm.platform.login.BaseLoginModule;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Refuse les utilisateurs administrator
 * @author SLE
 *
 */
public class SolonLoginPluginModule extends BaseLoginModule {

    public SolonLoginPluginModule() {
        // do nothing
    }

    @Override
    public Boolean initLoginModule() {
        return true;
    }

    @Override
    public String validatedUserIdentity(UserIdentificationInfo userIdent) {
        final String userName = userIdent.getUserName().trim();

        UserManager manager = ServiceUtil.getRequiredService(UserManager.class);

        NuxeoPrincipal principal = Framework.doPrivileged(() -> manager.getPrincipal(userName));

        //Si le compte existe le mot de passe est correct et n'est pas administrator alors on retourne l'utilisateur => l'utilisateur a le droit de se connecter
        if (
            principal != null &&
            manager.checkUsernamePassword(userName, userIdent.getPassword()) &&
            !principal.isAdministrator()
        ) {
            Map<String, Object> mapEtatApp = STUIServiceLocator
                .getEtatApplicationUIService()
                .getEtatApplicationDocumentUnrestricted();
            if (
                BooleanUtils.isTrue((Boolean) mapEtatApp.get(EtatApplicationService.RESTRICTION_ACCESS)) &&
                !principal.isMemberOf(STBaseFunctionConstant.ADMIN_ACCESS_UNRESTRICTED)
            ) {
                return null;
            }

            return userName;
        }
        return null;
    }
}
