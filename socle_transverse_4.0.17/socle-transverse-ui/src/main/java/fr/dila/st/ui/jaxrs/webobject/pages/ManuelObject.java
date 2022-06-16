package fr.dila.st.ui.jaxrs.webobject.pages;

import static fr.dila.st.api.constant.STConfigConstants.SOLON_MANUEL_FILE;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_MANUEL_FOLDER;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliManuel")
public class ManuelObject extends SolonWebObject {

    public ManuelObject() {
        super();
    }

    @GET
    public Response getManuel() {
        String folder = STServiceLocator.getConfigService().getValue(SOLON_MANUEL_FOLDER, EMPTY);
        String filename = STServiceLocator.getConfigService().getValue(SOLON_MANUEL_FILE, EMPTY);
        if (StringUtils.isBlank(folder) || StringUtils.isBlank(filename)) {
            throw new NuxeoException(
                "Configuration manquante pour le manuel utilisateur",
                Response.Status.NOT_FOUND.getStatusCode()
            );
        }
        return FileDownloadUtils.getResponse(new File(folder), FileUtils.sanitizePathTraversal(filename));
    }
}
