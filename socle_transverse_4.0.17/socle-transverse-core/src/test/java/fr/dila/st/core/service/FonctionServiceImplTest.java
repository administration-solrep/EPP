package fr.dila.st.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.FonctionService;
import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.api.user.BaseFunction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class })
public class FonctionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    FonctionService service;

    @Mock
    VocabularyService vocabularyService;

    @Mock
    DocumentModel docModel;

    @Mock
    BaseFunction baseFunction;

    @Before
    public void before() {
        service = new FonctionServiceImpl();

        when(docModel.getAdapter(BaseFunction.class)).thenReturn(baseFunction);

        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getVocabularyService()).thenReturn(vocabularyService);
        when(vocabularyService.getEntry(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR, "fonction1")).thenReturn(docModel);
        when(vocabularyService.getEntry(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR, "fonction2")).thenReturn(null);
    }

    @Test
    public void testGetFonction() {
        BaseFunction fonction = service.getFonction("fonction1");
        assertEquals(baseFunction, fonction);

        fonction = service.getFonction("fonction2");
        assertNull(fonction);
    }
}
