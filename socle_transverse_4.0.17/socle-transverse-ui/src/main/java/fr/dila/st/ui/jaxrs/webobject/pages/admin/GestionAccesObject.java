package fr.dila.st.ui.jaxrs.webobject.pages.admin;

import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.GestionAccesDTO;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "Acces")
public class GestionAccesObject extends SolonWebObject {

    public GestionAccesObject() {
        super();
    }

    @GET
    public ThTemplate getGestionAcces() {
        template.setName("pages/admin/gestionAcces");

        Map<String, Object> mapContext = new HashMap<>();
        context.setContextData(mapContext);
        context.setNavigationContextTitle(
            new Breadcrumb("Gestion de l'accès", "/admin/user/acces", Breadcrumb.TITLE_ORDER)
        );
        template.setContext(context);

        if (context.getAction(STActionEnum.ADMIN_USER_ACCES) == null) {
            throw new STAuthorizationException("/admin/user/acces");
        }

        Map<String, Object> map = new HashMap<>();
        GestionAccesDTO dto = STUIServiceLocator.getEtatApplicationUIService().getEtatApplicationDocument(context);
        map.put("gestionAccesDto", dto);

        // Valeurs possibles pour le champ format
        List<SelectValueDTO> formats = new ArrayList<>();
        formats.add(new SelectValueDTO("html", "Html"));
        formats.add(new SelectValueDTO("text", "Text"));
        formats.add(new SelectValueDTO("xml", "Xml"));
        map.put("formatListValues", formats);
        map.put("format", "html"); // valeur par défaut

        template.setData(map);
        return template;
    }
}
