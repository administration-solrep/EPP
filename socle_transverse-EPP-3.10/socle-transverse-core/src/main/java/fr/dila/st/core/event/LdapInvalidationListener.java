package fr.dila.st.core.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.directory.Directory;
import org.nuxeo.ecm.directory.api.DirectoryService;

import fr.dila.st.core.service.STServiceLocator;

public class LdapInvalidationListener implements EventListener {
	
	private static final Log LOGGER = LogFactory.getLog(LdapInvalidationListener.class);

	@Override
	public void handleEvent(Event event) throws ClientException {
		if (!"ldapInvalidationEvent".equals(event.getName())) {
            return;
        }
		
		DirectoryService ds = STServiceLocator.getDirectoryService();
		Directory droitsDirectory = ds.getDirectory("fonctionsDirectory");
		Directory profilsDirectory = ds.getDirectory("groupLdapDirectory");
		Directory usersDirectory = ds.getDirectory("userLdapDirectory");
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Début d'invalidation des caches ldap : fonctions, groups, users");
		}		
		droitsDirectory.getCache().invalidateAll();
		profilsDirectory.getCache().invalidateAll();
		usersDirectory.getCache().invalidateAll();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Fin d'invalidation des caches ldap : fonctions, groups, users");
		}		
	}

}
