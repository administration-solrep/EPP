/**
 *
 */
package fr.dila.st.api.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface commune à tous les types complexe.
 *
 */
public interface ComplexeType extends Serializable {
    /**
     * get serializable map.
     *
     * @return map
     */
    Map<String, Serializable> getSerializableMap();

    /**
     * set class values and serializable map.
     *
     * @param serializableMap
     */
    void setSerializableMap(Map<String, Serializable> serializableMap);
}
