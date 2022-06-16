package fr.dila.st.core.service;

import static fr.dila.st.api.constant.STConstant.COMMENT_DOCUMENT_TYPE;
import static fr.dila.st.api.constant.STEventConstant.EVENT_COMMENT_UPDATED;
import static fr.dila.st.api.constant.STSchemaConstant.COMMENT_PARENT_COMMENT_ID_PROPERTY;
import static fr.dila.st.api.constant.STSchemaConstant.COMMENT_SCHEMA;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.nuxeo.ecm.platform.comment.api.ExternalEntityConstants.EXTERNAL_ENTITY_FACET;
import static org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants.COMMENT_DOC_TYPE;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.domain.comment.STComment;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.schema.RoutingTaskSchemaUtils;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PartialList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.schema.PrefetchInfo;
import org.nuxeo.ecm.platform.comment.api.Comment;
import org.nuxeo.ecm.platform.comment.api.CommentEvents;
import org.nuxeo.ecm.platform.comment.api.CommentManager;
import org.nuxeo.ecm.platform.comment.api.Comments;
import org.nuxeo.ecm.platform.comment.api.ExternalEntity;
import org.nuxeo.ecm.platform.comment.api.exceptions.CommentNotFoundException;
import org.nuxeo.ecm.platform.comment.api.exceptions.CommentSecurityException;
import org.nuxeo.ecm.platform.comment.impl.AbstractCommentManager;
import org.nuxeo.ecm.platform.comment.impl.CommentSorter;
import org.nuxeo.ecm.platform.dublincore.constants.DublinCoreConstants;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

/**
 *
 * based on implementation : org.nuxeo.ecm.platform.comment.impl.CommentManagerImpl
 *
 * Realise le lien entre les documents et leur commentaire en ajoutant l'id du document dans le commentaire. (à la place
 * de l'utilisation systeme de relation JENA)
 *
 * Les documents sont recupérés par des requetes NXQL
 */
public class CommentManagerImpl extends AbstractCommentManager implements CommentManager {
    public static final String COMMENTS_DIRECTORY = "Comments";

    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CommentManagerImpl.class);

    private static final String COMMENTS_QUERY =
        "SELECT * FROM Comment WHERE " +
        STSchemaConstant.COMMENT_PREFIX +
        ":" +
        COMMENT_PARENT_COMMENT_ID_PROPERTY +
        "= '%s' and ecm:isProxy = 0";

    private static final String HIDDEN_FOLDER = "HiddenFolder";

    private static final String MEMBERS = "members";

    public CommentManagerImpl() {
        // Default constructor
    }

    @Override
    public List<DocumentModel> getComments(final CoreSession session, final DocumentModel docModel) {
        docModel.reset();

        List<String> docIds;
        if (COMMENT_DOCUMENT_TYPE.equals(docModel.getType())) {
            // On cherche les enfants
            final String docId = docModel.getId();

            final String query = String.format(COMMENTS_QUERY, docId);
            docIds = QueryUtils.doQueryForIds(session, query);
        } else {
            // On cherche les commentaires principaux
            docIds = RoutingTaskSchemaUtils.getComments(docModel);
        }

        List<DocumentModel> commentList = new ArrayList<>(session.getDocuments(docIds, getPrefetchInfo()));

        final CommentSorter sorter = new CommentSorter(true);
        commentList.sort(sorter);

        return commentList;
    }

    private static PrefetchInfo getPrefetchInfo() {
        return new PrefetchInfo(
            String.join(
                ",",
                Arrays.asList(DublinCoreConstants.DUBLINCORE_SCHEMA_NAME, COMMENT_SCHEMA, "externalEntity")
            )
        );
    }

    @Override
    public List<DocumentModel> getComments(final DocumentModel docModel) {
        try (CloseableCoreSession session = CoreInstance.openCoreSessionSystem(docModel.getRepositoryName())) {
            return getComments(session, docModel);
        }
    }

    @Override
    public DocumentModel createComment(final DocumentModel docModel, final String comment, final String author) {
        try (CloseableCoreSession session = CoreInstance.openCoreSessionSystem(docModel.getRepositoryName())) {
            DocumentModel commentDM = session.createDocumentModel(COMMENT_DOCUMENT_TYPE);
            commentDM.setProperty(COMMENT_SCHEMA, STSchemaConstant.COMMENT_TEXT_PROPERTY, comment);
            commentDM.setProperty(COMMENT_SCHEMA, STSchemaConstant.COMMENT_AUTHOR_PROPERTY, author);
            commentDM.setProperty(
                COMMENT_SCHEMA,
                STSchemaConstant.COMMENT_CREATION_DATE_PROPERTY,
                Calendar.getInstance()
            );
            commentDM = internalCreateComment(session, docModel, commentDM, null);
            session.save();

            return commentDM;
        }
    }

    @Override
    public DocumentModel createComment(final DocumentModel docModel, final String comment) {
        final String author = getCurrentUser(docModel);
        return createComment(docModel, comment, author);
    }

    /**
     * If the author property on comment is not set, retrieve the author name from the session
     */
    protected static String updateAuthor(final DocumentModel docModel, final DocumentModel comment) {
        // update the author if not set
        String author = getCommentAuthor(comment);
        if (author == null) {
            LOGGER.debug(
                STLogEnumImpl.LOG_DEBUG_TEC,
                "deprecated use of createComment: the client should set the author property on document"
            );
            author = getCurrentUser(docModel);
            comment.setProperty(COMMENT_SCHEMA, STSchemaConstant.COMMENT_AUTHOR_PROPERTY, author);
        }
        return author;
    }

    @Override
    public DocumentModel createComment(final DocumentModel docModel, final DocumentModel comment) {
        try (CloseableCoreSession session = CoreInstance.openCoreSessionSystem(docModel.getRepositoryName())) {
            DocumentModel doc = internalCreateComment(session, docModel, comment, null);
            session.save();
            doc.detach(true);
            return doc;
        }
    }

    /**
     * Prise en compte de la nature n-n du lien entre la note et l'objet
     * commenté.
     */
    protected DocumentModel internalCreateComment(
        final CoreSession session,
        final DocumentModel docModel,
        final DocumentModel comment,
        final String path
    ) {
        DocumentModel createdComment;

        if (COMMENT_DOCUMENT_TYPE.equals(docModel.getType())) {
            // Réponse à une note d'étape
            comment.setProperty(COMMENT_SCHEMA, COMMENT_PARENT_COMMENT_ID_PROPERTY, docModel.getId());

            createdComment = createCommentDocModel(session, docModel, comment, path);
        } else {
            // Note d'étape
            createdComment = createCommentDocModel(session, docModel, comment, path);

            List<String> commentIds = RoutingTaskSchemaUtils.getComments(docModel);

            commentIds.add(createdComment.getId());

            RoutingTaskSchemaUtils.setComments(docModel, commentIds);

            session.saveDocument(docModel);
        }

        notifyEvent(session, CommentEvents.COMMENT_ADDED, docModel, createdComment);

        return createdComment;
    }

    protected DocumentModel createCommentDocModel(
        final CoreSession session,
        final DocumentModel docModel,
        final DocumentModel commentDoc,
        final String path
    ) {
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
        PathSegmentService pss = ServiceUtil.getRequiredService(PathSegmentService.class);

        comment.setPathInfo(pathStr, pss.generatePathSegment(comment));
        comment = session.createDocument(comment);
        setCommentPermissions(comment);
        LOGGER.debug(session, STLogEnumImpl.LOG_DEBUG_TEC, "created comment with id=" + comment.getId());

        session.saveDocument(comment);

        return comment;
    }

    private static void setFolderPermissions(final DocumentModel folderDoc) {
        final ACP acp = new ACPImpl();
        final ACE grantAddChildren = new ACE(MEMBERS, SecurityConstants.ADD_CHILDREN, true);
        final ACE grantRemoveChildren = new ACE(MEMBERS, SecurityConstants.REMOVE_CHILDREN, true);
        final ACE grantRemove = new ACE(MEMBERS, SecurityConstants.REMOVE, true);
        final ACL acl = new ACLImpl();
        acl.setACEs(new ACE[] { grantAddChildren, grantRemoveChildren, grantRemove });
        acp.addACL(acl);
        folderDoc.setACP(acp, true);
    }

    private static void setCommentPermissions(final DocumentModel commentDoc) {
        ACP acp = ObjectHelper.requireNonNullElseGet(commentDoc.getACP(), ACPImpl::new);
        final ACE grantRead = new ACE(SecurityConstants.EVERYONE, SecurityConstants.READ, true);
        final ACE grantReadWrite = ACE.builder(getCommentAuthor(commentDoc), SecurityConstants.READ_WRITE).build();
        final ACE grantRemove = new ACE(MEMBERS, SecurityConstants.REMOVE, true);
        final ACL acl = acp.getOrCreateACL(ACL.LOCAL_ACL);
        acl.setACEs(new ACE[] { grantRead, grantReadWrite, grantRemove });
        acp.addACL(acl);
        commentDoc.setACP(acp, true);
    }

    private static String getCommentAuthor(DocumentModel commentDoc) {
        return commentDoc.getAdapter(STComment.class).getAuthor();
    }

    private String[] getCommentPathList(final DocumentModel commentDoc) {
        return new String[] {
            COMMENTS_DIRECTORY,
            SolonDateConverter.YEAR_MONTH.format(getCommentTimeStamp(commentDoc))
        };
    }

    /**
     * @deprecated if the caller is remote, we cannot obtain the session
     */
    @Deprecated
    private static String getCurrentUser(final DocumentModel target) {
        final CoreSession userSession = target.getCoreSession();
        if (userSession == null) {
            throw new NuxeoException("userSession is null, do not invoke this method when the user is not local");
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
    public DocumentModel createComment(
        final DocumentModel docModel,
        final DocumentModel parent,
        final DocumentModel child
    ) {
        try (CloseableCoreSession session = CoreInstance.openCoreSessionSystem(docModel.getRepositoryName())) {
            DocumentModel parentDocModel = session.getDocument(parent.getRef());
            String containerPath = parent.getPath().removeLastSegments(1).toString();
            DocumentModel newComment = internalCreateComment(session, parentDocModel, child, containerPath);

            session.save();
            return newComment;
        }
    }

    @Override
    protected NuxeoPrincipal getAuthor(final DocumentModel docModel) {
        final String creator = DublincoreSchemaUtils.getCreator(docModel);
        final UserManager userManager = Framework.getService(UserManager.class);
        return userManager.getPrincipal(creator);
    }

    @Override
    public List<DocumentModel> getDocumentsForComment(DocumentModel comment) {
        return CoreInstance.doPrivileged(
            comment.getRepositoryName(),
            session -> {
                String parentCommentId = comment.getAdapter(STComment.class).getParentCommentId();
                return session.getDocuments(singletonList(parentCommentId), new PrefetchInfo(COMMENT_SCHEMA));
            }
        );
    }

    @Override
    public DocumentModel createLocatedComment(
        final DocumentModel docModel,
        final DocumentModel comment,
        final String path
    ) {
        try (CloseableCoreSession session = CoreInstance.openCoreSessionSystem(docModel.getRepositoryName())) {
            DocumentModel createdComment = internalCreateComment(session, docModel, comment, path);
            session.save();
            return createdComment;
        }
    }

    @Override
    public Comment createComment(CoreSession session, Comment comment)
        throws CommentNotFoundException, CommentSecurityException {
        DocumentRef commentRef = new IdRef(comment.getParentId());
        if (!session.exists(commentRef)) {
            throw new CommentNotFoundException("The document " + comment.getParentId() + " does not exist.");
        }
        DocumentModel docToComment = session.getDocument(commentRef);
        DocumentModel commentModel = session.createDocumentModel(COMMENT_DOC_TYPE);
        commentModel.setPropertyValue("dc:created", Calendar.getInstance());

        Comments.commentToDocumentModel(comment, commentModel);
        if (comment instanceof ExternalEntity) {
            commentModel.addFacet(EXTERNAL_ENTITY_FACET);
            Comments.externalEntityToDocumentModel((ExternalEntity) comment, commentModel);
        }

        DocumentModel createdCommentModel = createComment(docToComment, commentModel);
        return Comments.newComment(createdCommentModel);
    }

    @Override
    public void deleteComment(CoreSession session, String commentId)
        throws CommentNotFoundException, CommentSecurityException {
        DocumentRef commentRef = new IdRef(commentId);
        if (!session.exists(commentRef)) {
            throw new CommentNotFoundException("Le commentaire " + commentId + " n'existe pas.");
        }

        CoreInstance.doPrivileged(
            session,
            privilegedSession -> {
                // il faut bien récupérer les 2 documents avant la suppression pour ne pas avoir d'erreur
                DocumentModel commentedDoc = Optional
                    .ofNullable(getTopLevelCommentAncestor(privilegedSession, commentRef))
                    .map(privilegedSession::getDocument)
                    .orElse(null);
                DocumentModel commentDoc = privilegedSession
                    .getDocuments(singletonList(commentId), new PrefetchInfo(COMMENT_SCHEMA))
                    .get(0);

                LOGGER.info(privilegedSession, STLogEnumImpl.DEL_COMMENT_TEC, commentDoc);
                privilegedSession.removeDocument(commentRef);

                notifyEvent(privilegedSession, CommentEvents.COMMENT_REMOVED, commentedDoc, commentDoc);

                privilegedSession.save();
            }
        );
    }

    @Override
    public void deleteComment(final DocumentModel docModel, final DocumentModel comment) {
        try (CloseableCoreSession session = CoreInstance.openCoreSessionSystem(docModel.getRepositoryName())) {
            comment.detach(true);
            DocumentRef ref = comment.getRef();
            if (!session.exists(ref)) {
                throw new NuxeoException("Comment Document does not exist: " + comment.getId());
            }

            session.removeDocument(ref);

            notifyEvent(session, CommentEvents.COMMENT_REMOVED, docModel, comment);

            session.save();
        }
    }

    @Override
    public void deleteExternalComment(CoreSession session, String entityId)
        throws CommentNotFoundException, CommentSecurityException {
        throw new UnsupportedOperationException(
            "Delete a comment from its external entity id is not possible through this implementation"
        );
    }

    @Override
    public Comment getComment(CoreSession session, String commentId)
        throws CommentNotFoundException, CommentSecurityException {
        DocumentRef commentRef = new IdRef(commentId);
        if (!session.exists(commentRef)) {
            throw new CommentNotFoundException("The document " + commentId + " does not exist.");
        }
        DocumentModel commentModel = session.getDocument(commentRef);
        return Comments.newComment(commentModel);
    }

    @Override
    public PartialList<Comment> getComments(
        CoreSession session,
        String documentId,
        Long pageSize,
        Long currentPageIndex,
        boolean sortAscending
    ) {
        return CoreInstance.doPrivileged(session, this.getComments(documentId, pageSize, currentPageIndex));
    }

    private Function<CoreSession, PartialList<Comment>> getComments(
        String documentId,
        Long pageSize,
        Long currentPageIndex
    ) {
        return session -> {
            DocumentRef docRef = new IdRef(documentId);
            if (!session.exists(docRef)) {
                return new PartialList<>(Collections.emptyList(), 0); // NOSONAR
            }

            DocumentModel commentedDoc = session.getDocument(docRef);
            // do a dummy implementation of pagination for former comment manager implementation
            List<DocumentModel> comments = getComments(commentedDoc);
            long maxSize = pageSize == null || pageSize <= 0 ? comments.size() : pageSize;
            long offset = currentPageIndex == null || currentPageIndex <= 0 ? 0 : currentPageIndex * pageSize;
            return comments
                .stream()
                .sorted(Comparator.comparing(doc -> (Calendar) doc.getPropertyValue("dc:created")))
                .skip(offset)
                .limit(maxSize)
                .map(Comments::newComment)
                .collect(collectingAndThen(toList(), list -> new PartialList<>(list, comments.size())));
        };
    }

    @Override
    public Comment getExternalComment(CoreSession session, String entityId) {
        throw new UnsupportedOperationException(
            "Get a comment from its external entity id is not possible through this implementation"
        );
    }

    @Override
    public boolean hasFeature(Feature feature) {
        switch (feature) {
            case COMMENTS_LINKED_WITH_PROPERTY:
                return false;
            default:
                throw new UnsupportedOperationException(feature.name());
        }
    }

    @Override
    public Comment updateComment(CoreSession session, String commentId, Comment comment) {
        DocumentModel updatedCommentDoc = session.getDocument(new IdRef(comment.getId()));

        Comments.commentToDocumentModel(comment, updatedCommentDoc);

        session.saveDocument(updatedCommentDoc);

        notifyEvent(session, EVENT_COMMENT_UPDATED, null, updatedCommentDoc);

        return comment;
    }

    @Override
    public Comment updateExternalComment(CoreSession session, String entityId, Comment comment) {
        throw new UnsupportedOperationException(
            "Update a comment from its external entity id is not possible through this implementation"
        );
    }

    @Override
    public DocumentRef getTopLevelCommentAncestor(CoreSession session, DocumentRef commentIdRef) {
        NuxeoPrincipal principal = session.getPrincipal();
        return CoreInstance.doPrivileged(
            session,
            privilegedSession -> {
                if (!privilegedSession.exists(commentIdRef)) {
                    throw new CommentNotFoundException(String.format("The comment %s does not exist.", commentIdRef));
                }

                DocumentModel documentModel = privilegedSession
                    .getDocuments(singletonList(commentIdRef.toString()), new PrefetchInfo(COMMENT_SCHEMA))
                    .get(0);
                while (documentModel != null && documentModel.hasSchema(COMMENT_SCHEMA)) {
                    List<DocumentModel> ancestors = getDocumentsForComment(documentModel);
                    documentModel = ancestors.isEmpty() ? null : ancestors.get(0);
                }

                if (
                    documentModel != null &&
                    !privilegedSession.hasPermission(principal, documentModel.getRef(), SecurityConstants.READ)
                ) {
                    throw new CommentSecurityException(
                        "The user " +
                        principal.getName() +
                        " does not have access to the comments of document " +
                        documentModel.getRef().reference()
                    );
                }

                return documentModel != null ? documentModel.getRef() : null;
            }
        );
    }
}
