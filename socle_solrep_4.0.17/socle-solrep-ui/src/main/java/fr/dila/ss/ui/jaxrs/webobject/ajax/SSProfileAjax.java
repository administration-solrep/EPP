package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.PageProfilDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ProfileAjax")
public class SSProfileAjax extends SolonWebObject {

    public SSProfileAjax() {
        super();
    }

    @GET
    @Path("listeProfile")
    public ThTemplate getListeProfile(@QueryParam("ordreProfil") String ordreProfil) {
        ThTemplate template = new AjaxLayoutThTemplate("fragments/components/profileListe", context);

        context.putInContextData(STContextDataKey.SORT_ORDER, SortOrder.fromValue(ordreProfil));

        Map<String, Object> map = new HashMap<>();

        // récupérer la liste des profils par le service (triée en ASC par défaut)
        PageProfilDTO ppd = SSUIServiceLocator.getProfilUIService().getPageProfilDTO(context);

        map.put(SSTemplateConstants.PROFILS, ppd.getProfils());
        map.put(STTemplateConstants.LST_COLONNES, ppd.getLstColonnes());
        map.put(STTemplateConstants.DATA_URL, "/admin/profile/liste");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/profile/listeProfile");
        template.setData(map);
        return template;
    }

    @Path("/ficheProfil")
    public Object getFicheProfile() {
        return newObject("ProfilFicheAjax");
    }
}
