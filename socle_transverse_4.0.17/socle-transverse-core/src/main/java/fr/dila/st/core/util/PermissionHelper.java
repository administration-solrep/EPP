package fr.dila.st.core.util;

import static fr.dila.st.core.util.ResourceHelper.getString;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.core.exception.STAuthorizationException;
import java.util.Objects;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public final class PermissionHelper {

    public static boolean isAdminFonctionnel(NuxeoPrincipal principal) {
        return (
            principal.isAdministrator() || principal.isMemberOf(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME)
        );
    }

    public static boolean isAdminMinisteriel(NuxeoPrincipal principal) {
        return principal.isMemberOf(STBaseFunctionConstant.ADMIN_MINISTERIEL_GROUP_NAME);
    }

    public static boolean isSuperviseurSgg(NuxeoPrincipal principal) {
        return principal.isMemberOf(STBaseFunctionConstant.SUPERVISEUR_SGG_GROUP_NAME);
    }

    public static boolean isAdminJournalReader(NuxeoPrincipal principal) {
        return (
            principal.isAdministrator() || principal.isMemberOf(STBaseFunctionConstant.ADMINISTRATION_JOURNAL_READER)
        );
    }

    /**
     * Lève une {@link STAuthorizationException} si le user n'est pas un
     * administrateur fonctionnel
     *
     * @param session
     *            le CoreSession de l'utilisateur
     * @param errorMsgKey
     *            la clef du message à passer dans l'exception
     */
    public static void checkAdminFonctionnel(CoreSession session, String errorMsgKey) {
        Objects.requireNonNull(session);
        if (!isAdminFonctionnel(session.getPrincipal())) {
            throw new STAuthorizationException(getString(errorMsgKey));
        }
    }

    private PermissionHelper() {}
}
