package fr.dila.solonepp.core.filter;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Filter;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;

/**
 * Filtre permettant de conserver uniquement les documents alerte.
 *
 * @author jtremeaux
 */
public class DocumentAlertePoseFilter implements Filter, Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
    
    private CoreSession session;
    
    /**
     * Filtre par la position de l'alerte (facultatif).
     */
    private Boolean positionAlerte;
    
    /**
     * Constructeur de DocumentAlertePoseFilter.
     * 
     * @param session Session
     * @param positionAlerte Position de l'alerte (true: posée, false: levée ou non renseigné)
     */
    public DocumentAlertePoseFilter(CoreSession session, Boolean positionAlerte) {
        this.session = session;
        this.positionAlerte = positionAlerte;
    }

    @Override
    public boolean accept(DocumentModel doc) {
        Evenement evenement = doc.getAdapter(Evenement.class);
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        String typeEvenement = evenement.getTypeEvenement();
        if (!evenementTypeService.isEvenementTypeAlerte(typeEvenement)) {
            return false;
        }
        
        if (positionAlerte != null) {
            try {
                final VersionService versionService = SolonEppServiceLocator.getVersionService();
                DocumentModel versionDoc = versionService.getLastVersion(session, evenement.getTitle());
                Version version = versionDoc.getAdapter(Version.class);
                return positionAlerte == version.isPositionAlerte();
            } catch (ClientException e) {
                throw new ClientRuntimeException(e);
            }
        }
        
        return true;
    }
}
