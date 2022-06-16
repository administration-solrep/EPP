package fr.dila.st.ui.contentview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import avro.shaded.com.google.common.collect.Lists;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.core.CoreQueryPageProviderDescriptor;
import org.nuxeo.ecm.platform.query.core.WhereClauseDescriptor;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryAndFetchPageProvider;

public class AbstractDTOPageProviderTest {
    private AbstractDTOPageProvider provider = Mockito.mock(AbstractDTOPageProvider.class, Mockito.CALLS_REAL_METHODS);

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel searchDocumentModel;

    private void initDefaultProviderData(CoreQueryPageProviderDescriptor definition) {
        definition.setPattern("Select distinct ecm:uuid as id From Test");

        Map<String, Serializable> props = new TreeMap<>();
        props.put(AbstractDTOPageProvider.CORE_SESSION_PROPERTY, (Serializable) session);

        provider.setDefinition(definition);
        provider.setProperties(props);
        provider.setSortInfos(Lists.newArrayList(new SortInfo("columnA", true), new SortInfo("columnB", false)));
        provider.setSearchDocumentModel(searchDocumentModel);
    }

    @Test
    public void testBuildQuery() {
        CoreQueryPageProviderDescriptor definition = new CoreQueryPageProviderDescriptor();

        initDefaultProviderData(definition);

        provider.buildQuery();

        assertEquals(
            "Select distinct ecm:uuid as id , columnA, columnB From Test ORDER BY columnA , columnB DESC",
            provider.getCurrentQuery()
        );

        provider.setSortInfos(null);

        provider.buildQuery();

        assertEquals("Select distinct ecm:uuid as id From Test", provider.getCurrentQuery());

        WhereClauseDescriptor where = new WhereClauseDescriptor();
        where.setFixedPart("columnA='toto'");
        try {
            Field protectedfield =
                CoreQueryPageProviderDescriptor.class.getSuperclass().getDeclaredField("whereClause");
            protectedfield.setAccessible(true);
            protectedfield.set(definition, where);

            provider.buildQuery();

            assertEquals("Select distinct ecm:uuid as id From Test where columnA='toto'", provider.getCurrentQuery());

            provider.setSearchDocumentModel(null);

            provider.buildQuery();

            fail("Une exception doit être lancée");
        } catch (Exception e) {
            assertTrue(e instanceof NuxeoException);
        }
    }

    @Test
    public void testGetCurrentPage() {
        CoreQueryPageProviderDescriptor definition = new CoreQueryPageProviderDescriptor();
        List<Map<String, Serializable>> currentItems = new ArrayList<>();

        initDefaultProviderData(definition);

        Mockito
            .doAnswer(
                new Answer<Void>() {

                    @Override
                    public Void answer(InvocationOnMock invocation) throws Throwable {
                        Field protectedfield = CoreQueryAndFetchPageProvider.class.getDeclaredField("currentItems");
                        protectedfield.setAccessible(true);
                        protectedfield.set(provider, currentItems);
                        return null;
                    }
                }
            )
            .when(provider)
            .fillCurrentPageMapList(session);

        List<Map<String, Serializable>> result = provider.getCurrentPage();

        assertNotNull(result);
        assertEquals(currentItems, result);
    }
}
