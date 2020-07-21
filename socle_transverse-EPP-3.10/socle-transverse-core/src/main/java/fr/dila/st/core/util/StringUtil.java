package fr.dila.st.core.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.Normalizer;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.rendering.fm.FreemarkerEngine;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Classe utilitaire sur les chaînes de caractères.
 * 
 * @author jtx
 */
public final class StringUtil extends StringUtils {

	private static final int	STRING_SIZE_MAX_VIEW	= 15;

	private static final String	ERROR_RENDER_FREEMARKER	= "Erreur de rendu freemarker";

	/**
	 * utility class
	 */
	private StringUtil() {
		super();
		// do nothing
	}

	/**
	 * Concatère une liste de chaines en ajoutant des quotes autout de chaque élément. Ex : ["java", "c#"] ->
	 * "'java', 'c#'"
	 * 
	 * @param elements
	 *            Elements à concaténer
	 * @param separator
	 *            Caractère séparateur (souvent ",")
	 * @param quote
	 *            Caractère de quotation (souvent "'")
	 * @return Chaîne concaténée
	 */
	public static String join(final String[] elements, final String separator, final String quote) {
		if (elements == null) {
			return "";
		}
		final String[] quotedElements = new String[elements.length];
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			stringBuilder.append(quote).append(elements[i]).append(quote);
			quotedElements[i] = stringBuilder.toString();
			stringBuilder.setLength(0);
		}
		return join(quotedElements, separator);
	}

	public static String join(final Collection<String> elements, final String separator, final String quote) {
		if (elements == null) {
			return "";
		}
		final String[] quotedElements = new String[elements.size()];
		int cptElt = 0;

		for (String element : elements) {

			quotedElements[cptElt++] = appendQuote(element, quote);
		}
		return join(quotedElements, separator);
	}

	private static String appendQuote(String element, String quote) {

		return quote + element + quote;
	}

	public static String joinValueToBlank(final Collection<String> elements, final String separator,
			final String quote, String value) {
		if (elements == null) {
			return "";
		}
		final String[] quotedElements = new String[elements.size()];
		int cptElt = 0;

		for (String element : elements) {
			if (value.equals(element)) {
				quotedElements[cptElt++] = appendQuote(" ", quote);
			} else {
				quotedElements[cptElt++] = appendQuote(element, quote);
			}
		}
		return join(quotedElements, separator);

	}

	/**
	 * Génère une chaine constituée d'une liste de points d'interrogations, séparés par des virgules.
	 * 
	 * @param count
	 *            Nombre de points d'interrogation
	 * @return Chaine
	 */
	public static String getQuestionMark(int count) {
		if (count <= 0) {
			return "";
		}

		String[] strings = new String[count];
		for (int i = 0; i < count; i++) {
			strings[i] = "?";
		}
		return join(strings, ",");
	}

	/**
	 * Converti une chaine de caractère au format dd/MM/yyyy en Date
	 * 
	 * @param sDate
	 * @return
	 * @throws ClientException
	 */
	public static Date stringToDate(String sDate) throws ClientException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
		ParsePosition pos = new ParsePosition(0);
		return formatter.parse(sDate, pos);
	}

	/**
	 * Affiche la stacktrace dans une chaîne de caractères.
	 * 
	 * @param throwable
	 *            Exception
	 * @return Affichage de la stacktrace
	 */
	public static String getStackTrace(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);
		printWriter.close();
		return stringWriter.toString();
	}

	/**
	 * Convertit une chaine de caractère écrite dans le format "dd MMMMMMM yyyy" en Date (exemple 1 janvier 2010).
	 * 
	 * @param sDate
	 * @return
	 * @throws ClientException
	 */
	public static Date stringFormatWithLitteralMonthDateToDate(String sDate) throws ClientException {
		Date date = null;
		String sDateReturn = sDate;
		// on enleve les espaces superflu
		sDateReturn = sDateReturn.replaceAll("bs{2,}b", " ");
		// on nettoie la date
		String[] data = split(sDateReturn, ' ');
		if (data.length != 3) {
			return date;
		}
		sDateReturn = data[0] + " " + data[1].toLowerCase() + " " + data[2];
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMMMMMMMM yyyy", Locale.FRANCE);
		ParsePosition pos = new ParsePosition(0);
		date = formatter.parse(sDateReturn, pos);
		return date;
	}

	/**
	 * Formatte un template avec Freemarker.
	 * 
	 * @param template
	 *            Contenu du template (ex: "Hello ${name}")
	 * @param paramMap
	 *            Tableau des paramètres (ex: { name -> "Toto" })
	 * @return Contenu formatté (ex: "Hello Toto")
	 * @throws ClientException
	 */
	public static String renderFreemarker(String template, Map<String, Object> paramMap) throws ClientException {
		FreemarkerEngine engine = new FreemarkerEngine();
		StringReader reader = new StringReader(template);
		try {
			Template temp = new Template("@inline", reader, engine.getConfiguration(), "UTF-8");
			StringWriter writer = new StringWriter();
			Environment env = temp.createProcessingEnvironment(paramMap, writer, engine.getObjectWrapper());
			env.process();
			return writer.toString();
		} catch (IOException exc) {
			throw new ClientException(ERROR_RENDER_FREEMARKER, exc);
		} catch (TemplateException exc) {
			throw new ClientException(ERROR_RENDER_FREEMARKER, exc);
		}
	}

	/**
	 * Nettoie une chaine de caractère des balises de commentaires &lt;-- et --&gt; et de leur contenu
	 * 
	 * @param str
	 *            la chaine à nettoyer
	 * @return la chaine de caractère moins toutes les balises de commentaires
	 */
	public static String deleteHtmlComment(String str) {
		return deletePattern(str, "<!--", "-->");
	}

	/**
	 * Nettoie une chaine de caractère d'un contenu compris entre deux bornes
	 * 
	 * @param str
	 *            la chaine à nettoyer
	 * @param begin
	 *            le début de chaine à nettoyer
	 * @param end
	 *            la fin de chaine à nettoyer
	 * @return la chaine de caractère moins toutes les chaines comprises entre les deux bornes
	 */
	public static String deletePattern(String str, String begin, String end) {
		String strToDelete = substring(str, begin, end);
		String strToReturn = str;
		while (isNotEmpty(strToDelete)) {
			strToReturn = replace(strToReturn, strToDelete, "");
			strToDelete = substring(strToReturn, begin, end);
		}
		return strToReturn;
	}

	/**
	 * SubstringBetween inclusif des balises de début et fin
	 * 
	 * @param str
	 *            la chaine entière
	 * @param begin
	 *            la chaine souhaitée au début de la chaine soustraite
	 * @param end
	 *            la chaine souhaitée à la fin de la chaine soustraite
	 * @return la chaine de caractère comprise en begin et end inclus - Si rien n'existe, retourne une chaine vide ("")
	 */
	public static String substring(String str, String begin, String end) {
		StringBuilder stringNettoyee = new StringBuilder();
		stringNettoyee.append(begin);
		String texte = substringBetween(str, begin, end);
		if (StringUtils.isNotEmpty(texte)) {
			stringNettoyee.append(texte);
			stringNettoyee.append(end);
		} else {
			stringNettoyee = new StringBuilder("");
		}
		return stringNettoyee.toString();
	}

	/**
	 * Supprime les caractères présents dans une chaine si leur code ASCII est supérieur à 255 (Caractères non UTF-8)
	 * 
	 * @param str
	 * @return la chaine de caractères moins les caractères dont l'ascii est > 255
	 */
	public static String deleteCharNotUTF8(String str) {
		if (str != null) {
			for (char ch : str.toCharArray()) {
				if (ch > 255) {
					str = str.replace(ch, ' ');
				}
			}
		}
		return str;
	}

	/**
	 * Réduit une String dont la taille dépasse 15 caractères. Utilisée pour les noms de fichiers trop long Indique par
	 * des ... que la chaine a été réduite <br />
	 * <br />
	 * Ex : nomDeFichierSuperLongPourUnAffichageGraphiqueCorrect.doc ---> nom...ect.doc
	 * 
	 * @param name
	 *            chaine à réduire
	 * @return String chaine réduite si initialement supérieure à 15 caractères, sinon renvoyée à l'identique
	 */
	public static String getShorterName(String name) {
		if (name.length() > STRING_SIZE_MAX_VIEW) {
			String debut = name.substring(0, 3);
			String fin = name.substring(name.length() - 7, name.length());
			String milieu = "...";
			return debut + milieu + fin;
		}
		return name;
	}

	/**
	 * remplace les caractères accentués par les caractères non accentués <br>
	 * ex : àéèîïëêôûù -> aeeiieeouu
	 * 
	 * @param source
	 * @return
	 */
	public static String removeAccent(String source) {
		return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	/**
	 * Remplace les espaces par des underscores (_) <br>
	 * ex : La petite maison -> La_petite_maison
	 * 
	 * @param source
	 * @return
	 */
	public static String removeSpaces(String source) {
		return source.replaceAll(" ", "_");
	}

	/**
	 * remplace les espaces et les accents d'une chaine de caractère par des underscores et les caractères non accentués
	 * 
	 * @see #removeAccent(String)
	 * @see #removeSpaces(String)
	 * @param source
	 * @return
	 */
	public static String removeSpacesAndAccent(String source) {
		return removeAccent(removeSpaces(source));
	}

	/**
	 * échappe les simples(') et doubles (") quotes. <br>
	 * Les doubles quotes (") sont remplacées par deux simples quotes (''). <br>
	 * ex : Une "phrase" pour l'exemple -> Une \'\'phrase\'\' pour l\'exemple
	 * 
	 * @param stringWithQuotes
	 * @return
	 */
	public static String escapeQuotes(String stringWithQuotes) {
		String stringWithoutQuotes = stringWithQuotes.replace("'", "\\\'");
		return stringWithoutQuotes.replace("\"", "\\\'\\\'");
	}

	public static String sanitizeJS(String string) {
		return string.trim().replaceAll("(?i)<script.*?>.*?</script.*?>", "") // balises script sont supprimées
				.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "") // appels javascript sont supprimés
				.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", ""); // appels aux attributs on* (onclick, onchange...) sont
																// supprimés
	}

	/**
	 * split une chaine src avec les regex donnés.
	 * @param src
	 * @param regexs
	 * @return List<String> la chaine splittée
	 */
	public static List<String> split(String src, String...regexs) {
		List<String> resultList = new ArrayList<String>();
		resultList.add(src);
		for (String regex : regexs) {
			List<String> resultTempList = new ArrayList<String>();
			for (String srcToSplit : resultList) {
				String[] result = srcToSplit.split(regex);
				resultTempList.addAll(Arrays.asList(result));
			}
			resultList = resultTempList;
		}
		return resultList;
	}

	/**
	 * Retourne true si la collection contient la chaine a rechercher, de
	 * manière insensible à la casse.
	 * 
	 * @param col
	 * @param searchString
	 * @return
	 */
	public static boolean containsIgnoreCase(Collection<String> col, String searchString) {
		for (String str : col) {
			if (str.equalsIgnoreCase(searchString)) {
				return true;
			}
		}
		return false;
	}

}
