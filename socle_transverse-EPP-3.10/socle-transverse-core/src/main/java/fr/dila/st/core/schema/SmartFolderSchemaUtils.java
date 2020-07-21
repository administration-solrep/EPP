package fr.dila.st.core.schema;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Manip des propriétés du schema smart_folder
 * 
 * @author SPL
 * 
 */
public final class SmartFolderSchemaUtils {

	/**
	 * utility class
	 */
	private SmartFolderSchemaUtils() {
		// do nothing
	}

	public static String getQueryPart(final DocumentModel document) {
		return PropertyUtil.getStringProperty(document, STRequeteConstants.SMART_FOLDER_SCHEMA,
				STRequeteConstants.SMART_FOLDER_QUERY_PART_PROP);
	}

	public static void setQueryPart(final DocumentModel document, final String whereClause) {
		PropertyUtil.setProperty(document, STRequeteConstants.SMART_FOLDER_SCHEMA,
				STRequeteConstants.SMART_FOLDER_QUERY_PART_PROP, whereClause);
	}

}
