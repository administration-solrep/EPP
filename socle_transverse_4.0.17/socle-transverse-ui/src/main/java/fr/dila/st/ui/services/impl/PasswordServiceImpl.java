package fr.dila.st.ui.services.impl;

import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.DEFAULT;
import static fr.dila.st.ui.enums.STContextDataKey.IS_PASSWORD_RESET;
import static fr.dila.st.ui.helper.UserSessionHelper.getUserSessionParameter;
import static fr.dila.st.ui.helper.UserSessionHelper.putUserSessionParameter;
import static java.lang.String.format;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.PasswordService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.map.HashedMap;
import org.nuxeo.ecm.core.api.CoreSession;

public class PasswordServiceImpl implements PasswordService {
    private static final STLogger LOGGER = STLogFactory.getLog(PasswordServiceImpl.class);

    protected static STUserService getUserService() {
        return STServiceLocator.getSTUserService();
    }

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        // récupère le paramètre isPasswordReset depuis la session de l'utilisateur
        Boolean isPasswordReset = getUserSessionParameter(context, IS_PASSWORD_RESET.getName(), Boolean.class);

        if (isPasswordReset == null) {
            CoreSession session = context.getSession();
            String currentUsername = session.getPrincipal().getName();

            isPasswordReset = isUserPasswordResetNeeded(session, currentUsername);
        }

        Map<String, Object> map = new HashedMap<>();
        map.put(IS_PASSWORD_RESET.getName(), isPasswordReset);
        context.putInContextData(IS_PASSWORD_RESET, isPasswordReset);
        return map;
    }

    private static boolean isUserPasswordResetNeeded(CoreSession session, String username) {
        STProfilUtilisateurService profilUtilisateurService = STServiceLocator.getSTProfilUtilisateurService();
        STUserService userService = STServiceLocator.getSTUserService();

        // Vérification de la date de dernier changement de mot de passe par rapport au paramètre
        if (profilUtilisateurService.isUserPasswordOutdated(session, username)) {
            userService.forceChangeOutdatedPassword(username);
        }

        boolean userPasswordResetNeeded = userService.isUserPasswordResetNeeded(username);
        if (userPasswordResetNeeded) {
            LOGGER.info(DEFAULT, format("Le mot de passe de l'utilisateur [%s] a expiré", username));
        }
        return userPasswordResetNeeded;
    }

    @Override
    public boolean updatePassword(SpecificContext context, String newPassword) {
        String userId = context.getFromContextData(STContextDataKey.USER_ID);
        Objects.requireNonNull(userId, "Context data [USER_ID] doit être défini");

        if (getUserService().saveNewUserPassword(userId, newPassword) != null) {
            putUserSessionParameter(context, IS_PASSWORD_RESET.getName(), false);
            return true;
        }

        return false;
    }

    @Override
    public void askResetPassword(SpecificContext context) {
        String userId = context.getFromContextData(STContextDataKey.USER_ID);
        String userEmail = context.getFromContextData(STContextDataKey.USER_EMAIL);
        getUserService().askResetPassword(userId, userEmail);
    }
}
