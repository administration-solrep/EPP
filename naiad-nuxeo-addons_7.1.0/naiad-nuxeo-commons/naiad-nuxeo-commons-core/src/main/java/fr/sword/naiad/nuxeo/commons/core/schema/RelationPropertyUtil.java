package fr.sword.naiad.nuxeo.commons.core.schema;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Classe utilitaire manipulant les propriétés du schéma relation.
 * 
 * @author fmh
 */
public final class RelationPropertyUtil {
	/**
	 * Constructeur privé.
	 */
	private RelationPropertyUtil() {
		// Classe utilitaire
	}

	public static String getSourceId(DocumentModel document)
			throws NuxeoException {
		return PropertyUtil.getStringProperty(document,
				CommonSchemaConstant.SCHEMA_RELATION,
				CommonSchemaConstant.PROP_RELATION_SOURCE);
	}

	public static void setSourceId(DocumentModel document, String sourceId)
			throws NuxeoException {
		PropertyUtil.setProperty(document,
				CommonSchemaConstant.SCHEMA_RELATION,
				CommonSchemaConstant.PROP_RELATION_SOURCE, sourceId);
	}

	public static String getTargetId(DocumentModel document)
			throws NuxeoException {
		return PropertyUtil.getStringProperty(document,
				CommonSchemaConstant.SCHEMA_RELATION,
				CommonSchemaConstant.PROP_RELATION_TARGET);
	}

	public static void setTargetId(DocumentModel document, String targetId)
			throws NuxeoException {
		PropertyUtil.setProperty(document,
				CommonSchemaConstant.SCHEMA_RELATION,
				CommonSchemaConstant.PROP_RELATION_TARGET, targetId);
	}
}
