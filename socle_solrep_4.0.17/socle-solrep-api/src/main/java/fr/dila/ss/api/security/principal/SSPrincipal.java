package fr.dila.ss.api.security.principal;

import fr.dila.st.api.security.principal.STPrincipal;
import java.util.Set;

/**
 * Principal des applications Réponses / SOLON EPG.
 *
 * @author jtremeaux
 */
public interface SSPrincipal extends STPrincipal {
    /**
     * Getter de ministereIdSet.
     *
     * @return ministereIdSet
     */
    Set<String> getMinistereIdSet();

    /**
     * Setter de ministereIdSet.
     *
     * @param ministereIdSet ministereIdSet
     */
    void setMinistereIdSet(Set<String> ministereIdSet);

    /**
     * Getter de directionIdSet.
     *
     * @return directionIdSet
     */
    Set<String> getDirectionIdSet();

    /**
     * Setter de directionIdSet.
     *
     * @param directionIdSet directionIdSet
     */
    void setDirectionIdSet(Set<String> directionIdSet);
}
