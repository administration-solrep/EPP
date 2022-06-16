package fr.dila.st.ui.assertions;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.ui.bean.AlertContainer;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;

public class ResponseAssertions {

    public static void assertResponseWithoutMessages(Response response) {
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertJsonResponse((JsonResponse) response.getEntity(), null, null, null, null);
    }

    public static void assertResponseWithDangerMessages(Response response, List<String> dangerMessages) {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.ACCEPTED.getStatusCode());
        assertJsonResponse((JsonResponse) response.getEntity(), null, null, dangerMessages, null);
    }

    public static void assertResponseWithBadRequest(Response response) {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
        assertThat(response.getMetadata()).isEmpty();
        assertThat(response.getEntity()).isNull();
    }

    public static void assertJsonResponse(
        JsonResponse response,
        List<String> infoMessages,
        List<String> warningMessages,
        List<String> dangerMessages,
        List<String> successMessages
    ) {
        assertThat(response).isNotNull();
        assertThat(response.getStatut()).isEqualTo(SolonStatus.OK);
        assertMessageQueue(response.getMessages().getInfoMessageQueue(), infoMessages);
        assertMessageQueue(response.getMessages().getWarningMessageQueue(), warningMessages);
        assertMessageQueue(response.getMessages().getDangerMessageQueue(), dangerMessages);
        assertMessageQueue(response.getMessages().getSuccessMessageQueue(), successMessages);
    }

    @SuppressWarnings("unchecked")
    private static void assertMessageQueue(List<AlertContainer> messageQueue, List<String> expectedMessages) {
        if (CollectionUtils.isEmpty(expectedMessages)) {
            assertThat(messageQueue).isEmpty();
        } else {
            assertThat(messageQueue).extracting(AlertContainer::getAlertMessage).containsExactly(expectedMessages);
        }
    }
}
