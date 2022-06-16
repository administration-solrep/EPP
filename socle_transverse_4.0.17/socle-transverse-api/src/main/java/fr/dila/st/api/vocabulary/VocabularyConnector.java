package fr.dila.st.api.vocabulary;

import java.util.List;

/**
 * Wrapper autour d'un vocabulaire (ou d'un directory). L'objet est censé être contruit à partir du service de
 * vocabulaire.
 *
 * @author jgomez
 */
public interface VocabularyConnector {
    /**
     * Permet de controler la présence d'une valeur dans le vocabulaire.
     *
     * @param valueToCheck
     * @return Vraie si la valeur est présente.
     */
    Boolean check(String valueToCheck);

    /**
     * Permet de renvoyer une liste de suggestions à partir des premières lettres d'un mot.
     *
     * @param suggestion
     *            Les premières lettres.
     * @return une liste de valeurs correspondants au début du mot.
     */
    List<String> getSuggestion(String suggestion);
}
