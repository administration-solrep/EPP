package fr.dila.st.ui.th.bean;

public class EntiteNorDirection {
    private String id;

    private String nor;

    public EntiteNorDirection() {}

    public EntiteNorDirection(String idMin, String nor) {
        this.id = idMin;
        this.nor = nor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNor() {
        return nor;
    }

    public void setNor(String nor) {
        this.nor = nor;
    }
}
