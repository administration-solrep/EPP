package fr.dila.tools.oracle.stat.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contient un ensemble de jeu de binding
 * 
 * @author spesnel
 * 
 */
public class BindingSet {
	public Map<String, Binding>	bindingGroup;

	public BindingSet() {
		bindingGroup = new HashMap<String, Binding>();
	}

	public Binding getBinding(String key) {
		Binding b = bindingGroup.get(key);
		if (b == null) {
			b = new Binding();
			bindingGroup.put(key, b);
		}
		return b;
	}

	public Set<String> getBindingKeys() {
		return bindingGroup.keySet();
	}
}
