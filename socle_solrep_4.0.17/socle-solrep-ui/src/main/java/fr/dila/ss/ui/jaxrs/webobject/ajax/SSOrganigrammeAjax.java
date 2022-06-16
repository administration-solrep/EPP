package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.jaxrs.webobject.ajax.STOrganigrammeAjax;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammeAjax")
public class SSOrganigrammeAjax extends STOrganigrammeAjax {

    @Override
    protected List<OrganigrammeElementDTO> loadOrganigrammWithAction(SpecificContext context) {
        return SSUIServiceLocator.getSSOrganigrammeManagerService().getOrganigramme(true, context);
    }
}
