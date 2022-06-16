package fr.dila.st.api.requeteur;

import java.util.Collection;
import java.util.Set;
import org.nuxeo.ecm.core.schema.SchemaManager;

/**
 * Une interface qui représente un document dont les champs seront requêté par le requêteur de Réponses ou Solon.
 *
 * @author jgomez
 *
 */
public interface SelectedDocument {
    /**
     * Récupère la clé du document, une lettre unique qui désigne le document finissant par un point et qui doit
     * correspondre à la clé de la contribution jointure (exemple : d. pour dossier)
     *
     * @return la clé du document
     */
    String getKey();

    /**
     * Met en place la clé du document
     *
     * @param key
     *            une chaîne de caractère idenfifiant le document
     */
    void setKey(String key);

    /**
     * Récupère le nom du document. Cela correspond au nom du type Nuxéo
     *
     * @return le nom du document
     */
    String getName();

    /**
     * Met en place le nom du document
     *
     * @param name
     *            le nom du document
     */
    void setName(String name);

    /**
     * Récupère les schémas associés à ce document. Ce sont les schémas qui vont être utilisés pour récupérer les noms
     * des champs du document.
     *
     * @return
     */
    String[] getSchemas();

    /**
     * Met en place les champs exclus pour ce document.
     *
     * @param excludedFields
     *            les champs qui seront exclus de la génération des widgets, et qui n'apparaîtront pas dans le requêteur
     */
    void setExcludedFields(String[] excludedFields);

    /**
     * Recupère les champs exclus du document
     *
     * @return les champs à exclure
     */
    Set<String> getExcludedFields();

    /**
     * Toutes les descriptions des champs qui correspondent à aux schémas de ce document, à l'exception des champs à
     * exclure
     *
     * @param schemaManager
     *            un manager de schéma
     * @return la collection de champs
     */
    Collection<? extends RequeteurWidgetDescription> createWidgetDescriptions(SchemaManager schemaManager);

    /**
     * Met en place les champs supplémentaire pour ce document.
     *
     * @param addedFields
     *            les champs qui n'apparaissent dans le schéma du document mais qui seront générés par le requêteur
     */
    void setAddedFields(String[] addedFields);

    /**
     * Récupère les champs supplémentaires du document
     *
     * @return Les champs supplémentaires du document
     */
    String[] getAddedFields();
}
