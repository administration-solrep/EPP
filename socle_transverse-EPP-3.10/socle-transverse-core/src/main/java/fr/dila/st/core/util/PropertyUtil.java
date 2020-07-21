package fr.dila.st.core.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.domain.ComplexeType;
import fr.dila.st.core.domain.ComplexeTypeImpl;

/**
 * Classe utilitaire pour extraire des propriétés des documents.
 * 
 * @author jtremeaux
 */
public class PropertyUtil {

	/**
	 * utility class
	 */
	protected PropertyUtil() {
		// do nothing
	}

	/**
	 * Extrait une propriété générique.
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	public static Object getProperty(DocumentModel doc, String schema, String property) {
		try {
			return doc.getProperty(schema, property);
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Extrait et retourne une propriété de type String d'un document.
	 * 
	 * @param document
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	public static String getStringProperty(DocumentModel document, String schema, String property) {
		try {
			return (String) document.getProperty(schema, property);
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Extrait et retourne une propriété de type Long d'un document.
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	public static Long getLongProperty(DocumentModel doc, String schema, String property) {
		try {
			return (Long) doc.getProperty(schema, property);
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Extrait et retourne une propriété de type Integer d'un document.
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	public static Integer getIntegerProperty(DocumentModel doc, String schema, String property) {
		try {
			return (Integer) doc.getProperty(schema, property);
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Extrait et retourne une propriété de type Calendar d'un document.
	 * 
	 * @param document
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	public static Calendar getCalendarProperty(DocumentModel document, String schema, String property) {
		try {
			return (Calendar) document.getProperty(schema, property);
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Extrait et retourne une propriété de type Boolean d'un document.
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	public static Boolean getBooleanProperty(DocumentModel document, String schema, String property) {
		try {
			Object value = document.getProperty(schema, property);
			/*
			 * Permet de gérer les valeurs par défaut dans le XSD : mettre '0' dans le XSD. -> getProperty retourne
			 * null. Si on met 'false' dans le XSD, il est impossible d'effectuer des requêtes.
			 */
			if (value == null) {
				return false;
			}
			return (Boolean) value;
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Extrait et retourne une propriété de type Blob d'un document.
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 * @throws IOException
	 */
	public static Blob getBlobProperty(DocumentModel doc, String schema, String property) {
		try {
			return (Blob) doc.getProperty(schema, property);			
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Extrait et retourne une propriété de type List<String> d'un document.
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getStringListProperty(DocumentModel doc, String schema, String property) {
		try {
			// Afin d'éviter le npe
			List<String> lstProp = (List<String>) doc.getProperty(schema, property);
			if (lstProp == null) {
				lstProp = new ArrayList<String>();
			}

			return lstProp;
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}
	
	/**
	 * Extrait et retourne une propriété de type List<Calendar> d'un document.
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	@SuppressWarnings("unchecked")
	public static List<Calendar> getCalendarListProperty(DocumentModel doc, String schema, String property) {
		try {
			// Afin d'éviter le npe
			List<Calendar> lstProp = (List<Calendar>) doc.getProperty(schema, property);
			if (lstProp == null) {
				lstProp = new ArrayList<Calendar>();
			}

			return lstProp;
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Extrait et retourne une propriété de type List<Map<String,Serializable>> d'un document
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Serializable>> getMapStringSerializableListProperty(DocumentModel doc,
			String schema, String property) {
		try {
			return (List<Map<String, Serializable>>) doc.getProperty(schema, property);
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Renseigne une propriété.
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @param value
	 *            Valeur
	 */
	public static void setProperty(DocumentModel doc, String schema, String property, Object value) {
		try {
			doc.setProperty(schema, property, value);
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	/**
	 * Retourne l'état courant du cycle de vie du document.
	 * 
	 * @param doc
	 *            Document
	 * @return État du cycle de vie
	 */
	public static String getCurrentLifeCycleState(DocumentModel doc) {
		try {
			return doc.getCurrentLifeCycleState();
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}

	public static void setListSerializableProperty(DocumentModel document, String schema, String property,
			List<? extends ComplexeType> listeToSerializable) {
		// init serializable List
		final List<Map<String, Serializable>> listeSerializable = new ArrayList<Map<String, Serializable>>();
		// Hack : to refactor
		// note : si l'on récupère tel quel une liste de type complexe via un getProperty ou un getPropertyValue, les
		// serializableMap sont vide.
		for (ComplexeType serizableMap : listeToSerializable) {
			listeSerializable.add(serizableMap.getSerializableMap());
		}
		setProperty(document, schema, property, listeSerializable);
	}

	public static List<ComplexeType> getListSerializableProperty(DocumentModel document, String schema, String value) {
		final List<ComplexeType> complexeTypeList = new ArrayList<ComplexeType>();
		final List<Map<String, Serializable>> complexeTypes = getMapStringSerializableListProperty(document, schema,
				value);
		if (complexeTypes != null) {
			for (Map<String, Serializable> complexeEntry : complexeTypes) {
				complexeTypeList.add(new ComplexeTypeImpl(complexeEntry));
			}
		}
		return complexeTypeList;
	}

	/**
	 * Extrait et retourne une propriété de type String[] d'un document.
	 * 
	 * @param doc
	 *            Document
	 * @param schema
	 *            Schéma
	 * @param property
	 *            Propriété
	 * @return Valeur de la propriété
	 */
	public static String[] getStringArrayProperty(DocumentModel doc, String schema, String property) {
		try {
			return (String[]) doc.getProperty(schema, property);
		} catch (ClientException e) {
			throw new ClientRuntimeException(e);
		}
	}
}
