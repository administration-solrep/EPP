package fr.dila.st.core.util;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Helper dédié à la récupération des properties.
 *
 * @author tlombard
 */
public final class ResourceHelper {
    private static final String TRANSVERSE_MESSAGES = "web/nuxeo.war/th-templates/messages/messages.properties";
    private static final String SOLREP_MESSAGES = "web/nuxeo.war/th-templates/messages/solrep/messages.properties";
    private static final String APP_MESSAGES = "web/nuxeo.war/th-templates/messages/appli/messages.properties";

    private static final STLogger LOGGER = STLogFactory.getLog(ResourceHelper.class);

    private static final String ERROR_MESSAGE = "Property non trouvée dans les bundles de l'application : %s";

    /**
     * Liste des ResourceBundles dans l'ordre de priorité : l'élément 0 contient les ressources de l'application
     * (surcharge finale), le dernier élément contient les valeurs potentiellement surchargées plus haut.
     */
    private static List<ResourceBundle> resourceBundles;

    /**
     * Utility class
     */
    private ResourceHelper() {
        // do nothing
    }

    /**
     * Initialisation d'un bundle donné.
     */
    private static ResourceBundle initBundle(String filePath) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)) {
            if (is != null) {
                return new PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            LOGGER.warn(
                STLogEnumImpl.LOG_EXCEPTION_TEC,
                "le fichier messages.properties à l'emplacement " + filePath + " n'a pas pu être lu"
            );
            throw new NuxeoException(e);
        }

        return null;
    }

    /**
     * Initialisation de l'ensemble des bundles dans l'ordre de priorité décroissant.
     */
    private static void init() {
        resourceBundles = new ArrayList<>();

        // Ressources à charger dans l'ordre de priorité décroissant
        Stream
            .of(APP_MESSAGES, SOLREP_MESSAGES, TRANSVERSE_MESSAGES)
            .forEach(
                resource -> {
                    ResourceBundle bundle = initBundle(resource);
                    if (bundle != null) {
                        resourceBundles.add(bundle);
                    } else {
                        LOGGER.trace(STLogEnumImpl.LOG_INFO_TEC, "La ressource " + resource + " n'a pas été trouvée");
                    }
                }
            );
    }

    /**
     * Récupère et renvoie la property associée à la clé indiquée en paramètre, en respectant l'ordre de priorité des
     * différents ResourceBundle. <br/>
     * Si la clé n'est trouvée dans aucun bundle, on renvoie la clé (en conformité avec le comportement Nuxeo).
     *
     * @param key
     *            clé
     * @return property associée ou la clé si elle n'est trouvée nul part.
     */
    public static String getString(String key, Object... arguments) {
        if (key == null) {
            return null;
        }

        if (resourceBundles == null) {
            init();
        }

        for (ResourceBundle resourceBundle : resourceBundles) {
            try {
                return MessageFormat.format(resourceBundle.getString(key), arguments);
            } catch (MissingResourceException e) {
                LOGGER.trace(STLogEnumImpl.LOG_INFO_TEC, "Property " + key + " non trouvée dans le bundle");
            }
        }

        // Property non trouvée
        LOGGER.warn(STLogEnumImpl.LOG_INFO_TEC, String.format(ERROR_MESSAGE, key));
        return key;
    }

    @SuppressWarnings("squid:S1166")
    public static boolean exists(String key) {
        if (resourceBundles == null) {
            init();
        }

        boolean exists = false;
        for (ResourceBundle resourceBundle : resourceBundles) {
            if (exists) {
                break;
            }
            try {
                resourceBundle.getString(key);
                exists = true;
            } catch (MissingResourceException e) {
                // continue searching in other bundles
            }
        }

        return exists;
    }

    public static Map<String, String> getStrings(Map<String, String> labelKeys) {
        return labelKeys.entrySet().stream().collect(toMap(Map.Entry::getKey, entry -> getString(entry.getValue())));
    }

    public static String translateKeysInString(String keys, String delim) {
        return ofNullable(keys)
            .map(s -> asList(s.split(delim)))
            .orElseGet(ArrayList::new)
            .stream()
            .filter(StringUtils::isNotBlank)
            .map(ResourceHelper::getString)
            .collect(joining(delim));
    }
}
