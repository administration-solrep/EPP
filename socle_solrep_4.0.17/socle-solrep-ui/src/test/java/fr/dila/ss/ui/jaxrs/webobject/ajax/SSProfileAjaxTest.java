package fr.dila.ss.ui.jaxrs.webobject.ajax;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.ss.ui.jaxrs.webobject.page.admin.SSProfils;
import fr.dila.ss.ui.services.ProfilUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.PageProfilDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
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
import org.nuxeo.ecm.webengine.WebEngine;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ WebEngine.class, SSUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class SSProfileAjaxTest {
    private static final String COLONNE_LABEL = "colonne label";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSProfileAjax page;

    @Mock
    private SpecificContext context;

    @Mock
    private ProfilUIService profilsUIService;

    @Before
    public void before() throws Exception {
        page = new SSProfileAjax();

        Whitebox.setInternalState(page, "context", context);

        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getProfilUIService()).thenReturn(profilsUIService);
    }

    @Test
    public void testProfils() {
        List<String> profilsDTO = ImmutableList.of("Administrateur", "Contributeur ministériel", "Superviseur SGG");
        List<ColonneInfo> colonnes = ImmutableList.of(new ColonneInfo(COLONNE_LABEL, true, true, false, true));

        PageProfilDTO ppd = new PageProfilDTO();
        ppd.setProfils(profilsDTO);
        ppd.setLstColonnes(colonnes);

        when(profilsUIService.getPageProfilDTO(context)).thenReturn(ppd);

        SortOrder order = SortOrder.ASC;

        ThTemplate template = page.getListeProfile(order.getValue());

        assertThat(template).isNotNull().isExactlyInstanceOf(AjaxLayoutThTemplate.class);
        assertThat(template.getName()).isEqualTo("fragments/components/profileListe");
        assertThat(template.getLayout()).isEqualTo("ajaxLayout");
        assertThat(template.getContext()).isEqualTo(context);

        assertThat(template.getData())
            .isNotNull()
            .containsOnly(
                entry(SSTemplateConstants.PROFILS, profilsDTO),
                entry(STTemplateConstants.LST_COLONNES, colonnes),
                entry(STTemplateConstants.DATA_URL, SSProfils.DATA_URL),
                entry(STTemplateConstants.DATA_AJAX_URL, SSProfils.DATA_AJAX_URL)
            );

        verify(context).putInContextData(STContextDataKey.SORT_ORDER, order);
        verify(context).getCopyDataToResponse();
        verify(profilsUIService).getPageProfilDTO(context);
        verifyNoMoreInteractions(context, profilsUIService);
    }
}
