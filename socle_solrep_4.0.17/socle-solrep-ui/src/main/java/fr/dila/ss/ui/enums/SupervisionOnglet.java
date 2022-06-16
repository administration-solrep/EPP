package fr.dila.ss.ui.enums;

public enum SupervisionOnglet {
    ACTIF("actif"),
    INACTIF("inactif");

    private final String id;

    SupervisionOnglet(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
