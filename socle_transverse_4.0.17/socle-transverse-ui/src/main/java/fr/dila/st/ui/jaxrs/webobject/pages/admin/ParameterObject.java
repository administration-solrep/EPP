package fr.dila.st.ui.jaxrs.webobject.pages.admin;

import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.model.param.ParameterValue;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "ParameterObject")
public class ParameterObject extends SolonWebObject {

    @GET
    public ThTemplate doListParams() {
        SpecificContext myContext = new SpecificContext();
        Map<String, Object> data = new HashMap<>();
        data.put("mytext", "list parameters");
        myContext.setContextData(data);
        myContext.setCopyDataToResponse(true);
        return new ThTemplate("pages/home", myContext);
    }

    @GET
    @Path("/{paramkey}")
    public ThTemplate doGetParam(@PathParam("paramkey") String paramkey) {
        SpecificContext myContext = new SpecificContext();
        Map<String, Object> data = new HashMap<>();
        data.put("mytext", "param : " + paramkey);
        myContext.setContextData(data);
        myContext.setCopyDataToResponse(true);
        return new ThTemplate("pages/home", myContext);
    }

    @POST
    @Path("/{paramkey}")
    public ThTemplate doUpdate(@PathParam("paramkey") String paramkey, @FormParam("value") ParameterValue value) {
        SpecificContext myContext = new SpecificContext();
        Map<String, Object> data = new HashMap<>();
        data.put("mytext", "param : " + paramkey + ", update value = " + value.getValue());
        myContext.setContextData(data);
        myContext.setCopyDataToResponse(true);
        return new ThTemplate("pages/home", myContext);
    }
}
