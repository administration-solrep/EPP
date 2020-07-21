package fr.dila.solonepp.web.action.metadonnees;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.contentview.seam.ContentViewActions;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.transaction.TransactionHelper;
import org.richfaces.component.html.HtmlCalendar;
import org.richfaces.component.html.HtmlInputText;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.constant.SolonEppViewConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.dto.PieceJointeDTO;
import fr.dila.solonepp.api.dto.TableReferenceDTO;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.action.corbeille.CorbeilleActionsBean;
import fr.dila.solonepp.web.action.evenement.EvenementActionsBean;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.VocabularyServiceImpl;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.web.administration.organigramme.OrganigrammeTreeBean;
import fr.dila.st.web.context.NavigationContextBean;

@Name("metadonneesActions")
@Scope(ScopeType.CONVERSATION)
public class MetaDonneesActionsBean implements Serializable {

    private static final long serialVersionUID = -4721596289835489136L;


    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(MetaDonneesActionsBean.class);
    
    
    private static final String METADONNEES_ERREUR_RECUPERATION = "metadonnees.erreur.recuperation";

    private static final String METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE = "metadonnees.erreur.recuperation.piece.jointe";

    public static final String LAYOUT_MODE_RECTIFIER = "rectifier";

    public static final String LAYOUT_MODE_COMPLETER = "completer";

    public static final String LAYOUT_MODE_CREER = "edit";

    @In(create = true, required = false)
    protected transient NavigationContextBean navigationContext;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true, required = false)
    protected transient FacesContext facesContext;

    @In(create = true, required = false)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(required = true, create = true)
    protected EppPrincipal eppPrincipal;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true, required = false)
    protected transient CorbeilleActionsBean corbeilleActions;

    @In(create = true, required = false)
    protected transient EvenementActionsBean evenementActions;

    @In(create = true, required = false)
    protected transient ContentViewActions contentViewActions;

    @In(create = true, required = false)
    protected transient OrganigrammeTreeBean organigrammeTree;

    /**
     * {@link PropertyDescriptor} cache
     */
    private Map<String, Map<String, PropertyDescriptor>> currentMap;

    @RequestParameter
    protected String tableReference;
    
    
    @RequestParameter
    protected String typeOrganisme ;

    /**
     * Identifiant du widget de la requête courante.
     */
    @RequestParameter
    protected String widgetId;

    protected String currentLayoutMode;

    protected Evenement currentEvent = null;

    protected DocumentModel currentDossierDoc = null;

    protected Set<String> evtTypeSet = null;

    /**
     * isColumnVisible
     * 
     * @param columnName nom de la metadonnée
     * @return true s'il existe un {@link PropertyDescriptor} correspondant a la colonne
     */
    public Boolean isColumnVisible(String columnName) {

        if (StringUtils.isNotEmpty(columnName)) {

            // meta toujours visible
            if ("NO_RESTRICTION".equals(columnName)) {
                return true;
            }
            final Evenement evt = getCurrentEvenement();
            if (evt != null) {
                try {
                    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(columnName, evt);

                    if (propertyDescriptor == null || propertyDescriptor.isHidden()) {
                    	return false;
                    }
                    
                    if (propertyDescriptor.isVisibility()) {
                        // verification si le principal fait partie du senat pour les propertyDescriptor pouvant être vu que par le senat
                        return eppPrincipal.isInstitutionSenat();
                    }

                    return true;

                } catch (ClientException e) {
                    logErrorRecuperation(evt, e);
                }
            }
        }

        return false;

    }

    /**
     * Retourne le label pour la metadonnée s'il existe, sinon chaine vide
     * 
     * @param columnName
     * @return
     */
    public String getCutomWidgetLabel(String columnName) {
        if (StringUtils.isNotEmpty(columnName)) {
            final Evenement evt = getCurrentEvenement();
            if (evt != null) {
                try {
                    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(columnName, evt);
                    if (propertyDescriptor != null) {
                        if (StringUtils.isNotBlank(propertyDescriptor.getLabel())) {
                            return propertyDescriptor.getLabel();
                        }
                    }
                } catch (ClientException e) {
                    logErrorRecuperation(evt, e);
                }
            }
        }
        return "";
    }

    /**
     * isColumnVisible
     * 
     * @param columnName nom de la metadonnée
     * @return true s'il existe un {@link PropertyDescriptor} correspondant a la colonne
     * @throws ClientException
     */
    public Boolean isColumnVisibleFicheDossier(String columnName) throws ClientException {

        final DossierService dossierService = SolonEppServiceLocator.getDossierService();

        if (StringUtils.isNotEmpty(columnName)) {
            if (SolonEppSchemaConstant.EVENEMENT_DOSSIER_PROPERTY.equals(columnName)) {
                return true;
            }
            final Evenement evt = getCurrentEvenement();

			if (evt != null && (currentEvent == null || !evt.getTitle().equals(currentEvent.getTitle()))) {
				currentEvent = evt;
				currentDossierDoc = dossierService.getDossier(documentManager, evt.getDossier());
				evtTypeSet = dossierService.getEvenementTypeDossierList(documentManager, currentDossierDoc);
			}

            if (evtTypeSet != null) {
                try {
                    return isFicheDossier(columnName, evtTypeSet);
                } catch (ClientException e) {
                    logErrorRecuperation(evt, e);
                }
            }
        }
        return false;
    }

    private boolean isFicheDossier(final String columnName, final Set<String> evtTypeSet) throws ClientException {

        if (currentMap == null) {
            currentMap = new HashMap<String, Map<String, PropertyDescriptor>>();
        }

        for (String type : evtTypeSet) {
            Map<String, PropertyDescriptor> map = currentMap.get(type);
            if (map == null) {
                // on vide la currentMap pour ne garder dans le bean que 1 typeEvenement
                // currentMap.clear();

                map = SolonEppServiceLocator.getMetaDonneesService().getMapProperty(type);
                currentMap.put(type, map);
            }
            PropertyDescriptor propertyDescriptor = map.get(columnName);
            if (propertyDescriptor != null && propertyDescriptor.isFicheDossier()) {
                if (propertyDescriptor.isVisibility()) {
                    // verification si le principal fait partie du senat pour les propertyDescriptor pouvant être vu que par le senat
                    return eppPrincipal.isInstitutionSenat();
                }
                return true;
            }
        }

        return false;
    }

    private PropertyDescriptor getPropertyDescriptor(final String columnName, final Evenement evt) throws ClientException {

        if (currentMap == null) {
            currentMap = new HashMap<String, Map<String, PropertyDescriptor>>();
        }

        Map<String, PropertyDescriptor> map = currentMap.get(evt.getTypeEvenement());
        if (map == null) {
            // on vide la currentMap pour ne garder dans le bean que 1 typeEvenement
            currentMap.clear();

            map = SolonEppServiceLocator.getMetaDonneesService().getMapProperty(evt.getTypeEvenement());
            currentMap.put(evt.getTypeEvenement(), map);
        }

        PropertyDescriptor propertyDescriptor = map.get(columnName);
        return propertyDescriptor;
    }

    private Evenement getCurrentEvenement() {
        if (!SolonEppViewConstant.VIEW_CREATE_EVENEMENT.equals(navigationContext.getCurrentView())
                && !SolonEppViewConstant.VIEW_CREATE_EVENEMENT_SUCCESSIF.equals(navigationContext.getCurrentView())
                && navigationContext.getCurrentDocument() instanceof DocumentModel) {
            return navigationContext.getCurrentDocument().getAdapter(Evenement.class);
        } else if (SolonEppViewConstant.VIEW_CREATE_EVENEMENT.equals(navigationContext.getCurrentView())) {
            DocumentModel doc = evenementActions.getCurrentEvenementForCreation();
            if (doc != null) {
                return doc.getAdapter(Evenement.class);
            }
        } else if (SolonEppViewConstant.VIEW_CREATE_EVENEMENT_SUCCESSIF.equals(navigationContext.getCurrentView())) {
            DocumentModel doc = evenementActions.getCurrentEvenementForCreation();
            if (doc != null) {
                return doc.getAdapter(Evenement.class);
            }
        }

        return null;
    }

    private void logErrorRecuperation(final Evenement evt, ClientException e) {
        String message = resourcesAccessor.getMessages().get(METADONNEES_ERREUR_RECUPERATION);        
        LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_META_DONNEE_TEC,message + evt.toString(), e) ;
        facesMessages.add(StatusMessage.Severity.WARN, message);
        TransactionHelper.setTransactionRollbackOnly();
    }

    /**
     * getWidgetMode
     * 
     * @param columnName nom de la metadonnée
     * @return 'edit' si le {@link PropertyDescriptor} de la metadonne est editable
     */
    public String getWidgetMode(String columnName) {

        if (StringUtils.isNotEmpty(columnName)) {
            final Evenement evt = getCurrentEvenement();
            if (evt != null) {
                try {

                    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(columnName, evt);
                    if (propertyDescriptor != null) {
                    	if (propertyDescriptor.isHidden()) {
                    		return "hidden";
                    	}

                        if (propertyDescriptor.isModifiable() && !propertyDescriptor.isRenseignerEpp()) {

                            if (StringUtils.isNotEmpty(currentLayoutMode)
                                    && (LAYOUT_MODE_RECTIFIER.equals(currentLayoutMode) || LAYOUT_MODE_COMPLETER.equals(currentLayoutMode))
                                    && ("emetteur".equals(columnName) || "destinataire".equals(columnName) || "destinataireCopie".equals(columnName) || "dossier"
                                            .equals(columnName))) {
                                return "view";
                            } else if (StringUtils.isNotEmpty(currentLayoutMode) && LAYOUT_MODE_COMPLETER.equals(currentLayoutMode)
                                    && propertyDescriptor.isObligatoire() && !propertyDescriptor.isMultiValue()) {
                                return "view";
                            } else if ("urlDossierAn".equals(columnName) && !eppPrincipal.isInstitutionAn() || "urlDossierSenat".equals(columnName)
                                    && !eppPrincipal.isInstitutionSenat()) {
                                return "view";
                            } else {
                                return "edit";
                            }
                            // cas de la création, mode vue pour les non modifiable
                        } else if (currentLayoutMode == null && !propertyDescriptor.isModifiable() && !propertyDescriptor.isRenseignerEpp()) {
                            return "view";
                        } else if (StringUtils.isNotEmpty(currentLayoutMode) && LAYOUT_MODE_RECTIFIER.equals(currentLayoutMode)
                                && propertyDescriptor.isRenseignerEpp()) {
                            return "hidden";
                        }
                    }
                } catch (ClientException e) {
                    logErrorRecuperation(evt, e);
                }
            }
        }

        return "view";

    }

    /**
     * isColumnEditable
     * 
     * @param columnName nom de la metadonnée
     * @return true si le {@link PropertyDescriptor} de la metadonne est editable
     */
    public Boolean isColumnRequired(String columnName) {

        if (StringUtils.isNotEmpty(columnName)) {
            final Evenement evt = getCurrentEvenement();
            if (evt != null) {
                try {

                    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(columnName, evt);
                    if (propertyDescriptor != null) {
                        return propertyDescriptor.isObligatoire();
                    }

                } catch (ClientException e) {
                    logErrorRecuperation(evt, e);
                }
            }
        }

        return false;
    }

    /**
     * isColumnRequiredWidget, retourne false si mode brouillon sinon la valeur de isColunmRequired
     * 
     * @param columnName nom de la metadonnée
     * @return true si le {@link PropertyDescriptor} de la metadonne est editable
     */
    public Boolean isColumnRequiredWidget(String columnName) {
        // enregistrerBrouillon = facesContext.getExternalContext().getRequestParameterMap().get("evenement_metadonnees:enregistrerBrouillon");

        if (evenementActions.getEnregistrerBrouillon() == null || !"true".equals(evenementActions.getEnregistrerBrouillon())) {
            return isColumnRequired(columnName);
        }
        return false;
    }

    public boolean displayRequiredField(String layoutName, String property, String enregistrerBrouillon) {

        if (!evenementActions.isDisplayRequired()) {
            return false;
        }
        if (evenementActions.getEnregistrerBrouillon() == null || !"true".equals(evenementActions.getEnregistrerBrouillon())) {

            // on regarde si le champ est vide
            DocumentModel doc = null;
            if ("metadonnees_evenement".equals(layoutName)) {
                doc = evenementActions.getCurrentEvenementForCreation();
            } else {
                doc = evenementActions.getCurrentVersionForCreation();
            }
            if (isColumnRequired(property)) {
                try {
                    Object value = doc.getPropertyValue(property);
                    if (value == null || value instanceof String && ((String) value).isEmpty()) {
                        return true;
                    } else if (value.toString().equals("[]")) {
                        // HACK : cas du tableau vide
                        return true;
                    }
                } catch (Exception e) {
                    LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_PROPERTY_TEC,"erreur lors du test d'affichage du message de validation d'un champ") ;
                }
            }
        }
        return false;
    }

    public String getLibelle(String typeEvenement) {
        try {
            EvenementTypeDescriptor evenementTypeDescriptor = SolonEppServiceLocator.getEvenementTypeService().getEvenementType(typeEvenement);
            return evenementTypeDescriptor.getLabel();
        } catch (ClientException e) {
            return null;
        }
    }

    private UIComponent findComponent(UIComponent c, String id) {
        if (id.equals(c.getId())) {
            return c;
        }
        Iterator<UIComponent> kids = c.getFacetsAndChildren();
        while (kids.hasNext()) {
            UIComponent found = findComponent(kids.next(), id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * ajout d'un text dans un field
     * 
     * @param field
     */
    public void addText(String widgetId, String schema, String property) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        UIComponent c = findComponent(root, widgetId);

        String text = (String) ((HtmlInputText) c).getSubmittedValue();

        if (StringUtils.isNotBlank(text)) {
            DocumentModel doc = null;
            if (schema.equals(SolonEppSchemaConstant.VERSION_SCHEMA)) {
                doc = evenementActions.getCurrentVersionForCreation();
            } else if (schema.equals(SolonEppSchemaConstant.EVENEMENT_SCHEMA)) {
                doc = evenementActions.getCurrentEvenementForCreation();
            }
            if (doc == null) {
                throw new UnsupportedOperationException();
            }

            List<String> selectionList = PropertyUtil.getStringListProperty(doc, schema, property);
            if (selectionList != null) {
                if (!selectionList.contains(text)) {
                    selectionList.add(text);
                    try {
                        doc.setProperty(schema, property, selectionList);
                        ((HtmlInputText) c).setSubmittedValue(null);
                    } catch (ClientException e) {
                        LOGGER.error(documentManager, STLogEnumImpl.FAIL_SET_PROPERTY_TEC," AddText : " + property) ;                        
                    }
                }
            }

        }
    }

    public void removeText(String value, String schema, String property) {
        DocumentModel doc = null;
        if (schema.equals(SolonEppSchemaConstant.VERSION_SCHEMA)) {
            doc = evenementActions.getCurrentVersionForCreation();
        } else if (schema.equals(SolonEppSchemaConstant.EVENEMENT_SCHEMA)) {
            doc = evenementActions.getCurrentEvenementForCreation();
        }
        if (doc == null) {
            throw new UnsupportedOperationException();
        }

        List<String> listString = PropertyUtil.getStringListProperty(doc, schema, property);
        listString.remove(value);
        try {

            doc.setProperty(schema, property, listString);

        } catch (ClientException e) {            
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_SET_PROPERTY_TEC," RemoveText : " + property) ;
        }
    }

    public String getFormattedDate(Date date) {
        if (date != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            return format.format(date);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    public List<?> getListFromArray(Object[] array) {
        if (array != null) {
            return Arrays.asList(array);
        } else {
            return new ArrayList();
        }
    }

    /**
     * ajout d'un text dans un field
     * 
     * @param field
     */
    public void addDate(String widgetId, String schema, String property) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIViewRoot root = context.getViewRoot();
        UIComponent c = findComponent(root, widgetId);

        String dateString = (String) ((HtmlCalendar) c).getSubmittedValue();
        Calendar date = null;
        try {
            date = DateUtil.parse(dateString);
        } catch (ClientException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (date != null) {
            DocumentModel doc = null;
            if (schema.equals(SolonEppSchemaConstant.VERSION_SCHEMA)) {
                doc = evenementActions.getCurrentVersionForCreation();
            } else if (schema.equals(SolonEppSchemaConstant.EVENEMENT_SCHEMA)) {
                doc = evenementActions.getCurrentEvenementForCreation();
            }
            if (doc == null) {
                throw new UnsupportedOperationException();
            }

            List<Calendar> selectionList = new ArrayList<Calendar>();
            Object[] selectionArray = (Object[]) PropertyUtil.getProperty(doc, schema, property);
            if (selectionArray != null) {
                selectionList.addAll(Arrays.asList((Calendar[]) selectionArray));
            }

            if (!selectionList.contains(date)) {
                selectionList.add(date);
                try {
                    doc.setProperty(schema, property, selectionList.toArray());
                    ((HtmlCalendar) c).setSubmittedValue(null);
                } catch (ClientException e) {
                    LOGGER.error(documentManager, STLogEnumImpl.FAIL_SET_PROPERTY_TEC," AddDate : " + property) ;                    
                }
            }
        }
    }

    public void removeDate(Date value, String schema, String property) {
        DocumentModel doc = null;
        if (schema.equals(SolonEppSchemaConstant.VERSION_SCHEMA)) {
            doc = evenementActions.getCurrentVersionForCreation();
        } else if (schema.equals(SolonEppSchemaConstant.EVENEMENT_SCHEMA)) {
            doc = evenementActions.getCurrentEvenementForCreation();
        }
        if (doc == null) {
            throw new UnsupportedOperationException();
        }

        List<Calendar> selectionList = new ArrayList<Calendar>();
        Object[] selectionArray = (Object[]) PropertyUtil.getProperty(doc, schema, property);
        if (selectionArray != null) {
            selectionList.addAll(Arrays.asList((Calendar[]) selectionArray));
        }
        if (value == null) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(value);
        if (selectionList.contains(cal)) {
            selectionList.remove(cal);
            try {
                doc.setProperty(schema, property, selectionList.toArray());
            } catch (ClientException e) {
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_SET_PROPERTY_TEC," AddDate : " + property) ;
            }
        }
    }

    /**
     * Recherche de suggestion dans une table de reference
     * 
     * @param input
     * @return
     */
    public Object getSuggestions(Object input) {
        try {
            boolean fullTableRef = false;
            if (widgetId != null
                    && facesContext.getExternalContext().getRequestParameterMap()
                            .get("evenement_metadonnees:nxl_metadonnees_version:" + widgetId + "_full_table_ref") != null) {
                fullTableRef = true;
            }
            return SolonEppServiceLocator.getTableReferenceService().searchTableReference(documentManager, (String) input, tableReference,
                    eppPrincipal.getInstitutionId(), fullTableRef,this.typeOrganisme);
        } catch (ClientException e) {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_TABLE_REFERENCE_TEC, "tableReference = " + tableReference, e);            
            return null;
        }
    }

    /**
     * Recherche de suggestion dans une table de reference
     * 
     * @param input
     * @return
     */
    public Object getSuggestionsAll(Object input) {
        try {
            boolean fullTableRef = false;
            if (widgetId != null
                    && facesContext.getExternalContext().getRequestParameterMap()
                            .get("evenement_metadonnees:nxl_metadonnees_version:" + widgetId + "_full_table_ref") != null) {
                fullTableRef = true;
            }
            return SolonEppServiceLocator.getTableReferenceService().searchTableReference(documentManager, (String) input, tableReference, null,
                    fullTableRef, this.typeOrganisme);
        } catch (ClientException e) {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_TABLE_REFERENCE_TEC, "tableReference = " + tableReference, e);
            return null;
        }
    }

    /**
     * retourne la description de l'element de la table de reference
     * 
     * @param tableRef
     * @param identifiant
     * @return
     */
    public String getTitleFromTableReference(String tableRef, String identifiant) {
        if (StringUtils.isEmpty(identifiant)) {
            return null;
        }
        try {
            // Calendar cal = Calendar.getInstance();
            // if (corbeilleActions.getCurrentVersion() != null) {
            // cal = corbeilleActions.getCurrentVersion().getHorodatage();
            // }
            TableReferenceDTO tableReferenceDTO = SolonEppServiceLocator.getTableReferenceService().findTableReferenceByIdAndType(documentManager,
                    identifiant, tableRef, null);
            if (tableReferenceDTO != null) {
                return tableReferenceDTO.getTitle();
            } else {
                return "**inconnu**";
            }

        } catch (ClientException e) {
            LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_TABLE_REFERENCE_TEC, "TableReference = " + tableRef + ", evenement = "
                    + navigationContext.getCurrentDocument().getAdapter(Evenement.class), e);
            return null;
        }
    }

    public String getLabelNatureVersion() {

        StringBuilder label = new StringBuilder();
        String nature = null;
        boolean versionCourante = false;
        Version currentVersion = corbeilleActions.getCurrentVersion();
        if (currentVersion != null) {
            nature = currentVersion.getNature();
            versionCourante = currentVersion.isVersionCourante();
        }

        if (StringUtils.isNotBlank(nature)) {
            label.append(resourcesAccessor.getMessages().get("metadonnees.version.nature." + nature));
        }
        
        if (versionCourante) {
            if (StringUtils.isNotBlank(nature)) {
                label.append(" - ");
            }
            label.append(resourcesAccessor.getMessages().get("metadonnees.version.courante"));
        }
        
        return label.toString();
    }
    
    /**
     * Retourne la liste des pieces jointe d'un certain type de la version courant
     * 
     * @param typePieceJointe
     * @return
     */
    public List<PieceJointeDTO> getListPieceJointeFichier(String typePieceJointe) {
        String idVersion = corbeilleActions.getIdCurrentVersion();
        if (idVersion != null) {
            try {
                return SolonEppServiceLocator.getPieceJointeService().findAllPieceJointeFichierByVersionAndType(typePieceJointe, idVersion,
                        documentManager);
            } catch (ClientException e) {
                String message = resourcesAccessor.getMessages().get(METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE);
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_METADONNEES_PIECE_JOINTE,message,e) ;
                facesMessages.add(StatusMessage.Severity.WARN, message);
                TransactionHelper.setTransactionRollbackOnly();
            }
        }

        return null;
    }

    /**
     * Retourne la liste des pieces jointe d'un certain type de la version courant
     * 
     * @param typePieceJointe
     * @return
     */
    public List<PieceJointeDTO> getDeletedListPieceJointe(String typePieceJointe) {
        String idVersion = corbeilleActions.getIdCurrentVersion();
		Evenement curEvenement = getCurrentEvenement();
        if (idVersion != null && curEvenement !=null) {
            try {
                return SolonEppServiceLocator.getPieceJointeService().getDeletedPieceJointeList(documentManager, typePieceJointe,
                        curEvenement.getDocument(), corbeilleActions.getCurrentVersion().getDocument());
            } catch (ClientException e) {
                String message = resourcesAccessor.getMessages().get(METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE);
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_METADONNEES_PIECE_JOINTE,message,e) ;
                facesMessages.add(StatusMessage.Severity.WARN, message);
                TransactionHelper.setTransactionRollbackOnly();
            }
        }

        return null;
    }

    public boolean isNewPieceJointe(String typePieceJointe, String titrePieceJointe) {
        String idVersion = corbeilleActions.getIdCurrentVersion();
        Evenement curEvenement = getCurrentEvenement();
        if (idVersion != null && curEvenement!=null) {
            try {
                Set<String> listNew = SolonEppServiceLocator.getPieceJointeService().getNewPieceJointeTitreList(documentManager, typePieceJointe,
                        curEvenement.getDocument(), corbeilleActions.getCurrentVersion().getDocument());
                return listNew.contains(titrePieceJointe);
            } catch (ClientException e) {
                String message = resourcesAccessor.getMessages().get(METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE);
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_METADONNEES_PIECE_JOINTE,message,e) ;
                facesMessages.add(StatusMessage.Severity.WARN, message);
                TransactionHelper.setTransactionRollbackOnly();
            }
        }

        return false;
    }

    public String getPieceJointeType(String pieceJointeType) {

        // cherche un label custom dans evenement-type-contrib, sinon dans le vocabulaire
        try {
            final Evenement evt = getCurrentEvenement();
			if (evt != null) {
				EvenementTypeDescriptor evenementTypeDescriptor = SolonEppServiceLocator.getEvenementTypeService()
						.getEvenementType(evt.getTypeEvenement());
				Map<String, PieceJointeDescriptor> pieceJointeMap = evenementTypeDescriptor.getPieceJointe();
				PieceJointeDescriptor pieceJointeDescriptor = pieceJointeMap.get(pieceJointeType);
				if (pieceJointeDescriptor != null && StringUtils.isNotBlank(pieceJointeDescriptor.getLabel())) {
					return pieceJointeDescriptor.getLabel();
				}
			}
        } catch (ClientException e) {
            return null;
        }

        String label = STServiceLocator.getVocabularyService().getEntryLabel(SolonEppVocabularyConstant.VOCABULARY_PIECE_JOINTE_DIRECTORY,
                pieceJointeType);
        if (VocabularyServiceImpl.UNKNOWN_ENTRY.equals(label)) {
            return pieceJointeType;
        } else {
            return label;
        }
    }

    public String getDeletedUrl(String typePieceJointe) {
        String idVersion = corbeilleActions.getIdCurrentVersion();
        Evenement curEvenement = getCurrentEvenement();
        if (idVersion != null && curEvenement!=null) {
            try {
                return SolonEppServiceLocator.getPieceJointeService().getDeletedUrl(documentManager, typePieceJointe,
                        curEvenement.getDocument(), corbeilleActions.getCurrentVersion().getDocument());
            } catch (ClientException e) {
                String message = resourcesAccessor.getMessages().get(METADONNEES_ERREUR_RECUPERATION_PIECE_JOINTE);
                LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_METADONNEES_PIECE_JOINTE,message,e) ;
                facesMessages.add(StatusMessage.Severity.WARN, message);
                TransactionHelper.setTransactionRollbackOnly();
            }
        }
        return "";
    }

    public List<MetaDonneesDescriptor> getListMetadonnees() {
        return SolonEppServiceLocator.getMetaDonneesService().getAll();
    }

    public void removeInstitution(String valueToRemove, String property) {
        DocumentModel eventDoc = evenementActions.getCurrentEvenementForCreation();

        if (eventDoc == null) {
            // lors de l'affichage simple on fait rien
            return;
        }

        try {
            Object value = eventDoc.getProperty(SolonEppSchemaConstant.EVENEMENT_SCHEMA, property);
            if (value instanceof List<?>) {
                List<Object> list = new ArrayList<Object>();
                list.remove(valueToRemove);
                eventDoc.setProperty(SolonEppSchemaConstant.EVENEMENT_SCHEMA, property, list);
                // cas destinataire rien a faire
            } else {
                eventDoc.setProperty(SolonEppSchemaConstant.EVENEMENT_SCHEMA, property, null);
                if ("emetteur".equals(property)) {
                	fireEmetteurChangeEvent();
                }
            }

        } catch (ClientException e) {
            LOGGER.error(documentManager, EppLogEnumImpl.FAIL_REMOVE_INSTITUTION,e) ;            
        }

    }

    public void addInstitution(String valueToAdd, String property) {
        DocumentModel eventDoc = evenementActions.getCurrentEvenementForCreation();

        if (eventDoc == null) {
            // lors de l'affichage simple on fait rien
            return;
        }

        try {
            Object value = eventDoc.getProperty(SolonEppSchemaConstant.EVENEMENT_SCHEMA, property);
            if (value instanceof List<?>) {
                List<Object> list = new ArrayList<Object>();
                list.add(valueToAdd);
                eventDoc.setProperty(SolonEppSchemaConstant.EVENEMENT_SCHEMA, property, list);
                // cas destinataire rien a faire
            } else {
                eventDoc.setProperty(SolonEppSchemaConstant.EVENEMENT_SCHEMA, property, valueToAdd);
                if ("emetteur".equals(property)) {
                	fireEmetteurChangeEvent();
                }
            }

            // check des valeurs pour pas qu'il y est 2 fois les mêmes dans emetteur/destinataire/copie
            final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();

            Evenement evenement = eventDoc.getAdapter(Evenement.class);
            MetaDonneesDescriptor metaDonneesDescriptor = metaDonneesService.getEvenementType(evenement.getTypeEvenement());

            EvenementMetaDonneesDescriptor evenementMetaDonneesDescriptor = metaDonneesDescriptor.getEvenement();
            if (evenementMetaDonneesDescriptor != null) {

                String emetteur = evenement.getEmetteur();
                String destinataire = evenement.getDestinataire();

                if (emetteur.equals(destinataire)) {
                    evenement.setDestinataire(null);
                }

                if (StringUtils.isEmpty(destinataire)) {
                    PropertyDescriptor propertyDescriptor = evenementMetaDonneesDescriptor.getProperty().get(
                            SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY);
                    // recuperation et settage des valeurs par defaut si possible du destinataire
                    if (propertyDescriptor != null) {

                        List<String> list = propertyDescriptor.getListInstitutions();
                        list.remove(emetteur);
                        if (list.size() == 1) {
                            // il en reste qu'un
                            evenement.setDestinataire(list.get(0));
                        }
                    }

                }

                if (StringUtils.isNotEmpty(evenement.getDestinataire())) {
                    PropertyDescriptor propertyDescriptor = evenementMetaDonneesDescriptor.getProperty().get(
                            SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_PROPERTY);
                    // recuperation et settage des valeurs par defaut si possible
                    if (propertyDescriptor != null) {
                        List<String> list = propertyDescriptor.getListInstitutions();
                        list.remove(emetteur);
                        list.remove(evenement.getDestinataire());
                        if (list.size() == 1) {
                            // il en reste qu'un
                            evenement.setDestinataireCopie(list);
                        }

                    }
                }

                List<String> destinataireCopie = evenement.getDestinataireCopie();

                if (destinataireCopie != null) {
                    destinataireCopie.remove(emetteur);
                    destinataireCopie.remove(destinataire);
                }

                evenement.setDestinataireCopie(destinataireCopie);

            }

        } catch (ClientException e) {
            LOGGER.error(documentManager, EppLogEnumImpl.FAIL_ADD_INSTITUTION,e) ;
        } finally {
            // indique a la popup de pas faire son rendu
            organigrammeTree.setVisible(Boolean.FALSE);
        }
    }

    /**
     * 
     * @param niveauLecture
     * @return true si le niveau de lecture est AN ou SENAT
     */
    public Boolean isNumeroLectureVisible(String selectionListId, String niveauLecture) {
        DocumentModel versionDoc = evenementActions.getCurrentVersionForCreation();
        if (versionDoc != null) {
            // creation en cours
            FacesContext context = FacesContext.getCurrentInstance();
            UIViewRoot root = context.getViewRoot();
            UIComponent selectionList = findComponent(root, selectionListId);
            if (selectionList instanceof ValueHolder) {
                niveauLecture = (String) ((ValueHolder) selectionList).getValue();
            }
        }

        return SolonEppVocabularyConstant.NIVEAU_LECTURE_AN_VALUE.equals(niveauLecture)
                || SolonEppVocabularyConstant.NIVEAU_LECTURE_SENAT_VALUE.equals(niveauLecture);
    }

    /**
     * Validation qu'une valeur est bien un int
     * 
     * @param facesContext
     * @param uIComponent
     * @param object
     * @throws ValidatorException
     */
    public void validateIntValue(FacesContext facesContext, UIComponent uIComponent, Object object) throws ValidatorException {
        String value = (String) object;
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // ce n'est pas un numero
            FacesMessage message = new FacesMessage();
            message.setSummary("Cette valeur est incorrecte. Nombre entier attendu.");
            throw new ValidatorException(message);
        }
    }

    /**
     * @return the currentLayoutMode
     */
    public String getCurrentLayoutMode() {
        return currentLayoutMode;
    }

    /**
     * @param currentLayoutMode the currentLayoutMode to set
     */
    public void setCurrentLayoutMode(String currentLayoutMode) {
        this.currentLayoutMode = currentLayoutMode;
    }

    /**
     * Retourne le dossier pour afficher la fiche dossier
     * 
     * @return
     * @throws ClientException
     */
    public DocumentModel getCurrentDossier() throws ClientException {
        Evenement evenement = navigationContext.getCurrentDocument().getAdapter(Evenement.class);
        String dossierId = evenement.getDossier();
        final DossierService dossierService = SolonEppServiceLocator.getDossierService();
        return dossierService.getDossier(documentManager, dossierId);
    }

    /**
     * Renvoie true si la méta a été modifiée
     * 
     * @param xpath le xpath de la méta
     * @return
     */
    public boolean notEqualsLastVersionPublieValue(String xpath, String mode) {
        Version currentVersion = null;
        if ("edit".equals(mode)) {
            DocumentModel currentVersionDoc = evenementActions.getCurrentVersionForCreation();
            if (currentVersionDoc != null && currentVersionDoc.getRef() != null) {
                currentVersion = currentVersionDoc.getAdapter(Version.class);
                if (currentVersionDoc.getId() == null || currentVersion.isEtatPublie()) {
                    return false;
                }
            }
        } else {
            currentVersion = corbeilleActions.getCurrentVersion();
        }
        if (currentVersion != null) {
            List<String> modifiedMetaList = currentVersion.getModifiedMetaList();
            return modifiedMetaList.contains(xpath) || this.isNiveauLectureModifie(xpath, modifiedMetaList) ; 
        }
        return false;
    }

      private boolean isNiveauLectureModifie(String xPath, List<String> metasModifiees) {
        if(xPath.contains(SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY)){
          for(String str  : metasModifiees){
            if(str.contains(SolonEppSchemaConstant.VERSION_NIVEAU_LECTURE_PROPERTY)){
              return true ;
            }                  
          }
        }
        return false  ;
      }

    
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object value) {

        if (value == null) {
            return true;
        } else if (value instanceof String && ((String) value).length() < 1) {
            return true;
        } else if (value.getClass().isArray()) {
            if (0 == java.lang.reflect.Array.getLength(value)) {
                return true;
            }
        } else if (value instanceof List) {
            if (((List) value).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public String getNiveauLectureLabel(String niveauLectureCode) {
        String label = STServiceLocator.getVocabularyService().getEntryLabel("niveau_lecture", niveauLectureCode);
        if (VocabularyServiceImpl.UNKNOWN_ENTRY.equals(label)) {
            return niveauLectureCode;
        } else {
            return label;
        }
    }
    
    public void fireEmetteurChangeEvent() {
    	Events.instance().raiseEvent("emetteurChangeEvent");
    }
}
