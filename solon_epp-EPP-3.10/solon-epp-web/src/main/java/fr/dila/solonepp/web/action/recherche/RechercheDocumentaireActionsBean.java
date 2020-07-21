package fr.dila.solonepp.web.action.recherche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.util.RepositoryLocation;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.solonepp.api.constant.SolonEppActionConstant;
import fr.dila.solonepp.api.constant.SolonEppContentView;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppViewConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.core.dao.MessageDao;
import fr.dila.solonepp.core.parser.XsdToUfnxqlParser;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.action.evenement.EvenementTypeActionsBean;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean Seam permettant de gérer la recherche documentaire de messages.
 * 
 * @author jtremeaux
 */
@Name("rechercheDocumentaireActions")
@Scope(ScopeType.CONVERSATION)
public class RechercheDocumentaireActionsBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    @In(create = true, required = true)
    protected transient ActionManager actionManager;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true)
    protected transient NavigationWebActionsBean navigationWebActions;

    @In(create = true)
    protected ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient ContentViewActions contentViewActions;

    @In(create = true, required = false)
    protected transient EvenementTypeActionsBean evenementTypeActions;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;
    /**
     * Critères de recherches sur les messages.
     */
    private MessageCriteria messageCriteria;

    /**
     * Critères de recherches sur les messages de l'historique.
     */
    private MessageCriteria messageCriteriaDirect;

    /**
     * param dans les URLs des flux rss
     */
    @RequestParameter
    String evenementId;

    // *********************************************************************
    // Critères de recherches communs à toutes les procédures
    // *********************************************************************
    /**
     * Catégorie d'événement séléctionnée.
     */
    private String categorieEvenementId;

    /**
     * Critère de recherche type d'événement.
     */
    private List<String> typeEvenement = new ArrayList<String>();

    /**
     * Critère de recherche objet du dossier.
     */
    private String objetDossier;

    /**
     * Critère de recherche identifiant du dossier.
     */
    private String idDossier;

    /**
     * Critère de recherche identifiant de l'événement.
     */
    private String idEvent;

    /**
     * Critère de recherche identifiant de l'événement pour acces direct de l'historique.
     */
    private String idEventDirect;

    /**
     * Critère de recherche date d'horodatage de la version (min / max).
     */
    private Date dateEvenement;

    /**
     * Critère de recherche date d'horodatage de la version (max).
     */
    private Date dateEvenementPeriode;

    /**
     * Critère de recherche émetteur du message.
     */
    private List<String> emetteur = new ArrayList<String>();

    /**
     * Critère de recherche destinataire du message.
     */
    private List<String> destinataire = new ArrayList<String>();

    /**
     * Critère de recherche destinataire en copie du message.
     */
    private List<String> copie = new ArrayList<String>();

    /**
     * Contenu des widget des listes d'institutions.
     */
    private List<SelectItem> participantList;

    /**
     * Contenu plein texte des fichiers de pièces jointes.
     */
    private String pieceJointeFichierFulltext;

    /**
     * Critères dynamique sur les métadonnées.
     */
    private Map<String, Object> metadonneeCriteria;

    /**
     * Critères dynamique sur les métadonnées.
     */
    private String champLibre;

    private List<Object> params = new ArrayList<Object>();

    private String requteLibre = null;

    
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(RechercheDocumentaireActionsBean.class) ;    
    
    
    /**
     * Sélectione une catégorie d'événements.
     * 
     * @param categorieEvenementId Catégorie d'événement sélectionnée
     * @return Vue
     * @throws ClientException
     */
    public String selectCategorieEvenement(String categorieEvenementId) throws ClientException {
        this.categorieEvenementId = categorieEvenementId;

        return resetCriteria();
    }

    /**
     * Navigue vers l'espace de recherche.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String navigateToRecherche() throws ClientException {

        initRecherche();
        selectCategorieEvenement(SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_PROCEDURE_LEGISLATIVE_VALUE);

        return SolonEppViewConstant.RECHERCHE_CRITERIA_VIEW;
    }

    /**
     * Navigue vers l'espace de recherche.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String navigateTo() throws ClientException {

        initRecherche();

        if (SolonEppVocabularyConstant.REQUETE_LIBRE_ID.equals(categorieEvenementId)) {
            return SolonEppViewConstant.REQUETE_LIBRE_VIEW;
        }
        return SolonEppViewConstant.RECHERCHE_CRITERIA_VIEW;
    }

    /**
     * Action qui effectue la recherche.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String SearchEventById() throws ClientException {

        // Réinitialise la content view
        contentViewActions.reset(SolonEppContentView.RECHERCHE_MESSAGE_LIST_CONTENT_VIEW);

        // Réinitialise le document courant
        navigationContext.resetCurrentDocument();

        return SolonEppViewConstant.RECHERCHE_RESULT_VIEW;
    }

    /**
     * Action qui effectue la recherche.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String search() throws ClientException {

        if(metadonneeCriteria == null){
          metadonneeCriteria = new HashMap<String, Object>() ;
        }
        // TCH :: mantis 0051365
        String niveauLecture = (String) metadonneeCriteria.get("niveauLecture");
        if (!(SolonEppVocabularyConstant.NIVEAU_LECTURE_AN_VALUE.equals(niveauLecture) || SolonEppVocabularyConstant.NIVEAU_LECTURE_SENAT_VALUE.equals(niveauLecture))) {
            // ces valeurs sont les memes que dans MetaDonneesActionsBean :: isNumeroLectureVisible
            metadonneeCriteria.remove("niveauLectureNumero");
        }

        if (champLibre == null) {
            messageCriteria = new MessageCriteria();
            messageCriteria.setCheckReadPermission(true);

            if (typeEvenement.isEmpty() && this.getCategorieEvenementId() != null) {
                final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
                List<EvenementTypeDescriptor> eventTypeList = evenementTypeService.findEvenementByCategory(this.getCategorieEvenementId());
                if (eventTypeList != null) {
                    for (EvenementTypeDescriptor eventType : eventTypeList) {
                        typeEvenement.add(eventType.getName());
                    }
                }
            }
            messageCriteria.setEvenementTypeIn(typeEvenement);

            if (StringUtils.isNotBlank(objetDossier)) {
                messageCriteria.setVersionObjetLike(objetDossier);
            }

            if (StringUtils.isNotBlank(idDossier)) {
                messageCriteria.setDossierId(idDossier);
            }

            if (StringUtils.isNotBlank(idEvent)) {
                messageCriteria.setEvenementId(idEvent);
            }

            if (dateEvenement != null) { // vide le cache du contentView pour ré-executer la requête
                if (contentViewActions != null) {
                    contentViewActions.reset(SolonEppContentView.RECHERCHE_MESSAGE_LIST_CONTENT_VIEW);
                }

                Calendar dateDebut = Calendar.getInstance();
                dateDebut.setTime(dateEvenement);
                messageCriteria.setVersionHorodatageMin(dateDebut);
                if (dateEvenementPeriode != null) {
                    Calendar dateFin = Calendar.getInstance();
                    dateFin.setTime(dateEvenementPeriode);
                    dateFin.add(Calendar.DAY_OF_MONTH, 1);
                    messageCriteria.setVersionHorodatageMax(dateFin);
                }
            }

            if (!emetteur.isEmpty()) {
                messageCriteria.setEvenementEmetteurIn(emetteur);
            }

            if (!destinataire.isEmpty()) {
                messageCriteria.setEvenementDestinataireIn(destinataire);
            }

            if (!copie.isEmpty()) {
                messageCriteria.setEvenementDestinataireCopieIn(copie);
            }

            if (StringUtils.isNotBlank(pieceJointeFichierFulltext)) {
                messageCriteria.setPieceJointeFichierFulltext(pieceJointeFichierFulltext);
            }

            messageCriteria.setMetadonneeCriteria(new HashMap<String, Object>(metadonneeCriteria));

            MessageDao messageDao = new MessageDao(documentManager, messageCriteria);
            params = messageDao.getParamList();
            // Réinitialise la content view
			if (contentViewActions != null) {
				contentViewActions.reset(SolonEppContentView.RECHERCHE_MESSAGE_LIST_CONTENT_VIEW);
			}

            // Réinitialise le document courant
            navigationContext.resetCurrentDocument();
        } else {
            try {
                XsdToUfnxqlParser xsdToUfnxqlParser = new XsdToUfnxqlParser();

                EppPrincipal eppPrincipal = (EppPrincipal) documentManager.getPrincipal();
                requteLibre = xsdToUfnxqlParser.parse(champLibre, params, documentManager, eppPrincipal.getInstitutionId());
                champLibre = null;
            } catch (Exception e) {
                
                String message = resourcesAccessor.getMessages().get("epp.rechercheDocumentaire.requete.libre.errone");                
                LOGGER.warn(documentManager, STLogEnumImpl.READ_UFNXQL_TEC,message, e) ;
                facesMessages.add(StatusMessage.Severity.WARN, message);
                return null;
            }
        }

        return SolonEppViewConstant.RECHERCHE_RESULT_VIEW;
    }

    /**
     * Action qui réinitialise les critères de recherche.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String resetCriteria() throws ClientException {
        // Réinitialise les critères de recherche
        typeEvenement = new ArrayList<String>();
        objetDossier = null;
        idDossier = null;
        idEvent = null;
        dateEvenement = null;
        dateEvenementPeriode = null;
        emetteur = new ArrayList<String>();
        destinataire = new ArrayList<String>();
        copie = new ArrayList<String>();
        pieceJointeFichierFulltext = null;
        metadonneeCriteria = new HashMap<String, Object>();
        params = new ArrayList<Object>();
        requteLibre = null;
        // Réinitialise la content view
        messageCriteria = null;
        contentViewActions.reset(SolonEppContentView.RECHERCHE_MESSAGE_LIST_CONTENT_VIEW);

        if (categorieEvenementId != null && SolonEppVocabularyConstant.REQUETE_LIBRE_ID.equals(categorieEvenementId)) {
            return SolonEppViewConstant.REQUETE_LIBRE_VIEW;
        }
        return SolonEppViewConstant.RECHERCHE_CRITERIA_VIEW;
    }

    /**
     * Action qui retourne au critères de recherche.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String backToCriteria() throws ClientException {
        return SolonEppViewConstant.RECHERCHE_CRITERIA_VIEW;
    }

    /**
     * Action qui retourne au critères de recherche.
     * 
     * @return Vue
     * @throws ClientException
     */
    public String backToRequeteLibre() throws ClientException {
        return SolonEppViewConstant.REQUETE_LIBRE_VIEW;
    }

    /**
     * Liste des participant pour le widget de recherche
     */
    public List<SelectItem> getParticipantList() {
        if (participantList == null) {
            participantList = new ArrayList<SelectItem>();
            for (InstitutionsEnum participant : SolonEppSchemaConstant.INSTITUTIONS_VALUES) {
                SelectItem selectItem = new SelectItem(participant.name(), participant.getLabel());
                participantList.add(selectItem);
            }
        }
        return participantList;
    }

    /**
     * Retourne la chaîne de requête de la recherche documentaire.
     * 
     * @return Chaîne de requête
     * @throws ClientException
     */
    public String getMessageListQueryString() throws ClientException {
        // recherche par id pour le historique
        if (StringUtils.isNotBlank(idEventDirect)) {
            messageCriteriaDirect = new MessageCriteria();
            messageCriteriaDirect.setCheckReadPermission(true);
            messageCriteriaDirect.setEvenementId(idEventDirect);
            MessageDao messageDao = new MessageDao(documentManager, messageCriteriaDirect);
            return messageDao.getQueryString();
        }
        if (requteLibre != null) {
            return requteLibre;

        } else {
            MessageDao messageDao = new MessageDao(documentManager, messageCriteria);
            return messageDao.getQueryString();
        }
    }

    /**
     * Retourne les critères de recherche de la recherche documentaire.
     * 
     * @return Chaîne de requête
     * @throws ClientException
     */
    public List<Object> getMessageListQueryParameter() throws ClientException {
        // recherche par id pour le historique
        if (StringUtils.isNotBlank(idEventDirect)) {
            MessageDao messageDao = new MessageDao(documentManager, messageCriteriaDirect);
            idEventDirect = null;
            return messageDao.getParamList();
        }
        return params;
    }

    /**
     * @return the typeEvenement
     */
    public List<String> getTypeEvenement() {
        return typeEvenement;
    }

    /**
     * @param typeEvenement the typeEvenement to set
     */
    public void setTypeEvenement(List<String> typeEvenement) {
        this.typeEvenement = typeEvenement;
    }

    /**
     * @return the objetDossier
     */
    public String getObjetDossier() {
        return objetDossier;
    }

    /**
     * @param objetDossier the objetDossier to set
     */
    public void setObjetDossier(String objetDossier) {
        this.objetDossier = objetDossier;
    }

    /**
     * @return the idDossier
     */
    public String getIdDossier() {
        return idDossier;
    }

    /**
     * @param idDossier the idDossier to set
     */
    public void setIdDossier(String idDossier) {
        this.idDossier = idDossier;
    }

    /**
     * @return the idEvent
     */
    public String getIdEvent() {
        return idEvent;
    }

    /**
     * @param idEvent the idEvent to set
     */
    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    /**
     * @return the dateEvenement
     */
    public Date getDateEvenement() {
        return dateEvenement;
    }

    /**
     * @param dateEvenement the dateEvenement to set
     */
    public void setDateEvenement(Date dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    /**
     * @return the dateEvenementPeriode
     */
    public Date getDateEvenementPeriode() {
        return dateEvenementPeriode;
    }

    /**
     * @param dateEvenementPeriode the dateEvenementPeriode to set
     */
    public void setDateEvenementPeriode(Date dateEvenementPeriode) {
        this.dateEvenementPeriode = dateEvenementPeriode;
    }

    /**
     * @return the emetteur
     */
    public List<String> getEmetteur() {
        return emetteur;
    }

    /**
     * @param emetteur the emetteur to set
     */
    public void setEmetteur(List<String> emetteur) {
        this.emetteur = emetteur;
    }

    /**
     * @return the destinataire
     */
    public List<String> getDestinataire() {
        return destinataire;
    }

    /**
     * @param destinataire the destinataire to set
     */
    public void setDestinataire(List<String> destinataire) {
        this.destinataire = destinataire;
    }

    /**
     * @return the copie
     */
    public List<String> getCopie() {
        return copie;
    }

    /**
     * @param copie the copie to set
     */
    public void setCopie(List<String> copie) {
        this.copie = copie;
    }

    /**
     * Getter de categorieEvenementId.
     * 
     * @return categorieEvenementId
     */
    public String getCategorieEvenementId() {
        return categorieEvenementId;
    }

    /**
     * Getter de pieceJointeFichierFulltext.
     * 
     * @return pieceJointeFichierFulltext
     */
    public String getPieceJointeFichierFulltext() {
        return pieceJointeFichierFulltext;
    }

    /**
     * Setter de pieceJointeFichierFulltext.
     * 
     * @param pieceJointeFichierFulltext pieceJointeFichierFulltext
     */
    public void setPieceJointeFichierFulltext(String pieceJointeFichierFulltext) {
        this.pieceJointeFichierFulltext = pieceJointeFichierFulltext;
    }

    /**
     * Getter de metadonneeCriteria.
     * 
     * @return metadonneeCriteria
     */
    public Map<String, Object> getMetadonneeCriteria() {
        return metadonneeCriteria;
    }

    /**
     * Setter de metadonneeCriteria.
     * 
     * @param metadonneeCriteria metadonneeCriteria
     */
    public void setMetadonneeCriteria(Map<String, Object> metadonneeCriteria) {
        this.metadonneeCriteria = metadonneeCriteria;
    }

    /**
     * Recherche lancée par le lien des flux rss
     * 
     * @return
     * @throws ClientException
     */
    @Begin(id = "#{conversationIdGenerator.currentOrNewMainConversationId}", join = true)
    public String navigateToRechercheEventIdURL() throws ClientException {

        if (documentManager == null) {
            NavigationContext navigationContext = (NavigationContext) Component.getInstance("navigationContext", true);
            navigationContext.setCurrentServerLocation(new RepositoryLocation("default"));
            documentManager = navigationContext.getOrCreateDocumentManager();
        }
        navigateTo();
        idEvent = evenementId;
        return search();
    }

    public String getChampLibre() {
        return champLibre;
    }

    public void setChampLibre(String champLibre) {
        this.champLibre = champLibre;
    }

    public boolean displayBackToRequeteLibreButton() {
        return SolonEppVocabularyConstant.REQUETE_LIBRE_ID.equals(categorieEvenementId);
    }

    public String getIdEventDirect() {
        return idEventDirect;
    }

    public void setIdEventDirect(String idEventDirect) {
        this.idEventDirect = idEventDirect;
    }

    /**
     * initialisation de la recherche
     * 
     * @throws ClientException
     */
    private void initRecherche() throws ClientException {

        // Réinitialise le document courant
        navigationContext.resetCurrentDocument();

        // Renseigne le menu du haut
        Action mainMenuAction = actionManager.getAction(SolonEppActionConstant.ESPACE_RECHERCHE);
        navigationWebActions.setCurrentMainMenuAction(mainMenuAction);

        // Renseigne le panneau de gauche
        Action leftMenuAction = actionManager.getAction(SolonEppActionConstant.LEFT_MENU_ESPACE_RECHERCHE);
        navigationWebActions.setCurrentLeftMenuAction(leftMenuAction);

    }
}