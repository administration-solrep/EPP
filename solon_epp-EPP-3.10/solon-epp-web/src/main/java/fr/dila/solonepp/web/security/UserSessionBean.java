package fr.dila.solonepp.web.security;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

import fr.dila.solonepp.api.security.principal.EppPrincipal;

/**
 * WebBean qui permet de récupérer le principal du socle transverse.
 * 
 * @author admin
 */
@Startup
@Name("userSession")
@Scope(ScopeType.SESSION)
@Install(precedence = Install.APPLICATION + 1)
public class UserSessionBean extends org.nuxeo.ecm.webapp.security.UserSessionBean {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 7639281445209754L;

    /**
     * Retourne le principal du socle transverse.
     * 
     * @return Principal du socle transverse
     */
    @Factory(value = "eppPrincipal", scope = ScopeType.SESSION)
    public EppPrincipal getCurrentNuxeoPrincipal() {
        return (EppPrincipal) getCurrentUser();
    }

}
