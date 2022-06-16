package fr.sword.naiad.nuxeo.commons.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe utilitaire contenant des méthodes de gestion de paths.
 * 
 * @author fmh
 */
public final class PathUtil {
	/**
	 * Séparateur de répertoires.
	 */
	private static final String PATH_SEPARATOR = "/";

	/**
	 * Constructeur privé.
	 */
	private PathUtil() {
		// Classe utilitaire
	}

	/**
	 * Ajoute un élément au path donné.
	 * 
	 * append(null, null) = null
	 * append(null, "...") = "..."
	 * append("...", null) = "..."
	 * append("", "...") = "..."
	 * append("...", "") = "..."
	 * append("/a/b", "c") = "/a/b/c"
	 * append("/a/b/", "c") = "/a/b/c"
	 * 
	 * @param path
	 *            Path de base.
	 * @param element
	 *            Elément à ajouter.
	 * @return Path complet.
	 */
	public static String append(String path, String element) {
		if (path == null && element == null) {
			return null;
		}

		if (path == null) {
			return element;
		}
		if (StringUtils.isEmpty(element)) {
			return path;
		}
		if (path.isEmpty()) {
			return element;
		}

		String newPath;
		if (path.endsWith(PATH_SEPARATOR)) {
			newPath = path + element;
		} else {
			newPath = path + PATH_SEPARATOR + element;
		}

		return newPath;
	}

	/**
	 * Retourne le parent d'un path donné.
	 * 
	 * parentPath(null) = null
	 * parentPath("") = null
	 * parentPath("a") = null
	 * parentPath("/") = null
	 * parentPath("a/b") = "a"
	 * parentPath("a/b/") = "a"
	 * parentPath("/a/b") = "/a"
	 * parentPath("/a/b/") = "/a"
	 * parentPath("/a") = "/"
	 * parentPath("/a/") = "/"
	 * 
	 * @param path
	 *            Path enfant.
	 * @return Path parent.
	 */
	public static String parentPath(String path) {
		if (StringUtils.isEmpty(path)) {
			return null;
		}

		int sepIndex = path.indexOf(PATH_SEPARATOR);

		if (sepIndex == -1 || path.matches("^" + PATH_SEPARATOR + "+$")) {
			return null;
		}

		String cleanPath = StringUtils.stripEnd(path, PATH_SEPARATOR);
		sepIndex = cleanPath.lastIndexOf(PATH_SEPARATOR);
		if (sepIndex == -1) {
			return null;
		}

		String parentPath = cleanPath.substring(0, sepIndex + 1);

		if (parentPath.matches("^" + PATH_SEPARATOR + "+$")) {
			return parentPath;
		}
		return StringUtils.stripEnd(parentPath, PATH_SEPARATOR);
	}
}
