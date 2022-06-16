package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.UtilisateurMenuService;
import fr.dila.st.ui.services.impl.UtilisateurMenuServiceImpl;

public class UtilisateurMenuComponent
    extends ServiceEncapsulateComponent<UtilisateurMenuService, UtilisateurMenuServiceImpl> {

    public UtilisateurMenuComponent() {
        super(UtilisateurMenuService.class, new UtilisateurMenuServiceImpl());
    }
}
