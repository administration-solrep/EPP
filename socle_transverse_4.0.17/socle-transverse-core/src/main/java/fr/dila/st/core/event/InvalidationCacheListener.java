package fr.dila.st.core.event;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.STUserManager;
import fr.dila.st.core.service.STServiceLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.api.DirectoryService;

public class InvalidationCacheListener implements EventListener {
    private static final Log LOGGER = LogFactory.getLog(InvalidationCacheListener.class);

    @Override
    public void handleEvent(Event event) {
        if (!"invalidationCacheEvent".equals(event.getName())) {
            return;
        }

        DirectoryService ds = STServiceLocator.getDirectoryService();
        Directory droitsDirectory = ds.getDirectory(STConstant.ORGANIGRAMME_BASE_FUNCTION_DIR);
        Directory profilsDirectory = ds.getDirectory(STConstant.ORGANIGRAMME_PROFILE_DIR);
        Directory usersDirectory = ds.getDirectory(STConstant.ORGANIGRAMME_USER_DIR);
        STUserManager userManager = (STUserManager) STServiceLocator.getUserManager();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Début d'invalidation des caches ldap : fonctions, groups, users");
        }
        droitsDirectory.getCache().invalidateAll();
        profilsDirectory.getCache().invalidateAll();
        usersDirectory.getCache().invalidateAll();
        userManager.invalidateAllPrincipals();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Fin d'invalidation des caches ldap : fonctions, groups, users");
        }
    }
}
