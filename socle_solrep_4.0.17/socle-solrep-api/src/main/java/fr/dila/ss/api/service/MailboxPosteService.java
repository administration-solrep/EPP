package fr.dila.ss.api.service;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.st.api.organigramme.EntiteNode;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de gérer les Mailbox des postes.
 *
 * @author jtremeaux
 */
public interface MailboxPosteService extends Serializable {
    /**
     * Génère l'identifiant technique d'une Mailbox poste, à partir du nom du poste. Cette méthode supprime les
     * caractères spéciaux, la casse, etc.
     *
     * @param posteName
     *            Nom du poste (doit être unique)
     * @return Identifiant technique de la Mailbox
     */
    String getPosteMailboxId(String posteName);

    /**
     * Retourne la liste des mailbox poste de l'utilisateur connecté.
     *
     * @param session
     *            Session
     * @return Liste de Mailbox
     *
     */
    List<Mailbox> getMailboxPosteList(CoreSession session);

    /**
     * Retourne la liste des mailbox poste correspondant a un ensemble d'identifiant technique
     *
     * @param session
     *            Session
     * @param posteIds
     *            ensemble d'identifiant technique de poste
     * @return Liste de Mailbox
     *
     */
    List<Mailbox> getMailboxPosteList(CoreSession session, Collection<String> posteIdSet, String... prefetch);

    /**
     * Retourne la mailbox d'un poste à partir de son identifiant technique. Si la Mailbox n'existe pas, retourne null.
     *
     * @param session
     *            Session
     * @param posteId
     *            Identifiant technique du poste
     * @return mailbox
     *
     */
    Mailbox getMailboxPoste(CoreSession session, String posteId);

    /**
     * Retourne l'identifiant technique de la Mailbox de l'administrateur SGG.
     *
     * @return Identifiant technique de la Mailbox de l'administrateur SGG
     *
     */
    String getMailboxSggId();

    /**
     * Retourne la mailbox d'un poste à partir de son identifiant technique. Si la Mailbox n'existe pas, la Mailbox est
     * créée à la volée.
     *
     * @param session
     *            Session
     * @param posteId
     *            Identifiant technique du poste
     * @return Mailbox
     *
     */
    Mailbox getOrCreateMailboxPoste(CoreSession session, String posteId);

    /**
     * Retourne la mailbox d'un poste à partir de son identifiant technique. Si la Mailbox n'existe pas, la Mailbox est
     * créée à la volée. Attention methode non Unrestricted contrairement a getOrCreateMailboxPoste(CoreSession session,
     * String posteId)
     *
     * @param session
     *            Session
     * @param posteId
     *            Identifiant technique du poste
     * @return Mailbox
     *
     */
    Mailbox getOrCreateMailboxPosteNotUnrestricted(CoreSession session, String posteId);

    /**
     * Crée si elle n'existe pas la Mailbox associée à un poste.
     *
     * @param session
     *            Session
     * @param posteId
     *            identifiant du poste
     * @param posteLabel
     *            Label du poste
     * @return Mailbox Mailbox nouvellement créée, ou null si elle existe déjà
     *
     */
    Mailbox createPosteMailbox(CoreSession session, String posteId, String posteLabel);

    /**
     * Crée si elle n'existe pas la Mailbox associée à un poste. Attention méthode non Unrestricted contrairement a
     * createPosteMailbox(CoreSession session, String posteName, String posteLabel)
     *
     * @param session
     *            Session
     * @param posteId
     *            identifiant du poste
     * @param posteLabel
     *            Label du poste
     * @return Mailbox Mailbox nouvellement créée, ou null si elle existe déjà
     *
     */
    Mailbox createPosteMailboxNotUnrestricted(CoreSession session, String posteId, String posteLabel);

    /**
     * Retourne la liste des ministères à partir d'un identifiant de Mailbox.
     *
     * @param mailboxId
     *            Identifiant technique de la Mailbox (ex: "poste-50002248")
     * @return Liste des ministères
     *
     */
    List<EntiteNode> getMinistereListFromMailbox(String mailboxId);

    /**
     * Retourne l'id de la direction à partir d'une mailbox.
     *
     * @param mailboxId
     *            Identifiant technique de la Mailbox (ex: "poste-50002248")
     * @return Identifiant technique de la direction
     *
     */
    String getIdDirectionFromMailbox(String mailboxId);

    /**
     * Retourne l'id du poste correspondant à la mailbox.
     *
     * @param mailboxId
     *            id de la mailbox
     * @return id du poste correspondant à la mailbox
     */
    String getPosteIdFromMailboxId(String mailboxId);

    /**
     * Construit la liste des identifiants de Mailbox à partir de l'ensemble des postes.
     *
     * @param posteIdSet
     *            Ensemble des identifiants techniques des postes
     * @return Ensemble des identifiants techniques des mailbox
     */
    Set<String> getMailboxPosteIdSetFromPosteIdSet(Collection<String> posteIdSet);

    /**
     * Crée si nécessaire les Mailbox poste de l'utilisateur.
     *
     * @param session
     *            Session
     * @param userDoc
     *            Document utilisateur
     * @return Liste des Mailbox créées
     *
     */
    List<Mailbox> createPosteMailboxes(CoreSession session, DocumentModel userDoc);

    /**
     * Retourne les noms des ministères en fonction de l'identifiant technique de la mailbox.
     *
     * @param mailboxId
     *            Identifiant technique de la mailbox (ex: "poste-1234")
     * @return Noms des ministères (ex. "Mnistère de l'économie, Ministère de l'agriculture").
     */
    String getMinisteresFromMailboxId(String mailboxId);

    /**
     * Retourne l'edition des ministères en fonction de l'identifiant technique de la mailbox.
     *
     * @param mailboxId
     *            Identifiant technique de la mailbox (ex: "poste-1234")
     * @return Edition des ministères
     */
    String getMinisteresEditionFromMailboxId(String mailboxId);

    List<Mailbox> getMailboxPosteListUnrestricted(
        CoreSession session,
        Collection<String> posteIdSet,
        String... prefetch
    );

    List<String> getMailboxPosteDocIds(CoreSession session, Collection<String> posteIdSet);

    List<String> toMailboxIds(Collection<String> posteIdSet);
}
