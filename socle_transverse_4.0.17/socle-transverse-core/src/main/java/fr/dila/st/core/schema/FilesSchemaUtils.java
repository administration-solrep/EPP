package fr.dila.st.core.schema;

import static fr.dila.st.api.constant.FilesSchemaConstant.PROP_FILE;
import static fr.dila.st.api.constant.FilesSchemaConstant.PROP_FILES;
import static fr.dila.st.api.constant.FilesSchemaConstant.SCHEMA_FILES;
import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 * @author SCE
 *
 */
public final class FilesSchemaUtils {

    @SuppressWarnings("unchecked")
    public static List<Blob> getFiles(final DocumentModel doc) {
        final List<Map<String, Serializable>> files = (List<Map<String, Serializable>>) doc.getProperty(
            SCHEMA_FILES,
            PROP_FILES
        );
        return files
            .stream()
            .map(Map::entrySet)
            .flatMap(Set::stream)
            .map(Entry::getValue)
            .filter(v -> Blob.class.isAssignableFrom(v.getClass()))
            .map(Blob.class::cast)
            .collect(toList());
    }

    public static void setFiles(final DocumentModel doc, final Collection<Blob> fileList) {
        final List<Map<String, Serializable>> maps = fileList
            .stream()
            .map(v -> Collections.singletonMap(PROP_FILE, (Serializable) v))
            .collect(toList());
        doc.setProperty(SCHEMA_FILES, PROP_FILES, maps);
    }

    private FilesSchemaUtils() {
        // do nothing
    }
}
