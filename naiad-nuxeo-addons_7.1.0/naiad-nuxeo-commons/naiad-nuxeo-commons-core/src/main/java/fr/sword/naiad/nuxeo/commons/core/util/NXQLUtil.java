package fr.sword.naiad.nuxeo.commons.core.util;

/**
 * Classe utilitaire contenant notamment des méthodes permettant d'échapper les
 * paramètres des requêtes NXQL.
 * 
 * @author fmh
 */
public final class NXQLUtil {
	/**
	 * Délimiteur par défaut.
	 */
	public static final String DEFAULT_DELIMITER_CHAR = "'";

	/**
	 * caractère d'échappement par défaut.
	 */
	public static final String DEFAULT_ESCAPE_CHAR = "\\";

	/**
	 * Constructeur privé.
	 */
	private NXQLUtil() {
		// Classe utilitaire
	}

	/**
	 * Echappe la chaîne en utilisant le délimiteur et le caractère
	 * d'échappement par défaut.
	 * 
	 * @see #escapeParameter(String, String, String)
	 * 
	 * @param parameter
	 *            Chaîne à échapper.
	 * @return Chaîne échappée.
	 */
	public static String escapeParameter(String parameter) {
		return escapeParameter(parameter, DEFAULT_DELIMITER_CHAR);
	}

	/**
	 * Echappe la chaîne en utilisant le caractère d'échappement par défaut.
	 * 
	 * @see #escapeParameter(String, String, String)
	 * 
	 * @param parameter
	 *            Chaîne à échapper.
	 * @param delimiter
	 *            Délimiteur de chaîne.
	 * @return Chaîne échappée.
	 */
	public static String escapeParameter(String parameter, String delimiter) {
		return escapeParameter(parameter, delimiter, DEFAULT_ESCAPE_CHAR);
	}

	/**
	 * Echappe le délimiteur et le caractère d'échappement avec le caractère
	 * d'échappement.
	 * 
	 * @param parameter
	 *            Paramètre contenant les caractères à échapper.
	 * @param delimiter
	 *            Délimiteur de chaîne.
	 * @param escapeChar
	 *            Caractère utilisé pour échapper les caractère à échapper.
	 * @return Chaîne échappée.
	 */
	public static String escapeParameter(String parameter, String delimiter,
			String escapeChar) {
		if (parameter == null || escapeChar == null) {
			return parameter;
		}

		String result = parameter.replace(escapeChar, escapeChar + escapeChar);

		if (delimiter != null) {
			result = result.replace(delimiter, escapeChar + delimiter);
		}

		return result;
	}
}
