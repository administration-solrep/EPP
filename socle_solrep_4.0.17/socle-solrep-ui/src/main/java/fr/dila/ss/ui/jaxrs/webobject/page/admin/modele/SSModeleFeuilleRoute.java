package fr.dila.ss.ui.jaxrs.webobject.page.admin.modele;

import fr.dila.ss.core.enumeration.StatutModeleFDR;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.ss.ui.th.bean.RechercheModeleFdrForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.ss.ui.th.constants.SSURLConstants;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ModeleFeuilleRoute")
public class SSModeleFeuilleRoute extends SolonWebObject {
    public static final String ADD_FIRST_STEP_ACTION_KEY = "addFirstStepAction";
    public static final String MFDR_SEARCH_MAP = "modele_fdr_search";
    public static final String MAIN_CONTENT = "#main_content";
    protected static final String URL_MODIFICATION = "%s/modele/modification?id=%s" + MAIN_CONTENT;

    public SSModeleFeuilleRoute() {
        super();
    }

    @GET
    @Path("/modele/dupliquer")
    public Object dupliquerModeleFdr(@QueryParam("id") String id) {
        context.setCurrentDocument(id);

        if (context.getAction(SSActionEnum.ADMIN_MENU_MODELE_RECHERCHE) != null) {
            DocumentModel newDoc = SSActionsServiceLocator
                .getModeleFeuilleRouteActionService()
                .duplicateRouteModel(context);

            if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
                return getRechercheModeleFdr();
            } else {
                addMessageQueueInSession();
                String baseUrl = context.getFromContextData(STContextDataKey.BREADCRUMB_BASE_URL);
                return redirect(String.format(URL_MODIFICATION, baseUrl, newDoc.getId()));
            }
        }
        return getRechercheModeleFdr();
    }

    @GET
    @Path("/modeles/supprimerEtape")
    public ThTemplate suppressionEtape(@QueryParam("idModeles") List<String> idModeles) {
        template.setName("pages/admin/modele/deleteStepModeleFDR");
        template.setContext(context);

        List<String> params = idModeles.stream().map("idModeles"::concat).collect(Collectors.toList());
        String baseUrl = context.getFromContextData(STContextDataKey.BREADCRUMB_BASE_URL);
        context.setNavigationContextTitle(
            new Breadcrumb(
                ResourceHelper.getString("modeleFDR.suppression.etape.breadcrumb"),
                baseUrl + "/modele/supprimerEtape?" + StringUtils.join(params, '&'),
                Breadcrumb.SUBTITLE_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );

        Map<String, Object> map = new HashMap<>();

        map.put(SSTemplateConstants.TYPE_ETAPE, SSUIServiceLocator.getSSSelectValueUIService().getRoutingTaskTypes());
        map.put("idModeles", idModeles);
        map.put(STTemplateConstants.ACTION, context.getActions(SSActionCategory.MODELE_FDR_SUPPR_MASSE_ACTIONS));
        template.setData(map);

        return template;
    }

    protected List<SelectValueDTO> getTypeEtapeAjout(String idModele) {
        // Méthode surchargé pour pouvoir filtrer des type d'étape lors de
        // l'ajout/modification
        return SSUIServiceLocator.getSSSelectValueUIService().getRoutingTaskTypes();
    }

    @GET
    @Path("/modele/modifier")
    public Object modifierModele(@QueryParam("id") String id) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
        context.setCurrentDocument(id);

        if (modeleAction.canInvalidateRoute(context)) {
            modeleAction.invalidateRouteModel(context);

            if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
                addMessageQueueInSession();
            }
        }
        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getInfoQueue())) {
            addMessageQueueInSession();
            String baseUrl = context.getFromContextData(STContextDataKey.BREADCRUMB_BASE_URL);
            return redirect(String.format(URL_MODIFICATION, baseUrl, id));
        } else {
            return getRechercheModeleFdr();
        }
    }

    @GET
    @Path("/rechercher")
    public ThTemplate getRechercheModeleFdr() {
        template.setContext(context);
        template.setName("pages/admin/fdr/recherche-modele-fdr");
        String baseUrl = context.getFromContextData(STContextDataKey.BREADCRUMB_BASE_URL);
        context.setNavigationContextTitle(
            new Breadcrumb(
                "admin.modele.recherche.title",
                baseUrl + "/rechercher#focusResultat",
                Breadcrumb.TITLE_ORDER,
                template.getContext().getWebcontext().getRequest()
            )
        );

        Map<String, Object> map = buildMap();
        map.put(SSTemplateConstants.TYPE_ETAPE, SSUIServiceLocator.getSSSelectValueUIService().getRoutingTaskTypes());
        map.put(STTemplateConstants.ACTION, context.getAction(SSActionEnum.MODELE_ACTION_CREER_MODELE));
        map.put(
            STTemplateConstants.ADD_FAVORI_RECHERCHE_ACTION,
            context.getAction(SSActionEnum.ADD_FDR_FAVORI_RECHERCHE)
        );
        map.put("statutModeleFDROptin", StatutModeleFDR.getLabelKeys());
        setSpecificDataMap(map);
        template.setData(map);
        return template;
    }

    private Map<String, Object> buildMap() {
        @SuppressWarnings("unchecked")
        Map<String, Object> mapSearchForm = UserSessionHelper.getUserSessionParameter(
            context,
            MFDR_SEARCH_MAP,
            Map.class
        );
        if (mapSearchForm == null) {
            mapSearchForm = new HashMap<>();
            mapSearchForm.put(STTemplateConstants.SEARCH_FORM, new RechercheModeleFdrForm());
            return mapSearchForm;
        }
        context.putInContextData(
            SSContextDataKey.SEARCH_MODELEFDR_FORM,
            mapSearchForm.get(STTemplateConstants.SEARCH_FORM)
        );
        context.putInContextData(SSContextDataKey.LIST_MODELE_FDR, mapSearchForm.get(STTemplateConstants.RESULT_FORM));
        ModeleFDRList lstResults = SSActionsServiceLocator
            .getRechercheModeleFeuilleRouteActionService()
            .getModeles(context);
        mapSearchForm.put(STTemplateConstants.RESULT_LIST, lstResults);
        mapSearchForm.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        mapSearchForm.put(STTemplateConstants.NB_RESULTS, lstResults.getNbTotal());
        mapSearchForm.put(STTemplateConstants.DATA_URL, SSURLConstants.URL_ADMIN_RECHERCHE_FDR);
        mapSearchForm.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/fdr/rechercher/resultats");
        mapSearchForm.put(
            STTemplateConstants.GENERALE_ACTIONS,
            context.getActions(SSActionCategory.MODELE_FDR_ACTIONS)
        );
        mapSearchForm.put(
            SSTemplateConstants.FAVORIS_RECHERCHE_FDR_ACTIONS,
            context.getActions(SSActionCategory.FAVORIS_RECHERCHE_FDR_ACTIONS)
        );
        UserSessionHelper.putUserSessionParameter(context, MFDR_SEARCH_MAP, mapSearchForm);
        return mapSearchForm;
    }

    // Overriden pour EPG
    protected void setSpecificDataMap(Map<String, Object> map) {}

    protected void setTemplateDataModeleModification(ThTemplate template, ModeleFdrForm modeleForm) {
        if (
            StatutModeleFDR.BROUILLON.name().equals(modeleForm.getEtat()) &&
            Boolean.TRUE.equals(modeleForm.getIsLockByCurrentUser())
        ) {
            template.setName("pages/admin/modele/editModeleFDR");
        } else {
            template.setName("pages/admin/modele/consultModeleFDR");
        }
        String baseUrl = context.getFromContextData(STContextDataKey.BREADCRUMB_BASE_URL);
        context.setNavigationContextTitle(
            new Breadcrumb(
                modeleForm.getIntitule(),
                String.format(URL_MODIFICATION, baseUrl, modeleForm.getId()),
                Breadcrumb.SUBTITLE_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );
    }

    protected Map<String, Object> buildMapModeleModification(String idModele, ModeleFdrForm modeleForm) {
        Map<String, Object> map = new HashMap<>();
        // type d'étape (pour modal ajout étape)
        map.put(SSTemplateConstants.TYPE_ETAPE, getTypeEtapeAjout(idModele));
        map.put(SSTemplateConstants.MODELE_FORM, modeleForm);
        map.put(
            SSTemplateConstants.IS_ADMIN_FONCTIONNEL,
            PermissionHelper.isAdminFonctionnel(context.getSession().getPrincipal())
        );
        map.put(
            SSTemplateConstants.IS_ADMIN_MINISTERIEL,
            PermissionHelper.isAdminMinisteriel(context.getSession().getPrincipal())
        );
        map.put(
            SSTemplateConstants.MODELE_LEFT_ACTIONS,
            context.getActions(SSActionCategory.MODELE_FICHE_LEFT_ACTIONS)
        );
        map.put(
            SSTemplateConstants.MODELE_RIGHT_ACTIONS,
            context.getActions(SSActionCategory.MODELE_FICHE_RIGHT_ACTIONS)
        );
        map.put(SSTemplateConstants.PROFIL, context.getWebcontext().getPrincipal().getGroups());

        if (context.getNavigationContext().size() > 1) {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        } else {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, "");
        }
        return map;
    }

    protected Object endSaveFormModele(ModeleFdrForm modeleForm, boolean isCreation) {
        String baseUrl = context.getFromContextData(STContextDataKey.BREADCRUMB_BASE_URL);
        addMessageQueueInSession();
        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            return redirect(String.format(URL_MODIFICATION, baseUrl, modeleForm.getId()));
        } else {
            if (isCreation) {
                UserSessionHelper.putUserSessionParameter(context, SSUserSessionKey.MODELE_FORM, modeleForm);
                return redirect("/admin/fdr/modele/creation" + MAIN_CONTENT);
            } else {
                return redirect(String.format(URL_MODIFICATION, baseUrl, modeleForm.getId()));
            }
        }
    }

    protected Map<String, Object> buildMapCreationModele(ModeleFdrForm modeleForm) {
        Map<String, Object> map = new HashMap<>();
        map.put(SSTemplateConstants.MODELE_FORM, modeleForm);

        if (context.getNavigationContext().size() > 1) {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        } else {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, "");
        }
        return map;
    }

    protected void setNavigationContextCreationModele() {
        String baseUrl = context.getFromContextData(STContextDataKey.BREADCRUMB_BASE_URL);
        context.setNavigationContextTitle(
            new Breadcrumb(
                "Créer un modèle de feuilles de route",
                baseUrl + "/modele/creation",
                Breadcrumb.SUBTITLE_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );
    }
}
