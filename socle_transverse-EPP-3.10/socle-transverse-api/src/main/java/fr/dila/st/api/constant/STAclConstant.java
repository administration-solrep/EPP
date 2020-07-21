package fr.dila.st.api.constant;

/**
 * Constantes permettant de construire les ACL / ACE du socle transverse.
 * 
 * @author jtremeaux
 */
public final class STAclConstant {
	// *************************************************************
	// ACL
	// *************************************************************
	/**
	 * ACL de la grille de sécurité.
	 */
	public static final String	ACL_SECURITY						= "security";

	// *************************************************************
	// ACE des DossierLink
	// *************************************************************
	/**
	 * Préfixe des ACE permettant de voir les DossierLink via la distribution dans les ministères.
	 */
	public static final String	DOSSIER_LINK_UPDATER_MIN_ACE_PREFIX	= "dossier_link_updater_min-";

	/**
	 * utility class
	 */
	private STAclConstant() {
		// do nothing
	}

}
