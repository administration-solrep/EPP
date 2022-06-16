package fr.dila.st.api.service;

import fr.dila.st.api.administration.EtatApplication;
import java.util.Map;
import javax.security.auth.login.LoginException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

/**
 * Service de gestion de l'état de l'application (restriction d'accès)
 *
 * @author Fabio Esposito
 *
 */
public interface EtatApplicationService {
    public String RESTRICTION_ACCESS = "RESTRICTION_ACCESS";
    public String RESTRICTION_DESCRIPTION = "RESTRICTION_DESCRIPTION";
    public String AFFICHAGE_BANNIERE = "AFFICHAGE_BANNIERE";
    public String BANNIERE_INFO = "BANNIERE_INFO";

    /**
     * Restreint l'accès à l'application aux administrateurs fonctionnels
     *
     * @param session
     * @param description
     */
    void restrictAccess(CoreSession session, String description);

    /**
     * Restore l'accès normal à l'application
     *
     * @param session
     */
    void restoreAccess(CoreSession session);

    void restrictTechnicalAccess(CoreSession session);

    void restoreTechnicalAccess(CoreSession session);

    /**
     * Retourne le document EtatApplication
     *
     * @param session
     * @return
     */
    EtatApplication getEtatApplicationDocument(CoreSession session);

    void createEtatApplication(CoreSession session);

    /**
     * Retourne RestrictionAcces
     *
     * Attention à utiliser que pour la page de login car l'utilisateur n'est pas encore connecté
     *
     * @return
     * @throws LoginException
     */
    Map<String, Object> getRestrictionAccesUnrestricted();

    /**
     * Document id can be kept in cache : this method enables to empty it.
     */
    void resetCache();

    boolean isApplicationRestricted(NuxeoPrincipal principal);

    boolean isApplicationRestricted();

    boolean isApplicationTechnicallyRestricted();
}
