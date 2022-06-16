package fr.dila.ss.ui.jaxrs.webobject.ajax.modele;

import fr.dila.ss.core.enumeration.StatutModeleFDR;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.ss.ui.th.bean.ModeleFdrEtapeSupprimeForm;
import fr.dila.ss.ui.th.bean.RechercheModeleFdrForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ModeleFeuilleRouteAjax")
public class SSModeleFeuilleRouteAjax extends SolonWebObject {
    public static final String MFDR_SEARCH_MAP = "modele_fdr_search";

    public SSModeleFeuilleRouteAjax() {
        super();
    }

    @Path("/modeles/supprimer")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response supprimerModele(@FormParam("id") String id) {
        context.setCurrentDocument(id);
        if (SSActionsServiceLocator.getModeleFeuilleRouteActionService().canUserDeleteRoute(context)) {
            ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
            modeleAction.deleteModele(context);
            addMessageQueueInSession();
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("/modele/retourList")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response retourListe(@FormParam("id") String id) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
        CoreSession session = context.getSession();
        DocumentModel modeleDoc = session.getDocument(new IdRef(id));
        context.setCurrentDocument(modeleDoc);

        if (LockUtils.isLockedByCurrentUser(session, modeleDoc.getRef())) {
            modeleAction.libererVerrou(context);
        }

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("/modele/unlock")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlockModele(@FormParam("id") String id) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
        context.setCurrentDocument(id);

        if (modeleAction.canUserLibererVerrou(context)) {
            modeleAction.libererVerrou(context);
            addMessageQueueInSession();
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("/modele/demandeValidation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response demandeValidationModele(@FormParam("id") String id) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
        context.setCurrentDocument(id);

        if (modeleAction.canRequestValidateRoute(context)) {
            modeleAction.requestValidateRouteModel(context);
            modeleAction.libererVerrou(context);
            if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
                addMessageQueueInSession();
            }
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("/modele/annulerDemandeValidation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response annulerDemandeValidationModele(@FormParam("id") String id) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
        context.setCurrentDocument(id);

        if (modeleAction.canCancelRequestValidateRoute(context)) {
            modeleAction.cancelRequestValidateRouteModel(context);

            if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
                addMessageQueueInSession();
            }
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("/modele/accepterDemandeValidation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response accepterDemandeValidationModele(@FormParam("id") String id) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
        context.setCurrentDocument(id);

        if (modeleAction.canValidateRoute(context)) {
            modeleAction.validateRouteModel(context);

            if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
                addMessageQueueInSession();
            }
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("/modele/refusValidation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response refusDemandeValidationModele(@FormParam("id") String id) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
        context.setCurrentDocument(id);

        if (modeleAction.canRefuseValidateRoute(context)) {
            modeleAction.refuseValidateRouteModel(context);

            if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
                addMessageQueueInSession();
            }
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("/modele/modifier")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifierModele(@FormParam("id") String id) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();
        context.setCurrentDocument(id);

        if (modeleAction.canInvalidateRoute(context)) {
            modeleAction.invalidateRouteModel(context);

            if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
                addMessageQueueInSession();
            }
        }
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @GET
    @Path("/modeles/showContentModalSupprimerEtape")
    public ThTemplate showContentModalSupprimerEtape(
        @QueryParam("idTypeEtape") String idTypeEtape,
        @QueryParam("idPoste") String idPoste,
        @QueryParam("idModeles[]") List<String> idModeles
    ) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("pages/admin/modele/deleteStepModeleFDRModalContent");

        context.putInContextData(SSContextDataKey.TYPE_ETAPE, idTypeEtape);
        context.putInContextData(SSContextDataKey.ID_POSTE, idPoste);
        context.putInContextData(SSContextDataKey.ID_MODELES, idModeles);
        template.setContext(context);

        // Récupérer la list de ModeleFdrEtapeSupprimeForm pour l'affichage des
        // étapes à supprimer
        List<ModeleFdrEtapeSupprimeForm> lModeleFdrEtapeSupprimeForm = SSActionsServiceLocator
            .getModeleFeuilleRouteActionService()
            .listStepsToDelete(context);

        Map<String, Object> map = new HashMap<>();
        map.put("listModeleFDR", lModeleFdrEtapeSupprimeForm);
        template.setData(map);

        return template;
    }

    @Path("/modeles/supprimerEtapeMasse")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response supprimerEtapeMasse(
        @FormParam("idTypeEtape") String idTypeEtape,
        @FormParam("idPoste") String idPoste,
        @FormParam("idModeles[]") List<String> idModeles
    ) {
        context.putInContextData(SSContextDataKey.TYPE_ETAPE, idTypeEtape);
        context.putInContextData(SSContextDataKey.ID_POSTE, idPoste);
        context.putInContextData(SSContextDataKey.ID_MODELES, idModeles);

        // Supprimer les étapes en masse
        SSActionsServiceLocator.getModeleFeuilleRouteActionService().deleteMultipleStepsFromRoute(context);

        // Comme je recharge la page si pas d'erreur je met en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            addMessageQueueInSession();
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    /**
     * @param searchForm
     * @param modeleFDRform
     * @return resultat de la recherche: liste de modèle de feuille de route
     */
    @POST
    @Path("/rechercher/resultats")
    public ThTemplate getResultSearch(
        @SwBeanParam RechercheModeleFdrForm searchForm,
        @SwBeanParam ModeleFDRListForm modeleFDRform
    ) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/fdr/recherche/listeModeleFDR");

        context.putInContextData(SSContextDataKey.SEARCH_MODELEFDR_FORM, searchForm);
        context.putInContextData(SSContextDataKey.LIST_MODELE_FDR, modeleFDRform);
        ModeleFDRList lstResults = SSActionsServiceLocator
            .getRechercheModeleFeuilleRouteActionService()
            .getModeles(context);

        if (searchForm.getUtilisateurCreateur() != null) {
            searchForm.setMapUtilisateurCreateur(
                Collections.singletonMap(
                    searchForm.getUtilisateurCreateur(),
                    STServiceLocator.getSTUserService().getUserFullNameWithUsername(searchForm.getUtilisateurCreateur())
                )
            );
        }

        if (searchForm.getMinistere() != null) {
            searchForm.setMapMinistere(
                Collections.singletonMap(
                    searchForm.getMinistere(),
                    STServiceLocator
                        .getOrganigrammeService()
                        .getOrganigrammeNodeById(searchForm.getMinistere(), OrganigrammeType.MINISTERE)
                        .getLabel()
                )
            );
        }

        if (searchForm.getDestinataire() != null) {
            searchForm.setMapDestinataire(
                Collections.singletonMap(
                    searchForm.getDestinataire(),
                    STServiceLocator
                        .getOrganigrammeService()
                        .getOrganigrammeNodeById(searchForm.getDestinataire(), OrganigrammeType.POSTE)
                        .getLabel()
                )
            );
        }

        Map<String, Object> map = new HashMap<>();
        map.put("statutModeleFDR", StatutModeleFDR.values());
        map.put(STTemplateConstants.RESULT_LIST, lstResults);
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.LST_SORTED_COLONNES, lstResults.getListeSortedColonnes());
        map.put(STTemplateConstants.LST_SORTABLE_COLONNES, lstResults.getListeSortableAndVisibleColonnes());
        map.put(STTemplateConstants.NB_RESULTS, lstResults.getNbTotal());
        map.put(STTemplateConstants.SEARCH_FORM, searchForm);
        map.put(STTemplateConstants.RESULT_FORM, modeleFDRform);
        map.put(
            STTemplateConstants.DATA_URL,
            isFromAdmin(context) ? "/admin/fdr/rechercher" : "/recherche/fdr/rechercher"
        );
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/fdr/rechercher/resultats");
        map.put(STTemplateConstants.ACTION, context.getAction(SSActionEnum.MODELE_ACTION_CREER_MODELE));
        map.put(STTemplateConstants.GENERALE_ACTIONS, context.getActions(SSActionCategory.MODELE_FDR_ACTIONS));
        map.put(
            SSTemplateConstants.FAVORIS_RECHERCHE_FDR_ACTIONS,
            context.getActions(SSActionCategory.FAVORIS_RECHERCHE_FDR_ACTIONS)
        );
        map.put(SSTemplateConstants.MASS_ACTIONS, context.getActions(SSActionCategory.MODELE_FDR_MASS_ACTIONS));
        map.put(STTemplateConstants.BASE_URL, isFromAdmin(context) ? "/admin/fdr" : "/recherche/fdr");
        // action non nul seulement pour EPG
        map.put(SSTemplateConstants.EXPORT_ACTIONS, context.getAction(SSActionEnum.RECHERCHE_MODELE_EXPORT));
        setSpecificDataMap(map);
        UserSessionHelper.putUserSessionParameter(context, MFDR_SEARCH_MAP, map);
        template.setContext(context);
        template.setData(map);
        return template;
    }

    /**
     * Réinitialisation de la recherche
     */
    @POST
    @Path("/reinit/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReinitSearchMfdr() {
        UserSessionHelper.putUserSessionParameter(context, MFDR_SEARCH_MAP, null);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    protected boolean isFromAdmin(SpecificContext context) {
        return SSActionsServiceLocator.getNavigationActionService().isFromAdmin(context);
    }

    // Données spécifiques par appli
    protected void setSpecificDataMap(Map<String, Object> map) {}
}
