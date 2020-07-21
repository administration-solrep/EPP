package fr.dila.solonepp.web.action.corbeille;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webapp.action.WebActionsBean;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.solonepp.api.constant.SolonEppActionConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.service.VersionActionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.context.NavigationContextBean;

@Name("versionActions")
@Scope(ScopeType.CONVERSATION)
public class VersionActionsBean implements Serializable {

    /**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1286544553547683261L;
	
	private static final STLogger LOGGER = STLogFactory.getLog(VersionActionsBean.class);

	List<String> actionStringList;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;
    
    @In(create = true, required = false)
    protected transient WebActionsBean webActions;
    
    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;
    
    @In(create = true, required = false)
    protected transient CorbeilleActionsBean corbeilleActions;
    
    /**
     * Default constructor
     */
    public VersionActionsBean() {
    	// Default constructor
    }

    /**
     * Retourne la liste des actions possible sur la version courante
     * 
     * @return Liste d'action nuxeo
     * @throws ClientException
     */
    public List<Action> getActionList() throws ClientException {
        List<Action> actionPossibleList = new ArrayList<Action>();
        
        VersionActionService versionActionService = SolonEppServiceLocator.getVersionActionService();
        
        DocumentModel evenmentDoc = navigationContext.getCurrentDocument();
        Message message = corbeilleActions.getCurrentMessage();
        Version version = corbeilleActions.getCurrentVersion();

        if (message == null || version == null) {
        	actionStringList = new ArrayList<String>();
        } else {
        	actionStringList = versionActionService.findActionPossible(documentManager, evenmentDoc, version.getDocument(), message.getMessageType(), message.getEtatMessage());
        }

        List<Action> actionList = webActions.getActionsList(SolonEppActionConstant.VERSION_ACTION_CATEGORY);
        
        for (Action action : actionList) {
            if (actionStringList.contains(action.getId())) {
                actionPossibleList.add(action);
            }
        }
        
        return actionPossibleList;
    }
    
    /**
     * Retourne true si l'action est autorisée
     * 
     * @param action id de l'action
     * @return
     * @throws ClientException
     */
    public boolean isActionPossible(String action) throws ClientException {
        VersionActionService versionActionService = SolonEppServiceLocator.getVersionActionService();
        
        DocumentModel evenmentDoc = navigationContext.getCurrentDocument();
        Message message = corbeilleActions.getCurrentMessage();
        Version version = corbeilleActions.getCurrentVersion();
        if (message == null || version == null) {
            LOGGER.warn(documentManager, EppLogEnumImpl.FAIL_GET_VERSION_FONC);
            LOGGER.warn(documentManager, EppLogEnumImpl.FAIL_GET_MESSAGE_FONC);
            TransactionHelper.setTransactionRollbackOnly();
            return false;
        } else {
        	actionStringList = versionActionService.findActionPossible(documentManager, evenmentDoc, version.getDocument(), message.getMessageType(), message.getEtatMessage());
        }
        return actionStringList.contains(action);
    }
    
    /**
     * retourne le message de confirmation à afficher à l'utilisateur
     * 
     * @return
     */
    public String getConfirmMessageAccepter() {
        
        Version version = corbeilleActions.getCurrentVersion();
        if (version != null && SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(version.getModeCreation())) {
            return "label.version.confirm.annulation";
        } else if (version != null && SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE.equals(version.getModeCreation())) {
            return "label.version.confirm.rectification";
        } 
        return "";
    }
        
    /**
     * retourne le message de confirmation à afficher à l'utilisateur
     * 
     * @return
     */
    public String getConfirmMessageRejeter() {

        Version version = corbeilleActions.getCurrentVersion();
        if (version != null && SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(version.getModeCreation())) {
            return "label.version.rejeter.annulation";
        } else if (version != null && SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE.equals(version.getModeCreation())) {
            return "label.version.rejeter.rectification";
        }
        return "";
    }
        
    /**
     * retourne le message de confirmation à afficher à l'utilisateur
     * 
     * @return
     */
    public String getConfirmMessageAbandonner() {

        Version version = corbeilleActions.getCurrentVersion();
        if (version != null && SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(version.getModeCreation())) {
            return "label.version.confirm.abandonner.annulation";
        } else if (version != null && SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE.equals(version.getModeCreation())) {
            return "label.version.confirm.abandonner.rectification";
        }
        return "";
    }
    
    
}
