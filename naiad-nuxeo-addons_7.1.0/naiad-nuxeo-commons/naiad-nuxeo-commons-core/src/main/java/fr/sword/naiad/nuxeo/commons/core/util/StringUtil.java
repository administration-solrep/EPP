package fr.sword.naiad.nuxeo.commons.core.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.Normalizer;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.rendering.fm.FreemarkerEngine;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Class utitaire : opréation sur les chaines de caratère
 * 
 * @author SPL
 *
 */
public final class StringUtil {

	// regex
    private static final String REGEX_ALPHA_DASHES_SPACES = "[^a-z0-9-\\s]";
    private static final String REGEX_N_SPACES = "\\s{2,}";
    private static final String REGEX_ALPHA_DASH = "[^a-z0-9-]";

    // replacement string
    private static final String NONE = "";
    private static final String SPACE = " ";
    private static final String DASH = "-";
	
	/**
	 * Classe utilitaire
	 */
	private StringUtil(){
		
	}

	/**
	 * Concatène tous les éléments de la collection.
	 * @param elts Collection de chaînes de caractères.
	 * @return Chaîne concaténée.
	 */
	public static String join(Collection<String> elts) {
		return join(elts, null);
	}

	/**
	 * Concatène tous les éléments de la collection en les séparant par sep.
	 * @param elts Collection de chaînes de caractères.
	 * @param sep Séparateur.
	 * @return Chaîne concaténée.
	 */
	public static String join(Collection<String> elts, String sep) {
		return join(elts, sep, null);
	}
	
	/**
	 * Concatène un ensemble d'élément en les faisant précédés et suivre par le contenu d'une chaine, et en les séparant par une autre chaine
	 * 
	 * si elts contient 'a', 'b' et 'c'
	 * si quote = | 
	 * si sep = -
	 * alors le resultat est : '|a|-|b|-|c|'
	 * 
	 * @param elts éléments à concaténer
	 * @param sep chaine intercalée entre les éléments (rien si null ou vide)
	 * @param quote chaine ajoutée avant et après les éléments (rien si null ou vide)
	 * @return chaine des elements caoncatés encadres par quote et séparé par sep
	 */
	public static String join(Collection<String> elts, String sep, String quote){
		return join(elts, sep, quote, quote);
	}
	
	/**
	 * 
	 * 
	 * @param elts éléments à concaténer
	 * @param separator chaine intercalée entre les éléments (rien si null ou vide)
	 * @param startQuote chaine ajoutée avant les éléments (rien si null ou vide)
	 * @param endQuote chaine ajoutée après les éléments (rien si null ou vide)
	 * @return elements concaténés précédé par startQuote, suivi par endQuote, et séparé par separator
	 */
	public static String join(Collection<String> elts, String separator, String startQuote, String endQuote){
		String sep = separator;
		String pStartQuote = startQuote;
		String pEndQuote = endQuote;
		
		if(sep == null){
			sep = "";
		}		
		if(pStartQuote == null){
			pStartQuote = "";
		}
		if(pEndQuote == null){
			pEndQuote = "";
		}
		final StringBuilder strBuilder = new StringBuilder();
		boolean first = true;
		for(String str : elts){
			if(first){
				first = false;
			} else {
				strBuilder.append(sep);
			}
			strBuilder.append(pStartQuote).append(str).append(pEndQuote);
		}
		return strBuilder.toString();
	}
	
	/**
     * Formatte un template avec Freemarker.
     * 
     * @param templateName nom du template utilisé comme clé
     * @param templateContent Contenu du template (ex: "Hello ${name}")
     * @param paramMap Tableau des paramètres (ex: { name -&gt; "Toto" })
     * @return Contenu formatté (ex: "Hello Toto")
     * @throws NuxeoException
     */
    public static String renderFreemarker(String templateName, String templateContent, Map<String, ?> paramMap) throws NuxeoException {
    	if(StringUtils.isEmpty(templateContent)){
    		return "";
    	} else {
	        FreemarkerEngine engine = new FreemarkerEngine();
	        engine.getConfiguration().setEncoding(Locale.FRANCE, "UTF-8");
	        StringReader reader = new StringReader(templateContent);
	        try {        	
	            Template temp = new Template(templateName, reader, engine.getConfiguration());
	            StringWriter writer = new StringWriter();
	            Environment env = temp.createProcessingEnvironment(paramMap, writer, engine.getObjectWrapper());
	            env.process();
	            return writer.toString();
	        } catch (IOException | TemplateException e) {
	            throw new NuxeoException("Erreur de rendu freemarker", e);
	        }
    	}
    }

    /**
     * Retourne une chaine de n marks : ?,?,?
     * @param nbMark le nombre de ? a générer
     * @return une chaine de caractere contenant nue liste de ? séparé par des virgules
     */
	public static String genMarksSuite(int nbMark){
		if(nbMark == 0){
			return "";
		} else {
			String marksStr = StringUtils.repeat("?,", nbMark);
			// remove the last comma
			return marksStr.substring(0, marksStr.length()-1);
		}
	}
	

	/**
     * Transform an input String into a slug
     * 
     * @param input chaine d'entree à traité
     * @return la chaine d'entree sans accent, sans espace, ...
     */
	public static String slugify(String input) {

        String result = input;

        // --- replace accented characters
        result = unAccent(result);

        // --- convert to lowercase
        result = result.toLowerCase();

        // --- remove spécial characters
        result = result.replaceAll(REGEX_ALPHA_DASHES_SPACES, NONE);

        // remove space at end and beginning
        result = result.trim();

        // --- n spaces becomes one space
        result = result.replaceAll(REGEX_N_SPACES, SPACE);

        // --- replace invalid characters
        return result.replaceAll(REGEX_ALPHA_DASH, DASH);

	}
	
	/**
     * Remove all accents from in and replace them using their ascii equivalent
     * 
     * @param input the string to process
     * @return the input string without accents
     */
    public static String unAccent(String input) {

            String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("");
    }

}
