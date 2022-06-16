package fr.dila.st.ui.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.VocabularyServiceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(STServiceLocator.class)
@PowerMockIgnore("javax.management.*")
public class VocabularyUtilsTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private VocabularyService vocabularyService;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getVocabularyService()).thenReturn(vocabularyService);
    }

    @Test
    public void getLabelFromVocabulary() {
        String directoryName = "directoryName";
        String entryId = "entryId";
        String entryLabel = "entryLabel";

        when(vocabularyService.getEntryLabel(directoryName, entryId)).thenReturn(entryLabel);

        assertThat(VocabularyUtils.getLabelFromVocabulary(directoryName, entryId)).isEqualTo(entryLabel);
    }

    @Test
    public void getLabelFromVocabularyWithUnknownEntry() {
        String directoryName = "directoryName";
        String entryId = "entryId";

        when(vocabularyService.getEntryLabel(directoryName, entryId)).thenReturn(VocabularyServiceImpl.UNKNOWN_ENTRY);

        assertThat(VocabularyUtils.getLabelFromVocabulary(directoryName, entryId)).isEqualTo(entryId);
    }

    @Test
    public void getLabelFromVocabularyWithDefaultEmpty() {
        String directoryName = "directoryName";
        String entryId = "entryId";
        String entryLabel = "entryLabel";

        when(vocabularyService.getEntryLabel(directoryName, entryId)).thenReturn(entryLabel);

        assertThat(VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty(directoryName, entryId))
            .isEqualTo(entryLabel);
    }

    @Test
    public void getLabelFromVocabularyWithDefaultEmptyWithNullEntry() {
        assertThat(VocabularyUtils.getLabelFromVocabularyWithDefaultEmpty("directoryName", null)).isEmpty();

        verifyZeroInteractions(vocabularyService);
    }
}
