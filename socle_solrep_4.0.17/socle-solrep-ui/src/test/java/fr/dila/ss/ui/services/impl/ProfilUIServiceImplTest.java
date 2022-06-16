package fr.dila.ss.ui.services.impl;

import static fr.dila.st.ui.enums.STContextDataKey.PROFILE_ID;
import static fr.dila.st.ui.enums.STContextDataKey.SORT_ORDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import fr.dila.ss.ui.services.ProfilUIService;
import fr.dila.st.api.service.FonctionService;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.user.BaseFunction;
import fr.dila.st.api.user.Profile;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.FicheProfilDTO;
import fr.dila.st.ui.bean.IColonneInfo;
import fr.dila.st.ui.bean.PageProfilDTO;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STActionsServiceLocator.class, STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class ProfilUIServiceImplTest {
    private static final String GROUP_NAME = "groupName";
    private static final SortOrder ORDRE_TRI = SortOrder.ASC;
    private static final String FONCTION_1 = "fonction_1";
    private static final String FONCTION_2 = "fonction_2";
    private static final String FONCTION_DESC = "fonctionDescription";
    private static final String PROFIL_SORT_ID = "ordreProfilHeader";
    private static final String FONCTION_SORT_ID = "fonctionsAttribueesHeader";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Spy
    private ProfilUIService service = new ProfilUIServiceImpl();

    @Mock
    private FonctionService fonctionService;

    @Mock
    private SpecificContext context;

    @Mock
    private DocumentModel doc;

    @Mock
    private Profile profile;

    @Mock
    private BaseFunction baseFunction;

    @Mock
    private CoreSession coreSession;

    @Mock
    private UserManager userManager;

    @Mock
    private NuxeoPrincipal nuxeoPrincipal;

    @Mock
    private ProfileService profileService;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STActionsServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        when(STServiceLocator.getFonctionService()).thenReturn(fonctionService);
        when(STServiceLocator.getUserManager()).thenReturn(userManager);
        when(STServiceLocator.getProfileService()).thenReturn(profileService);
        when(context.getFromContextData(SORT_ORDER)).thenReturn(ORDRE_TRI);
    }

    @Test
    public void testGetPageProfileDTO() {
        List<DocumentModel> docList = new ArrayList<>();
        docList.add(doc);
        DocumentModelList modelList = new DocumentModelListImpl(docList);

        doReturn(modelList).when(service).getProfiles(context, "*");
        when(doc.getAdapter(Profile.class)).thenReturn(profile);
        when(profile.getName()).thenReturn(GROUP_NAME);

        PageProfilDTO pageProfileDTO = service.getPageProfilDTO(context);

        assertNotNull(pageProfileDTO);
        assertNotNull(pageProfileDTO.getProfils());
        assertEquals(1, pageProfileDTO.getProfils().size());
        assertEquals(GROUP_NAME, pageProfileDTO.getProfils().get(0));
        assertEquals(1, pageProfileDTO.getLstColonnes().size());
        IColonneInfo colonne = pageProfileDTO.getLstColonnes().get(0);
        assertEquals(ProfilUIServiceImpl.PROFIL_SORT_NAME, colonne.getSortName());
        assertEquals(PROFIL_SORT_ID, colonne.getSortId());
        assertEquals(ORDRE_TRI.getValue(), colonne.getSortValue());
        assertEquals(ProfilUIServiceImpl.COLONNE_PROFIL_TITLE, colonne.getLabel());
    }

    @Test
    public void testGetFicheProfilDTO() {
        List<String> baseFunctionList = Arrays.asList(FONCTION_1, FONCTION_2);

        when(context.getFromContextData(PROFILE_ID)).thenReturn(GROUP_NAME);
        doReturn(doc).when(service).getProfilDoc(context);
        when(context.getCurrentDocument()).thenReturn(doc);
        when(doc.getAdapter(Profile.class)).thenReturn(profile);
        when(profile.getName()).thenReturn(GROUP_NAME);
        when(profile.getBaseFunctionList()).thenReturn(baseFunctionList);
        when(baseFunction.getDescription()).thenReturn(FONCTION_DESC);
        when(fonctionService.getFonction(FONCTION_1)).thenReturn(baseFunction);
        when(fonctionService.getFonction(FONCTION_2)).thenReturn(null);
        when(context.getSession()).thenReturn(coreSession);
        when(context.computeFromContextDataIfAbsent(any(), Mockito.<Supplier<?>>any()))
            .thenAnswer(invocation -> invocation.getArgumentAt(1, Supplier.class).get());
        when(userManager.areGroupsReadOnly()).thenReturn(false);
        when(coreSession.getPrincipal()).thenReturn(nuxeoPrincipal);
        when(nuxeoPrincipal.isAdministrator()).thenReturn(true);
        when(profileService.isProfileUpdatable(anyString())).thenReturn(true);

        FicheProfilDTO ficheProfilDTO = service.getFicheProfilDTO(context);

        assertNotNull(ficheProfilDTO);
        assertEquals(GROUP_NAME, ficheProfilDTO.getId());
        assertEquals(GROUP_NAME, ficheProfilDTO.getLabel());
        assertEquals(1, ficheProfilDTO.getLstColonnes().size());
        IColonneInfo colonne = ficheProfilDTO.getLstColonnes().get(0);
        assertEquals(ProfilUIServiceImpl.FONCTION_SORT_NAME, colonne.getSortName());
        assertEquals(FONCTION_SORT_ID, colonne.getSortId());
        assertEquals(ORDRE_TRI.getValue(), colonne.getSortValue());
        assertEquals(ProfilUIServiceImpl.PROFIL_FICHE_HEADER, colonne.getLabel());
        assertNotNull(ficheProfilDTO.getFonctions());
        assertEquals(2, ficheProfilDTO.getFonctions().size());
        SelectValueDTO fonction1 = ficheProfilDTO.getFonctions().get(0);
        SelectValueDTO fonction2 = ficheProfilDTO.getFonctions().get(1);
        assertEquals(FONCTION_1, fonction1.getId());
        assertEquals(FONCTION_DESC, fonction1.getLabel());
        assertEquals(FONCTION_2, fonction2.getId());
        assertEquals(FONCTION_2, fonction2.getLabel());
    }
}
