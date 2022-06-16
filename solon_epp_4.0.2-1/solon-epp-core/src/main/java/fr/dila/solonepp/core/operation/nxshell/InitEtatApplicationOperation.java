package fr.dila.solonepp.core.operation.nxshell;

import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.service.STServiceLocator;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

@Operation(id = InitEtatApplicationOperation.ID, category = "Initialisation")
@STVersion(version = "4.0.0")
public class InitEtatApplicationOperation {
    public static final String ID = "EtatApplication.Initialisation";

    @Context
    protected OperationContext context;

    @OperationMethod
    public void run() {
        if (!context.getPrincipal().isAdministrator()) {
            throw new NuxeoException("seulement un admin nuxeo peux initier les données");
        }

        CoreSession session = context.getCoreSession();

        STServiceLocator.getEtatApplicationService().createEtatApplication(session);
    }
}
