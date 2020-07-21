/**
 * 
 */
package fr.dila.st.core.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import fr.dila.st.api.domain.ComplexeType;

/**
 * ComplexeType Implementation.
 * 
 * @author antoine Rolin
 * 
 */
public class ComplexeTypeImpl implements ComplexeType {

	private static final long			serialVersionUID	= 6943593468500432082L;

	private Map<String, Serializable>	serializableMap;

	public ComplexeTypeImpl() {
		serializableMap = new HashMap<String, Serializable>();
	}

	public ComplexeTypeImpl(Map<String, Serializable> serializableMap) {
		this.serializableMap = serializableMap;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.dila.solonepg.api.cases.typescomplexe.ApplicationLoi#getApplicationLoiMap()
	 */
	@Override
	public final Map<String, Serializable> getSerializableMap() {
		return serializableMap;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.dila.solonepg.api.cases.typescomplexe.ComplexeType#setSerializableMap(java.util.Map)
	 */
	@Override
	public void setSerializableMap(Map<String, Serializable> serializableMap) {
		this.serializableMap = serializableMap;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ComplexeTypeImpl) {
			Map<String, Serializable> other = ((ComplexeType) obj).getSerializableMap();
			// pour chaque entrée, on vérifie que le typeComplexe possède bien la même clé et la même valeur
			for (Entry<String, Serializable> entry : serializableMap.entrySet()) {
				if (other.containsKey(entry.getKey())) {
					// on vérifie que les valeurs des 2 entrées sont identiques et on gère le cas où la valeur est nulle
					if ((other.get(entry.getKey()) == null && entry.getValue() != null)
							|| (other.get(entry.getKey()) != null && !other.get(entry.getKey())
									.equals(entry.getValue()))) {
						return false;
					}
				} else {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return serializableMap.hashCode();
	}

	protected void put(String key, Serializable value) {
		serializableMap.put(key, value);
	}
}
