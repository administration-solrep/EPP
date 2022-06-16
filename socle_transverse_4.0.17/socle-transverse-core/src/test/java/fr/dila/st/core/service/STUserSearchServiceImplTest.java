package fr.dila.st.core.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.api.service.STUserSearchService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.query.sql.model.QueryBuilder;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class STUserSearchServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private UserManager um;

    @Mock
    private DocumentModel user1;

    @Mock
    private DocumentModel user2;

    @Mock
    private DocumentModel user3;

    @Mock
    private DocumentModel user4;

    @Mock
    private DocumentModel user5;

    private STUserSearchService service;

    @Before
    public void setUp() {
        service = new STUserSearchServiceImpl();

        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getUserManager()).thenReturn(um);

        DocumentModelList searchUsers1 = new DocumentModelListImpl(asList(user1, user2));
        DocumentModelList searchUsers2 = new DocumentModelListImpl(asList(user3, user4));
        DocumentModelList searchUsers3 = new DocumentModelListImpl(singletonList(user5));
        when(um.searchUsers(Mockito.any(QueryBuilder.class))).thenReturn(searchUsers1, searchUsers2, searchUsers3);
    }

    @Test
    public void getUsers() {
        service.getUsersFromIds(asList("id1", "id2"));
        verify(um).searchUsers(Mockito.any(QueryBuilder.class));
    }

    @Test
    public void getUsersForListGreaterThan1000() {
        List<String> ids = new ArrayList<>();
        float nbIds = 2500;
        for (int i = 0; i < nbIds; i++) {
            ids.add(String.valueOf(i));
        }

        List<DocumentModel> users = service.getUsersFromIds(ids);

        assertThat(users).containsExactly(user1, user2, user3, user4, user5);

        // should make 3 partitions of 1000 max ids each before searching each sublist
        verify(um, times(3)).searchUsers(Mockito.any(QueryBuilder.class));
    }
}
