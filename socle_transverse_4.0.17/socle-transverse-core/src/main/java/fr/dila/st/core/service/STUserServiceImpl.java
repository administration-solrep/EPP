package fr.dila.st.core.service;

import static fr.dila.st.api.constant.STSchemaConstant.USER_DELETED;
import static fr.dila.st.core.service.STServiceLocator.getUserManager;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.nuxeo.ecm.core.query.sql.model.Operator.AND;
import static org.nuxeo.ecm.core.query.sql.model.Operator.OR;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.exception.UserNotFoundException;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.STPersistanceService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.user.STPasswordHelper;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.query.sql.model.MultiExpression;
import org.nuxeo.ecm.core.query.sql.model.OrderByExpr;
import org.nuxeo.ecm.core.query.sql.model.Predicate;
import org.nuxeo.ecm.core.query.sql.model.Predicates;
import org.nuxeo.ecm.core.query.sql.model.QueryBuilder;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.UserManagerImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.api.login.LoginComponent;

/**
 * @author ARN
 */
public class STUserServiceImpl implements STUserService {
    private static final long serialVersionUID = 7475407168269459198L;

    private static final STLogger LOGGER = STLogFactory.getLog(STUserServiceImpl.class);

    private static final String ERROR_GET_INFO_USER = "erreur lors de la récupération d'information sur l'utilisateur ";

    private static final String ALPHA_MIN_LIST = "abcdefghijklmonpqrstuvwxyz";
    private static final String ALPHA_MAJ_LIST = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUM_LIST = "0123456789";
    private static final String CARAC_SPE_LIST = "$?!:;()[]{}+-=_\\/|*,.";

    private static final String POSTE_INCONNU = "**poste inconnu**";
    private static final String MINSTERE_INCONNU = "**ministere inconnu**";

    public static final String CIVILITE_ABREGEE_PRENOM_NOM_TEL_MAIL = "c p n t m";

    public static final int PASSWORD_MIN_LENGTH = 12;

    /**
     * Default constructor
     */
    public STUserServiceImpl() {
        // do nothing
    }

    @Override
    public String getUserFullNameAndCivilite(String userLogin) {
        if (isSystem(userLogin)) {
            return userLogin;
        }
        String fullNameAndCivilite = userLogin;
        final UserManager userManager = STServiceLocator.getUserManager();
        DocumentModel userModel;
        try {
            userModel = userManager.getUserModel(fullNameAndCivilite);
            fullNameAndCivilite = getUserFullNameAndCivilite(userModel);
        } catch (Exception exc) {
            LOGGER.warn(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, ERROR_GET_INFO_USER + userLogin);
            LOGGER.debug(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, exc);
            return userLogin;
        }
        return fullNameAndCivilite;
    }

    @Override
    public String getUserFullNameAndCivilite(DocumentModel userModel) {
        // récupération des inforamtions sur l'utilisateur
        STUser user = userModel.getAdapter(STUser.class);

        String lastName = user.getLastName();
        if (lastName == null || lastName.isEmpty()) {
            return userModel.getId();
        }
        StringBuilder fullName = new StringBuilder();
        // ajout de la civilite
        String civilite = user.getTitle();
        if (civilite != null && !civilite.isEmpty()) {
            fullName.append(civilite);
        }

        // ajout du nom complet
        fullName.append(" ").append(user.getReversedFullName());

        return fullName.toString();
    }

    @Override
    public String getUserFullName(String userLogin) {
        return getUserInfoOrUsername(userLogin, this::getUserFullName);
    }

    @Override
    public String getUserFullNameOrEmpty(String userLogin) {
        return ofNullable(userLogin)
            .map(getUserManager()::getUserModel)
            .map(u -> u.getAdapter(STUser.class))
            .map(STUser::getFullNameOrEmpty)
            .orElse("");
    }

    @Override
    public String getUserFullName(DocumentModel userModel) {
        // récupération des inforamtions sur l'utilisateur
        STUser user = userModel.getAdapter(STUser.class);
        return user.getFullName();
    }

    @Override
    public String getLegacyUserFullName(String userLogin) {
        return getUserInfoOrUsername(userLogin, this::getLegacyUserFullName);
    }

    private String getLegacyUserFullName(DocumentModel userModel) {
        // récupération des informations sur l'utilisateur
        STUser user = userModel.getAdapter(STUser.class);
        String lastName = user.getLastName();
        if (StringUtils.isBlank(lastName)) {
            return userModel.getId();
        }

        return StringUtils.defaultIfBlank(user.getFirstName(), StringUtils.EMPTY) + StringUtils.SPACE + lastName;
    }

    @Override
    public String getUserFullNameWithUsername(String userLogin) {
        return getUserInfoOrUsername(
            userLogin,
            userDoc -> {
                STUser user = userDoc.getAdapter(STUser.class);
                return user.getFullNameWithUsername();
            }
        );
    }

    private static String getUserInfoOrUsername(String username, Function<DocumentModel, String> getUserInfo) {
        if (isBlank(username)) {
            return null;
        }

        if (isSystem(username)) {
            return username;
        }

        try {
            return Optional
                .ofNullable(STServiceLocator.getUserManager().getUserModel(username))
                .map(getUserInfo)
                .orElse(username);
        } catch (Exception exc) {
            LOGGER.warn(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, ERROR_GET_INFO_USER + username);
            LOGGER.debug(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, exc);
            return username;
        }
    }

    @Override
    public String generateAndSaveNewUserPassword(String userId) {
        return saveNewUserPassword(userId, getRandomSecurePassword(), true);
    }

    @Override
    public String saveNewUserPassword(String userId, String newPassword) {
        return saveNewUserPassword(userId, newPassword, false);
    }

    private String saveNewUserPassword(String userId, String newPassword, boolean passwordReset) {
        UserManager userManager = STServiceLocator.getUserManager();
        STPersistanceService persistanceService = STServiceLocator.getSTPersistanceService();

        validatePassword(userManager, persistanceService, userId, newPassword);

        STUser user = getFullPrivilegedUser(userManager, userId);

        if (user.getPassword() != null && user.getSalt() != null) {
            persistanceService.saveCurrentPassword(user.getPassword(), user.getUsername(), user.getSalt().getBytes());
        }

        byte[] salt = STPasswordHelper.generateSalt();
        String hashedPassword = STPasswordHelper.hashPassword(newPassword, salt);

        user.setSalt(new String(salt));
        user.setPassword(hashedPassword);
        user.setPwdReset(passwordReset);

        try (CloseableCoreSession coreSession = SessionUtil.openSession()) {
            STServiceLocator.getSTProfilUtilisateurService().changeDatePassword(coreSession, user.getUsername());
        }

        userManager.updateUser(user.getDocument());

        STServiceLocator.getBruteforceSecurityService().deleteLoginInfos(user.getUsername());

        return newPassword;
    }

    @Override
    public Optional<STUser> getOptionalUser(String username) {
        return ofNullable(getUserManager().getUserModel(username)).map(u -> u.getAdapter(STUser.class));
    }

    @Override
    public STUser getUser(String username) {
        return getOptionalUser(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    /**
     * Get user with password
     */
    private STUser getFullPrivilegedUser(UserManager userManager, String userId) {
        return getPrivilegedUser(userManager, userId, true);
    }

    /**
     * Get a user using system privileges
     *
     * @param userManager
     *            UserManager
     * @param userId
     *            username
     * @param full
     *            if true, read all columns, including password
     * @return user
     */
    private STUser getPrivilegedUser(UserManager userManager, String userId, boolean full) {
        return Framework.doPrivileged(
            () -> {
                DirectoryService directoryService = ServiceUtil.getRequiredService(DirectoryService.class);
                try (Session userDir = directoryService.open(userManager.getUserDirectoryName())) {
                    if (full) {
                        userDir.setReadAllColumns(true);
                    }
                    DocumentModel userDoc = userDir.getEntry(userId);
                    if (userDoc == null) {
                        throw new UserNotFoundException(userId);
                    }
                    return userDoc.getAdapter(STUser.class);
                }
            }
        );
    }

    private static void validatePassword(
        UserManager userManager,
        STPersistanceService persistanceService,
        String userId,
        String password
    ) {
        if (isBlank(password)) {
            throw new STValidationException("form.validation.password.notblank");
        }

        if (!userManager.validatePassword(password)) {
            throw new STValidationException("form.validation.password.invalid");
        }

        if (userManager.checkUsernamePassword(userId, password)) {
            throw new STValidationException("form.validation.password.notcurrent");
        }

        if (persistanceService.checkPasswordHistory(password, userId)) {
            throw new STValidationException("form.validation.password.nothistorical");
        }

        if (password.contains(userId)) {
            throw new STValidationException("form.validation.password.notusername");
        }
    }

    protected static String getRandomSecurePassword() {
        SecureRandom rand = new SecureRandom();
        List<Character> chars = Stream
            .of(
                ALPHA_MIN_LIST.charAt(rand.nextInt(ALPHA_MIN_LIST.length())),
                ALPHA_MIN_LIST.charAt(rand.nextInt(ALPHA_MIN_LIST.length())),
                ALPHA_MIN_LIST.charAt(rand.nextInt(ALPHA_MIN_LIST.length())),
                ALPHA_MAJ_LIST.charAt(rand.nextInt(ALPHA_MAJ_LIST.length())),
                ALPHA_MAJ_LIST.charAt(rand.nextInt(ALPHA_MAJ_LIST.length())),
                ALPHA_MAJ_LIST.charAt(rand.nextInt(ALPHA_MAJ_LIST.length())),
                CARAC_SPE_LIST.charAt(rand.nextInt(CARAC_SPE_LIST.length())),
                CARAC_SPE_LIST.charAt(rand.nextInt(CARAC_SPE_LIST.length())),
                CARAC_SPE_LIST.charAt(rand.nextInt(CARAC_SPE_LIST.length())),
                NUM_LIST.charAt(rand.nextInt(NUM_LIST.length())),
                NUM_LIST.charAt(rand.nextInt(NUM_LIST.length())),
                NUM_LIST.charAt(rand.nextInt(NUM_LIST.length()))
            )
            .collect(Collectors.toList());

        Collections.shuffle(chars);
        StringBuilder randomValue = new StringBuilder(PASSWORD_MIN_LENGTH);
        for (char c : chars) {
            randomValue.append(c);
        }
        // clear password from collection
        chars.clear();

        return randomValue.toString();
    }

    @Override
    public boolean isUserPasswordResetNeeded(String userLogin) {
        boolean rstPassword = false;
        final UserManager userManager = STServiceLocator.getUserManager();
        DocumentModel userModel;
        userModel = userManager.getUserModel(userLogin);
        STUser user = userModel.getAdapter(STUser.class);
        rstPassword = user.isPwdReset();
        return rstPassword;
    }

    @Override
    public void forceChangeOutdatedPassword(String userLogin) {
        final UserManager userManager = STServiceLocator.getUserManager();
        DocumentModel userModel;
        userModel = userManager.getUserModel(userLogin);
        STUser user = userModel.getAdapter(STUser.class);
        // Force la nécessité d'un changement de mot de passe
        user.setPwdReset(true);
        userManager.updateUser(user.getDocument());
    }

    @Override
    public void askResetPassword(String userLogin, String email) {
        Framework.doPrivileged(
            () -> {
                if (StringUtils.isBlank(userLogin)) {
                    throw new STValidationException("form.validation.username.notblank");
                }
                DocumentModel doc = STServiceLocator.getUserManager().getUserModel(userLogin);

                if (doc == null) {
                    throw new STValidationException("form.validation.username.invalid");
                }

                STUser user = doc.getAdapter(STUser.class);

                if (email != null && email.equalsIgnoreCase(user.getEmail())) {
                    try (CloseableCoreSession coreSession = SessionUtil.openSession()) {
                        STServiceLocator
                            .getSTMailService()
                            .sendMailResetPassword(coreSession, userLogin, generateAndSaveNewUserPassword(userLogin));
                    }
                } else {
                    throw new STValidationException("form.validation.username.invalid"); // Message d'erreur peu spécifique pour des raisons de sécurité
                }
            }
        );
    }

    @Override
    public String getUserInfo(String userLogin, String format) {
        String fullName = userLogin;
        final UserManager userManager = STServiceLocator.getUserManager();
        DocumentModel userModel;
        try {
            userModel = userManager.getUserModel(fullName);
            fullName = getUserInfo(userModel, format);
        } catch (NuxeoException exc) {
            LOGGER.warn(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, ERROR_GET_INFO_USER + userLogin);
            LOGGER.debug(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, exc);
            return userLogin;
        }
        return fullName != null ? fullName : userLogin;
    }

    @Override
    public String getUserInfo(DocumentModel userModel, String format) {
        String userName = null;

        if (userModel == null) {
            return userName;
        } else {
            if (format == null || format.isEmpty()) {
                userName = CIVILITE_ABREGEE_PRENOM_NOM_TEL_MAIL;
            } else {
                userName = format;
            }
            STUser user = userModel.getAdapter(STUser.class);
            String title = user.getTitle() == null ? "" : user.getTitle();
            String first = user.getFirstName() == null ? "" : user.getFirstName();
            String last = user.getLastName() == null ? "" : user.getLastName();
            String phone = user.getTelephoneNumber() == null ? "" : user.getTelephoneNumber();
            String mail = user.getEmail() == null ? "" : user.getEmail();

            userName = userName.replace("c", "{c}");
            userName = userName.replace("C", "{C}");
            userName = userName.replace("p", "{p}");
            userName = userName.replace("n", "{n}");
            userName = userName.replace("t", "{t}");
            userName = userName.replace("m", "{m}");

            userName = userName.replace("{c}", title);
            if (userName.contains("{C}")) {
                userName = userName.replace("{C}", title);
                userName = userName.replace("Monsieur", "M.");
                userName = userName.replace("Madame", "Mme.");
            }
            userName = userName.replace("{p}", first);
            userName = userName.replace("{n}", last);
            userName = userName.replace("{t}", phone);
            userName = userName.replace("{m}", mail);

            return userName.trim();
        }
    }

    @Override
    public String getUserProfils(String userId) {
        try {
            final UserManager userManager = STServiceLocator.getUserManager();
            if (userId == null || userId.isEmpty() || userManager == null) {
                return null;
            }
            // récupération de l'utilisateur
            DocumentModel userModel = userManager.getUserModel(userId);
            if (userModel == null) {
                return null;
            }
            STUser stUserModel = userModel.getAdapter(STUser.class);
            if (stUserModel == null) {
                return null;
            }
            // récupération des identifiants de postes
            List<String> postesIds = stUserModel.getPostes();
            if (postesIds == null || postesIds.isEmpty()) {
                return "";
            }
            // récupérations des labels des postes
            List<String> listPosteLabels = new ArrayList<>();

            List<PosteNode> listNode = STServiceLocator.getSTPostesService().getPostesNodes(postesIds);
            for (OrganigrammeNode node : listNode) {
                // récpération du label du poste
                if (node.getLabel() != null) {
                    listPosteLabels.add(node.getLabel());
                }
            }
            // affichage de la liste des postes
            if (listPosteLabels.isEmpty()) {
                return "";
            } else {
                return StringUtils.join(listPosteLabels, ", ");
            }
        } catch (NuxeoException exc) {
            LOGGER.warn(null, STLogEnumImpl.FAIL_GET_POSTE_TEC, userId);
            LOGGER.debug(null, STLogEnumImpl.FAIL_GET_POSTE_TEC, exc);
            return POSTE_INCONNU;
        }
    }

    @Override
    public List<Address> getAddressFromUserList(List<STUser> recipients) {
        List<Address> emailsAdresses = new ArrayList<>();
        if (recipients == null || recipients.isEmpty()) {
            return emailsAdresses;
        }

        // récupération des adresses mails
        for (STUser stUser : recipients) {
            if (StringUtils.isNotBlank(stUser.getEmail())) {
                try {
                    emailsAdresses.add(new InternetAddress(stUser.getEmail(), stUser.getFullName()));
                } catch (UnsupportedEncodingException exc) {
                    LOGGER.error(null, STLogEnumImpl.FAIL_GET_MAIL_TEC, exc);
                }
            }
        }
        return emailsAdresses;
    }

    /**
     * Retourne la liste des adresses email sous forme de texte.
     *
     * @param addressList
     *            Liste d'adresses email
     * @return Liste des adresses email sous forme de texte
     */
    protected List<String> fromAddressToString(List<Address> addressList) {
        List<String> recipients = new ArrayList<>();
        for (Address address : addressList) {
            recipients.add(address.toString());
        }
        return recipients;
    }

    @Override
    public List<String> getEmailAddressFromUserList(List<STUser> recipients) {
        List<Address> addressList = getAddressFromUserList(recipients);
        return fromAddressToString(addressList);
    }

    private static boolean isSystem(final String userId) {
        return LoginComponent.SYSTEM_USERNAME.equals(userId);
    }

    @Override
    public String getUserMinisteres(String userId) {
        List<PosteNode> posteList = STServiceLocator.getSTPostesService().getAllPostesForUser(userId);
        List<String> ministeres = new ArrayList<>();
        if (posteList != null && !posteList.isEmpty()) {
            for (PosteNode poste : posteList) {
                List<EntiteNode> entiteNodeList = STServiceLocator.getSTMinisteresService().getMinisteresParents(poste);
                if (entiteNodeList != null && !entiteNodeList.isEmpty()) {
                    EntiteNode entiteNode = entiteNodeList.get(0);
                    if (!ministeres.contains(entiteNode.getLabel())) {
                        ministeres.add(entiteNode.getLabel());
                    }
                }
            }
        }

        if (ministeres.isEmpty()) {
            return MINSTERE_INCONNU;
        }
        // On concatène ensuite la liste de ministères
        return StringUtils.join(ministeres, ", ");
    }

    @Override
    public String getAllDirectionsRattachement(String userId) {
        List<PosteNode> posteListNode = STServiceLocator.getSTPostesService().getAllPostesForUser(userId);
        List<String> directions = new ArrayList<>();

        if (posteListNode != null && !posteListNode.isEmpty()) {
            for (PosteNode posteNode : posteListNode) {
                String posteId = posteNode.getId();
                List<OrganigrammeNode> dirNodeList = STServiceLocator
                    .getSTUsAndDirectionService()
                    .getDirectionFromPoste(posteId);
                if (dirNodeList != null && !dirNodeList.isEmpty()) {
                    OrganigrammeNode dirNode = dirNodeList.get(0);
                    if (!directions.contains(dirNode.getLabel())) {
                        directions.add(dirNode.getLabel());
                    }
                }
            }
        }
        return StringUtils.join(directions, ", ");
    }

    @Override
    public String getUserPostes(String userId) {
        List<PosteNode> posteListNode = STServiceLocator.getSTPostesService().getAllPostesForUser(userId);
        Set<String> postes = new HashSet<>();

        if (posteListNode != null && !posteListNode.isEmpty()) {
            for (PosteNode posteNode : posteListNode) {
                postes.add(posteNode.getLabel());
            }
        }
        return StringUtils.join(postes, ", ");
    }

    @Override
    public List<String> getAllUserMinisteresId(String userId) {
        List<PosteNode> posteList = STServiceLocator.getSTPostesService().getAllPostesForUser(userId);
        List<String> ministeres = new ArrayList<>();
        if (posteList != null && !posteList.isEmpty()) {
            for (PosteNode poste : posteList) {
                List<EntiteNode> entiteNodeList = STServiceLocator.getSTMinisteresService().getMinisteresParents(poste);
                if (entiteNodeList != null && !entiteNodeList.isEmpty()) {
                    EntiteNode entiteNode = entiteNodeList.get(0);
                    if (!ministeres.contains(entiteNode.getId())) {
                        ministeres.add(entiteNode.getId());
                    }
                }
            }
        }
        return ministeres;
    }

    @Override
    public void clearUserFromCache(String username) {
        UserManager userManager = STServiceLocator.getUserManager();
        userManager.notifyUserChanged(username, UserManagerImpl.INVALIDATE_PRINCIPAL_EVENT_ID);
    }

    @Override
    public boolean isMigratedUser(String username) {
        if (StringUtils.isNotBlank(username)) {
            try {
                STUser user = getFullPrivilegedUser(STServiceLocator.getUserManager(), username);
                return StringUtils.isEmpty(user.getPassword()) && StringUtils.isEmpty(user.getSalt());
            } catch (UserNotFoundException e) {
                // Utilisateur non existant => il n'a pas été migré
                LOGGER.warn(STLogEnumImpl.FAIL_GET_USER_TEC, e.getMessage());
                LOGGER.debug(STLogEnumImpl.FAIL_GET_USER_TEC, e);
                return false;
            }
        }

        return false;
    }

    @Override
    public List<STUser> getActiveUsers(Predicate... predicates) {
        return getActiveUserDocs(null, predicates).stream().map(d -> d.getAdapter(STUser.class)).collect(toList());
    }

    @Override
    public List<STUser> getActiveUsers(List<OrderByExpr> orderExps, Predicate... predicates) {
        return getActiveUserDocs(orderExps, predicates).stream().map(d -> d.getAdapter(STUser.class)).collect(toList());
    }

    @Override
    public List<DocumentModel> getActiveUserDocs(Predicate... predicates) {
        return getActiveUserDocs(null, predicates);
    }

    @Override
    public List<DocumentModel> getActiveUserDocs(List<OrderByExpr> orderExps, Predicate... predicates) {
        List<Predicate> preds = new ArrayList<>();

        // deleted predicate
        Predicate deleted = Predicates.eq(USER_DELETED, STConstant.FALSE);
        preds.add(deleted);

        // dateFin predicate : is null or > now
        Predicate dateFinIsNull = Predicates.isnull(STSchemaConstant.USER_DATE_FIN);
        Date now = Calendar.getInstance().getTime();
        Predicate dateFinGt = Predicates.gt(STSchemaConstant.USER_DATE_FIN, now);
        MultiExpression dateFinExpr = new MultiExpression(OR, asList(dateFinIsNull, dateFinGt));
        preds.add(dateFinExpr);

        // dateDebut predicate
        Predicate dateDebutPred = Predicates.lt(STSchemaConstant.USER_DATE_DEBUT, now);
        preds.add(dateDebutPred);

        // additional predicates
        if (ArrayUtils.isNotEmpty(predicates)) {
            preds.addAll(asList(predicates));
        }

        QueryBuilder qb = new QueryBuilder().predicate(new MultiExpression(AND, preds));
        if (orderExps != null) {
            qb.orders(orderExps);
        }
        return getUserManager().searchUsers(qb);
    }

    @Override
    public List<String> getActiveUsernames(Predicate... predicates) {
        return getActiveUsernames(null, predicates);
    }

    @Override
    public List<String> getActiveUsernames(List<OrderByExpr> orderExps, Predicate... predicates) {
        return getActiveUserDocs(orderExps, predicates).stream().map(DocumentModel::getId).collect(toList());
    }

    @Override
    public Map<String, Calendar> getMapUsernameDateDerniereConnexion(List<DocumentModel> usersDocs) {
        return usersDocs
            .stream()
            .map(user -> user.getAdapter(STUser.class))
            .filter(user -> user.getDateDerniereConnexion() != null)
            .collect(Collectors.toMap(STUser::getUsername, STUser::getDateDerniereConnexion));
    }
}
