package fr.dila.ss.api.service.vocabulary;

import fr.dila.st.api.service.AbstractCommonVocabularyService;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;

public interface RoutingTaskTypeService extends AbstractCommonVocabularyService<Integer> {
    /**
     * Récupère la liste des entrées filtré avec une liste d'identifiant à exclure, d'un Vocabulary sous forme d'une {@link ImmutablePair} avec l'id et le label de
     * chaque entrée du vocabulaire. Les entrées sont triées dans l'ordre croissant de l'id.
     * @return la liste des entrées d'un Vocabulary sous forme d'une {@link ImmutablePair} avec l'id et le label de
     * chaque entrée du vocabulaire.
     */
    List<ImmutablePair<Integer, String>> getEntriesFiltered(List<Integer> idExcludes);
}
