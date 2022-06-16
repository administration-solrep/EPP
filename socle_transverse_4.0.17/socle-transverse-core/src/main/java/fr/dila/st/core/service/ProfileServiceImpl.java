package fr.dila.st.core.service;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.util.DirSessionUtil;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelComparator;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Implémentation du service permettant de gérer les profils.
 *
 * @author jtremeaux
 */
public class ProfileServiceImpl implements ProfileService {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 7475407168269459198L;

    /**
     * Logger.
     */
    private static final Log LOGGER = LogFactory.getLog(ProfileServiceImpl.class);

    private static Map<String, DocumentModel> profileDocumentsMap = null;

    /**
     * default constructor
     */
    public ProfileServiceImpl() {
        // do nothing
    }

    @Override
    public boolean isProfileUpdatable(String profileId) {
        return !STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME.equals(profileId);
    }

    @Override
    public Map<String, DocumentModel> getProfilMap() {
        if (profileDocumentsMap == null) {
            profileDocumentsMap = new HashMap<>();
            try (Session session = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_PROFILE_DIR)) {
                Iterator<DocumentModel> itDocModel = session.query(new HashMap<>()).iterator();
                while (itDocModel.hasNext()) {
                    DocumentModel docModel = itDocModel.next();
                    profileDocumentsMap.put(docModel.getTitle(), docModel);
                }
            }
        }

        return profileDocumentsMap;
    }

    @Override
    public List<DocumentModel> findAllProfil() {
        try (Session session = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_PROFILE_DIR)) {
            List<DocumentModel> docList = session.query(new HashMap<>());
            Collections.sort(docList, new IdComparator());
            return docList;
        }
    }

    @Override
    public List<DocumentModel> findAllBaseFunction() {
        try (Session session = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR)) {
            Map<String, Serializable> filter = new HashMap<>();
            Map<String, String> orderBy = new HashMap<>();
            orderBy.put(STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY, DocumentModelComparator.ORDER_ASC);
            Set<String> fulltext = new HashSet<>();
            fulltext.add(STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY);
            List<DocumentModel> docList = session.query(filter, fulltext, orderBy, false);
            return docList;
        }
    }

    @Override
    public List<DocumentModel> getProfilListForUserCreate(STPrincipal principal) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Génération de la liste des profils");
        }

        STUser user = principal.getModel().getAdapter(STUser.class);

        List<String> userGroupList = user.getGroups();

        try (Session session = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_PROFILE_DIR)) {
            List<DocumentModel> docList = new ArrayList<>();
            for (String group : userGroupList) {
                if (StringUtils.isNotBlank(group)) {
                    DocumentModel profilDoc = session.getEntry(group);
                    if (profilDoc != null) {
                        docList.add(profilDoc);
                    }
                }
            }

            Collections.sort(docList, new IdComparator());
            return docList;
        }
    }

    @Override
    public Set<String> getBaseFunctionFromProfil(String profil) {
        Set<String> baseFunctionSet = new HashSet<>();

        try (Session session = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_PROFILE_DIR)) {
            // Recherche les sous-groupes (= fonctions unitaires)
            DocumentModel groupDocument = session.getEntry(profil);
            if (groupDocument == null) {
                // Le groupe ne correspond pas à un groupe du LDAP (peut être est-ce un groupe virtuel)
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Groupe <" + profil + "> non trouvé dans l'annuaire");
                }
            } else {
                // Ajoute les fonctions unitaires à l'ensemble des fonctions unitaires du profil
                @SuppressWarnings("unchecked")
                List<String> functions = (List<String>) groupDocument.getProperty(
                    STSchemaConstant.GROUP_SCHEMA,
                    STSchemaConstant.DIRECTORY_GROUP_FUNCTIONS_PROPERTY
                );
                if (functions != null) {
                    baseFunctionSet.addAll(functions);
                }
            }
        }
        return baseFunctionSet;
    }

    @Override
    public Set<String> getBaseFunctionFromProfil(Collection<String> profilList) {
        Set<String> baseFunctionSet = new HashSet<>();
        try (Session session = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_PROFILE_DIR)) {
            for (String profil : profilList) {
                // Recherche les sous-groupes (= fonctions unitaires)
                DocumentModel groupDocument = session.getEntry(profil);
                if (groupDocument == null) {
                    // Le groupe ne correspond pas à un groupe du LDAP (peut être est-ce un groupe virtuel)
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Groupe <" + profil + "> non trouvé dans l'annuaire");
                    }
                    continue;
                } else {
                    // Ajoute les fonctions unitaires à l'ensemble des fonctions unitaires du profil
                    @SuppressWarnings("unchecked")
                    List<String> subGroups = (List<String>) groupDocument.getProperty(
                        STSchemaConstant.GROUP_SCHEMA,
                        STSchemaConstant.DIRECTORY_GROUP_FUNCTIONS_PROPERTY
                    );
                    if (subGroups != null) {
                        baseFunctionSet.addAll(subGroups);
                    }
                }
            }
        }
        return baseFunctionSet;
    }

    @Override
    public List<STUser> getUsersFromBaseFunction(String baseFunctionId) {
        List<STUser> usersList = new ArrayList<>();

        final Calendar today = Calendar.getInstance();

        final UserManager userManager = STServiceLocator.getUserManager();

        try (
            Session baseFunctionSession = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR);
            Session profilSession = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_PROFILE_DIR)
        ) {
            DocumentModel baseFunctionDoc = baseFunctionSession.getEntry(baseFunctionId);
            if (baseFunctionDoc != null) {
                List<String> profilIdList = PropertyUtil.getStringListProperty(
                    baseFunctionDoc,
                    "base_function",
                    "parentGroups"
                );
                Set<String> userIdSet = new HashSet<>();

                for (String profilId : profilIdList) {
                    DocumentModel profilDocument = profilSession.getEntry(profilId);
                    userIdSet.addAll(PropertyUtil.getStringListProperty(profilDocument, "group", "members"));
                }

                for (String userId : userIdSet) {
                    DocumentModel userModel = userManager.getUserModel(userId);
                    if (userModel == null) {
                        LOGGER.warn(
                            "L'utilisateur avec l'id " +
                            userId +
                            " n'existe pas dans la branche people mais existe dans un ou plusieurs groupes"
                        );
                        continue;
                    }
                    STUser user = userModel.getAdapter(STUser.class);
                    // On n'ajoute que les utilisteurs qui ne sont pas effacés et dont la date de fin n'est pas passée
                    if (!user.isDeleted() && (user.getDateFin() == null || today.before(user.getDateFin()))) {
                        usersList.add(user);
                    }
                }
            }
        }

        return usersList;
    }

    @Override
    public Set<String> getProfilFromBaseFunction(String baseFunction) {
        Set<String> profilesList = new HashSet<>();

        try (
            Session baseFunctionSession = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR);
            Session profilSession = DirSessionUtil.getSession(STConstant.ORGANIGRAMME_PROFILE_DIR)
        ) {
            DocumentModel baseFunctionDoc = baseFunctionSession.getEntry(baseFunction);
            if (baseFunctionDoc != null) {
                List<String> profilIdList = PropertyUtil.getStringListProperty(
                    baseFunctionDoc,
                    "base_function",
                    "parentGroups"
                );
                if (profilesList != null) {
                    for (String profilId : profilIdList) {
                        profilesList.add(profilId);
                    }
                }
            }
        }

        return profilesList;
    }

    /**
     * Static inner class pour comparer deux documentModels par rapport à leurs id
     *
     */
    private static class IdComparator implements Comparator<DocumentModel> {

        /**
         * Default constructor
         */
        public IdComparator() {
            // do nothing
        }

        @Override
        public int compare(DocumentModel p1, DocumentModel p2) {
            return p1.getId().compareTo(p2.getId());
        }
    }

    @Override
    public void resetProfilMap() {
        profileDocumentsMap = null;
    }
}
