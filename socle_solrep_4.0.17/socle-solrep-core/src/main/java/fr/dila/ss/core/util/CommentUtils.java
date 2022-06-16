package fr.dila.ss.core.util;

import java.util.Calendar;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.comment.workflow.utils.CommentsConstants;

public final class CommentUtils {

    private CommentUtils() {}

    public static DocumentModel initComment(CoreSession session, String commentContent) {
        DocumentModel newCommentDoc = session.createDocumentModel(CommentsConstants.COMMENT_DOC_TYPE);

        NuxeoPrincipal principal = session.getPrincipal();
        newCommentDoc.setPropertyValue(CommentsConstants.COMMENT_AUTHOR, principal.getName());
        newCommentDoc.setPropertyValue(CommentsConstants.COMMENT_TEXT, commentContent);
        newCommentDoc.setPropertyValue(CommentsConstants.COMMENT_CREATION_DATE, Calendar.getInstance());
        return newCommentDoc;
    }
}
