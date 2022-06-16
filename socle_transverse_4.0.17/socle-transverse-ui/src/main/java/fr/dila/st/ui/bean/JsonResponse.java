package fr.dila.st.ui.bean;

import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import java.beans.Transient;
import java.io.Serializable;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class JsonResponse implements Serializable {
    private static final long serialVersionUID = 4427883204519731514L;

    private SolonStatus statut;
    private JsonMessagesContainer messages;
    private Serializable data;

    public JsonResponse() {}

    public JsonResponse(SolonStatus statut, JsonMessagesContainer messages, Serializable data) {
        this.statut = statut;
        this.messages = messages;
        this.data = data;
    }

    public JsonResponse(SolonStatus statut, SolonAlertManager manager, Serializable data) {
        this(
            statut,
            new JsonMessagesContainer(
                manager.getInfoQueue(),
                manager.getWarnQueue(),
                manager.getErrorQueue(),
                manager.getSuccessQueue()
            ),
            data
        );
    }

    public JsonResponse(SolonStatus statut, SolonAlertManager manager) {
        this(statut, manager, null);
    }

    public SolonStatus getStatut() {
        return statut;
    }

    public void setStatut(SolonStatus statut) {
        this.statut = statut;
    }

    public JsonMessagesContainer getMessages() {
        return messages;
    }

    public void setMessages(JsonMessagesContainer messages) {
        this.messages = messages;
    }

    public Object getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }

    @Transient
    public Response build() {
        Status status = ACCEPTED;

        if (isStatusOk(getMessages())) {
            status = OK;
        }

        return Response.status(status).entity(this).build();
    }

    private static boolean isStatusOk(JsonMessagesContainer messages) {
        return isEmpty(messages.getDangerMessageQueue()) && isEmpty(messages.getWarningMessageQueue());
    }
}
