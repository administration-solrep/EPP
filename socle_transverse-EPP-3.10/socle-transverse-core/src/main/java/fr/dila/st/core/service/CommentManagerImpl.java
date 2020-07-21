package fr.dila.st.core.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.comment.api.CommentConstants;
import org.nuxeo.ecm.platform.comment.api.CommentEvents;
import org.nuxeo.ecm.platform.comment.api.CommentManager;
import org.nuxeo.ecm.platform.comment.impl.CommentSorter;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.domain.comment.STComment;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.SessionUtil;

/**
 * 
 * based on implementation : org.nuxeo.ecm.platform.comment.impl.CommentManagerImpl
 * 
 * Realise le lien entre les documents et leur commentaire en ajoutant l'id du document dans le commentaire. (à la place
 * de l'utilisation systeme de relation JENA)
 * 
 * Les documents sont recupérés par des requetes NXQL
 */
public class CommentManagerImpl implements CommentManager {

	public static final String		COMMENTS_DIRECTORY	= "Comments";

	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(CommentManagerImpl.class);

	private static final String		DATE_FORMAT_STR		= "yyyy-MM";

	private static final String COMMENTS_QUERY = "SELECT * FROM Comment WHERE " + STSchemaConstant.COMMENT_PREFIX + ":"
			+ STSchemaConstant.COMMENT_PARENT_COMMENT_ID_PROPERTY + "= '%s' and ecm:isProxy = 0";

	private static final String		COMMENTED_QUERY		= "SELECT * FROM Document WHERE ecm:uuid = '%s' "
																+ "and ecm:isProxy = 0";

	private static final String		HIDDEN_FOLDER		= "HiddenFolder";

	private static final String		MEMBERS				= "members";

	public CommentManagerImpl() {
		// Default constructor
	}

	protected CoreSession openCoreSession(final String repositoryName) throws ClientException {
		return SessionUtil.getCoreSession();
	}

	protected void closeCoreSession(final LoginContext loginContext, final CoreSession session) throws ClientException {
		if (loginContext != null) {
			try {
				loginContext.logout();
			} catch (final LoginException e) {
				throw new ClientException(e);
			}
		}

		SessionUtil.close(session);
	}

	protected List<DocumentModel> getComments(final CoreSession session, final DocumentModel docModel)
			throws ClientException {
		final List<DocumentModel> commentList = new ArrayList<DocumentModel>();
		docModel.reset();

		if (STConstant.COMMENT_DOCUMENT_TYPE.equals(docModel.getType())) {
			// On cherche les enfants
			final String docId = docModel.getId();
			final String query = String.format(COMMENTS_QUERY, docId);
			final DocumentModelList dml = session.query(query);
			for (final DocumentModel dm : dml) {
				commentList.add(dm);
			}
		} else {
			// On cherche les commentaires principaux
			List<String> docIds = PropertyUtil.getStringListProperty(docModel, STSchemaConstant.ROUTING_TASK_SCHEMA,
					STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY);

			for (final String docId : docIds) {
				commentList.add(session.getDocument(new IdRef(docId)));
			}
		}

		final CommentSorter sorter = new CommentSorter(true);
		Collections.sort(commentList, sorter);

		return commentList;
	}

	@Override
	public List<DocumentModel> getComments(final DocumentModel docModel) throws ClientException {

		LoginContext loginContext = null;
		CoreSession session = null;
		try {
			loginContext = Framework.login();
			session = openCoreSession(docModel.getRepositoryName());

			return getComments(session, docModel);

		} catch (final Exception e) {
			throw new ClientException(e);
		} finally {
			closeCoreSession(loginContext, session);
		}

	}

	@Override
	public DocumentModel createComment(final DocumentModel docModel, final String comment, final String author)
			throws ClientException {
		LoginContext loginContext = null;
		CoreSession session = null;
		try {
			loginContext = Framework.login();
			session = openCoreSession(docModel.getRepositoryName());

			DocumentModel commentDM = session.createDocumentModel("Comment");
			commentDM.setProperty(STSchemaConstant.COMMENT_SCHEMA, STSchemaConstant.COMMENT_TEXT_PROPERTY, comment);
			commentDM.setProperty(STSchemaConstant.COMMENT_SCHEMA, STSchemaConstant.COMMENT_AUTHOR_PROPERTY, author);
			commentDM.setProperty(STSchemaConstant.COMMENT_SCHEMA, STSchemaConstant.COMMENT_CREATION_DATE_PROPERTY,
					Calendar.getInstance());
			commentDM = internalCreateComment(session, docModel, commentDM, null);
			session.save();

			return commentDM;
		} catch (final Exception e) {
			throw new ClientException(e);
		} finally {
			closeCoreSession(loginContext, session);
		}
	}

	@Override
	public DocumentModel createComment(final DocumentModel docModel, final String comment) throws ClientException {
		final String author = getCurrentUser(docModel);
		return createComment(docModel, comment, author);
	}

	/**
	 * If the author property on comment is not set, retrieve the author name from the session
	 * 
	 * @param docModel
	 *            The document model that holds the session id
	 * @param comment
	 *            The comment to update
	 * @throws ClientException
	 */
	protected static String updateAuthor(final DocumentModel docModel, final DocumentModel comment)
			throws ClientException {
		// update the author if not set
		String author = (String) comment.getProperty(STSchemaConstant.COMMENT_SCHEMA,
				STSchemaConstant.COMMENT_AUTHOR_PROPERTY);
		if (author == null) {
			LOGGER.debug(STLogEnumImpl.LOG_DEBUG_TEC,
					"deprecated use of createComment: the client should set the author property on document");
			author = getCurrentUser(docModel);
			comment.setProperty(STSchemaConstant.COMMENT_SCHEMA, STSchemaConstant.COMMENT_AUTHOR_PROPERTY, author);
		}
		return author;
	}

	@Override
	public DocumentModel createComment(final DocumentModel docModel, final DocumentModel comment)
			throws ClientException {
		LoginContext loginContext = null;
		CoreSession session = null;
		try {
			loginContext = Framework.login();
			session = openCoreSession(docModel.getRepositoryName());
			final DocumentModel doc = internalCreateComment(session, docModel, comment, null);
			session.save();
			return doc;
		} catch (final Exception e) {
			throw new ClientException(e);
		} finally {
			closeCoreSession(loginContext, session);
		}
	}

	/**
	 * Prise en compte de la nature n-n du lien entre la note et l'objet
	 * commenté.
	 */
	protected DocumentModel internalCreateComment(final CoreSession session, final DocumentModel docModel,
			final DocumentModel comment, final String path) throws ClientException {
		final String author = updateAuthor(docModel, comment);
		DocumentModel createdComment;

		try {
			if (STConstant.COMMENT_DOCUMENT_TYPE.equals(docModel.getType())) {
				// Réponse à une note d'étape
				comment.setProperty(STSchemaConstant.COMMENT_SCHEMA,
						STSchemaConstant.COMMENT_PARENT_COMMENT_ID_PROPERTY, docModel.getId());

				createdComment = createCommentDocModel(session, docModel, comment, path);
			} else {
				// Note d'étape
				createdComment = createCommentDocModel(session, docModel, comment, path);

				List<String> commentIds = PropertyUtil.getStringListProperty(docModel,
						STSchemaConstant.ROUTING_TASK_SCHEMA, STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY);

				commentIds.add(createdComment.getId());

				docModel.setProperty(STSchemaConstant.ROUTING_TASK_SCHEMA,
						STSchemaConstant.ROUTING_TASK_COMMENTS_PROPERTY, commentIds);

				session.save();
			}
		} catch (final Exception e) {
			throw new ClientException("failed to create comment", e);
		}

		NuxeoPrincipal principal = null;

		try {
			final UserManager userManager = Framework.getService(UserManager.class);
			if (userManager == null) {
				LOGGER.error(session, STLogEnumImpl.FAIL_GET_PARAM_TEC,
						"Error notifying comment added: UserManager service not found");
			} else {
				principal = userManager.getPrincipal(author);
			}
		} catch (final Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_GET_PRIN_TEC, "Error building principal for notification", e);
		}
		if (principal != null) {
			notifyEvent(session, docModel, CommentEvents.COMMENT_ADDED, null, createdComment, principal);
		}

		return createdComment;
	}

	protected DocumentModel createCommentDocModel(final CoreSession session, final DocumentModel docModel,
			final DocumentModel commentDoc, final String path) throws ClientException {

		DocumentModel comment = commentDoc;

		String domainPath;
		updateAuthor(docModel, comment);

		final String[] pathList = getCommentPathList(comment);

		if (path == null) {
			domainPath = docModel.getPath().segment(0);
		} else {
			domainPath = path;
		}
		if (session == null) {
			return null;
		}

		// TODO GR upgrade this code. It can't work if current user
		// doesn't have admin rights

		DocumentModel parent = session.getDocument(new PathRef(domainPath));
		for (final String name : pathList) {
			boolean found = false;
			final String pathStr = parent.getPathAsString();
			if (COMMENTS_DIRECTORY.equals(name)) {
				final List<DocumentModel> children = session.getChildren(new PathRef(pathStr), HIDDEN_FOLDER);
				for (final DocumentModel documentModel : children) {
					if (COMMENTS_DIRECTORY.equals(documentModel.getTitle())) {
						found = true;
						parent = documentModel;
						break;
					}
				}
			} else {
				final DocumentRef ref = new PathRef(pathStr, name);
				if (session.exists(ref)) {
					parent = session.getDocument(ref);
					found = true;
				}

			}
			if (!found) {
				DocumentModel hiddenFolderDoc = session.createDocumentModel(pathStr, name, HIDDEN_FOLDER);
				DublincoreSchemaUtils.setTitle(hiddenFolderDoc, name);
				DublincoreSchemaUtils.setDescription(hiddenFolderDoc, "");
				DublincoreSchemaUtils.setCreatedDate(hiddenFolderDoc, Calendar.getInstance());
				hiddenFolderDoc = session.createDocument(hiddenFolderDoc);
				setFolderPermissions(hiddenFolderDoc);
				parent = hiddenFolderDoc;
				session.save();
			}
		}

		final String pathStr = parent.getPathAsString();
		PathSegmentService pss;
		try {
			pss = Framework.getService(PathSegmentService.class);
		} catch (final Exception e) {
			throw new ClientException(e);
		}

		comment.setPathInfo(pathStr, pss.generatePathSegment(comment));
		comment = session.createDocument(comment);
		setCommentPermissions(comment);
		LOGGER.debug(session, STLogEnumImpl.LOG_DEBUG_TEC, "created comment with id=" + comment.getId());

		session.saveDocument(comment);

		return comment;
	}

	protected static void notifyEvent(final CoreSession session, final DocumentModel docModel, final String eventType,
			final DocumentModel parent, final DocumentModel child, final NuxeoPrincipal principal)
			throws ClientException {

		final DocumentEventContext ctx = new DocumentEventContext(session, principal, docModel);
		final Map<String, Serializable> props = new HashMap<String, Serializable>();
		if (parent != null) {
			props.put(CommentConstants.PARENT_COMMENT, parent);
		}
		props.put(CommentConstants.COMMENT, child);
		props.put(CommentConstants.COMMENT_TEXT,
				(String) child.getProperty(STSchemaConstant.COMMENT_SCHEMA, STSchemaConstant.COMMENT_TEXT_PROPERTY));
		props.put("category", CommentConstants.EVENT_COMMENT_CATEGORY);
		ctx.setProperties(props);
		final Event event = ctx.newEvent(eventType);

		try {
			final EventProducer evtProducer = Framework.getService(EventProducer.class);
			evtProducer.fireEvent(event);
		} catch (final Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_SEND_EVENT_TEC, "Error while send message", e);
		}
		// send also a synchronous Seam message so the CommentManagerActionBean
		// can rebuild its list
		// Events.instance().raiseEvent(eventType, docModel);
	}

	private static void setFolderPermissions(final DocumentModel folderDoc) {
		final ACP acp = new ACPImpl();
		final ACE grantAddChildren = new ACE(MEMBERS, SecurityConstants.ADD_CHILDREN, true);
		final ACE grantRemoveChildren = new ACE(MEMBERS, SecurityConstants.REMOVE_CHILDREN, true);
		final ACE grantRemove = new ACE(MEMBERS, SecurityConstants.REMOVE, true);
		final ACL acl = new ACLImpl();
		acl.setACEs(new ACE[] { grantAddChildren, grantRemoveChildren, grantRemove });
		acp.addACL(acl);
		try {
			folderDoc.setACP(acp, true);
		} catch (final ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	private static void setCommentPermissions(final DocumentModel commentDoc) {
		final ACP acp = new ACPImpl();
		final ACE grantRead = new ACE(SecurityConstants.EVERYONE, SecurityConstants.READ, true);
		final ACE grantRemove = new ACE(MEMBERS, SecurityConstants.REMOVE, true);
		final ACL acl = new ACLImpl();
		acl.setACEs(new ACE[] { grantRead, grantRemove });
		acp.addACL(acl);
		try {
			commentDoc.setACP(acp, true);
		} catch (final ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	private String[] getCommentPathList(final DocumentModel commentDoc) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STR, Locale.FRENCH);

		return new String[] { COMMENTS_DIRECTORY, dateFormat.format(getCommentTimeStamp(commentDoc)) };
	}

	/**
	 * @deprecated if the caller is remote, we cannot obtain the session
	 */
	@Deprecated
	private static String getCurrentUser(final DocumentModel target) throws ClientException {
		final CoreSession userSession = target.getCoreSession();
		if (userSession == null) {
			throw new ClientException("userSession is null, do not invoke this method when the user is not local");
		}
		return userSession.getPrincipal().getName();
	}

	private static Date getCommentTimeStamp(final DocumentModel comment) {
		Calendar creationDate = DublincoreSchemaUtils.getCreatedDate(comment);
		if (creationDate == null) {
			creationDate = Calendar.getInstance();
		}
		return creationDate.getTime();
	}

	@Override
	public void deleteComment(final DocumentModel docModel, final DocumentModel comment) throws ClientException {
		LoginContext loginContext = null;
		CoreSession session = null;
		try {
			loginContext = Framework.login();
			session = openCoreSession(docModel.getRepositoryName());

			if (session == null) {
				throw new ClientException("Unable to acess repository for comment: " + comment.getId());
			}
			final DocumentRef ref = comment.getRef();
			if (!session.exists(ref)) {
				throw new ClientException("Comment Document does not exist: " + comment.getId());
			}

			final NuxeoPrincipal author = getAuthor(comment);
			LOGGER.info(session, STLogEnumImpl.DEL_COMMENT_TEC, comment);
			session.removeDocument(ref);

			notifyEvent(session, docModel, CommentEvents.COMMENT_REMOVED, null, comment, author);

			session.save();

		} catch (final Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_DEL_COM_TEC, comment, exc);
			throw new ClientException("failed to delete comment", exc);
		} finally {
			closeCoreSession(loginContext, session);
		}
	}

	@Override
	public DocumentModel createComment(final DocumentModel docModel, final DocumentModel parent,
			final DocumentModel child) throws ClientException {
		LoginContext loginContext = null;
		CoreSession session = null;
		try {
			loginContext = Framework.login();
			session = openCoreSession(docModel.getRepositoryName());

			final String author = updateAuthor(docModel, child);
			final DocumentModel parentDocModel = session.getDocument(parent.getRef());
			final DocumentModel newComment = internalCreateComment(session, parentDocModel, child, null);

			final UserManager userManager = Framework.getService(UserManager.class);
			final NuxeoPrincipal principal = userManager.getPrincipal(author);
			notifyEvent(session, docModel, CommentEvents.COMMENT_ADDED, parent, newComment, principal);

			session.save();
			return newComment;

		} catch (final Exception e) {
			throw new ClientException(e);
		} finally {
			closeCoreSession(loginContext, session);
		}
	}

	private static NuxeoPrincipal getAuthor(final DocumentModel docModel) {
		try {
			final String creator = DublincoreSchemaUtils.getCreator(docModel);
			final UserManager userManager = Framework.getService(UserManager.class);
			return userManager.getPrincipal(creator);
		} catch (final Exception e) {
			LOGGER.error(docModel.getCoreSession(), STLogEnumImpl.FAIL_GET_PRIN_TEC, docModel, e);
			return null;
		}
	}

	@Override
	public List<DocumentModel> getComments(final DocumentModel docModel, final DocumentModel parent)
			throws ClientException {
		try {
			return getComments(parent);
		} catch (final Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public List<DocumentModel> getDocumentsForComment(final DocumentModel comment) throws ClientException {

		LoginContext loginContext = null;
		CoreSession session = null;
		try {
			loginContext = Framework.login();
			session = openCoreSession(comment.getRepositoryName());

			final String commentedDocId = (String) comment.getProperty("comment", "commentedDocId");
			final String query = String.format(COMMENTED_QUERY, commentedDocId);
			final DocumentModelList dml = session.query(query);

			return dml;
		} catch (final Exception e) {
			throw new ClientException(e);
		} finally {
			closeCoreSession(loginContext, session);
		}

	}

	@Override
	public DocumentModel createLocatedComment(final DocumentModel docModel, final DocumentModel comment,
			final String path) throws ClientException {
		LoginContext loginContext = null;
		CoreSession session = null;
		DocumentModel createdComment;
		try {
			loginContext = Framework.login();
			session = openCoreSession(docModel.getRepositoryName());
			createdComment = internalCreateComment(session, docModel, comment, path);
			session.save();
		} catch (final Exception e) {
			throw new ClientException(e);
		} finally {
			closeCoreSession(loginContext, session);
		}

		return createdComment;
	}
	
	/**
	 * @return true si l'intersection entre les postes de l'utilisateur courant
	 *         et ceux de l'auteur de la note est non vide.
	 */
	public boolean isInAuthorPoste(STComment comment, String userName) throws ClientException {
		if (comment == null) {
			return false;
		}
		
		// Postes de l'auteur du poste
		STPostesService posteService = STServiceLocator.getSTPostesService();
		List<String> authorPostes = posteService.getAllPosteIdsForUser(comment.getAuthor());

		// Postes de l'utilisateur courant
		List<String> currentUserPostes = posteService.getAllPosteIdsForUser(userName);

		// Vérification de l'intersection
		for (String currentUserPosteId : currentUserPostes) {
			if (authorPostes.contains(currentUserPosteId)) {
				return true;
			}
		}

		return false;
	}
}
