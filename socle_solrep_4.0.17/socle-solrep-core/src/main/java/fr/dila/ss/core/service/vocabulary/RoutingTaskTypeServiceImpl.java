package fr.dila.ss.core.service.vocabulary;

import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;

import fr.dila.ss.api.service.vocabulary.RoutingTaskTypeService;
import fr.dila.st.api.constant.STVocabularyConstants;
import fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.nuxeo.ecm.core.api.DocumentModel;

public class RoutingTaskTypeServiceImpl
    extends AbstractCommonVocabularyServiceImpl<Integer>
    implements RoutingTaskTypeService {

    public RoutingTaskTypeServiceImpl() {
        super(STVocabularyConstants.ROUTING_TASK_TYPE_VOCABULARY, VOCABULARY, "types d'étape");
    }

    @Override
    protected Comparator<ImmutablePair<Integer, String>> getComparator() {
        return Comparator.comparing(ImmutablePair::getRight);
    }

    @Override
    protected Integer getId(DocumentModel doc) {
        return Integer.valueOf(getDefaultId(doc));
    }

    @Override
    public List<ImmutablePair<Integer, String>> getEntriesFiltered(List<Integer> idExcludes) {
        return getFilteredEntries(doc -> !idExcludes.contains(getId(doc)));
    }
}
