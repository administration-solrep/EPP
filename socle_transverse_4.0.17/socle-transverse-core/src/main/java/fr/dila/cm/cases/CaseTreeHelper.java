package fr.dila.cm.cases;

import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import java.util.Date;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;

/**
 * Helper for mail tree
 * <p>
 * Emails and Mail envelopes are created within trees of folder.
 *
 * @author <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 */
public class CaseTreeHelper {
    public static final String DELETED_STATE = "deleted";

    /**
     * Find or create a set of folders representing the date hierarchy
     *
     * @return the last child created (day)
     */
    public static DocumentModel getOrCreateDateTreeFolder(
        CoreSession session,
        DocumentModel root,
        Date date,
        String folderType
    ) {
        String subPath = SolonDateConverter.DATE_SLASH_REVERSE.format(date);
        return getOrCreatePath(session, root, subPath, folderType);
    }

    public static DocumentModel getOrCreatePath(
        CoreSession session,
        DocumentModel root,
        String subPath,
        String folderType
    ) {
        String[] pathSplit = subPath.split("/");
        String parentPath = root.getPathAsString();
        DocumentModel child = root;
        for (String id : pathSplit) {
            child = getOrCreate(session, parentPath, id, folderType);
            parentPath = child.getPathAsString();
        }
        return child;
    }

    public static synchronized DocumentModel getOrCreate(
        CoreSession session,
        String rootPath,
        String id,
        String folderType
    ) {
        String path = String.format("%s/%s", rootPath, id);
        DocumentRef pathRef = new PathRef(path);
        boolean exists = session.exists(pathRef);
        if (exists) {
            DocumentModel existing = session.getDocument(pathRef);
            if (!DELETED_STATE.equals(existing.getCurrentLifeCycleState())) {
                return existing;
            }
        }
        // create it
        DocumentModel newDocument = session.createDocumentModel(
            rootPath,
            IdUtils.generateId(id, "-", true, 24),
            folderType
        );
        DublincorePropertyUtil.setTitle(newDocument, id);
        newDocument = session.createDocument(newDocument);
        return newDocument;
    }
}
