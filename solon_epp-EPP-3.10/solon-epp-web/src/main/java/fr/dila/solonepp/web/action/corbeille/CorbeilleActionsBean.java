package fr.dila.solonepp.web.action.corbeille;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.model.NoSuchDocumentException;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.solonepp.api.constant.SolonEppActionConstant;
import fr.dila.solonepp.api.constant.SolonEppViewConstant;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.dto.VersionSelectionDTO;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.core.dao.MessageDao;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.client.MessageDTO;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

@Name("corbeilleActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK + 2)
public class CorbeilleActionsBean implements Serializable {
    private static final long serialVersionUID = -6601690797613742328L;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = false)
    protected transient ActionManager actionManager;

    @In(create = true)
    protected transient NavigationWebActionsBean navigationWebActions;

    @In(create = true, required = false)
    protected transient CorbeilleTreeBean corbeilleTree;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true, required = false)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected transient ContentViewActions contentViewActions;
    
    public static final String REFRESH_CORBEILLE = "REFRESH_CORBEILLE";


    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CorbeilleActionsBean.class);    

    private static final String VERSION_ERREUR_RECUPERATION = "version.erreur.recuperation";
    private static final String PIECE_JOINTE_ERREUR_RECUPERATION = "piece.jointe.erreur.recuperation";

    private DocumentModel currentVersionModel;

    private String idCurrentEvent;

    private List<VersionSelectionDTO> currentVersion;

    private Message currentMessage;

    private MessageCriteria messageCriteriaFromRecherche;

    private Boolean extendMessage = false;

    /**
     * Navigation vers l'espace de traitement.
     * 
     * @return Vue de l'espace de traitement
     * @throws ClientException
     */
    public String navigateTo() throws ClientException {


        LOGGER.debug(documentManager, STLogEnumImpl.NAVIGATION_TO_CORBEILLE_VIEW_TEC) ;
        
        reset();

        // Renseigne le menu du haut
        Action mainMenuAction = actionManager.getAction(SolonEppActionConstant.ESPACE_CORBEILLE);
        navigationWebActions.setCurrentMainMenuAction(mainMenuAction);

        // Renseigne le menu de gauche
        Action action = actionManager.getAction(SolonEppActionConstant.LEFT_MENU_ESPACE_CORBEILLE);
        navigationWebActions.setCurrentLeftMenuAction(action);

        // Renseigne le vue courante
        navigationContext.setCurrentView(SolonEppViewConstant.ESPACE_CORBEILLE_VIEW);

        contentViewActions.refresh(SolonEppViewConstant.CORBEILLE_CONTENT_VIEW);

        return SolonEppViewConstant.ESPACE_CORBEILLE_VIEW;
    }

    /**
     * reinitialise toutes les propriétés du bean
     * 
     * @throws ClientException
     */
    private void reset() throws ClientException {
        // Réinitialise le document courant
        navigationContext.resetCurrentDocument();

        idCurrentEvent = null;
        currentMessage = null;
        currentVersion = null;
        currentVersionModel = null;
    }

    /**
     * Retourne le message dao.
     * 
     * @return MessageDao
     * @throws ClientException
     */
    public MessageDao getMessageListDao() throws ClientException {

        MessageCriteria messageCriteria = null;
        if (messageCriteriaFromRecherche == null) {
            messageCriteria = new MessageCriteria();
            messageCriteria.setCheckReadPermission(true);
            messageCriteria.setCorbeilleMessageType(true);

            if (extendMessage) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.add(Calendar.DAY_OF_MONTH, -20);
                messageCriteria.setDateTraitementMin(cal);
            }

        } else {
            messageCriteria = messageCriteriaFromRecherche;
        }
        // on met l'id de la corbeille sélectionnée
        if (corbeilleTree.getCurrentItem() != null) {
            messageCriteria.setCorbeille(corbeilleTree.getCurrentItem().getName());
        } else {
            facesMessages.add(StatusMessage.Severity.INFO, "Vous devez sélectionner une corbeille");
        }
        messageCriteria.setJoinEvenementForSorting(true);
        messageCriteria.setJoinVersionForSorting(true);

        return new MessageDao(documentManager, messageCriteria);
    }

    /**
     * Retourne les critères de recherche des modèles de feuille de route sous forme textuelle.
     * 
     * @return Critères de recherche des modèles de feuille de route sous forme textuelle
     * @throws ClientException
     */
    public String getMessageListQueryString() throws ClientException {
        MessageDao messageDao = getMessageListDao();
        return messageDao.getQueryString();
    }

    /**
     * Retourne les critères de recherche des modèles de feuille de route sous forme textuelle.
     * 
     * @return Critères de recherche des modèles de feuille de route sous forme textuelle
     * @throws ClientException
     */
    public List<Object> getMessageListQueryParameter() throws ClientException {
        // reinitialise la vue courante à SolonEppViewConstant.ESPACE_CORBEILLE_VIEW
        navigationContext.setCurrentView(SolonEppViewConstant.ESPACE_CORBEILLE_VIEW);
        MessageDao messageDao = getMessageListDao();
        return messageDao.getParamList();
    }

    public String readMessageLink(String idMessage, String idEvenement, Boolean read) throws ClientException {
        return readMessageLink(idMessage, idEvenement, read, null);
    }

    public String readMessageLink(String idMessage, String idEvenement, Boolean read, String tabId) throws ClientException {
        DocumentModel evenementDoc = documentManager.getDocument(new IdRef(idEvenement));

        DocumentModel messageDoc = documentManager.getDocument(new IdRef(idMessage));
        currentMessage = messageDoc.getAdapter(Message.class);

        navigationContext.setCurrentDocument(evenementDoc);
        return null; // currentView;
    }

    /**
     * Recharge le message et l'evenement, met la version courante à null.
     * 
     * @return
     * @throws ClientException
     */
    public String refreshEvenement() throws ClientException {

        DocumentModel eventDoc = navigationContext.getCurrentDocument();
        if (eventDoc != null) {
            DocumentModel evenementDoc = documentManager.getDocument(eventDoc.getRef());
            DocumentModel messageDoc = documentManager.getDocument(currentMessage.getDocument().getRef());
            currentMessage = messageDoc.getAdapter(Message.class);
            navigationContext.setCurrentDocument(evenementDoc);
            currentVersion = null;
            currentVersionModel = null;
        }
        Events.instance().raiseEvent(REFRESH_CORBEILLE);
        contentViewActions.refresh(SolonEppViewConstant.CORBEILLE_CONTENT_VIEW);
        return SolonEppViewConstant.ESPACE_CORBEILLE_VIEW;
    }

    /**
     * retourne la liste des versions selectionnable par l'utilisateur de l'evenement en cours d'affichage
     * 
     * @return {@link VersionSelectionDTO}
     * @throws ClientException
     */
    public List<VersionSelectionDTO> getVisibleNumeroVersion() {
        if (currentMessage == null) {
        	String message = resourcesAccessor.getMessages().get(VERSION_ERREUR_RECUPERATION);
            LOGGER.error(documentManager, EppLogEnumImpl.FAIL_GET_VERSION_TEC, message);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return new ArrayList<VersionSelectionDTO>();
        } 

        if (currentVersion == null || !navigationContext.getCurrentDocument().getId().equals(idCurrentEvent)) {
            String messageType = currentMessage.getMessageType();
            try {
                currentVersion = SolonEppServiceLocator.getVersionService().findVersionSelectionnable(documentManager, navigationContext.getCurrentDocument(), messageType);
            } catch (ClientException exc) {
                String message = resourcesAccessor.getMessages().get(VERSION_ERREUR_RECUPERATION);
                LOGGER.error(documentManager, EppLogEnumImpl.FAIL_GET_VERSION_TEC, message);
                LOGGER.debug(documentManager, EppLogEnumImpl.FAIL_GET_VERSION_TEC, exc);
                facesMessages.add(StatusMessage.Severity.WARN, message);
                TransactionHelper.setTransactionRollbackOnly();
            }
        }
        idCurrentEvent = navigationContext.getCurrentDocument().getId();
        return currentVersion;
    }

    /**
     * retourne la version selectionner par l'utilisateur de l'evenement en cours d'affichage, la derniere par defaut
     * 
     * @return
     * @throws ClientException
     */
    public DocumentModel getSelectedVersion() throws ClientException {
    	// identifiant de l'évènement affiché qui est différent de l'identifiant de l'évènement en cours (qui est stocké dans ce bean)
        String idAffiche = navigationContext.getCurrentDocument().getId();
        // On récupère la dernière version action du document lorsqu'on affiche un nouveau document, sinon on ne fait rien
        if (currentVersionModel == null || idCurrentEvent!=null && !idCurrentEvent.equals(idAffiche)) {
        	String messageType = currentMessage.getMessageType();
        	DocumentModel doc = SolonEppServiceLocator.getVersionService().getVersionActive(documentManager, navigationContext.getCurrentDocument(), messageType);
	        currentVersionModel = doc;
	        currentVersion = null;
	        idCurrentEvent = navigationContext.getCurrentDocument().getId();
        } 
        
        return currentVersionModel;
        
    }

    public String getIdCurrentVersion() {
        if (currentVersionModel != null) {
            return currentVersionModel.getId();
        } else {
            return null;
        }

    }

    public Version getCurrentVersion() {
        if (currentVersionModel != null) {
            return currentVersionModel.getAdapter(Version.class);
        } else {
            return null;
        }

    }

    public String setCurrentVersion(VersionSelectionDTO versionSelectionDTO) {
    	
        if (versionSelectionDTO != null && StringUtils.isNotEmpty(versionSelectionDTO.getId())) {
            if (versionSelectionDTO.getVersion() == null) {
                try {
                    versionSelectionDTO.setVersion(SolonEppServiceLocator.getVersionService().findVersionByUIID(documentManager, versionSelectionDTO.getId()));

                } catch (ClientException e) {
                    String message = resourcesAccessor.getMessages().get(VERSION_ERREUR_RECUPERATION);
                    LOGGER.error(documentManager, EppLogEnumImpl.FAIL_GET_VERSION_TEC, message);
                    LOGGER.debug(documentManager, EppLogEnumImpl.FAIL_GET_VERSION_TEC, message, e);
                    facesMessages.add(StatusMessage.Severity.WARN, message);
                    TransactionHelper.setTransactionRollbackOnly();
                }
            }
            
            currentVersionModel = versionSelectionDTO.getVersion();
        }
        return null;
    }

    public Set<String> getListTypePieceJointe() {
        return getListTypePieceJointe(navigationContext.getCurrentDocument());
    }

    public Set<String> getListTypePieceJointe(DocumentModel document) {
        if (document != null) {
            Evenement evenement = document.getAdapter(Evenement.class);
            try {
                return SolonEppServiceLocator.getEvenementTypeService().getEvenementType(evenement.getTypeEvenement()).getOrderedPieceJointe().keySet();
            } catch (ClientException e) {
                String message = resourcesAccessor.getMessages().get(PIECE_JOINTE_ERREUR_RECUPERATION);                
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_PIECE_JOINTE_TEC,message,e) ;
                facesMessages.add(StatusMessage.Severity.WARN, message);
                TransactionHelper.setTransactionRollbackOnly();
            }
        }
        return null;
    }

    public Map<String, PieceJointeDescriptor> getMapTypePieceJointe(DocumentModel document) {
        if (document != null) {
            Evenement evenement = document.getAdapter(Evenement.class);
            try {
                return SolonEppServiceLocator.getEvenementTypeService().getEvenementType(evenement.getTypeEvenement()).getOrderedPieceJointe();
            } catch (ClientException e) {
                String message = resourcesAccessor.getMessages().get(PIECE_JOINTE_ERREUR_RECUPERATION);
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_PIECE_JOINTE_TEC,message,e) ;
                facesMessages.add(StatusMessage.Severity.WARN, message);
                TransactionHelper.setTransactionRollbackOnly();
            }
        }
        return null;
    }

    /**
     * @return the messageCriteriaFromRecherche
     */
    public MessageCriteria getMessageCriteriaFromRecherche() {
        return messageCriteriaFromRecherche;
    }

    /**
     * @param messageCriteriaFromRecherche the messageCriteriaFromRecherche to set
     */
    public void setMessageCriteriaFromRecherche(MessageCriteria messageCriteriaFromRecherche) {
        this.messageCriteriaFromRecherche = messageCriteriaFromRecherche;
    }

    /**
     * @return the currentMessage
     */
    public Message getCurrentMessage() {
        return currentMessage;
    }

    /**
     * @param currentMessage the currentMessage to set
     */
    public void setCurrentMessage(Message currentMessage) {
        this.currentMessage = currentMessage;
    }

    public String getStyleForMessageLine(MessageDTO message) {
        StringBuilder style = new StringBuilder();
        if (message != null) {
            if (MessageDTO.EN_ATTENTE_VALIDATION_ANNULATION.equals(message.getEtatEvenement()) || MessageDTO.ANNULER.equals(message.getEtatEvenement())) {
                style.append("text-decoration:line-through;");
            } else if (MessageDTO.NON_TRAITE.equals(message.getEtatMessage()) || MessageDTO.EN_COURS_REDACTION.equals(message.getEtatMessage())) {
                style.append("font-weight:bold;");
            } else if (MessageDTO.EN_COURS_TRAITEMENT.equals(message.getEtatMessage()) || MessageDTO.EN_ATTENTE_AR.equals(message.getEtatMessage())) {
                style.append("font-style:italic;");
            }
            if (MessageDTO.EN_ALERTE.equals(message.getEtatDossier())) {
                style.append("color:red;");
            }
        }
        return style.toString();
    }

    /**
     * @return the extendMessage
     */
    public Boolean getExtendMessage() {
        return extendMessage;
    }

    /**
     * @param extendMessage the extendMessage to set
     */
    public void setExtendMessage(Boolean extendMessage) {
        this.extendMessage = extendMessage;
    }

    /**
     * rafaichissement de la corbeille
     */
    @Observer(REFRESH_CORBEILLE)
    public void refreshCorbeille() throws ClientException {
        corbeilleTree.forceRefresh();
    }

    @SuppressWarnings("unchecked")
    public boolean oneMessageInList(String contentViewName) throws ClientException {
        if (StringUtils.isEmpty(contentViewName)) {
            return false;
        }
        PageProvider<MessageDTO> pageProvider = (PageProvider<MessageDTO>) contentViewActions.getContentViewWithProvider(contentViewName).getCurrentPageProvider();
        if (pageProvider.getResultsCount() != 1L) {
            return false;
        }
        // on récupère le premier message de la page car il n'y a en a qu'un.
        MessageDTO messageDto = pageProvider.getCurrentPage().get(0);
        String messageId = messageDto.getIdMessage();
        if (navigationContext.getCurrentDocument() != null && navigationContext.getCurrentDocument().getId().equals(messageId)) {
            // le message est déjà sélectionné, on ne rien de plus
            return true;
        }
        // on sélectionne le message de la liste et on rècupère son messageLink
        pageProvider.setCurrentEntry(messageDto);
        try {
            readMessageLink(messageId, messageDto.getUidEvenement(), true);
        } catch (ClientException clientException) {
            if (ExceptionUtils.getRootCause(clientException) instanceof NoSuchDocumentException) {
                // le document a été supprimé par un autre utilisateur ou l'utilisateur n'a plus les droits pour voir le document : on le signale à l'utilisateur sans renvoyer une exception
                String errorMessage = resourcesAccessor.getMessages().get(STConstant.NO_SUCH_DOCUMENT_ERROR_MSG);
                facesMessages.add(StatusMessage.Severity.WARN, errorMessage);
                return false;
            }
            throw clientException;
        }
        return true;
    }

}
