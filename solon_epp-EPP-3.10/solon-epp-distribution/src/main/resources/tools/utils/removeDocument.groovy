import java.lang.StringBuilder;
import java.util.List;

import javax.transaction.Status;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.st.core.query.QueryUtils;

/**
 * script groovy de nettoyage
 * 
 */
class TransactionUtils {
    public static boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }
}    

class CleanUtils {    
    public static boolean run(final CoreSession session, final String docType) {
        boolean response = true;      
        final StringBuilder query = new StringBuilder();
        query.append("SELECT f.ecm:uuid as id FROM ").append(docType).append(" as f ");
        Long count = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(query.toString()), null);
        print count + " " + docType + " trouvés";
        long limit = (long) 50;
        for (Long offset = (long) 0; offset <= count; offset += limit) {
            Long borne = (count<offset+limit?count:offset+limit);
            print "Récupération " + docType + " de " + offset + " à " + borne;
            if (!TransactionUtils.isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
            List<DocumentModel> docModelList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, query.toString(), null, limit, 0);
            for (DocumentModel docModel : docModelList) {
                try {
                    session.removeDocument(docModel.getRef());
                } catch (ClientException ce) {
                    print "Impossible de supprimer le document " + docType + " id : " + docModel.getId();
                    response = false;
                }
            }
            session.save();
            TransactionHelper.commitOrRollbackTransaction();
            print "Fin traitement " + docType + " de " + offset + " à " + borne;
        }
        return response;
    }
}
    
print "Début du script groovy de nettoyage";
print "-------------------------------------------------------------------------------";
String docType = Context.get("docType");

if(StringUtils.isBlank(docType)) {
	print "Argument docType non trouvé.";
    print "Fin du script - docType n'a pas été renseigné";
    print "-------------------------------------------------------------------------------";
	return "Fin du script - docType n'a pas été renseigné";
}
docType = docType.replace("'", "");
if (!CleanUtils.run (Session, docType)) {
	print "Fin du script - Tous les documents n'ont pas pu être supprimés";
    print "-------------------------------------------------------------------------------";
	return "Fin du script - Tous les documents n'ont pas pu être supprimés";
} 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de nettoyage";
return "Fin du script groovy";

