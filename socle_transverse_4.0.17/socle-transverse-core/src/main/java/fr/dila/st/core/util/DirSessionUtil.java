package fr.dila.st.core.util;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.uidgen.UIDGeneratorService;
import org.nuxeo.ecm.core.uidgen.UIDSequencer;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;

/**
 * Classe utilitaire sur les sessions LDAP.
 *
 * @author jtremeaux
 */
public final class DirSessionUtil {
    private static final int ID_LDAP_DEFAULT = 60000000;

    /**
     * Utility class
     */
    private DirSessionUtil() {
        // do nothing
    }

    public static Directory getDirectory(String directoryName) throws DirectoryException {
        DirectoryService directoryService = ServiceUtil.getRequiredService(DirectoryService.class);
        return directoryService.getDirectory(directoryName);
    }

    /**
     * Ouvre une nouvelle session sur le repository et retourne la session. /!\ Attention /!\ La session doit
     * impérativement être fermée dans un bloc finally !
     *
     * @param directoryName
     *            Nom du répertoire à ouvrir
     * @return Nouvelle session
     */
    public static Session getSession(String directoryName) {
        Directory dir = getDirectory(directoryName);
        return dir.getSession();
    }

    /**
     * Ferme la session ouverte avec la méthode getLdapSession ci-dessus.
     *
     * @param session Session
     */
    public static void close(Session session) {
        if (session != null) {
            session.close();
        }
    }

    /**
     * Retourne un id issu d'une séquence pour l'organigramme.
     *
     * @return
     * @throws DirectoryException
     */
    public static String getNextId(Session session) throws DirectoryException {
        // récupération d'un id unique
        final UIDGeneratorService uidGeneratorService = ServiceUtil.getRequiredService(UIDGeneratorService.class);
        UIDSequencer sequencer = uidGeneratorService.getSequencer();
        String entiteId = null;
        DocumentModel result = null;
        do {
            entiteId = String.valueOf(ID_LDAP_DEFAULT + sequencer.getNextLong("ORGANIGRAMME_SEQUENCER"));
            result = session.getEntry(entiteId);
        } while (result != null);
        return entiteId;
    }
}
