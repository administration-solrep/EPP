package fr.dila.ss.ui.jaxrs.webobject.page;

import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.st.ui.th.model.SpecificContext;

public interface SSRequeteExperte {
    default String getSuffixForSessionKeys(SpecificContext context) {
        return this.getClass().getSimpleName();
    }

    default String getResultsSessionKey(SpecificContext context) {
        return SSUserSessionKey.REQUETE_EXPERTE_RESULT.name() + getSuffixForSessionKeys(context);
    }

    default String getDtoSessionKey(SpecificContext context) {
        return SSUserSessionKey.REQUETE_EXPERTE_DTO.name() + getSuffixForSessionKeys(context);
    }
}
