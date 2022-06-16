package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.GestionAccesDTO;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.IOException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AccesAjax")
public class GestionAccesAjax extends SolonWebObject {

    public GestionAccesAjax() {
        super();
    }

    @Path("sauvegarde")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveGestionAcces(@SwBeanParam GestionAccesDTO gestionAccesDto) throws IOException {
        context.putInContextData(STContextDataKey.GESTION_ACCES, gestionAccesDto);
        STUIServiceLocator.getEtatApplicationUIService().updateDocument(context);
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
