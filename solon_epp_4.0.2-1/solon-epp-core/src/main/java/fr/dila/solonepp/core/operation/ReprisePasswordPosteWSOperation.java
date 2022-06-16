package fr.dila.solonepp.core.operation;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IterableQueryResult;

@Operation(id = ReprisePasswordPosteWSOperation.ID, label = "ReprisPwdPosteWS")
public class ReprisePasswordPosteWSOperation {
    public static final String ID = "SolonEpp.Organigramme.Poste.Pwd";

    private static final STLogger LOGGER = STLogFactory.getLog(ReprisePasswordPosteWSOperation.class);

    @Context
    protected CoreSession session;

    @OperationMethod
    public void run() throws Exception {
        LOGGER.info(session, STLogEnumImpl.LOG_INFO_TEC, "Début de procédure de reprise des mots de passe poste WS");

        // Récupération des mots de passe en bdd et poste id correspondants
        Map<String, String> idPwdMap = new HashMap<String, String>();
        IterableQueryResult res = QueryUtils.doSqlQuery(
            session,
            new String[] { FlexibleQueryMaker.COL_ID, "dc:title" },
            "select poste.id_organigramme , poste.ws_mdp from POSTE where poste.ws_mdp is not null",
            new Object[] {}
        );
        if (res != null) {
            try {
                Iterator<Map<String, Serializable>> iterator = res.iterator();
                while (iterator.hasNext()) {
                    Map<String, Serializable> row = iterator.next();
                    String id = (String) row.get(FlexibleQueryMaker.COL_ID);
                    String pwd = (String) row.get("dc:title");
                    idPwdMap.put(id, pwd);
                }
            } finally {
                res.close();
            }
        }
        // récupération des postes et remise en place du pwd avec setPassword
        STPostesService posteService = STServiceLocator.getSTPostesService();
        List<PosteNode> postes = posteService.getPostesNodes(idPwdMap.keySet());
        for (PosteNode poste : postes) {
            poste.setWsPassword(idPwdMap.get(poste.getId()));
        }
        STServiceLocator.getOrganigrammeService().updateNodes(postes, false);

        LOGGER.info(session, STLogEnumImpl.LOG_INFO_TEC, "Fin de procédure de reprise des mots de passe poste WS");
    }
}
