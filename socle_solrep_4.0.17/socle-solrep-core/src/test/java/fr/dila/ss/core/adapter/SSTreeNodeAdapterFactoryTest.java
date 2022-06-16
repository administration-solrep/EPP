package fr.dila.ss.core.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSet;
import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.ss.api.constant.SSTreeConstants;
import fr.dila.ss.api.tree.SSTreeNode;
import fr.dila.ss.core.tree.SSTreeNodeImpl;
import java.util.Arrays;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

@RunWith(MockitoJUnitRunner.class)
public class SSTreeNodeAdapterFactoryTest {
    private SSTreeNodeAdapterFactory adapter;

    @Mock
    private DocumentModel doc;

    @Before
    public void setUp() {
        adapter = new SSTreeNodeAdapterFactory();
    }

    @Test
    public void getAdapter() {
        when(doc.hasFacet(SSTreeConstants.FOLDERISH_FACET)).thenReturn(true);
        when(doc.hasSchema(SSTreeConstants.FILE_SCHEMA)).thenReturn(true);

        assertThat(adapter.getAdapter(doc, SSTreeNode.class)).isNotNull().isExactlyInstanceOf(SSTreeNodeImpl.class);
    }

    @Test
    public void getAdapterWithWrongFacet() {
        String docId = "doc-id";
        when(doc.getId()).thenReturn(docId);

        String facet = SSTreeConstants.FOLDERISH_FACET;
        when(doc.hasFacet(facet)).thenReturn(false);

        Set<String> facets = ImmutableSet.of(CaseLinkConstants.CASE_LINK_FACET);
        when(doc.getFacets()).thenReturn(facets);

        Throwable throwable = catchThrowable(() -> adapter.getAdapter(doc, SSTreeNode.class));
        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessageStartingWith(
                "Le document avec l'id %s doit contenir la facette : %s. Liste des facettes du document : %s",
                docId,
                facet,
                facets
            );
    }

    @Test
    public void getAdapterWithWrongSchema() {
        String docId = "doc-id";
        when(doc.getId()).thenReturn(docId);

        when(doc.hasFacet(SSTreeConstants.FOLDERISH_FACET)).thenReturn(true);

        String schema = SSTreeConstants.FILE_SCHEMA;
        when(doc.hasSchema(schema)).thenReturn(false);

        String[] schemas = { ActualiteConstant.ACTUALITE_SCHEMA };
        when(doc.getSchemas()).thenReturn(schemas);

        Throwable throwable = catchThrowable(() -> adapter.getAdapter(doc, SSTreeNode.class));
        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessageStartingWith(
                "Le document avec l'id %s doit contenir le schéma : %s. Liste des schémas du document : %s",
                docId,
                schema,
                Arrays.toString(schemas)
            );
    }
}
