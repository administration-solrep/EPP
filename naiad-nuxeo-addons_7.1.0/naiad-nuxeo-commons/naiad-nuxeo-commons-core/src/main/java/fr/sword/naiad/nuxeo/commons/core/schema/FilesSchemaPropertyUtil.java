package fr.sword.naiad.nuxeo.commons.core.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Manip du schema faceted_search_default
 * @author SPL
 *
 */
public final class FilesSchemaPropertyUtil {

	@Deprecated
	public static final String KEY_FILENAME = "filename";
	public static final String KEY_FILE = "file";
	
	/**
	 * utility class
	 */
	private FilesSchemaPropertyUtil(){
		// do nothing
	}
	
	public static List<Map<String, Serializable>> getFiles(final DocumentModel doc) {
		return PropertyUtil.getMapStringSerializableListProperty(doc, CommonSchemaConstant.SCHEMA_FILES, CommonSchemaConstant.PROP_FILES_FILES);
	}
	
	public static void setFiles(final DocumentModel doc, final List<Map<String, Serializable>> files) {
		PropertyUtil.setProperty(doc, CommonSchemaConstant.SCHEMA_FILES, CommonSchemaConstant.PROP_FILES_FILES, files);
	}

	@Deprecated	
	public static Map<String, Serializable> buildMapFile(String filename, Blob blob){				
		Map<String, Serializable> data = new HashMap<String, Serializable>();
		data.put(KEY_FILE, (Serializable) blob);
		return data;
	}

        public static Map<String, Serializable> buildMapFile(Blob blob){
                Map<String, Serializable> data = new HashMap<String, Serializable>();
                data.put(KEY_FILE, (Serializable) blob);
                return data;
        }
}
