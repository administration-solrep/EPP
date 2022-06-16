package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.st.api.descriptor.parlement.DefaultValue;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Implémentation du service permettant de gérer les schemas.
 *
 * @author asatre
 */
public class MetaDonneesServiceImpl extends DefaultComponent implements MetaDonneesService {
    private static final String META_TYPE_INSTITUTION = "institution";

    private static final String META_TYPE_VOCABULARY = "vocabulary";

    private static final String META_TYPE_BOOLEAN = "boolean";

    private static final String META_TYPE_INTEGER = "int";

    private static final String META_TYPE_STRING = "string";

    private static final String META_TYPE_POSITION_ALERTE = "positionAlerte";

    private static final String META_TYPE_DATE = "date";

    private static final String META_TYPE_OBJET = "objet";

    private static final String META_TYPE_OBJET_DOSSIER = "objetDossier";

    private static final String META_TYPE_CONDITIONNEL = "conditionnel";

    private static final String META_DEF_DATE_TOMORROW_OR_MONDAY = "tomorrowOrMonday";

    private static final long serialVersionUID = 8266232282826240132L;

    /**
     * Point d'extention des schemas des versions.
     */
    public static final String METADONNEES_SCHEMA_EXTENSION_POINT = "metadonnees";

    /**
     * Map des metadonnees.
     */
    private Map<String, MetaDonneesDescriptor> metaDonneesMap;

    /**
     * Cache des metadonnees.
     */
    private Map<String, Map<String, PropertyDescriptor>> metaDonneesCache;

    @Override
    public void activate(ComponentContext context) {
        metaDonneesMap = new HashMap<String, MetaDonneesDescriptor>();
        metaDonneesCache = new HashMap<String, Map<String, PropertyDescriptor>>();
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (extensionPoint.equals(METADONNEES_SCHEMA_EXTENSION_POINT)) {
            MetaDonneesDescriptor descriptor = (MetaDonneesDescriptor) contribution;
            metaDonneesMap.put(descriptor.getName(), descriptor);
        } else {
            throw new IllegalArgumentException("Unknown extension point: " + extensionPoint);
        }
    }

    @Override
    public MetaDonneesDescriptor getEvenementType(final String evenementType) {
        MetaDonneesDescriptor metaDonneesDescriptor = metaDonneesMap.get(evenementType);
        if (metaDonneesDescriptor == null) {
            throw new NuxeoException("Type de communication inconnu : " + evenementType);
        }
        return metaDonneesDescriptor;
    }

    @Override
    public Map<String, PropertyDescriptor> getMapProperty(final String evenementType) {
        Map<String, PropertyDescriptor> mapResult = metaDonneesCache.get(evenementType);
        if (mapResult == null) {
            final MetaDonneesDescriptor metaDonneesDescriptor = getEvenementType(evenementType);
            mapResult = new LinkedHashMap<>();

            if (metaDonneesDescriptor != null) {
                final EvenementMetaDonneesDescriptor evt = metaDonneesDescriptor.getEvenement();
                if (evt != null) {
                    mapResult.putAll(evt.getProperty());
                }

                final VersionMetaDonneesDescriptor version = metaDonneesDescriptor.getVersion();
                if (version != null) {
                    mapResult.putAll(version.getProperty());
                }

                metaDonneesCache.put(evenementType, mapResult);
            }
        }

        return mapResult;
    }

    @Override
    public List<MetaDonneesDescriptor> getAll() {
        List<MetaDonneesDescriptor> listResult = new ArrayList<MetaDonneesDescriptor>();

        List<String> listKey = new ArrayList<String>(metaDonneesMap.keySet());
        Collections.sort(listKey);

        for (String key : listKey) {
            listResult.add(metaDonneesMap.get(key));
        }

        return listResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DocumentModel remapDefaultMetaDonnees(
        DocumentModel docPrecedent,
        DocumentModel docSuivant,
        DocumentModel doc,
        Map<String, PropertyDescriptor> mapProperty
    ) {
        for (String name : mapProperty.keySet()) {
            DefaultValue defaultValue = mapProperty.get(name).getDefaultValue();
            if (defaultValue != null) {
                Object value = null;
                if (META_TYPE_OBJET.equals(defaultValue.getType())) {
                    // recherche dans le document precedent
                    if (docPrecedent != null) {
                        value = docPrecedent.getProperty(defaultValue.getSource(), defaultValue.getValue());
                    }
                }
                if (META_TYPE_OBJET_DOSSIER.equals(defaultValue.getType())) {
                    // recherche dans le document precedent
                    if (doc != null) {
                        value = doc.getProperty(defaultValue.getSource(), defaultValue.getValue());
                    }
                }
                if (META_TYPE_DATE.equals(defaultValue.getType())) {
                    // la valeur par defaut d'une date est la date du jour
                    value = Calendar.getInstance();
                    if (META_DEF_DATE_TOMORROW_OR_MONDAY.equals(defaultValue.getValue())) {
                        ((Calendar) value).add(Calendar.DATE, 1);
                        if (Calendar.SUNDAY == ((Calendar) value).get(Calendar.DAY_OF_WEEK)) {
                            ((Calendar) value).add(Calendar.DATE, 1);
                        }
                    }
                }
                if (META_TYPE_BOOLEAN.equals(defaultValue.getType())) {
                    value = Boolean.parseBoolean(defaultValue.getValue());
                }
                if (META_TYPE_INTEGER.equals(defaultValue.getType())) {
                    value = Integer.parseInt(defaultValue.getValue());
                }
                if (META_TYPE_STRING.equals(defaultValue.getType())) {
                    value = defaultValue.getValue();
                }
                if (META_TYPE_VOCABULARY.equals(defaultValue.getType())) {
                    // pour un vocabulaire la valeur est l'identifiant dans le vacabulaire
                    value = defaultValue.getValue();
                }
                if (META_TYPE_INSTITUTION.equals(defaultValue.getType())) {
                    // pour une institution la valeur est l'identifiant dans l'institution
                    value = defaultValue.getValue();
                }
                if (META_TYPE_POSITION_ALERTE.equals(defaultValue.getType())) {
                    // recherche dans le document precedent la position de l'alerte
                    if (docPrecedent != null) {
                        value = docPrecedent.getProperty(defaultValue.getSource(), defaultValue.getValue());
                    }
                    if (value == null || !(Boolean) value) {
                        // si pas d'alerte, debut alerte
                        value = Boolean.TRUE;
                    } else {
                        // si alerte avant, fin alerte
                        value = Boolean.FALSE;
                    }
                }

                Object objet = docSuivant.getProperty(defaultValue.getDestination(), name);
                if (objet instanceof List<?>) {
                    List<Object> list = new ArrayList<Object>();
                    if (value instanceof List<?>) {
                        list.addAll((Collection<? extends Object>) value);
                    } else {
                        list.add(value);
                    }
                    docSuivant.setProperty(defaultValue.getDestination(), name, list);
                } else {
                    docSuivant.setProperty(defaultValue.getDestination(), name, value);
                }
            }
        }

        return docSuivant;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void remapConditionnelMetaDonnees(
        DocumentModel eventDoc,
        DocumentModel versionDoc,
        Map<String, PropertyDescriptor> mapProperty
    ) {
        for (String name : mapProperty.keySet()) {
            DefaultValue defaultValue = mapProperty.get(name).getDefaultValue();
            if (defaultValue != null) {
                Object value = null;
                if (META_TYPE_CONDITIONNEL.equals(defaultValue.getType())) {
                    DocumentModel sourceDoc = eventDoc;
                    DocumentModel destinationDoc = versionDoc;
                    if (!sourceDoc.hasSchema(defaultValue.getSource())) {
                        sourceDoc = versionDoc;
                    }
                    if (!destinationDoc.hasSchema(defaultValue.getDestination())) {
                        destinationDoc = eventDoc;
                    }
                    String docValue = (String) sourceDoc.getProperty(defaultValue.getSource(), defaultValue.getValue());
                    for (String conditionValue : defaultValue.getConditionnelValue().split(",")) {
                        String[] condition = conditionValue.split(":");
                        if (condition[0].equals(docValue)) {
                            value = condition[1];
                        }
                    }
                    Object objet = destinationDoc.getProperty(defaultValue.getDestination(), name);
                    if (objet instanceof List<?>) {
                        List<Object> list = new ArrayList<Object>();
                        if (value instanceof List<?>) {
                            list.addAll((Collection<? extends Object>) value);
                        } else {
                            list.add(value);
                        }
                        destinationDoc.setProperty(defaultValue.getDestination(), name, list);
                    } else {
                        destinationDoc.setProperty(defaultValue.getDestination(), name, value);
                    }
                }
            }
        }
    }
}
