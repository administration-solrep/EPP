package fr.dila.ss.ui.jaxrs.webobject.page.admin.parametres;

import fr.dila.ss.ui.bean.parametres.ParametreDTO;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "Parametres")
public class SsParametres extends SolonWebObject {
    private static final String DATA_URL_LIST = "/admin/param/technique";
    private static final String DATA_URL_CONSULT = DATA_URL_LIST + "/%s";
    private static final String DATA_URL_EDIT = DATA_URL_LIST + "/%s/editer";
    protected static final String PARAMETRE_LIST = "pages/admin/param/parametres";
    protected static final String PARAMETRE_CONSULT = "pages/admin/param/parametre";
    protected static final String PARAMETRE_EDIT = "pages/admin/param/parametreForm";

    @Path("technique")
    public Object listerParametres() {
        if (context.getAction(SSActionEnum.ADMIN_PARAM_TECHNIQUE) == null) {
            throw new STAuthorizationException(DATA_URL_LIST);
        }

        Map<String, Object> mapData = new HashMap<>();

        template.setContext(context);
        template.setData(mapData);
        template.setName(PARAMETRE_LIST);

        return newObject("ParametresAjax", context, template);
    }

    @Path("technique/{name}")
    @GET
    public Object consulterParametre(@PathParam("name") String name) {
        verifyAction(SSActionEnum.ADMIN_PARAM_TECHNIQUE, String.format(DATA_URL_CONSULT, name));

        Map<String, Object> mapData = new HashMap<>();

        ParametreDTO param = MapDoc2Bean.docToBean(
            STServiceLocator.getSTParametreService().getParametre(context.getSession(), name).getDocument(),
            ParametreDTO.class
        );

        context.setNavigationContextTitle(
            new Breadcrumb(param.getTitre(), String.format(DATA_URL_CONSULT, name), Breadcrumb.TITLE_ORDER + 2)
        );

        mapData.put(SSTemplateConstants.PARAMETRE, param);
        mapData.put(SSTemplateConstants.PARAM_CONTEXT, "technique");
        mapData.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        mapData.put(STTemplateConstants.EDIT_ACTIONS, context.getActions(SSActionCategory.ADMIN_EDIT_PARAMETER));

        template.setContext(context);
        template.setData(mapData);
        template.setName(PARAMETRE_CONSULT);

        return template;
    }

    @Path("technique/{name}/editer")
    @GET
    public Object editerParametre(@PathParam("name") String name) {
        verifyAction(SSActionEnum.ADMIN_PARAM_TECHNIQUE, String.format(DATA_URL_EDIT, name));

        Map<String, Object> mapData = new HashMap<>();

        ParametreDTO param = MapDoc2Bean.docToBean(
            STServiceLocator.getSTParametreService().getParametre(context.getSession(), name).getDocument(),
            ParametreDTO.class
        );

        context.setNavigationContextTitle(
            new Breadcrumb("Modifier", String.format(DATA_URL_EDIT, name), Breadcrumb.TITLE_ORDER + 3)
        );

        mapData.put(SSTemplateConstants.PARAMETRE, param);
        mapData.put(SSTemplateConstants.EDIT_URL, "/ajax/admin/param/modifier");
        mapData.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        template.setName(PARAMETRE_EDIT);
        template.setContext(context);
        template.setData(mapData);

        return template;
    }
}
