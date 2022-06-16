package fr.dila.st.api.security.principal;

import java.util.Collection;
import java.util.Set;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

/**
 * Principal du socle transverse.
 *
 * @author jtremeaux
 */
public interface STPrincipal extends NuxeoPrincipal {
    /**
     * Getter de baseFunctionSet.
     *
     * @return baseFunctionSet
     */
    Set<String> getBaseFunctionSet();

    /**
     * Setter de baseFunctionSet.
     *
     * @param baseFunctionSet
     *            baseFunctionSet
     */
    void setBaseFunctionSet(Set<String> baseFunctionSet);

    /**
     * Getter de posteIdSet.
     *
     * @return posteIdSet
     */
    Set<String> getPosteIdSet();

    /**
     * Setter de posteIdSet.
     *
     * @param posteIdSet
     *            posteIdSet
     */
    void setPosteIdSet(Set<String> posteIdSet);

    /**
     * Teste si l'utilisateur est membre d'au moins un des groupes de la collection passée en paramètre.
     */
    boolean isMemberOfAtLeastOne(Collection<String> groups);
}
