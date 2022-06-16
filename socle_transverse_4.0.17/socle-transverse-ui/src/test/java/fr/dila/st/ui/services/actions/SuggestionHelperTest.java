package fr.dila.st.ui.services.actions;

import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.ui.services.actions.suggestion.SuggestionHelper;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ QueryUtils.class })
@PowerMockIgnore("javax.management.*")
public class SuggestionHelperTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private CoreSession mockSession;

    private String colname = "col2";

    private String query = "This is a select SQL query";
    private Object[] params = new Object[] { "param1", "param2" };

    @Before
    public void setUp() {
        List<Map<String, Serializable>> list = IntStream
            .rangeClosed(1, 10)
            .mapToObj(SuggestionHelperTest::createValues)
            .collect(Collectors.toList());

        IterableQueryResult mockIqr = Mockito.mock(IterableQueryResult.class);
        when(mockIqr.spliterator()).thenReturn(list.spliterator());

        PowerMockito.mockStatic(QueryUtils.class);
        when(QueryUtils.doSqlQuery(mockSession, new String[] { colname }, query, params, 10000, 0)).thenReturn(mockIqr);
    }

    @Test
    public void test_buildSuggestionsList() {
        List<String> expected = IntStream.rangeClosed(1, 10).mapToObj(i -> "value2_" + i).collect(Collectors.toList());

        List<String> strList = SuggestionHelper.buildSuggestionsList(colname, mockSession, query, params);

        Assertions.assertThat(strList).containsExactlyElementsOf(expected);
    }

    private static Map<String, Serializable> createValues(int index) {
        return ImmutableMap.of("col1", "value1_" + index, "col2", "value2_" + index, "col3", "value3_" + index);
    }
}
