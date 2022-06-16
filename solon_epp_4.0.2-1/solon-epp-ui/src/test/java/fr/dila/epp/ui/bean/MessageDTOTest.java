package fr.dila.epp.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageDTOTest {

    @Test
    public void testConstructor() {
        EppMessageDTO dto = new EppMessageDTO();
        assertNull(dto.getId());
        assertNull(dto.getIdDossier());
        assertNull(dto.getObjetDossier());
        assertNull(dto.getLecture());
        assertNull(dto.getEmetteur());
        assertNull(dto.getDestinataire());
        assertNull(dto.getCopie());
        assertNull(dto.getCommunication());
        assertNull(dto.getVersion());
        assertNull(dto.getDate());

        dto =
            new EppMessageDTO(
                "1",
                "objet",
                "",
                "emetteur",
                "destinataire",
                "",
                "la communication",
                "1.0",
                "25/07/2020"
            );
        assertEquals("1", dto.getIdDossier());
        assertEquals("objet", dto.getObjetDossier());
        assertEquals("", dto.getLecture());
        assertEquals("emetteur", dto.getEmetteur());
        assertEquals("destinataire", dto.getDestinataire());
        assertEquals("", dto.getCopie());
        assertEquals("la communication", dto.getCommunication());
        assertEquals("1.0", dto.getVersion());
        assertEquals("25/07/2020", dto.getDate());
    }

    @Test
    public void testSetters() {
        EppMessageDTO dto = new EppMessageDTO();
        assertNull(dto.getId());
        assertNull(dto.getIdDossier());
        assertNull(dto.getObjetDossier());
        assertNull(dto.getLecture());
        assertNull(dto.getEmetteur());
        assertNull(dto.getDestinataire());
        assertNull(dto.getCopie());
        assertNull(dto.getCommunication());
        assertNull(dto.getVersion());
        assertNull(dto.getDate());

        dto.setId("id");
        assertEquals("id", dto.getId());

        dto.setIdDossier("iddossier");
        assertEquals("iddossier", dto.getIdDossier());

        dto.setObjetDossier("obj");
        assertEquals("obj", dto.getObjetDossier());

        dto.setLecture("lecture");
        assertEquals("lecture", dto.getLecture());

        dto.setEmetteur("auteur");
        assertEquals("auteur", dto.getEmetteur());

        dto.setDestinataire("dest");
        assertEquals("dest", dto.getDestinataire());

        dto.setCopie("copie");
        assertEquals("copie", dto.getCopie());

        dto.setCommunication("communication");
        assertEquals("communication", dto.getCommunication());

        dto.setVersion("version");
        assertEquals("version", dto.getVersion());

        dto.setDate("date");
        assertEquals("date", dto.getDate());

        dto.setEtatMessage("publié");
        assertEquals("publié", dto.getEtatMessage());

        dto.setEtatEvenement("annulé");
        assertEquals("annulé", dto.getEtatEvenement());

        dto.setPieceJointe(true);
        assertTrue(dto.isPieceJointe());

        dto.setEnAlerte(true);
        assertTrue(dto.isEnAlerte());

        dto.setDossierEnAlerte(true);
        assertTrue(dto.isDossierEnAlerte());

        dto.setLocker("locker");
        assertEquals("locker", dto.getLocker());

        dto.setLockTime("lockTime");
        assertEquals("lockTime", dto.getLockTime());

        dto.setModeCreationVersion("modeCreation");
        assertEquals("modeCreation", dto.getModeCreationVersion());
    }
}
