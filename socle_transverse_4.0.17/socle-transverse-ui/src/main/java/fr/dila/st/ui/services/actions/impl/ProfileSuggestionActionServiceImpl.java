package fr.dila.st.ui.services.actions.impl;

import static fr.dila.st.api.constant.STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR;
import static fr.dila.st.api.constant.STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY;
import static fr.dila.st.api.constant.STSchemaConstant.BASE_FUNCTION_SCHEMA;
import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.ui.services.actions.ProfileSuggestionActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelComparator;
import org.nuxeo.ecm.core.api.NuxeoGroup;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.SizeLimitExceededException;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.platform.usermanager.UserManager;

public class ProfileSuggestionActionServiceImpl implements ProfileSuggestionActionService {
    private static final Log LOG = LogFactory.getLog(ProfileSuggestionActionService.class);

    public static final String TYPE_KEY_NAME = "type";

    public static final String ENTRY_KEY_NAME = "entry";

    public static final String GROUP_TYPE = "GROUP_TYPE";

    public static final String ID_KEY_NAME = "id";

    public static final String PREFIXED_ID_KEY_NAME = "prefixed_id";

    private static final List<String> ADMIN_MINISTERIEL_HIDDEN_GROUPS = ImmutableList.of(
        STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME,
        STBaseFunctionConstant.SUPERVISEUR_SGG_GROUP_NAME,
        STBaseFunctionConstant.CONTRIBUTEUR_SPM_GROUP_NAME,
        STBaseFunctionConstant.VIGIE_SGG_GROUP_NAME,
        STBaseFunctionConstant.ADMIN_NOMINATIONS_GROUP_NAME
    );

    @Override
    public List<DocumentModel> getGroupsSuggestions(SpecificContext context, String pattern) {
        try (Session session = getRequiredService(DirectoryService.class).open(ORGANIGRAMME_BASE_FUNCTION_DIR)) {
            Map<String, Serializable> filter = new HashMap<>();
            if (pattern != null && !"".equals(pattern)) {
                filter.put("description", pattern);
            }
            Map<String, String> orderBy = new HashMap<>();
            orderBy.put("description", DocumentModelComparator.ORDER_ASC);

            return session.query(filter, new HashSet<String>(filter.keySet()), orderBy, false);
        } catch (SizeLimitExceededException e) {
            context.getMessageQueue().addErrorToQueue("label.security.searchOverFlow");
            LOG.error(e);
            return Collections.emptyList();
        }
    }

    @Override
    public Object getSuggestions(SpecificContext context, String input, String userSuggestionSearchType) {
        List<DocumentModel> groups = Collections.emptyList();
        if (GROUP_TYPE.equals(userSuggestionSearchType) || StringUtils.isEmpty(userSuggestionSearchType)) {
            groups = getGroupsSuggestions(context, input);
        }

        int groupSize = groups.size();
        int totalSize = groupSize;

        List<Map<String, Object>> result = new ArrayList<>(totalSize);

        for (DocumentModel group : groups) {
            Map<String, Object> entry = new HashMap<>();
            entry.put(TYPE_KEY_NAME, GROUP_TYPE);
            final String description = (String) group.getProperty(
                BASE_FUNCTION_SCHEMA,
                BASE_FUNCTION_DESCRIPTION_PROPERTY
            );
            entry.put(ENTRY_KEY_NAME, description);
            String groupId = group.getId();
            entry.put(ID_KEY_NAME, groupId);
            entry.put(PREFIXED_ID_KEY_NAME, NuxeoGroup.PREFIX + groupId);
            result.add(entry);
        }

        return result;
    }

    // XXX: needs optimisation
    @Override
    public Map<String, Object> getPrefixedUserInfo(String id) {
        Map<String, Object> res = new HashMap<>();
        res.put(PREFIXED_ID_KEY_NAME, id);
        if (id != null) {
            final UserManager userManager = STServiceLocator.getUserManager();
            if (id.startsWith(NuxeoPrincipal.PREFIX)) {
                String username = id.substring(NuxeoPrincipal.PREFIX.length());
                res.put(ID_KEY_NAME, username);
                res.put(ENTRY_KEY_NAME, userManager.getUserModel(username));
            } else if (id.startsWith(NuxeoGroup.PREFIX)) {
                res.put(TYPE_KEY_NAME, GROUP_TYPE);
                String groupname = id.substring(NuxeoGroup.PREFIX.length());
                res.put(ID_KEY_NAME, groupname);
                res.put(ENTRY_KEY_NAME, userManager.getGroupModel(groupname));
            } else {
                res.put(ID_KEY_NAME, id);
            }
        }
        return res;
    }

    @Override
    public Map<String, Object> getUserInfo(String id) {
        Map<String, Object> res = new HashMap<>();
        res.put(ID_KEY_NAME, id);

        res.put(PREFIXED_ID_KEY_NAME, NuxeoGroup.PREFIX + id);
        res.put(TYPE_KEY_NAME, GROUP_TYPE);
        DocumentModel functionDocument = getProfileModel(id);
        if (functionDocument != null) {
            final String description = (String) functionDocument.getProperty(
                STSchemaConstant.BASE_FUNCTION_SCHEMA,
                STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY
            );
            res.put(ENTRY_KEY_NAME, description);
        } else {
            res.put(ENTRY_KEY_NAME, id);
        }

        return res;
    }

    private DocumentModel getProfileModel(String profileName) {
        if (profileName == null) {
            return null;
        }
        try (Session session = getRequiredService(DirectoryService.class).open(ORGANIGRAMME_BASE_FUNCTION_DIR)) {
            return session.getEntry(profileName);
        }
    }

    /**
     * Un Administrateur fonctionnel et un Superviseur SGG peut affecter n’importe quel profil
     */
    @Override
    public boolean filterProfilToDisplay(NuxeoPrincipal principal, String profil) {
        return (
            PermissionHelper.isAdminFonctionnel(principal) ||
            PermissionHelper.isSuperviseurSgg(principal) ||
            isNotProfilHiddenForAdminMinOrProfilSGG(profil)
        );
    }

    private static boolean isNotProfilHiddenForAdminMinOrProfilSGG(String profil) {
        Set<String> functions = STServiceLocator.getProfileService().getBaseFunctionFromProfil(profil);
        return (
            !ADMIN_MINISTERIEL_HIDDEN_GROUPS.contains(profil) && !functions.contains(STBaseFunctionConstant.PROFIL_SGG)
        );
    }
}
