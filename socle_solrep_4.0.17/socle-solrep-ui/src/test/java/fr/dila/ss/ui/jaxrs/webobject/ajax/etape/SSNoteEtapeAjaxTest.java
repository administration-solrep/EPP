package fr.dila.ss.ui.jaxrs.webobject.ajax.etape;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.ss.ui.bean.fdr.NoteEtapeFormDTO;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.comment.SSCommentManagerUIService;
import fr.dila.st.ui.bean.AlertContainer;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(SSUIServiceLocator.class)
@PowerMockIgnore("javax.management.*")
public class SSNoteEtapeAjaxTest {
    private static final String ROUTE_STEP_ID = "a6715757-ede1-4740-8f55-641f957cb651";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSNoteEtapeAjax controlleur;

    @Mock
    private SpecificContext context;

    @Mock
    private SSCommentManagerUIService ssCommentManagerUIService;

    private NoteEtapeFormDTO noteEtapeFormDTO;

    @Before
    public void before() {
        controlleur = new SSNoteEtapeAjax();

        noteEtapeFormDTO = new NoteEtapeFormDTO();
        noteEtapeFormDTO.setStepId(ROUTE_STEP_ID);

        Whitebox.setInternalState(controlleur, "context", context);

        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getSSCommentManagerUIService()).thenReturn(ssCommentManagerUIService);

        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
    }

    @Test
    public void testCreate() {
        Response response = controlleur.create(noteEtapeFormDTO);

        assertThat(response).isNotNull();

        verify(context).setCurrentDocument(ROUTE_STEP_ID);
        verify(context).putInContextData(SSContextDataKey.NOTE_ETAPE_FORM, noteEtapeFormDTO);
        verify(ssCommentManagerUIService).addComment(context);
        assertThat(context.getMessageQueue().getSuccessQueue())
            .flatExtracting(AlertContainer::getAlertMessage)
            .containsExactlyInAnyOrder("Note d'étape ajoutée");
    }

    @Test
    public void testEdit() {
        Response response = controlleur.edit(noteEtapeFormDTO);

        assertThat(response).isNotNull();

        verify(context).setCurrentDocument(ROUTE_STEP_ID);
        verify(context).putInContextData(SSContextDataKey.NOTE_ETAPE_FORM, noteEtapeFormDTO);
        verify(ssCommentManagerUIService).updateComment(context);
        assertThat(context.getMessageQueue().getSuccessQueue())
            .flatExtracting(AlertContainer::getAlertMessage)
            .containsExactlyInAnyOrder("Note d'étape modifiée");
    }

    @Test
    public void testRemove() {
        String commentId = "comment-id";
        noteEtapeFormDTO.setCommentId(commentId);

        Response response = controlleur.remove(noteEtapeFormDTO);

        assertThat(response).isNotNull();

        verify(context).setCurrentDocument(ROUTE_STEP_ID);
        verify(context).putInContextData(SSContextDataKey.COMMENT_ID, commentId);
        verify(ssCommentManagerUIService).deleteComment(context);
        assertThat(context.getMessageQueue().getSuccessQueue())
            .flatExtracting(AlertContainer::getAlertMessage)
            .containsExactlyInAnyOrder("Note d'étape supprimée");
    }
}
