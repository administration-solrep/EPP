package fr.dila.st.ui.services.actions.impl;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.api.user.Profile;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.ui.services.actions.BaseFunctionSelectionActionService;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonMockitoFeature.class)
public class BaseFunctionSelectionActionServiceImplTest {
    private static final String FUNCTION1 = "fonction1";
    private static final String FUNCTION2 = "fonction2";

    private BaseFunctionSelectionActionService service;
    private List<String> functions;

    @Mock
    private DocumentModel profileDoc;

    @Mock
    private Profile profile;

    @Captor
    private ArgumentCaptor<List<String>> functionsCaptor;

    @Before
    public void before() {
        service = new BaseFunctionSelectionActionServiceImpl();
        functions = newArrayList(FUNCTION1);
        when(profile.getBaseFunctionList()).thenReturn(functions);
        when(profileDoc.getAdapter(Profile.class)).thenReturn(profile);
    }

    @Test
    public void addBaseFunctionsShouldAddIfFunctionIsNotContained() {
        service.addBaseFunction(profileDoc, FUNCTION2);
        verify(profile).setBaseFunctionList(functionsCaptor.capture());
        assertThat(functionsCaptor.getValue()).containsExactly(FUNCTION1, FUNCTION2);
    }

    @Test
    public void addBaseFunctionsShouldNotAddIfFunctionIsContained() {
        service.addBaseFunction(profileDoc, FUNCTION1);
        verify(profile, never()).setBaseFunctionList(functions);
    }

    @Test
    public void testRemoveBaseFunctionShouldRemoveIfFunctionIsContained() {
        service.removeBaseFunction(profileDoc, FUNCTION1);
        verify(profile).setBaseFunctionList(functionsCaptor.capture());
        assertThat(functionsCaptor.getValue()).isEmpty();
    }

    @Test
    public void addBaseFunctionsShouldNotRemoveIfFunctionIsNotContained() {
        service.removeBaseFunction(profileDoc, FUNCTION2);
        verify(profile, never()).setBaseFunctionList(functions);
    }
}
