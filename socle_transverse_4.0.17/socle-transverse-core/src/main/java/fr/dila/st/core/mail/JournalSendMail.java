package fr.dila.st.core.mail;

import fr.dila.st.api.constant.STEventConstant;
import java.io.Serializable;

public class JournalSendMail implements Serializable {
    private static final long serialVersionUID = 4570524079120019953L;

    private String eventName = STEventConstant.EVENT_GENERAL_ENVOI_MAIL;
    private String docId;
    private String username;
    private String category = STEventConstant.CATEGORY_ADMINISTRATION;

    public JournalSendMail() {}

    public JournalSendMail(String category) {
        this();
        this.category = category;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
