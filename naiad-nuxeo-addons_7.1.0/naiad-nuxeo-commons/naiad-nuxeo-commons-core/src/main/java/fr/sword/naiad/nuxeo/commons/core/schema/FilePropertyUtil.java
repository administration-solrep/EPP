package fr.sword.naiad.nuxeo.commons.core.schema;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Class permettant de manipuler les propriétés du schema File
 * 
 * @author SPL
 * 
 */
public final class FilePropertyUtil {
	/**
	 * class utilitaire
	 */
	private FilePropertyUtil() {
		// do nothing
	}

	public static Blob getFile(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getBlobProperty(document,
				CommonSchemaConstant.SCHEMA_FILE,
				CommonSchemaConstant.PROP_FILE_CONTENT);
	}

	public static void setFile(DocumentModel document, Blob file) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_FILE, CommonSchemaConstant.PROP_FILE_CONTENT, file);
	}


}
