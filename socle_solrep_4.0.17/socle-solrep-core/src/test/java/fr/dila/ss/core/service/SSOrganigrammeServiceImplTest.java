package fr.dila.ss.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ QueryUtils.class, SSServiceLocator.class })
@Category(fr.dila.ss.core.PowerMockitoTests.class)
public class SSOrganigrammeServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private FeuilleRouteModelService mockFeuilleRouteModelService;

    @Mock
    private CoreSession session;

    @Mock
    private SSAbstractOrganigrammeService ssOrganigrammeService;

    @Mock
    private CoreSession mockSession;

    @Mock
    private UniteStructurelleNode mockUniteStructurelle;

    @Mock
    private UniteStructurelleNode mockDirection;

    private List<DocumentModel> fdr;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(SSServiceLocator.class);

        DocumentModel docModel1 = Mockito.mock(DocumentModel.class);
        DocumentModel docModel2 = Mockito.mock(DocumentModel.class);

        fdr = Lists.newArrayList(docModel1, docModel2);

        Mockito.when(SSServiceLocator.getFeuilleRouteModelService()).thenReturn(mockFeuilleRouteModelService);
        Mockito
            .when(
                mockFeuilleRouteModelService.getFdrModelFromMinistereAndDirection(
                    Mockito.any(),
                    Mockito.eq(null),
                    Mockito.anyString(),
                    Mockito.eq(true)
                )
            )
            .thenReturn(fdr);

        when(ssOrganigrammeService.validateDeleteDirectionFeuillesRoute(mockDirection, mockSession))
            .thenCallRealMethod();

        when(mockUniteStructurelle.getType()).thenReturn(OrganigrammeType.UNITE_STRUCTURELLE);
        when(mockDirection.getType()).thenReturn(OrganigrammeType.DIRECTION);
    }

    @Test
    public void testValidateDeleteUniteStructurelleNode_notDirection() {
        Collection<OrganigrammeNodeDeletionProblem> problems = ssOrganigrammeService.validateDeleteUniteStructurelleNode(
            mockSession,
            mockUniteStructurelle
        );
        assertNotNull(problems);
        assertTrue(problems.isEmpty());
    }

    @Test
    public void testValidateDeleteDirectionFeuillesRoute() {
        Collection<OrganigrammeNodeDeletionProblem> problems = ssOrganigrammeService.validateDeleteDirectionFeuillesRoute(
            mockDirection,
            mockSession
        );
        assertFalse(problems.isEmpty());
        assertEquals(2, problems.size());
    }
}
