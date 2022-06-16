package fr.dila.ss.ui.jaxrs.webobject.page.admin.actualite;

import fr.dila.ss.ui.bean.actualites.ActualiteConsultationDTO;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ActualitesConsultation")
public class SSActualitesConsultation extends SolonWebObject {
    private static final String ACTUALITE_VIEW = "pages/admin/actualites/actualiteView";

    private static final String ACTUALITE_PARAM = "id";

    protected ActualiteUIService getActualiteUIService() {
        return SSUIServiceLocator.getActualiteUIService();
    }

    @GET
    @Path("/{id}")
    public Object viewActualite(@PathParam(ACTUALITE_PARAM) String actualiteId) {
        template.setContext(context);
        template.setName(ACTUALITE_VIEW);
        DocumentModel actualiteDoc;
        try {
            actualiteDoc = context.getSession().getDocument(new IdRef(actualiteId));
        } catch (DocumentNotFoundException e) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("actualite.not.existe"));
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
            return redirect("/admin/actualites");
        }

        ActualiteConsultationDTO dto = getActualiteUIService().toActualiteForm(actualiteDoc);
        context.setNavigationContextTitle(
            new Breadcrumb(dto.getObjet(), "/admin/actualites/consultation/" + dto.getId(), Breadcrumb.TITLE_ORDER + 10)
        );
        template.getData().put(SSTemplateConstants.ACTUALITE_DTO, dto);
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        return template;
    }
}
