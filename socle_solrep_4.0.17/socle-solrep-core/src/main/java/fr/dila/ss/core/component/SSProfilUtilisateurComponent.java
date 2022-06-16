package fr.dila.ss.core.component;

import fr.dila.ss.api.service.SSProfilUtilisateurService;
import fr.dila.ss.core.service.SSProfilUtilisateurServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSProfilUtilisateurComponent
    extends ServiceEncapsulateComponent<SSProfilUtilisateurService, SSProfilUtilisateurServiceImpl> {

    public SSProfilUtilisateurComponent() {
        super(SSProfilUtilisateurService.class, new SSProfilUtilisateurServiceImpl());
    }
}
