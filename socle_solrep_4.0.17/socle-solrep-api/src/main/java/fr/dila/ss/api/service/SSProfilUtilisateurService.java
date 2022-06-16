package fr.dila.ss.api.service;

import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.user.STProfilUtilisateur;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface SSProfilUtilisateurService<T extends STProfilUtilisateur> extends STProfilUtilisateurService<T> {
    void addDossierToListDerniersDossierIntervention(CoreSession session, String idDossier);

    List<DocumentModel> getListeDerniersDossiersIntervention(final CoreSession session);
}
