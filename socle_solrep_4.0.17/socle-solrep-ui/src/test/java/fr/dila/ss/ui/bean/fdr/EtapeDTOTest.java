package fr.dila.ss.ui.bean.fdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.nuxeo.ecm.platform.actions.Action;

public class EtapeDTOTest {

    @Test
    public void testConstructor() {
        EtapeDTO dto = new EtapeDTO();
        assertNull(dto.getId());
        assertNull(dto.getEtat());
        assertNull(dto.getParent());
        assertTrue(CollectionUtils.isEmpty(dto.getNotes()));
        assertNull(dto.getAction());
        assertNull(dto.getPoste());
        assertNull(dto.getMinistere());
        assertNull(dto.getMailBoxId());
        assertNull(dto.getEcheanceDate());
        assertNull(dto.getTraiteDate());
        assertEquals("non", dto.getObligatoire());
        assertTrue(dto.getIsStripped());
        assertNull(dto.getParent());
        assertTrue(CollectionUtils.isEmpty(dto.getActions()));
        assertTrue(CollectionUtils.isEmpty(dto.getParentsId()));
        assertNull(dto.getDepth());
    }

    @Test
    public void testSetter() {
        EtapeDTO dto = new EtapeDTO();
        assertNull(dto.getId());
        assertNull(dto.getEtat());
        assertNull(dto.getParent());
        assertTrue(CollectionUtils.isEmpty(dto.getNotes()));
        assertNull(dto.getAction());
        assertNull(dto.getPoste());
        assertNull(dto.getMinistere());
        assertNull(dto.getMailBoxId());
        assertNull(dto.getEcheanceDate());
        assertNull(dto.getTraiteDate());
        assertEquals("non", dto.getObligatoire());
        assertTrue(dto.getIsStripped());
        assertNull(dto.getParent());
        assertTrue(CollectionUtils.isEmpty(dto.getActions()));
        assertTrue(CollectionUtils.isEmpty(dto.getParentsId()));
        assertNull(dto.getDepth());

        dto.setId("monId");
        assertEquals("monId", dto.getId());

        dto.setEtat(new EtatEtapeDTO("monEtat", ""));
        assertNotNull(dto.getEtat());
        assertEquals("monEtat", dto.getEtat().getLabel());

        List<NoteEtapeDTO> list = new ArrayList<>();
        NoteEtapeDTO note = new NoteEtapeDTO();
        note.setAuteur("monAuteur");
        list.add(note);
        dto.setNotes(list);
        assertEquals(1, dto.getNotes().size());
        assertEquals("monAuteur", dto.getNotes().get(0).getAuteur());

        dto.setAction("action1");
        assertEquals("action1", dto.getAction());

        dto.setPoste("monPoste");
        assertEquals("monPoste", dto.getPoste());

        dto.setMinistere("monMinistere");
        assertEquals("monMinistere", dto.getMinistere());

        dto.setMailBoxId("mailBox");
        assertEquals("mailBox", dto.getMailBoxId());

        Calendar echeanceDate = GregorianCalendar.from(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        dto.setEcheanceDate(echeanceDate);
        assertEquals(echeanceDate, dto.getEcheanceDate());
        Calendar traiteDate = GregorianCalendar.from(ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        dto.setTraiteDate(traiteDate);
        assertEquals(traiteDate, dto.getTraiteDate());

        dto.setObligatoire(EtapeDTO.MINISTERE);
        assertEquals(EtapeDTO.MINISTERE, dto.getObligatoire());

        dto.setObligatoire(EtapeDTO.SGG);
        assertEquals(EtapeDTO.SGG, dto.getObligatoire());

        dto.setIsStripped(false);
        assertFalse(dto.getIsStripped());
        ContainerDTO parent = new ContainerDTO();

        parent.setId("parentId");
        dto.setParent(parent);
        assertNotNull(dto.getParent());
        assertEquals("parentId", dto.getParent().getId());
        List<Action> listAction = new ArrayList<>();

        Action action = new Action();
        action.setLabel("monAction");
        listAction.add(action);
        dto.setActions(listAction);
        assertEquals(1, dto.getActions().size());
        assertEquals("monAction", dto.getActions().get(0).getLabel());
        List<String> listIds = new ArrayList<>();

        listIds.add("parentId");
        dto.setParentsId(listIds);
        assertEquals(1, dto.getParentsId().size());
        assertEquals("parentId", dto.getParentsId().get(0));
        dto.setDepth(1);

        assertEquals(new Integer(1), dto.getDepth());
    }

    @Test
    public void testGetPostInMinistere() {
        EtapeDTO dto = new EtapeDTO();

        assertEquals("Sans ministère défini <br /> sans poste", dto.getPosteInMinistere());

        dto.setMinistere("mon Ministere");
        assertEquals("mon Ministere <br /> sans poste", dto.getPosteInMinistere());

        dto.setPoste("mon poste");
        assertEquals("mon Ministere <br /> mon poste", dto.getPosteInMinistere());

        dto.setMinistere(null);
        assertEquals("Sans ministère défini <br /> mon poste", dto.getPosteInMinistere());
    }
}
