package fr.sword.naiad.nuxeo.commons.core.helper;

import java.util.Locale;

import org.nuxeo.common.utils.i18n.I18NUtils;

/**
 * Classe utilitaire permettant d'appeler la classe I18NUtils de Nuxeo.
 * 
 * @author fmh
 */
public final class I18nHelper {
	private static final String MESSAGES_BUNDLE = "messages";

	/**
	 * Constructeur privé.
	 */
	private I18nHelper() {
		// Classe utilitaire
	}

	/**
	 * Récupère la traduction ayant pour identifiant key, avec la locale par défaut.
	 * 
	 * @param key
	 *            Clé du message.
	 * @return Message traduit.
	 */
	public static String getMessage(String key) {
		return getMessage(key, Locale.getDefault());
	}

	/**
	 * Récupère la traduction ayant pour identifiant key, avec la locale spécifiée.
	 * 
	 * @param key
	 *            Clé du message.
	 * @param locale
	 *            Locale à utiliser.
	 * @return Message traduit.
	 */
	public static String getMessage(String key, Locale locale) {
		return getMessage(key, locale, null);
	}

	/**
	 * Récupère la traduction ayant pour identifiant key, avec la locale par défaut et les paramètres spécifiés.
	 * 
	 * @param key
	 *            Clé du message.
	 * @param params
	 *            Paramètres du message.
	 * @return Message traduit.
	 */
	public static String getMessage(String key, Object[] params) {
		return getMessage(key, Locale.getDefault(), params);
	}

	/**
	 * Récupère la traduction ayant pour identifiant key, avec la locale et les paramètres spécifiés.
	 * 
	 * @param key
	 *            Clé du message.
	 * @param locale
	 *            Locale à utiliser.
	 * @param params
	 *            Paramètres du message.
	 * @return Message traduit.
	 */
	public static String getMessage(String key, Locale locale, Object[] params) {
		return I18NUtils.getMessageString(MESSAGES_BUNDLE, key, params, locale);
	}
}
