package fr.sword.naiad.nuxeo.ufnxql.core.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.schema.FacetNames;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMakerFeature;

@RunWith(FeaturesRunner.class)
@Features(FlexibleQueryMakerFeature.class)
@Deploy({ "fr.sword.naiad.nuxeo.ufnxql.core:OSGI-INF/test-ufnxql-config-1-contrib.xml",
        "fr.sword.naiad.nuxeo.ufnxql.core:OSGI-INF/test-ufnxql-config-2-contrib.xml",
        "fr.sword.naiad.nuxeo.ufnxql.core:OSGI-INF/test-ufnxql-config-3-contrib.xml"})
public class FNXQLConfigServiceTest {

    public static final int DEFAULT_FUNCTION_NUMBER = 6;

    @Test
    public void testService() throws NuxeoException {
        final FNXQLConfigService service = ServiceUtil.getService(FNXQLConfigService.class);
        Assert.assertNotNull(service);
        final String typeNotDeclared = "doc_type_xx";
        final String facetNotDeclared = "doc_facet_xx";

        // type non declare
        Assert.assertNull(service.getSchemaForType(typeNotDeclared));
        Assert.assertFalse(service.isMixinTypeGlobalToTypes(facetNotDeclared));

        // type declare
        Assert.assertEquals("doc_schema_1", service.getSchemaForType("doc_type_1"));
        Assert.assertTrue(service.isMixinTypeGlobalToTypes("doc_facet_1"));

        // type redeclare
        Assert.assertEquals("doc_schema_22", service.getSchemaForType("doc_type_2"));
        Assert.assertNull(service.getSchemaForType("doc_type_3"));

        final Set<String> facets = new HashSet<String>(Arrays.asList(new String[] { "doc_facet_xx", "doc_facet_1",
                "doc_facet_2" }));
        final Set<String> instanceFacets = new HashSet<String>(Arrays.asList(new String[] { "doc_facet_xx",
                "doc_facet_2" }));
        Assert.assertEquals(instanceFacets, service.extractMixinTypePerInstance(facets));

        // type declare par défaut
        Assert.assertTrue(service.isMixinTypeGlobalToTypes(FacetNames.FOLDERISH));
        Assert.assertTrue(service.isMixinTypeGlobalToTypes(FacetNames.HIDDEN_IN_NAVIGATION));

        // function declarée : nombre par défaut + 1
        Assert.assertEquals(DEFAULT_FUNCTION_NUMBER + 1, service.getUFNXQLFunctions().size());

    }

}
