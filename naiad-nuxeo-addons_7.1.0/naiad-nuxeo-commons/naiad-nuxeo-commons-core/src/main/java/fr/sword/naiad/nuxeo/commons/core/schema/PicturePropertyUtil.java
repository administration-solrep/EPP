package fr.sword.naiad.nuxeo.commons.core.schema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Classe utilitaire contenant des méthodes d'accès aux propriétés du schéma picture.
 * 
 * @author fmh
 */
public final class PicturePropertyUtil {
	/**
	 * Constructeur privé.
	 */
	private PicturePropertyUtil() {
		// Classe utilitaire
	}

	/**
	 * Retourne la liste des vues (tailles) d'une Picture.
	 */
	public static List<Map<String, Serializable>> getViews(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getMapStringSerializableListProperty(document, CommonSchemaConstant.SCHEMA_PICTURE,
				CommonSchemaConstant.PROP_PICTURE_VIEWS);
	}
}
