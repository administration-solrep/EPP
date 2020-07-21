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
 * Taches #8035
 * Mantis DILA 0150679: Présence dans la corbeille administration legis de communications de type OPEX-02 en provenance du Sénat
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

class CleanMessaesUtils {    
    public static boolean clean(final CoreSession session) {
			   
			   
        boolean response = true;      

	final StringBuilder query  = new StringBuilder() ;
	query.append("SELECT m.ecm:uuid as id FROM Message AS m, ") ;
	query.append("Evenement AS e ") ;
	query.append(" WHERE m.cslk:idEvenement = e.evt:idEvenement ") ;
	query.append("AND e.evt:typeEvenement = 'EVT38'");
	query.append(" AND ") ;
	query.append("m.cslk:messageType = 'COPIE'") ;        
                  
        Long count = QueryUtils.doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(query.toString()), null);
       
        print count + " Messages trouvés";
        
        long limit = (long) 50;
        for (Long offset = (long) 0; offset <= count; offset += limit) {
            Long borne = (count<offset+limit?count:offset+limit);
            print "Récupération message de " + offset + " à " + borne;
            if (!TransactionUtils.isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
            List<DocumentModel> docModelList = QueryUtils.doUFNXQLQueryAndFetchForDocuments(session, query.toString(), null, limit, 0);


	    
            for (DocumentModel docModel : docModelList) {
                try {
                    session.removeDocument(docModel.getRef());
                } catch (ClientException ce) {
                    print "Erreur lors de la suppression du document id : " + docModel.getId();
                    response = false;
                }
                print "Le document id : " + docModel.getId() + " est supprimé .";
                
            }
            session.save();
            TransactionHelper.commitOrRollbackTransaction();
            print "Fin suppression des messages" + offset + " à " + borne;
        }
        
        return response;
			   
        }
    }

    
print "Début du script groovy :  suppression des messages de type copie, liés aux evenements génériques .";


if (!CleanMessaesUtils.clean(Session)) {
	print "Fin du script - Tous les documents n'ont pas pu être supprimés";

	return "Fin du script - Tous les documents n'ont pas pu être supprimés";
} 

print "Fin du script groovy : suppression des messages de type copie, liés aux evenements génériques .";
return "Fin du script groovy";