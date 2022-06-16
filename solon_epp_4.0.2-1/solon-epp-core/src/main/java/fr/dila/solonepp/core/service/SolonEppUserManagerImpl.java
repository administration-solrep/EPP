package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.solonepp.core.security.principal.EppPrincipalImpl;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.WorkspaceNotFoundException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.STUserManagerImpl;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.runtime.api.Framework;

/**
 * Gestionnaires d'utilisateur de l'application SOLON EPP.
 *
 * @author jtremeaux
 */
public class SolonEppUserManagerImpl extends STUserManagerImpl implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 2406829190055220601L;

    /**
     * Logger.
     */
    private static final STLogger LOGGER = STLogFactory.getLog(SolonEppUserManagerImpl.class);

    protected NuxeoPrincipal makeEppPrincipal(DocumentModel userEntry, boolean anonymous, List<String> groups) {
        boolean admin = false;
        String username = userEntry.getId();

        // XXX why not set groups to anonymous user?
        List<String> virtualGroups = new LinkedList<String>();
        if (!anonymous) {
            // Add preconfigured groups: useful for LDAP
            if (defaultGroup != null) {
                virtualGroups.add(defaultGroup);
            }
            // Add additional groups: useful for virtual users
            if (groups != null) {
                virtualGroups.addAll(groups);
            }
            // Create a default admin if needed
            if (administratorIds != null && administratorIds.contains(username)) {
                admin = true;
                if (administratorGroups != null) {
                    virtualGroups.addAll(administratorGroups);
                }
            }
        }

        EppPrincipalImpl principal = new EppPrincipalImpl(username, anonymous, admin, false);
        principal.setConfig(userConfig);

        principal.setModel(userEntry, false);
        principal.setVirtualGroups(virtualGroups, true);

        List<String> roles = Arrays.asList("regular");
        principal.setRoles(roles);

        STUser stUser = userEntry.getAdapter(STUser.class);
        if (stUser == null) {
            throw new NuxeoException("Cannot cast user to STUser");
        }

        // Renseigne les fonctions unitaires de l'utilisateur
        final ProfileService profileService = STServiceLocator.getProfileService();
        Set<String> baseFunctionSet = profileService.getBaseFunctionFromProfil(principal.getAllGroups());
        principal.setBaseFunctionSet(baseFunctionSet);

        // Renseigne les postes de l'utilisateur
        List<String> posteListe = stUser.getPostes();
        Set<String> posteIdSet = new HashSet<String>(posteListe);
        principal.setPosteIdSet(posteIdSet);

        // Renseigne les institutions de l'utilisateur
        final OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
        Set<String> institutionIdSet = new HashSet<String>();
        for (String posteId : posteIdSet) {
            List<InstitutionNode> institutionNodeList = organigrammeService.getInstitutionParentFromPoste(posteId);
            for (OrganigrammeNode institutionNode : institutionNodeList) {
                final String institutionId = institutionNode.getId();
                institutionIdSet.add(institutionId);
            }
        }
        principal.setInstitutionIdSet(institutionIdSet);

        return principal;
    }

    @Override
    protected NuxeoPrincipal makePrincipal(DocumentModel userEntry, boolean anonymous, List<String> groups) {
        NuxeoPrincipal principal = makeEppPrincipal(userEntry, anonymous, groups);

        if (activateComputedGroup() && principal instanceof NuxeoPrincipalImpl) {
            NuxeoPrincipalImpl nuxPrincipal = (NuxeoPrincipalImpl) principal;

            List<String> vGroups = getService().computeGroupsForUser(nuxPrincipal);

            if (vGroups == null) {
                vGroups = new ArrayList<String>();
            }

            List<String> origVGroups = nuxPrincipal.getVirtualGroups();
            if (origVGroups == null) {
                origVGroups = new ArrayList<String>();
            }

            // MERGE!
            origVGroups.addAll(vGroups);

            nuxPrincipal.setVirtualGroups(origVGroups);

            // This a hack to work around the problem of running tests
            if (!Framework.isTestModeSet()) {
                nuxPrincipal.updateAllGroups();
            } else {
                List<String> allGroups = nuxPrincipal.getGroups();
                for (String vGroup : vGroups) {
                    if (!allGroups.contains(vGroup)) {
                        allGroups.add(vGroup);
                    }
                }
                nuxPrincipal.setGroups(allGroups);
            }
        }
        return principal;
    }

    @Override
    public boolean checkUsernamePassword(String username, String password) {
        // Test de la validité du mot de passe
        RepositoryManager repoService = ServiceUtil.getRequiredService(RepositoryManager.class);
        org.nuxeo.runtime.transaction.TransactionHelper.runInTransaction(
            () ->
                CoreInstance.doPrivileged(
                    repoService.getDefaultRepositoryName(),
                    session -> {
                        try {
                            NuxeoPrincipal principal = STServiceLocator.getUserManager().getPrincipal(username);
                            if (principal != null) {
                                String userId = principal.getName();
                                SolonEppServiceLocator
                                    .getProfilUtilisateurService()
                                    .getOrCreateUserProfilFromId(session, userId);
                                if (
                                    SolonEppServiceLocator
                                        .getProfilUtilisateurService()
                                        .isUserPasswordOutdated(session, userId)
                                ) {
                                    LOGGER.info(
                                        session,
                                        STLogEnumImpl.NOTIFICATION_PASSWORD_FONC,
                                        "Mot de passe expiré pour l'utilisateur " + username
                                    );
                                }
                            }
                        } catch (WorkspaceNotFoundException we) {
                            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we.getMessage());
                            LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we);
                        }
                    }
                )
        );
        if (super.checkUsernamePassword(username, password)) {
            try {
                if (virtualUsers.containsKey(username)) {
                    return true;
                } else {
                    DocumentModel userdoc = getPrivilegedUserModel(username);
                    STUser user = userdoc.getAdapter(STUser.class);
                    return user.isActive();
                }
            } catch (NuxeoException e) {
                return false;
            }
        }
        return false;
    }

    protected NuxeoPrincipal getPrincipalUsingCache(String username) {
        NuxeoPrincipal ret = (NuxeoPrincipal) principalCache.get(username);
        if (ret == null) {
            ret = getPrincipal(username, null);
            if (ret == null) {
                return ret;
            }
            principalCache.put(username, ret);
        }
        return ((EppPrincipalImpl) ret).cloneTransferable(); // should not return cached principal
    }
}
