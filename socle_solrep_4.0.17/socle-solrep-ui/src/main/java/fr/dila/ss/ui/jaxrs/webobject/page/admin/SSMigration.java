package fr.dila.ss.ui.jaxrs.webobject.page.admin;

import fr.dila.ss.ui.bean.MigrationDTO;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIService;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "Migration")
public class SSMigration extends SolonWebObject {
    public static final String BREADCRUMB_TITLE = "menu.admin.migration.migrations.title";

    public static final String BREADCRUMB_PATH = "/admin/migration/migrations#main_content";

    public static final String MIGRATION_DEPLACER_DIRECTION_PILOTES_MODELES =
        "migration.deplacer.direction.pilotes.modeles";

    public static final String MIGRATION_DEPLACER_ELEMENT_FILS = "migration.deplacer.element.fils";

    public static final String MIGRATION_MISE_A_JOUR_CORBEILLE_POSTE = "migration.mise.a.jour.corbeille.poste";

    public static final String MIGRATION_MISE_A_JOUR_DROITS_QE = "migration.mise.a.jour.droits.qe";

    public static final String MIGRATION_MIGRER_ETAPES_FDR_MODELES = "migration.migrer.etapes.fdr.modeles";

    public static final String MIGRATION_MODIFIER_MINISTERE_DIRECTION_RATTACHEMENT =
        "migration.modifier.ministere.direction.rattachement";

    public static final String MIGRATION_REATTRIBUER_NOR_INITIES_LANCES = "migration.reattribuer.nor.inities.lances";

    public static final String MIGRATION_BULLETINS_OFFICIELS = "migration.bulletins.officiels";

    public static final String MIGRATION_LISTE_MOTS_CLES_GESTION_INDEXATION =
        "migration.liste.mots.cles.gestion.indexation";

    public static final String MIGRATION_MISE_A_JOUR_DROITS_DOSSIERS = "migration.mise.a.jour.droits.dossiers";

    @GET
    @Path("migrations")
    public ThTemplate getMigrations() {
        if (context.getAction(SSActionEnum.ADMIN_MIGRATION_MIGRATIONS) == null) {
            throw new STAuthorizationException("/admin/migration/migrations");
        }
        ThTemplate template = getMyTemplate(context);

        template.setName("pages/admin/migration/migrations");
        template.setContext(context);

        Map<String, Object> map = new HashMap<>();

        context.setNavigationContextTitle(
            new Breadcrumb(ResourceHelper.getString(BREADCRUMB_TITLE), BREADCRUMB_PATH, Breadcrumb.TITLE_ORDER)
        );

        SSMigrationGouvernementUIService migrationGouvernementUIService = getMigrationGouvernementUIService();
        MigrationDTO migrationDto = migrationGouvernementUIService.getMigrationDTO(context);

        map.put(SSTemplateConstants.MIGRATION_DTO, migrationDto);
        map.put(SSTemplateConstants.MIGRATION_TYPES, getMigrationTypes());
        map.put(SSTemplateConstants.ACTIONS, getActions());
        map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        template.setData(map);

        return template;
    }

    public Map<String, String> getMigrationTypes() {
        return new HashMap<>();
    }

    public Map<String, List<String>> getActions() {
        return new HashMap<>();
    }

    protected SSMigrationGouvernementUIService getMigrationGouvernementUIService() {
        throw new UnsupportedOperationException("Implemented in app-specific classes");
    }

    protected ThTemplate getMyTemplate(SpecificContext context) {
        return new ThTemplate();
    }
}
