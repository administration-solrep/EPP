package fr.sword.naiad.nuxeo.commons.core.util;

import java.io.File;

/**
 * Classe utilitaire de gestion de fichiers et de leur nom.
 * 
 * @author fmh
 */
public final class FileUtil {
	/**
	 * Constructeur privé.
	 */
	private FileUtil() {
		// Classe utilitaire.
	}

	/**
	 * Retourne l'extension d'un nom de fichier à partir d'un chemin vers ce
	 * fichier.
	 * 
	 * @param path
	 *            Chemin vers le fichier.
	 * @return Extension du fichier, null si le nom de fichier ne peut pas être
	 *         récupéré ou s'il n'a pas d'extension.
	 */
	public static String getExtension(String path) {
		String filename = getFilenameByPath(path);

		if (filename == null) {
			return null;
		}

		int lastDot = filename.lastIndexOf('.');

		if (lastDot == -1 || lastDot == 0 || lastDot == filename.length() - 1) {
			// le fichier ne contient pas de point ou n'en contient qu'un en
			// première position
			return null;
		}

		return filename.substring(lastDot + 1);
	}

	/**
	 * Retourne le nom du fichier à partir d'un chemin.
	 * 
	 * @param path
	 *            Chemin vers le fichier.
	 * @return Nom du fichier, null si path est null ou vide, ou si path se termine par
	 *         un séparateur de répertoires.
	 */
	public static String getFilenameByPath(String path) {
		if (path == null || path.isEmpty() || path.endsWith(File.separator)) {
			return null;
		}

		int lastSep = path.lastIndexOf(File.separatorChar);

		if (lastSep == -1) {
			return path;
		}

		return path.substring(lastSep + 1);
	}
}
