package fr.dila.solonepp.web.action.recherche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppContentView;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.web.action.corbeille.CorbeilleActionsBean;
import fr.dila.st.web.action.NavigationWebActionsBean;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Bean seam pour gérer les actions communes des recherches.
 * 
 * @author feo
 */
@Name("rechercheWebActions")
@Scope(ScopeType.CONVERSATION)
public class RechercheWebActionsBean implements Serializable {
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
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(create = true, required = false)
    protected transient ContentViewActions contentViewActions;
        
    // Champs des widgets de recherche 
    private List<String> typeEvenement = new ArrayList<String>();
    private String objetDossier;
    private String idDossier;
    private String idEvent;
    private Date dateEvenement;
    private Date dateEvenementPeriode;
    private List<String> emetteur = new ArrayList<String>();
    private List<String> destinataire = new ArrayList<String>();
    private List<String> copie = new ArrayList<String>();
    private List<SelectItem> participantList;
    
    /**
     * Retourne l'action qui affiche la mini-vue de la recherche simple dans le bandeau du haut.
     * @return L'action de la recherche simple
     */
    public Action getRechercheBandeauServiceAction(){
        return null;//actionManager.getAction(VIEW_RECHERCHE_BANDEAU_SERVICE);
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
    
    
    public String rechercheRapide() throws ClientException {
        MessageCriteria messageCriteria = new MessageCriteria();
        messageCriteria.setCheckReadPermission(true);
        
        if (!typeEvenement.isEmpty()) {
            messageCriteria.setEvenementTypeIn(typeEvenement);
        }

        if (StringUtils.isNotBlank(objetDossier)) {
            messageCriteria.setVersionObjetLike(objetDossier);
        }
        
        if (StringUtils.isNotBlank(idDossier)) {
            messageCriteria.setDossierId(idDossier);
        }
        
        if (StringUtils.isNotBlank(idEvent)) {
            messageCriteria.setEvenementId(idEvent);
        }
        
        if (dateEvenement != null) {
            Calendar dateDebut = Calendar.getInstance();
            dateDebut.setTime(dateEvenement);
            messageCriteria.setVersionHorodatageMin(dateDebut);
        }
        
        if (dateEvenementPeriode != null) {
          Calendar dateFin = Calendar.getInstance();
          dateFin.setTime(dateEvenementPeriode);
          dateFin.add(Calendar.DAY_OF_MONTH, 1);
          messageCriteria.setVersionHorodatageMax(dateFin);
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

        corbeilleActions.setMessageCriteriaFromRecherche(messageCriteria);
        
        // vide le cache du contentView pour ré-executer la requête
        if (contentViewActions != null) {
            contentViewActions.reset(SolonEppContentView.CORBEILLE_MESSAGE_LIST_CONTENT_VIEW);
        }
        
        return corbeilleActions.navigateTo();
    }
    
    public String resetRecherche() throws ClientException {
        typeEvenement = new ArrayList<String>();
        objetDossier = null;
        idDossier = null;
        idEvent = null;
        dateEvenement = null;
        dateEvenementPeriode = null;
        emetteur = null;
        destinataire = null;
        copie = null;
        corbeilleActions.setMessageCriteriaFromRecherche(null);
        // vide le cache du contentView pour ré-executer la requête
        if (contentViewActions != null) {
            contentViewActions.reset(SolonEppContentView.CORBEILLE_MESSAGE_LIST_CONTENT_VIEW);
        }
        return corbeilleActions.navigateTo();
    }
    
    public boolean displayMessageRecherche() throws ClientException {
      if (navigationContext.getCurrentDocument() != null
              && navigationContext.getCurrentDocument().getType().equals(SolonEppConstant.EVENEMENT_DOC_TYPE)) {
          return true;
      }
      return corbeilleActions.oneMessageInList(contentViewActions.getCurrentContentView().getName());
  }

}