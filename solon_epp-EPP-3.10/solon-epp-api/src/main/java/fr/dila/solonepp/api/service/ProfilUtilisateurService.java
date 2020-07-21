package fr.dila.solonepp.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.user.STUser;

public interface ProfilUtilisateurService extends STProfilUtilisateurService {
     
     List<STUser> filterUserForNotification(CoreSession session, final List<STUser> userList );

     /**
      * Retourne la liste des utilisateurs actifs possédant un profil utilisateur
      * @param session CoreSession
      * 
      * @return liste filtrée
      * 
      * @throws ClientException
      */
     List<STUser> getUserWithProfilList(CoreSession session) throws ClientException;
}
