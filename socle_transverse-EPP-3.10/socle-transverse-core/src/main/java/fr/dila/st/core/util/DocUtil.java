package fr.dila.st.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DataModel;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.TypeProvider;
import org.nuxeo.runtime.api.Framework;

import fr.dila.st.core.schema.DublincoreSchemaUtils;

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
	 * @throws ClientException
	 */
	public static void copySchema(DocumentModel src, DocumentModel dst, String schema) throws ClientException {
		DataModel dataModelSrc = src.getDataModel(schema);
		TypeProvider typeProvider = Framework.getLocalService(SchemaManager.class);
		DataModel newDataModel = DocumentModelImpl.cloneDataModel(typeProvider.getSchema(dataModelSrc.getSchema()),
				dataModelSrc);
		dst.setProperties(schema, newDataModel.getMap());
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
		final List<E> results = new ArrayList<E>();
		for (DocumentModel doc : docs) {
			results.add(doc.getAdapter(adapterClazz));
		}
		return results;
	}

}
