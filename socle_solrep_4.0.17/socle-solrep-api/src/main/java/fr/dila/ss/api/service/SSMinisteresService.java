package fr.dila.ss.api.service;

import fr.dila.st.api.service.organigramme.STMinisteresService;
import org.nuxeo.ecm.core.api.CoreSession;

public interface SSMinisteresService extends STMinisteresService {
    /**
     * Récupère la liste des ministères sur lesquels l'utilisateur courant a les droits.
     *
     * @param session
     * @return
     */
    String getMinisteresQuery(CoreSession session);
}
