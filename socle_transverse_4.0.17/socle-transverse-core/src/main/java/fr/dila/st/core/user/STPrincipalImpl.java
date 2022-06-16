package fr.dila.st.core.user;

import fr.dila.st.api.security.principal.STPrincipal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;

/**
 * Implémentation du principal du socle transverse.
 *
 * @author jtremeaux
 */
public class STPrincipalImpl extends NuxeoPrincipalImpl implements STPrincipal {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ensemble des fonctions unitaires de l'utilisateur.
     */
    private Set<String> baseFunctionSet;

    /**
     * Ensemble des identifiants techniques des postes de l'utilisateur.
     */
    private Set<String> posteIdSet;

    /**
     * Constructeur de STPrincipalImpl.
     *
     * @param name
     *            name
     * @param isAnonymous
     *            isAnonymous
     * @param isAdministrator
     *            isAdministrator
     * @param updateAllGroups
     *            updateAllGroups
     */
    public STPrincipalImpl(String name, boolean isAnonymous, boolean isAdministrator, boolean updateAllGroups) {
        super(name, isAnonymous, isAdministrator, updateAllGroups);
    }

    public STPrincipalImpl(STPrincipalImpl princ) {
        super(princ);
        if (princ.baseFunctionSet != null) {
            this.baseFunctionSet = new HashSet<>(princ.baseFunctionSet);
        }
        if (princ.posteIdSet != null) {
            this.posteIdSet = new HashSet<>(princ.posteIdSet);
        }
    }

    @Override
    public Set<String> getBaseFunctionSet() {
        return baseFunctionSet;
    }

    @Override
    public void setBaseFunctionSet(Set<String> baseFunctionSet) {
        this.baseFunctionSet = baseFunctionSet;
    }

    @Override
    public Set<String> getPosteIdSet() {
        return posteIdSet;
    }

    @Override
    public void setPosteIdSet(Set<String> posteIdSet) {
        this.posteIdSet = posteIdSet;
    }

    @Override
    public boolean isMemberOfAtLeastOne(Collection<String> groups) {
        return groups.stream().anyMatch(this::isMemberOf);
    }
}
