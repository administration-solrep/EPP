package fr.dila.ss.ui.jaxrs.webobject.ajax.dossier;

import fr.dila.ss.api.service.SSExcelService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.bean.DossierMailForm;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ActionDossierAjax")
public class SSDossierAjax extends SolonWebObject {

    public SSDossierAjax() {
        super();
    }

    @GET
    @Path("mail/contenu")
    public ThTemplate loadDossierContent() {
        ThTemplate template = new AjaxLayoutThTemplate("fragments/dossier/dossierMailModalContent", context);

        DossierMailForm dossierMailForm = new DossierMailForm();
        dossierMailForm.setObjet("Envoi dossier(s)");
        dossierMailForm.setMessage("Un ou plusieurs dossier(s) vous ont été(s) envoyé(s).");

        Map<String, Object> map = new HashMap<>();
        map.put("dossierMailForm", dossierMailForm);
        template.setData(map);

        return template;
    }

    @POST
    @Path("mail/sendDossier")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendDossierMail(
        @SwBeanParam DossierMailForm dossierMailForm,
        @FormParam("dossierId") String dossierId
    ) {
        context.putInContextData(STContextDataKey.DOSSIER_ID, dossierId);
        context.putInContextData(SSContextDataKey.DOSSIER_MAIL_FORM, dossierMailForm);

        SSUIServiceLocator.getSSArchiveUIService().envoyerMailDossier(context);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("export/excel")
    @Produces("application/vnd.ms-excel")
    public Response exportExcel(@FormParam("idDossiers[]") List<String> idDossiers) {
        SSExcelService excelService = SSServiceLocator.getSSExcelService();

        List<DocumentRef> dossiersRefs = idDossiers.stream().map(IdRef::new).collect(Collectors.toList());

        Consumer<OutputStream> consumerOutputStream = excelService.creationExcelListDossiers(
            context.getSession(),
            dossiersRefs
        );
        return FileDownloadUtils.getAttachmentXls(consumerOutputStream::accept, "export-dossiers.xls");
    }
}
