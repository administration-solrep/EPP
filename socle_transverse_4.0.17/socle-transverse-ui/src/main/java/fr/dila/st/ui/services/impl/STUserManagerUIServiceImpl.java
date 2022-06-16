package fr.dila.st.ui.services.impl;

import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.FAIL_SAVE_PWD_TEC;
import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.NOTIFICATION_PASSWORD_FONC;
import static fr.dila.st.core.service.STServiceLocator.getUserManager;
import static fr.dila.st.ui.enums.STContextDataKey.USER_ID;
import static fr.dila.st.ui.services.STUIServiceLocator.getPasswordService;
import static fr.dila.st.ui.services.STUIServiceLocator.getSTUtilisateursUIService;
import static org.apache.commons.collections4.CollectionUtils.isEqualCollection;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.actions.STUserManagerActionsDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.directory.BaseSession;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.exceptions.UserAlreadyExistsException;
import org.nuxeo.runtime.api.Framework;

public class STUserManagerUIServiceImpl implements STUserManagerUIService {
    public static final STLogger LOGGER = STLogFactory.getLog(STUserManagerUIService.class);

    protected boolean getCanCreateUsers(SpecificContext context) {
        if (Boolean.TRUE.equals(getUserManager().areUsersReadOnly())) {
            return false;
        }

        NuxeoPrincipal pal = context.getSession().getPrincipal();
        return (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_CREATOR));
    }

    protected boolean getCanEditUsers(SpecificContext context, boolean allowCurrentUser) {
        if (Boolean.TRUE.equals(getUserManager().areUsersReadOnly())) {
            return false;
        }
        NuxeoPrincipal pal = context.getSession().getPrincipal();
        if (pal.isAdministrator()) {
            return true;
        }
        DocumentModel selectedUser = context.getCurrentDocument();

        return allowCurrentUser && isSelf(pal, selectedUser);
    }

    protected boolean isSelf(NuxeoPrincipal pal, DocumentModel selectedUser) {
        return selectedUser != null && Objects.equals(pal.getName(), selectedUser.getId());
    }

    @Override
    public boolean getAllowEditUser(SpecificContext context) {
        DocumentModel selectedUser = context.getCurrentDocument();
        return getCanEditUsers(context, true) && !BaseSession.isReadOnlyEntry(selectedUser);
    }

    @Override
    public boolean getAllowChangePassword(SpecificContext context) {
        return getAllowEditUser(context);
    }

    @Override
    public boolean getAllowCreateUser(SpecificContext context) {
        return getCanCreateUsers(context);
    }

    @Override
    public boolean getAllowDeleteUser(SpecificContext context) {
        return getAllowEditUser(context);
    }

    @Override
    public boolean isNotReadOnly() {
        return !isReadOnly();
    }

    @Override
    public boolean isReadOnly() {
        return Boolean.parseBoolean(Framework.getProperty("org.nuxeo.ecm.webapp.readonly.mode", "false"));
    }

    @Override
    public void createUser(SpecificContext context) {
        UserForm userForm = context.getFromContextData(STContextDataKey.USER_FORM);
        Objects.requireNonNull(userForm);

        getSTUtilisateursUIService().validateUserForm(context);

        UserManager userManager = getUserManager();
        DocumentModel newUserDoc = userManager.getBareUserModel();
        getSTUtilisateursUIService().updateDocWithUserForm(newUserDoc.getAdapter(STUser.class), userForm);
        try {
            userManager.createUser(newUserDoc);
            if ("oui".equals(userForm.getTemporaire())) {
                context
                    .getMessageQueue()
                    .addToastSuccess(ResourceHelper.getString("admin.user.create.occasionnel.success"));
            } else {
                context
                    .getMessageQueue()
                    .addToastSuccess(ResourceHelper.getString("admin.user.create.success", userForm.getUtilisateur()));
            }
        } catch (UserAlreadyExistsException e) {
            LOGGER.error(STLogEnumImpl.FAIL_CREATE_LDAP_USER_TEC, e);
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("admin.user.already.exist"));
        }
    }

    @Override
    public void deleteUser(SpecificContext context) {
        SolonAlertManager alert = context.getMessageQueue();

        try {
            DocumentModel userDoc = context.getCurrentDocument();

            getUserManager().deleteUser(userDoc);
            alert.addSuccessToQueue("admin.user.delete.success");
        } catch (NuxeoException e) {
            LOGGER.error(context.getSession(), STLogEnumImpl.DEL_USER_TEC, e);
            alert.addErrorToQueue("Une erreur est survenue lors de la suppression de l'utilisateur");
        }
    }

    @Override
    public void updateUser(SpecificContext context) {
        SolonAlertManager alert = context.getMessageQueue();
        STUser user = context.getCurrentDocument().getAdapter(STUser.class);
        NuxeoPrincipal currentPrincipal = context.getSession().getPrincipal();
        UserForm userForm = context.getFromContextData(STContextDataKey.USER_FORM);
        checkProtectedPropertiesModification(currentPrincipal, user, userForm);
        STUIServiceLocator.getSTUtilisateursUIService().updateDocWithUserForm(user, userForm);

        try {
            DocumentModel userDoc = context.getCurrentDocument();

            getUserManager().updateUser(userDoc);

            alert.addSuccessToQueue("admin.user.update.success");
        } catch (NuxeoException e) {
            LOGGER.error(context.getSession(), STLogEnumImpl.UPDATE_USER_TEC, e);
            alert.addErrorToQueue("Une erreur est survenue lors de la modification de l'utilisateur");
        }
    }

    /**
     * Vérifie que l'utilisateur en cours a le droit de modifier les propriétés
     * protégés si les propriétés sont en cours de modifications
     * <p>
     * Lève une {@link STAuthorizationException} le cas échéant
     *
     * @param editor  L'utilisateur effectuant la modification
     * @param user le user en cours de modification
     * @param userForm le formulaire avec les valeurs à modifier
     */
    private void checkProtectedPropertiesModification(NuxeoPrincipal editor, STUser user, UserForm userForm) {
        if (!canUserModifyProtectedProperties(user, editor) && areProtectedPropertiesModified(user, userForm)) {
            throw new STAuthorizationException("La modification des propriétés protégées n'est pas permise");
        }
    }

    private static boolean areProtectedPropertiesModified(STUser user, UserForm userForm) {
        return (
            user.isTemporary() != userForm.isOccasionnel() ||
            !isEqualCollection(user.getGroups(), userForm.getProfils()) ||
            !isEqualCollection(user.getPostes(), userForm.getPostes()) ||
            !StringUtils.equals(SolonDateConverter.DATE_SLASH.format(user.getDateFin()), userForm.getDateFin())
        );
    }

    private boolean canUserModifyProtectedProperties(STUser oldUser, NuxeoPrincipal editor) {
        NuxeoPrincipal userPrinc = getUserManager().getPrincipal(oldUser.getUsername());

        return (
            PermissionHelper.isAdminFonctionnel(editor) ||
            (PermissionHelper.isAdminMinisteriel(editor) && inSameFonctionalGroup(editor, userPrinc))
        );
    }

    protected boolean inSameFonctionalGroup(NuxeoPrincipal user1, NuxeoPrincipal user2) {
        return true;
    }

    @Override
    public boolean isCurrentUserPermanent(NuxeoPrincipal principal) {
        DocumentModel userDoc = getUserManager().getUserModel(principal.getName());
        STUser stUser = userDoc.getAdapter(STUser.class);
        return stUser.isPermanent();
    }

    @Override
    public void initUserContext(SpecificContext context) {
        STUserManagerActionsDTO userActions = context.computeFromContextDataIfAbsent(
            STContextDataKey.USER_ACTIONS,
            k -> new STUserManagerActionsDTO()
        );

        String userId = context.getFromContextData(USER_ID);
        if (userId != null) {
            context.setCurrentDocument(getUserManager().getUserModel(userId));
            userActions.setIsEditUserAllowed(getAllowEditUser(context));
            userActions.setIsDeleteUserAllowed(getAllowDeleteUser(context));
        }

        initUserCreationContext(context);
    }

    @Override
    public void initUserCreationContext(SpecificContext context) {
        STUserManagerActionsDTO userActions = context.computeFromContextDataIfAbsent(
            STContextDataKey.USER_ACTIONS,
            k -> new STUserManagerActionsDTO()
        );
        userActions.setIsCreateUserAllowed(getAllowCreateUser(context));
    }

    @Override
    public void updatePassword(SpecificContext context) {
        if (context.getAction(STActionEnum.USER_EDIT_PASSWORD_CHANGE) == null) {
            throw new STAuthorizationException("action changement de mot de passe utilisateur courant");
        }

        SolonAlertManager alert = context.getMessageQueue();

        try {
            getPasswordService()
                .updatePassword(context, context.getFromContextData(STContextDataKey.NEW_USER_PASSWORD));
            alert.addSuccessToQueue(ResourceHelper.getString("reset.password.success"));
            UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, alert);
        } catch (STValidationException e) {
            alert.addErrorToQueue(e.getMessage());
            LOGGER.error(context.getSession(), FAIL_SAVE_PWD_TEC, context.getCurrentDocument(), e);
        }
    }

    @Override
    public void askResetPassword(SpecificContext context) {
        SolonAlertManager alert = context.getMessageQueue();

        try {
            getPasswordService().askResetPassword(context);
            alert.addSuccessToQueue(ResourceHelper.getString("ask.reset.password.success"));
            UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, alert);
        } catch (STValidationException exception) {
            alert.addErrorToQueue(exception.getMessage());
            LOGGER.error(context.getSession(), NOTIFICATION_PASSWORD_FONC, context.getCurrentDocument(), exception);
        }
    }
}
