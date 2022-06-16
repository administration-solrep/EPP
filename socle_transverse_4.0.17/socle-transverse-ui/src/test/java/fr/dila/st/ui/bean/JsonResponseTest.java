package fr.dila.st.ui.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Test;

public class JsonResponseTest {

    @Test
    public void testConstructorNoArgs() {
        JsonResponse response = new JsonResponse();
        assertNull(response.getStatut());
        assertNull(response.getMessages());
        assertNull(response.getData());
    }

    @Test
    public void testConstructorTreeArgsContainer() {
        JsonMessagesContainer messages = new JsonMessagesContainer();
        JsonResponse response = new JsonResponse(SolonStatus.OK, messages, "donnees");
        assertEquals(SolonStatus.OK, response.getStatut());
        assertEquals(messages, response.getMessages());
        assertEquals("donnees", response.getData());
    }

    @Test
    public void testConstructorTreeArgsManager() {
        SolonAlertManager manager = new SolonAlertManager();
        manager.addErrorToQueue("errorMessage");
        JsonResponse response = new JsonResponse(SolonStatus.OK, manager, "donnees");
        assertEquals(SolonStatus.OK, response.getStatut());
        assertNotNull(response.getMessages());
        assertEquals(1, response.getMessages().getDangerMessageQueue().size());
        assertEquals("errorMessage", response.getMessages().getDangerMessageQueue().get(0).getAlertMessage().get(0));
        assertTrue(response.getMessages().getInfoMessageQueue().isEmpty());
        assertTrue(response.getMessages().getSuccessMessageQueue().isEmpty());
        assertTrue(response.getMessages().getWarningMessageQueue().isEmpty());
        assertEquals("donnees", response.getData());
    }

    @Test
    public void testConstructorTwoArgsManager() {
        SolonAlertManager manager = new SolonAlertManager();
        manager.addErrorToQueue("errorMessage");
        JsonResponse response = new JsonResponse(SolonStatus.OK, manager);
        assertEquals(SolonStatus.OK, response.getStatut());
        assertNotNull(response.getMessages());
        assertEquals(1, response.getMessages().getDangerMessageQueue().size());
        assertEquals("errorMessage", response.getMessages().getDangerMessageQueue().get(0).getAlertMessage().get(0));
        assertTrue(response.getMessages().getInfoMessageQueue().isEmpty());
        assertTrue(response.getMessages().getSuccessMessageQueue().isEmpty());
        assertTrue(response.getMessages().getWarningMessageQueue().isEmpty());
        assertNull(response.getData());
    }

    @Test
    public void testSetters() {
        JsonResponse response = new JsonResponse();
        assertNull(response.getStatut());
        assertNull(response.getMessages());
        assertNull(response.getData());

        response.setStatut(SolonStatus.FUNCTIONAL_ERROR);
        assertEquals(SolonStatus.FUNCTIONAL_ERROR, response.getStatut());

        JsonMessagesContainer messages = new JsonMessagesContainer();
        response.setMessages(messages);
        assertEquals(messages, response.getMessages());

        response.setData("data");
        assertEquals("data", response.getData());
    }

    @Test
    public void testBuildShouldReturnResponseOk() {
        /* Cas sans message */
        SolonAlertManager manager = new SolonAlertManager();

        JsonResponse jsonResponse = new JsonResponse(SolonStatus.OK, manager);

        Response response = jsonResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        assertThat((JsonResponse) response.getEntity()).isEqualTo(jsonResponse);

        /* Cas message d'info seul */
        manager.addInfoToQueue("infoMessage");

        jsonResponse = new JsonResponse(SolonStatus.OK, manager);

        response = jsonResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        assertThat((JsonResponse) response.getEntity()).isEqualTo(jsonResponse);

        /* Cas message de succès seul */
        manager.addSuccessToQueue("successMessage");

        jsonResponse = new JsonResponse(SolonStatus.OK, manager);

        response = jsonResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        assertThat((JsonResponse) response.getEntity()).isEqualTo(jsonResponse);

        /* Cas message d'info et succès */
        manager.addInfoToQueue("infoMessage");
        manager.addSuccessToQueue("successMessage");

        jsonResponse = new JsonResponse(SolonStatus.OK, manager);

        response = jsonResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
        assertThat((JsonResponse) response.getEntity()).isEqualTo(jsonResponse);
    }

    @Test
    public void testBuildShouldReturnResponseAccepted() {
        /* Cas message de warning seul */
        SolonAlertManager manager = new SolonAlertManager();
        manager.addWarnToQueue("warningMessage");

        JsonResponse jsonResponse = new JsonResponse(SolonStatus.OK, manager);

        Response response = jsonResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.ACCEPTED.getStatusCode());
        assertThat((JsonResponse) response.getEntity()).isEqualTo(jsonResponse);

        /* Cas message d'erreur seul */
        manager.addErrorToQueue("errorMessage");

        jsonResponse = new JsonResponse(SolonStatus.OK, manager);

        response = jsonResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.ACCEPTED.getStatusCode());
        assertThat((JsonResponse) response.getEntity()).isEqualTo(jsonResponse);

        /* Cas message de warning et d'erreur */
        manager.addWarnToQueue("warningMessage");
        manager.addErrorToQueue("errorMessage");

        jsonResponse = new JsonResponse(SolonStatus.OK, manager);

        response = jsonResponse.build();

        assertThat(response.getStatus()).isEqualTo(Status.ACCEPTED.getStatusCode());
        assertThat((JsonResponse) response.getEntity()).isEqualTo(jsonResponse);
    }
}
