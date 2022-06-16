package fr.sword.idl.naiad.nuxeo.addons.status.ws.management;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.map.ObjectMapper;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusService;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

@Path("/status")
@WebObject(type = "NaiadStatusRoot")
@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
public class NaiadStatusRoot extends ModuleRoot {

    @GET
    public Object getStatus(@DefaultValue("true") @QueryParam("full") final boolean full,
            @DefaultValue("true") @QueryParam("pretty") final boolean pretty) throws NuxeoException {

        final StatusService statusService = ServiceUtil.getRequiredService(StatusService.class);
        final Map<String, Object> status = statusService.getStatus();
        if (full) {
            return toJSON(status, pretty);
        } else {
            return getSimpleStatus(status);
        }

    }

    public Object getSimpleStatus(final Map<String, Object> status) {

        for (final Entry<String, Object> entry : status.entrySet()) {
            final Object value = entry.getValue();
            if (value instanceof ResultInfo) {
                final ResultInfo resultInfo = (ResultInfo) value;
                if (ResultEnum.KO.equals(resultInfo.getStatut())) {
                    throw new NuxeoException(resultInfo.getStatut().toString());
                }
            }
        }
        return "Ok";
    }

    public Object toJSON(final Object object, final boolean prettyPrint) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();

            if (prettyPrint) {
                return objectMapper.defaultPrettyPrintingWriter().writeValueAsString(object);
            } else {
                return objectMapper.writeValueAsString(object);
            }
        } catch (final IOException e) {
            throw new NuxeoException(e);
        }

    }
}
