package fr.dila.st.core.operation.organigramme;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.usermanager.UserManager;

@Operation(
    id = CleanUsersOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "CleanUsers",
    description = "Clean the users that are in the poste table but not in the database"
)
public class CleanUsersOperation {
    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "ST.Organigramme.CleanUsers";
    private static final STLogger LOGGER = STLogFactory.getLog(CleanUsersOperation.class);
    private static final String EXECUTION_MODE = "exec";
    private static final String READING_MODE = "info";

    @Context
    protected CoreSession session;

    @Param(name = "infoOuExec")
    protected String infoOuExec = READING_MODE;

    @Param(name = "nbrLimit", required = false)
    protected int nbrLimit = Integer.MAX_VALUE;

    /**
     * Default constructor
     */
    public CleanUsersOperation() {
        // Do nothing
    }

    @OperationMethod
    public void run() throws Exception {
        LOGGER.info(
            STLogEnumImpl.LOG_INFO_TEC,
            "Lancement de la procédure de nettoyage des utilisateurs dans la table poste"
        );
        if (READING_MODE.equals(infoOuExec)) {
            LOGGER.info(
                STLogEnumImpl.LOG_INFO_TEC,
                "Le mode lecture a été choisi. Les utilisateurs à supprimer seront listés"
            );
        } else if (EXECUTION_MODE.equals(infoOuExec)) {
            LOGGER.info(
                STLogEnumImpl.LOG_INFO_TEC,
                "Le mode exécution a été choisi. Les utilisateurs à supprimer seront supprimés de la table poste"
            );
        }
        // Recherche de tous les postes
        STPostesService stPosteService = STServiceLocator.getSTPostesService();
        List<PosteNode> listPostes = stPosteService.getAllPostes();
        List<String> listUtilisateursANettoyer = new ArrayList<>();

        if (listPostes == null) {
            // Rien à faire. On s'arrête là
            LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Erreur lors de la récupération des postes");
        } else {
            LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Parcours des postes de l'application");

            List<String> listUtilisateursOK = new ArrayList<>();
            final UserManager userManager = STServiceLocator.getUserManager();

            for (PosteNode poste : listPostes) {
                // Récupération des utilisateurs
                List<String> listUsernames = poste.getMembers();
                if (listUsernames == null) {
                    continue;
                }
                for (String userName : listUsernames) {
                    if (
                        StringUtils.isEmpty(userName) ||
                        listUtilisateursANettoyer.contains(userName) ||
                        listUtilisateursOK.contains(userName)
                    ) {
                        // Username vide ou on a déjà traité cet utilisateur. On passe donc au suivant
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
                    } catch (NuxeoException e) {
                        continue;
                    }
                }
            }

            String listUserConcat = StringUtil.join(listUtilisateursANettoyer, ",", "'");
            LOGGER.info(
                STLogEnumImpl.LOG_INFO_TEC,
                "Fin du parcours des postes. Les utilisateurs à supprimer sont les suivants :" + listUserConcat
            );
            if (EXECUTION_MODE.equals(infoOuExec)) {
                LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Début de suppression des utilisateurs");
                listUtilisateursANettoyer
                    .stream()
                    .limit(nbrLimit)
                    .forEach(
                        idUser -> {
                            String messageRetour = stPosteService.deleteUserFromAllPostes(idUser);
                            LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, messageRetour);
                        }
                    );
                LOGGER.info(
                    STLogEnumImpl.LOG_INFO_TEC,
                    "Fin de la procédure de nettoyage des utilisateurs dans la table poste"
                );
            }
        }
    }
}
