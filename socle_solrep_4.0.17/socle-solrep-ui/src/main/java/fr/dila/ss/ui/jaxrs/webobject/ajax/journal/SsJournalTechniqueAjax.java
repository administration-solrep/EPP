package fr.dila.ss.ui.jaxrs.webobject.ajax.journal;

import static fr.dila.ss.ui.jaxrs.webobject.ajax.journal.SsJournalAjax.JOURNAL_LIST_FORM;
import static fr.dila.ss.ui.jaxrs.webobject.ajax.journal.SsJournalAjax.JOURNAL_SEARCH_FORM;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.SHORT_IDS;
import static java.time.ZoneId.of;
import static java.util.Date.from;

import fr.dila.ss.ui.bean.JournalTechniqueResultList;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.SSJournalAdminUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.bean.JournalDossierForm;
import fr.dila.ss.ui.th.bean.JournalSearchForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "JournalTechniqueAjax")
public class SsJournalTechniqueAjax extends SolonWebObject {
    private static final int NB_LIMIT_EXPORT = 65000;

    public SsJournalTechniqueAjax() {
        super();
    }

    @GET
    public ThTemplate getHome(
        @SwBeanParam JournalSearchForm journalTechnique,
        @SwBeanParam JournalDossierForm resultForm
    ) {
        return getResultSearch(journalTechnique, resultForm);
    }

    @POST
    @Path("resultats")
    public ThTemplate getResultSearch(
        @SwBeanParam JournalSearchForm journalTechnique,
        @SwBeanParam JournalDossierForm resultForm
    ) {
        if (context.getAction(SSActionEnum.ADMIN_JOURNAL_TECHNIQUE) == null) {
            throw new STAuthorizationException(getBaseUrl());
        }

        context.setNavigationContextTitle(
            new Breadcrumb(
                getBreadcrumbTitle(),
                getBaseUrl(),
                Breadcrumb.TITLE_ORDER,
                template.getContext().getWebcontext().getRequest()
            )
        );
        journalTechnique = initContext(journalTechnique, resultForm);
        JournalTechniqueResultList journalTechniqueList = getJournalAdminUIService().getJournalDTO(context);

        template.getData().put(SSTemplateConstants.JOURNAL_FORM, journalTechnique);
        template.getData().put(STTemplateConstants.RESULT_LIST, journalTechniqueList);
        template.getData().put(STTemplateConstants.NB_RESULTS, journalTechniqueList.getNbTotal());
        template.getData().put(STTemplateConstants.LST_COLONNES, journalTechniqueList.getListeColonnes(resultForm));
        template.getData().put(STTemplateConstants.AJAX_SEARCH_ENDPOINT, getAjaxUrl());
        template.getData().put(STTemplateConstants.RESULT_FORM, resultForm);
        template.getData().put(STTemplateConstants.DATA_URL, getBaseUrl());
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax" + getAjaxUrl());
        template.getData().put(SSTemplateConstants.DATA_AJAX_EXPORT_URL, getAjaxExportUrl());
        template.getData().put(SSTemplateConstants.EXPORT_LIMIT, NB_LIMIT_EXPORT);
        template.getData().put(SSTemplateConstants.LABEL_EXPORT, getLabelExport(journalTechniqueList.getNbTotal()));
        template
            .getData()
            .put(STTemplateConstants.EXPORT_ACTION, context.getAction(SSActionEnum.RECHERCHE_ADMIN_ACTION_EXPORT));
        return template;
    }

    private JournalSearchForm initContext(JournalSearchForm journalTechnique, JournalDossierForm resultForm) {
        if (journalTechnique.getUtilisateurKey() != null) {
            journalTechnique.setMapUtilisateur(
                Collections.singletonMap(
                    journalTechnique.getUtilisateurKey(),
                    STServiceLocator
                        .getSTUserService()
                        .getUserFullNameWithUsername(journalTechnique.getUtilisateurKey())
                )
            );
        }

        //La date de début est initialisée à la date du jour
        if (Objects.isNull(journalTechnique.getDateDebut())) {
            journalTechnique.setDateDebut(today());
        }
        context.putInContextData(JOURNAL_SEARCH_FORM, journalTechnique);
        context.putInContextData(JOURNAL_LIST_FORM, resultForm);
        return journalTechnique;
    }

    @Path("exporter")
    @GET
    @Produces("application/vnd.ms-excel")
    public Response exporter(
        @SwBeanParam JournalSearchForm journalTechnique,
        @SwBeanParam JournalDossierForm resultForm
    ) {
        verifyAction(SSActionEnum.ADMIN_JOURNAL_TECHNIQUE, getBaseUrl());

        initContext(journalTechnique, resultForm);

        File file = getJournalAdminUIService().exportJournal(context);

        return FileDownloadUtils.getAttachmentXls(file, "Liste.xls");
    }

    protected String getBaseUrl() {
        return "/admin/journal/technique";
    }

    protected String getAjaxUrl() {
        return "/admin/journalTechnique/resultats";
    }

    protected String getAjaxExportUrl() {
        return "/admin/journalTechnique/exporter";
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/table/tableJournalTechnique", getMyContext());
    }

    protected String getBreadcrumbTitle() {
        return "journal.technique.titre";
    }

    protected SSJournalAdminUIService getJournalAdminUIService() {
        return SSUIServiceLocator.getJournalAdminUIService();
    }

    private String today() {
        Calendar cal = Calendar.getInstance();
        Date d = from(now().toLocalDate().atStartOfDay(of(SHORT_IDS.get("ECT"))).toInstant());
        cal.setTime(d);
        return SolonDateConverter.DATE_SLASH.format(cal);
    }

    private String getLabelExport(Integer nbResult) {
        return (nbResult > NB_LIMIT_EXPORT) ? "journal.label.export.impossible" : "label.recherche.export.excel";
    }
}
