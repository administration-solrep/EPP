package fr.sword.naiad.nuxeo.commons.core.schema;

import java.util.List;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Classe utilitaire contenant des méthodes d'accès aux propriétés du schéma user.
 * 
 * @author fmh
 */
public final class UserPropertyUtil {
	/**
	 * Constructeur privé.
	 */
	private UserPropertyUtil() {
		// Classe utilitaire
	}

	public static void setUserName(DocumentModel document, String userName) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_USERNAME,
				userName);
	}

	public static String getUserName(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_USERNAME);
	}

	public static String getFirstName(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_FIRSTNAME);
	}
	
	public static void setFirstName(DocumentModel document, String firstname) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_FIRSTNAME, firstname);
	}

	public static String getLastName(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_LASTNAME);
	}
	
	public static void setLastName(DocumentModel document, String lastname) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_LASTNAME, lastname);
	}

	public static String getEmail(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_EMAIL);
	}
	
	public static void setEmail(DocumentModel document, String email) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_EMAIL, email);
	}

	public static String getPhone(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_PHONE);
	}

	public static void setPhone(DocumentModel document, String phone) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_PHONE, phone);
	}
	
	public static List<String> getGroups(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringListProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_GROUPS);
	}

	public static void setGroups(DocumentModel document, List<String> groups) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_GROUPS, groups);
	}
	
	public static String getCompany(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_COMPANY);
	}
	
	public static void setCompany(DocumentModel document, String company) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_COMPANY, company);
	}
	
	public static void setPassword(DocumentModel document, String password) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_USER, CommonSchemaConstant.PROP_USER_PASSWORD, password);
	}
}
