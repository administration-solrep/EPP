package fr.dila.epp.ui.jaxrs.webobject.ajax;

import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.jaxrs.webobject.ajax.STOrganigrammeAjax;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammeAjax")
public class EppOrganigrammeAjax extends STOrganigrammeAjax {

    @Override
    protected List<OrganigrammeElementDTO> loadOrganigrammWithAction(SpecificContext context) {
        return SolonEppUIServiceLocator.getOrganigrammeService().getOrganigramme(true, context);
    }
}
