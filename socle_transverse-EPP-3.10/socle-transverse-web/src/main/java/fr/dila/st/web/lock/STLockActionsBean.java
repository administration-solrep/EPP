package fr.dila.st.web.lock;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.edit.lock.LockActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;

/**
 * Bean Web permettant de gérer les verrous sur les documents.
 * 
 * @author sly
 * @author jtremeaux
 */
@Name("stLockActions")
@Scope(ScopeType.CONVERSATION)
public class STLockActionsBean implements Serializable {

	/**
	 * Message d'erreur renvoyé lorsque
	 */
	public static final String				LOCK_LOST_ERROR_MSG							= "st.lock.action.lockLost.error";

	/**
	 * Serial Version UID.
	 */
	private static final long				serialVersionUID							= -440650977111463806L;

	/**
	 * Logger.
	 */
	private static final Log				LOG											= LogFactory
																								.getLog(STLockActionsBean.class);

	private static final String				UNLOCK_FAILED								= "document.unlock.failed";

	private static final String				UNLOCK_DONE									= "document.unlock.done";

	private static final String				UNLOCK										= "document.unlock";

	private static final String				UNLOCK_NOT_PERMITTED						= "document.unlock.not.permitted";

	private static final String				LOCK_FAILED									= "document.lock.failed";

	private static final String				LOCK_SUCCESS								= "document.lock";

	private static final String				LOCK_DETAIL_TITLE							= "st.lock.detail.title";

	private static final String				FORMAT_CIV_PRENOM_NOM						= "c p n";

	private static final String				UNLOCK_DOC_DEBUG							= "Unlock documents ...";

	private static final String				FORMAT_DOCUMENT								= "document";

	public static final String				EVENT_LOCK_DOCUMENTS						= "stLockActions.lockDocuments";

	public static final String				EVENT_UNLOCK_DOCUMENTS						= "stLockActions.unlockDocuments";

	@In(create = true, required = false)
	protected transient CoreSession			documentManager;

	@In(create = true)
	protected transient LockActions			lockActions;

	@In(create = true, required = false)
	protected transient FacesMessages		facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor	resourcesAccessor;

	@In(create = true, required = false)
	protected transient NavigationContext	navigationContext;

	@In(required = true, create = true)
	protected NuxeoPrincipal				currentUser;

	/**
	 * Vrai si le verrou du document courant est actionnable par l'utilisateur courant.
	 */
	protected Boolean						currentDocIsLockActionnableByCurrentUser	= null;

	/**
	 * Vrai si le verrou du document courant est verrouillé.
	 */
	protected Boolean						isCurrentDocumentLocked						= null;

	/**
	 * Vrai si le verrou du document courant est verrouillé par l'utilisateur courant.
	 */
	protected Boolean						isCurrentDocumentLockedByCurrentUser		= null;

	/**
	 * Détermine si un document est verrouillé.
	 * 
	 * @param document
	 *            Document
	 * @return Document verrouillé ou non
	 * @throws ClientException
	 */
	public boolean isDocumentLocked(DocumentModel document) throws ClientException {
		String lockOwnerName = getLockOwnerName(document);
		return StringUtils.isNotBlank(lockOwnerName);
	}

	/**
	 * Détermine si un document est verrouillé par l'utilisateur en cours.
	 * 
	 * @param document
	 *            Document
	 * @return Document verrouillé ou non
	 * @throws ClientException
	 */
	public boolean isDocumentLockedByCurrentUser(DocumentModel document) throws ClientException {
		String lockOwnerName = getLockOwnerName(document);
		return StringUtils.isNotBlank(lockOwnerName) && lockOwnerName.equals(currentUser.getName());
	}

	/**
	 * Détermine si un document est verrouillé par un autre utilisateur.
	 * 
	 * @param document
	 *            Document
	 * @return Document verrouillé ou non
	 * @throws ClientException
	 */
	public boolean isDocumentLockedByAnotherUser(DocumentModel document) throws ClientException {
		String lockOwnerName = getLockOwnerName(document);
		return StringUtils.isNotBlank(lockOwnerName) && !lockOwnerName.equals(currentUser.getName());
	}

	/**
	 * Retourne vrai si le verrou du document courant est actionnable par l'utilisateur courant.
	 * 
	 * @return Vrai si le verrou du document courant est actionnable par l'utilisateur courant
	 * @throws ClientException
	 */
	public boolean currentDocIsLockActionnableByCurrentUser() throws ClientException {
		if (currentDocIsLockActionnableByCurrentUser == null) {
			currentDocIsLockActionnableByCurrentUser = STServiceLocator.getSTLockService().isLockActionnableByUser(
					documentManager, navigationContext.getCurrentDocument(), currentUser, lockActions);
		}
		return currentDocIsLockActionnableByCurrentUser;
	}

	/**
	 * Retourne vrai si le verrou du document courant est verrouillé.
	 * 
	 * @return Vrai si le verrou du document courant est verrouillé
	 * @throws ClientException
	 */
	public boolean isCurrentDocumentLocked() throws ClientException {
		if (navigationContext.getCurrentDocument() == null) {
			return Boolean.FALSE;
		} else {
			if (isCurrentDocumentLocked == null) {
				Map<String, String> lockDetails = STServiceLocator.getSTLockService().getLockDetails(documentManager,
						navigationContext.getCurrentDocument());
				isCurrentDocumentLocked = lockDetails != null && !lockDetails.isEmpty();
			}
			return isCurrentDocumentLocked;
		}
	}

	/**
	 * Retourne vrai si le verrou du document courant est verrouillé par l'utilisateur courant.
	 * 
	 * @return Vrai si le verrou du document courant est verrouillé par l'utilisateur courant
	 * @throws ClientException
	 */
	public boolean isCurrentDocumentLockedByCurrentUser() throws ClientException {
			return isCurrentDocumentLockedByCurrentUser = STServiceLocator.getSTLockService().isLockByUser(documentManager,
					navigationContext.getCurrentDocument(), currentUser);
	}

	/**
	 * Retourne vrai si l'utilisateur a le droit de verrouiller le document passé en paramètre, ou que le document est
	 * déjà verrouillé par cet utilisateur.
	 * 
	 * @param document
	 *            Document à tester
	 * @return Vrai si l'utilisateur a le droit de verrouiller le document
	 */
	public boolean canUserLockDoc(DocumentModel document) {
		if (document == null) {
			LOG.warn("Can't evaluate lock action : document is null");
			return false;
		}

		if (document.isProxy()) {
			return false;
		}

		try {
			NuxeoPrincipal principal = (NuxeoPrincipal) documentManager.getPrincipal();
			Lock docLock = documentManager.getLockInfo(document.getRef());
			return docLock == null
					|| (principal.isAdministrator() || documentManager.hasPermission(document.getRef(),
							SecurityConstants.WRITE_PROPERTIES)) && !document.isVersion();
		} catch (Exception e) {
			LOG.debug("evaluation of document lock " + document.getName() + " failed (" + e.getMessage()
					+ ": returning false");
			return false;
		}
	}

	/**
	 * Méthode de lock d'un document, identique à celle de LockActionsBean, à la différence qu'elle ne reset pas les
	 * tabs avec webActions.resetTabList(), et qu'elle renvoie un booléen.
	 * 
	 * @param document
	 *            le document
	 * @return Boolean true = lock ; false = lock failed
	 * @throws ClientException
	 */
	public Boolean lockDocument(DocumentModel document) throws ClientException {
		return lockDocuments(Collections.singletonList(document), null);
	}

	/**
	 * Verrouille une liste de documents.
	 * 
	 * @param documents
	 * @return false si au moins 1 lock faux.
	 * @throws ClientException
	 */
	public Boolean lockDocuments(List<DocumentModel> documents) throws ClientException {
		return lockDocuments(documents, null);
	}

	/**
	 * Verrouille une liste de documents.
	 * 
	 * @param documents
	 * @return false si au moins 1 lock faux.
	 * @throws ClientException
	 */
	public Boolean lockDocuments(List<DocumentModel> documents, String type) throws ClientException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Lock several documents ...");
		}

		resetEventContext();
		Boolean result = true;
		String message = LOCK_SUCCESS;
		final STLockService socleLockActionsService = STServiceLocator.getSTLockService();
		for (DocumentModel document : documents) {
			if (!socleLockActionsService.lockDoc(documentManager, document)) {
				result = false;
				message = LOCK_FAILED;
			}
		}

		if (type != null) {
			message = message.replace(FORMAT_DOCUMENT, type);
		}

		// Affiche un message d'information
		facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(message));
		lockActions.resetLockState();
		
		Events.instance().raiseEvent(EVENT_LOCK_DOCUMENTS);

		return result;
	}

	/**
	 * Déverrouille un document.
	 * 
	 * @param document
	 *            Document à déverrouiller
	 * @return
	 * @throws ClientException
	 */
	public String unlockDocument(DocumentModel document) throws ClientException {
		return unlockDocuments(Collections.singletonList(document));
	}

	/**
	 * Déverrouille une liste de document.
	 * 
	 * @param documents
	 *            Documents à déverrouiller
	 * @return
	 * @throws ClientException
	 */
	public String unlockDocuments(List<DocumentModel> documents) throws ClientException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(UNLOCK_DOC_DEBUG);
		}

		resetEventContext();
		String message = UNLOCK_FAILED;
		final STLockService socleLockActionsService = STServiceLocator.getSTLockService();
		for (DocumentModel document : documents) {
			Map<String, Serializable> lockDetails = lockActions.getLockDetails(document);
			if (lockDetails == null) {
				message = UNLOCK_DONE;
			} else {
				if (socleLockActionsService.unlockDoc(document, documentManager, lockActions)) {
					message = UNLOCK;
				} else {
					message = UNLOCK_NOT_PERMITTED;
				}
			}
		}
		facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(message));
		lockActions.resetLockState();
		Events.instance().raiseEvent(EVENT_UNLOCK_DOCUMENTS);

		return null;
	}

	public String unlockDocumentUnrestricted(DocumentModel document) throws ClientException {
		return unlockDocumentsUnrestricted(Collections.singletonList(document), null);
	}

	/**
	 * Déverrouille un ensemble de documents sans ternir compte des droits de l'utilisateur courant.
	 * 
	 * @param documents
	 *            Documents
	 * @return Vue
	 * @throws ClientException
	 */
	public String unlockDocumentsUnrestricted(List<DocumentModel> documents) throws ClientException {
		return unlockDocumentsUnrestricted(documents, null);
	}

	/**
	 * Déverrouille un ensemble de documents sans tenir compte des droits de l'utilisateur courant.
	 * 
	 * @param documents
	 *            Documents
	 * @return Vue
	 * @throws ClientException
	 */
	public String unlockDocumentsUnrestricted(List<DocumentModel> documents, String type) throws ClientException {
		if (LOG.isDebugEnabled()) {
			LOG.debug(UNLOCK_DOC_DEBUG);
		}

		resetEventContext();
		String message = UNLOCK_FAILED;
		final STLockService socleLockActionsService = STServiceLocator.getSTLockService();
		for (DocumentModel document : documents) {
			if (socleLockActionsService.unlockDocUnrestricted(documentManager, document)) {
				message = UNLOCK;
			} else {
				message = UNLOCK_NOT_PERMITTED;
			}
		}
		if (type != null) {
			message = message.replace(FORMAT_DOCUMENT, type);
		}
		facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get(message));
		lockActions.resetLockState();
		Events.instance().raiseEvent(EVENT_UNLOCK_DOCUMENTS);

		return null;
	}

	/**
	 * Retourne les détails de verrou d'un document.
	 * 
	 * @param document
	 *            Document
	 * @return Détails de verrou d'un document
	 */
	public Map<String, String> getLockDetails(DocumentModel document) {
		Map<String, String> lockDetails = new HashMap<String, String>();
		if (document == null) {
			return null;
		}
		try {
			Lock lock = documentManager.getLockInfo(document.getRef());
			if (lock == null) {
				return null;
			}

			lockDetails.put(LockActions.LOCKER, lock.getOwner());
			if (lock.getCreated() != null) {
				lockDetails.put(LockActions.LOCK_CREATED, DateUtil.formatForClient(lock.getCreated().getTime()));
			}
		} catch (ClientException e) {
			LOG.error("error getting log details", e);
		}

		return lockDetails;
	}

	/**
	 * Retourne le nom de la personne qui a verrouillé un document.
	 * 
	 * @param document
	 *            Document
	 * @return Nom de la personne qui a verrouillé un document
	 */
	public String getLockOwnerName(DocumentModel document) {
		Map<String, String> lockDetails = getLockDetails(document);
		if (lockDetails == null) {
			return "";
		}

		return lockDetails.get(LockActions.LOCKER);
	}

	/**
	 * Retourne un message indiquant l'état du verrou sur le document.
	 * 
	 * @param document
	 *            Document
	 * @return Message d'information
	 */
	public String getLockMessage(DocumentModel document) {
		Map<String, String> lockDetails = getLockDetails(document);
		if (lockDetails == null) {
			return "";
		}

		String pattern = resourcesAccessor.getMessages().get(LOCK_DETAIL_TITLE);
		String lockOwner = lockDetails.get(LockActions.LOCKER);
		return MessageFormat.format(pattern, lockOwner);
	}

	/**
	 * Retourne un message indiquant l'état du verrou sur le document + la civilité, le nom et le prenom de
	 * l'utilisateur qui a posé le verrou. Le formattage est de la forme "c p n t" avec : - c : Civilité - C : Civilité
	 * abrégée (M. au lieu de Monsieur et Mme. au lieu de Madame) - p : Prénom - n : Nom - t : Téléphone - m : Mail
	 * 
	 * @param document
	 *            Document
	 * @return Message d'information
	 */
	public String getLockMessageWithUserInfo(DocumentModel document, String format) {
		String lockOwner = getUserInfo(document, format);
		String pattern = resourcesAccessor.getMessages().get(LOCK_DETAIL_TITLE);
		return MessageFormat.format(pattern, lockOwner);
	}

	/**
	 * Retourne un message indiquant l'état du verrou sur le document + la civilité, le nom et le prenom de
	 * l'utilisateur qui a posé le verrou. Le formattage est de la forme "c p n t" avec : - c : Civilité - C : Civilité
	 * abrégée (M. au lieu de Monsieur et Mme. au lieu de Madame) - p : Prénom - n : Nom - t : Téléphone - m : Mail
	 * 
	 * @param document
	 *            Document
	 * @return Message d'information
	 */
	public String getLockMessageWithUserInfo(String ownerName) {
		// appel du service pour récupérer le nom complet de l'utilisateur
		final STUserService stUserservice = STServiceLocator.getSTUserService();
		String lockOwner = stUserservice.getUserInfo(ownerName, FORMAT_CIV_PRENOM_NOM);
		String pattern = resourcesAccessor.getMessages().get(LOCK_DETAIL_TITLE);
		return MessageFormat.format(pattern, lockOwner);
	}

	/**
	 * Retourne un message indiquant l'état du verrou sur le document + la civilité, le nom et le prenom de
	 * l'utilisateur qui a posé le verrou.
	 * 
	 * @param document
	 *            Document
	 * @return Message d'information
	 */
	public String getLockMessageWithUserInfo(DocumentModel document) {
		return getLockMessageWithUserInfo(document, FORMAT_CIV_PRENOM_NOM);
	}

	public String getCurrentLockOwnerName() {
		return getLockOwnerName(navigationContext.getCurrentDocument());
	}

	public String getCurrentLockOwnerInfo() {
		return getUserInfo(navigationContext.getCurrentDocument(), FORMAT_CIV_PRENOM_NOM);
	}

	public String getLockTime(DocumentModel document) {
		Map<String, String> lockDetails = getLockDetails(document);
		if (lockDetails == null || lockDetails.get(LockActions.LOCK_CREATED) == null) {
			return null;
		} else {
			return lockDetails.get(LockActions.LOCK_CREATED);
		}
	}

	public String getCurrentLockTime() {
		return getLockTime(navigationContext.getCurrentDocument());
	}

	/**
	 * Retourne la civilité, le nom et le prenom de l'utilisateur qui a posé le verrou sur le document. Le formattage
	 * est de la forme "c p n t" avec : - c : Civilité - C : Civilité abrégée (M. au lieu de Monsieur et Mme. au lieu de
	 * Madame) - p : Prénom - n : Nom - t : Téléphone - m : Mail
	 * 
	 * @param documentCourant
	 *            Document
	 * @param format
	 *            format
	 * @return Message d'information
	 */
	protected String getUserInfo(DocumentModel documentCourant, String format) {
		Map<String, String> lockDetails = getLockDetails(documentCourant);
		if (lockDetails == null) {
			return "";
		}
		String lockOwner = lockDetails.get(LockActions.LOCKER);
		// appel du service pour récupérer le nom complet de l'utilisateur
		final STUserService stUserservice = STServiceLocator.getSTUserService();
		return stUserservice.getUserInfo(lockOwner, format);
	}

	protected void resetEventContext() {
		Context evtCtx = Contexts.getEventContext();
		if (evtCtx != null) {
			evtCtx.remove("currentDocumentCanBeLocked");
			evtCtx.remove("currentDocumentLockDetails");
			evtCtx.remove("currentDocumentCanBeUnlocked");
		}
	}

	/**
	 * Réinitialise les variables liées au verrou lors du changement de doc. courant.
	 */
	@Observer(STEventConstant.CURRENT_DOCUMENT_CHANGED_EVENT)
	public void resetValue() {
		isCurrentDocumentLockedByCurrentUser = null;
		currentDocIsLockActionnableByCurrentUser = null;
		isCurrentDocumentLocked = null;
	}
}
