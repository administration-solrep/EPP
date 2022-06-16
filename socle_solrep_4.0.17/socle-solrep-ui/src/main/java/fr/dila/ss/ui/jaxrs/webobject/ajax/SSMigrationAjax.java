package fr.dila.ss.ui.jaxrs.webobject.ajax;

import static fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIServiceImpl.MIGRATIONS_LIST;

import fr.dila.ss.ui.bean.MigrationDTO;
import fr.dila.ss.ui.bean.MigrationDetailDTO;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIService;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "MigrationAjax")
public class SSMigrationAjax extends SolonWebObject {
    private static final String INDEX = "index";

    @GET
    @Path("switchType")
    public ThTemplate switchType(@QueryParam("migrationType") String migrationType, @QueryParam(INDEX) Integer index) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/components/migration/migration-structure");
        template.setContext(context);

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();

        MigrationDetailDTO migration = new MigrationDetailDTO(migrationType);

        map.put(SSTemplateConstants.MIGRATION, migration);
        map.put(STTemplateConstants.INDEX, index);
        map.put(SSTemplateConstants.IS_RUNNING, false);
        map.put(SSTemplateConstants.ACTIONS, getActions());
        map.put(SSTemplateConstants.MIGRATION_TYPES, getMigrationTypes());
        map.put(STTemplateConstants.DATA_URL, "/admin/migrations");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/migrations/switchType");

        template.setData(map);

        return template;
    }

    @GET
    @Path("add")
    public ThTemplate addMigration(@QueryParam(INDEX) Integer index) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/components/migration/migration-detail");
        template.setContext(context);

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();

        MigrationDetailDTO migration = new MigrationDetailDTO();

        map.put(SSTemplateConstants.MIGRATION, migration);
        map.put(STTemplateConstants.INDEX, index);
        map.put(SSTemplateConstants.IS_RUNNING, false);
        map.put(SSTemplateConstants.ACTIONS, getActions());
        map.put(SSTemplateConstants.MIGRATION_TYPES, getMigrationTypes());
        map.put(STTemplateConstants.DATA_URL, "/admin/migrations");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/migrations/add");

        template.setData(map);

        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("launch")
    public Response launchMigration(@SwBeanParam MigrationDTO dto) {
        context.putInContextData("migrationDTO", dto);

        SSMigrationGouvernementUIService migrationGouvernementUIService = getMigrationGouvernementUIService();

        migrationGouvernementUIService.ajouterMigrations(dto.getDetails(), context);
        migrationGouvernementUIService.lancerMigration(context);

        // Comme je recharge la page si pas d'erreur je mets en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @GET
    @Path("refresh")
    public ThTemplate refreshMigrations() {
        ThTemplate template = new AjaxLayoutThTemplate();

        template.setName("fragments/components/migration/migration-list");
        template.setContext(context);

        Map<String, Object> map = new HashMap<>();

        SSMigrationGouvernementUIService migrationGouvernementUIService = getMigrationGouvernementUIService();
        MigrationDTO migrationDto = migrationGouvernementUIService.getMigrationDTO(context);

        map.put(SSTemplateConstants.MIGRATION_DTO, migrationDto);
        map.put(SSTemplateConstants.IS_RUNNING, migrationDto.isRunning());
        map.put(SSTemplateConstants.MIGRATION_TYPES, getMigrationTypes());
        map.put(SSTemplateConstants.ACTIONS, getActions());

        template.setData(map);

        return template;
    }

    @POST
    @Path("reinit")
    public void reinitMigrations() {
        UserSessionHelper.putUserSessionParameter(context, MIGRATIONS_LIST, null);
    }

    protected SSMigrationGouvernementUIService getMigrationGouvernementUIService() {
        throw new UnsupportedOperationException("Implemented in app-specific classes");
    }

    public Map<String, String> getMigrationTypes() {
        return new HashMap<>();
    }

    public Map<String, List<String>> getActions() {
        return new HashMap<>();
    }

    protected ThTemplate getMyTemplate(SpecificContext context) {
        return new ThTemplate();
    }
}
