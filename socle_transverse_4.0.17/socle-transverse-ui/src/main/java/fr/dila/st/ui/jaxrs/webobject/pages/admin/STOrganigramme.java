package fr.dila.st.ui.jaxrs.webobject.pages.admin;

import fr.dila.st.core.util.FileUtils;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.STAdminTemplate;
import fr.dila.st.ui.th.model.STUtilisateurTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliOrganigramme")
public class STOrganigramme extends SolonWebObject {
    public static final int ORGANIGRAMME_ORDER = Breadcrumb.TITLE_ORDER;
    public static final String ORGANIGRAMME_URL = "/admin/organigramme/consult#main_content";

    public STOrganigramme() {
        super();
    }

    @Path("consult")
    public Object getUserOrganigramme() throws IllegalAccessException, InstantiationException {
        ThTemplate template = getMyTemplate(context);

        template.setName("pages/admin/user/organigramme");
        context.setNavigationContextTitle(
            new Breadcrumb("Gestion de l'organigramme", ORGANIGRAMME_URL, ORGANIGRAMME_ORDER)
        );

        template.setContext(context);

        return newObject("OrganigrammeAjax", context, template);
    }

    @Path("ministere")
    public Object getMinistere() {
        return newObject("OrganigrammeMinistere", context, template);
    }

    @Path("unitestructurelle")
    public Object getUniteStructurelle() {
        return newObject("OrganigrammeUniteStructurelle", context, template);
    }

    @Path("poste")
    public Object getPoste() {
        return newObject("OrganigrammePoste", context, template);
    }

    @Path("posteWs")
    public Object getPosteWs() {
        return newObject("OrganigrammePosteWs", context, template);
    }

    @Path("gouvernement")
    public Object getGouvernement() {
        return newObject("OrganigrammeGouvernement", context, template);
    }

    @Produces("application/vnd.ms-excel")
    @GET
    @Path("download")
    public Response download(@QueryParam("fileName") String fileName) {
        return FileDownloadUtils.getResponse(FileUtils.getAppTmpFolder(), FileUtils.sanitizePathTraversal(fileName));
    }

    protected ThTemplate getMyTemplate(SpecificContext context) throws IllegalAccessException, InstantiationException {
        @SuppressWarnings("unchecked")
        Class<ThTemplate> oldTemplate = (Class<ThTemplate>) context
            .getWebcontext()
            .getUserSession()
            .get(SpecificContext.LAST_TEMPLATE);
        if (oldTemplate != ThTemplate.class) {
            return oldTemplate.newInstance();
        } else if (context.getWebcontext().getPrincipal().isMemberOf("EspaceAdministrationReader")) {
            return new STAdminTemplate();
        } else {
            return new STUtilisateurTemplate();
        }
    }
}
