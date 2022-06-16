package fr.dila.ss.ui.enums;

import fr.dila.st.ui.enums.UserSessionKey;

public enum SSUserSessionKey implements UserSessionKey {
    MODELE_FDR_LIST_FORM,
    MODELE_FORM,
    NOR,
    REQUETE_EXPERTE_DTO,
    REQUETE_EXPERTE_RESULT,
    SEARCH_RESULT_FORM,
    SUBSTITUTION_CRITERIA,
    SUPERVISION_ACTIF_USER,
    SUPERVISION_DATE_CONNEXION,
    SUPERVISION_INACTIF_USER,
    SUPERVISION_ONGLET;

    @Override
    public String getName() {
        return name();
    }
}
