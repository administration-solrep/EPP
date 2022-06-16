package fr.dila.st.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;

public interface STCorbeilleActionService {
    /**
     * Retourne vrai si le dossier est présent dans une Mailbox et chargé en
     * session, c'est-à-dire que l'utilisateur peut agir sur le dossier.
     *
     * @return Vrai si l'utilisateur peut agir sur le dossier
     */
    boolean isDossierLoadedInCorbeille(SpecificContext context);
}
