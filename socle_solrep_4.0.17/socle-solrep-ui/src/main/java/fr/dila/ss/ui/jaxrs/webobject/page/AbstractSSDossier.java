package fr.dila.ss.ui.jaxrs.webobject.page;

import fr.dila.ss.api.tree.SSTreeFile;
import fr.dila.st.ui.bean.OngletConteneur;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public abstract class AbstractSSDossier extends AbstractCommonSSDossier {
    public static final String DOSSIER_WEBOBJECT = "AppliDossier";

    private final String defaultTab;

    protected AbstractSSDossier(String tab) {
        this.defaultTab = tab;
    }

    @Path("{id}/{tab}")
    public Object getDossier(
        @PathParam("id") String id,
        @PathParam("tab") String tab,
        @QueryParam("dossierLinkId") String dossierLinkId,
        @QueryParam("idMessage") String idMessage
    )
        throws IllegalAccessException, InstantiationException {
        ThTemplate template = getMyTemplate(context);
        template.setName("pages/dossier/consult");
        template.setContext(context);

        buildContextData(context, id, tab, dossierLinkId);
        buildTemplateData(template, tab, idMessage);

        OngletConteneur content = (OngletConteneur) template.getData().get(STTemplateConstants.MY_TABS);
        if (!content.isCurrentTabAllowed()) {
            return getDossier(id, this.defaultTab, dossierLinkId, idMessage);
        }

        setSpecificDataForGetDossier(template, id, tab);

        return newObject(DOSSIER_WEBOBJECT + StringUtils.capitalize(tab), context, template);
    }

    protected abstract void setSpecificDataForGetDossier(ThTemplate template, String id, String tab);

    @GET
    @Path("{id}/telecharger")
    public Response downloadFile(@QueryParam("fileId") String fileId) {
        DocumentModel docModel = context.getSession().getDocument(new IdRef(fileId));
        SSTreeFile ssTreeFile = getFondDeDossierFileAdapter(docModel);
        File file = ssTreeFile.getContent().getFile();
        return FileDownloadUtils.getResponse(file, ssTreeFile.getFilename(), ssTreeFile.getFileMimeType());
    }

    protected final SSTreeFile getFondDeDossierFileAdapter(DocumentModel docModel) {
        return docModel.getAdapter(SSTreeFile.class);
    }

    protected ThTemplate getMyTemplate(SpecificContext context) throws IllegalAccessException, InstantiationException {
        @SuppressWarnings("unchecked")
        Class<ThTemplate> oldTemplate = (Class<ThTemplate>) context
            .getWebcontext()
            .getUserSession()
            .get(SpecificContext.LAST_TEMPLATE);
        if (oldTemplate == ThTemplate.class) {
            return getMyTemplate();
        } else {
            return oldTemplate.newInstance();
        }
    }

    @Path("{id}/substitution")
    public Object getSubstitution(@PathParam("id") String id) {
        context.setCurrentDocument(id);
        return newObject("SubstitutionFDR", context);
    }
}
