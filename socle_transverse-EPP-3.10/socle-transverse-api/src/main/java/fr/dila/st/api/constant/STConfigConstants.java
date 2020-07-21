package fr.dila.st.api.constant;

/**
 * Paramètres de l'application.
 * 
 * @author jtremeaux
 */
public final class STConfigConstants {
	/**
	 * Activation du bulk mode.
	 */
	public static final String	NUXEO_BULK_MODE								= "nuxeo.bulk.mode";

	/**
	 * Tag de l'application : numéro de version et date du build.
	 */
	public static final String	PRODUCT_TAG									= "reponses.product.tag";

	/**
	 * URL du serveur d'application.
	 */
	public static final String	SERVER_URL									= "nuxeo.url";

	/**
	 * Port du serveur d'application.
	 */
	public static final String	TOMCAT_PORT									= "nuxeo.server.http.port";

	/**
	 * Nom du serveur d'application.
	 */
	public static final String	SERVER_HOST									= "org.nuxeo.ecm.instance.host";

	/**
	 * Nom de l'utilisateur qui exécute les batch.
	 */
	public static final String	NUXEO_BATCH_USER							= "nuxeo.batch.user";

	/**
	 * Nombre de document à partir du duquel on créé un nouveau jeton maître dont le numéro a été incrémenté de 1.
	 */
	public static final String	WEBSERVICE_JETON_RESULT_SIZE				= "jeton.result.size";

	/**
	 * URL de légifrance.
	 */
	public static final String	LEGIFRANCE_JORF_URL							= "legifrance.jorf.url";

	/**
	 * mail.from
	 */
	public static final String	MAIL_FROM									= "mail.from";

	/**
	 * FEV521 : Libellé d'identification de la plateforme.
	 */
	public static final String	SOLON_IDENTIFICATION_PLATEFORME_LIBELLE		= "solon.identification.plateforme.libelle";
	/**
	 * FEV521 : Couleur du libellé d'identification de la plateforme.
	 */
	public static final String	SOLON_IDENTIFICATION_PLATEFORME_COULEUR		= "solon.identification.plateforme.couleur";
	/**
	 * FEV521 (EPG) : Nom de l'image d'identification de la plateforme (page de login).
	 */
	public static final String	SOLON_IDENTIFICATION_PLATEFORME_BACKGROUND	= "solon.identification.plateforme.background";
	
	/**
	 * FEV521 (EPP) : Couleur de fond d'identification de la plateforme (page de login).
	 */
	public static final String	SOLON_IDENTIFICATION_PLATEFORME_COULEURBG	= "solon.identification.plateforme.couleurbg";
	
	/**
	 * FEV552 : limite d'envoi de mails en masses
	 */
	public static final String	SEND_MAIL_LIMIT	= "mail.masse.limit";
	
	/**
	 * FEV552 : délai entre deux mails envoyés en masse
	 */
	public static final String	SEND_MAIL_DELAY	= "mail.masse.delay";

	/**
	 * utility class
	 */
	private STConfigConstants() {
		// do nothing
	}
}
