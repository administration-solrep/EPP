package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.ui.bean.SupervisionUsersListForm;
import fr.dila.ss.ui.bean.supervision.SupervisionUserList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.ss.ui.enums.SupervisionOnglet;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxSupervision")
public class SSSupervisionAjax extends SolonWebObject {
    public static final String URL_SUPERVISION = "/admin/supervision";
    private static final String KEY_DATE_CONNEXION = "dateConnexion";
    public static final String ID = "idTable";

    public SSSupervisionAjax() {
        super();
    }

    @GET
    @Path("actif")
    public ThTemplate getAllUsersConnected(@SwBeanParam SupervisionUsersListForm resultForm) {
        SupervisionUserList connectedUserList = UserSessionHelper.getUserSessionParameter(
            context,
            "supervisionUserActif",
            SupervisionUserList.class
        );
        context.putInContextData(SSContextDataKey.LIST_USERS_FORM, resultForm);
        if (connectedUserList == null) {
            connectedUserList = new SupervisionUserList();
            connectedUserList.setListe(SSUIServiceLocator.getSupervisionUIService().getAllUserConnected(context));
            UserSessionHelper.putUserSessionParameter(
                context,
                SSUserSessionKey.SUPERVISION_ACTIF_USER,
                connectedUserList
            );
        }
        UserSessionHelper.putUserSessionParameter(
            context,
            SSUserSessionKey.SUPERVISION_ONGLET,
            SupervisionOnglet.ACTIF
        );
        template.getData().put(STTemplateConstants.RESULT_LIST, connectedUserList.getListe());
        template.getData().put(STTemplateConstants.LST_COLONNES, connectedUserList.getListeColonnes(resultForm));
        template
            .getData()
            .put(STTemplateConstants.LST_SORTED_COLONNES, connectedUserList.getListeSortedColonnes(resultForm));
        template
            .getData()
            .put(
                STTemplateConstants.LST_SORTABLE_COLONNES,
                connectedUserList.getListeSortableAndVisibleColonnes(resultForm)
            );
        template.getData().put(STTemplateConstants.RESULT_FORM, resultForm);
        template.getData().put(STTemplateConstants.DATA_URL, URL_SUPERVISION);
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/admin/supervision/actif");
        template.getData().put(ID, "supConnectesResults");
        template.getData().put("mailAction", context.getActions(SSActionCategory.SUPERVISION_ACTION_MAIL));
        template
            .getData()
            .put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(SSActionCategory.SUPERVISION_ACTION_PRINT));
        template
            .getData()
            .put(
                STTemplateConstants.TITLE,
                ResourceHelper.getString(
                    "supervision.table.nb.utilisateurs.connectes",
                    connectedUserList.getListe().size()
                )
            );
        return template;
    }

    @GET
    @Path("inactif")
    public ThTemplate getUsersListNotConnected() {
        UserSessionHelper.putUserSessionParameter(
            context,
            SSUserSessionKey.SUPERVISION_ONGLET,
            SupervisionOnglet.INACTIF
        );
        ThTemplate template = new AjaxLayoutThTemplate(
            "fragments/supervision/supervision-utilisateurs-non-connectes",
            getMyContext()
        );
        String dateConnexion = UserSessionHelper.getUserSessionParameter(
            context,
            SSUserSessionKey.SUPERVISION_DATE_CONNEXION
        );
        return buildTemplateFromData(null, dateConnexion, template, ID);
    }

    @GET
    @Path("resultats")
    public ThTemplate getResult(@QueryParam("dateConnexion") String dateConnexion) {
        return buildTemplateFromData(null, dateConnexion, template, ID);
    }

    @GET
    @Path("tri")
    public ThTemplate getTriUsersNonConnectes(
        @QueryParam("dateConnexion") String dateConnexion,
        @SwBeanParam SupervisionUsersListForm resultForm
    ) {
        return buildTemplateFromData(resultForm, dateConnexion, template, ID);
    }

    private ThTemplate buildTemplateFromData(
        SupervisionUsersListForm resultForm,
        String dateConnexion,
        ThTemplate template,
        String idKey
    ) {
        resultForm = ObjectHelper.requireNonNullElseGet(resultForm, SupervisionUsersListForm::newForm);
        context.putInContextData(
            SSContextDataKey.DATE_CONNEXION,
            StringUtils.isNotBlank(dateConnexion) ? SolonDateConverter.DATE_SLASH.parseToCalendar(dateConnexion) : null
        );
        context.putInContextData(SSContextDataKey.LIST_USERS_FORM, resultForm);
        SupervisionUserList connectedUserList = new SupervisionUserList();

        template.getData().put("dateConnexion", dateConnexion);
        connectedUserList.setListe(SSUIServiceLocator.getSupervisionUIService().getAllUserNotConnectedSince(context));
        UserSessionHelper.putUserSessionParameter(
            context,
            SSUserSessionKey.SUPERVISION_INACTIF_USER,
            connectedUserList
        );
        UserSessionHelper.putUserSessionParameter(context, SSUserSessionKey.SUPERVISION_DATE_CONNEXION, dateConnexion);
        template.getData().put(KEY_DATE_CONNEXION, dateConnexion);
        template.getData().put("mailAction", context.getActions(SSActionCategory.SUPERVISION_ACTION_MAIL));
        template
            .getData()
            .put(
                SSTemplateConstants.PRINT_ACTIONS,
                context.getActions(SSActionCategory.SUPERVISION_ACTION_PRINT_NOT_CONNECTED)
            );
        template
            .getData()
            .put(
                STTemplateConstants.TITLE,
                ResourceHelper.getString(
                    "supervision.table.nb.utilisateurs.trouves",
                    connectedUserList.getListe().size()
                )
            );
        template.getData().put(idKey, "supNonConnectesResults");
        template.getData().put(STTemplateConstants.RESULT_LIST, connectedUserList.getListe());
        template.getData().put(STTemplateConstants.LST_COLONNES, connectedUserList.getListeColonnes(resultForm));
        template
            .getData()
            .put(STTemplateConstants.LST_SORTED_COLONNES, connectedUserList.getListeSortedColonnes(resultForm));
        template
            .getData()
            .put(
                STTemplateConstants.LST_SORTABLE_COLONNES,
                connectedUserList.getListeSortableAndVisibleColonnes(resultForm)
            );
        template.getData().put(STTemplateConstants.DATA_URL, URL_SUPERVISION);
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/admin/supervision/tri");
        return template;
    }

    @GET
    @Path("reinit")
    @Produces(MediaType.APPLICATION_JSON)
    public Object reinitSearch() {
        UserSessionHelper.clearUserSessionParameter(context, SSUserSessionKey.SUPERVISION_INACTIF_USER);
        UserSessionHelper.clearUserSessionParameter(context, SSUserSessionKey.SUPERVISION_DATE_CONNEXION);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/supervision/table-supervision-utilisateurs", getMyContext());
    }
}
