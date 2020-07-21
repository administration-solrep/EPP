package fr.dila.solonepp.web.action.corbeille;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.richfaces.component.UITree;
import org.richfaces.event.NodeExpandedEvent;

import fr.dila.solonepp.api.constant.SolonEppContentView;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.CorbeilleTypeService;
import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Classe de gestion de l'arbre des corbeilles.
 * 
 * @author bgamard
 */
@Name("corbeilleTree")
@Scope(ScopeType.CONVERSATION)
public class CorbeilleTreeBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CorbeilleTreeBean.class);    

    private List<CorbeilleNode> corbeilleNodes;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(required = true, create = true)
    protected EppPrincipal eppPrincipal;

    @In(create = true, required = false)
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(create = true, required = false)
    protected transient ContentViewActions contentViewActions;
    
    @In(create = true, required = false)
	protected transient FacesMessages facesMessages;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;
    
    private CorbeilleNode currentItem;

    protected Set<String> userMailboxIds;
    

    /**
     * charge l'arbre des corbeilles
     * 
     * @throws ClientException
     */
    private void loadTree() throws ClientException {
        corbeilleNodes = getCorbeille(eppPrincipal.getInstitutionId());
    }

    /**
     * 
     * @param afficherPrecomptage afiche ou non le precomptage sur les ministeres (masque le ministere si 0)
     * @return
     * @throws ClientException
     */
    private List<CorbeilleNode> getCorbeille(String institution) throws ClientException {
        final CorbeilleTypeService corbeilleTypeService = SolonEppServiceLocator.getCorbeilleTypeService();
        
        if(StringUtils.isEmpty(institution)){
        	String message = "Aucune institution trouvee";
        	LOGGER.error(documentManager, EppLogEnumImpl.FAIL_GET_INSTITUTION) ;
    		facesMessages.add(StatusMessage.Severity.WARN, message);
    		TransactionHelper.setTransactionRollbackOnly();
    		return new ArrayList<CorbeilleNode>();
        }
        
        List<CorbeilleNode> corbeilles = corbeilleTypeService.getCorbeilleInstitutionTreeWithCount(institution, documentManager);

        return corbeilles;
    }

    public Boolean adviseNodeSelected(UITree treeComponent) {
        return Boolean.FALSE;
    }

    public Boolean adviseNodeOpened(UITree treeComponent) {
        Object value = treeComponent.getRowData();
        if (value instanceof CorbeilleNode) {
            CorbeilleNode minNode = (CorbeilleNode) value;
            return minNode.isOpened();
        }
        return null;
    }

    /**
     * Méthode qui renvoie l'arbre des corbeilles complet
     * 
     * @return l'arbre chargé
     * @throws ClientException
     */
    public List<CorbeilleNode> getCorbeille() throws ClientException {
        if (corbeilleNodes == null) {
                loadTree();
        }
        return corbeilleNodes;
    }

    public void forceRefresh() throws ClientException {
        corbeilleNodes = null;
        getCorbeille();
    }

    public void changeExpandListener(NodeExpandedEvent event) {
        UIComponent component = event.getComponent();
        if (component instanceof UITree) {
            UITree treeComponent = (UITree) component;
            Object value = treeComponent.getRowData();
            if (value instanceof CorbeilleNode) {
                CorbeilleNode corbeilleNode = (CorbeilleNode) value;
                corbeilleNode.setOpened(!corbeilleNode.isOpened());
            }
        }
    }

    @Observer("corbeilleChanged")
    public String setContext(CorbeilleNode item) throws ClientException {
        corbeilleActions.setMessageCriteriaFromRecherche(null);
        currentItem = item;

        // Vide le cache du contentView pour ré-executer la requête
        if (contentViewActions != null) {
            contentViewActions.reset(SolonEppContentView.CORBEILLE_MESSAGE_LIST_CONTENT_VIEW);
        }
        return corbeilleActions.navigateTo();
    }
    
    /**
     * Remet à zéro le contexte de la vue corbeille (plus de liste de message et de communication courante)
     * @return Vue corbeille
     * @throws ClientException
     */
    public String resetContext() throws ClientException {
        navigationContext.setCurrentDocument(null);
        return setContext(null);
    }
    
    /**
     * @return the currentItem
     */
    public CorbeilleNode getCurrentItem() {
        return currentItem;
    }

    /**
     * @param currentItem the currentItem to set
     */
    public void setCurrentItem(CorbeilleNode currentItem) {
        this.currentItem = currentItem;
    }

}
