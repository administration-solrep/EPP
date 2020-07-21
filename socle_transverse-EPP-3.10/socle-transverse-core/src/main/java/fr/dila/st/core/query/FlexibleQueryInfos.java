package fr.dila.st.core.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.dila.st.core.query.FlexibleQueryMaker.KeyCode;

/**
 * Construit sur une requete FNXQL Extrait les mot clé du debut de chaine
 * 
 * @author spesnel
 * 
 */
class FlexibleQueryInfos {

	private String								queryWithoutCode;
	private final List<KeyCode>					keycodes;
	private final Map<KeyCode, List<String>>	keyargs;

	public FlexibleQueryInfos(String query) {
		this.queryWithoutCode = query;
		this.keycodes = new ArrayList<KeyCode>();
		this.keyargs = new HashMap<KeyCode, List<String>>();

		KeyCode kc = extractKeyCode(queryWithoutCode);
		while (kc != null) {
			keycodes.add(kc);
			queryWithoutCode = queryWithoutCode.substring(kc.key.length());
			List<String> args = new ArrayList<String>();
			String arg = extractArg(queryWithoutCode);
			while (arg != null) {
				args.add(arg);
				queryWithoutCode = queryWithoutCode.substring(arg.length() + 2);
				arg = extractArg(queryWithoutCode);
			}
			if (!args.isEmpty()) {
				keyargs.put(kc, args);
			}
			kc = extractKeyCode(queryWithoutCode);
		}
	}

	/**
	 * extract the first matchin keycode at the beginning of the query
	 * 
	 * @param query
	 * @return
	 */
	private KeyCode extractKeyCode(String query) {
		for (KeyCode kc : KeyCode.values()) {
			if (kc.match(query)) {
				return kc;
			}
		}
		return null;
	}

	private String extractArg(String query) {
		if (query.startsWith("[")) {
			int idx = query.indexOf("]");
			if (idx == 1) {
				return "";
			} else {
				return query.substring(1, idx);
			}
		}
		return null;
	}

	/** read accessors */

	public String getQueryWithoutCode() {
		return queryWithoutCode;
	}

	public List<KeyCode> getKeyCodes() {
		return keycodes;
	}

	public boolean hasKeyCode(KeyCode kc) {
		return keycodes.contains(kc);
	}

	public List<String> getArgs(KeyCode kc) {
		return keyargs.get(kc);
	}
}
