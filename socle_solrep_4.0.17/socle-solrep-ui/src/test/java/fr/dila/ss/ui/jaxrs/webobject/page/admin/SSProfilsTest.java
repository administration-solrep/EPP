package fr.dila.ss.ui.jaxrs.webobject.page.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.ProfilUIService;
import fr.dila.ss.ui.services.SSTemplateUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.PageProfilDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ WebEngine.class, SSUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class SSProfilsTest {
    private static final String COLONNE_LABEL = "colonne label";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSProfils page;

    @Mock
    private SpecificContext context;

    @Mock
    private ProfilUIService profilsUIService;

    @Mock
    private SSTemplateUIService templateUiService;

    @Before
    public void before() {
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getProfilUIService()).thenReturn(profilsUIService);
        when(SSUIServiceLocator.getSSTemplateUIService()).thenReturn(templateUiService);
        when(templateUiService.getLeftMenuTemplate(any())).thenReturn(new ThTemplate());

        page = new SSProfils();

        Whitebox.setInternalState(page, "context", context);
    }

    @Test
    public void testProfils() {
        List<String> profilsDTO = ImmutableList.of("Administrateur", "Contributeur ministériel", "Superviseur SGG");
        List<ColonneInfo> colonnes = ImmutableList.of(new ColonneInfo(COLONNE_LABEL, true, true, false, true));

        PageProfilDTO ppd = new PageProfilDTO();
        ppd.setProfils(profilsDTO);
        ppd.setLstColonnes(colonnes);

        Action createAction = new Action();

        when(profilsUIService.getPageProfilDTO(context)).thenReturn(ppd);
        when(context.getAction(SSActionEnum.ADMIN_USER_PROFILS)).thenReturn(new Action());
        when(context.getAction(SSActionEnum.ADMIN_PROFIL_CREATE)).thenReturn(createAction);

        SortOrder order = SortOrder.ASC;

        ThTemplate template = page.getProfils(order.getValue());

        assertThat(template).isNotNull();
        assertThat(template.getName()).isEqualTo("pages/admin/profile/liste");
        assertThat(template.getContext()).isEqualTo(context);

        assertThat(template.getData())
            .isNotNull()
            .containsOnly(
                entry(SSTemplateConstants.PROFILS, profilsDTO),
                entry(STTemplateConstants.LST_COLONNES, colonnes),
                entry(STTemplateConstants.DATA_URL, SSProfils.DATA_URL),
                entry(STTemplateConstants.DATA_AJAX_URL, SSProfils.DATA_AJAX_URL),
                entry(STTemplateConstants.CREATE_ACTION, createAction)
            );

        verify(context).getAction(SSActionEnum.ADMIN_USER_PROFILS);
        verify(context).getAction(SSActionEnum.ADMIN_PROFIL_CREATE);
        verify(context).putInContextData(STContextDataKey.SORT_ORDER, order);
        verify(context)
            .setNavigationContextTitle(
                new Breadcrumb(SSProfils.NAVIGATION_TITLE, SSProfils.DATA_URL, Breadcrumb.TITLE_ORDER)
            );
        verify(profilsUIService).computeListProfilActions(context);
        verify(profilsUIService).getPageProfilDTO(context);
        verify(templateUiService).getLeftMenuTemplate(null);
        verifyNoMoreInteractions(context, profilsUIService, templateUiService);
    }
}
