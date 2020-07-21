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
 * Taches #6011
 * Mantis DILA 0150599: [SEN] les messages génériques hors procédure législative ne devraient pas avoir de copie.
 * Suppression des messages de type copie, liés aux evenements génériques
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
	query.append("AND e.evt:typeEvenement IN(") ;
	query.append("'GENERIQUE03',") ;
	query.append("'GENERIQUE04',") ;
	query.append("'GENERIQUE05',") ;
	query.append("'GENERIQUE06',") ;
	query.append("'GENERIQUE07',") ;
	query.append("'GENERIQUE08',") ;
	query.append("'GENERIQUE09',") ;
	query.append("'GENERIQUE10'") ;
	query.append(")") ;
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

    
print "Début du script groovy :  suppression des messages de type copie, liés aux evenements OPEX-02 : Résultat du vote sur la demande de prolongation d'une intervention extérieure.";


if (!CleanMessaesUtils.clean(Session)) {
	print "Fin du script - Tous les documents n'ont pas pu être supprimés";

	return "Fin du script - Tous les documents n'ont pas pu être supprimés";
} 

print "Fin du script groovy : suppression des messages de type copie, liés aux evenements OPEX-02 : Résultat du vote sur la demande de prolongation d'une intervention extérieure.";
return "Fin du script groovy";