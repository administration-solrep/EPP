package fr.sword.naiad.nuxeo.commons.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Classe utilitaires pour la manipulation des exception
 * 
 */
public final class ExceptionUtil {

	/**
	 * utility class
	 */
	private ExceptionUtil() {
		// private default constructor
	}

	/**
	 * Affiche la stacktrace dans une chaîne de caractères.
	 * 
	 * @param throwable {@link Throwable}
	 * @return une chaine de caractere contenant la stacktrace
	 */
	public static String printStackTrace(Throwable throwable) {
		StringWriter swriter = new StringWriter();
		PrintWriter pwriter = new PrintWriter(swriter);
		throwable.printStackTrace(pwriter);
		pwriter.close();
		return swriter.toString();
	}

}
