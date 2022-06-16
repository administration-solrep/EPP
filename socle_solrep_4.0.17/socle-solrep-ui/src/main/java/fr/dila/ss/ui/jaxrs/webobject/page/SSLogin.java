package fr.dila.ss.ui.jaxrs.webobject.page;

import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.LOGIN_WAIT;

import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.jaxrs.webobject.LoginObject;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliLogin")
public class SSLogin extends LoginObject {

    @Override
    @GET
    public Object getLogin(
        @QueryParam("failed") Boolean failed,
        @QueryParam(LOGIN_WAIT) Boolean wait,
        @QueryParam("resetpwd") Boolean resetpwd
    ) {
        Object response = super.getLogin(failed, wait, resetpwd);
        if (response instanceof ThTemplate) {
            ThTemplate template = (ThTemplate) response;

            Map<String, Object> data = template.getData();

            String urlPageDila = STServiceLocator
                .getSTParametreService()
                .getParametreWithoutSession(STParametreConstant.PAGE_INTERNET_DILA_PARAMETER_NAME);

            String paramValue = STServiceLocator
                .getSTParametreService()
                .getParametreWithoutSession(STParametreConstant.MESSAGE_ACCESSIBILITE_LOGIN_PARAMETER_NAME);
            data.put(SSTemplateConstants.MESSAGE_ACCESSIBILITE_LOGIN, paramValue);

            data.put(SSTemplateConstants.URL_DILA, urlPageDila);
            data.put(SSTemplateConstants.HAS_INFO_ACCESSIBILITE, true);
        }

        return response;
    }
}
