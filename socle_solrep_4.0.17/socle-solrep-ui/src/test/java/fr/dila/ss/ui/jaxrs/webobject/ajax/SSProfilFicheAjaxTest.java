package fr.dila.ss.ui.jaxrs.webobject.ajax;

import static fr.dila.st.ui.enums.STContextDataKey.PROFILE_ID;
import static fr.dila.st.ui.enums.STContextDataKey.SORT_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.ProfilUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.FicheProfilDTO;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SSProfilFicheAjax.class, SpecificContext.class, SSUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class SSProfilFicheAjaxTest {
    private static final String URL_PREVIOUS_PAGE = "liste#main_content";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSProfilFicheAjax page;

    @Mock
    private SpecificContext context;

    @Mock
    private HttpServletRequest request;

    @Mock
    private WebContext webContext;

    @Mock
    private ProfilUIService profilUIService;

    @Mock
    private DocumentModel profilDoc;

    @Before
    public void before() throws Exception {
        page = new SSProfilFicheAjax();

        Whitebox.setInternalState(page, "context", context);

        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getProfilUIService()).thenReturn(profilUIService);

        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getRequest()).thenReturn(request);
        when(context.getUrlPreviousPage()).thenReturn(URL_PREVIOUS_PAGE);
    }

    @Test
    public void testFicheProfil() {
        String profilId = "Documentaliste";

        String fonctionIndexationCompId = "IndexationComplementaireAdminMinistereUpdater";
        String fonctionDossiersConnexesId = "DossiersConnexesReader";

        List<SelectValueDTO> fonctions = ImmutableList.of(
            new SelectValueDTO(fonctionIndexationCompId, "Accès à l'indexation complémentaire"),
            new SelectValueDTO(fonctionDossiersConnexesId, "Accès à l'onglet Dossiers Connexes")
        );

        List<String> fonctionsOfProfil = ImmutableList.of(fonctionIndexationCompId, fonctionDossiersConnexesId);

        SortOrder order = SortOrder.ASC;
        List<ColonneInfo> colonnes = ImmutableList.of(
            new ColonneInfo("profil.fiche.header", true, "fonctionsAttribuees", order)
        );

        FicheProfilDTO fiche = new FicheProfilDTO();
        fiche.setFonctions(fonctions);
        fiche.setLstColonnes(colonnes);

        when(profilUIService.getProfilDoc(context)).thenReturn(profilDoc);
        when(profilUIService.getFicheProfilDTO(context)).thenReturn(fiche);

        Action editAction = new Action();
        when(context.getAction(SSActionEnum.ADMIN_PROFIL_EDIT)).thenReturn(editAction);

        Action deleteAction = new Action();
        when(context.getAction(SSActionEnum.ADMIN_PROFIL_DELETE)).thenReturn(deleteAction);

        ThTemplate template = page.getFicheProfil(profilId, order.getValue());

        assertThat(template).isNotNull().isExactlyInstanceOf(AjaxLayoutThTemplate.class);
        assertThat(template.getName()).isEqualTo("fragments/components/profil/fonctionsAttribueesList");
        assertThat(template.getContext()).isEqualTo(context);

        assertThat(template.getData())
            .isNotNull()
            .containsOnly(
                entry(SSTemplateConstants.PROFIL, fiche),
                entry(SSTemplateConstants.FONCTIONS, fonctions),
                entry(SSTemplateConstants.FONCTIONS_OF_PROFIL, fonctionsOfProfil),
                entry(STTemplateConstants.LST_COLONNES, colonnes),
                entry(STTemplateConstants.DATA_URL, SSProfilFicheAjax.FICHE_PROFIL_URL),
                entry(STTemplateConstants.DATA_AJAX_URL, SSProfilFicheAjax.FICHE_PROFIL_AJAX_URL),
                entry(STTemplateConstants.URL_PREVIOUS_PAGE, URL_PREVIOUS_PAGE),
                entry(STTemplateConstants.EDIT_ACTION, editAction),
                entry(STTemplateConstants.DELETE_ACTION, deleteAction)
            );

        verify(context).getWebcontext();
        verify(context).getUrlPreviousPage();
        verify(context)
            .setNavigationContextTitle(
                new Breadcrumb(
                    SSProfilFicheAjax.FICHE_PROFIL_LABEL,
                    SSProfilFicheAjax.FICHE_PROFIL_URL,
                    SSProfilFicheAjax.PROFILS_ORDER + 1,
                    request
                )
            );
        verify(context).putInContextData(PROFILE_ID, profilId);
        verify(context).putInContextData(SORT_ORDER, order);
        verify(context).setCurrentDocument(profilDoc);
        verify(context).getAction(SSActionEnum.ADMIN_PROFIL_DELETE);
        verify(context).getAction(SSActionEnum.ADMIN_PROFIL_EDIT);
        verify(profilUIService).getProfilDoc(context);
        verify(profilUIService).getFicheProfilDTO(context);
        verifyNoMoreInteractions(context, profilUIService, profilDoc);
    }
}
