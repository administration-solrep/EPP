package fr.dila.ss.ui.jaxrs.webobject.ajax;

import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.ACTIVE_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.MASQUER_CORBEILLES_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.MODE_TRI_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.POSTE_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.SELECTED_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.SELECTION_VALIDEE_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.USER_KEY;

import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.ss.ui.bean.MailboxListDTO;
import fr.dila.ss.ui.services.SSMailboxListComponentService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "CorbeilleAjax")
public class SSCorbeilleAjax extends SolonWebObject {

    public SSCorbeilleAjax() {
        super();
    }

    @GET
    @Path("mailbox")
    public ThTemplate getMailbox(
        @QueryParam("tri") TypeRegroupement tri,
        @QueryParam("key") String key,
        @QueryParam("selectionPoste") String selectionPoste,
        @QueryParam("selectionUser") String selectionUser,
        @QueryParam("isSelectionValidee") boolean isSelectionValidee,
        @QueryParam("masquerCorbeilles") boolean masquerCorbeilles
    ) {
        // Je déclare mon template et j'instancie mon context
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/components/arbre");
        if (tri != null) {
            context.putInContextData(MODE_TRI_KEY, tri);
        }
        if (StringUtils.isNotBlank(key)) {
            context.putInContextData(SELECTED_KEY, key);
        }
        if (StringUtils.isNotBlank(selectionPoste)) {
            context.putInContextData(POSTE_KEY, selectionPoste);
        }
        if (StringUtils.isNotBlank(selectionUser)) {
            context.putInContextData(USER_KEY, selectionUser);
        }
        context.putInContextData(SELECTION_VALIDEE_KEY, isSelectionValidee);
        context.putInContextData(MASQUER_CORBEILLES_KEY, masquerCorbeilles);
        template.setContext(context);

        Map<String, Object> serviceMap = getMyService().getData(context);
        MailboxListDTO dto = (MailboxListDTO) serviceMap.get("mailboxListMap");

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.TREE_LIST, dto.getChilds());
        map.put(STTemplateConstants.LEVEL, 1);
        map.put(SSTemplateConstants.MY_ID, "");
        map.put(SSTemplateConstants.TOGGLER_ID, "");
        map.put(STTemplateConstants.IS_OPEN, true);
        map.put(STTemplateConstants.TITLE, "corbeilles");
        map.put(ACTIVE_KEY, serviceMap.get(ACTIVE_KEY) != null ? serviceMap.get(ACTIVE_KEY) : key);
        template.setData(map);
        return template;
    }

    @GET
    @Path("actualiser")
    public ThTemplate actualiserCorbeilles(@QueryParam("masquerCorbeilles") boolean masquerCorbeilles) {
        UserSessionHelper.putUserSessionParameter(
            context,
            SSMailboxListComponentServiceImpl.REFRESH_CORBEILLE_KEY,
            true
        );
        return getMailbox(null, null, null, null, false, masquerCorbeilles);
    }

    // must be overriden
    protected SSMailboxListComponentService getMyService() {
        return SSUIServiceLocator.getSSMailboxListComponentService();
    }
}
