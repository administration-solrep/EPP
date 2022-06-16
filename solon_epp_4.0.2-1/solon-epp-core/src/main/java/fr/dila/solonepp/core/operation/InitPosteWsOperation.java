package fr.dila.solonepp.core.operation;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.AbstractPersistenceDefaultComponent;
import fr.dila.st.core.util.CryptoUtils;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;

@Operation(
    id = InitPosteWsOperation.ID,
    category = Constants.CAT_DOCUMENT,
    label = "CreateParametrageMGPPOperation",
    description = "Initialisation de l'URL des postes WS ws_infopar et com_epp"
)
public class InitPosteWsOperation extends AbstractPersistenceDefaultComponent {
    public static final String ID = "SolonEpp.PosteWS.Init";
    private static final STLogger LOG = STLogFactory.getLog(InitPosteWsOperation.class);

    private static final String LOGIN_INFOPAR = "ws_infopar";
    private static final String LOGIN_COMEPP = "com_epp";
    private static final String QUERY = "UPDATE POSTE set WS_URL = :url, WS_MDP = :mdp WHERE WS_LOGIN = :login";

    @Context
    private CoreSession session;

    @Param(name = "urlMgpp")
    private String urlMgpp = "";

    @Param(name = "mdpMgpp")
    private String mdpMgpp = "";

    @Param(name = "urlInfoPar")
    private String urlInfoPar = "";

    @Param(name = "mdpInfoPar")
    private String mdpInfoPar = "";

    @Param(name = "urlAN", required = false)
    private String urlAN = "";

    @Param(name = "userAN", required = false)
    private String userAN = "";

    @Param(name = "mdpAN", required = false)
    private String mdpAN = "";

    public InitPosteWsOperation() {
        super("organigramme-provider");
    }

    @OperationMethod
    public void run() {
        LOG.info(
            STLogEnumImpl.DEFAULT,
            "Début de l'opération d'initialisation de l'URL des postes WS ws_infopar, com_epp et ws_epp"
        );
        accept(
            true,
            entityManager -> {
                executeUpdateQuery(entityManager, urlMgpp, LOGIN_COMEPP, mdpMgpp);
                executeUpdateQuery(entityManager, urlInfoPar, LOGIN_INFOPAR, mdpInfoPar);
                executeUpdateQuery(entityManager, urlAN, userAN, mdpAN);
            }
        );
        LOG.info(
            STLogEnumImpl.DEFAULT,
            "Fin de l'opération d'initialisation de l'URL des postes WS ws_infopar, com_epp et ws_epp"
        );
    }

    private void executeUpdateQuery(EntityManager entityManager, String url, String login, String mdp) {
        if (StringUtils.isNotBlank(login)) {
            Query query = entityManager.createNativeQuery(QUERY);
            query.setParameter("url", url);
            query.setParameter("mdp", CryptoUtils.encodeValue(mdp));
            query.setParameter("login", login);

            query.executeUpdate();
        }
    }
}
