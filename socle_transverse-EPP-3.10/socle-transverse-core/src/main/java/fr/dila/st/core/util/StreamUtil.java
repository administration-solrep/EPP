package fr.dila.st.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Classe utilitaire pour les flux.
 * 
 * @author jgomez
 */
public final class StreamUtil {

	/**
	 * utility class
	 */
	private StreamUtil() {
		// do nothing
	}

	/**
	 * Prends un inputStream et retourne une chaîne de caractères.
	 * 
	 * @param stream
	 *            le flux d'entrée
	 * @return une chaîne de caractère
	 * @throws IOException
	 */
	public static String inputStreamAsString(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder stringBuilder = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			stringBuilder.append(line + "\n");
			line = reader.readLine();
		}
		reader.close();
		return stringBuilder.toString();
	}

}
