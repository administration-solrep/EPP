package fr.dila.ss.core.security.principal;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.core.user.STPrincipalImpl;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Implémentation du principal du socle Solrep.
 *
 * @author jtremeaux
 */
public class SSPrincipalImpl extends STPrincipalImpl implements SSPrincipal {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ensemble des identifiants techniques des ministères de l'utilisateur.
     */
    private Set<String> ministereIdSet;

    /**
     * Ensemble des identifiants techniques des directions de l'utilisateur.
     */
    private Set<String> directionIdSet;

    /**
     * Constructeur de SSPrincipalImpl.
     *
     * @param name name
     * @param isAnonymous isAnonymous
     * @param isAdministrator isAdministrator
     * @param updateAllGroups updateAllGroups
     */
    public SSPrincipalImpl(String name, boolean isAnonymous, boolean isAdministrator, boolean updateAllGroups) {
        super(name, isAnonymous, isAdministrator, updateAllGroups);
    }

    public SSPrincipalImpl(SSPrincipalImpl princ) {
        super(princ);
        if (princ.ministereIdSet != null) {
            this.ministereIdSet = new HashSet<>(princ.ministereIdSet);
        }
        if (princ.directionIdSet != null) {
            this.directionIdSet = new HashSet<>(princ.directionIdSet);
        }
    }

    @Override
    public Set<String> getMinistereIdSet() {
        return ministereIdSet;
    }

    @Override
    public void setMinistereIdSet(Set<String> ministereIdSet) {
        this.ministereIdSet = ministereIdSet;
    }

    @Override
    public Set<String> getDirectionIdSet() {
        return directionIdSet;
    }

    @Override
    public void setDirectionIdSet(Set<String> directionIdSet) {
        this.directionIdSet = directionIdSet;
    }

    @Override
    public SSPrincipal cloneTransferable() {
        return new TransferableClone(this);
    }

    protected static class TransferableClone extends SSPrincipalImpl {

        protected TransferableClone(SSPrincipalImpl other) {
            super(other);
        }

        static class DataTransferObject implements Serializable {
            private static final long serialVersionUID = 1L;

            private final String username;

            private final String originatingUser;

            DataTransferObject(SSPrincipal principal) {
                username = principal.getName();
                originatingUser = principal.getOriginatingUser();
            }

            private Object readResolve() throws ObjectStreamException {
                UserManager userManager = Framework.getService(UserManager.class);
                // look up principal as system user to avoid permission checks in directories
                SSPrincipal principal = (SSPrincipal) Framework.doPrivileged(() -> userManager.getPrincipal(username));
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
