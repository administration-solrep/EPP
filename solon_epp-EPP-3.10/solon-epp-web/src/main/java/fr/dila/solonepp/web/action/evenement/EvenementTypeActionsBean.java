package fr.dila.solonepp.web.action.evenement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.transaction.TransactionHelper;

import java.util.Collections;

import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.action.metadonnees.MetaDonneesActionsBean;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.web.context.NavigationContextBean;

/**
 * Actions sur les types d'événements.
 * 
 * @author jtremeaux
 */
@Name("evenementTypeActions")
@Scope(ScopeType.EVENT)
public class EvenementTypeActionsBean implements Serializable {
    private static final String AUTRES_COMMUNICATIONS = "Autres communications";

    private static final String COMMUNICATIONS_CONSEILLEES = "Communications conseillées";

    private static final String EVENEMENT_SUCCESSIF = "create.evenement.evenement.successif.recupertation.error";

    private static final String TYPE_EVENEMENT_VIDE = "create.evenement.type.evenement.vide";

    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -8856979171092339852L;

    /**
    * Logger surcouche socle de log4j
    */
    private static final STLogger LOGGER = STLogFactory.getLog(MetaDonneesActionsBean.class);

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true, required = false)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(required = true, create = true)
    protected EppPrincipal eppPrincipal;
    
    @In(required = true, create = true)
    private EvenementActionsBean evenementActions;

    /**
     * Liste de tous les éléments.
     */
    private List<SelectItem> listEvenement;

    /**
     * liste des evenements createurs
     */
    private List<SelectItem> listEvenementCreateur;
    
    
    protected final List<String> ignoredFromSortItems = Arrays.asList("GENERIQUE","FUSION","ALERTE","EVT53");

    private EvenementTypeDescriptor currentEvtTypeDescriptor = null;

    /**
     * Retourne la liste de tous les événements.
     * 
     * @return Liste de tous les événements
     */
    public List<SelectItem> getEvenementTypeList() {
        if (listEvenement == null) {
            listEvenement = new ArrayList<SelectItem>();
            final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
            List<EvenementTypeDescriptor> eventTypeList = evenementTypeService.findEvenementType();

            for (EvenementTypeDescriptor eventType : eventTypeList) {
                SelectItem item = new SelectItem(eventType.getName(), eventType.getLabel());
                listEvenement.add(item);
            }
        }
        
        this.sortEvent(listEvenement) ;
        
        return listEvenement;
    }

    private void sortEvent(List<SelectItem> listEvenement) {
        // tri par label
        Collections.sort(listEvenement, new Comparator<SelectItem>() {

            @Override
            public int compare(SelectItem o1, SelectItem o2) {
                //les événements générique toujours à la fin de la liste
                String value1 = (String) o1.getValue() ;
                String value2 = (String) o2.getValue() ;
                
                if(inIgnoredList(value1)&& !inIgnoredList(value2)){
                    return 1 ;
                }
                if(inIgnoredList(value2) && !inIgnoredList(value1)){
                    return -1 ;
                }            
                return (value1).compareTo(value2);
            }
        });
        
    }
    
    private boolean inIgnoredList(String value) {
        for(String str : this.ignoredFromSortItems){
            if(value != null && value.trim().contains(str)){
                return true  ;
            }
        }
        return false  ;
    }    
    
    /**
     * Retourne la liste de tous les événements d'une catégorie.
     * 
     * @param categorieEvenementId Identifiant technique de la catégorie d'événements
     * @return Liste de tous les événements
     * @throws ClientException
     */
    public List<SelectItem> getEvenementTypeList(String categorieEvenementId) throws ClientException {
        if (StringUtils.isBlank(categorieEvenementId)) {
            return getEvenementTypeList();
        }

        List<SelectItem> evenementTypeList = new ArrayList<SelectItem>();
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        List<EvenementTypeDescriptor> eventTypeList = evenementTypeService.findEvenementByCategory(categorieEvenementId);
        if (eventTypeList == null) {
            throw new ClientException("Procédure non trouvée: " + categorieEvenementId);
        }

        for (EvenementTypeDescriptor eventType : eventTypeList) {
            SelectItem item = new SelectItem(eventType.getName(), eventType.getLabel());
            evenementTypeList.add(item);
        }

        return evenementTypeList;
    }

    public List<SelectItem> getEvenementCreateurList() {
        if (listEvenementCreateur == null) {
            List<SelectItem> list = new ArrayList<SelectItem>();
            final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
            List<EvenementTypeDescriptor> eventTypeList = evenementTypeService.findEvenementTypeCreateur();

            buildListEvenement(list, eventTypeList);

            this.sortEvent(list) ;
            
            listEvenementCreateur = list;
        }
        return listEvenementCreateur;
    }

    private void buildListEvenement(List<SelectItem> list, List<EvenementTypeDescriptor> eventTypeList) {
        if (eventTypeList == null) {
            return;
        }
        for (EvenementTypeDescriptor eventType : eventTypeList) {
            // Ajout des evenements que si on est dans la liste des emetteurs
            if (eventType.getDistribution().getEmetteur().getInstitution().keySet().contains(eppPrincipal.getInstitutionId())) {
                SelectItem item = new SelectItem(eventType.getName(), eventType.getLabel());
                list.add(item);
            }
        }
    }

    public List<SelectItem> getEvenementSuccessifList() {

        String evenementType = null;
        if (navigationContext.getCurrentDocument() != null) {
            evenementType = navigationContext.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement();
        } else {
            String message = resourcesAccessor.getMessages().get(TYPE_EVENEMENT_VIDE);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        List<EvenementTypeDescriptor> eventTypeList;
        List<EvenementTypeDescriptor> eventConseilleTypeList = null;
        try {
            eventTypeList = evenementTypeService.findEvenementTypeSuccessifWithSameProcedure(evenementType);
            if (StringUtils.isNotBlank(evenementType)) {
                eventConseilleTypeList = evenementTypeService.findEvenementTypeSuccessif(documentManager, evenementType);
                eventTypeList.removeAll(eventConseilleTypeList);
            }

        } catch (ClientException e) {
            String message = resourcesAccessor.getMessages().get(EVENEMENT_SUCCESSIF);
            facesMessages.add(StatusMessage.Severity.WARN, message);
            TransactionHelper.setTransactionRollbackOnly();
            return null;
        }

        // tri par label
        Collections.sort(eventTypeList, new Comparator<EvenementTypeDescriptor>() {

            @Override
            public int compare(EvenementTypeDescriptor o1, EvenementTypeDescriptor o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        List<SelectItem> listConseille = new ArrayList<SelectItem>();
        buildListEvenement(listConseille, eventConseilleTypeList);

        List<SelectItem> listAutre = new ArrayList<SelectItem>();
        buildListEvenement(listAutre, eventTypeList);

        SelectItemGroup selectItemGroupSuivant = new SelectItemGroup(COMMUNICATIONS_CONSEILLEES);
        selectItemGroupSuivant.setSelectItems(listConseille.toArray(new SelectItem[0]));

        SelectItemGroup selectItemGroupOthers = new SelectItemGroup(AUTRES_COMMUNICATIONS);
        selectItemGroupOthers.setSelectItems(listAutre.toArray(new SelectItem[0]));

        List<SelectItem> options = new ArrayList<SelectItem>();
        options.add(selectItemGroupSuivant);
        options.add(selectItemGroupOthers);

        return options;

    }

    public EvenementTypeDescriptor getCurrentEvtTypeDescriptor() {
    	if (currentEvtTypeDescriptor ==null) {
            String evenementType = evenementActions.getCurrentTypeEvenement();
            if (evenementType == null) {
            	evenementType = evenementActions.getCurrentTypeEvenementSuccessif();
            }
    		try {
				currentEvtTypeDescriptor = SolonEppServiceLocator.getEvenementTypeService().getEvenementType(evenementType);
			} catch (ClientException ce) {
				LOGGER.error(documentManager, EppLogEnumImpl.FAIL_GET_DESCRIPTOR_FONC, ce);
			}
    	}
    	return currentEvtTypeDescriptor;
    }
    
    public void setCurrentEvtTypeDescriptor(EvenementTypeDescriptor currentEvtTypeDescriptor) {
    	this.currentEvtTypeDescriptor = currentEvtTypeDescriptor;
    }
}
