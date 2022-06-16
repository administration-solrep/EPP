package fr.dila.epp.ui.jaxrs.webobject.page;

import static fr.dila.solonepp.core.service.SolonEppServiceLocator.getMessageService;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.validators.annot.SwNotEmpty;
import fr.dila.st.ui.validators.annot.SwRequired;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "Evenement")
public class EppEvenementObject extends SolonWebObject {

    @GET
    public Object getEvenement(@SwRequired @SwNotEmpty @QueryParam("evenementId") String id) {
        CoreSession session = context.getSession();
        DocumentModel message = getMessageService().getMessageByEvenementId(session, id);
        if (message == null) {
            throw new NuxeoException("Pas de message pour l'évenement " + id, SC_NOT_FOUND);
        }
        return redirect(String.format("dossier/%s/detailCommunication", message.getId()));
    }
}
