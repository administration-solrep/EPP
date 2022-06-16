package fr.dila.epp.ui.enumeration;

import fr.dila.st.ui.enums.ActionCategory;

public enum EppActionCategory implements ActionCategory {
    BASE_COMMUNICATION_DISPLAY,
    BASE_COMMUNICATION_EDIT,
    MAIN_COMMUNICATION_CREER,
    MAIN_COMMUNICATION_CREER_ALERTE,
    MAIN_COMMUNICATION_DISPLAY,
    MAIN_COMMUNICATION_EDIT,
    MAIN_TRANSMETTRE_PAR_MEL;

    @Override
    public String getName() {
        return name();
    }
}
