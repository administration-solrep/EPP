package fr.dila.st.api.service;

import fr.dila.st.api.exception.UserNotFoundException;
import fr.dila.st.api.user.STUser;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.mail.Address;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.query.sql.model.OrderByExpr;
import org.nuxeo.ecm.core.query.sql.model.Predicate;

/**
 * Interface du service sur les utilisateurs du socle transverse.
 *
 * @author ARN
 */
public interface STUserService extends Serializable {
    /**
     * format pour afficher la civilite abregée, le nom et le prenom de l'utilisateur.
     */
    String CIVILITE_ABREGEE_PRENOM_NOM = "C p n";

    /**
     * Récupère la civilite, le nom et le prénom de l'utilisateur à partir du login de l'utilisateur. Si le nom de
     * l'utilisateur n'existe pas, on renvoie le login
     *
     * @param userLogin
     *            Identifiant technique de l'utilisateur
     * @return Nom complet et civilité
     */
    String getUserFullNameAndCivilite(String userLogin);

    /**
     * Récupère les informations de l'utilisateur à partir du formatage suivant : Le formattage est de la forme
     * "c p n t" avec : - c : Civilité - C : Civilité abrégée (M. au lieu de Monsieur et Mme. au lieu de Madame) - p :
     * Prénom - n : Nom - t : Téléphone - m : Mail
     *
     * @param userLogin
     *            Identifiant technique de l'utilisateur
     * @param format
     * @return Nom complet et civilité
     */
    String getUserInfo(String userLogin, String format);

    /**
     * Récupère la civilite, le nom et le prénom de l'utilisateur à partir d'un documentModel. Si le nom de
     * l'utilisateur n'existe pas, on renvoie le login.
     *
     * @param userModel
     *            Utilisateur
     * @return Nom complet et civilité
     *
     */
    String getUserFullNameAndCivilite(DocumentModel userModel);

    /**
     * Récupère les informations de l'utilisateur à partir du formatage suivant : Le formattage est de la forme
     * "c p n t" avec : - c : Civilité - C : Civilité abrégée (M. au lieu de Monsieur et Mme. au lieu de Madame) - p :
     * Prénom - n : Nom - t : Téléphone - m : Mail
     *
     * @param userModel
     * @param format
     * @return Nom complet
     *
     */
    String getUserInfo(DocumentModel userModel, String format);

    /**
     * Récupère le nom et le prénom de l'utilisateur à partir du login de l'utilisateur. Si le nom de l'utilisateur
     * n'existe pas, on renvoie le login.
     *
     * @param userLogin
     *            Identifiant technique de l'utilisateur
     * @return Nom complet
     *
     */
    String getUserFullName(String userLogin);

    String getUserFullNameOrEmpty(String userLogin);

    /**
     * Récupère le nom et le prénom de l'utilisateur à partir d'un documentModel. Si le nom de l'utilisateur n'existe
     * pas, on renvoie le login.
     *
     * @param userModel
     *            Utilisateur
     * @return Nom complet
     *
     */
    String getUserFullName(DocumentModel userModel);

    /**
     * Ancienne méthode qui a été conservée pour garder la compatibilité avec le
     * stockage et la recherche des audit logs.
     * <p>
     * Cette méthode retourne le prénom et le nom de l'utilisateur séparés par
     * une espace. Si l'utilisateur n'a pas de prénom, le nom est retourné avec
     * un espace au début. Ce format doit uniquement être utilisé pour la
     * gestion des audit logs pour éviter d'altérer les audit logs existants
     * pour prendre en compte le nouveau format sans espace.
     * <p>
     * Cette méthode doit être immuable
     *
     * @param userLogin
     *            Utilisateur
     * @return legacy full name
     *
     */
    String getLegacyUserFullName(String userLogin);

    /**
     * Récupère le prénom, le nom et le login de l'utilisateur à partir du login de l'utilisateur. Si le nom de
     * l'utilisateur n'existe pas, on renvoie le login.
     */
    String getUserFullNameWithUsername(String userLogin);

    /**
     * Récupère la liste des profils de l'utilisateurs sous forme d'une chaine de caractère Si le nom de l'utilisateur
     * n'existe pas, on renvoie "**poste inconnu**".
     *
     * @param userId
     *            utilisateur identifiant
     * @return la liste des profils de l'utilisateurs sous forme d'une chaine de caractèret
     *
     */
    String getUserProfils(String userId);

    /**
     * Génere le mot de passe de l'utilisateur et l'enregistre dans le LDAP.
     *
     * @param userId
     *            id de l'utilisateur
     * @return le mot de passe en clair pour l'envoie par mail à l'utilisateur
     *
     */
    String generateAndSaveNewUserPassword(String userId);

    String saveNewUserPassword(String userId, String newPassword);

    /**
     * Retourne vrai si l'utilisateur doit changer de mot de passe.
     *
     * @param userLogin
     *            Identifiant technique de l'utilisateur
     * @return
     *
     */
    boolean isUserPasswordResetNeeded(String userLogin);

    /**
     * Force l'utilisateur à changer de mot de passe lorsque le délai de renouvellement est dépassé.
     *
     * @param userLogin
     *            Identifiant technique de l'utilisateur
     *
     *
     */
    void forceChangeOutdatedPassword(String userLogin);

    /**
     * Reset le mot de passe d'un utilisateur et renvoie un mail.
     *
     * @param userLogin
     *            login
     * @param email
     *            email
     *
     */
    void askResetPassword(String userLogin, String email);

    /**
     * Retourne les addresses mail de la liste des utilisateurs.
     *
     * @param recipients
     *            La liste des utilisateurs
     * @return Liste d'adresses email
     *
     */
    List<Address> getAddressFromUserList(List<STUser> recipients);

    /**
     * Retourne la liste des adresses email sous forme de texte.
     *
     * @param recipients
     *            Liste d'utilisateurs
     * @return Liste des adresses email sous forme de texte
     *
     */
    List<String> getEmailAddressFromUserList(List<STUser> recipients);

    /**
     * Retourne la liste concaténée des ministères auxquels est rattaché l'utilisateur
     *
     * @param userId
     * @return
     *
     */
    String getUserMinisteres(String userId);

    /**
     * Retourne la liste concaténée des directions auxquelles est rattaché l'utilisateur
     *
     * @param userId
     * @return
     *
     */
    String getAllDirectionsRattachement(String userId);

    /**
     * Retourne la liste des id des ministères de l'utilisateur
     */
    List<String> getAllUserMinisteresId(String userId);

    /**
     * Retourne la liste concaténée des postes auxquels est rattaché l'utilisateur
     * @param userId
     * @return
     *
     */
    String getUserPostes(String userId);

    Optional<STUser> getOptionalUser(String username);

    /**
     * Retourne un {@link STUser}
     *
     * @param username username
     * @return STUser
     * @throws UserNotFoundException si l'utilisateur n'existe pas
     */
    STUser getUser(String username);

    /**
     * Cette méthode vide l'utilisateur du cache pour permettre la mise à jour de ses profils
     * @param username
     */
    void clearUserFromCache(String username);

    /**
     * S'agit-il d'un utilisateur migré S2NG ?
     *
     * @return true ssi le user de la BDD lié au username a un mot de passe NULL et un salt NULL
     */
    boolean isMigratedUser(String username);

    List<STUser> getActiveUsers(Predicate... predicates);

    List<STUser> getActiveUsers(List<OrderByExpr> orderExp, Predicate... predicates);

    List<DocumentModel> getActiveUserDocs(Predicate... predicates);

    List<DocumentModel> getActiveUserDocs(List<OrderByExpr> orderExp, Predicate... predicates);

    List<String> getActiveUsernames(Predicate... predicates);

    List<String> getActiveUsernames(List<OrderByExpr> orderExp, Predicate... predicates);

    Map<String, Calendar> getMapUsernameDateDerniereConnexion(List<DocumentModel> usersDocs);
}
