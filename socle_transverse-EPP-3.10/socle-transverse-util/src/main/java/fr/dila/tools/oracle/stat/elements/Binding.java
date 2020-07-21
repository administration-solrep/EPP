package fr.dila.tools.oracle.stat.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Set of binding key value : example ':1' --> 'Dossier'
 * 
 * @author spesnel
 * 
 */
public class Binding {
	private Map<String, String>	bindings;

	public Binding() {
		bindings = new HashMap<String, String>();
	}

	public void put(String key, String value) {
		bindings.put(key, value);
	}

	public Set<String> getKeys() {
		return bindings.keySet();
	}

	public String getValue(String key) {
		return bindings.get(key);
	}
}
