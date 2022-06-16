package fr.dila.st.ui.bean;

import java.io.Serializable;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @see nuxeo-features/nuxeo-platform-comment/nuxeo-platform-comment-web/src/main/java/org/nuxeo/ecm/platform/comment/web/ThreadEntry.java
 * @author tlombard
 */
public class ThreadEntry implements Serializable {
    private static final long serialVersionUID = 8765190624691092L;

    private DocumentModel comment;

    private int depth;

    public ThreadEntry(DocumentModel comment, int depth) {
        this.comment = comment;
        this.depth = depth;
    }

    public DocumentModel getComment() {
        return comment;
    }

    public int getDepth() {
        return depth;
    }

    public String getId() {
        return comment.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ThreadEntry)) {
            return false;
        }
        ThreadEntry other = (ThreadEntry) obj;
        String id = getId();
        String otherId = other.getId();
        return id == null ? otherId == null : id.equals(otherId);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
