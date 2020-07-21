package fr.dila.solonepp.core.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.runtime.api.Framework;

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
import fr.dila.st.core.util.SessionUtil;

/**
 * Gestionnaires d'utilisateur de l'application SOLON EPP.
 * 
 * @author jtremeaux
 */
public class SolonEppUserManagerImpl extends STUserManagerImpl implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long		serialVersionUID	= 2406829190055220601L;

	/**
	 * Logger.
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(SolonEppUserManagerImpl.class);

	protected NuxeoPrincipal makeEppPrincipal(DocumentModel userEntry, boolean anonymous, List<String> groups)
			throws ClientException {
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

		// TODO: reenable roles initialization once we have a use case for
		// a role directory. In the mean time we only set the JBOSS role
		// that is required to login
		List<String> roles = Arrays.asList("regular");
		principal.setRoles(roles);

		STUser stUser = userEntry.getAdapter(STUser.class);
		if (stUser == null) {
			throw new ClientException("Cannot cast user to STUser");
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
	protected NuxeoPrincipal makePrincipal(DocumentModel userEntry, boolean anonymous, List<String> groups)
			throws ClientException {
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
	public boolean checkUsernamePassword(String username, String password) throws ClientException {
		// Test de la validité du mot de passe
		CoreSession session = null;
		LoginContext loginContext = null;
		try {
			String userId = username;
			loginContext = Framework.login();
			session = SessionUtil.getCoreSession();

			NuxeoPrincipal principal = STServiceLocator.getUserManager().getPrincipal(username);
			if (principal != null) {
				userId = principal.getName();
				SolonEppServiceLocator.getProfilUtilisateurService().getOrCreateUserProfilFromId(session, userId);
				if (SolonEppServiceLocator.getProfilUtilisateurService().isUserPasswordOutdated(session, userId)) {
					LOGGER.info(session, STLogEnumImpl.NOTIFICATION_PASSWORD_FONC,
							"Mot de passe expiré pour l'utilisateur " + username);
				}
			}
		} catch (WorkspaceNotFoundException we) {
			LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we.getMessage());
			LOGGER.debug(session, STLogEnumImpl.FAIL_GET_WORKSPACE_FONC, we);
		} catch (LoginException le) {
			LOGGER.warn(session, STLogEnumImpl.FAIL_GET_SESSION_TEC, le.getMessage());
			LOGGER.debug(session, STLogEnumImpl.FAIL_GET_SESSION_TEC, le);
		} finally {
			SessionUtil.close(session);
			if (loginContext != null) {
				try {
					loginContext.logout();
				} catch (LoginException exc) {
					LOGGER.warn(session, STLogEnumImpl.FAIL_GET_SESSION_TEC, "Echec du logout : " + exc.getMessage());
					LOGGER.debug(session, STLogEnumImpl.FAIL_GET_SESSION_TEC, exc);
				}
			}
		}
		if (super.checkUsernamePassword(username, password)) {
			try {
				DocumentModel userdoc = getUserModel(username);
				STUser user = userdoc.getAdapter(STUser.class);
				return user.isActive();
			} catch (ClientException e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public void deleteUser(String userId) throws ClientException {
		Session userDir = null;
		try {
			userDir = dirService.open(userDirectoryName);
			if (!userDir.hasEntry(userId)) {
				throw new DirectoryException("User does not exist: " + userId);
			}
			userDir.deleteEntry(userId);
			userDir.commit();
			notifyUserChanged(userId);
			notify(userId, USERDELETED_EVENT_ID);

		} finally {
			if (userDir != null) {
				userDir.close();
			}
		}
	}

	@Override
	public void deleteUser(DocumentModel userModel) throws ClientException {
		String userId = getUserId(userModel);
		deleteUser(userId);
	}

}
