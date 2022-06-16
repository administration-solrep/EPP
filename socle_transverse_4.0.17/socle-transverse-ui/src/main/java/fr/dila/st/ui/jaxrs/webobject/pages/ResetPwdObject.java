package fr.dila.st.ui.jaxrs.webobject.pages;

import static fr.dila.st.ui.th.constants.STTemplateConstants.CONDITIONS_ACCESS_URL;

import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ResetPwdObject")
public class ResetPwdObject extends SolonWebObject {

    @GET
    public ThTemplate getResetPwd(@QueryParam("username") String username) {
        Map<String, Object> mapData = new HashMap<>();

        context.setContextData(mapData);
        context.setCopyDataToResponse(true);

        mapData.put("config", STUIServiceLocator.getConfigUIService().getConfig());

        String conditionAccesUrl = STServiceLocator
            .getSTParametreService()
            .getParametreWithoutSession(STParametreConstant.PAGE_RENSEIGNEMENTS_ID);
        mapData.put(CONDITIONS_ACCESS_URL, conditionAccesUrl);
        mapData.put("username", username);

        ThTemplate template = new ThTemplate("askResetPassword", context);
        template.setData(mapData);
        return template;
    }
}
