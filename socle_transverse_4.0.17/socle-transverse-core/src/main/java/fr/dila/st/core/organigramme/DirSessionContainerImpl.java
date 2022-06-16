package fr.dila.st.core.organigramme;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.organigramme.DirSessionContainer;
import fr.dila.st.core.util.DirSessionUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.Session;

/**
 * Conteneur de session LDAP afin de reutiliser les mêmes pour eviter les bind/unbind a repétition
 *
 *
 * @author asatre
 *
 */
public class DirSessionContainerImpl implements DirSessionContainer {
    private Map<String, Pair<Directory, Session>> mapSession;

    public DirSessionContainerImpl() {
        mapSession = new HashMap<>();
    }

    @Override
    public Session getSessionUser() {
        return getSession(STConstant.ORGANIGRAMME_USER_DIR);
    }

    @Override
    public void closeAll() {
        for (Entry<String, Pair<Directory, Session>> entry : mapSession.entrySet()) {
            DirSessionUtil.close(entry.getValue().getRight());
        }
        mapSession.clear();
    }

    @Override
    public void close() {
        closeAll();
    }

    @Override
    public void invalidateAllCaches() {
        for (Entry<String, Pair<Directory, Session>> entry : mapSession.entrySet()) {
            entry.getValue().getLeft().invalidateCaches();
        }
    }

    @Override
    public Session getSession(String directory) {
        return getPair(directory).getRight();
    }

    @Override
    public Directory getDirectory(String directory) {
        return getPair(directory).getLeft();
    }

    protected Pair<Directory, Session> getPair(String directory) {
        if (mapSession.get(directory) == null) {
            mapSession.put(
                directory,
                new ImmutablePair<Directory, Session>(
                    DirSessionUtil.getDirectory(directory),
                    DirSessionUtil.getSession(directory)
                )
            );
        }
        return mapSession.get(directory);
    }
}
