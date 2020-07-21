package dila.spellchecker.engine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.zip.ZipFile;

import org.dts.spell.SpellChecker;
import org.dts.spell.dictionary.OpenOfficeSpellDictionary;
import org.dts.spell.dictionary.SpellDictionary;
import org.nuxeo.ecm.webengine.WebEngine;

/**
 * @author admin
 * 
 */
public class SpellCheckerConfiguration {

	private static SpellCheckerConfiguration	instance;

	public static final String					MAX_SUGGESTIONS_COUNT_PARAM	= "maxSuggestionsCount";
	public static final String					PRELOADED_LANGUAGES_PARAM	= "preloadedLanguages";

	public static final String					DEFAULT_LANGUAGE			= "en";
	public static final String					GET_METHOD_RESPONSE_ERROR	= "This servlet expects a JSON encoded body, POSTed to this URL";

	public static String						DEFAULT_PRELOADED_LANGUAGES	= "fr_FR";

	private static final String					DICTIONARIES				= "/dictionary";

	/*
	 * private enum methods { checkWords, getSuggestions } private int maxSuggestionsCount = 25;
	 */

	private Map<String, SpellChecker>			spellcheckersCache			= new Hashtable<String, SpellChecker>(2);

	private SpellCheckerConfiguration() throws SpellCheckException {
		init();
	}

	/**
	 * 
	 */
	private void init() throws SpellCheckException {
		preloadSpellcheckers();
	}

	/**
	 * Load the SpellChecker object from the file-system.
	 * 
	 * @param lang
	 * @return loaded SpellChecker object
	 * @throws SpellCheckException
	 */
	public synchronized SpellChecker loadSpellChecker(final String lang) throws SpellCheckException {
		//
		WebEngine.getActiveContext().getModule().getResourceBindings();
		//
		URL urldictionaries = this.getClass().getResource(DICTIONARIES);
		if (urldictionaries == null) {
			throw new SpellCheckException("Resources dictionaries not exist !");
		}
		File dictionariesDir = new File(urldictionaries.getFile());
		File dictionaryFile = new File(dictionariesDir, lang + ".zip");

		SpellDictionary dict = null;
		try {
			dict = new OpenOfficeSpellDictionary(new ZipFile(dictionaryFile));
		} catch (IOException e) {
			throw new SpellCheckException("Failed to load dictionary for language" + lang, e);
		}
		SpellChecker checker = new SpellChecker(dict);
		configureSpellChecker(checker);
		return checker;
	}

	private void configureSpellChecker(SpellChecker checker) {
		checker.setSkipNumbers(true);
		checker.setIgnoreUpperCaseWords(true);
		// set checker.setCaseSensitive(false) to avoid checking the case of word (JMySpell require first word in the
		// sentence to be upper-cased)
		checker.setCaseSensitive(false);
	}

	private void preloadSpellcheckers() throws SpellCheckException {
		// String preloaded = getServletConfig().getInitParameter(PRELOADED_LANGUAGES_PARAM);
		// if (preloaded == null || preloaded.trim().length() == 0) {
		String preloaded = DEFAULT_PRELOADED_LANGUAGES;
		// }

		String[] preloadedLanguages = preloaded.split(";");
		for (String preloadedLanguage : preloadedLanguages) {
			try {
				preloadLanguageChecker(preloadedLanguage);
			} catch (SpellCheckException e) {
				throw new SpellCheckException(e.getMessage(), e);
			}
		}
	}

	protected void preloadLanguageChecker(String preloadedLanguage) throws SpellCheckException {
		getChecker(preloadedLanguage);
	}

	protected Object getChecker(String lang) throws SpellCheckException {
		SpellChecker cachedCheker = spellcheckersCache.get(lang);
		if (cachedCheker == null) {
			cachedCheker = loadSpellChecker(lang);
			spellcheckersCache.put(lang, cachedCheker);
		}
		return cachedCheker;
	}

	/**
	 * @return the instance
	 */
	public static synchronized SpellCheckerConfiguration getInstance() throws SpellCheckException {
		if (instance == null) {
			instance = new SpellCheckerConfiguration();
		}
		return instance;
	}

}
