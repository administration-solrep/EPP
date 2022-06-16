package fr.dila.ss.core.operation.distribution;

import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.ss.core.service.ActualiteServiceImpl;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.util.ObjectHelper;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;

@Operation(id = CreateActualiteRootOperation.ID, category = ActualiteConstant.ACTUALITE_DOCUMENT_TYPE)
@STVersion(version = "4.0.0")
public class CreateActualiteRootOperation {
    public static final String ID = "Actualite.Root.Creation";

    @Context
    protected OperationContext context;

    @OperationMethod
    public void createRootFolder() {
        if (context.getPrincipal().isAdministrator()) {
            CoreSession session = context.getCoreSession();
            if (!session.exists(ActualiteServiceImpl.ACTUALITES_ROOT_PATH_REF)) {
                session.createDocument(
                    session.createDocumentModel(
                        STConstant.CASE_MANAGEMENT_PATH,
                        ActualiteConstant.ACTUALITE_ROOT_PATH_NAME,
                        ActualiteConstant.ACTUALITE_ROOT_DOCUMENT_TYPE
                    )
                );

                session.setACP(ActualiteServiceImpl.ACTUALITES_ROOT_PATH_REF, createAcp(session), true);
            }
        } else {
            throw new NuxeoException("seulement un admin nuxeo peux créer le dossier racine actualité");
        }
    }

    private ACP createAcp(CoreSession session) {
        ACP acp = ObjectHelper.requireNonNullElseGet(
            session.getACP(ActualiteServiceImpl.ACTUALITES_ROOT_PATH_REF),
            ACPImpl::new
        );
        ACL acl = acp.getOrCreateACL(ACL.LOCAL_ACL);
        acl.add(ACE.builder(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME, SecurityConstants.READ_WRITE).build());
        acl.add(ACE.builder(SecurityConstants.EVERYONE, SecurityConstants.READ).build());
        acp.addACL(acl);

        return acp;
    }
}
