package fr.dila.st.ui.jaxrs.webobject;

import fr.dila.st.ui.utils.URLUtils;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

public class SolonModuleRoot extends ModuleRoot {
    private static final String SLASH = "/";

    @Context
    private HttpServletRequest request;

    @Override
    public Response redirect(String uri) {
        String completeUri = URLUtils.generateRedirectPath(uri, request);
        return super.redirect(completeUri);
    }

    public Response redirectWithoutContext(String uri) {
        String baseUrl = VirtualHostHelper.getBaseURL(request);
        if (baseUrl.endsWith(SLASH) && uri.startsWith(SLASH)) {
            uri = uri.substring(1);
        }
        return super.redirect(baseUrl + uri);
    }
}
