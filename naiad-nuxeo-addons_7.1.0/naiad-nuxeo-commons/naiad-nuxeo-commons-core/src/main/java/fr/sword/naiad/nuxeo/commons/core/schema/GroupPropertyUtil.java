package fr.sword.naiad.nuxeo.commons.core.schema;

import java.util.List;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Classe utilitaire contenant des méthodes d'accès aux propriétés du schéma group.
 * 
 * @author fmh
 */
public final class GroupPropertyUtil {
	/**
	 * Constructeur privé.
	 */
	private GroupPropertyUtil() {
		// Classe utilitaire
	}

	public static void setGroupName(DocumentModel document, String groupName) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_NAME,
				groupName);
	}
	
	public static String getGroupName(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_NAME);
	}
	
	public static void setGroupLabel(DocumentModel document, String groupLabel) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_LABEL,
				groupLabel);
	}
	
	public static String getGroupLabel(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_LABEL);
	}
	
	public static void setGroupDescription(DocumentModel document, String groupDescr) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_DESCRIPTION,
				groupDescr);
	}
	
	public static String getGroupDescription(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_DESCRIPTION);
	}

	public static List<String> getUsers(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringListProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_MEMBERS);
	}

	public static void setUsers(DocumentModel document, List<String> users) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_MEMBERS, users);
	}
	
	public static List<String> getSubGroups(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringListProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_SUBGROUPS);
	}
	
	public static void setSubGroups(DocumentModel document, List<String> subgroups) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_GROUP, CommonSchemaConstant.PROP_GROUP_SUBGROUPS, subgroups);
	}
	
}
