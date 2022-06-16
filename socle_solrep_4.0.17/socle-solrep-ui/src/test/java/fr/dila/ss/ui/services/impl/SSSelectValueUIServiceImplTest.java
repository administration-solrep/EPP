package fr.dila.ss.ui.services.impl;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import fr.dila.ss.api.service.vocabulary.RoutingTaskTypeService;
import fr.dila.ss.ui.services.SSSelectValueUIService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.util.List;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class SSSelectValueUIServiceImplTest {
    private SSSelectValueUIService service;

    @Mock
    @RuntimeService
    private RoutingTaskTypeService routingTaskTypeService;

    @Before
    public void setUp() {
        service = new SSSelectValueUIServiceImpl();
    }

    @Test
    public void getRoutingTaskTypes() {
        ImmutablePair<Integer, String> routingTaskType2 = ImmutablePair.of(1, "Pour attribution");
        ImmutablePair<Integer, String> routingTaskType1 = ImmutablePair.of(2, "Pour rédaction");
        ImmutablePair<Integer, String> routingTaskType3 = ImmutablePair.of(3, "Pour visa");

        when(routingTaskTypeService.getEntries())
            .thenReturn(newArrayList(routingTaskType1, routingTaskType2, routingTaskType3));

        List<SelectValueDTO> routingTaskTypes = service.getRoutingTaskTypes();

        assertThat(routingTaskTypes)
            .extracting(SelectValueDTO::getId, SelectValueDTO::getLabel)
            .containsExactly(
                tuple(routingTaskType1.getLeft().toString(), routingTaskType1.getRight()),
                tuple(routingTaskType2.getLeft().toString(), routingTaskType2.getRight()),
                tuple(routingTaskType3.getLeft().toString(), routingTaskType3.getRight())
            );
    }
}
