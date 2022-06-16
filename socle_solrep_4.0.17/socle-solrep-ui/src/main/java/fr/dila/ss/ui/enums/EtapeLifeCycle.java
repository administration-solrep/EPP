package fr.dila.ss.ui.enums;

public enum EtapeLifeCycle {
    DRAFT("draft", "label.reponses.feuilleRoute.etape.draft", "icon--previous-circle table-line__icon--is-disabled"),
    VALIDATED("validated", "label.reponses.feuilleRoute.etape.validated", "icon--check-circle base-paragraph--success"),
    DONE("done", "label.reponses.feuilleRoute.etape.done", "icon--check-circle base-paragraph--success"),
    CANCELED("canceled", "label.reponses.feuilleRoute.etape.canceled", "icon--times-circle base-paragraph--danger"),
    READY("ready", "label.reponses.feuilleRoute.etape.ready", "icon--next-circle table-line__icon--is-disabled"),
    RUNNING("running", "label.reponses.feuilleRoute.etape.running", "icon--arrow-triangle-right-circle");

    private String value;
    private String labelKey;
    private String icon;

    EtapeLifeCycle(String value, String labelKey, String icon) {
        this.value = value;
        this.labelKey = labelKey;
        this.icon = icon;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public String getIcon() {
        return icon;
    }

    public String getValue() {
        return value;
    }
}
