package fr.dila.ss.core.service.vocabulary;

import static com.google.common.collect.Lists.newArrayList;
import static fr.dila.st.api.constant.STVocabularyConstants.ROUTING_TASK_TYPE_VOCABULARY;
import static fr.dila.st.api.constant.STVocabularyConstants.VOCABULARY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.ID_PROPERTY;
import static fr.dila.st.core.service.AbstractCommonVocabularyServiceImpl.LABEL_PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import fr.dila.ss.api.service.vocabulary.RoutingTaskTypeService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(STServiceLocator.class)
public class RoutingTaskTypeServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private RoutingTaskTypeService service;

    @Mock
    private DocumentModel routingTaskType1Doc;

    @Mock
    private DocumentModel routingTaskType2Doc;

    @Mock
    private DocumentModel routingTaskType3Doc;

    @Mock
    private VocabularyService vocabularyService;

    @Before
    public void setUp() {
        service = new RoutingTaskTypeServiceImpl();

        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getVocabularyService()).thenReturn(vocabularyService);
    }

    @Test
    public void getRoutingTaskTypes() {
        ImmutablePair<Integer, String> routingTaskType1 = ImmutablePair.of(1, "Pour rédaction");
        ImmutablePair<Integer, String> routingTaskType2 = ImmutablePair.of(2, "Pour attribution");
        ImmutablePair<Integer, String> routingTaskType3 = ImmutablePair.of(3, "Pour visa");

        when(vocabularyService.getAllEntry(ROUTING_TASK_TYPE_VOCABULARY))
            .thenReturn(
                new DocumentModelListImpl(newArrayList(routingTaskType1Doc, routingTaskType2Doc, routingTaskType3Doc))
            );

        when(routingTaskType1Doc.getProperty(VOCABULARY, ID_PROPERTY))
            .thenReturn(routingTaskType1.getLeft().toString());
        when(routingTaskType1Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(routingTaskType1.getRight());

        when(routingTaskType2Doc.getProperty(VOCABULARY, ID_PROPERTY))
            .thenReturn(routingTaskType2.getLeft().toString());
        when(routingTaskType2Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(routingTaskType2.getRight());

        when(routingTaskType3Doc.getProperty(VOCABULARY, ID_PROPERTY))
            .thenReturn(routingTaskType3.getLeft().toString());
        when(routingTaskType3Doc.getProperty(VOCABULARY, LABEL_PROPERTY)).thenReturn(routingTaskType3.getRight());

        List<ImmutablePair<Integer, String>> routingTaskTypes = service.getEntries();

        assertThat(routingTaskTypes).containsExactly(routingTaskType1, routingTaskType2, routingTaskType3);
    }

    @Test
    public void getRoutingTaskTypesWithError() {
        when(vocabularyService.getAllEntry(ROUTING_TASK_TYPE_VOCABULARY)).thenReturn(null);

        Throwable throwable = catchThrowable(() -> service.getEntries());
        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessage("Une erreur s'est produite lors de la récupération des types d'étape");
    }
}
