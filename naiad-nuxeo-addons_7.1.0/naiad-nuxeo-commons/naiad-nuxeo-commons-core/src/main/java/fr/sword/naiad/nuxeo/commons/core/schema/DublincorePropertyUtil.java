package fr.sword.naiad.nuxeo.commons.core.schema;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;

/**
 * Class utilitaire manipulant les propriétés du schéma dublincore
 * 
 * @author SPL
 */
public final class DublincorePropertyUtil {

	/**
	 * Class utilitaire
	 */
	private DublincorePropertyUtil(){
		
	}
	
	/**
	 * Extrait le titre : propriété du schema dublincore
	 * @return le titre
	 */
	public static String getTitle(DocumentModel document) throws NuxeoException{
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_TITLE);
	}
	
	/**
	 * Extrait la description : propriété du schema dublincore
	 * @return la description
	 */
	public static String getDescription(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_DESCRIPTION);
	}

	/**
	 * Extrait la date de derniere modification : propriété du schema dublincore
	 */
	public static Calendar getModificationDate(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getCalendarProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_MODIFIED);
	}

	/**
	 * Extrait la liste des sujets associé à un document : propriété du schema dublincore
	 */
	public static String[] getSubjects(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringArrayProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_SUBJECTS);
	}
	
	/**
	 * Definit la valeur du titre : propriété du schema dublincore
	 */
	public static void setTitle(DocumentModel document, String title) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_TITLE, title);
	}
	
	/**
	 * Definit la valeur de description : propriété du schema dublincore
	 */
	public static void setDescription(DocumentModel document, String description) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_DESCRIPTION, description);
	}
	
	/**
	 * Met à jour la date de dernière modification
	 */
	public static void updateModified(DocumentModel document) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_MODIFIED, null);
	}
	
	public static void setModified(DocumentModel document, Calendar date) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_MODIFIED, date);
	}
	
	/**
	 * Definitla liste des sujets associé à un document : propriété du schema dublincore
	 */
	public static void setSubjects(DocumentModel document, String[] subjects) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_SUBJECTS, subjects);
	}

	public static String getLastContributor(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_LAST_CONTRIBUTOR);
	}
	
	public static void setLastContributor(DocumentModel document, String contributor) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_LAST_CONTRIBUTOR, contributor);
	}
	
	public static String getCreator(DocumentModel document) throws NuxeoException {
		return PropertyUtil.getStringProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_CREATOR);
	}
	
	public static void setCreator(DocumentModel document, String creator) throws NuxeoException {
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_CREATOR, creator);
	}

	public static Calendar getCreated(DocumentModel document) throws NuxeoException{
		return PropertyUtil.getCalendarProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_CREATED);
	}

	public static void setCreated(DocumentModel document, Calendar created) throws NuxeoException{
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_CREATED, created);
	}
	
	public static String[] getContributors(DocumentModel document) throws NuxeoException{
		return PropertyUtil.getStringArrayProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_CONTRIBUTORS);
	}

	public static void setContributors(DocumentModel document, String[] contributors) throws NuxeoException{
		PropertyUtil.setProperty(document, CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_CONTRIBUTORS, contributors);
	}
	
	

}
