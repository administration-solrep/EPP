package fr.dila.st.core.service;

import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.api.constant.STQueryConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.user.STProfilUtilisateur;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.WorkspaceNotFoundException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.core.util.UnrestrictedQueryRunner;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.time.DateUtils;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;

public abstract class AbstractSTProfilUtilisateurServiceImpl<T extends STProfilUtilisateur>
    implements STProfilUtilisateurService<T> {
    private static final STLogger LOGGER = STLogFactory.getLog(AbstractSTProfilUtilisateurServiceImpl.class);

    private static final String QUERY_WORKSPACE =
        "SELECT * FROM " +
        STProfilUtilisateurConstants.WORKSPACE_DOCUMENT_TYPE +
        STQueryConstant.WHERE +
        STSchemaConstant.ECM_NAME_XPATH +
        " = '%s' AND " +
        STSchemaConstant.ECM_ISPROXY_XPATH +
        " = 0";

    /**
     * Default constructor
     */
    public AbstractSTProfilUtilisateurServiceImpl() {
        // do nothing
    }

    @Override
    public T getProfilUtilisateur(CoreSession session, String username) {
        DocumentModel profilUtilisateurDoc = getProfilUtilisateurDoc(session, username);
        if (profilUtilisateurDoc != null) {
            return adapt(profilUtilisateurDoc);
        }
        return null;
    }

    @Override
    public DocumentModel getProfilUtilisateurDoc(CoreSession session, String username) {
        DocumentModel profilUtilisateurDoc = null;
        try {
            profilUtilisateurDoc =
                getProfilUtilisateurDocFromWorkspace(session, getUserWorkspaceDoc(session, username));
        } catch (WorkspaceNotFoundException we) {
            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we.getMessage());
            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we);
        }
        return profilUtilisateurDoc;
    }

    @Override
    public T getProfilUtilisateurForCurrentUser(CoreSession session) {
        DocumentModel profilUtilisateurDoc = null;
        try {
            profilUtilisateurDoc =
                getProfilUtilisateurDocFromWorkspace(session, getUserWorkspaceDocForCurrentUser(session));
        } catch (WorkspaceNotFoundException we) {
            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we.getMessage());
            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we);
        }
        if (profilUtilisateurDoc != null) {
            return adapt(profilUtilisateurDoc);
        }
        return null;
    }

    @Override
    public DocumentModel getOrCreateUserProfilFromId(final CoreSession session, final String username) {
        DocumentModel profilUtilisateurDocument = getProfilUtilisateurDoc(session, username);

        if (profilUtilisateurDocument == null) {
            final DocumentModel userWorkspaceDoc = getUserWorkspaceDoc(session, username);
            profilUtilisateurDocument = initProfilUtilisateurFromUserWorkspace(session, userWorkspaceDoc);
        }
        return profilUtilisateurDocument;
    }

    @Override
    public DocumentModel getOrCreateCurrentUserProfil(final CoreSession session) {
        DocumentModel userWorkspaceDoc = getUserWorkspaceDocForCurrentUser(session);
        STProfilUtilisateur profilUtilisateur = getProfilUtilisateurForCurrentUser(session);
        if (profilUtilisateur == null) {
            return initProfilUtilisateurFromUserWorkspace(session, userWorkspaceDoc);
        }
        return profilUtilisateur.getDocument();
    }

    @Override
    public int getNumberDayBeforeOutdatedPassword(CoreSession session, STUser user) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();

        STProfilUtilisateur profilUtilisateur = getProfilUtilisateur(session, user.getUsername());
        int dayBeforeReminder =
            Integer.parseInt(
                paramService.getParametreValue(
                    session,
                    STParametreConstant.DELAI_PREVENANCE_RENOUVELLEMENT_MOT_DE_PASSE
                )
            ) +
            1;

        if (profilUtilisateur == null) {
            return dayBeforeReminder;
        }

        Calendar dateToChangePassword = (Calendar) profilUtilisateur.getDernierChangementMotDePasse().clone();
        String maxTimeOutdate = paramService.getParametreValue(
            session,
            STParametreConstant.DELAI_RENOUVELLEMENT_MOTS_DE_PASSE
        );
        if (dateToChangePassword != null) {
            dateToChangePassword.add(Calendar.MONTH, Integer.parseInt(maxTimeOutdate));
            Calendar todayReference = Calendar.getInstance();
            if (
                todayReference.compareTo(dateToChangePassword) >= 0 ||
                DateUtils.isSameDay(todayReference, dateToChangePassword)
            ) {
                return 0;
            }

            for (int i = 1; i <= dayBeforeReminder; i++) {
                todayReference.add(Calendar.DAY_OF_MONTH, 1);
                if (
                    todayReference.get(Calendar.DAY_OF_MONTH) == dateToChangePassword.get(Calendar.DAY_OF_MONTH) &&
                    todayReference.get(Calendar.MONTH) == dateToChangePassword.get(Calendar.MONTH) &&
                    todayReference.get(Calendar.YEAR) == dateToChangePassword.get(Calendar.YEAR)
                ) {
                    return i;
                }
            }
        }

        return dayBeforeReminder;
    }

    @Override
    public boolean isUserPasswordOutdated(CoreSession session, String username) {
        STProfilUtilisateur profilUtilisateur = getProfilUtilisateur(session, username);
        final STParametreService paramService = STServiceLocator.getSTParametreService();

        if (profilUtilisateur != null) {
            String maxTimeOutdate = paramService.getParametreValue(
                session,
                STParametreConstant.DELAI_RENOUVELLEMENT_MOTS_DE_PASSE
            );
            Calendar todayReference = DateUtil.removeMonthsToNow(Integer.parseInt(maxTimeOutdate));
            if (
                profilUtilisateur.getDernierChangementMotDePasse() != null &&
                todayReference.compareTo(profilUtilisateur.getDernierChangementMotDePasse()) > 0
            ) {
                // Dans ce cas le mot de passe doit être renouvellé
                return true;
            }
        }
        return false;
    }

    @Override
    public void changeDatePassword(final CoreSession session, final String username) {
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                final DocumentModel profilUtilisateurDoc = getOrCreateUserProfilFromId(session, username);
                final STProfilUtilisateur profilUtilisateur = profilUtilisateurDoc.getAdapter(
                    STProfilUtilisateur.class
                );
                // Initialisation de la date de changement de mot de passe
                if (profilUtilisateur != null) {
                    profilUtilisateur.setDernierChangementMotDePasse(Calendar.getInstance());
                    session.saveDocument(profilUtilisateur.getDocument());
                    session.save();
                }
            }
        }
        .runUnrestricted();
    }

    @Override
    public Set<STUser> getToRemindChangePasswordUserList(CoreSession session) {
        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final Set<STUser> userToRemind = new HashSet<>();

        String reminderDate = paramService.getParametreValue(
            session,
            STParametreConstant.DELAI_PREVENANCE_RENOUVELLEMENT_MOT_DE_PASSE
        );
        int maxTimeOutdate = Integer.parseInt(
            paramService.getParametreValue(session, STParametreConstant.DELAI_RENOUVELLEMENT_MOTS_DE_PASSE)
        );
        Calendar refInf = DateUtil.removeMonthsToNow(maxTimeOutdate);
        Calendar refSup = DateUtil.removeMonthsToNow(maxTimeOutdate);
        refSup.add(Calendar.DAY_OF_MONTH, Integer.parseInt(reminderDate));

        // Sélection des utilisateurs dont le mdp doit être changé prochainement
        final String query = getReminderMDPQuery(
            SolonDateConverter.DATE_DASH_REVERSE.format(refInf),
            SolonDateConverter.DATE_DASH_REVERSE.format(refSup)
        );
        DocumentModelList allProfil = new UnrestrictedQueryRunner(session, query).findAll();
        for (DocumentModel dm : allProfil) {
            String pathProfil = dm.getPathAsString();
            // Le workspace title est l'identifiant utilisateur
            String workspaceTitle;
            if (pathProfil != null && pathProfil.contains(STProfilUtilisateurConstants.PROFIL_UTILISATEUR_PATH)) {
                DocumentModel workspaceDoc = session.getDocument(dm.getParentRef());
                workspaceTitle = DublincoreSchemaUtils.getTitle(workspaceDoc);
            } else {
                workspaceTitle = DublincoreSchemaUtils.getTitle(dm);
            }

            DocumentModel userModel = STServiceLocator.getUserManager().getUserModel(workspaceTitle);
            if (userModel == null) {
                LOGGER.warn(
                    session,
                    STLogEnumImpl.FAIL_GET_USER_TEC,
                    "Pas d'utilisateur trouvé pour l'identifiant : " + workspaceTitle
                );
            } else {
                STUser stUser = userModel.getAdapter(STUser.class);
                if (stUser.isActive()) {
                    userToRemind.add(stUser);
                }
            }
        }

        return userToRemind;
    }

    /*
     * ############################################################################################## Méthodes protégées
     * #############################################################################################
     */

    /**
     * Génère la requête pour récupérer les utilisateurs dont le mot de passe va arriver à expiration
     *
     * @param nxqlDateInf
     * @param nxqlDateSup
     */
    protected abstract String getReminderMDPQuery(String nxqlDateInf, String nxqlDateSup);

    /**
     *
     * @param session
     * @param userWorkspaceDoc
     * @return
     */
    protected abstract DocumentModel getProfilUtilisateurDocFromWorkspace(
        CoreSession session,
        DocumentModel userWorkspaceDoc
    );

    /**
     * Construit le nom du workspace depuis le nom de l'utilisateur comme s'est fait dans DefaultUserWorkspace
     *
     * @see DefaultUserWorkspace.getUserWorkspaceNameForUser
     * @param userName
     *
     */
    protected String getUserWorkspaceNameForUser(String userName) {
        return IdUtils.generateId(userName, "-", false, 30);
    }

    /**
     * Créé le document d'un nouveau profil utilisateur avec le parentPath en paramètre
     *
     * @param session
     * @param parentPath
     * @return DocumentModel le documentModel du profil utilisateur
     * @exception ClientException
     */
    protected DocumentModel createDefaultUserProfil(final CoreSession session, final String parentPath) {
        DocumentModel userProfilDocument = session.createDocumentModel(
            parentPath,
            STProfilUtilisateurConstants.PROFIL_UTILISATEUR_PATH,
            STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DOCUMENT_TYPE
        );

        // sauvegarde du document
        userProfilDocument = session.createDocument(userProfilDocument);
        return userProfilDocument;
    }

    /**
     *
     * @param session
     * @param username
     * @return
     * @throws ClientException
     */
    protected DocumentModel getUserWorkspaceDoc(CoreSession session, String username) {
        final String workspaceName = getUserWorkspaceNameForUser(username);
        String query = String.format(QUERY_WORKSPACE, workspaceName);
        DocumentModelList dml = session.query(query);
        if (dml.isEmpty()) {
            final UserWorkspaceService userWorkspaceService = STServiceLocator.getUserWorkspaceService();
            DocumentModel doc = userWorkspaceService.getUserPersonalWorkspace(username, session.getRootDocument());
            if (doc != null) {
                return doc;
            }
            throw new WorkspaceNotFoundException("Pas de workspace pour l'utilisateur " + username);
        } else if (dml.size() > 1) {
            LOGGER.warn(
                session,
                STLogEnumImpl.ANO_WORKSPACE_MULTI_TEC,
                "Pour l'utilisateur <" + username + "> nom du workspace <" + workspaceName + ">"
            );
            return null;
        } else {
            return dml.get(0);
        }
    }

    /**
     * récupère le userWorkspace de l'utilisateur connecté
     *
     * @param session
     * @return
     */
    protected DocumentModel getUserWorkspaceDocForCurrentUser(CoreSession session) throws WorkspaceNotFoundException {
        final UserWorkspaceService userWorkspaceService = STServiceLocator.getUserWorkspaceService();
        DocumentModel doc = userWorkspaceService.getCurrentUserPersonalWorkspace(session);
        if (doc == null) {
            throw new WorkspaceNotFoundException("Pas de workspace pour l'utilisateur courant");
        } else {
            return doc;
        }
    }

    /**
     * initialise le profil utilisateur dans le workspace passé en paramètre
     *
     * @param session
     * @param userWorkspaceDoc
     * @return
     * @throws ClientException
     */
    protected DocumentModel initProfilUtilisateurFromUserWorkspace(
        CoreSession session,
        DocumentModel userWorkspaceDoc
    ) {
        if (userWorkspaceDoc != null) {
            final DocumentModel profilUtilisateurDocument = createDefaultUserProfil(
                session,
                userWorkspaceDoc.getPathAsString()
            );
            // Initialisation de la date de changement de mot de passe
            if (profilUtilisateurDocument != null) {
                STProfilUtilisateur profilUtilisateur = profilUtilisateurDocument.getAdapter(STProfilUtilisateur.class);
                if (profilUtilisateur != null && profilUtilisateur.getDernierChangementMotDePasse() == null) {
                    profilUtilisateur.setDernierChangementMotDePasse(Calendar.getInstance());
                    session.saveDocument(profilUtilisateur.getDocument());
                    session.save();
                }
            }
            return profilUtilisateurDocument;
        }
        return null;
    }

    @SuppressWarnings("unchecked") // compatible inherited adapters
    private T adapt(DocumentModel doc) {
        return (T) doc.getAdapter(STProfilUtilisateur.class);
    }
}
