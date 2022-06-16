package fr.sword.naiad.nuxeo.commons.core.schema;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Classe utilitaire contenant des méthodes d'accès aux propriétés du schéma picturebook.
 * 
 * @author fmh
 */
public final class PictureBookPropertyUtil {
	/**
	 * Constructeur privé.
	 */
	private PictureBookPropertyUtil() {
		// Classe utilitaire
	}

	/**
	 * Récupère la liste des templates d'images utilisés pour générer plusieurs tailles d'images.
	 */
	public static List<Map<String, Serializable>> getPictureTemplates(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getMapStringSerializableListProperty(document, CommonSchemaConstant.SCHEMA_PICTUREBOOK,
				CommonSchemaConstant.PROP_PICTUREBOOK_PICTURETEMPLATES);
	}

	/**
	 * Définit la liste des templates d'images.
	 */
	public static void setPictureTemplates(DocumentModel document, List<Map<String, Serializable>> templates)
			throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_PICTUREBOOK,
				CommonSchemaConstant.PROP_PICTUREBOOK_PICTURETEMPLATES, templates);
	}
}
