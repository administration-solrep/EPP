package dila.spellchecker.engine;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Derived form JMySpellServlet
 */

public abstract class AbstractTinyMCESpellChecker {

	private static Log	logger	= LogFactory.getLog(AbstractTinyMCESpellChecker.class.getName());

	private enum methods {
		checkWords, getSuggestions
	}

	private int	maxSuggestionsCount	= 25;

	protected abstract void preloadLanguageChecker(String preloadedLanguage) throws SpellCheckException;

	/**
	 * This method look for the already created SpellChecker object in the cache, if it is not present in the cache then
	 * it try to load it and put newly created object in the cache. SpellChecker loading is quite expensive operation to
	 * do it for every spell-checking request, so in-memory-caching here is almost a "MUST to have"
	 * 
	 * @param lang
	 *            the language code like "en" or "en-us"
	 * @return instance of SpellChecker for particular implementation
	 * @throws SpellCheckException
	 *             if method failed to load the SpellChecker for lang (it happens if there is no dictionaries for that
	 *             language was found in the classpath
	 */
	protected abstract Object getChecker(String lang) throws Exception;

	/**
	 * @see javax.servlet.Servlet#destroy()
	 */
	public void destroy() {
		clearSpellcheckerCache();
	}

	protected abstract void clearSpellcheckerCache();

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	public String doPost(String requestBody, HttpServletResponse response) {
		setResponeHeaders(response);
		try {

			JSONObject jsonInput = new JSONObject(requestBody);

			String methodName = jsonInput.optString("method");
			if (methodName == null || methodName.trim().equals("")) {
				throw new SpellCheckException("Wrong spellchecker-method-name:" + methodName);
			}

			JSONObject jsonOutput = new JSONObject("{'id':null,'result':[],'error':null}");
			switch (methods.valueOf(methodName.trim())) {
				case checkWords:
					jsonOutput.put("result", checkWords(jsonInput.optJSONArray("params")));
					break;
				case getSuggestions:
					jsonOutput.put("result", getSuggestions(jsonInput.optJSONArray("params")));
					break;
				default:
					throw new SpellCheckException("Unimplemented spellchecker method {" + methodName + "}");
			}

			// PrintWriter pw = response.getWriter();
			// pw.println(jsonOutput.toString());
			return jsonOutput.toString();
		} catch (SpellCheckException se) {
			logger.warn(se.getMessage(), se);
			return returnError(response, se.getMessage());
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return returnError(response, e.getMessage());
		}
		// response.getWriter().flush();
	}

	/**
	 * @param response
	 */
	private void setResponeHeaders(HttpServletResponse response) {
		response.setContentType("text/plain; charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", System.currentTimeMillis());
	}

	/**
	 * @param response
	 * @param message
	 */
	private String returnError(HttpServletResponse response, String message) {
		response.setStatus(500);
		try {
			return "{'id':null,'response':[],'error':'" + message + "'}";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONArray checkWords(JSONArray params) throws SpellCheckException {
		JSONArray misspelledWords = new JSONArray();
		if (params != null) {
			JSONArray checkedWords = params.optJSONArray(1);
			String lang = params.optString(0);
			lang = ("".equals(lang)) ? SpellCheckerConfiguration.DEFAULT_LANGUAGE : lang;

			List<String> misspelledWordsList = findMisspelledWords(new JsonArrayIterator(checkedWords), lang);

			for (String misspelledWord : misspelledWordsList) {
				misspelledWords.put(misspelledWord);
			}

		}
		return misspelledWords;
	}

	private JSONArray getSuggestions(JSONArray params) throws SpellCheckException {
		JSONArray suggestions = new JSONArray();
		if (params != null) {
			String lang = params.optString(0);
			lang = ("".equals(lang)) ? SpellCheckerConfiguration.DEFAULT_LANGUAGE : lang;
			String word = params.optString(1);
			List<String> suggestionsList = findSuggestions(word, lang, maxSuggestionsCount);
			for (String suggestion : suggestionsList) {
				suggestions.put(suggestion);
			}
		}
		return suggestions;
	}

	protected abstract List<String> findMisspelledWords(Iterator<String> checkedWordsIterator, String lang)
			throws SpellCheckException;

	protected abstract List<String> findSuggestions(String word, String lang, int maxSuggestionsCount)
			throws SpellCheckException;

	private static class JsonArrayIterator implements Iterator<String> {
		private JSONArray	array;
		private int			index	= 0;

		private JsonArrayIterator(JSONArray array) {
			this.array = array;
		}

		public boolean hasNext() {
			return index < array.length();
		}

		public String next() {
			return array.optString(index++);
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
