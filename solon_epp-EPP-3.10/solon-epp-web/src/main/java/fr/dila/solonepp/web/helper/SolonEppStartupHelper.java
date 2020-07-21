package fr.dila.solonepp.web.helper;

import static org.jboss.seam.ScopeType.SESSION;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

import fr.dila.cm.web.helper.CaseManagementStartupHelper;
import fr.dila.cm.web.mailbox.CaseManagementMailboxActionsBean;
import fr.dila.solonepp.api.service.ProfilUtilisateurService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.action.corbeille.CorbeilleActionsBean;
import fr.dila.solonepp.web.utilisateur.UserManagerActionsBean;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SessionUtil;
import fr.dila.st.web.action.NavigationWebActionsBean;

/**
 * Initialise le contexte utilisateur et redirige vers sa page d'accueil lors du login.
 * 
 * @author jtremeaux
 */
@Name("startupHelper")
@Scope(SESSION)
@Install(precedence = Install.DEPLOYMENT + 1)
public class SolonEppStartupHelper extends CaseManagementStartupHelper {

	/**
	 * Serial UID.
	 */
	private static final long								serialVersionUID	= -3606085944027894437L;

	private static final STLogger							LOGGER				= STLogFactory
																						.getLog(SolonEppStartupHelper.class);

	@In(create = true)
	protected transient CaseManagementMailboxActionsBean	cmMailboxActions;

	@In(create = true)
	protected transient NuxeoPrincipal						currentNuxeoPrincipal;

	@In(create = true)
	protected transient UserManagerActionsBean				userManagerActions;

	@In(create = true)
	protected transient NavigationWebActionsBean			navigationWebActions;

	@In(create = true, required = false)
	protected transient CorbeilleActionsBean				corbeilleActions;

	@Override
	public String initServerAndFindStartupPage() throws ClientException {
		super.initServerAndFindStartupPage();
		CoreSession session = null;

		try {
			final String user = currentNuxeoPrincipal.getName();
			if (currentNuxeoPrincipal.isAdministrator()) {
				return logout();
			}

			final STUserService userService = STServiceLocator.getSTUserService();
			final ProfilUtilisateurService profilUtilisateurService = SolonEppServiceLocator
					.getProfilUtilisateurService();

			// Vérification de la date de dernier changement de mot de passe par rapport au paramètre
			if (profilUtilisateurService.isUserPasswordOutdated(documentManager, user)) {
				userService.forceChangeOutdatedPassword(user);
			}

			// Navigue vers l'écran permettant à l'utilisateur de changer son mot de passe
			if (userService.isUserPasswordResetNeeded(user)) {
				return userManagerActions.resetCurrentUserPassword(user);
			}
			return corbeilleActions.navigateTo();
		} catch (Exception e) {
			try {
				session = SessionUtil.getCoreSession();
				LOGGER.error(session, STLogEnumImpl.FAIL_GET_CORBEILLE_TEC, e);
			} catch (ClientException e1) {
			} finally {
				SessionUtil.close(session);
			}
		}

		return corbeilleActions.navigateTo();
	}

	private String logout() throws ClientException {
		try {
			return navigationWebActions.logout();
		} catch (Exception e) {
			LOGGER.error(null, null, "L'utilisateur n'a pas le droit d'accéder à l'application", e);
			throw new ClientException(e);
		}
	}

}
