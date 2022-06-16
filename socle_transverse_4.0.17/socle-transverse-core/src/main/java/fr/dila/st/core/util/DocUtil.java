package fr.dila.st.core.util;

import static java.util.stream.Collectors.toList;

import fr.dila.st.core.schema.DublincoreSchemaUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Classe utilitaire pour les documents model.
 *
 * @author jgomez
 */
public final class DocUtil {

    /**
     * utility class
     */
    private DocUtil() {
        // do nothing
    }

    /**
     * Copy le schéma d'un document.
     *
     * @param src
     * @param dst
     * @param schema
     * @return
     *
     */
    public static void copySchema(DocumentModel src, DocumentModel dst, String schema) {
        Map<String, Object> srcData = src.getProperties(schema);
        dst.setProperties(schema, srcData);
    }

    /**
     * Collecte l'ensemble des titres des documents fournis en paramètre.
     *
     * @param documentModelList
     *            Collection de documents
     * @return Tableau associatif titre -> Documents
     */
    public static Map<String, DocumentModel> collectTitleMap(Collection<DocumentModel> documentModelList) {
        Map<String, DocumentModel> uuidMap = new HashMap<String, DocumentModel>();
        if (documentModelList != null) {
            for (DocumentModel doc : documentModelList) {
                uuidMap.put(DublincoreSchemaUtils.getTitle(doc), doc);
            }
        }

        return uuidMap;
    }

    /**
     * Adapte une liste de document model de même type
     *
     * @param adapterClazz
     *            la class de l'adapter
     * @param docs
     *            les documents à adapter
     * @return une liste de documents adaptés
     *
     */
    public static <E> List<E> adapt(final List<DocumentModel> docs, final Class<E> adapterClazz) {
        return docs.stream().map(d -> d.getAdapter(adapterClazz)).collect(toList());
    }
}
