package fr.dila.ss.ui.jaxrs.webobject.page.admin.actualite;

import fr.dila.ss.ui.bean.actualites.ActualiteCreationDTO;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ActualitesCreation")
public class SSActualitesCreation extends SolonWebObject {

    @GET
    public ThTemplate getCreationActualite() {
        template.setName("pages/admin/actualites/creationActualites");

        Map<String, Object> mapContext = new HashMap<>();
        context.setContextData(mapContext);
        context.setNavigationContextTitle(
            new Breadcrumb("Créer une actualité", "/admin/actualites", Breadcrumb.TITLE_ORDER + 10)
        );
        template.setContext(context);

        if (context.getAction(SSActionEnum.CREATE_NEWS) == null) {
            throw new STAuthorizationException("/admin/actualites/creation");
        }

        // Cas du réaffichage après erreur
        ActualiteCreationDTO dto = new ActualiteCreationDTO();

        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        map.put("actualiteCreationDTO", dto);
        template.setData(map);

        return template;
    }
}
