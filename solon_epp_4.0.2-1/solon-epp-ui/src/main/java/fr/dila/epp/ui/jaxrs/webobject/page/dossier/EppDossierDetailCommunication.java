package fr.dila.epp.ui.jaxrs.webobject.page.dossier;

import static fr.dila.st.ui.enums.STContextDataKey.ID;

import fr.dila.epp.ui.enumeration.EppActionCategory;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.services.VersionUIService;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.st.ui.bean.DetailCommunication;
import fr.dila.st.ui.bean.VersionSelectDTO;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "EppDossierDetailCommunication")
public class EppDossierDetailCommunication extends SolonWebObject {
    private static final String MESSAGE = "message";
    private static final String ACCEPTER_ANNULATION_MESSAGE =
        "communication.action.accepter.annulation.confirm.message";
    private static final String REJETER_ANNULATION_MESSAGE = "communication.action.rejeter.annulation.confirm.message";
    private static final String ABANDONNER_ANNULATION_MESSAGE =
        "communication.action.abandonner.annulation.confirm.message";

    public EppDossierDetailCommunication() {
        super();
    }

    @GET
    public ThTemplate getDetail(
        @PathParam("id") String id,
        @PathParam("tab") String tab,
        @QueryParam("version") String version
    ) {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        DetailCommunication detail = new DetailCommunication();
        detail.setLstWidgets(SolonEppUIServiceLocator.getMetadonneesUIService().getWidgetListForCommunication(context));

        final VersionUIService versionUIService = SolonEppUIServiceLocator.getVersionUIService();
        context.putInContextData(ID, SolonEppConstant.VERSION_ACTION_CREER_EVENEMENT);
        boolean displayComSuccessive = versionUIService.isActionPossible(context);

        if (displayComSuccessive) {
            detail.setLstComSuccessives(
                SolonEppActionsServiceLocator.getEvenementTypeActionService().getEvenementSuccessifList(context)
            );
        }

        List<String> actionList = versionUIService.getActionList(context);
        List<Action> baseActions = context
            .getActions(EppActionCategory.BASE_COMMUNICATION_DISPLAY)
            .stream()
            .filter(a -> actionList.contains(a.getId()))
            .collect(Collectors.toList());
        List<Action> mainActions = context
            .getActions(EppActionCategory.MAIN_COMMUNICATION_DISPLAY)
            .stream()
            .filter(a -> actionList.contains(a.getId()))
            .collect(Collectors.toList());

        String natureVersion = SolonEppActionsServiceLocator
            .getMetadonneesActionService()
            .getLabelNatureVersion(context);

        if (natureVersion.contains("annulation")) {
            for (Action action : mainActions) {
                if (action.getId().equals("ACCEPTER")) {
                    action.getProperties().put(MESSAGE, ACCEPTER_ANNULATION_MESSAGE);
                }
                if (action.getId().equals("REJETER")) {
                    action.getProperties().put(MESSAGE, REJETER_ANNULATION_MESSAGE);
                }
                if (action.getId().equals("ABANDONNER")) {
                    action.getProperties().put(MESSAGE, ABANDONNER_ANNULATION_MESSAGE);
                }
            }
        }

        template.getData().put(STTemplateConstants.ID, id);
        template.getData().put("natureVersion", natureVersion);
        template.getData().put(STTemplateConstants.LST_WIDGETS, detail.getLstWidgets());
        template.getData().put("displayComSuccessive", displayComSuccessive);
        template.getData().put("comSuccessiveSelect", detail.getLstComSuccessives());
        template.getData().put(STTemplateConstants.BASE_ACTIONS, baseActions);
        template.getData().put(STTemplateConstants.MAIN_ACTIONS, mainActions);
        List<VersionSelectDTO> lstVersions = SolonEppActionsServiceLocator
            .getCorbeilleActionService()
            .getallVersions(context);
        template.getData().put("lstVersions", lstVersions);
        String curVersion = SolonEppActionsServiceLocator.getMetadonneesActionService().getSelectedVersion(context);
        template.getData().put("curVersion", curVersion);
        template
            .getData()
            .put(
                "curDescription",
                lstVersions
                    .stream()
                    .filter(versionSelector -> versionSelector.getText().equals(curVersion))
                    .map(VersionSelectDTO::getDescription)
                    .findFirst()
                    .orElse("")
            );

        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/dossier/onglets/detailCommunication", getMyContext());
    }
}
