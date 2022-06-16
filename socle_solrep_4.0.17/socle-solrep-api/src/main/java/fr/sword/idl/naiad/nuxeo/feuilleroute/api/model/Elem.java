package fr.sword.idl.naiad.nuxeo.feuilleroute.api.model;

public class Elem {
    private String docId;
    private String docType;

    private String title;

    public Elem() {
        // do nothing
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }
}
