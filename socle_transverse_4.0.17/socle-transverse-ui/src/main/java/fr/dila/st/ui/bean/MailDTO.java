package fr.dila.st.ui.bean;

import java.util.List;

public class MailDTO {
    private String objet;

    private String message;

    private List<String> files;

    public MailDTO(String objet, String message, List<String> files) {
        super();
        this.objet = objet;
        this.message = message;
        this.files = files;
    }

    public String getObjet() {
        return objet;
    }

    public void setObjet(String objet) {
        this.objet = objet;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
