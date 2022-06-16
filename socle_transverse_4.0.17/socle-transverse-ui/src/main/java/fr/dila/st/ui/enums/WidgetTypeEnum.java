package fr.dila.st.ui.enums;

import fr.dila.st.ui.enums.parlement.WidgetTypeConstants;

public enum WidgetTypeEnum {
    TEXT(WidgetTypeConstants.TEXT),
    URL(WidgetTypeConstants.URL),
    FILE_MULTI(WidgetTypeConstants.FILE_MULTI),
    INPUT_TEXT(WidgetTypeConstants.INPUT_TEXT),
    INPUT_TEXT_HIDDEN(WidgetTypeConstants.INPUT_TEXT_HIDDEN),
    MULTIPLE_INPUT_TEXT(WidgetTypeConstants.MULTIPLE_INPUT_TEXT),
    TEXT_AREA(WidgetTypeConstants.TEXTAREA),
    DATE(WidgetTypeConstants.DATE),
    DATE_TIME(WidgetTypeConstants.DATE_TIME),
    MULTIPLE_DATE(WidgetTypeConstants.MULTIPLE_DATE),
    RADIO(WidgetTypeConstants.RADIO),
    NIVEAU_LECTURE(WidgetTypeConstants.NIVEAU_LECTURE),
    SELECT(WidgetTypeConstants.SELECT),
    PIECE_JOINTE(WidgetTypeConstants.PIECE_JOINTE),
    // Que côté Mgpp
    MULTIPLE_SELECT(WidgetTypeConstants.MULTIPLE_SELECT);

    /** Nom du fragment associé côté EPP/MGPP. Attention à ne pas modifier à outrance ! */
    private final String name;

    WidgetTypeEnum(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
