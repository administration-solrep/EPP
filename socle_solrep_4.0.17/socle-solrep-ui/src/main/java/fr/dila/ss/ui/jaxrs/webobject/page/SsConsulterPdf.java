package fr.dila.ss.ui.jaxrs.webobject.page;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import java.io.File;
import java.nio.file.Paths;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliConsulterpdf")
public class SsConsulterPdf extends SolonWebObject {

    @GET
    @Path("{fileName}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Object consulterFichierPdf(@PathParam("fileName") String fileName) {
        ConfigService configService = STServiceLocator.getConfigService();
        fileName = fileName.replace("/", "").replace("..", "");
        java.nio.file.Path p = Paths.get(fileName);
        fileName = p.getFileName().toString();
        String tmpDirectory = configService.getValue(STConfigConstants.APP_FOLDER_TMP);
        java.nio.file.Path fullPath = Paths.get(String.format("%s/%s", tmpDirectory, fileName));
        File file = new File(fullPath.toString());
        return Response
            .ok(file, MediaType.APPLICATION_OCTET_STREAM)
            .header("Content-Disposition", "inline; filename=\"" + file.getName() + "\"")
            .build();
    }
}
