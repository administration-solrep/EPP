package fr.dila.st.core.util;

import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Classe utilitaire sur les chaînes de caractères.
 *
 * @author jtx
 */
public final class StringHelper {
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
        Pattern.CASE_INSENSITIVE
    );
    private static final Pattern INVALID_ID_CHARACTERS = Pattern.compile("[^a-zA-Z0-9\\-]");
    private static final String NULL_STRING = "null";

    /**
     * utility class
     */
    private StringHelper() {}

    public static boolean isEmail(String email) {
        return StringUtils.isNotBlank(email) && VALID_EMAIL_ADDRESS_REGEX.matcher(email).find();
    }

    /**
     * Concatère une liste de chaines en ajoutant des quotes autout de chaque
     * élément. Ex : ["java", "c#"] -> "'java', 'c#'"
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
        return StringUtil.join(Arrays.asList(elements), separator, quote);
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
        return StringUtils.join(quotedElements, separator);
    }

    private static String appendQuote(String element, String quote) {
        return quote + element + quote;
    }

    /**
     * Joint les strings passés en paramètre, séparés par separator, quotés par
     * quote. Si value est trouvée dans la liste, l'élément est remplacé par " "
     */
    public static String joinValueToBlank(
        final Collection<String> elements,
        final String separator,
        final String quote,
        String value
    ) {
        if (CollectionUtils.isEmpty(elements)) {
            return "";
        }

        return StringUtils.join(
            elements
                .stream()
                .map(
                    element -> {
                        if (value.equals(element)) {
                            return appendQuote(" ", quote);
                        } else {
                            return appendQuote(element, quote);
                        }
                    }
                )
                .collect(Collectors.toList()),
            separator
        );
    }

    /**
     * Génère une chaine constituée d'une liste de points d'interrogations,
     * séparés par des virgules.
     *
     * @param count
     *            Nombre de points d'interrogation
     * @return Chaine
     */
    public static String getQuestionMark(int count) {
        if (count <= 0) {
            return "";
        }
        return StringUtil.genMarksSuite(count);
    }

    /**
     * Affiche la stacktrace dans une chaîne de caractères.
     *
     * @param throwable
     * Exception
     * @return Affichage de la stacktrace
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();

        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
        }

        return stringWriter.toString();
    }

    /**
     * Convertit une chaine de caractère écrite dans le format "dd MMMMMMM yyyy"
     * en Date (exemple 1 janvier 2010).
     *
     * @param sDate
     * @return
     */
    public static Date stringFormatWithLitteralMonthDateToDate(String sDate) {
        Date date = null;
        String sDateReturn = sDate;
        // on enleve les espaces superflu
        sDateReturn = sDateReturn.replaceAll("bs{2,}b", " ");
        // on nettoie la date
        String[] data = StringUtils.split(sDateReturn, ' ');
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
     */
    public static String renderFreemarker(String template, Map<String, Object> paramMap) {
        return StringUtil.renderFreemarker("@inline", template, paramMap);
    }

    /**
     * Nettoie une chaine de caractère des balises de commentaires &lt;-- et
     * --&gt; et de leur contenu
     *
     * @param str
     *            la chaine à nettoyer
     * @return la chaine de caractère moins toutes les balises de commentaires
     */
    public static String deleteHtmlComment(String str) {
        if (StringUtils.isNotBlank(str)) {
            return str.replaceAll("(?s)<!--.*?-->", "");
        } else {
            return str;
        }
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
     * @return la chaine de caractère comprise en begin et end inclus - Si rien
     *         n'existe, retourne une chaine vide ("")
     */
    public static String substring(String str, String begin, String end) {
        StringBuilder stringNettoyee = new StringBuilder();
        stringNettoyee.append(begin);
        String texte = StringUtils.substringBetween(str, begin, end);
        if (StringUtils.isNotEmpty(texte)) {
            stringNettoyee.append(texte);
            stringNettoyee.append(end);
        } else {
            stringNettoyee = new StringBuilder("");
        }
        return stringNettoyee.toString();
    }

    /**
     * Supprime les caractères présents dans une chaine si leur code ASCII est
     * supérieur à 255 (Caractères non UTF-8)
     *
     * @param str
     * @return la chaine de caractères moins les caractères dont l'ascii est >
     *         255
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
     * Remplace les espaces par des underscores (_) <br>
     * ex : La petite maison -> La_petite_maison
     */
    public static String removeSpaces(String source) {
        return source.replaceAll(" ", "_");
    }

    /**
     * remplace les espaces et les accents d'une chaine de caractère par des
     * underscores et les caractères non accentués
     */
    public static String removeSpacesAndAccent(String source) {
        return StringUtils.stripAccents(removeSpaces(source));
    }

    public static String convertToId(String source) {
        return INVALID_ID_CHARACTERS.matcher(StringUtils.stripAccents(source)).replaceAll("");
    }

    /**
     * échappe les simples(') et doubles (") quotes. <br>
     * Les doubles quotes (") sont remplacées par deux simples quotes (''). <br>
     * ex : Une "phrase" pour l'exemple -> Une \'\'phrase\'\' pour l\'exemple
     */
    public static String escapeQuotes(String stringWithQuotes) {
        String stringWithoutQuotes = stringWithQuotes.replace("'", "\\\'");
        return stringWithoutQuotes.replace("\"", "\\\'\\\'");
    }

    /**
     * Retourne true si la collection contient la chaine a rechercher, de manière insensible à la casse.
     */
    public static boolean containsIgnoreCase(Collection<String> col, String searchString) {
        return col.stream().anyMatch(str -> str.equalsIgnoreCase(searchString));
    }

    public static String escapeSql(String str) {
        if (str == null) {
            return null;
        }
        return StringUtils.replace(str, "'", "''");
    }

    public static String quote(String str) {
        return StringUtils.wrap(str, "'");
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> T removeNullStrings(T value) {
        if (value instanceof String && NULL_STRING.equals(value)) {
            return null;
        } else if (value instanceof List) {
            List<T> valueList = (List<T>) value;
            return (T) valueList.stream().filter(s -> !NULL_STRING.equals(s)).collect(Collectors.toList());
        }
        return value;
    }
}
