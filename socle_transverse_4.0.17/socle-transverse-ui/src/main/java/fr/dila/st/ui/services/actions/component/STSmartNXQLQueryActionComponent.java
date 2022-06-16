/**
 *
 * Surcharge le beam seam SmartNXQLQueryActions du package smartSearch de Nuxeo,
 * afin d'apporter d'avantages de fonctionnalité.
 * Correction de problème d'échappement dans la requête et traduction d'une requête en language
 * utilisateur.
 *
 */
package fr.dila.st.ui.services.actions.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.actions.STSmartNXQLQueryActionService;
import fr.dila.st.ui.services.actions.impl.STSmartNXQLQueryActionServiceImpl;

public class STSmartNXQLQueryActionComponent
    extends ServiceEncapsulateComponent<STSmartNXQLQueryActionService, STSmartNXQLQueryActionServiceImpl> {

    /**
     * Default constructor
     */
    public STSmartNXQLQueryActionComponent() {
        super(STSmartNXQLQueryActionService.class, new STSmartNXQLQueryActionServiceImpl());
    }
}
