package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.administration.ProfilUtilisateur;
import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.user.STUser;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public interface ProfilUtilisateurService extends STProfilUtilisateurService<ProfilUtilisateur> {
    List<STUser> filterUserForNotification(CoreSession session, final List<STUser> userList);

    /**
     * Retourne la liste des utilisateurs actifs possédant un profil utilisateur
     * @param session CoreSession
     *
     * @return liste filtrée
     */
    List<STUser> getUserWithProfilList(CoreSession session);
}
