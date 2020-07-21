package fr.dila.solonepp.web.action.historique;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.richfaces.component.UITree;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.action.recherche.RechercheDocumentaireActionsBean;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.context.NavigationContextBean;

@Name("historiqueDossierTree")
@Scope(ScopeType.SESSION)
public class HistoriqueDossierTreeBean implements Serializable {

    /**
     * uid
     */
    private static final long serialVersionUID = 8164374428143449458L;

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(HistoriqueDossierTreeBean.class);    

    @In(create = true, required = false)
    protected CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = false)
    protected transient RechercheDocumentaireActionsBean rechercheDocumentaireActions;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    private TreeNode<HistoriqueDataNode> historiqueTree;

    private DocumentModel currentDossier;

    private DocumentModel currentEvent;

    /**
     * @return the historiqueTree
     * @throws ClientException
     */
    public TreeNode<HistoriqueDataNode> getHistoriqueTree() throws ClientException {

        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        DocumentModel evenementDoc = navigationContext.getCurrentDocument();
        Evenement evtCourant = evenementDoc.getAdapter(Evenement.class);
        DocumentModel dossierDoc = dossierService.getDossier(documentManager, evtCourant.getDossier());

        if (historiqueTree == null || currentEvent == null || evenementDoc.getId() != currentEvent.getId()) {
            currentEvent = evenementDoc;
            currentDossier = dossierDoc;
            loadTree();
        }

        return historiqueTree;
    }

    /**
     * Charge l'historique du dossier
     * 
     * @throws ClientException
     */
    private void loadTree() throws ClientException {

        historiqueTree = new TreeNodeImpl<HistoriqueDataNode>();

        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        
        LOGGER.debug(documentManager, STLogEnumImpl.LOADING_DOSSIER_HISTORY_TEC, "dossier : " + currentDossier.getTitle()) ;
        
        Map<DocumentModel,DocumentModel> evtsRacinesDoc = getEvenementNodes(null);
        
        RootNodeIndex rootIndex = new RootNodeIndex();
        if(evtsRacinesDoc==null){
          return;
        }
        for (DocumentModel evtRacineDoc : evtsRacinesDoc.keySet()) {
            Evenement evtRacine = evtRacineDoc.getAdapter(Evenement.class);
            DocumentModel messageDoc = evtsRacinesDoc.get(evtRacineDoc);
            
            if (messageDoc != null) {
              Message message = messageDoc.getAdapter(Message.class);
              DocumentModel versionDoc = versionService.getVersionActive(documentManager, evtRacine.getDocument(), message.getMessageType());
              
              if (versionDoc == null) {
                  continue;
              }
              
              Version version = versionDoc.getAdapter(Version.class);
              
              String label = getEvenementTypeLabel(evtRacine.getTypeEvenement());
              
              TreeNode<HistoriqueDataNode> node = new TreeNodeImpl<HistoriqueDataNode>();
              HistoriqueDataNode dataNode = new HistoriqueDataNode(label, isEvenementCourant(evtRacineDoc), evtRacine.isEtatAnnule(), evtRacine.getTitle());
              node.setData(dataNode);
              historiqueTree.addChild(rootIndex.index, node);
              
              rootIndex.index++;
              rootIndex.node = historiqueTree;
              
              addChildren(rootIndex, node, evtRacine, version);
            }
        }
    }
    
    private Map<DocumentModel,DocumentModel> getEvenementNodes(String parentNode) throws ClientException {
      final EvenementService evtService = SolonEppServiceLocator.getEvenementService();
      final MessageService messageService = SolonEppServiceLocator.getMessageService();
      Map<DocumentModel, DocumentModel> evenementList = new HashMap<DocumentModel,DocumentModel>();
      List<DocumentModel> evtSuccessifList = null;
      Map<DocumentModel, DocumentModel> tempList;
      if(parentNode==null) {
        evtSuccessifList = evtService.getEvenementsRacineDuDossier(documentManager, currentDossier);
      }
      else {
        evtSuccessifList = evtService.findEvenementSuccessif(documentManager, parentNode);
      }
      if(evtSuccessifList.size()==0) {
        return null;
      }
      for (DocumentModel evtSuccessifDoc : evtSuccessifList) {
        DocumentModel messageDoc = messageService.getMessageByEvenementId(documentManager, evtSuccessifDoc.getTitle());
        if(messageDoc==null) {
          // Si le message est null, c'est que l'utilisateur n'est pas autorisé à le voir.
          //chercher parmis les evenements successif
          tempList = getEvenementNodes(evtSuccessifDoc.getTitle());
          if(tempList!=null) {
            evenementList.putAll(tempList);
          }
        }
        else {
          evenementList.put(evtSuccessifDoc, messageDoc);
        }
      }
      return evenementList;
    }

    /**
     * Ajout des évènements enfants
     * 
     * @param evtNodeParent
     * @param evtParent
     * @throws ClientException
     */
    private void addChildren(RootNodeIndex rootNodeIndex, TreeNode<HistoriqueDataNode> evtNodeParent, Evenement evtParent, Version verParent) throws ClientException {
        int index = 0;

        final VersionService versionService = SolonEppServiceLocator.getVersionService();
        
        Map<DocumentModel,DocumentModel> evtSuccessifList = getEvenementNodes(evtParent.getTitle());
        boolean isRootNode = false;
        if(evtSuccessifList==null) {
          return;
        }
        for (DocumentModel evtSuccessifDoc : evtSuccessifList.keySet()) {
            TreeNode<HistoriqueDataNode> evtNode = new TreeNodeImpl<HistoriqueDataNode>();
            Evenement evtSuccessif = evtSuccessifDoc.getAdapter(Evenement.class);
            isRootNode = false;
            DocumentModel messageDoc = evtSuccessifList.get(evtSuccessifDoc);
            Version versionSucc = null;
            if (messageDoc != null) {
                Message message = messageDoc.getAdapter(Message.class); 
                DocumentModel versionSuccDoc = versionService.getVersionActive(documentManager, evtSuccessif.getDocument(), message.getMessageType());
                if (versionSuccDoc != null && verParent != null) {
                    versionSucc = versionSuccDoc.getAdapter(Version.class);
                
                    if (verParent.getNiveauLecture() != null && versionSucc.getNiveauLecture() != null) {
                        if (!verParent.getNiveauLecture().equals(versionSucc.getNiveauLecture())) {
                            isRootNode=true;
                        } else if ( verParent.getNiveauLectureNumero() != null && versionSucc.getNiveauLectureNumero() != null 
                                &&  !verParent.getNiveauLectureNumero().equals(versionSucc.getNiveauLectureNumero())) {
                            isRootNode=true;
                        }
                    }
                }
                
                String label = getEvenementTypeLabel(evtSuccessif.getTypeEvenement());
                HistoriqueDataNode dataNode = new HistoriqueDataNode(label, isEvenementCourant(evtSuccessifDoc), evtSuccessif.isEtatAnnule(), evtSuccessif.getTitle());
                evtNode.setData(dataNode);
                if(isRootNode) {
                    index = rootNodeIndex.index;
                    rootNodeIndex.index++;
                    rootNodeIndex.node.addChild(index, evtNode);
                }
                else {
                    evtNodeParent.addChild(index, evtNode);
                }
                index++;
                addChildren(rootNodeIndex, evtNode, evtSuccessif, versionSucc);
            }
        }
    }

    /**
     * Retourne le label de l'évènement
     * 
     * @param evenementType
     * @return
     * @throws ClientException
     */
    private String getEvenementTypeLabel(String evenementType) throws ClientException {
        final EvenementTypeService evtTypeService = SolonEppServiceLocator.getEvenementTypeService();
        EvenementTypeDescriptor evtTypeDesc = evtTypeService.getEvenementType(evenementType);
        return evtTypeDesc.getLabel();
    }

    public Boolean adviseNodeOpened(UITree tree) {
        return Boolean.TRUE;
    }

    private Boolean isEvenementCourant(DocumentModel evenementDoc) {
        DocumentModel evenementCourantDoc = navigationContext.getCurrentDocument();
        return evenementCourantDoc.getId().equals(evenementDoc.getId());
    }

    /**
     * Navigation vers la recherche suite au clic que un noeud
     * @param historiqueDataNode
     * @return
     */
    public String navigateTo(HistoriqueDataNode historiqueDataNode) {
        if (historiqueDataNode != null) {
            try {
                // navigation vers la recherche (UC-DOS-22)
                rechercheDocumentaireActions.navigateTo();
//                rechercheDocumentaireActions.resetCriteria();
                rechercheDocumentaireActions.setIdEventDirect(historiqueDataNode.getIdEvenement());
                return rechercheDocumentaireActions.SearchEventById();
            } catch (ClientException e) {                
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_NAVIGATE_TO_COMMUNICATION_TEC,e) ;
                facesMessages.add(StatusMessage.Severity.WARN, "Navigation vers la communication " + historiqueDataNode.getIdEvenement()
                        + "impossible");
                TransactionHelper.setTransactionRollbackOnly();
            }
        }
        return null;
    }

    private class RootNodeIndex {
        public Integer index = 0;
        public TreeNode<HistoriqueDataNode> node;
    }
    
}
