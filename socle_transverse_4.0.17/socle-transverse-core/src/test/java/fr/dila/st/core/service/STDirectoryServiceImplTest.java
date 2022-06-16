package fr.dila.st.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRule;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;

public class STDirectoryServiceImplTest {
    @Rule
    public MockitoJUnitRule mockitoJUnitRule = new MockitoJUnitRule(this);

    @Mock
    private DirectoryService directoryService;

    @Mock
    private Session session;

    @Spy
    private STDirectoryServiceImpl service = new STDirectoryServiceImpl();

    @Before
    public void setup() {
        doReturn(directoryService).when(service).getDirectoryService();
        when(directoryService.open(any())).thenReturn(session);
    }

    @Test
    public void testGetSuggestions() {
        // Given
        DocumentModel profil = mock(DocumentModel.class);
        when(profil.getProperty("group", "groupname")).thenReturn("Rédacteur ministériel");

        when(directoryService.getDirectorySchema("groupDirectory")).thenReturn("group");

        when(session.query(any(), any())).thenReturn(new DocumentModelListImpl(Arrays.asList(profil)));

        // When
        List<String> suggestions = service.getSuggestions("red", "groupDirectory", "groupname");

        // Then

        Mockito.verify(session).query(ImmutableMap.of("groupname", "red"), new HashSet<>(Arrays.asList("groupname")));

        assertThat(suggestions).contains("Rédacteur ministériel");
    }

    @Test
    public void testGetSuggestionsEmpty() {
        // Given
        when(directoryService.getDirectorySchema("groupDirectory")).thenReturn("group");

        when(session.query(any(), any())).thenReturn(new DocumentModelListImpl());

        // When
        List<String> suggestions = service.getSuggestions("red", "groupDirectory", "groupname");

        // Then
        assertThat(suggestions).isEmpty();
    }
}
