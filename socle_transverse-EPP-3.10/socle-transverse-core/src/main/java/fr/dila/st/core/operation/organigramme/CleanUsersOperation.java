package fr.dila.st.core.operation.organigramme;

import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Operation(id = CleanUsersOperation.ID, category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY, label = "CleanUsers", description = "Clean the users that are in the poste table but not in the LDAP")
public class CleanUsersOperation {

	/**
	 * Identifiant technique de l'opération.
	 */
	public static final String		ID			= "ST.Organigramme.CleanUsers";
	private static final STLogger	LOGGER		= STLogFactory.getLog(CleanUsersOperation.class);

	@Context
	protected CoreSession			session;

	@Param(name = "infoOuExec", required = true)
	protected String				infoOuExec	= "info";

	@Param(name = "nbrLimit", required = false)
	protected Integer				nbrLimit	= null;

	/**
	 * Default constructor
	 */
	public CleanUsersOperation() {
		// Do nothing
	};

	@OperationMethod
	public void run() throws Exception {
		LOGGER.info(STLogEnumImpl.LOG_INFO_TEC,
				"Lancement de la procédure de nettoyage des utilisateurs dans la table poste");
		if (infoOuExec.equals("info")) {
			LOGGER.info(STLogEnumImpl.LOG_INFO_TEC,
					"Le mode lecture a été choisi. Les utilisateurs à supprimer seront listés");
		} else if (infoOuExec.equals("exec")) {
			LOGGER.info(STLogEnumImpl.LOG_INFO_TEC,
					"Le mode exécution a été choisi. Les utilisateurs à supprimer seront supprimés de la table poste");
		}
		// Recherche de tous les postes
		STPostesService stPosteService = STServiceLocator.getSTPostesService();
		List<PosteNode> listPostes = stPosteService.getAllPostes();
		List<String> listUtilisateursANettoyer = new ArrayList<String>();
		List<String> listUtilisateursOK = new ArrayList<String>();
		final UserManager userManager = STServiceLocator.getUserManager();
		if (listPostes == null) {
			// Rien à faire. On s'arrête là
			LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Erreur lors de la récupération des postes");
		} else {
			LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Parcours des postes de l'application");
			for (PosteNode poste : listPostes) {
				// Récupération des utilisateurs
				List<String> listUsernames = poste.getMembers();
				// On recherche chaque utilisateur dans le ldap
				if (listUsernames == null) {
					continue;
				}
				for (String userName : listUsernames) {
					if (listUtilisateursANettoyer.contains(userName)) {
						// On a déjà traité cet utilisateur. On passe donc au suivant
						continue;
					} else if (listUtilisateursOK.contains(userName)) {
						// idem
						continue;
					}
					// Tentative de récupération de l'utilisateur
					try {
						DocumentModel userModel = userManager.getUserModel(userName);
						if (userModel == null) {
							// ajout de l'utilisateur à la liste des utilisateurs à supprimer
							listUtilisateursANettoyer.add(userName);
						} else {
							listUtilisateursOK.add(userName);
						}
					} catch (ClientException e) {
						continue;
					}
				}
			}
			String listUserConcat = StringUtil.join(listUtilisateursANettoyer, ",", "'");
			LOGGER.info(STLogEnumImpl.LOG_INFO_TEC,
					"Fin du parcours des postes. Les utilisateurs à supprimer sont les suivants :" + listUserConcat);
			// Lister les users à supprimer dans table poste
			if (infoOuExec.equals("info")) {
				// Lecture
				// On ne fait rien
			} else if (infoOuExec.equals("exec")) {
				LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Début de suppression des utilisateurs");
				Integer nbrSupprim = 0;
				for (String idUser : listUtilisateursANettoyer) {
					nbrSupprim = nbrSupprim + 1;
					String messageRetour = stPosteService.deleteUserFromAllPostes(idUser);
					LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, messageRetour);
					if (nbrLimit != null && nbrLimit.equals(nbrSupprim)) {
						break;
					}
				}
				LOGGER.info(STLogEnumImpl.LOG_INFO_TEC,
						"Fin de la procédure de nettoyage des utilisateurs dans la table poste");
			}
		}
	}
}
