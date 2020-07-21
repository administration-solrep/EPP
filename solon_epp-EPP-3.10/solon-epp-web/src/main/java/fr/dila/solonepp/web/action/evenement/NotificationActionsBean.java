package fr.dila.solonepp.web.action.evenement;

import java.io.Serializable;
import java.util.Calendar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.action.corbeille.CorbeilleTreeBean;
import fr.dila.solonepp.web.client.NotificationDTO;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean Seam d'alerte utilisateur lorsque la corbeille et/ou la communication courante sont modifiées.
 *  
 * @author bgamard
 */
@Name("notificationActions")
@Scope(ScopeType.PAGE)
public class NotificationActionsBean implements Serializable {

    /**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6989514955011107870L;

	@In(create = true, required = false)
    protected transient CoreSession documentManager;
    
    @In(create = true, required = false)
    protected transient CorbeilleTreeBean corbeilleTree;
    
    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    /**
     * Date de dernière mise à jour de la corbeille/communication courante.
     */
    private Calendar lastUpdate;
    
    /**
     * Default constructor
     */
    public NotificationActionsBean() {
    	// Default constructor
    }
    
    @Create
    public void onCreate() {
        lastUpdate = Calendar.getInstance();
    }
    
    /**
     * Retourne les données permettant d'afficher une notification en cas
     * de changement de la corbeille/communication courante.
     * @return
     * @throws ClientException
     */
    public NotificationDTO getNotificationDTO() throws ClientException {
        JetonService jetonService = SolonEppServiceLocator.getJetonService();
        NotificationDTO notificationDTO = new NotificationDTO();
        
        // Modifications sur la corbeille
        CorbeilleNode corbeilleNode = corbeilleTree.getCurrentItem();
        if (corbeilleNode != null && corbeilleNode.isTypeCorbeille()) {
            Long count = jetonService.getCountJetonsCorbeilleSince(documentManager, corbeilleNode.getName(), lastUpdate);
            if (count > 0) {
                notificationDTO.setCorbeilleModified(true);
            }
        }
        
        // Modifications sur l'évènement
        DocumentModel evenementDoc = navigationContext.getCurrentDocument();
        if (evenementDoc != null && evenementDoc.hasSchema(SolonEppSchemaConstant.EVENEMENT_SCHEMA)) {
            Evenement evenement = evenementDoc.getAdapter(Evenement.class);
            Long count = jetonService.getCountJetonsEvenementSince(documentManager, evenement.getTitle(), lastUpdate);
            if (count > 0) {
                notificationDTO.setEvenementModified(true);
            }
        }
        
        return notificationDTO;
    }
}
