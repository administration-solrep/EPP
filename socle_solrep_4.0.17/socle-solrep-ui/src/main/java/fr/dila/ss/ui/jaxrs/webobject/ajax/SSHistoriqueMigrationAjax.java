package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.enums.AlertType;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "HistoriqueMigrationAjax")
public class SSHistoriqueMigrationAjax extends SolonWebObject implements IHistoriqueMigrationAjax {
    private static final STLogger LOGGER = STLogFactory.getLog(SSHistoriqueMigrationAjax.class);

    /**
     * Méthode surchargée dans l'appli pour récuperer les bonnes infos
     *
     * @return template
     */
    @GET
    public ThTemplate getHistorique() {
        return template;
    }

    @GET
    @Path("rafraichir")
    public Object refreshHistorique() {
        template = new AjaxLayoutThTemplate();
        template.setName("fragments/table/tableHistoriqueMigration");
        template.setContext(context);
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.TABLE_CAPTION, getTableCaption());
        template.setData(map);
        return getHistorique();
    }

    @GET
    @Path("exporter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response export(@QueryParam("id") String id) {
        try {
            context.putInContextData(SSContextDataKey.ID_MIGRATION_LOGGER, id);
            getMigrationGouvernementUIService().sendMailMigrationDetails(context);
            context.getMessageQueue().addToastSuccess(ResourceHelper.getString("export.message.succes"));
        } catch (SSException e) {
            LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_SEND_EVENT_TEC, e);
            context.getMessageQueue().addMessageToQueue(e.getMessage(), AlertType.TOAST_WARNING);
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    protected SSMigrationGouvernementUIService getMigrationGouvernementUIService() {
        throw new UnsupportedOperationException("Implemented in app-specific classes");
    }

    @Override
    public String getTableCaption() {
        // Méthode surchargée dans l'appli pour récuperer les bonnes infos
        return null;
    }
}
