package fr.dila.epp.ui.jaxrs.webobject.ajax;

import static fr.dila.st.ui.enums.STContextDataKey.ID;

import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "EppDossierAjax")
public class EppDossierAjax extends SolonWebObject {

    @Path("{id}/{tab}")
    public Object doTab(
        @PathParam("id") String id,
        @PathParam("tab") String tab,
        @QueryParam("version") String versionNum
    ) {
        // On indique en context l'onglet courant
        context.getContextData().put("currentTab", tab);

        context.putInContextData(ID, id);
        context.putInContextData(EppContextDataKey.VERSION_ID, versionNum);
        SolonEppUIServiceLocator.getEvenementUIService().consulterEvenement(context);

        return newObject("EppDossier" + StringUtils.capitalize(tab), context);
    }
}
