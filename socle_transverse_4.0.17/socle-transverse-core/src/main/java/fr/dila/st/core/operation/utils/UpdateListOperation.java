package fr.dila.st.core.operation.utils;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.SolonDateConverter;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.model.Property;

/**
 * Opération pour modifier le contenu d'une liste pour une propriété d'un document paramètres : <br />
 * id : Obligatoire - Uid du document à modifier <br />
 * property : Obligatoire - propriété à modifier sous la forme prefixe_schéma:propriété (exemple : dc:contributors) <br />
 * mode : Obligatoire - action à effectuer (ajout, suppression, vidage de la liste) ; add, rm, ou rmAll <br />
 * data : Facultatif - Donnée en paramètre si le mode d'ajout ou suppression est choisi ; la chaine de caractère sera
 * convertie <br />
 * dataType : Facultatif - Utile lorsqu'on ajoute une donnée dans une liste ou map qui est vide (impossibilité de
 * "deviner" le type contenu dans l'objet multivalué)
 *
 * Sont modifiables : les listes de dates, les listes de chaines de caractères, et les listes de nombres. <br />
 * Pour les dates, il faut transmettre une chaine de caractère sous la forme "dd/MM/yyyy HH:mm" <br />
 * Pour modifier une liste elle doit être transmise sous la forme [Lelt1;elt2;elt3] / [Mkey/value%key2/value%key3/value]
 * pour une map
 *
 */
@Operation(
    id = UpdateListOperation.ID,
    category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY,
    label = "UpdateList",
    description = "Met à jour le contenu d'une donnée multivaluée"
)
public class UpdateListOperation {
    /**
     * LOGGER
     */
    private static final STLogger LOGGER = STLogFactory.getLog(UpdateListOperation.class);

    // Constantes utilisées pour echapper le caractère [ en cas de regexp
    private static final String MAP_INIT_STR_REGEXP = "\\[M";
    private static final String LIST_INIT_STR_REGEXP = "\\[L";

    /**
     * Identifiant technique de l'opération.
     */
    public static final String ID = "ST.Update.List";

    public static final String EMPTY_PARAM_EXC =
        "Les paramètres property et mode ne doivent pas être vides, id ou path doit également être renseigné";

    public static final String BOTH_ID_PATH_EXC =
        "Les paramètres id et path sont tous les deux renseignés. Un seul est nécessaire.";

    public static final String DOC_NOT_FOUND_FOR_ID = "Aucun dossier trouvé avec l'id : ";

    public static final String DOC_NOT_FOUND_FOR_PATH = "Aucun dossier trouvé avec le chemin : ";

    public static final String EMPTY_DATA_PARAM_EXC = "La valeur à modifier (data) ne doit pas être vide";

    public static final String CANT_GET_PROPERTY_EXC = "Impossible de récupérer la propriété à modifier : ";

    public static final String INCOMPATIBLE_TYPE_EXC =
        "Impossible d'appliquer l'opération pour ce type de propriété : ";

    public static final String CANT_UPDATE_DATA_EXC = "Impossible de mettre à jour cette donnée";

    public static final String WRONG_DATA_EXC = "Les paramètres pour mettre à jour ces données ne correspondent pas";

    public static final String WRONG_DATATYPE_EXC = "DataType n'est pas correct";

    public static final String EMPTY_DATATYPE_EXC =
        "La propriété à modifier est vide (aucune valeurs dans le document). Informations de dataType requis pour la propriété : ";

    public static final String REMOVE_MODE = "rm";
    public static final String ADD_MODE = "add";
    public static final String REMOVEALL_MODE = "rmAll";

    // Constantes pour les définitions de listes et maps dans les chaines de caractères
    public static final String MAP_INIT_STR = "[M";
    public static final String MAP_END_STR = "]";
    public static final String LIST_INIT_STR = "[L";
    public static final String LIST_END_STR = "]";

    // Separateurs pour les données en chaines de caractères
    // Sépare les données d'une liste
    public static final String LIST_SEPARATOR = ";";
    // Sépare les entrées d'une map
    public static final String MAP_SEPARATOR = "%";
    // Sépare les clé/valeurs d'une entrée de map
    public static final String KEY_VALUE_SEPARATOR = "~";

    @Context
    private CoreSession session;

    /**
     * Uid du document à modifier
     */
    @Param(name = "id", required = false)
    private String id;

    /**
     * Uid du document à modifier
     */
    @Param(name = "path", required = false)
    private String path;

    /**
     * prefix schema + propriété concernée ex : dc:contributors
     */
    @Param(name = "property", required = true)
    private String property;

    /**
     * Mode demandé : add or remove(rm) or removeAll(rmAll)
     */
    @Param(name = "mode", required = true)
    private String mode;

    /**
     * La donnée à ajouter ou retirer si le mode est add or remove
     */
    @Param(name = "data", required = false)
    private String data = "";

    @Param(name = "dataType", required = false)
    private String dataType = "";

    private DocumentModel docToModify = null;
    private Property propertyToModify = null;

    public UpdateListOperation() {
        // Default contructor
    }

    @OperationMethod
    public void run() throws OperationException {
        LOGGER.info(session, STLogEnumImpl.INIT_OPERATION_UPDATE_LIST_TEC);
        try {
            StringBuilder paramInfoLog = new StringBuilder();
            paramInfoLog.append("Property : ").append(property);
            paramInfoLog.append(" - Mode : ").append(mode);
            if (ADD_MODE.equalsIgnoreCase(mode) || REMOVE_MODE.equalsIgnoreCase(mode)) {
                paramInfoLog.append(" - Data : ").append(data);
                if (StringUtils.isNotBlank(dataType)) {
                    paramInfoLog.append(" - DataType : ").append(dataType);
                }
            }
            LOGGER.info(session, STLogEnumImpl.PROCESS_OPERATION_UPDATE_LIST_TEC, paramInfoLog.toString());
            checkParameters();

            updateDocument(docToModify, propertyToModify, data);

            saveDocument();
        } catch (OperationException exc) {
            LOGGER.warn(session, STLogEnumImpl.FAIL_PROCESS_OPERATION_UPDATE_LIST_TEC, exc);
            throw exc;
        } finally {
            LOGGER.info(session, STLogEnumImpl.END_OPERATION_UPDATE_LIST_TEC);
        }
    }

    /**
     * Renvoie la valeur courante de la property pour le document en paramètre en fonction du type de données.
     * @param doc objet DocumentModel
     * @param schema Schéma
     * @param property objet Property
     * @return un objet de type Objet[] / List / Map
     */
    private Object getValue(DocumentModel doc, String schema, Property property) {
        String typeName = property.getField().getType().getName();
        if ("stringArray".equals(typeName)) {
            return PropertyUtil.getStringArrayProperty(doc, schema, property.getName());
        } else if ("stringList".equals(typeName)) {
            return PropertyUtil.getStringListProperty(doc, schema, property.getName());
        } else if ("dateArray".equals(typeName) && property.getValue() == null) {
            return new Calendar[] {};
        } else if ("dateList".equals(typeName) && property.getValue() == null) {
            return new ArrayList<Calendar>();
        } else if ("intArray".equals(typeName) && property.getValue() == null) {
            return new Long[] {};
        } else if ("intList".equals(typeName) && property.getValue() == null) {
            return new ArrayList<Long>();
        }
        return property.getValue();
    }

    @SuppressWarnings("rawtypes")
    private void updateDocument(DocumentModel docToUpdate, Property propertyToUpdate, String inputParameters)
        throws OperationException {
        Object propertyValue = getValue(docToUpdate, docToModify.getType(), propertyToUpdate);
        Serializable propertyUpdated = null;
        inputParameters = inputParameters.trim();
        if (propertyValue.getClass().isArray()) {
            propertyUpdated = runArray((Object[]) propertyValue, inputParameters);
        } else if (propertyValue instanceof List) {
            propertyUpdated = (Serializable) runList((List) propertyValue, inputParameters);
        } else if (propertyValue instanceof Map) {
            propertyUpdated = (Serializable) runMap((Map) propertyValue, inputParameters);
        } else {
            throw new OperationException(INCOMPATIBLE_TYPE_EXC + propertyValue.getClass().getName());
        }

        docToUpdate.setPropertyValue(property, propertyUpdated);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object[] runArray(Object[] currentValues, String inputParameters) throws OperationException {
        Object parametersValues = null;
        Object[] valuesUpdated = Arrays.copyOf(currentValues, currentValues.length + 1);

        if (REMOVEALL_MODE.equalsIgnoreCase(mode)) {
            valuesUpdated = null;
        } else {
            if (currentValues.length > 0) {
                Object objectInArray = currentValues[0];
                if (REMOVE_MODE.equalsIgnoreCase(mode)) {
                    int newPosition = 0;
                    if (objectInArray.getClass().isArray()) {
                        // We have an array of array
                        parametersValues = extractArrayFromString((Object[]) objectInArray, inputParameters);
                        for (int i = 0; i < currentValues.length; i++) {
                            if (!compareArrays((Object[]) currentValues[i], (Object[]) parametersValues)) {
                                valuesUpdated[newPosition++] = currentValues[i];
                            }
                        }
                    } else if (objectInArray instanceof List) {
                        // We have an array of List
                        parametersValues = extractListFromString((List) objectInArray, inputParameters);
                        for (int i = 0; i < currentValues.length; i++) {
                            if (!compareLists((List) currentValues[i], (List) parametersValues)) {
                                valuesUpdated[newPosition++] = currentValues[i];
                            }
                        }
                    } else if (objectInArray instanceof Map) {
                        // We have a list of Map
                        parametersValues = extractMapFromString((Map) objectInArray, inputParameters);
                        for (int i = 0; i < currentValues.length; i++) {
                            if (!compareMaps((Map) currentValues[i], (Map) parametersValues)) {
                                valuesUpdated[newPosition++] = currentValues[i];
                            }
                        }
                    } else {
                        parametersValues = getObjectFromSimpleName(objectInArray.getClass(), inputParameters);
                        for (int i = 0; i < currentValues.length; i++) {
                            if (!compareObjects(currentValues[i], parametersValues)) {
                                valuesUpdated[newPosition++] = currentValues[i];
                            }
                        }
                    }
                    if (newPosition != 0 && newPosition < valuesUpdated.length) {
                        valuesUpdated = Arrays.copyOf(valuesUpdated, newPosition);
                    }
                } else if (ADD_MODE.equalsIgnoreCase(mode)) {
                    if (objectInArray.getClass().isArray()) {
                        // We have an array of array
                        parametersValues = extractArrayFromString((Object[]) objectInArray, inputParameters);
                    } else if (objectInArray instanceof List) {
                        // We have an array of List
                        parametersValues = extractListFromString((List) objectInArray, inputParameters);
                    } else if (objectInArray instanceof Map) {
                        // We have an array of Map
                        parametersValues = extractMapFromString((Map) objectInArray, inputParameters);
                    } else {
                        parametersValues = getObjectFromSimpleName(objectInArray.getClass(), inputParameters);
                    }
                    valuesUpdated[currentValues.length] = parametersValues;
                }
            } else {
                checkDataType();
                if (ADD_MODE.equalsIgnoreCase(mode)) {
                    // Parse dataType to create Object to add
                    valuesUpdated[0] = parseDataType(dataType, inputParameters);
                }
            }
        }

        return valuesUpdated;
    }

    /**
     * Cas particulier des maps : si l'entrée de la map est une liste, on modifie la liste avec le parametre (cad : add
     * : on ajoute dans la liste, rm : on retire de la liste)
     *
     * @param propertyValue
     * @param inputParameters
     * @return
     * @throws OperationException
     * @throws ClientException
     * @throws IllegalAccessException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map runMap(Map propertyValue, String inputParameters) throws OperationException {
        Map<String, Object> currentValues = propertyValue;
        HashMap<String, Object> valuesUpdated = new HashMap<String, Object>();

        if (REMOVEALL_MODE.equalsIgnoreCase(mode)) {
            for (Entry<String, Object> currentEntry : currentValues.entrySet()) {
                valuesUpdated.put(currentEntry.getKey(), null);
            }
        } else {
            if (isMapWellKnown(currentValues, inputParameters)) {
                Map<String, Object> input = extractMapFromString(currentValues, inputParameters);
                for (Entry<String, Object> currentEntry : currentValues.entrySet()) {
                    if (input.containsKey(currentEntry.getKey())) {
                        Object value = currentEntry.getValue();
                        if (REMOVE_MODE.equalsIgnoreCase(mode)) {
                            if (value.getClass().isArray()) {
                                // We have a map of array
                                Object[] currentArray = (Object[]) value;
                                Object[] arrayUpdated = new Object[currentArray.length];
                                Object[] objectsArrayToRemove = (Object[]) input.get(currentEntry.getKey());
                                int newPosition = 0;
                                for (int i = 0; i < currentArray.length; i++) {
                                    Object objectInArray = currentArray[i];
                                    for (int j = 0; j < objectsArrayToRemove.length; j++) {
                                        Object objectToRemove = objectsArrayToRemove[j];
                                        if (objectInArray.getClass().isArray()) {
                                            if (!compareArrays((Object[]) objectInArray, (Object[]) objectToRemove)) {
                                                arrayUpdated[newPosition++] = objectInArray;
                                            }
                                        } else if (objectInArray instanceof List) {
                                            if (!compareLists((List) objectInArray, (List) objectToRemove)) {
                                                arrayUpdated[newPosition++] = objectInArray;
                                            }
                                        } else if (objectInArray instanceof Map) {
                                            if (!compareMaps((Map) objectInArray, (Map) objectToRemove)) {
                                                arrayUpdated[newPosition++] = objectInArray;
                                            }
                                        } else {
                                            if (!compareObjects(objectInArray, objectToRemove)) {
                                                arrayUpdated[newPosition++] = objectInArray;
                                            }
                                        }
                                    }
                                }
                                if (newPosition != 0 && newPosition < arrayUpdated.length) {
                                    arrayUpdated = Arrays.copyOf(arrayUpdated, newPosition);
                                }
                                valuesUpdated.put(currentEntry.getKey(), arrayUpdated);
                            } else if (value instanceof List) {
                                // We have a map of List
                                List currentList = (List) value;
                                List listUpdated = new ArrayList(currentList);
                                List objectsListToRemove = (List) input.get(currentEntry.getKey());
                                for (Object objectInList : currentList) {
                                    for (int i = 0; i < objectsListToRemove.size(); i++) {
                                        Object objectToRemove = objectsListToRemove.get(i);
                                        if (objectInList.getClass().isArray()) {
                                            if (compareArrays((Object[]) objectInList, (Object[]) objectToRemove)) {
                                                listUpdated.remove(objectInList);
                                            }
                                        } else if (objectInList instanceof List) {
                                            if (compareLists((List) objectInList, (List) objectToRemove)) {
                                                listUpdated.remove(objectInList);
                                            }
                                        } else if (objectInList instanceof Map) {
                                            if (compareMaps((Map) objectInList, (Map) objectToRemove)) {
                                                listUpdated.remove(objectInList);
                                            }
                                        } else {
                                            if (compareObjects(objectInList, objectToRemove)) {
                                                listUpdated.remove(objectInList);
                                            }
                                        }
                                    }
                                }
                                valuesUpdated.put(currentEntry.getKey(), listUpdated);
                            } else if (value instanceof Map) {
                                // We have a map of Map
                                Map currentMap = (Map) value;
                                if (compareMaps(currentMap, (Map) input.get(currentEntry.getKey()))) {
                                    valuesUpdated.remove(currentEntry.getKey());
                                }
                            } else {
                                if (compareObjects(value, input.get(currentEntry.getKey()))) {
                                    valuesUpdated.remove(currentEntry.getKey());
                                }
                            }
                        } else if (ADD_MODE.equalsIgnoreCase(mode)) {
                            if (value.getClass().isArray()) {
                                // We have a map of array
                                Object[] currentArray = (Object[]) value;
                                Object[] arrayToAdd = (Object[]) input.get(currentEntry.getKey());
                                Object[] arrayUpdated = Arrays.copyOf(
                                    currentArray,
                                    currentArray.length + arrayToAdd.length
                                );
                                for (int i = 0; i < arrayToAdd.length; i++) {
                                    arrayUpdated[currentArray.length + i] = arrayToAdd[i];
                                }
                                valuesUpdated.put(currentEntry.getKey(), arrayUpdated);
                            } else if (value instanceof List) {
                                // We have a map of List
                                List listUpdated = (List) value;
                                listUpdated.addAll((Collection) input.get(currentEntry.getKey()));
                                valuesUpdated.put(currentEntry.getKey(), listUpdated);
                            } else if (value instanceof Map) {
                                // We have a map of Map
                                Map mapUpdated = (Map) value;
                                mapUpdated.putAll((Map) input.get(currentEntry.getKey()));
                                valuesUpdated.put(currentEntry.getKey(), mapUpdated);
                            } else {
                                valuesUpdated.put(currentEntry.getKey(), input.get(currentEntry.getKey()));
                            }
                        }
                    } else {
                        // On recopie la valeur telle quelle
                        valuesUpdated.put(currentEntry.getKey(), currentValues.get(currentEntry.getKey()));
                    }
                }
            } else {
                checkDataType();
                if (ADD_MODE.equalsIgnoreCase(mode)) {
                    valuesUpdated = new HashMap<String, Object>(currentValues);
                    for (Entry entry : (
                        (HashMap<String, Object>) parseDataType(dataType, inputParameters)
                    ).entrySet()) {
                        valuesUpdated.put((String) entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return valuesUpdated;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List runList(List currentValues, String inputParameters) throws OperationException {
        Object parametersValues = null;
        ArrayList<Object> valuesUpdated = new ArrayList<Object>(currentValues);
        if (REMOVEALL_MODE.equalsIgnoreCase(mode)) {
            valuesUpdated = null;
        } else {
            if (!currentValues.isEmpty()) {
                Object objectInList = currentValues.get(0);
                if (REMOVE_MODE.equalsIgnoreCase(mode)) {
                    if (objectInList.getClass().isArray()) {
                        // We have a list of array
                        parametersValues = extractArrayFromString((Object[]) objectInList, inputParameters);
                        List<Object[]> currentListOfArray = currentValues;
                        for (Object[] arrayValue : currentListOfArray) {
                            if (compareArrays(arrayValue, (Object[]) parametersValues)) {
                                valuesUpdated.remove(arrayValue);
                            }
                        }
                    } else if (objectInList instanceof List) {
                        // We have a list of List
                        parametersValues = extractListFromString((List) objectInList, inputParameters);
                        List<List<Object>> currentListOfList = currentValues;
                        for (List<Object> listValue : currentListOfList) {
                            if (compareLists(listValue, (List) parametersValues)) {
                                valuesUpdated.remove(listValue);
                            }
                        }
                    } else if (objectInList instanceof Map) {
                        // We have a list of Map
                        parametersValues = extractMapFromString((Map) objectInList, inputParameters);
                        List<Map<String, Object>> currentListOfMap = currentValues;
                        for (Map<String, Object> mapValue : currentListOfMap) {
                            if (compareMaps(mapValue, (Map) parametersValues)) {
                                valuesUpdated.remove(mapValue);
                            }
                        }
                    } else {
                        // list of simple type (primitives or Integer, Long, String...)
                        parametersValues = getObjectFromSimpleName(objectInList.getClass(), inputParameters);
                        for (Object value : currentValues) {
                            if (compareObjects(value, parametersValues)) {
                                valuesUpdated.remove(value);
                            }
                        }
                    }
                } else if (ADD_MODE.equalsIgnoreCase(mode)) {
                    if (objectInList.getClass().isArray()) {
                        // We have a list of array
                        parametersValues = extractArrayFromString((Object[]) objectInList, inputParameters);
                    } else if (objectInList instanceof List) {
                        // We have a list of List
                        parametersValues = extractListFromString((List) objectInList, inputParameters);
                    } else if (objectInList instanceof Map) {
                        // We have a list of Map
                        parametersValues = extractMapFromString((Map) objectInList, inputParameters);
                    } else {
                        parametersValues = getObjectFromSimpleName(objectInList.getClass(), inputParameters);
                    }
                    valuesUpdated.add(parametersValues);
                }
            } else {
                checkDataType();
                if (ADD_MODE.equalsIgnoreCase(mode)) {
                    valuesUpdated.add(parseDataType(dataType, inputParameters));
                }
            }
        }
        return valuesUpdated;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Map<String, Object> extractMapFromString(Map mapToMimic, String mapStr) throws OperationException {
        Map<String, Object> parametersMap = new HashMap<String, Object>();
        if (mapStr.startsWith(MAP_INIT_STR) && mapStr.endsWith(MAP_END_STR)) {
            // Split map content
            String parameters = mapStr.replaceFirst(MAP_INIT_STR_REGEXP, "");
            parameters = StringUtils.removeEnd(parameters, MAP_END_STR);
            String[] mapKeysValues = parameters.split(MAP_SEPARATOR);
            StringBuilder mapEntry = new StringBuilder();
            for (int i = 0; i < mapKeysValues.length; i++) {
                mapEntry.delete(0, mapEntry.length()).append(mapKeysValues[i]);
                if (mapEntry.toString().contains(MAP_INIT_STR)) {
                    // A map was inside parameters - rebuild
                    while (!mapKeysValues[i].contains(MAP_END_STR)) {
                        mapEntry.append(MAP_SEPARATOR).append(mapKeysValues[++i]);
                    }
                    // last token to add
                    mapEntry.append(MAP_SEPARATOR).append(mapKeysValues[i]);
                }
                String[] entry = mapEntry.toString().split(KEY_VALUE_SEPARATOR, 2);
                if (entry[1].startsWith(MAP_INIT_STR)) {
                    parametersMap.put(
                        entry[0].trim(),
                        extractMapFromString((Map) mapToMimic.get(entry[0].trim()), entry[1])
                    );
                } else if (entry[1].startsWith(LIST_INIT_STR)) {
                    if (mapToMimic.get(entry[0].trim()).getClass().isArray()) {
                        parametersMap.put(
                            entry[0].trim(),
                            extractArrayFromString((Object[]) mapToMimic.get(entry[0].trim()), entry[1])
                        );
                    } else if (mapToMimic.get(entry[0].trim()) instanceof List) {
                        parametersMap.put(
                            entry[0].trim(),
                            extractListFromString((List) mapToMimic.get(entry[0].trim()), entry[1])
                        );
                    }
                } else {
                    parametersMap.put(
                        entry[0].trim(),
                        getObjectFromSimpleName(mapToMimic.get(entry[0].trim()).getClass(), entry[1].trim())
                    );
                }
            }
        } else {
            throw new OperationException(WRONG_DATA_EXC);
        }
        return parametersMap;
    }

    private List<Object> extractListFromString(List<Object> listToMimic, String listStr) throws OperationException {
        List<Object> parametersList = new ArrayList<Object>();
        Object objectInList = null;
        if (!listToMimic.isEmpty()) {
            objectInList = listToMimic.get(0);
        }
        parametersList.addAll(splitListInfos(listStr, objectInList));
        return parametersList;
    }

    private Object[] extractArrayFromString(Object[] arrayToMimic, String listStr) throws OperationException {
        List<Object> parametersList = new ArrayList<Object>();
        Object objectInArray = null;
        if (arrayToMimic.length > 0) {
            objectInArray = arrayToMimic[0];
        }
        parametersList.addAll(splitListInfos(listStr, objectInArray));
        return parametersList.toArray();
    }

    /**
     * Permet de convertir une string de la forme key~value%key2~value2...%keyN~valueN en une liste de String de la
     * forme key~value
     *
     * @param mapInfos
     * @return
     */
    private ArrayList<String> splitMapInfos(String mapInfos) {
        String[] entryKeysValuesSplitted = mapInfos.split(MAP_SEPARATOR);
        ArrayList<String> entryKeysValues = new ArrayList<String>();
        StringBuilder mapEntry = new StringBuilder();
        for (int i = 0; i < entryKeysValuesSplitted.length; i++) {
            mapEntry.delete(0, mapEntry.length()).append(entryKeysValuesSplitted[i]);
            if (mapEntry.toString().contains(MAP_INIT_STR)) {
                // A map was inside parameters - rebuild
                while (!entryKeysValuesSplitted[i].contains(MAP_END_STR)) {
                    mapEntry.append(MAP_SEPARATOR).append(entryKeysValuesSplitted[++i]);
                }
                // last token to add
                mapEntry.append(MAP_SEPARATOR).append(entryKeysValuesSplitted[i]);
            }
            entryKeysValues.add(mapEntry.toString());
        }
        return entryKeysValues;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Object> splitListInfos(String listInfos, Object objectToMimic) throws OperationException {
        List<Object> parametersList = new ArrayList<Object>();
        if (listInfos.startsWith(LIST_INIT_STR) && listInfos.endsWith(LIST_END_STR)) {
            // Split map content
            String parameters = listInfos.replaceFirst(LIST_INIT_STR_REGEXP, "");
            parameters = StringUtils.removeEnd(parameters, LIST_END_STR);
            String[] listValues = parameters.split(LIST_SEPARATOR);
            StringBuilder listEntry = new StringBuilder();
            String listEntryStr = null;
            for (int i = 0; i < listValues.length; i++) {
                listEntry.delete(0, listEntry.length()).append(listValues[i]);
                if (listEntry.toString().startsWith(LIST_INIT_STR)) {
                    // A list was inside parameters - rebuild
                    while (!listValues[i].contains(LIST_END_STR)) {
                        listEntry.append(LIST_SEPARATOR).append(listValues[i++]);
                    }
                    // last token to add
                    listEntry.append(LIST_SEPARATOR).append(listValues[i]);
                }
                listEntryStr = listEntry.toString();
                if (listEntryStr.startsWith(LIST_INIT_STR)) {
                    if (objectToMimic.getClass().isArray()) {
                        parametersList.add(extractArrayFromString((Object[]) objectToMimic, listEntryStr.trim()));
                    } else if (objectToMimic instanceof List) {
                        parametersList.add(extractListFromString((List) objectToMimic, listEntryStr.trim()));
                    }
                } else if (listEntryStr.startsWith(MAP_INIT_STR)) {
                    parametersList.add(extractMapFromString((Map) objectToMimic, listEntryStr.trim()));
                } else {
                    parametersList.add(getObjectFromSimpleName(objectToMimic.getClass(), listEntryStr.trim()));
                }
            }
        } else {
            parametersList.add(getObjectFromSimpleName(objectToMimic.getClass(), listInfos));
        }
        return parametersList;
    }

    /**
     * Converti une donnée (dataToConvert) en fonction du dataType passé en paramètre (dataTypeToParse) Méthode
     * récursive - on met à jour le parse et la data à convertir symétriquement
     *
     * @param dataTypeToParse
     * @param dataToConvert
     * @return
     * @throws ClientException
     * @throws OperationException
     */
    private Object parseDataType(String dataTypeToParse, String dataToConvert) throws OperationException {
        String dataType = dataTypeToParse;
        String dataConvert = dataToConvert;

        if (dataTypeToParse.startsWith(LIST_INIT_STR)) {
            // if dataTypToParse = [Ltype]

            ArrayList<Object> arrayList = new ArrayList<Object>();
            dataType = dataType.replaceFirst(LIST_INIT_STR_REGEXP, "");
            dataType = StringUtils.removeEnd(dataType, LIST_END_STR);
            dataConvert = dataConvert.replaceFirst(LIST_INIT_STR_REGEXP, "");
            dataConvert = StringUtils.removeEnd(dataConvert, LIST_END_STR);

            if (dataType.startsWith(LIST_INIT_STR) || dataType.startsWith(MAP_INIT_STR)) {
                // if dataType = [Mtype/type] or [Ltype]
                // Add to the list data converted
                arrayList.add(parseDataType(dataType, dataToConvert));
            } else {
                // else dataType = simpletype
                if (dataConvert.contains(LIST_SEPARATOR)) {
                    // if dataToConvert has many values
                    String[] datasToAdd = dataConvert.split(LIST_SEPARATOR);
                    for (String data : datasToAdd) {
                        arrayList.add(getObjectFromSimpleName(dataType.trim(), data.trim()));
                    }
                } else {
                    arrayList.add(getObjectFromSimpleName(dataType.trim(), dataConvert.trim()));
                }
            }
            return arrayList;
        } else if (dataTypeToParse.startsWith(MAP_INIT_STR)) {
            // if dataTypeToParse = [Mtype/type]

            HashMap<String, Object> map = new HashMap<String, Object>();
            dataType = dataType.replaceFirst(MAP_INIT_STR_REGEXP, "");
            dataType = StringUtils.removeEnd(dataType, MAP_END_STR);
            dataConvert = dataConvert.replaceFirst(MAP_INIT_STR_REGEXP, "");
            dataConvert = StringUtils.removeEnd(dataConvert, MAP_END_STR);

            if (dataType.contains(MAP_SEPARATOR)) {
                // if map is String,Object, try to know Object type
                // ex : type/type1%type/type2%type/type3

                ArrayList<String> keysValuesTypes = splitMapInfos(dataType);
                ArrayList<String> keysValuesValues = splitMapInfos(dataConvert);
                if (keysValuesTypes.size() != keysValuesValues.size()) {
                    throw new OperationException(WRONG_DATATYPE_EXC);
                }

                for (int i = 0; i < keysValuesTypes.size(); i++) {
                    String keyValueTypeEntry = keysValuesTypes.get(i);
                    String keyValueValueEntry = keysValuesValues.get(i);

                    String[] keyValueValueArray = keyValueValueEntry.split(KEY_VALUE_SEPARATOR, 2);
                    String[] keyValueTypeArray = keyValueTypeEntry.split(KEY_VALUE_SEPARATOR, 2);

                    map.put(
                        keyValueValueArray[0].trim(),
                        parseDataType(keyValueTypeArray[1].trim(), keyValueValueArray[1].trim())
                    );
                }
            } else {
                // map is String/Type
                String[] entryTypes = dataType.split(KEY_VALUE_SEPARATOR, 2);
                if (String.class.getSimpleName().equalsIgnoreCase(entryTypes[0].trim())) {
                    String valueType = entryTypes[1].trim();
                    if (valueType.startsWith(MAP_INIT_STR)) {
                        // if type=[Mtype]

                        if (dataConvert.contains(MAP_SEPARATOR)) {
                            // if dataConvert has many map values of map type
                            ArrayList<String> keysValuesEntry = splitMapInfos(dataConvert);

                            for (int i = 0; i < keysValuesEntry.size(); i++) {
                                String[] entryKeyValue = keysValuesEntry.get(0).split(KEY_VALUE_SEPARATOR, 2);
                                map.put(entryKeyValue[0].trim(), parseDataType(valueType, entryKeyValue[1]));
                            }
                        } else {
                            // dataConvert has one value of type map
                            String[] entryKeyValue = dataConvert.split(KEY_VALUE_SEPARATOR);
                            map.put(entryKeyValue[0].trim(), parseDataType(valueType, entryKeyValue[1]));
                        }
                    } else if (valueType.startsWith(LIST_INIT_STR)) {
                        // if type = [Ltype]

                        if (dataConvert.contains(LIST_SEPARATOR)) {
                            // if dataConvert has many list values of map type
                            String[] entryValues = dataConvert.split(LIST_SEPARATOR);
                            StringBuilder entry = new StringBuilder();
                            for (int i = 0; i < entryValues.length; i++) {
                                entry.delete(0, entry.length()).append(entryValues[i]);
                                if (entry.toString().contains(LIST_INIT_STR)) {
                                    // A list was inside - rebuild
                                    while (!entryValues[i].contains(LIST_END_STR)) {
                                        entry.append(LIST_SEPARATOR).append(entryValues[++i]);
                                    }
                                    // last token to add
                                    entry.append(LIST_SEPARATOR).append(entryValues[i]);
                                }
                                String[] entryKeyValue = entry.toString().split(KEY_VALUE_SEPARATOR);
                                map.put(entryKeyValue[0].trim(), parseDataType(valueType, entryKeyValue[1]));
                            }
                        } else {
                            // dataConvert has one value of type list
                            String[] entryKeyValue = dataConvert.split(KEY_VALUE_SEPARATOR);
                            map.put(entryKeyValue[0].trim(), parseDataType(valueType, entryKeyValue[1]));
                        }
                    } else {
                        // type is simpleType
                        String[] entryValues = dataConvert.split(MAP_SEPARATOR);
                        for (int i = 0; i < entryValues.length; i++) {
                            String[] entry = entryValues[i].split(KEY_VALUE_SEPARATOR);
                            map.put(entry[0].trim(), getObjectFromSimpleName(valueType, entry[1].trim()));
                        }
                    }
                } else {
                    throw new OperationException(WRONG_DATATYPE_EXC);
                }
            }
            return map;
        } else {
            // Si dataTypeToParse = simpleType
            return getObjectFromSimpleName(dataTypeToParse, dataToConvert);
        }
    }

    /**
     * Détermine si une map contient des objets instanciés
     *
     * @param map
     * @param inputParameters
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean isMapWellKnown(Map<String, Object> map, String inputParameters) {
        if (map.isEmpty()) {
            return false;
        }

        for (Entry<String, Object> entry : map.entrySet()) {
            if (inputParameters.contains(entry.getKey())) {
                if (entry.getValue() == null) {
                    return false;
                }
                if (entry.getValue() instanceof List) {
                    if (((List) entry.getValue()).isEmpty()) {
                        return false;
                    }
                } else if (entry.getValue().getClass().isArray()) {
                    if (((Object[]) entry.getValue()).length == 0) {
                        return false;
                    }
                } else if (entry.getValue() instanceof Map) {
                    return isMapWellKnown((Map) entry.getValue(), inputParameters);
                }
            }
        }
        return true;
    }

    private void saveDocument() {
        session.saveDocument(docToModify);
        session.save();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean compareLists(List<Object> list1, List<Object> list2) {
        if (list1 == list2) {
            return true;
        }

        if (list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i) instanceof List) {
                if (!compareLists((List) list1.get(i), (List) list2.get(i))) {
                    return false;
                }
            } else if (list1.get(i) instanceof Map) {
                if (!compareMaps((Map) list1.get(i), (Map) list2.get(i))) {
                    return false;
                }
            } else if (list1.get(i).getClass().isArray()) {
                if (!compareArrays((Object[]) list1.get(i), (Object[]) list2.get(i))) {
                    return false;
                }
            } else {
                if (!compareObjects(list1.get(i), list2.get(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean compareMaps(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == map2) {
            return true;
        }

        if (map1.size() != map2.size()) {
            return false;
        }

        for (Entry<String, Object> entry : map1.entrySet()) {
            if (!map2.containsKey(entry.getKey())) {
                return false;
            } else {
                if (entry.getValue() instanceof List) {
                    if (!compareLists((List) entry.getValue(), (List) map2.get(entry.getKey()))) {
                        return false;
                    }
                } else if (entry.getValue() instanceof Map) {
                    if (!compareMaps((Map) entry.getValue(), (Map) map2.get(entry.getKey()))) {
                        return false;
                    }
                } else if (entry.getValue().getClass().isArray()) {
                    if (!compareArrays((Object[]) entry.getValue(), (Object[]) map2.get(entry.getKey()))) {
                        return false;
                    }
                } else {
                    if (!compareObjects(entry.getValue(), map2.get(entry.getKey()))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean compareArrays(Object[] array1, Object[] array2) {
        if (array1 == array2) {
            return true;
        }

        if (!array1.getClass().isArray() || !array2.getClass().isArray()) {
            return false;
        }

        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] instanceof List) {
                if (!compareLists((List) array1[i], (List) array2[i])) {
                    return false;
                }
            } else if (array1[i] instanceof Map) {
                if (!compareMaps((Map) array1[i], (Map) array2[i])) {
                    return false;
                }
            } else if (array1[i].getClass().isArray()) {
                if (!compareArrays((Object[]) array1[i], (Object[]) array2[i])) {
                    return false;
                }
            } else {
                if (!compareObjects(array1[i], array2[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean compareObjects(Object obj, Object dataToUpdate) {
        boolean isCalendar = obj instanceof Calendar;
        if (!obj.equals(dataToUpdate) && !isCalendar) {
            return false;
        } else if (isCalendar) {
            // We can't compare calendars with equals because of lack of precision
            Calendar dataInArray = (Calendar) obj;
            Calendar dataToRemove = (Calendar) dataToUpdate;
            if (
                dataInArray.get(Calendar.MINUTE) != dataToRemove.get(Calendar.MINUTE) ||
                dataInArray.get(Calendar.HOUR) != dataToRemove.get(Calendar.HOUR) ||
                dataInArray.get(Calendar.DAY_OF_YEAR) != dataToRemove.get(Calendar.DAY_OF_YEAR) ||
                dataInArray.get(Calendar.MONTH) != dataToRemove.get(Calendar.MONTH) ||
                dataInArray.get(Calendar.YEAR) != dataToRemove.get(Calendar.YEAR)
            ) {
                return false;
            }
        }
        return true;
    }

    private Object getObjectFromSimpleName(@SuppressWarnings("rawtypes") Class element, String dataToConvert)
        throws OperationException {
        return getObjectFromSimpleName(element.getSimpleName(), dataToConvert);
    }

    private Object getObjectFromSimpleName(String elementName, String dataToConvert) throws OperationException {
        if (String.class.getSimpleName().equalsIgnoreCase(elementName)) {
            return dataToConvert;
        } else if ("int".equalsIgnoreCase(elementName)) {
            return Integer.parseInt(dataToConvert);
        } else if (Integer.class.getSimpleName().equalsIgnoreCase(elementName)) {
            return Integer.getInteger(dataToConvert);
        } else if (Double.class.getSimpleName().equalsIgnoreCase(elementName)) {
            return Double.parseDouble(dataToConvert);
        } else if (Float.class.getSimpleName().equalsIgnoreCase(elementName)) {
            return Float.parseFloat(dataToConvert);
        } else if (Long.class.getSimpleName().equalsIgnoreCase(elementName)) {
            return Long.parseLong(dataToConvert);
        } else if (Calendar.class.getSimpleName().equalsIgnoreCase(elementName)) {
            return SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.parseToCalendar(dataToConvert);
        } else if (GregorianCalendar.class.getSimpleName().equalsIgnoreCase(elementName)) {
            return SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.parseToCalendar(dataToConvert);
        } else {
            throw new OperationException(INCOMPATIBLE_TYPE_EXC + elementName);
        }
    }

    private void checkParameters() throws OperationException {
        boolean isEmptyId = StringUtils.isEmpty(id);
        boolean isEmptyPath = StringUtils.isEmpty(path);
        if ((isEmptyId && isEmptyPath) || StringUtils.isEmpty(property) || StringUtils.isEmpty(mode)) {
            throw new OperationException(EMPTY_PARAM_EXC);
        }

        if (!isEmptyId && !isEmptyPath) {
            throw new OperationException(BOTH_ID_PATH_EXC);
        }

        try {
            if (!isEmptyId) {
                docToModify = session.getDocument(new IdRef(id));
            }
            if (!isEmptyPath) {
                docToModify = session.getDocument(new PathRef(path));
            }
        } catch (NuxeoException exc) {
            if (!isEmptyId) {
                throw new OperationException(DOC_NOT_FOUND_FOR_ID + id, exc);
            }
            if (!isEmptyPath) {
                throw new OperationException(DOC_NOT_FOUND_FOR_PATH + path, exc);
            }
        }
        if (docToModify == null) {
            throw new OperationException(DOC_NOT_FOUND_FOR_ID + id);
        }
        if (
            !REMOVE_MODE.equalsIgnoreCase(mode) &&
            !REMOVEALL_MODE.equalsIgnoreCase(mode) &&
            !ADD_MODE.equalsIgnoreCase(mode)
        ) {
            throw new OperationException(getModeException(mode));
        }
        if ((REMOVE_MODE.equalsIgnoreCase(mode) || ADD_MODE.equalsIgnoreCase(mode)) && data.isEmpty()) {
            throw new OperationException(EMPTY_DATA_PARAM_EXC);
        }
        try {
            propertyToModify = docToModify.getProperty(property);
        } catch (NuxeoException exc) {
            throw new OperationException(CANT_GET_PROPERTY_EXC + property, exc);
        }
    }

    private void checkDataType() throws OperationException {
        if (StringUtils.isBlank(dataType)) {
            throw new OperationException(EMPTY_DATATYPE_EXC + property);
        }
    }

    public static String getModeException(String modeParameter) {
        StringBuilder excStr = new StringBuilder();
        excStr
            .append("Mode ")
            .append(modeParameter)
            .append(" non reconnu ; ")
            .append(REMOVE_MODE)
            .append(", ")
            .append(REMOVEALL_MODE)
            .append(", ")
            .append(ADD_MODE)
            .append(" autorisés");
        return excStr.toString();
    }
}
