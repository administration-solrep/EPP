package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.administration.ProfilUtilisateur;
import fr.dila.solonepp.api.constant.SolonEppProfilUtilisateurConstants;
import fr.dila.solonepp.api.service.ProfilUtilisateurService;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.AbstractSTProfilUtilisateurServiceImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.platform.usermanager.UserManager;

public class ProfilUtilisateurServiceImpl
    extends AbstractSTProfilUtilisateurServiceImpl<ProfilUtilisateur>
    implements ProfilUtilisateurService {
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(ProfilUtilisateurServiceImpl.class);

    @Override
    public List<STUser> filterUserForNotification(CoreSession session, final List<STUser> userList) {
        List<STUser> usersListResult = new ArrayList<STUser>();
        List<String> usersNameList = new ArrayList<String>();
        // on récupère les profils utilisateurs des utilisateurs du poste
        final StringBuilder sb = new StringBuilder("SELECT w.ecm:name as userName FROM ");
        sb.append(SolonEppProfilUtilisateurConstants.PROFIL_UTILISATEUR_DOCUMENT_TYPE);
        sb.append(" AS p, ").append("Workspace");
        sb.append(" AS w  WHERE ").append("w.ecm:name IN (");
        sb.append(StringHelper.getQuestionMark(userList.size()));
        sb.append(") AND p.ecm:parentId = w.ecm:uuid AND p.pusr:notificationEmail = 0 ");
        final List<String> paramList = new ArrayList<String>();
        for (STUser stUser : userList) {
            paramList.add(stUser.getUsername());
        }

        IterableQueryResult res = null;
        try {
            res = QueryUtils.doUFNXQLQuery(session, sb.toString(), paramList.toArray(new String[paramList.size()]));
            Iterator<Map<String, Serializable>> it = res.iterator();
            // pour chaque utilisateur, on vérifie si l'on envoie ou non de mail
            while (it.hasNext()) {
                Map<String, Serializable> row = it.next();
                String userName = (String) row.get("userName");
                usersNameList.add(userName);
            }

            for (STUser user : userList) {
                if (!usersNameList.contains(user.getUsername())) {
                    usersListResult.add(user);
                }
            }
        } catch (NuxeoException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_EXECUTE_UFNXQL_TEC, exc);
        } finally {
            if (res != null) {
                res.close();
            }
        }
        return usersListResult;
    }

    @Override
    public List<STUser> getUserWithProfilList(CoreSession session) {
        final UserManager userManager = STServiceLocator.getUserManager();
        Map<String, Serializable> filter = new HashMap<String, Serializable>();
        final DocumentModelList userModelList = userManager.searchUsers(filter, null);
        final List<STUser> allUsersList = new ArrayList<STUser>();

        for (DocumentModel userDocModel : userModelList) {
            STUser user = userDocModel.getAdapter(STUser.class);
            if (user.isActive()) {
                allUsersList.add(user);
            }
        }

        final List<STUser> allUsersWithProfilList = new ArrayList<STUser>();
        for (STUser user : allUsersList) {
            ProfilUtilisateur profilUtilisateur = getOrCreateCurrentUserProfil(session)
                .getAdapter(ProfilUtilisateur.class);
            if (profilUtilisateur != null) {
                allUsersWithProfilList.add(user);
            }
        }
        return allUsersWithProfilList;
    }

    @Override
    protected String getReminderMDPQuery(String nxqlDateInf, String nxqlDateSup) {
        StringBuilder query = new StringBuilder("SELECT * FROM ")
            .append(STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DOCUMENT_TYPE)
            .append(" WHERE ")
            .append("(pusr:dernierChangementMotDePasse >= DATE '")
            .append(nxqlDateInf)
            .append("')")
            .append(" AND (pusr:dernierChangementMotDePasse <= DATE '")
            .append(nxqlDateSup)
            .append("')");
        return query.toString();
    }

    @Override
    protected DocumentModel getProfilUtilisateurDocFromWorkspace(CoreSession session, DocumentModel userWorkspaceDoc) {
        if (userWorkspaceDoc != null) {
            final String userWorkspacePath = userWorkspaceDoc.getPathAsString();
            final PathRef profilUtilisateurRef = new PathRef(
                userWorkspacePath + "/" + STProfilUtilisateurConstants.PROFIL_UTILISATEUR_PATH
            );
            return session.getDocument(profilUtilisateurRef);
        }
        return null;
    }
}
