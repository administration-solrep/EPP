package fr.dila.solonepp.core.security.principal;

import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.st.core.user.STPrincipalImpl;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Implémentation du principal du socle SOLON EPP.
 *
 * @author jtremeaux
 */
public class EppPrincipalImpl extends STPrincipalImpl implements EppPrincipal {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ensemble des identifiants techniques des institutions.
     */
    private Set<String> institutionIdSet;

    /**
     * Constructeur de STPrincipalImpl.
     *
     * @param name name
     * @param isAnonymous isAnonymous
     * @param isAdministrator isAdministrator
     * @param updateAllGroups updateAllGroups
     */
    public EppPrincipalImpl(String name, boolean isAnonymous, boolean isAdministrator, boolean updateAllGroups) {
        super(name, isAnonymous, isAdministrator, updateAllGroups);
    }

    public EppPrincipalImpl(EppPrincipalImpl other) {
        super(other);
        if (other.institutionIdSet != null) {
            this.institutionIdSet = new HashSet<>(other.institutionIdSet);
        }
    }

    @Override
    public Set<String> getInstitutionIdSet() {
        return institutionIdSet;
    }

    @Override
    public void setInstitutionIdSet(Set<String> institutionSet) {
        this.institutionIdSet = institutionSet;
    }

    @Override
    public String getInstitutionId() {
        if (institutionIdSet == null || institutionIdSet.isEmpty()) {
            return null;
        }
        return institutionIdSet.iterator().next();
    }

    @Override
    public boolean isInstitutionGouvernement() {
        return institutionIdSet != null && institutionIdSet.contains(InstitutionsEnum.GOUVERNEMENT.name());
    }

    @Override
    public boolean isInstitutionAn() {
        return institutionIdSet != null && institutionIdSet.contains(InstitutionsEnum.ASSEMBLEE_NATIONALE.name());
    }

    @Override
    public boolean isInstitutionSenat() {
        return institutionIdSet != null && institutionIdSet.contains(InstitutionsEnum.SENAT.name());
    }

    @Override
    public boolean isInstitutionDila() {
        return institutionIdSet != null && institutionIdSet.contains(InstitutionsEnum.DILA.name());
    }

    @Override
    public void setAdministrator(boolean administrator) {
        this.isAdministrator = administrator;
    }

    @Override
    public EppPrincipal cloneTransferable() {
        return new TransferableClone(this);
    }

    protected static class TransferableClone extends EppPrincipalImpl {

        protected TransferableClone(EppPrincipalImpl other) {
            super(other);
        }

        static class DataTransferObject implements Serializable {
            private static final long serialVersionUID = 1L;

            private final String username;

            private final String originatingUser;

            DataTransferObject(EppPrincipal principal) {
                username = principal.getName();
                originatingUser = principal.getOriginatingUser();
            }

            private Object readResolve() throws ObjectStreamException {
                UserManager userManager = Framework.getService(UserManager.class);
                // look up principal as system user to avoid permission checks in directories
                EppPrincipal principal = (EppPrincipal) Framework.doPrivileged(
                    () -> userManager.getPrincipal(username)
                );
                if (principal == null) {
                    throw new NullPointerException("No principal: " + username);
                }
                principal.setOriginatingUser(originatingUser);
                return principal;
            }
        }

        private Object writeReplace() throws ObjectStreamException {
            return new DataTransferObject(this);
        }
    }
}
