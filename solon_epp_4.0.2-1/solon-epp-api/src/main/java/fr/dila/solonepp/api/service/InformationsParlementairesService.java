package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.evenement.Version;
import java.io.Serializable;
import org.nuxeo.ecm.core.api.CoreSession;

public interface InformationsParlementairesService extends Serializable {
    /**
     * Appelle le ws epg CreerDossier pour la communication EVT45
     * @param session
     * @param dossier
     * @param evenement
     * @param version
     * @param isPublier
     * @throws Exception
     */
    void callWsCreerDossierEpg(
        CoreSession session,
        Dossier dossier,
        Evenement evenement,
        Version version,
        boolean isPublier
    )
        throws Exception;

    /**
     * Appelle le ws epg ModifierDossier pour la communication EVT45
     * @param session
     * @param evenement
     * @param version
     * @param pieceJointe
     * @throws Exception
     */
    void callWsModifierDossierEpg(CoreSession session, Evenement evenement, Version version, PieceJointe pieceJointe)
        throws Exception;
}
