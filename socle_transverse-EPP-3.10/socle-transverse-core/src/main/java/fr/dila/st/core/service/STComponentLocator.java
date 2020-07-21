package fr.dila.st.core.service;

import org.nuxeo.ecm.platform.forms.layout.service.WebLayoutManagerImpl;

import fr.dila.st.core.util.ServiceUtil;

/**
 * ComponentLocator du socle transverse : permet de rechercher les components de Nuxeo.
 * 
 * @author jtremeaux
 */
public final class STComponentLocator {

	/**
	 * utility class
	 */
	private STComponentLocator() {
		// do nothing
	}

	/**
	 * Retourne le composant WebLayoutManagerImpl.
	 * 
	 * @return Composant WebLayoutManagerImpl
	 */
	public static WebLayoutManagerImpl getWebLayoutManagerImpl() {
		return (WebLayoutManagerImpl) ServiceUtil
				.getComponentInstance("org.nuxeo.ecm.platform.forms.layout.WebLayoutManager");
	}
}
