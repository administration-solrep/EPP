package fr.dila.st.api.organigramme;

import java.io.Closeable;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.Session;

/**
 * Conteneur de session afin de reutiliser les mêmes pour eviter les bind/unbind a repétition
 *
 *
 * @author asatre
 *
 */
public interface DirSessionContainer extends Closeable {
    Session getSessionUser();

    void closeAll();

    void invalidateAllCaches();

    Session getSession(String directory);

    Directory getDirectory(String directory);
}
