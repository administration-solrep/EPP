package fr.dila.solonepp.web.action.evenement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppViewConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementService;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.api.service.PieceJointeService;
import fr.dila.solonepp.api.service.VersionCreationService;
import fr.dila.solonepp.api.service.VersionService;
import fr.dila.solonepp.api.service.evenement.AnnulerEvenementContext;
import fr.dila.solonepp.api.service.evenement.AnnulerEvenementRequest;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementContext;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementRequest;
import fr.dila.solonepp.api.service.evenement.InitialiserEvenementResponse;
import fr.dila.solonepp.api.service.version.AccuserReceptionContext;
import fr.dila.solonepp.api.service.version.AccuserReceptionRequest;
import fr.dila.solonepp.api.service.version.CreerVersionContext;
import fr.dila.solonepp.api.service.version.CreerVersionRequest;
import fr.dila.solonepp.api.service.version.CreerVersionResponse;
import fr.dila.solonepp.api.service.version.ValiderVersionContext;
import fr.dila.solonepp.api.service.version.ValiderVersionRequest;
import fr.dila.solonepp.core.assembler.ws.evenement.BaseAssembler;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.core.validator.EvenementValidator;
import fr.dila.solonepp.core.validator.PieceJointeValidator;
import fr.dila.solonepp.core.validator.VersionValidator;
import fr.dila.solonepp.web.action.corbeille.CorbeilleActionsBean;
import fr.dila.solonepp.web.action.metadonnees.MetaDonneesActionsBean;
import fr.dila.solonepp.web.action.recherche.RechercheDocumentaireActionsBean;
import fr.dila.solonepp.web.contentview.ProviderBean;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.VocabularyServiceImpl;
import fr.dila.st.web.context.NavigationContextBean;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * Actions sur les événements.
 * 
 * @author jtremeaux
 */
@Name("evenementActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class EvenementActionsBean implements Serializable {

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -8856979171092339852L;

    private static final String TYPE_EVENEMENT_VIDE = "create.evenement.type.evenement.vide";
    private static final String EVENEMENT_PRECEDENT = "create.evenement.evenement.precedent.vide";
    private static final String EVENEMENT_ENREGISTRE = "create.evenement.ok";
    private static final String EVENEMENT_PUBLIE = "publier.evenement.ok";
    private static final String EVENEMENT_RECTIFIER = "rectifier.evenement.ok";
    private static final String EVENEMENT_RECTIFIER_ATTENTE_VALIDATION = "rectifier.evenement.AttenteValidation";
    private static final Object EVENEMENT_COMPLETER = "completer.evenement.ok";
    private static final Object EVENEMENT_VALIDER = "evenement.valider.ok";
    private static final Object EVENEMENT_REJETER = "evenement.rejeter.ok";
    private static final Object EVENEMENT_MODIFIER = "modifier.evenement.ok";
    private static final String EVENEMENT_CANT_LOCK = "evenement.cant.lock";
    private static final String EVENEMENT_LOCK_LOST = "evenement.lock.lost";
    private static final String COMPETER_META_OBLIGATOIRES = "label.epp.meta.obligatoires";

    private static final String MAIL_OBJET = "objetMail";
    private static final String MAIL_DESTINATAIRE = "destinataireMail";
    private static final String MAIL_MESSAGE = "messageMail";

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true, required = false)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = false)
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(create = true, required = false)
    protected transient MetaDonneesActionsBean metadonneesActions;

    @In(create = true, required = false)
    protected transient RechercheDocumentaireActionsBean rechercheDocumentaireActions;

    @In(required = true, create = true)
    protected EppPrincipal eppPrincipal;
    
    @In(create = true, required = false)
    protected transient EvenementTypeActionsBean evenementTypeActions;

    @RequestParameter("enregistrerBrouillon")
    protected String enregistrerBrouillon;

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(EvenementActionsBean.class);    

    /**
     * typeEvenement courant pour la creation
     */
    private String currentTypeEvenement;

    /**
     * typeEvenement courant pour la creation d'un evenement successif
     */
    private String currentTypeEvenementSuccessif;

    /**
     * evenement de creation courant
     */
    private DocumentModel currentEvenementForCreation;

    /**
     * version de creation courante
     */
    private DocumentModel currentVersionForCreation;

    private List<PieceJointe> pieceJointeList;

    private Map<String, String> mailEvenement = new HashMap<String, String>();

    private boolean showRemoveLockPopup = false;

    protected boolean displayRequired = false;

    public String navigateToCreationCommunication() throws ClientException {
        displayRequired = false;
        metadonneesActions.setCurrentLayoutMode(null);
        navigationContext.resetCurrentDocument();

        if (StringUtils.isEmpty(currentTypeEvenement)) {
            final String message = resourcesAccessor.getMessages().get(TYPE_EVENEMENT_VIDE);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        final InitialiserEvenementContext initialiserEvenementContext = new InitialiserEvenementContext();
        final InitialiserEvenementRequest initialiserEvenementRequest = initialiserEvenementContext.getInitialiserEvenementRequest();
        initialiserEvenementRequest.setTypeEvenement(currentTypeEvenement);

        currentTypeEvenementSuccessif = null;

        // initialisation des métadonnees de l'evenement
        try {
            SolonEppServiceLocator.getEvenementService().initialiserEvenement(documentManager, initialiserEvenementContext);
        } catch (final ClientException e) {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_COMM_TEC, e);
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        final InitialiserEvenementResponse initialiserEvenementResponse = initialiserEvenementContext.getInitialiserEvenementResponse();

        currentEvenementForCreation = initialiserEvenementResponse.getEvenementDoc();
        currentVersionForCreation = initialiserEvenementResponse.getVersionDoc();

        pieceJointeList = new ArrayList<PieceJointe>();
        
		final EvenementTypeDescriptor descriptor = SolonEppServiceLocator.getEvenementTypeService()
				.getEvenementType(currentTypeEvenement);
		if (descriptor != null) {
			final Map<String, PieceJointeDescriptor> map = descriptor.getPieceJointe();
			if (map != null) {
				for (PieceJointeDescriptor pieceJointeDescriptor : map.values()) {
					if (pieceJointeDescriptor != null && pieceJointeDescriptor.isInitToOne()) {
						// On initialise ce container avec une valeur
						addPieceJointe(pieceJointeDescriptor.getType());
					}
				}
			}
		}

        // Renseigne le vue courante
        navigationContext.setCurrentView(SolonEppViewConstant.VIEW_CREATE_EVENEMENT);

        return SolonEppViewConstant.VIEW_CREATE_EVENEMENT;

    }

    public String navigateToCreationCommunicationSuccessive() throws ClientException {
        displayRequired = false;
        metadonneesActions.setCurrentLayoutMode(null);

        if (StringUtils.isEmpty(currentTypeEvenementSuccessif)) {
            final String message = resourcesAccessor.getMessages().get(TYPE_EVENEMENT_VIDE);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        // check verrou
        if (!checkLockMessage()) {
            return null;
        }

        currentTypeEvenement = null;

        String idEvenementPrecedent = null;
        if (navigationContext.getCurrentDocument() != null) {
            idEvenementPrecedent = navigationContext.getCurrentDocument().getAdapter(Evenement.class).getTitle();
        } else {
            final String message = resourcesAccessor.getMessages().get(EVENEMENT_PRECEDENT);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        final InitialiserEvenementContext initialiserEvenementContext = new InitialiserEvenementContext();
        final InitialiserEvenementRequest initialiserEvenementRequest = initialiserEvenementContext.getInitialiserEvenementRequest();
        initialiserEvenementRequest.setTypeEvenement(currentTypeEvenementSuccessif);
        initialiserEvenementRequest.setIdEvenementPrecedent(idEvenementPrecedent);

        // initialisation des métadonnees de l'evenement
        try {
            SolonEppServiceLocator.getEvenementService().initialiserEvenement(documentManager, initialiserEvenementContext);

        } catch (final ClientException e) {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_COMM_TEC, e);
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        final InitialiserEvenementResponse initialiserEvenementResponse = initialiserEvenementContext.getInitialiserEvenementResponse();

        currentEvenementForCreation = initialiserEvenementResponse.getEvenementDoc();
        currentVersionForCreation = initialiserEvenementResponse.getVersionDoc();

        pieceJointeList = new ArrayList<PieceJointe>();

        // Renseigne le vue courante
        navigationContext.setCurrentView(SolonEppViewConstant.VIEW_CREATE_EVENEMENT_SUCCESSIF);

        return SolonEppViewConstant.VIEW_CREATE_EVENEMENT_SUCCESSIF;

    }

    public DocumentModel getCurrentEvenementForCreation() {
        return currentEvenementForCreation;
    }

    public DocumentModel getCurrentVersionForCreation() {
        return currentVersionForCreation;
    }

    public String getCurrentTypeEvenement() {
        return currentTypeEvenement;
    }

    public void setCurrentTypeEvenement(final String currentTypeEvenement) {
        this.currentTypeEvenement = currentTypeEvenement;
    }

    public String cancelCreationEvenement() throws ClientException {
        unlockCurrentMessage();
        // Renseigne le vue courante
        navigationContext.setCurrentView(null);
        resetCreation();
        return corbeilleActions.navigateTo();
    }

    public String cancelCreationEvenementSuccessif() throws ClientException {
        unlockCurrentMessage();
        resetCreation();
        return corbeilleActions.refreshEvenement();
    }

    private boolean checkMetadonnees(final boolean publie) {
        try {
            final EvenementValidator evtValidator = new EvenementValidator(documentManager);
            evtValidator.validateMetaObligatoire(currentEvenementForCreation, publie);
        } catch (final ClientException e) {
            final String message = resourcesAccessor.getMessages().get(COMPETER_META_OBLIGATOIRES);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            displayRequired = true;
            return false;
        }

        if (publie == true) {
            try {
                final VersionValidator versionValidator = new VersionValidator();
                versionValidator.validateMetaObligatoire(currentVersionForCreation, currentEvenementForCreation);

                // check piece jointe
                String type = currentTypeEvenement;
                if (currentTypeEvenement == null) {
                    type = currentTypeEvenementSuccessif;
                }
                final EvenementTypeDescriptor descriptor = SolonEppServiceLocator.getEvenementTypeService().getEvenementType(type);
                for (final String typePieceJointe : descriptor.getPieceJointe().keySet()) {
                    if (descriptor.getPieceJointe().get(typePieceJointe).isObligatoire()) {
                        if (getListPieceJointe(typePieceJointe) == null || getListPieceJointe(typePieceJointe).isEmpty()) {
                            final String message = resourcesAccessor.getMessages().get(COMPETER_META_OBLIGATOIRES);
                            facesMessages.add(StatusMessage.Severity.WARN, message);
                            displayRequired = true;
                            return false;
                        }
                    }
                }
            } catch (final ClientException e) {
                final String message = resourcesAccessor.getMessages().get(COMPETER_META_OBLIGATOIRES);
                facesMessages.add(StatusMessage.Severity.WARN, message);
                facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
                displayRequired = true;
                return false;
            }
        }
        return true;
    }
    

    public String saveCreationEvenement(final boolean publie) {

        // validation des metas required
        if (!checkMetadonnees(publie)) {
            return null;
        }

        try {

            final CreerVersionContext creerVersionContext = new CreerVersionContext();
            creerVersionContext.setPublie(publie);

            final CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            creerVersionRequest.setEvenementDoc(currentEvenementForCreation);
            creerVersionRequest.setVersionDoc(currentVersionForCreation);

            // initialisation du dossier
            final Version version = currentVersionForCreation.getAdapter(Version.class);

            final DocumentModel dossierDoc = SolonEppServiceLocator.getDossierService().createBareDossier(documentManager);
            final Dossier dossier = dossierDoc.getAdapter(Dossier.class);
            final Evenement event = currentEvenementForCreation.getAdapter(Evenement.class);

            if (StringUtils.isNotBlank(version.getNor())) {
                dossier.setTitle(version.getNor());
                event.setDossier(version.getNor());
            } else {
                dossier.setTitle(event.getDossier());
                version.setNor(event.getDossier());
            }

            creerVersionRequest.setDossierDoc(dossierDoc);

            for (final PieceJointe pieceJointe : pieceJointeList) {
                creerVersionRequest.getPieceJointeDocList().add(pieceJointe.getDocument());
            }

            if (!checkPieceJointe(creerVersionRequest.getPieceJointeDocList(), publie, event)) {
                return null;
            }

            final VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
            versionCreationService.createVersion(documentManager, creerVersionContext);

            String message = null;
			// Vérification pour la FEV 549 : Informations parlementaires si l'événement a été publié quand c'est la demande de l'utilisateur
			// sinon un message d'erreur est affiché ainsi que le message de création du brouillon
			if ("EVT45".equals(evenementTypeActions.getCurrentEvtTypeDescriptor().getName()) && publie) {
				if (creerVersionContext.isPublier()) {
					message = resourcesAccessor.getMessages().get(EVENEMENT_PUBLIE);
				} else {
					message = resourcesAccessor.getMessages().get(EVENEMENT_ENREGISTRE);
					facesMessages.add(StatusMessage.Severity.WARN, "Publication KO dans EPG");
				}
			} else {
				if (publie) {
					message = resourcesAccessor.getMessages().get(EVENEMENT_PUBLIE);
				} else {
					message = resourcesAccessor.getMessages().get(EVENEMENT_ENREGISTRE);
				}
            }
            facesMessages.add(StatusMessage.Severity.INFO, message);

            resetCreation();

            // refresh du provider
            Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);

            // refresh de la corbeille courante
            Events.instance().raiseEvent(CorbeilleActionsBean.REFRESH_CORBEILLE);

            final String idEvent = creerVersionContext.getCreerVersionResponse().getEvenementDoc().getAdapter(Evenement.class).getTitle();
            final String typeEvt = creerVersionContext.getCreerVersionResponse().getEvenementDoc().getAdapter(Evenement.class).getTypeEvenement();

            rechercheDocumentaireActions.resetCriteria();

            rechercheDocumentaireActions.setIdEvent(idEvent);
            rechercheDocumentaireActions.getTypeEvenement().add(typeEvt);
            return rechercheDocumentaireActions.search();

        } catch (final ClientException e) {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_COMM_TEC, e.getMessage());
            LOGGER.debug(documentManager, STLogEnumImpl.FAIL_CREATE_COMM_TEC, e);
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();

            return null;

        }

    }

    public String saveCreationEvenementSuccessif(final boolean publie) {

        // validation des metas required
        if (!checkMetadonnees(publie)) {
            return null;
        }

        try {
            // check verrou
            if (!checkLockMessage()) {
                final String message = resourcesAccessor.getMessages().get(EVENEMENT_LOCK_LOST);
                facesMessages.add(StatusMessage.Severity.WARN, message);
                return null;
            }

            final Version version = currentVersionForCreation.getAdapter(Version.class);
            if (!BaseAssembler.hasNumeroLecture(version.getNiveauLecture())) {
                version.setNiveauLectureNumero(null);
            }
            
            final CreerVersionContext creerVersionContext = new CreerVersionContext();
            creerVersionContext.setPublie(publie);

            final CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            creerVersionRequest.setEvenementDoc(currentEvenementForCreation);
            creerVersionRequest.setVersionDoc(version.getDocument());

            // initialisation du dossier
            final Evenement event = currentEvenementForCreation.getAdapter(Evenement.class);

            final DocumentModel dossierDoc = SolonEppServiceLocator.getDossierService().getDossier(documentManager, event.getDossier());

            creerVersionRequest.setDossierDoc(dossierDoc);

            for (final PieceJointe pieceJointe : pieceJointeList) {
                creerVersionRequest.getPieceJointeDocList().add(pieceJointe.getDocument());
            }

            if (!checkPieceJointe(creerVersionRequest.getPieceJointeDocList(), publie, event)) {
                return null;
            }

            final VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
            versionCreationService.createVersion(documentManager, creerVersionContext);

            String message = null;
            if (publie) {
                message = resourcesAccessor.getMessages().get(EVENEMENT_PUBLIE);
            } else {
                message = resourcesAccessor.getMessages().get(EVENEMENT_ENREGISTRE);
            }
            facesMessages.add(StatusMessage.Severity.INFO, message);

            resetCreation();

            // refresh du provider
            Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);

            // refresh de la corbeille courante
            Events.instance().raiseEvent(CorbeilleActionsBean.REFRESH_CORBEILLE);

            unlockCurrentMessage();

            final String idEvent = creerVersionContext.getCreerVersionResponse().getEvenementDoc().getAdapter(Evenement.class).getTitle();
            final String typeEvt = creerVersionContext.getCreerVersionResponse().getEvenementDoc().getAdapter(Evenement.class).getTypeEvenement();

            rechercheDocumentaireActions.resetCriteria();

            rechercheDocumentaireActions.setIdEvent(idEvent);
            rechercheDocumentaireActions.getTypeEvenement().add(typeEvt);
            return rechercheDocumentaireActions.search();

        } catch (final ClientException e) {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_COMM_TEC, e);
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();

            return null;

        }

    }

    /**
     * Retourne true si toutes les pièces jointes comportent des fichiers. En mode brouillon, retourne toujours true.
     * 
     * @param listDocumentModel
     * @param publie
     * @return
     * @throws ClientException
     */
    private boolean checkPieceJointe(final List<DocumentModel> listDocumentModel, final boolean publie, Evenement evenement) throws ClientException {
    	final PieceJointeValidator pieceJointeValidator = new PieceJointeValidator();
    	pieceJointeValidator.validatePiecesJointes(listDocumentModel, !publie, evenement.getTypeEvenement());
    	return true;
    }

    private void resetCreation() {
        // Renseigne le vue courante
        navigationContext.setCurrentView(null);
        currentEvenementForCreation = null;
        currentVersionForCreation = null;
        currentTypeEvenement = null;
        currentTypeEvenementSuccessif = null;
        metadonneesActions.setCurrentLayoutMode(null);
        evenementTypeActions.setCurrentEvtTypeDescriptor(null);
    }

    public String getCurrentTypeLibelle() throws ClientException {
        if (currentTypeEvenement != null) {
            final EvenementTypeDescriptor descriptor = SolonEppServiceLocator.getEvenementTypeService().getEvenementType(currentTypeEvenement);
            if (descriptor != null) {
                return descriptor.getLabel();
            }
        }

        if (currentTypeEvenementSuccessif != null) {
            final EvenementTypeDescriptor descriptor = SolonEppServiceLocator.getEvenementTypeService().getEvenementType(
                    currentTypeEvenementSuccessif);
            if (descriptor != null) {
                return descriptor.getLabel();
            }
        }
        return null;
    }

    public boolean isEvenementAlerte() {
        if (currentTypeEvenementSuccessif != null) {
            final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
            return evenementTypeService.isEvenementTypeAlerte(currentTypeEvenementSuccessif);
        }
        return false;
    }

    public PieceJointe addPieceJointe(final String pieceJointeType) throws ClientException {

        String titlePieceJointe = "";
        String type = currentTypeEvenement;
        if (currentTypeEvenement == null) {
            type = currentTypeEvenementSuccessif;
        }
        final EvenementTypeDescriptor descriptor = SolonEppServiceLocator.getEvenementTypeService().getEvenementType(type);
        if (descriptor != null) {
            final Map<String, PieceJointeDescriptor> map = descriptor.getPieceJointe();
            if (map != null) {
                final PieceJointeDescriptor pieceJointeDescriptor = map.get(pieceJointeType);
                if (pieceJointeDescriptor != null) {
                    if (!pieceJointeDescriptor.isMultivalue() && getListPieceJointe(pieceJointeType).size() > 0) {
                        return null;
                    }
                    titlePieceJointe = pieceJointeDescriptor.getLabel();
                }
            }
        }

        if (!StringUtils.isNotBlank(titlePieceJointe)) {
            titlePieceJointe = STServiceLocator.getVocabularyService().getEntryLabel(SolonEppVocabularyConstant.VOCABULARY_PIECE_JOINTE_DIRECTORY,
                    pieceJointeType);
            if (VocabularyServiceImpl.UNKNOWN_ENTRY.equals(titlePieceJointe)) {
                titlePieceJointe = pieceJointeType;
            }
        }

        titlePieceJointe = titlePieceJointe.replace("(s)", "");
        final DocumentModel doc = SolonEppServiceLocator.getPieceJointeService().createBarePieceJointe(documentManager);
        final PieceJointe pj = doc.getAdapter(PieceJointe.class);
        pj.setNom(titlePieceJointe);
        pj.setTypePieceJointe(pieceJointeType);
        pieceJointeList.add(pj);

        return pj;
    }

    public void removePieceJointe(final PieceJointe pieceJointe) {
        pieceJointeList.remove(pieceJointe);
    }

    public String addFile(final PieceJointe pj) throws ClientException {
        final DocumentModel doc = SolonEppServiceLocator.getPieceJointeFichierService().createBarePieceJointeFichier(documentManager);
        pj.getPieceJointeFichierDocList().add(doc);
        return null;
    }

    public List<PieceJointe> getListPieceJointe(final String type) {
        final List<PieceJointe> listPieceJointe = new ArrayList<PieceJointe>();
        for (final PieceJointe pieceJointe : pieceJointeList) {
            if (pieceJointe.getTypePieceJointe().equals(type)) {
                listPieceJointe.add(pieceJointe);
            }
        }
        return listPieceJointe;
    }

    public void removePieceJointeFichier(final PieceJointe pieceJointe, final DocumentModel fichier) {
        pieceJointe.getPieceJointeFichierDocList().remove(fichier);

    }

    public void setCurrentTypeEvenementSuccessif(final String currentTypeEvenementSuccessif) {
        this.currentTypeEvenementSuccessif = currentTypeEvenementSuccessif;
    }

    public String getCurrentTypeEvenementSuccessif() {
        return currentTypeEvenementSuccessif;
    }

    public String rectifierEvenement() throws ClientException {

        currentTypeEvenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement();
        if (StringUtils.isEmpty(currentTypeEvenement)) {
            final String message = resourcesAccessor.getMessages().get(TYPE_EVENEMENT_VIDE);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        // cherck verrou
        if (!checkLockMessage()) {
            return null;
        }

        currentEvenementForCreation = navigationContext.getCurrentDocument();
        currentVersionForCreation = corbeilleActions.getSelectedVersion();

        // chargement des pièces jointes
        final PieceJointeService pjService = SolonEppServiceLocator.getPieceJointeService();
        final List<DocumentModel> pjDocList = pjService.getVersionPieceJointeList(documentManager, currentVersionForCreation);
        pieceJointeList = new ArrayList<PieceJointe>();
        for (final DocumentModel pjDoc : pjDocList) {
            final PieceJointe pj = pjDoc.getAdapter(PieceJointe.class);
            pieceJointeList.add(pj);

            final List<DocumentModel> pjfList = SolonEppServiceLocator.getPieceJointeFichierService().findAllPieceJointeFichier(documentManager,
                    pjDoc);
            pj.setPieceJointeFichierDocList(pjfList);
        }

        metadonneesActions.setCurrentLayoutMode(MetaDonneesActionsBean.LAYOUT_MODE_RECTIFIER);

        // Renseigne le vue courante
        navigationContext.setCurrentView(SolonEppViewConstant.VIEW_RECTIFIER_EVENEMENT);
        // refresh de la corbeille courante
        Events.instance().raiseEvent(CorbeilleActionsBean.REFRESH_CORBEILLE);
        return SolonEppViewConstant.VIEW_RECTIFIER_EVENEMENT;
    }

    public String cancelRectifierEvenement() throws ClientException {
        unlockCurrentMessage();
		return corbeilleActions.refreshEvenement();
    }

    public String saveRectifierEvenement(final boolean publier) {

        // validation des metas required
        if (!checkMetadonnees(publier)) {
            return null;
        }

        try {
            // check verrou
            if (!checkLockMessage()) {
                final String message = resourcesAccessor.getMessages().get(EVENEMENT_LOCK_LOST);
                facesMessages.add(StatusMessage.Severity.WARN, message);
                return null;
            }

            final CreerVersionContext creerVersionContext = new CreerVersionContext();
            final CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            creerVersionRequest.setEvenementDoc(currentEvenementForCreation);
            creerVersionRequest.setVersionDoc(currentVersionForCreation);

            for (final PieceJointe pieceJointe : pieceJointeList) {
                creerVersionRequest.getPieceJointeDocList().add(pieceJointe.getDocument());
            }

            if (!checkPieceJointe(creerVersionRequest.getPieceJointeDocList(), publier, currentEvenementForCreation.getAdapter(Evenement.class))) {
                return null;
            }
            CreerVersionResponse creerVersionResponse = null;
            final VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
            if (publier) {
                creerVersionResponse = versionCreationService.rectifierPublier(documentManager, creerVersionContext);
            } else {
                creerVersionResponse = versionCreationService.rectifierBrouillon(documentManager, creerVersionContext);
            }

            final Evenement event = creerVersionResponse.getEvenementDoc().getAdapter(Evenement.class);

            if (event.isEtatAttenteValidation()) {
                final String message = resourcesAccessor.getMessages().get(EVENEMENT_RECTIFIER_ATTENTE_VALIDATION);
                facesMessages.add(StatusMessage.Severity.INFO, message);
            } else {
                final String message = resourcesAccessor.getMessages().get(EVENEMENT_RECTIFIER);
                facesMessages.add(StatusMessage.Severity.INFO, message);
            }

            resetCreation();

            // refresh du provider
            Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);

            // refresh de la corbeille courante
            Events.instance().raiseEvent(CorbeilleActionsBean.REFRESH_CORBEILLE);

            unlockCurrentMessage();
            return corbeilleActions.refreshEvenement();

        } catch (final ClientException e) {            
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_COMM_TEC, e);
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();

            return null;
        }
    }

    public String completerEvenement() throws ClientException {
        
        currentTypeEvenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement();
        if (StringUtils.isEmpty(currentTypeEvenement)) {
            final String message = resourcesAccessor.getMessages().get(TYPE_EVENEMENT_VIDE);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        // cherck verrou
        if (!checkLockMessage()) {
            return null;
        }

        currentEvenementForCreation = navigationContext.getCurrentDocument();
        currentVersionForCreation = corbeilleActions.getSelectedVersion();

        // chargement des pièces jointes
        final PieceJointeService pjService = SolonEppServiceLocator.getPieceJointeService();
        final List<DocumentModel> pjDocList = pjService.getVersionPieceJointeList(documentManager, currentVersionForCreation);

        pieceJointeList = new ArrayList<PieceJointe>();
        for (final DocumentModel pjDoc : pjDocList) {
            final PieceJointe pj = pjDoc.getAdapter(PieceJointe.class);
            pieceJointeList.add(pj);

            final List<DocumentModel> pjfList = SolonEppServiceLocator.getPieceJointeFichierService().findAllPieceJointeFichier(documentManager,
                    pjDoc);
            pj.setPieceJointeFichierDocList(pjfList);
        }

        metadonneesActions.setCurrentLayoutMode(MetaDonneesActionsBean.LAYOUT_MODE_COMPLETER);

        // Renseigne le vue courante
        navigationContext.setCurrentView(SolonEppViewConstant.VIEW_COMPLETER_EVENEMENT);
        return SolonEppViewConstant.VIEW_COMPLETER_EVENEMENT;
    }

    public String cancelCompleterEvenement() throws ClientException {
        unlockCurrentMessage();
		return corbeilleActions.refreshEvenement();
    }

    public String saveCompleterEvenement(final boolean publier) {

		// validation des metas required sauf si JO-01
		if (!currentEvenementForCreation.getAdapter(Evenement.class).getTypeEvenement().equals("EVT45")
				&& !checkMetadonnees(publier)) {
            return null;
        }

        try {
            // check verrou
            if (!checkLockMessage()) {
                final String message = resourcesAccessor.getMessages().get(EVENEMENT_LOCK_LOST);
                facesMessages.add(StatusMessage.Severity.WARN, message);
                return null;
            }

            final CreerVersionContext creerVersionContext = new CreerVersionContext();
            final CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            creerVersionRequest.setEvenementDoc(currentEvenementForCreation);
            creerVersionRequest.setVersionDoc(currentVersionForCreation);

            for (final PieceJointe pieceJointe : pieceJointeList) {
                creerVersionRequest.getPieceJointeDocList().add(pieceJointe.getDocument());
            }

            if (!checkPieceJointe(creerVersionRequest.getPieceJointeDocList(), publier, currentEvenementForCreation.getAdapter(Evenement.class))) {
                return null;
            }

            final VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
            if (publier) {
                versionCreationService.completerPublier(documentManager, creerVersionContext);
            } else {
                versionCreationService.completerBrouillon(documentManager, creerVersionContext);
            }

            final String message = resourcesAccessor.getMessages().get(EVENEMENT_COMPLETER);
            facesMessages.add(StatusMessage.Severity.INFO, message);

            resetCreation();

            // refresh du provider
            Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);
            // refresh de la corbeille courante
            Events.instance().raiseEvent(CorbeilleActionsBean.REFRESH_CORBEILLE);

            unlockCurrentMessage();

            return corbeilleActions.refreshEvenement();

        } catch (final ClientException e) {            
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_COMM_TEC,e) ;
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();

            return null;
        }
    }

    /**
     * Modifier un évenement brouillon
     * 
     * @return
     * @throws ClientException
     */
    public String modifierEvenement() throws ClientException {
        currentTypeEvenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement();
        if (StringUtils.isEmpty(currentTypeEvenement)) {
            final String message = resourcesAccessor.getMessages().get(TYPE_EVENEMENT_VIDE);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        // cherck verrou
        if (!checkLockMessage()) {
            return null;
        }

        currentEvenementForCreation = navigationContext.getCurrentDocument();
        currentVersionForCreation = corbeilleActions.getSelectedVersion();

        final Version version = currentVersionForCreation.getAdapter(Version.class);
        final String modeCreation = version.getModeCreation();

        // chargement des pièces jointes
        final PieceJointeService pjService = SolonEppServiceLocator.getPieceJointeService();
        final List<DocumentModel> pjDocList = pjService.getVersionPieceJointeList(documentManager, currentVersionForCreation);

        pieceJointeList = new ArrayList<PieceJointe>();
        for (final DocumentModel pjDoc : pjDocList) {
            final PieceJointe pj = pjDoc.getAdapter(PieceJointe.class);
            pieceJointeList.add(pj);

            final List<DocumentModel> pjfList = SolonEppServiceLocator.getPieceJointeFichierService().findAllPieceJointeFichier(documentManager,
                    pjDoc);
            pj.setPieceJointeFichierDocList(pjfList);
        }

        if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_INIT_VALUE.equals(modeCreation)) {
            metadonneesActions.setCurrentLayoutMode(MetaDonneesActionsBean.LAYOUT_MODE_CREER);
        } else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(modeCreation)) {
            metadonneesActions.setCurrentLayoutMode(MetaDonneesActionsBean.LAYOUT_MODE_COMPLETER);
        } else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(modeCreation)) {
            metadonneesActions.setCurrentLayoutMode(MetaDonneesActionsBean.LAYOUT_MODE_RECTIFIER);
        }

        navigationContext.setCurrentView(SolonEppViewConstant.VIEW_MODIFIER_EVENEMENT);
        return SolonEppViewConstant.VIEW_MODIFIER_EVENEMENT;
    }

    public String suivreTransitionEnCoursTraitement() throws ClientException {

        final MessageService messageService = SolonEppServiceLocator.getMessageService();
        messageService.followTransitionEnCours(documentManager, navigationContext.getCurrentDocument().getTitle());

        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("evenement.transition.encours.ok"));

        return corbeilleActions.refreshEvenement();
    }

    public String cancelModifierEvenement() throws ClientException {
        unlockCurrentMessage();
        return corbeilleActions.refreshEvenement();
    }

    public String saveModifierEvenement(final boolean publier) {

        // validation des metas required
        if (!checkMetadonnees(publier)) {
            return null;
        }

        try {
            // check verrou
            if (!checkLockMessage()) {
                final String message = resourcesAccessor.getMessages().get(EVENEMENT_LOCK_LOST);
                facesMessages.add(StatusMessage.Severity.WARN, message);
                return null;
            }

            final CreerVersionContext creerVersionContext = new CreerVersionContext();
            final CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            creerVersionRequest.setEvenementDoc(currentEvenementForCreation);
            creerVersionRequest.setVersionDoc(currentVersionForCreation);

            final Version version = currentVersionForCreation.getAdapter(Version.class);
            final String modeCreation = version.getModeCreation();

            for (final PieceJointe pieceJointe : pieceJointeList) {
                creerVersionRequest.getPieceJointeDocList().add(pieceJointe.getDocument());
            }

            if (!checkPieceJointe(creerVersionRequest.getPieceJointeDocList(), publier, currentEvenementForCreation.getAdapter(Evenement.class))) {
                return null;
            }

            final VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();
            if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_INIT_VALUE.equals(modeCreation)) {
                final DocumentModel dossierDoc = documentManager.getDocument(currentEvenementForCreation.getParentRef());
                creerVersionRequest.setDossierDoc(dossierDoc);
                if (publier) {
                    creerVersionContext.setPublie(true);
                }
                versionCreationService.createVersion(documentManager, creerVersionContext);
            } else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(modeCreation)) {
                if (publier) {
                    versionCreationService.completerPublier(documentManager, creerVersionContext);
                } else {
                    versionCreationService.completerBrouillon(documentManager, creerVersionContext);
                }
            } else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(modeCreation)) {
                if (publier) {
                    versionCreationService.rectifierPublier(documentManager, creerVersionContext);
                } else {
                    versionCreationService.rectifierBrouillon(documentManager, creerVersionContext);
                }
            }

            final String message = resourcesAccessor.getMessages().get(EVENEMENT_MODIFIER);
            facesMessages.add(StatusMessage.Severity.INFO, message);

            resetCreation();

            // refresh du provider
            Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);

            unlockCurrentMessage();
            return corbeilleActions.refreshEvenement();

        } catch (final ClientException e) {            
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_CREATE_COMM_TEC, e);
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();

            return null;
        }
    }

    public String suivreTransitionTraite() throws ClientException {

        final MessageService messageService = SolonEppServiceLocator.getMessageService();
        messageService.followTransitionTraite(documentManager, navigationContext.getCurrentDocument().getTitle());

        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("evenement.transition.traite.ok"));

        return corbeilleActions.refreshEvenement();
    }

    /**
     * Valide la version
     * 
     * @param accepter true : accepter, false : rejeter
     * @return
     * @throws ClientException
     */
    public String validerVersion(final boolean accepter) throws ClientException {
        final Evenement evenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class);
        final ValiderVersionContext validerVersionContext = new ValiderVersionContext();
        final String messageType = corbeilleActions.getCurrentMessage().getMessageType();
        final VersionService versionService = SolonEppServiceLocator.getVersionService();

        final ValiderVersionRequest validerVersionRequest = new ValiderVersionRequest();
        validerVersionContext.setValiderVersionRequest(validerVersionRequest);

        validerVersionRequest.setEvenementId(evenement.getTitle());
        validerVersionRequest.setAccepter(accepter);

        if (SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_DESTINATAIRE_VALUE.equals(messageType)) {
            versionService.validerVersionDestinataire(documentManager, validerVersionContext);
        }

        String message = null;

        if (accepter) {
            message = resourcesAccessor.getMessages().get(EVENEMENT_VALIDER);
        } else {
            message = resourcesAccessor.getMessages().get(EVENEMENT_REJETER);
        }
        facesMessages.add(StatusMessage.Severity.INFO, message);

        return corbeilleActions.refreshEvenement();
    }

    /**
     * Accuser reception version
     * 
     * @return
     * @throws ClientException
     */
    public String accuserReceptionVersion() throws ClientException {
        final Evenement evenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class);
        final AccuserReceptionContext accuserReceptionContext = new AccuserReceptionContext();
        final Version version = corbeilleActions.getSelectedVersion().getAdapter(Version.class);
        final VersionService versionService = SolonEppServiceLocator.getVersionService();

        final AccuserReceptionRequest accuserReceptionRequest = new AccuserReceptionRequest();
        accuserReceptionContext.setAccuserReceptionRequest(accuserReceptionRequest);

        accuserReceptionRequest.setEvenementId(evenement.getTitle());
        accuserReceptionRequest.setNumeroVersion(version.getNumeroVersion());

        versionService.accuserReceptionDestinataire(documentManager, accuserReceptionContext);

        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("evenement.accuser.reception.version.ok"));

        return corbeilleActions.refreshEvenement();
    }

    /**
     * Abandonner version
     * 
     * @return
     * @throws ClientException
     */
    public String abandonnerVersion() throws ClientException {
        final Evenement evenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class);
        final ValiderVersionContext validerVersionContext = new ValiderVersionContext();
        final VersionService versionService = SolonEppServiceLocator.getVersionService();

        final ValiderVersionRequest validerVersionRequest = new ValiderVersionRequest();
        validerVersionContext.setValiderVersionRequest(validerVersionRequest);

        validerVersionRequest.setEvenementId(evenement.getTitle());

        versionService.validerVersionEmetteur(documentManager, validerVersionContext);

        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("evenement.abandonner.version.ok"));

        return corbeilleActions.refreshEvenement();
    }

    /**
     * Lever alerte
     * 
     * @return
     * @throws ClientException
     */
    public String leverAlerte() throws ClientException {
        Evenement evenementPrecedent = null;
        if (navigationContext.getCurrentDocument() != null) {
            evenementPrecedent = navigationContext.getCurrentDocument().getAdapter(Evenement.class);
        } else {
            final String message = resourcesAccessor.getMessages().get(EVENEMENT_PRECEDENT);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        setCurrentTypeEvenementSuccessif(evenementPrecedent);

        return navigateToCreationCommunicationSuccessive();
    }

    private void setCurrentTypeEvenementSuccessif(final Evenement evenementPrecedent) throws ClientException {

        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();

        final EvenementTypeDescriptor evtParentTypeDescriptor = evenementTypeService.getEvenementType(evenementPrecedent.getTypeEvenement());
        if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_PROCEDURE_LEGISLATIVE_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_01.value();
        } else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_PROCEDURE_CENSURE_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_02.value();
        } else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_ORGANISATION_SESSION_EXTRAORDINAIRE_VALUE.equals(evtParentTypeDescriptor
                .getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_03.value();
        } else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_CONSULTATION_ASSEMBLEE_PROJET_NOMINATION_VALUE.equals(evtParentTypeDescriptor
                .getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_04.value();
        } else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_CONVOCATION_CONGRES_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_05.value();
        } else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DEMANDE_PROLONGATION_INTERVENTION_EXTERIEURE_VALUE.equals(evtParentTypeDescriptor
                .getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_06.value();
        } else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_RESOLUTION_ARTICLE_34_1_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_07.value();
        } else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DEPOT_RAPPORT_PARLEMENT_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_08.value();
        } else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_INSERTION_INFORMATION_PARLEMENTAIRE_JO_VALUE.equals(evtParentTypeDescriptor
                .getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_09.value();
        } else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_ORGANISME_EXTRA_PARLEMENTAIRE_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_10.value();
        }else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DEMANDE_DE_MISE_EN_OEUVRE_ARTICLE_28_3C_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_13.value();
        }else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DECLARATION_DE_POLITIQUE_GENERALE_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_11.value();
        }else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DECLARATION_SUR_UN_SUJET_DETERMINE_50_1C_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_12.value();
        }else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_DEMANDE_AUDITION_PERSONNES_EMPLOIS_ENVISAGEE_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_14.value();
        }else if (SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_AUTRES_DOCUMENTS_TRANSMIS_AUX_ASSEMBLEES_VALUE.equals(evtParentTypeDescriptor.getProcedure())) {
            currentTypeEvenementSuccessif = EvenementType.ALERTE_15.value();
        }   
        
        
        
    }

    /**
     * Annuler événement
     * 
     * @return
     * @throws ClientException
     */
    public String annulerEvenement() throws ClientException {
        final Evenement evenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class);
        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final AnnulerEvenementContext annulerEvenementContext = new AnnulerEvenementContext();
        final AnnulerEvenementRequest annulerEvenementRequest = annulerEvenementContext.getAnnulerEvenementRequest();
        annulerEvenementContext.setAnnulerEvenementRequest(annulerEvenementRequest);

        annulerEvenementRequest.setEvenementId(evenement.getTitle());

        evenementService.annulerEvenement(documentManager, annulerEvenementContext);

        final String message = resourcesAccessor.getMessages().get("evenement.annuler.ok");
        facesMessages.add(StatusMessage.Severity.INFO, message);

        return corbeilleActions.refreshEvenement();
    }

    /**
     * Créer une alerte
     * 
     * @return
     * @throws ClientException
     */
    public String creerAlerte() throws ClientException {
        Evenement evenementPrecedent = null;
        if (navigationContext.getCurrentDocument() != null) {
            evenementPrecedent = navigationContext.getCurrentDocument().getAdapter(Evenement.class);
        } else {
            final String message = resourcesAccessor.getMessages().get(EVENEMENT_PRECEDENT);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        setCurrentTypeEvenementSuccessif(evenementPrecedent);

        return navigateToCreationCommunicationSuccessive();
    }

    /**
     * Publie directement un événement à l'état brouillon (sans modifier de données).
     * 
     * @return Vue
     * @throws ClientException
     */
    public String publierEvenement() throws ClientException {
        try {
            final CreerVersionContext creerVersionContext = new CreerVersionContext();
            final CreerVersionRequest creerVersionRequest = creerVersionContext.getCreerVersionRequest();
            creerVersionContext.setPublie(true);

            final DocumentModel evenementDoc = navigationContext.getCurrentDocument();
            creerVersionRequest.setEvenementDoc(evenementDoc);

            final VersionService versionService = SolonEppServiceLocator.getVersionService();
            final DocumentModel versionDoc = versionService.getVersionActive(documentManager, evenementDoc,
                    SolonEppSchemaConstant.CASE_LINK_MESSAGE_TYPE_EMETTEUR_VALUE);
            creerVersionRequest.setVersionDoc(versionDoc);

            final DocumentModel dossierDoc = documentManager.getDocument(evenementDoc.getParentRef());
            creerVersionRequest.setDossierDoc(dossierDoc);
            
            // check meta
            final EvenementValidator evenementValidator = new EvenementValidator(documentManager);
            evenementValidator.validateMetaObligatoire(evenementDoc, true);
            final VersionValidator versionValidator = new VersionValidator();
            versionValidator.validateMetaObligatoire(versionDoc, evenementDoc);

            // chargement des pièces jointes
            final PieceJointeService pjService = SolonEppServiceLocator.getPieceJointeService();
            final List<DocumentModel> pjDocList = pjService.getVersionPieceJointeListWithFichier(documentManager, versionDoc);
            creerVersionRequest.getPieceJointeDocList().addAll(pjDocList);
            if (!checkPieceJointe(creerVersionRequest.getPieceJointeDocList(), true, evenementDoc.getAdapter(Evenement.class))) {
                return null;
            }
            

            final VersionCreationService versionCreationService = SolonEppServiceLocator.getVersionCreationService();

            final Version version = versionDoc.getAdapter(Version.class);
            if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_COMPLETION_VALUE.equals(version.getModeCreation())) {
                versionCreationService.completerPublier(documentManager, creerVersionContext);
            } else if (SolonEppSchemaConstant.VERSION_MODE_CREATION_BROUILLON_RECTIFICATION_VALUE.equals(version.getModeCreation())) {
                versionCreationService.rectifierPublier(documentManager, creerVersionContext);
            } else {
                versionCreationService.createVersion(documentManager, creerVersionContext);
            }

            final String message = resourcesAccessor.getMessages().get(EVENEMENT_PUBLIE);
            facesMessages.add(StatusMessage.Severity.INFO, message);

            resetCreation();

            // refresh du provider
            Events.instance().raiseEvent(ProviderBean.REFRESH_CONTENT_VIEW_EVENT);
            // refresh de la corbeille courante
            Events.instance().raiseEvent(CorbeilleActionsBean.REFRESH_CORBEILLE);

            return corbeilleActions.refreshEvenement();

        } catch (final ClientException e) {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_UPDATE_COMM_TEC, "Echec de publication", e);
            facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());
            TransactionHelper.setTransactionRollbackOnly();

            return null;
        }
    }

	/**
	 * Supprimer événement
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String supprimerEvenement() throws ClientException {
		try {
			final Evenement evenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class);
			final VersionService versionService = SolonEppServiceLocator.getVersionService();

			final Version currentVersion = corbeilleActions.getCurrentVersion();
			if (currentVersion != null) {
				versionService.supprimerVersionBrouillon(documentManager, evenement.getDocument(),
						currentVersion.getDocument());
			}

			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("evenement.supprimer.ok"));

			return corbeilleActions.navigateTo();
		} catch (final ClientException e) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_UPDATE_COMM_TEC, "Echec de suppression", e);
			facesMessages.add(StatusMessage.Severity.WARN, resourcesAccessor.getMessages().get("evenement.supprimer.ko"));
			facesMessages.add(StatusMessage.Severity.WARN, e.getMessage());

			return null;
		}
	}

    /**
     * Transmettre par mail
     * 
     * @return
     * @throws ClientException
     */
    public String transmettreParMail() throws ClientException {
        final String typeEvenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement();

        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        final EvenementTypeDescriptor evtType = evenementTypeService.getEvenementType(typeEvenement);
        if (evtType != null) {
            mailEvenement.put(MAIL_OBJET, evtType.getLabel());
        }

        return SolonEppViewConstant.VIEW_TRANSMETTRE_MAIL_EVENEMENT;
    }

    /**
     * Envoi du message suite à l'action Transmettre par mail
     * 
     * @return
     * @throws ClientException
     * @throws Exception
     */
    public String transmettreParMailEnvoyer() throws ClientException {

        final String corps = mailEvenement.get(MAIL_MESSAGE);
        final String objet = mailEvenement.get(MAIL_OBJET);
        final String destinataire = mailEvenement.get(MAIL_DESTINATAIRE);

        final Evenement evenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class);

        final Version currentVersion = corbeilleActions.getCurrentVersion();

        final PieceJointeService pjService = SolonEppServiceLocator.getPieceJointeService();
        final List<DocumentModel> pjDocList = pjService.getVersionPieceJointeList(documentManager, currentVersion.getDocument());

        final EvenementService evenementService = SolonEppServiceLocator.getEvenementService();
        final DocumentModel dossierDoc = documentManager.getDocument(evenement.getDocument().getParentRef());
        evenementService.envoyerMel(documentManager, eppPrincipal.getName(), objet, corps, destinataire, eppPrincipal.getEmail(),
                evenement.getDocument(), currentVersion.getDocument(), dossierDoc, pjDocList);

        facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("evenement.transferer.ok"));

        return corbeilleActions.refreshEvenement();
    }

    /**
     * Annuler l'action transmettre par mail
     * 
     * @return
     * @throws ClientException
     */
    public String transmettreParMailAnnuler() throws ClientException {
        return corbeilleActions.refreshEvenement();
    }

    /**
     * @return the mailEvenement
     */
    public Map<String, String> getMailEvenement() {
        return mailEvenement;
    }

    /**
     * @param mailEvenement the mailEvenement to set
     */
    public void setMailEvenement(final Map<String, String> mailEvenement) {
        this.mailEvenement = mailEvenement;
    }

    /**
     * @return the showRemoveLockPopup
     */
    public boolean isShowRemoveLockPopup() {
        return showRemoveLockPopup;
    }

    /**
     * @param showRemoveLockPopup the showRemoveLockPopup to set
     */
    public void setShowRemoveLockPopup(final boolean showRemoveLockPopup) {
        this.showRemoveLockPopup = showRemoveLockPopup;
    }

    /**
     * Renvoie l'utilisateur qui a le verrou sur l'événement
     * 
     * @return
     * @throws ClientException
     */
    public String getMessageLocker() throws ClientException {
        String locker = null;
        final STLockService lockService = STServiceLocator.getSTLockService();

        final Message message = corbeilleActions.getCurrentMessage();

        final Map<String, String> lockDetail = lockService.getLockDetails(documentManager, message.getDocument());

        if (lockDetail != null) {
            locker = lockDetail.get(STLockService.LOCKER);
        }

        return locker;
    }

    /**
     * Renvoie true si l'utilisateur courant possède le verrou sur l'événement courant
     * 
     * @return
     * @throws ClientException
     */
    private boolean isCurrentUserLocker(final String locker) throws ClientException {
        if (locker != null) {
            if (eppPrincipal.getName().equals(locker)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Lock le message courant si cela est possible sinon affiche la popup de forçage du verrou
     * 
     * @return
     * @throws ClientException
     */
    private boolean checkLockMessage() throws ClientException {
        // cherck verrou
        final String locker = getMessageLocker();
        if (locker != null) {
            if (!isCurrentUserLocker(locker)) {
                showRemoveLockPopup = true;
                return false;
            }
        } else {
            if (!lockCurrentMessage()) {
                final String message = resourcesAccessor.getMessages().get(EVENEMENT_CANT_LOCK);
                facesMessages.add(StatusMessage.Severity.WARN, message);
                return false;
            }
        }
        return true;
    }

    /**
     * Lock le message courant
     * 
     * @return
     * @throws ClientException
     */
    private boolean lockCurrentMessage() throws ClientException {
        final STLockService lockService = STServiceLocator.getSTLockService();
        if (corbeilleActions.getCurrentMessage() != null) {
            final Message message = corbeilleActions.getCurrentMessage();
            if (lockService.isLockActionnableByUser(documentManager, message.getDocument(), eppPrincipal)) {
                return lockService.lockDoc(documentManager, message.getDocument());
            }
        }
        return false;
    }

    /**
     * Unlock le message courant
     * 
     * @return
     * @throws ClientException
     */
    private boolean unlockCurrentMessage() throws ClientException {
        final STLockService lockService = STServiceLocator.getSTLockService();
        if (corbeilleActions.getCurrentMessage() != null) {
            final Message message = corbeilleActions.getCurrentMessage();
            if (lockService.isLockActionnableByUser(documentManager, message.getDocument(), eppPrincipal)) {
                return lockService.unlockDoc(documentManager, message.getDocument());
            }
        }
        return false;
    }

    /**
     * Force le déverrouillage de message courant
     * 
     * @throws ClientException
     */
    public String forceUnlockCurrentMessage() throws ClientException {
        final STLockService lockService = STServiceLocator.getSTLockService();
        final STMailService mailService = STServiceLocator.getSTMailService();
        final UserManager userManager = STServiceLocator.getUserManager();

        final Message message = corbeilleActions.getCurrentMessage();

        final Map<String, String> lockDetails = lockService.getLockDetails(documentManager, message.getDocument());
        if (lockDetails != null && !lockDetails.get(STLockService.LOCKER).equals(eppPrincipal.getName())) {
            final String locker = lockDetails.get(STLockService.LOCKER);
            lockService.unlockDocUnrestricted(documentManager, message.getDocument());
            final DocumentModel userDoc = userManager.getUserModel(locker);
            if (userDoc != null) {
                // Send mail to user
                final STUser user = userDoc.getAdapter(STUser.class);
                final List<STUser> userList = new ArrayList<STUser>();
                userList.add(user);
                mailService.sendMailToUserList(documentManager, userList, "Notifier Verrou", "Vous avez perdu votre verrou sur la communication : "
                        + message.getTitle());
            }
            lockCurrentMessage();
        }

        return null;
    }

    /**
     * @return the enregistrerBrouillon
     */
    public String getEnregistrerBrouillon() {
        return enregistrerBrouillon;
    }

    /**
     * @param enregistrerBrouillon the enregistrerBrouillon to set
     */
    public void setEnregistrerBrouillon(final String enregistrerBrouillon) {
        this.enregistrerBrouillon = enregistrerBrouillon;
    }

    public void modeBrouillon(final ActionEvent event) {
        setEnregistrerBrouillon("true");
    }

    /**
     * @return the displayRequired
     */
    public boolean isDisplayRequired() {
        return displayRequired;
    }

    /**
     * @param displayRequired the displayRequired to set
     */
    public void setDisplayRequired(final boolean displayRequired) {
        this.displayRequired = displayRequired;
    }
}
