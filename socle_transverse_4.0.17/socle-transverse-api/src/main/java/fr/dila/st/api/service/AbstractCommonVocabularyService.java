package fr.dila.st.api.service;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.ImmutablePair;

public interface AbstractCommonVocabularyService<T> {
    /**
     * Récupère une entrée d'un Vocabulary sous forme d'une {@link ImmutablePair} avec l'id et le label de
     * chaque entrée du vocabulaire.
     * 
     * @param id l'identifiant d'une entrée

     * @return l'entrée trouvée d'un Vocabulary sous forme d'une {@link ImmutablePair} avec l'id et le label
     */
    Optional<ImmutablePair<T, String>> getEntry(T id);

    default String idAsString(T id) {
        if (id == null) {
            return null;
        }

        if (id instanceof String) {
            return (String) id;
        }

        if (id instanceof Integer) {
            return String.valueOf(id);
        }

        throw new IllegalArgumentException("Impossible à convertir en String, merci de surcharger");
    }

    /**
     * Récupère la liste des entrées d'un Vocabulary sous forme d'une {@link ImmutablePair} avec l'id et le label de
     * chaque entrée du vocabulaire. Les entrées sont triées dans l'ordre croissant de l'id.
     * @return la liste des entrées d'un Vocabulary sous forme d'une {@link ImmutablePair} avec l'id et le label de
     * chaque entrée du vocabulaire.
     */
    List<ImmutablePair<T, String>> getEntries();

    /**
     * Retourne les entrées commençant par le label fourni.
     * @param labelPrefix label préfixé à rechercher
     */
    List<String> getFilteredEntries(String labelPrefix);

    default String getTranslation(T id) {
        return getEntry(id).map(ImmutablePair::getRight).orElse("");
    }
}
