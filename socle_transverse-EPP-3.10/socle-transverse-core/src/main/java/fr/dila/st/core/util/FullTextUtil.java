package fr.dila.st.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.nuxeo.common.utils.FullTextUtils;

/**
 * Classe utilitaire pour le fulltext.
 * 
 * @author jgomez
 */
public class FullTextUtil {

	public static final int	MINIMAL_SIZE_NUMERIC	= 1;

	/**
	 * Utility class
	 */
	protected FullTextUtil() {
		// do nothing
	}

	/**
	 * Remplace les % par des * dans la chaîne str
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceStarByPercent(String str) {
		return str.replace('*', '%');
	}

	/**
	 * Remplace les ? par des _ dans la chaîne str
	 * 
	 * @param str
	 * @return
	 */
	private static String replaceQuestionMarkByUnderscore(String str) {
		return str.replace('?', '_');
	}

	/**
	 * Parse un mot pour la recherche fulltext : - les valeurs numériques de 1 chiffre retournent null. - les mots en
	 * dessous de 2 caractères retournent null. - les mots sont passés en minuscules et retournés entourés de
	 * l'opérateur de stemming d'Oracle. - Si le mot contient * ou ?, il faut utiliser les troncature Oracle : on
	 * transforme * en % et ? en _, sans utiliser l'opérateur de stemming pour ce mot
	 * 
	 * @param mot
	 * @return la forme simplifié du mot entouré de l'opérateur Oracle de stemming $(word)
	 */
	public static String parseWord(String word) {
		if (word.contains("*") || word.contains("?")) {
			String wordParsed = replaceStarByPercent(word);
			wordParsed = replaceQuestionMarkByUnderscore(wordParsed);
			return wordParsed;
		}
		if (StringUtils.isNumeric(word) && word.length() > MINIMAL_SIZE_NUMERIC) {
			return stem(word);
		}
		int len = word.length();
		if (len < FullTextUtils.MIN_SIZE) {
			return null;
		}
		StringBuilder buf = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			char lowerChar = Character.toLowerCase(word.charAt(i));
			buf.append(lowerChar);
		}
		return stem(word);
	}

	/**
	 * Retourne un mot avec l'opérateur de stemming d'Oracle.
	 * 
	 * @param word
	 * @return
	 */
	private static String stem(String word) {
		return "${" + word + "}";
	}

	/**
	 * Nettoie le contenu d'une chaîne de caractère des caractères posant problème.
	 * 
	 * @param contenu
	 * @return un contenu nettoyé
	 */
	public static String cleanContent(String content) {
		String contentCleaned = content;
		contentCleaned = contentCleaned.replaceAll("\"", "");
		contentCleaned = contentCleaned.replaceAll("\n", "");
		contentCleaned = contentCleaned.replaceAll("\r", "");
		contentCleaned = contentCleaned.replaceAll("\r\n", "");
		contentCleaned = contentCleaned.replaceAll("œ", "oe");
		return contentCleaned;
	}

	/**
	 * Preparation de la chaîne de caractère pour le champ fulltext. Attention ! Ce champ est retraité par la suite par
	 * la machinerie Nuxeo, donc on ne peut pas faire ce qu'on veut
	 * 
	 * @param content
	 * @return
	 */
	public static String parseContent(String content) {
		String contentCleaned = cleanContent(content);
		List<String> changedWords = new ArrayList<String>();
		for (String word : contentCleaned.split(" ")) {
			String parseWord = parseWord(word);
			if (parseWord != null) {
				changedWords.add(parseWord);
			}
		}
		String resultContent = StringUtils.join(changedWords, " ");
		return StringEscapeUtils.escapeSql(String.format("%s", resultContent));
	}
}
