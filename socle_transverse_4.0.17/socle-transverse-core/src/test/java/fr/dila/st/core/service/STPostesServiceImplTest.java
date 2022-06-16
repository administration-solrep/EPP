package fr.dila.st.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.organigramme.GouvernementNodeImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.organigramme.STPostesServiceImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class STPostesServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private OrganigrammeService mockOrgaService;

    @Mock
    private CoreSession session;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(STServiceLocator.class);
        Mockito.when(STServiceLocator.getOrganigrammeService()).thenReturn(mockOrgaService);
    }

    @Test
    public void testGetUsersHavingOnePosteOnly() {
        PosteNode mockPoste1 = Mockito.mock(PosteNode.class);

        STUser mockUser1 = Mockito.mock(STUser.class);
        Mockito.when(mockUser1.isActive()).thenReturn(true);
        STUser mockUser2 = Mockito.mock(STUser.class);
        Mockito.when(mockUser2.isActive()).thenReturn(true);
        STUser mockUser3 = Mockito.mock(STUser.class);
        Mockito.when(mockUser3.isActive()).thenReturn(true);

        List<STUser> posteUsers = Lists.newArrayList(mockUser1, mockUser2, mockUser3);

        Mockito.when(mockPoste1.getUserList()).thenReturn(posteUsers);
        Mockito.when(mockUser1.getPostes()).thenReturn(Lists.newArrayList("A", "B", "C"));
        Mockito.when(mockUser2.getPostes()).thenReturn(Lists.newArrayList("A"));
        Mockito.when(mockUser3.getPostes()).thenReturn(Lists.newArrayList("A"));

        assertEquals(3, mockPoste1.getUserList().size());

        STPostesServiceImpl postesService = new STPostesServiceImpl();
        Collection<STUser> users = postesService.getUsersHavingOnePosteOnly(mockPoste1);

        assertEquals(2, users.size());
        assertFalse(users.contains(mockUser1));
        assertTrue(users.contains(mockUser2));
        assertTrue(users.contains(mockUser3));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPosteHasBDC() {
        STPostesServiceImpl postesService = new STPostesServiceImpl();

        // Cas ok
        Mockito
            .when(
                mockOrgaService.getChildrenList(
                    Mockito.any(CoreSession.class),
                    Mockito.any(OrganigrammeNode.class),
                    Mockito.anyBoolean()
                )
            )
            .thenReturn(
                buildFakeEntites(),
                buildFakeOkPoste(),
                buildFakeUnites(),
                buildFakeOkPoste(),
                buildFakeKoPoste()
            );
        List<String> lstEntite = postesService.getEntiteWithoutBDCInGouvernement(buildFakeGouv());
        assertTrue(lstEntite.isEmpty());

        // Cas KO
        Mockito
            .when(
                mockOrgaService.getChildrenList(
                    Mockito.any(CoreSession.class),
                    Mockito.any(OrganigrammeNode.class),
                    Mockito.anyBoolean()
                )
            )
            .thenReturn(
                buildFakeEntites(),
                buildFakeKoPoste(),
                buildFakeUnites(),
                buildFakeKoPoste(),
                buildFakeKoPoste(),
                Lists.newArrayList()
            );
        lstEntite = postesService.getEntiteWithoutBDCInGouvernement(buildFakeGouv());
        assertFalse(lstEntite.isEmpty());
        assertEquals(Lists.newArrayList("Ministere 1", "Ministere 2"), lstEntite);
    }

    private GouvernementNode buildFakeGouv() {
        GouvernementNode gouv = new GouvernementNodeImpl();

        gouv.setId("test");
        gouv.setLabel("Gouvernement de test");

        return gouv;
    }

    private List<OrganigrammeNode> buildFakeEntites() {
        List<OrganigrammeNode> lstEntites = new ArrayList<>();
        EntiteNode entite1 = new EntiteNodeImpl();
        EntiteNode entite2 = new EntiteNodeImpl();

        entite1.setId("entite1");
        entite1.setLabel("Ministere 1");

        entite2.setId("entite2");
        entite2.setLabel("Ministere 2");

        lstEntites.add(entite1);
        lstEntites.add(entite2);

        return lstEntites;
    }

    private List<OrganigrammeNode> buildFakeUnites() {
        List<OrganigrammeNode> lstUnites = new ArrayList<>();
        UniteStructurelleNode unite1 = new UniteStructurelleNodeImpl();
        UniteStructurelleNode unite2 = new UniteStructurelleNodeImpl();

        unite1.setId("unite1");
        unite1.setLabel("Direction 1");

        unite2.setId("unite2");
        unite2.setLabel("Direction 2");

        lstUnites.add(unite1);
        lstUnites.add(unite2);

        return lstUnites;
    }

    private List<OrganigrammeNode> buildFakeOkPoste() {
        List<OrganigrammeNode> lstPostes = new ArrayList<>();
        PosteNode poste = new PosteNodeImpl();

        poste.setId("poste1");
        poste.setLabel("Agent BDC");
        poste.setPosteBdc(true);

        lstPostes.add(poste);

        return lstPostes;
    }

    private List<OrganigrammeNode> buildFakeKoPoste() {
        List<OrganigrammeNode> lstPostes = new ArrayList<>();
        PosteNode poste = new PosteNodeImpl();

        poste.setId("poste1");
        poste.setLabel("Agent BDC");

        lstPostes.add(poste);

        return lstPostes;
    }
}
