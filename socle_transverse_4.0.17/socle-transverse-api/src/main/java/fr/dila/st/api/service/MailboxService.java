package fr.dila.st.api.service;

import fr.dila.cm.mailbox.Mailbox;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service de mailbox du socle transverse, étend et remplace celui de Nuxeo.
 *
 * @author jtremeaux
 */
public interface MailboxService {
    int MAX_USERNAME_LENGTH = 45;

    /**
     * Retourne le type de document Mailbox (en interrogeant le type service de Nuxeo).
     *
     * @return Type de document Mailbox
     */
    String getMailboxType();

    /**
     * Retourne une Mailbox par son ID sans restrictions de droits.
     *
     * @param session
     *            Session
     * @param mailboxId
     *            Identifiant technique de la Mailbox
     * @return Mailbox
     */
    Mailbox getMailboxUnrestricted(CoreSession session, String mailboxId);

    /**
     * Retourne l'id du document correspondant à une mailbox. Recupere le document sans restriction de droit
     *
     * @param session
     *            Session
     * @param mailboxId
     *            identifiants techniques de la Mailbox
     * @return id du document mailbox
     */
    String getMailboxDocIdUnrestricted(CoreSession session, String mailboxId);

    /**
     * Retourne une Mailbox par son ID.
     *
     * @param session
     *            Session
     * @param mailboxId
     *            Identifiant technique de la Mailbox
     * @return Mailbox
     */
    Mailbox getMailbox(CoreSession session, String mailboxId);

    /**
     * Retourne un ensemble de Mailbox par leur ID
     *
     * @param session
     *            Session
     * @param mailboxIds
     *            ensemble d'identifiants techniques de la Mailbox
     * @return Mailbox
     */
    List<Mailbox> getMailbox(CoreSession session, Collection<String> mailboxIdList, String... prefetch);

    /**
     * Retourne le répertoire MailboxRoot où créer les Mailbox.
     *
     * @param session
     *            Session
     * @return Répertoire MailboxRoot
     */
    DocumentModel getMailboxRoot(CoreSession session);

    /**
     * Retourne le titre d'une mailbox.
     *
     * Appel unrestricted : pas de test des acls
     *
     * @param session
     *            Session
     * @param mailboxId
     *            Identifiant technique de la Mailbox (ex: "poste-50002248")
     * @return Titre de la Mailbox
     */
    String getMailboxTitle(CoreSession session, String mailboxId);

    /**
     * Retourne tous les Mailboxs poste
     *
     * @param session
     * @return
     */
    List<Mailbox> getAllMailboxPoste(CoreSession session);

    /**
     * getUserPersonalMailbox en UFXQL sans testACL
     *
     * @param session
     * @param user
     * @return
     */
    Mailbox getUserPersonalMailboxUFNXQL(CoreSession session, String user);

    //////////////////////////////////////

    /**
     * Returns the mailbox DocumentModel given the unique identifier
     *
     * @param session coreSession
     * @param muid mailbox identifier
     * @return the mailbox DocumentModel
     */
    DocumentModel getMailboxDocumentModel(CoreSession session, String muid);

    /**
     * Returns true if a mailbox with given id exists
     *
     * @param session
     * @param muid
     *            mailbox id
     */
    boolean hasMailbox(CoreSession session, String muid);

    /**
     * Returns the mailboxes with given unique identifiers.
     *
     * @param session
     *            a Core Session
     * @param muids
     *            Users ids
     *
     */
    List<Mailbox> getMailboxes(CoreSession session, List<String> muids);

    /**
     * Returns the personal mailbox id for this user.
     *
     * @param user
     *            User id
     */
    String getUserPersonalMailboxId(String user);

    /**
     * Returns the personal mailbox id for this user.
     *
     * @param userModel
     *            User id
     */
    String getUserPersonalMailboxId(DocumentModel userModel);

    /**
     * Returns all mailboxes for the current user.
     *
     * @param session
     */
    List<Mailbox> getUserMailboxes(CoreSession session);

    /**
     * Returns the personal mailbox of the given user.
     *
     * @param session
     * @param userId
     *            User id
     */
    Mailbox getUserPersonalMailbox(CoreSession session, String userId);

    /**
     * Returns the personal mailbox of the given user.
     *
     * @param session
     * @param userModel
     */
    Mailbox getUserPersonalMailbox(CoreSession session, DocumentModel userModel);

    /**
     * Returns a mailbox for given email
     *
     * @param session
     * @param email
     */
    Mailbox getUserPersonalMailboxForEmail(CoreSession session, String email);

    /**
     * Create the personal Mailbox with the registered
     *
     * @param session
     * @param userModel
     * @return personal mailbox
     */
    Mailbox createPersonalMailboxes(CoreSession session, DocumentModel userModel);

    /**
     * Test if the user has a personal mailbox created
     *
     * @param session
     * @param userModel
     * @return true if the user has a personal mailbox
     */
    boolean hasUserPersonalMailbox(CoreSession session, DocumentModel userModel);

    List<Mailbox> getMailboxUnrestricted(CoreSession session, Collection<String> mailboxIdList, String... prefetch);

    List<String> getMailboxDocIds(CoreSession session, Collection<String> mailboxIdList);

    Map<String, String> getMapMailboxDocIdsIds(CoreSession session, Collection<String> mailboxIdList);
}
