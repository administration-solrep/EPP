package fr.dila.st.ui.th.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.ui.services.FragmentService;
import fr.dila.st.ui.services.HeaderService;
import fr.dila.st.ui.services.MenuService;
import fr.dila.st.ui.th.model.templates.FakeAddFragmentTemplate;
import fr.dila.st.ui.th.model.templates.FakeMultipleComplexFragmentTemplate;
import fr.dila.st.ui.th.model.templates.FakeMultipleFragmentTemplate;
import fr.dila.st.ui.th.model.templates.FakeOverrideFragmentTemplate;
import fr.dila.st.ui.th.model.templates.FakeSingleFragmentTemplate;
import fr.dila.st.ui.th.model.templates.FakeTwiceSingleFragmentTemplate;
import fr.dila.st.ui.th.model.templates.FakeWithoutFragmentTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

@RunWith(FeaturesRunner.class)
@Features({ TransactionalFeature.class, SolonMockitoFeature.class })
public class ThTemplateTest {
    @Mock
    @RuntimeService
    MenuService menuServiceMock;

    @Mock
    @RuntimeService
    HeaderService headerServiceMock;

    @Mock
    @RuntimeService
    FragmentService fragmentServiceMock;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession session;

    @Before
    public void before() {
        Map<String, Object> mockMenuMap = new HashMap<>();
        mockMenuMap.put("menu", "valueMenu");
        when(menuServiceMock.getData(Mockito.anyObject())).thenReturn(mockMenuMap);

        Map<String, Object> mockHeaderMap = new HashMap<>();
        mockHeaderMap.put("header", "valueHeader");
        when(headerServiceMock.getData(Mockito.anyObject())).thenReturn(mockHeaderMap);

        Map<String, Object> mockFragmentMap = new HashMap<>();
        mockFragmentMap.put("fragment", "valueFragment");
        when(fragmentServiceMock.getData(Mockito.anyObject())).thenReturn(mockFragmentMap);

        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(session);
    }

    @Test
    public void processDataEmptyTest() {
        ThTemplate template = new ThTemplate();
        template.setContext(context);
        assertNotNull(template.getData());
        assertEquals(true, template.getData().isEmpty());

        template.processData();
        assertNotNull(template.getData());
        assertTrue(template.getData().isEmpty());
    }

    @Test
    public void processDatawithoutFragmentTest() {
        ThTemplate template = new FakeWithoutFragmentTemplate();
        template.setContext(context);
        assertNotNull(template.getData());
        assertEquals(true, template.getData().isEmpty());

        template.processData();
        Map<String, Object> processedMap = template.getData();
        assertNotNull(processedMap);
        assertTrue(processedMap.isEmpty());
    }

    @Test
    public void processDataSingleFragmentTest() {
        ThTemplate template = new FakeSingleFragmentTemplate();
        template.setContext(context);
        assertNotNull(template.getData());
        assertEquals(true, template.getData().isEmpty());

        template.processData();
        Map<String, Object> processedMap = template.getData();
        assertNotNull(processedMap);
        assertFalse(processedMap.isEmpty());
        assertEquals(1, processedMap.size());
        assertNotNull(processedMap.get("menu"));
        assertEquals("valueMenu", processedMap.get("menu"));
    }

    @Test
    public void processDataSingleFragmentWithDataTest() {
        //Put initial data
        SpecificContext myContext = spy(new SpecificContext());
        Map<String, Object> initialMap = new HashMap<>();
        initialMap.put("initial", "initialValue");
        myContext.setContextData(initialMap);
        myContext.setCopyDataToResponse(true);
        doReturn(webContext).when(myContext).getWebcontext();

        //Create template with only one fragment
        ThTemplate template = new FakeSingleFragmentTemplate("test", myContext);

        //Check if map is initialized
        Map<String, Object> processedMap = template.getData();
        assertNotNull(processedMap);
        assertFalse(processedMap.isEmpty());
        assertEquals(1, processedMap.size());

        //Process template
        template.processData();

        //Check data template.
        //We must have initial data + fragment data
        processedMap = template.getData();
        assertNotNull(processedMap);
        assertFalse(processedMap.isEmpty());
        assertEquals(2, processedMap.size());

        //Check fragments data from service
        assertNotNull(processedMap.get("menu"));
        assertEquals("valueMenu", processedMap.get("menu"));
        assertNotNull(processedMap.get("initial"));
        assertEquals("initialValue", processedMap.get("initial"));
    }

    @Test
    public void getNameTest() {
        SpecificContext myContext = new SpecificContext();
        Map<String, Object> initialMap = new HashMap<>();
        myContext.setContextData(initialMap);
        myContext.setCopyDataToResponse(true);
        ThTemplate template = new FakeSingleFragmentTemplate("test", myContext);
        assertNotNull(template.getName());
        assertEquals("test", template.getName());
    }

    @Test
    public void setNameTest() {
        ThTemplate template = new FakeSingleFragmentTemplate();
        assertNull(template.getName());

        template.setName("test");
        assertNotNull(template.getName());
        assertEquals("test", template.getName());
    }

    @Test
    public void setMapTest() {
        ThTemplate template = new FakeSingleFragmentTemplate();
        assertNotNull(template.getData());
        assertEquals(true, template.getData().isEmpty());

        Map<String, Object> modifiedMap = new HashMap<>();
        modifiedMap.put("modified", "modifiedValue");
        template.setData(modifiedMap);
        Map<String, Object> processedMap = template.getData();
        assertNotNull(processedMap);
        assertFalse(processedMap.isEmpty());
        assertEquals(1, processedMap.size());
        assertNotNull(processedMap.get("modified"));
        assertEquals("modifiedValue", processedMap.get("modified"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void processDataMultipleFragmentTest() {
        //Create template with multiple fragment
        ThTemplate template = new FakeMultipleFragmentTemplate();
        template.setContext(context);

        //Check data is empty
        assertNotNull(template.getData());
        assertEquals(true, template.getData().isEmpty());

        //Process template
        template.processData();

        //Check data template.
        //We must have fragments data + fragment blocks without specific container
        Map<String, Object> processedMap = template.getData();
        assertNotNull(processedMap);
        assertFalse(processedMap.isEmpty());
        assertEquals(3, processedMap.size());

        //Check fragments blocks from default container
        assertNotNull(processedMap.get("_blocks"));

        List<FragmentBlock> blockList = (List<FragmentBlock>) processedMap.get("_blocks");
        assertEquals(2, blockList.size());

        //Check first fragment
        FragmentBlock processedBlock = blockList.get(0);
        assertNotNull(processedBlock);
        assertEquals("fragments/header", processedBlock.getFilename());
        assertEquals("header", processedBlock.getName());

        //Check second fragment
        processedBlock = blockList.get(1);
        assertNotNull(processedBlock);
        assertEquals("fragments/menu", processedBlock.getFilename());
        assertEquals("menu", processedBlock.getName());

        //Check fragments data from service
        assertNotNull(processedMap.get("menu"));
        assertEquals("valueMenu", processedMap.get("menu"));
        assertNotNull(processedMap.get("header"));
        assertEquals("valueHeader", processedMap.get("header"));
    }

    @Test
    public void processFragmentOverrideTwice() {
        ThTemplate template = new FakeTwiceSingleFragmentTemplate();
        template.setContext(context);

        //Check data is empty
        assertNotNull(template.getData());
        assertEquals(true, template.getData().isEmpty());

        //Process template
        template.processData();

        Map<String, Object> processedMap = template.getData();
        assertNotNull(processedMap);
        assertFalse(processedMap.isEmpty());
        assertEquals(1, processedMap.size());
        assertNotNull(processedMap.get("menu"));
        assertEquals("valueMenu", processedMap.get("menu"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void processDataMultipleComplexFragmentTest() {
        //Create template with multiple fragment with multiple container
        ThTemplate template = new FakeMultipleComplexFragmentTemplate();
        template.setContext(context);

        //Check data is empty
        assertNotNull(template.getData());
        assertEquals(true, template.getData().isEmpty());

        //Process template
        template.processData();

        //Check data template.
        //We must have fragments data + fragment blocks in each specific container
        Map<String, Object> processedMap = template.getData();
        assertNotNull(processedMap);
        assertFalse(processedMap.isEmpty());
        assertEquals(4, processedMap.size());

        //Check first container
        assertNotNull(processedMap.get("header_blocks"));
        List<FragmentBlock> blockList = (List<FragmentBlock>) processedMap.get("header_blocks");
        assertEquals(1, blockList.size());

        FragmentBlock processedBlock = blockList.get(0);
        assertNotNull(processedBlock);
        assertEquals("fragments/header", processedBlock.getFilename());
        assertEquals("header", processedBlock.getName());

        //Check second container
        assertNotNull(processedMap.get("menu_blocks"));
        blockList = (List<FragmentBlock>) processedMap.get("menu_blocks");
        assertEquals(1, blockList.size());
        processedBlock = blockList.get(0);
        assertNotNull(processedBlock);
        assertEquals("fragments/menu", processedBlock.getFilename());
        assertEquals("menu", processedBlock.getName());

        //Check fragments data from service
        assertNotNull(processedMap.get("menu"));
        assertEquals("valueMenu", processedMap.get("menu"));
        assertNotNull(processedMap.get("header"));
        assertEquals("valueHeader", processedMap.get("header"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void processDataOverideFragmentTest() {
        //Create template with fragment overriding other fragment
        ThTemplate template = new FakeOverrideFragmentTemplate();
        template.setContext(context);

        //Check data is empty
        assertNotNull(template.getData());
        assertEquals(true, template.getData().isEmpty());

        //Process template
        template.processData();

        //Check data template.
        //We must have fragments data + fragment blocks in specific container
        //The override fragment must replace the other in data
        Map<String, Object> processedMap = template.getData();
        assertNotNull(processedMap);
        assertFalse(processedMap.isEmpty());
        assertEquals(5, processedMap.size());

        //Check first container
        assertNotNull(processedMap.get("header_blocks"));
        List<FragmentBlock> blockList = (List<FragmentBlock>) processedMap.get("header_blocks");
        assertEquals(1, blockList.size());

        FragmentBlock processedBlock = blockList.get(0);
        assertNotNull(processedBlock);
        assertEquals("fragments/header", processedBlock.getFilename());
        assertEquals("header", processedBlock.getName());

        //Check second container
        assertNotNull(processedMap.get("menu_blocks"));
        blockList = (List<FragmentBlock>) processedMap.get("menu_blocks");
        assertEquals(1, blockList.size());
        processedBlock = blockList.get(0);
        assertNotNull(processedBlock);
        assertEquals("fragments/menu", processedBlock.getFilename());
        assertEquals("menu", processedBlock.getName());

        //Check fragments data from service
        //The data must came from override fragment service
        assertNotNull(processedMap.get("menu"));
        assertEquals("valueMenu", processedMap.get("menu"));
        assertNotNull(processedMap.get("fragment"));
        assertEquals("valueFragment", processedMap.get("fragment"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void processDataAddFragmentTest() {
        //Create template with fragment added to other template
        ThTemplate template = new FakeAddFragmentTemplate();
        template.setContext(context);

        //Check data is empty
        assertNotNull(template.getData());
        assertEquals(true, template.getData().isEmpty());

        //Process template
        template.processData();

        //Check data template.
        //We must have fragments data + fragment blocks in specific container
        //The new fragment must be placed before the other (same order attribute) in container
        Map<String, Object> processedMap = template.getData();
        assertNotNull(processedMap);
        assertFalse(processedMap.isEmpty());
        assertEquals(5, processedMap.size());

        //Check first container
        assertNotNull(processedMap.get("header_blocks"));
        List<FragmentBlock> blockList = (List<FragmentBlock>) processedMap.get("header_blocks");
        assertEquals(2, blockList.size());

        //Check if added block is before the other
        FragmentBlock processedBlock = blockList.get(0);
        assertNotNull(processedBlock);
        assertEquals("fragments/frag", processedBlock.getFilename());
        assertEquals("frag", processedBlock.getName());
        processedBlock = blockList.get(1);
        assertNotNull(processedBlock);
        assertEquals("fragments/header", processedBlock.getFilename());
        assertEquals("header", processedBlock.getName());

        //Check second block
        assertNotNull(processedMap.get("menu_blocks"));
        blockList = (List<FragmentBlock>) processedMap.get("menu_blocks");
        assertEquals(1, blockList.size());
        processedBlock = blockList.get(0);
        assertNotNull(processedBlock);
        assertEquals("fragments/menu", processedBlock.getFilename());
        assertEquals("menu", processedBlock.getName());

        //Check fragments data from service
        //The data from added fragment service must be set with the other
        assertNotNull(processedMap.get("menu"));
        assertEquals("valueMenu", processedMap.get("menu"));
        assertNotNull(processedMap.get("header"));
        assertEquals("valueHeader", processedMap.get("header"));
        assertNotNull(processedMap.get("fragment"));
        assertEquals("valueFragment", processedMap.get("fragment"));
    }
}
