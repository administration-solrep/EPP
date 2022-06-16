package fr.dila.st.api.service;

import fr.dila.st.api.user.STProfilUtilisateur;
import fr.dila.st.api.user.STUser;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface du service ProfilUtilisateur
 *
 * @author user
 *
 */
public interface STProfilUtilisateurService<T extends STProfilUtilisateur> {
    /**
     * Récupère le documentModel du profil utilisateur
     *
     * @param session
     * @param username
     * @return
     *
     */
    DocumentModel getProfilUtilisateurDoc(CoreSession session, String username);

    /**
     * retourne le profil de l'utilisateur
     *
     * @param username
     * @return
     *
     */
    T getProfilUtilisateur(CoreSession session, String username);

    /**
     * retourne le profil de l'utilisateur courant
     *
     * @param session
     * @return
     *
     */
    T getProfilUtilisateurForCurrentUser(CoreSession session);

    /**
     * récupère ou initialise un profilUtilisateur s'il n'existe pas
     *
     * @param session
     * @param userId
     * @return le documentModel du profilUtilisateur
     *
     */
    DocumentModel getOrCreateUserProfilFromId(CoreSession session, String userId);

    /**
     * Récupère le profil utilisateur de l'utilisateur courant. Le créé s'il n'existe pas
     *
     * @param session
     * @return
     *
     */
    DocumentModel getOrCreateCurrentUserProfil(CoreSession session);

    /**
     * Calcul le nombre de jour avant qu'un mot de passe n'expire
     *
     * @param session
     * @param user
     * @return
     *
     */
    int getNumberDayBeforeOutdatedPassword(CoreSession session, STUser user);

    /**
     * Détermine si le mot de passe utilisateur est expiré
     *
     * @param session
     * @param username
     * @return
     *
     */
    boolean isUserPasswordOutdated(CoreSession session, String username);

    /**
     * Met à jour la date de dernière modification du mot de passe
     *
     * @param session
     * @param username
     *
     */
    void changeDatePassword(CoreSession session, String username);

    /**
     * Récupère la liste des utilisateurs pour lesquels le mot de passe va expirer prochainement
     *
     * @param session
     * @return
     *
     */
    Set<STUser> getToRemindChangePasswordUserList(CoreSession session);
}
