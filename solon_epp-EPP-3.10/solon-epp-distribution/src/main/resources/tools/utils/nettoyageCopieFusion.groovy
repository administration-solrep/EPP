import java.lang.StringBuilder;
import java.util.List;

import javax.transaction.Status;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.transaction.TransactionHelper;
import fr.dila.solonepp.api.domain.evenement.Evenement;

import fr.dila.st.core.query.QueryUtils;

/**
 * script groovy de nettoyage de champ copie de communications generiques hors procédure législative
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

class ClearCopieUtils {    
    public static boolean run(final CoreSession session) {
        boolean response = true;      

        final StringBuilder query = new StringBuilder();
    
            query.append("SELECT e.ecm:uuid as id FROM Evenement as e where e.evt:typeEvenement = 'EVT53-02'");
         query.append(" OR e.evt:typeEvenement = 'EVT53-03'");
        query.append(" OR e.evt:typeEvenement = 'EVT53-04'");
        query.append(" OR e.evt:typeEvenement = 'EVT53-05'");
        query.append(" OR e.evt:typeEvenement = 'EVT53-06'");
        query.append(" OR e.evt:typeEvenement = 'EVT53-07'");
        query.append(" OR e.evt:typeEvenement = 'EVT53-08'");
        query.append(" OR e.evt:typeEvenement = 'EVT53-09'");
        query.append(" OR e.evt:typeEvenement = 'EVT53-10'");
        
                  
        Long count = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(query.toString()), null);
       
        print count + " Evenement trouvés";
        long limit = (long) 50;
        for (Long offset = (long) 0; offset <= count; offset += limit) {
            Long borne = (count<offset+limit?count:offset+limit);
            print "Récupération evenement de " + offset + " à " + borne;
            if (!TransactionUtils.isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
            List<DocumentModel> docModelList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, query.toString(), null, limit, 0);



            for (DocumentModel docModel : docModelList) {
                try {
                    Evenement evenement = docModel.getAdapter(Evenement.class);
                    evenement.setDestinataireCopie(null);
                    evenement.setDestinataireCopieConcat(null);
                    session.saveDocument(docModel);
                } catch (ClientException ce) {
                    print "Impossible de modifier le document id : " + docModel.getId();
                    response = false;
                }
            }
            session.save();
            TransactionHelper.commitOrRollbackTransaction();
            print "Fin traitement evenement" + offset + " à " + borne;
        }
       
        return response;
        }
    }

    
print "Début du script groovy de clear copie de communication generiques hors procédure législative";
print "-------------------------------------------------------------------------------";

if (!ClearCopieUtils.run (Session)) {
	print "Fin du script - Tous les documents n'ont pas pu être clear";
    print "-------------------------------------------------------------------------------";
	return "Fin du script - Tous les documents n'ont pas pu être clear";
} 
print "-------------------------------------------------------------------------------";
print "Fin du script groovy de clear copie de communication generiques hors procédure législative";
return "Fin du script groovy";

