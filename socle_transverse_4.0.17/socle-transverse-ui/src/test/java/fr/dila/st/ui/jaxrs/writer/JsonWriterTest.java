package fr.dila.st.ui.jaxrs.writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import fr.dila.st.ui.bean.JsonMessagesContainer;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

public class JsonWriterTest {

    @Test
    public void testGetSize() {
        JsonWriter writer = new JsonWriter();
        assertEquals(-1, writer.getSize(null, null, null, null, null));
    }

    @Test
    public void testIsWriteable() {
        JsonWriter writer = new JsonWriter();

        //Test bonne classe / mauvais media
        assertFalse(writer.isWriteable(JsonResponse.class, null, null, MediaType.APPLICATION_XML_TYPE));

        //Test mauvaise classe / bon media
        assertFalse(writer.isWriteable(String.class, null, null, MediaType.APPLICATION_JSON_TYPE));

        //Test OK
        assertTrue(writer.isWriteable(JsonResponse.class, null, null, MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void testWriteTo() throws IOException {
        JsonWriter writer = new JsonWriter();
        OutputStream stream = new ByteArrayOutputStream();
        JsonResponse reponse = new JsonResponse();
        writer.writeTo(reponse, null, null, null, null, null, stream);

        assertEquals("{\"statut\":null,\"messages\":null,\"data\":null}", stream.toString());

        reponse.setStatut(SolonStatus.OK);
        reponse.setMessages(new JsonMessagesContainer());
        reponse.setData("hello");

        stream = new ByteArrayOutputStream();
        writer.writeTo(reponse, null, null, null, null, null, stream);
        assertEquals(
            "{\"statut\":\"OK\",\"messages\":{\"infoMessageQueue\":[],\"warningMessageQueue\":[],\"dangerMessageQueue\":[],\"successMessageQueue\":[]},\"data\":\"hello\"}",
            stream.toString()
        );
    }
}
