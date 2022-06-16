package fr.dila.ss.ui.jaxrs.webobject.page.admin;

import static fr.dila.ss.ui.enums.SSActionCategory.SUPERVISION_ACTION_LIST;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.ui.bean.SupervisionUsersListForm;
import fr.dila.ss.ui.bean.supervision.SupervisionUserList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.ss.ui.enums.SupervisionOnglet;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.Onglet;
import fr.dila.st.ui.bean.OngletConteneur;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "Supervision")
public class SSSupervision extends SolonWebObject {
    public static final String URL_SUPERVISION = "/admin/supervision";

    @GET
    public ThTemplate getConnectedUsers(@SwBeanParam SupervisionUsersListForm resultForm) {
        if (context.getAction(SSActionEnum.ADMIN_USER_SUPERVISION) == null) {
            throw new STAuthorizationException(URL_SUPERVISION);
        }
        template.setName("pages/admin/supervision/supervision");

        context.setNavigationContextTitle(new Breadcrumb("Supervision", URL_SUPERVISION, Breadcrumb.TITLE_ORDER));
        template.setContext(context);

        // onglets
        SupervisionOnglet ongletSelected = UserSessionHelper.getUserSessionParameter(
            context,
            SSUserSessionKey.SUPERVISION_ONGLET
        );
        List<Action> actions = context.getActions(SUPERVISION_ACTION_LIST);
        template.getData().put(STTemplateConstants.MY_TABS, actionsToTabs(actions, ongletSelected));

        // boutons actions (mail + print)
        template.getData().put("mailAction", context.getActions(SSActionCategory.SUPERVISION_ACTION_MAIL));
        template
            .getData()
            .put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(SSActionCategory.SUPERVISION_ACTION_PRINT));

        SupervisionUserList connectedUserList = new SupervisionUserList();
        // On vide les paramètre de session
        UserSessionHelper.clearUserSessionParameter(context, SSUserSessionKey.SUPERVISION_ACTIF_USER);
        UserSessionHelper.clearUserSessionParameter(context, SSUserSessionKey.SUPERVISION_INACTIF_USER);

        context.putInContextData(SSContextDataKey.LIST_USERS_FORM, resultForm);

        if (SupervisionOnglet.INACTIF.equals(ongletSelected)) {
            String dateConnexion = UserSessionHelper.getUserSessionParameter(
                context,
                SSUserSessionKey.SUPERVISION_DATE_CONNEXION
            );
            if (StringUtils.isNotBlank(dateConnexion)) {
                context.putInContextData(
                    SSContextDataKey.DATE_CONNEXION,
                    StringUtils.isNotBlank(dateConnexion)
                        ? SolonDateConverter.DATE_SLASH.parseToCalendar(dateConnexion)
                        : null
                );
                connectedUserList.setListe(
                    SSUIServiceLocator.getSupervisionUIService().getAllUserNotConnectedSince(context)
                );
            }
            template.getData().put("dateConnexion", dateConnexion);
            template.getData().put("idTable", "supNonConnectesResults");
            template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/admin/supervision/tri");
        } else {
            connectedUserList.setListe(SSUIServiceLocator.getSupervisionUIService().getAllUserConnected(context));
            template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/admin/supervision/actif");
        }
        resultForm = ObjectHelper.requireNonNullElseGet(resultForm, SupervisionUsersListForm::newForm);
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

        return template;
    }

    private OngletConteneur actionsToTabs(List<Action> actions, SupervisionOnglet current) {
        SupervisionOnglet currentOnglet = current;
        if (Objects.isNull(currentOnglet)) {
            currentOnglet = SupervisionOnglet.ACTIF;
        }
        OngletConteneur conteneur = new OngletConteneur();
        List<Onglet> onglets = new ArrayList<>();
        for (Action action : actions) {
            Onglet onglet = new Onglet();
            onglet.setLabel(action.getLabel());
            onglet.setId((String) action.getProperties().get("name"));
            if (onglet.getId().equals(currentOnglet.getId())) {
                onglet.setFragmentFile((String) action.getProperties().get("fragmentFile"));
                onglet.setFragmentName((String) action.getProperties().get("fragmentName"));
                onglet.setIsActif(true);
                onglet.setScript((String) action.getProperties().get("script"));
            } else {
                onglet.setScript((String) action.getProperties().get("script"));
            }
            onglets.add(onglet);
        }
        conteneur.setOnglets(onglets);
        return conteneur;
    }

    @GET
    @Path("telecharger/pdf")
    @Produces("application/pdf")
    public Response telechargerPdf(@QueryParam("dateConnexion") String dateConnexion) {
        context.putInContextData(SSContextDataKey.BIRT_OUTPUT_FORMAT, BirtOutputFormat.PDF);
        fillContextData(dateConnexion);
        File file = SSUIServiceLocator.getSupervisionUIService().getUsersExport(context);
        return FileDownloadUtils.getInlinePdf(file);
    }

    @GET
    @Path("telecharger/xls")
    @Produces("application/vnd.ms-excel")
    public Response telechargerExcel(@QueryParam("dateConnexion") String dateConnexion) {
        fillContextData(dateConnexion);
        File file = SSUIServiceLocator.getSupervisionUIService().getUsersExport(context);
        return FileDownloadUtils.getAttachmentXls(file, "report.xls");
    }

    private void fillContextData(String dateConnexion) {
        SupervisionUsersListForm form = new SupervisionUsersListForm();
        context.putInContextData(SSContextDataKey.LIST_USERS_FORM, form);

        if (StringUtils.isNotEmpty(dateConnexion)) {
            context.putInContextData(
                SSContextDataKey.DATE_CONNEXION,
                SolonDateConverter.DATE_SLASH.parseToCalendar(dateConnexion)
            );
        }
    }
}
