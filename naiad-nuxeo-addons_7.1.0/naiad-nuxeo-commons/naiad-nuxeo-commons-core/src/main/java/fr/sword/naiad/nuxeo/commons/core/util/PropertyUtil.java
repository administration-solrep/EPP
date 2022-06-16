package fr.sword.naiad.nuxeo.commons.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Classe utilitaire pour extraire et convertir les propriétés des documents.
 * 
 * @author SPL
 */
public final class PropertyUtil {

    /**
     * Interface utilisé pour transformé les donnees des type complexe stocké dans des objet Map&lt;String, Serializable&gt; en objet métier
     * 
     * @author user
     * 
     * @param <T>
     */
    public interface ComplexeTypeMapper<T> {

        T doMapping(Map<String, Serializable> data) throws NuxeoException;

    }

    /**
     * Utility class
     */
    private PropertyUtil() {

    }

    /**
     * Récupère au format String la valeur d'une propriété d'un document.
     * 
     * @param document
     *            Document contenant la propriété.
     * @param schema
     *            Schéma de la propriété.
     * @param property
     *            Nom de la propriété.
     * @return Valeur de la propriété au format String.
     * @throws NuxeoException
     */
    public static String getStringProperty(final DocumentModel document, final String schema, final String property)
            throws NuxeoException {
        return (String) document.getProperty(schema, property);
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
    public static Long getLongProperty(final DocumentModel doc, final String schema, final String property)
            throws NuxeoException {
        return (Long) doc.getProperty(schema, property);
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
    public static Calendar getCalendarProperty(final DocumentModel document, final String schema, final String property)
            throws NuxeoException {
        return (Calendar) document.getProperty(schema, property);
    }

    /**
     * Extrait et retourne une propriété de type Boolean d'un document.
     * 
     * @param document
     *            Document
     * @param schema
     *            Schéma
     * @param property
     *            Propriété
     * @return Valeur de la propriété
     */
    public static Boolean getBooleanProperty(final DocumentModel document, final String schema, final String property)
            throws NuxeoException {
        Object value = document.getProperty(schema, property);
        if (value == null) {
            value = Boolean.FALSE;
        }
        return (Boolean) value;
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
     */
    public static Blob getBlobProperty(final DocumentModel doc, final String schema, final String property)
            throws NuxeoException {
        return (Blob) doc.getProperty(schema, property);
    }

    /**
     * Extrait et retourne une propriété de type List&lt;String&gt; d'un document.
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
    public static List<String> getStringListProperty(final DocumentModel doc, final String schema, final String property)
            throws NuxeoException {
        return (List<String>) doc.getProperty(schema, property);
    }

    /**
     * Extrait et retourne une propriété de type List&lt;Map&lt;String, Serializable&gt;&gt; d'un document : liste de type complexe
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
    public static List<Map<String, Serializable>> getMapStringSerializableListProperty(final DocumentModel doc,
            final String schema, final String property) throws NuxeoException {
        return (List<Map<String, Serializable>>) doc.getProperty(schema, property);
    }

    /**
     * Extrait et retourne une propriété de type List&lt;Map&lt;String, Serializable&gt;&gt; d'un document : element de type complexe
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
    public static Map<String, Serializable> getMapStringSerializableProperty(final DocumentModel doc,
            final String schema, final String property) throws NuxeoException {
        return (Map<String, Serializable>) doc.getProperty(schema, property);
    }

    /**
     * Extrait une propriété complexe et appele un objet pour transformer l'objet Map&lt;String, Serializable&gt; en un objet métier
     * 
     * @param doc
     * @param schema
     * @param property
     * @param mapper
     *            transforme Map&lt;String, Serializable&gt; en un objet du type souhaité
     * @return un objet métier
     * @throws NuxeoException
     */
    public static <T> T getComplexeProperty(final DocumentModel doc, final String schema, final String property,
            final ComplexeTypeMapper<T> mapper) throws NuxeoException {
        final Map<String, Serializable> data = getMapStringSerializableProperty(doc, schema, property);
        return mapper.doMapping(data);
    }

    /**
     * Extrait une liste de propriété complexe et appele un objet pour transformer chaque objet Map&lt;String, Serializable&gt; en un objet métier
     * 
     * @param doc
     * @param schema
     * @param property
     * @param mapper
     *            transforme Map&lt;String, Serializable&gt; en un objet du type souhaité
     * @return une liste d'objet métier
     * @throws NuxeoException
     */
    public static <T> List<T> getComplexePropertyList(final DocumentModel doc, final String schema,
            final String property, final ComplexeTypeMapper<T> mapper) throws NuxeoException {
        final List<Map<String, Serializable>> dataList = getMapStringSerializableListProperty(doc, schema, property);
        final List<T> result = new ArrayList<T>();
        for (final Map<String, Serializable> data : dataList) {
            result.add(mapper.doMapping(data));
        }
        return result;
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
    public static void setProperty(final DocumentModel doc, final String schema, final String property,
            final Object value) throws NuxeoException {
        doc.setProperty(schema, property, value);
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
    public static String[] getStringArrayProperty(final DocumentModel doc, final String schema, final String property)
            throws NuxeoException {
        final Object object = doc.getProperty(schema, property);
        return getStringArray(object);
    }

    /**
     * Extrait et retourne une propriété de type String[] d'une map.
     * 
     * @param variables
     *            map
     * @param variable
     *            nom de la propriété
     * @return Valeur de la propriété
     */
    public static String[] getStringArray(final Map<String, Serializable> variables, final String variable)
            throws NuxeoException {
        final Serializable ser = variables.get(variable);
        return getStringArray(ser);
    }

    private static String[] getStringArray(final Object ser) throws NuxeoException {
        if (ser instanceof String[]) {
            return (String[]) ser;
        } else if (ser instanceof Serializable[]) {
            final List<String> list = new ArrayList<String>();
            for (final Serializable obj : (Serializable[]) ser) {
                list.add(obj.toString());
            }
            return list.toArray(new String[] {});
        } else if (ser instanceof Object[]) {
            final List<String> list = new ArrayList<String>();
            for (final Object obj : (Object[]) ser) {
                list.add(obj.toString());
            }
            return list.toArray(new String[] {});
        } else if (ser == null) {
            final List<String> list = new ArrayList<String>();
            return list.toArray(new String[] {});
        } else {
            throw new NuxeoException("nuxeo n'a pas retourné le bon type de données...");
        }
    }
}
