package fr.dila.st.ui.jaxrs.webobject;

import fr.dila.st.ui.utils.URLUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.core.io.download.DownloadService;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.runtime.api.Framework;

@WebObject(type = "AppliNxfile")
public class DownloadWebObject extends SolonWebObject {

    public DownloadWebObject() {
        super();
    }

    // Copié depuis org.nuxeo.ecm.platform.ui.web.download.DownloadServlet
    @GET
    @Path("/{s:.*}")
    public Response handleDownload(@Context HttpServletRequest req, @Context HttpServletResponse resp)
        throws IOException {
        String requestURI;
        try {
            requestURI = new URI(req.getRequestURI()).getPath();
        } catch (URISyntaxException e) {
            requestURI = req.getRequestURI();
        }

        // remove context
        String context = String.join("/", VirtualHostHelper.getContextPath(req), URLUtils.APP_CONTEXT_PATH);
        if (!requestURI.startsWith(context)) {
            return Response.status(HttpServletResponse.SC_NOT_FOUND).entity("Invalid URL syntax").build();
        }

        String path = requestURI.substring(context.length());
        DownloadService downloadService = Framework.getService(DownloadService.class);
        String baseUrl = VirtualHostHelper.getBaseURL(req);
        downloadService.handleDownload(req, resp, baseUrl, path);
        return null;
    }
}
