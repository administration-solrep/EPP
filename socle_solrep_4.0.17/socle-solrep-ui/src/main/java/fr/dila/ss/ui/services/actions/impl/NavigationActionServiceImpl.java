package fr.dila.ss.ui.services.actions.impl;

import fr.dila.ss.ui.services.actions.NavigationActionService;
import fr.dila.st.ui.th.model.SpecificContext;

public class NavigationActionServiceImpl implements NavigationActionService {
    public static final String ESPACE_TRAVAIL = "menu.travail.title";

    public static final String RECHERCHE = "menu.recherche.title";

    public static final String PLAN_CLASSEMENT = "menu.classement.title";

    public static final String ESPACE_ADMIN = "menu.admin.title";

    public static final String ESPACE_UTILISATEUR = "menu.utiliateur.title";

    @Override
    public boolean isFromEspaceTravail(SpecificContext context) {
        // Retourne true si la page précédemment consulté est l'espace de travail
        return context.comesFromMenu(ESPACE_TRAVAIL);
    }

    @Override
    public boolean isFromAdmin(SpecificContext context) {
        // Retourne true si la page précédemment consulté est l'espace d'admin
        return context.comesFromMenu(ESPACE_ADMIN);
    }
}
