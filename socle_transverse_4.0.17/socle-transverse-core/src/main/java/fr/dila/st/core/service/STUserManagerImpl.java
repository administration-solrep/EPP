package fr.dila.st.core.service;

import static fr.dila.st.core.service.STServiceLocator.getJournalService;
import static org.nuxeo.ecm.webengine.WebEngine.getActiveContext;
import static org.nuxeo.runtime.api.Framework.doPrivileged;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STUserManager;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.user.STPasswordHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelComparator;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.platform.computedgroups.UserManagerWithComputedGroups;
import org.nuxeo.ecm.platform.usermanager.VirtualUser;
import org.nuxeo.ecm.platform.usermanager.exceptions.UserAlreadyExistsException;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.runtime.api.Framework;

/**
 * Surcharge du gestionnaire d'utilisateurs de Nuxeo. Ce gestionnaire
 * d'utilisateur permet d'instancier les objets Principal spécifiques à notre
 * application.
 *
 * @author jtremeaux
 */
public class STUserManagerImpl extends UserManagerWithComputedGroups implements STUserManager {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -4946382095391343101L;

    private static final STLogger LOGGER = STLogFactory.getLog(STUserManagerImpl.class);

    /**
     * Default constructor
     */
    public STUserManagerImpl() {
        super();
    }

    private boolean checkValidInput(String username, String password) {
        if (username == null || password == null) {
            String message = ResourceHelper.getString("login.journal.passwordOrUsername.null");
            LOGGER.warn(STLogEnumImpl.FAIL_LOGIN_USER_TEC, message);
            return false;
        }

        // deal with anonymous user
        String anonymousUserId = getAnonymousUserId();
        if (username.equals(anonymousUserId)) {
            String message = ResourceHelper.getString("login.journal.anonymousUser.authenticate", anonymousUserId);
            LOGGER.warn(STLogEnumImpl.FAIL_LOGIN_USER_TEC, message);
            return false;
        }

        return true;
    }

    protected String getUserDirName() {
        String userDirName;
        // BBB backward compat for userDirectory + userAuthentication
        if ("userDirectory".equals(userDirectoryName) && dirService.getDirectory("userAuthentication") != null) {
            userDirName = "userAuthentication";
        } else {
            userDirName = userDirectoryName;
        }
        return userDirName;
    }

    @Override
    public boolean checkUsernamePassword(String username, String password) {
        if (checkValidInput(username, password)) {
            if (virtualUsers.containsKey(username)) {
                VirtualUser user = virtualUsers.get(username);
                String expected = user.getPassword();
                return password.equals(expected);
            }
            final DocumentModel userdoc = getPrivilegedUserModel(username, true);
            if (userdoc == null) {
                return false;
            }
            return isUserStillValid(userdoc) && isPasswordValid(userdoc, password) && !isUserDeleted(userdoc);
        }
        return false;
    }

    protected DocumentModel getPrivilegedUserModel(String username) {
        return getPrivilegedUserModel(username, false);
    }

    private DocumentModel getPrivilegedUserModel(String username, boolean full) {
        return Framework.doPrivileged(
            () -> {
                try (Session userDir = dirService.open(getUserDirectoryName())) {
                    if (full) {
                        userDir.setReadAllColumns(true);
                    }
                    return userDir.getEntry(username);
                }
            }
        );
    }

    private boolean isPasswordValid(DocumentModel userAuthenticating, String password) {
        STUser user = userAuthenticating.getAdapter(STUser.class);
        if (
            STPasswordHelper.verifyPassword(
                password,
                user.getSalt() == null ? null : user.getSalt().getBytes(Charsets.UTF_8),
                user.getPassword()
            )
        ) {
            return true;
        } else {
            String message = ResourceHelper.getString(
                "login.journal.password.incorrect",
                user.getFullNameWithUsername()
            );
            LOGGER.warn(STLogEnumImpl.FAIL_LOGIN_USER_TEC, message);
            return false;
        }
    }

    private boolean isUserStillValid(DocumentModel userdoc) {
        try {
            final STUser user = userdoc.getAdapter(STUser.class);
            final Calendar cal = user.getDateFin();
            if (cal == null || !cal.before(Calendar.getInstance())) {
                return true;
            } else {
                String message = ResourceHelper.getString(
                    "login.journal.password.expire",
                    user.getFullNameWithUsername()
                );
                LOGGER.warn(STLogEnumImpl.FAIL_LOGIN_USER_TEC, message);
                return false;
            }
        } catch (NuxeoException e) {
            LOGGER.error(STLogEnumImpl.FAIL_LOGIN_USER_TEC, e);
            return false;
        }
    }

    private boolean isUserDeleted(DocumentModel userdoc) {
        STUser user = userdoc.getAdapter(STUser.class);
        if (virtualUsers.containsKey(user.getUsername())) {
            return false;
        }
        if (user.isDeleted()) {
            String message = ResourceHelper.getString("login.journal.user.deleted", user.getFullNameWithUsername());
            LOGGER.warn(STLogEnumImpl.FAIL_LOGIN_USER_TEC, message);
            return true;
        }
        return false;
    }

    @Override
    public boolean isTechnicalUser(String userId) {
        // L'utilisateur qui exécute les batchs est un utilisateur technique
        final ConfigService configService = STServiceLocator.getConfigService();
        String batchUser = configService.getValue(STConfigConstants.NUXEO_BATCH_USER);
        return (batchUser == null || batchUser.equals(userId));
    }

    /**
     * Surcharge de la méthode nuxeo afin de notifier la création de
     * l'utilisateur dans les logs techniques.
     *
     * @param userModel
     * @return
     */
    @Override
    public DocumentModel createUser(final DocumentModel userModel) {
        // Chargement des services
        final STMailService stMailService = STServiceLocator.getSTMailService();
        final STUserService stUserService = STServiceLocator.getSTUserService();
        final JournalService journalService = STServiceLocator.getJournalService();

        try (Session userDir = dirService.open(userDirectoryName)) {
            String userId = getUserId(userModel);

            // check the user does not exist
            if (userDir.hasEntry(userId)) {
                throw new UserAlreadyExistsException();
            }

            String schema = dirService.getDirectorySchema(userDirectoryName);
            String clearUsername = (String) userModel.getProperty(schema, userDir.getIdField());
            String clearPassword = (String) userModel.getProperty(schema, userDir.getPasswordField());

            DocumentModel createdUserModel = doPrivileged(() -> userDir.createEntry(userModel));

            syncDigestAuthPassword(clearUsername, clearPassword);
            clearPassword = "";

            notifyUserChanged(userId, USERCREATED_EVENT_ID);

            Consumer<CoreSession> sendMail = session -> {
                try {
                    stMailService.sendMailUserPasswordCreation(
                        session,
                        userId,
                        stUserService.generateAndSaveNewUserPassword(userId)
                    );
                } catch (Exception e) {
                    String message = ResourceHelper.getString("journal.erreur.envoi.mail", userId);
                    LOGGER.error(STLogEnumImpl.FAIL_SEND_MAIL_TEC, e, message);
                    createdUserModel.putContextData(STConstant.MAIL_SEND_ERROR, true);
                }
            };

            WebContext activeContext = getActiveContext();
            if (activeContext != null && activeContext.getPrincipal() == null) {
                CoreSession activeSession = activeContext.getCoreSession();
                sendMail.accept(activeSession);
                // Event pour le journal seulement en cas d'un contexte WebEngine avec un principal
                final String comment = "Création d'un utilisateur [" + userId + "]";
                journalService.journaliserActionAdministration(activeSession, USERCREATED_EVENT_ID, comment);
            } else {
                try (CloseableCoreSession newSession = SessionUtil.openSession()) {
                    sendMail.accept(newSession);
                }
            }

            return createdUserModel;
        }
    }

    @Override
    public void updateUser(DocumentModel userModel) {
        hashPassword(userModel);

        Framework.doPrivileged(() -> super.updateUser(userModel));

        WebContext activeContext = getActiveContext();

        if (activeContext == null) {
            // no log since action is being carried out by system outside of the WebEngine context
            return;
        }

        // Event pour le journal
        String comment = String.format("Modification d'un utilisateur [%s]", getUserId(userModel));
        getJournalService()
            .journaliserActionAdministration(activeContext.getRequest().getUserPrincipal() != null ? activeContext.getCoreSession() : SessionUtil.openSession(), USERMODIFIED_EVENT_ID, comment);
    }

    @Override
    public String getUserId(DocumentModel userModel) {
        return super.getUserId(userModel);
    }

    @Override
    public void deleteUser(DocumentModel userModel) {
        final JournalService journalService = STServiceLocator.getJournalService();
        String userId = getUserId(userModel);

        try (Session userDir = dirService.open(userDirectoryName)) {
            STUser user = userModel.getAdapter(STUser.class);
            List<String> postes = user.getPostes();
            user.setDeleted(true);
            Framework.doPrivileged(() -> userDir.updateEntry(user.getDocument()));
            notifyUserChanged(userId, USERDELETED_EVENT_ID);

            // remove user from poste
            final STPostesService posteService = STServiceLocator.getSTPostesService();
            postes.forEach(poste -> posteService.removeUserFromPoste(poste, user.getUsername()));
        }

        WebContext activeContext = getActiveContext();

        if (activeContext == null || activeContext.getPrincipal() == null) {
            // no log since action is being carried out by system
            return;
        }

        // Event pour le journal
        String comment = "Suppression d'un utilisateur [" + userId + "]";
        journalService.journaliserActionAdministration(activeContext.getCoreSession(), USERDELETED_EVENT_ID, comment);
    }

    @Override
    public void deleteUser(String userId) {
        try (Session userDir = dirService.open(userDirectoryName)) {
            if (!userDir.hasEntry(userId)) {
                throw new DirectoryException("User does not exist: " + userId);
            }
            DocumentModel user = userDir.getEntry(userId);
            deleteUser(user);
        }
    }

    @Override
    public void physicalDeleteUser(DocumentModel userModel) {
        String userId = getUserId(userModel);
        physicalDeleteUser(userId);
    }

    @Override
    public void physicalDeleteUser(String userId) {
        try (Session userDir = dirService.open(userDirectoryName)) {
            if (!userDir.hasEntry(userId)) {
                throw new DirectoryException("User does not exist: " + userId);
            }
            userDir.deleteEntry(userId);

            notifyUserChanged(userId, USERDELETED_EVENT_ID);
        }
    }

    @Override
    public DocumentModelList searchUsers(String pattern) {
        DocumentModelList entries;

        if (StringUtils.isEmpty(pattern)) {
            entries = searchUsers(newSearchUserFilters(), null);
        } else {
            Map<String, DocumentModel> uniqueEntries = new HashMap<>();
            entries = new DocumentModelListImpl();

            for (Entry<String, MatchType> fieldEntry : userSearchFields.entrySet()) {
                Map<String, Serializable> filters = newSearchUserFilters();
                filters.put(fieldEntry.getKey(), pattern);
                Set<String> fulltext = fieldEntry.getValue() == MatchType.SUBSTRING
                    ? new HashSet<>(filters.keySet())
                    : null;

                searchUsers(filters, fulltext).forEach(entry -> uniqueEntries.put(entry.getId(), entry));
            }
            String message = ResourceHelper.getString("journal.search.user.found", uniqueEntries.size());
            LOGGER.debug(STLogEnumImpl.DEFAULT, message);
            entries.addAll(uniqueEntries.values());
        }

        // sort
        Collections.sort(entries, new DocumentModelComparator(userSchemaName, getUserSortMap()));

        return entries;
    }

    private Map<String, Serializable> newSearchUserFilters() {
        Map<String, Serializable> filter = new HashMap<>();
        filter.put("deleted", "FALSE");
        return filter;
    }

    private DocumentModel hashPassword(DocumentModel userModel) {
        try (Session userDir = dirService.open(userDirectoryName)) {
            String schema = dirService.getDirectorySchema(userDirectoryName);
            //Si on n'a pas modifié le MDP le mdp est toujours null
            String password = (String) userModel.getProperty(schema, userDir.getPasswordField());

            if (password != null && !STPasswordHelper.isHashed(password)) {
                byte[] salt = STPasswordHelper.generateSalt();
                password = STPasswordHelper.hashPassword(password, salt);
                userModel.setProperty(schema, userDir.getPasswordField(), password);
                userModel.setProperty(schema, STSchemaConstant.USER_SALT, new String(salt));
            }
            password = "";
        }
        return userModel;
    }

    @Override
    public void invalidateAllPrincipals() {
        if (useCache()) {
            principalCache.invalidateAll();
        }
    }
}
