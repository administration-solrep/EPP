package fr.dila.solonepp.core.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.evenement.NumeroVersion;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.service.VersionNumeroService;

/**
 * Implémentation du service permettant de gérer les numéros de version.
 * 
 * @author jtremeaux
 */
public class VersionNumeroServiceImpl implements VersionNumeroService {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    @Override
    public NumeroVersion getFirstNumeroVersion(boolean publie) throws ClientException {
        NumeroVersion numeroVersion = null;
        if (publie) {
            numeroVersion = new NumeroVersion(1L, 0L);
        } else {
            numeroVersion = new NumeroVersion(0L, 1L);
        }
        
        return numeroVersion;
    }

    @Override
    public NumeroVersion getNextNumeroVersion(DocumentModel lastVersionDoc, boolean publie) throws ClientException {
        Version version = lastVersionDoc.getAdapter(Version.class);
        NumeroVersion numeroVersion = version.getNumeroVersion();
        
        return getNextNumeroVersion(numeroVersion, publie);
    }
    
    @Override
    public NumeroVersion getNextNumeroVersion(NumeroVersion numeroVersion, boolean publie) throws ClientException {
        Long majorVersion = numeroVersion.getMajorVersion();
        Long minorVersion = numeroVersion.getMinorVersion();
        if (publie) {
            // 0.3 -> 1.0
            majorVersion++;
            minorVersion = 0L;
        } else {
            // 0.3 -> 0.4
            minorVersion++;
        }
        
        return new NumeroVersion(majorVersion, minorVersion);
    }
}
