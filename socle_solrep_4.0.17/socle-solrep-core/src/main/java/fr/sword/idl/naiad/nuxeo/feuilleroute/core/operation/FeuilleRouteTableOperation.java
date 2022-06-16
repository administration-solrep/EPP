package fr.sword.idl.naiad.nuxeo.feuilleroute.core.operation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.FeuilleRouteMdl;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteDisplayService;
import java.io.IOException;
import java.io.StringWriter;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 *
 *
 *
 */
@Operation(
    id = FeuilleRouteTableOperation.ID,
    category = FeuilleRouteConstant.OPERATION_CATEGORY_ROUTING_NAME,
    label = "Resume Step",
    description = "Resume a step that were in running step."
)
public class FeuilleRouteTableOperation {
    public static final String ID = "Feuille.Route.Table.Get";

    @Context
    private FeuilleRouteDisplayService displayService;

    @Context
    private CoreSession session;

    @OperationMethod
    public Blob run(DocumentModel doc) {
        FeuilleRoute route = doc.getAdapter(FeuilleRoute.class);
        //    	List<RouteTableElement> table  = displayService.getRouteElements(route, session);
        FeuilleRouteMdl table = displayService.getModel(route, session);

        StringWriter writer = new StringWriter();
        JsonFactory factory = new JsonFactory();
        try (JsonGenerator jg = factory.createGenerator(writer)) {
            jg.setCodec(new ObjectMapper());
            jg.writeObject(table);
            return Blobs.createJSONBlob(writer.toString());
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }
}
