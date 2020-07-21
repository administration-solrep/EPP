package dila.spellchecker.engine;

/*
 Copyright (c) 2008
 Rich Irwin <rirwin@seacliffedu.com>, Andrey Chorniy <andrey.chorniy@gmail.com>

 Permission is hereby granted, free of charge, to any person
 obtaining a copy of this software and associated documentation
 files (the "Software"), to deal in the Software without
 restriction, including without limitation the rights to use,
 copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the
 Software is furnished to do so, subject to the following
 conditions:

 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 OTHER DEALINGS IN THE SOFTWARE.
 */

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dts.spell.SpellChecker;

/**
 * Implementation based on http://jmyspell.javahispano.net/ Dictionaries could be downloaded from
 * http://wiki.services.openoffice.org/wiki/Dictionaries
 * 
 * @author: Andrey Chorniy Date: 10.12.2009
 */
public class JMySpellChecker extends AbstractTinyMCESpellChecker {

	private Map<String, SpellChecker>	spellcheckersCache	= new Hashtable<String, SpellChecker>(2);

	protected void preloadLanguageChecker(String preloadedLanguage) throws SpellCheckException {
		SpellCheckerConfiguration.getInstance().getChecker(preloadedLanguage);
	}

	protected List<String> findMisspelledWords(Iterator<String> checkedWordsIterator, String lang)
			throws SpellCheckException {
		SpellChecker checker = (SpellChecker) SpellCheckerConfiguration.getInstance().getChecker(lang);

		List<String> misspelledWordsList = new ArrayList<String>();
		while (checkedWordsIterator.hasNext()) {
			String word = checkedWordsIterator.next();
			if (!word.equals("") && !checker.isCorrect(word)) {
				misspelledWordsList.add(word);
			}
		}

		return misspelledWordsList;
	}

	protected List<String> findSuggestions(String word, String lang, int maxSuggestions) throws SpellCheckException {
		SpellChecker checker = (SpellChecker) SpellCheckerConfiguration.getInstance().getChecker(lang);
		return checker.getDictionary().getSuggestions(word, maxSuggestions);
	}

	/**
	 * This method look for the already created SpellChecker object in the cache, if it is not present in the cache then
	 * it try to load it and put newly created object in the cache. SpellChecker loading is quite expensive operation to
	 * do it for every spell-checking request, so in-memory-caching here is almost a "MUST to have"
	 * 
	 * @param lang
	 *            the language code like "en" or "en-us"
	 * @return instance of jazzy SpellChecker
	 * @throws SpellCheckException
	 *             if method failed to load the SpellChecker for lang (it happens if there is no dictionaries for that
	 *             language was found in the classpath
	 */
	protected Object getChecker(String lang) throws Exception {
		SpellChecker cachedCheker = spellcheckersCache.get(lang);
		if (cachedCheker == null) {
			cachedCheker = SpellCheckerConfiguration.getInstance().loadSpellChecker(lang);
			spellcheckersCache.put(lang, cachedCheker);
		}
		return cachedCheker;
	}

	@Override
	protected void clearSpellcheckerCache() {
		spellcheckersCache.clear();
		spellcheckersCache = new Hashtable<String, SpellChecker>();
	}
}
