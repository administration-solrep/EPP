package fr.dila.st.core.smartquery;

import java.io.Serializable;

/**
 * Common interface for query build
 *
 * @since 5.4
 * @author Anahide Tchertchian
 */
public interface SmartQuery extends Serializable {
    /**
     * Returns a String with the built query (or query part).
     */
    String buildQuery();

    /**
     * Returns true if query is valid.
     */
    boolean isValid();
}
