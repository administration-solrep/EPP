package fr.dila.ss.ui.services.actions.impl;

import static fr.dila.ss.ui.services.actions.SSActionsServiceLocator.getModeleFeuilleRouteActionService;

import fr.dila.cm.core.dao.FeuilleRouteDao;
import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.criteria.FeuilleRouteCriteria;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.core.enumeration.StatutModeleFDR;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.fdr.FeuilleRouteDTO;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.actions.SSRechercheModeleFeuilleRouteActionService;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.ss.ui.th.bean.RechercheModeleFdrForm;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.contentview.UfnxqlPageDocumentProvider;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public class SSRechercheModeleFeuilleRouteActionServiceImpl implements SSRechercheModeleFeuilleRouteActionService {
    private static final String INTITULE_KEY = "intitule";
    private static final String ID_KEY = "id";

    protected String getSearchQueryString(SpecificContext context) {
        FeuilleRouteCriteria criteria = getFeuilleRouteCriteria(context);
        FeuilleRouteDao feuilleRouteDao = new FeuilleRouteDao(context.getSession(), criteria);

        return feuilleRouteDao.getQueryString();
    }

    /**
     * Retourne les critères de recherche des modèles de feuille de route sous
     * forme textuelle.
     *
     * @return Critères de recherche des modèles de feuille de route sous forme
     *         textuelle
     */
    protected List<Object> getSearchQueryParameter(SpecificContext context) {
        FeuilleRouteCriteria criteria = getFeuilleRouteCriteria(context);
        FeuilleRouteDao feuilleRouteDao = new FeuilleRouteDao(context.getSession(), criteria);

        return feuilleRouteDao.getParamList();
    }

    protected FeuilleRouteCriteria getFeuilleRouteCriteria(SpecificContext context) {
        RechercheModeleFdrForm form = context.getFromContextData(SSContextDataKey.SEARCH_MODELEFDR_FORM);
        FeuilleRouteCriteria criteria = context.getFromContextData(SSContextDataKey.FEUILLE_ROUTE_CRITERIA);
        if (criteria == null) {
            criteria = new FeuilleRouteCriteria();
        }
        criteria.setCheckReadPermission(true);
        criteria.setIntitule(form.getIntitule());
        criteria.setMinistere(form.getMinistere());
        criteria.setCreationUtilisateur(form.getUtilisateurCreateur());
        if (form.getStatut() != null) {
            form.getStatut().updateFeuilleRouteCriteria(criteria);
        }
        criteria.setRoutingTaskType(form.getTypeEtape());
        criteria.setDistributionMailboxId(
            StringUtils.isBlank(form.getDestinataire()) ? "" : "poste-" + form.getDestinataire()
        );
        if (StringUtils.isNotBlank(form.getEcheanceIndicative())) {
            criteria.setDeadline(Long.valueOf(form.getEcheanceIndicative()));
        }
        criteria.setAutomaticValidation(form.getFranchissementAutomatique());
        criteria.setObligatoireSGG(form.getObligatoireSGG());
        criteria.setObligatoireMinistere(form.getObligatoireMinistere());
        if (form.getDateCreationStart() != null) {
            Calendar dateMin = form.getDateCreationStart();
            criteria.setCreationDateMin(new Date(dateMin.getTimeInMillis()));
        }
        if (form.getDateCreationEnd() != null) {
            Calendar dateMax = form.getDateCreationEnd();
            DateUtil.setDateToEndOfDay(dateMax);
            criteria.setCreationDateMax(new Date(dateMax.getTimeInMillis()));
        }

        return criteria;
    }

    @Override
    public ModeleFDRList getModeles(SpecificContext context) {
        ModeleFDRListForm listForm = context.getFromContextData(SSContextDataKey.LIST_MODELE_FDR);

        UfnxqlPageDocumentProvider provider = getProvider(context, listForm);
        List<DocumentModel> docs = provider.getCurrentPage();

        ModeleFDRList listResult = new ModeleFDRList();
        listResult.setListe(
            docs
                .stream()
                .map(doc -> this.getFeuilleRouteDTOFromDoc(doc, context.getSession()))
                .collect(Collectors.toList())
        );
        listResult.setNbTotal((int) provider.getResultsCount());
        listResult.buildColonnes(listForm);
        listResult.setHasSelect(true);
        listResult.setHasPagination(true);

        //Récupérer les actions des modèles
        getActionFDRList(context, listResult);

        return listResult;
    }

    protected UfnxqlPageDocumentProvider getProvider(SpecificContext context, ModeleFDRListForm listForm) {
        final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        DocumentModel currentDocument = feuilleRouteModelService.getFeuilleRouteModelFolder(context.getSession());
        context.setCurrentDocument(currentDocument);

        return buildProvider(context, listForm);
    }

    protected void getActionFDRList(SpecificContext context, ModeleFDRList modeleFDRList) {
        for (FeuilleRouteDTO fdrDTO : modeleFDRList.getListe()) {
            context.getContextData().put(INTITULE_KEY, fdrDTO.get(INTITULE_KEY));
            context.getContextData().put(ID_KEY, fdrDTO.get(ID_KEY));
            final String id = fdrDTO.getId();
            context.setCurrentDocument(id);
            context.putInContextData(
                "canDeleteModeleFDR",
                getModeleFeuilleRouteActionService().canUserDeleteRoute(context)
            );
            context.putInContextData("isModeleValide", StatutModeleFDR.VALIDE.name().equals(fdrDTO.getEtat()));
            context.putInContextData(
                "isLockByAnotherUser",
                LockUtils.isLockedByAnotherUser(context.getSession(), new IdRef(id))
            );
            fdrDTO.setActions(new ArrayList<>(context.getActions(SSActionCategory.MODELE_TAB_ACTION)));
        }
    }

    private UfnxqlPageDocumentProvider buildProvider(SpecificContext context, ModeleFDRListForm listForm) {
        UfnxqlPageDocumentProvider provider = listForm.getPageProvider(
            context.getSession(),
            "recherche_fdr_resultats",
            "r.",
            null
        );

        Map<String, Serializable> props = provider.getProperties();
        props.put("queryString", getSearchQueryString(context));
        props.put("parameters", (Serializable) getSearchQueryParameter(context));
        props.put("typeDocument", SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE);
        provider.setProperties(props);

        return provider;
    }

    @Override
    public FeuilleRouteDTO getFeuilleRouteDTOFromDoc(DocumentModel doc, CoreSession session) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        SSFeuilleRoute fdr = doc.getAdapter(SSFeuilleRoute.class);
        String etat = StatutModeleFDR.getStatutFromDoc(doc);
        String intitule = fdr.getTitle();
        STUserService userService = STServiceLocator.getSTUserService();
        String auteur = userService.getUserFullName(DublincorePropertyUtil.getCreator(doc));
        String ministereId = fdr.getMinistere();
        String ministere = StringUtils.isNotEmpty(ministereId)
            ? organigrammeService.getOrganigrammeNodeById(ministereId, OrganigrammeType.MINISTERE).getLabel()
            : "";

        Calendar modifiedDate = DublincorePropertyUtil.getModificationDate(doc);
        Boolean lock = doc.getLockInfo() != null;
        String lockOwner = STActionsServiceLocator.getSTLockActionService().getLockOwnerName(doc, session);
        final String lockOwnerFullName = STServiceLocator.getSTUserService().getUserFullName(lockOwner);
        final Calendar date = modifiedDate;
        return new FeuilleRouteDTO(
            doc.getId(),
            etat,
            intitule,
            ministere,
            auteur,
            SolonDateConverter.DATE_SLASH.format(date),
            lock,
            lockOwnerFullName
        );
    }
}
