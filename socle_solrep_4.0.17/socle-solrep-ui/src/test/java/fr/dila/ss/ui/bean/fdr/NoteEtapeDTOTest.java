package fr.dila.ss.ui.bean.fdr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.nuxeo.ecm.platform.actions.Action;

public class NoteEtapeDTOTest {

    @Test
    public void testConstructor() {
        NoteEtapeDTO dto = new NoteEtapeDTO();

        assertNull(dto.getAuteur());
        assertNull(dto.getDate());
        assertNull(dto.getContent());
        assertNotNull(dto.getActions());
        assertThat(dto.getActions()).isEmpty();
        assertNotNull(dto.getReponses());
        assertThat(dto.getReponses()).isEmpty();
    }

    @Test
    public void testSetter() {
        NoteEtapeDTO dto = new NoteEtapeDTO();

        assertNull(dto.getAuteur());
        assertNull(dto.getDate());
        assertNull(dto.getContent());
        assertNotNull(dto.getActions());
        assertThat(dto.getActions()).isEmpty();
        assertNotNull(dto.getReponses());
        assertThat(dto.getReponses()).isEmpty();

        dto.setAuteur("auteur");
        assertEquals("auteur", dto.getAuteur());

        Date date = new Date();
        dto.setDate(date);
        assertEquals(date, dto.getDate());

        dto.setContent("contenu de ma note");
        assertEquals("contenu de ma note", dto.getContent());

        List<Action> list = new ArrayList<>();
        Action action = new Action();
        action.setLabel("monAction");
        list.add(action);
        dto.setActions(list);
        assertEquals(1, dto.getActions().size());
        assertEquals("monAction", dto.getActions().get(0).getLabel());

        List<NoteEtapeDTO> listReponses = new ArrayList<>();
        NoteEtapeDTO reponse = new NoteEtapeDTO();
        reponse.setAuteur("auteur reponse");
        listReponses.add(reponse);
        dto.setReponses(listReponses);
        assertEquals(1, dto.getReponses().size());
        assertEquals("auteur reponse", dto.getReponses().get(0).getAuteur());
    }
}
