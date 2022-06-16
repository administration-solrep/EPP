package fr.dila.st.ui.jaxrs.webobject.ajax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.jaxrs.webobject.pages.users.TransverseUsers;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "SocleUsersAjax")
public class SocleUsersAjax extends SolonWebObject {

    public SocleUsersAjax() {
        super();
    }

    @GET
    @Path("/liste")
    public ThTemplate getUsers(@SwBeanParam UsersListForm usersform) {
        return TransverseUsers.generateListUsersTemplate(
            context,
            template,
            "/admin/users/liste",
            ResourceHelper.getString("menu.admin.user.users.title"),
            usersform
        );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/suggestions")
    public String getSuggestions(@QueryParam("input") String filtrer, @QueryParam("profil") String profil)
        throws JsonProcessingException {
        List<SuggestionDTO> list = STUIServiceLocator
            .getSTUtilisateursUIService()
            .getNotificationUserSuggestions(filtrer);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(list);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        ThTemplate myTemplate = new AjaxLayoutThTemplate("fragments/table/tableUsers", getMyContext());
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.IS_CHECKBOX, false);
        map.put(STTemplateConstants.IS_PAGINATION_VISIBLE, true);
        myTemplate.setData(map);
        return myTemplate;
    }
}
