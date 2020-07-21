package fr.dila.st.api.service;

import java.util.Collection;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.MailboxManagementService;

/**
 * Service de mailbox du socle transverse, étend et remplace celui de Nuxeo.
 * 
 * @author jtremeaux
 */
public interface MailboxService extends MailboxManagementService {

	int	MAX_USERNAME_LENGTH	= 45;

	/**
	 * Retourne le type de document Mailbox (en interrogeant le type service de Nuxeo).
	 * 
	 * @return Type de document Mailbox
	 */
	String getMailboxType() throws ClientException;

	/**
	 * Génère l'identifiant technique d'une Mailbox personnelle, à partir du login de l'utilisateur. Cette méthode
	 * supprime les caractères spéciaux, la casse, etc.
	 * 
	 * @param posteName
	 *            Login de l'utilisateur (doit être unique)
	 * @return Identifiant technique de la Mailbox
	 */
	String getPersoMailboxId(String userName);

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
	@Override
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
	List<Mailbox> getMailbox(CoreSession session, Collection<String> mailboxIds);

	/**
	 * Retourne le répertoire MailboxRoot où créer les Mailbox.
	 * 
	 * @param session
	 *            Session
	 * @return Répertoire MailboxRoot
	 * @throws ClientException
	 * @throws CaseManagementException
	 */
	DocumentModel getMailboxRoot(CoreSession session) throws ClientException;

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
	 * @throws ClientException
	 */
	String getMailboxTitle(CoreSession session, String mailboxId) throws ClientException;

	/**
	 * Retourne tous les Mailboxs poste
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	List<Mailbox> getAllMailboxPoste(CoreSession session) throws ClientException;

	/**
	 * getUserPersonalMailbox en UFXQL sans testACL
	 * 
	 * @param session
	 * @param name
	 * @return
	 * @throws ClientException
	 */
	Mailbox getUserPersonalMailboxUFNXQL(CoreSession session, String user) throws ClientException;

}
