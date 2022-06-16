package fr.dila.ss.ui.jaxrs.webobject.ajax.journal;

import static fr.dila.ss.ui.services.SSUIServiceLocator.getJournalUIService;

import fr.dila.ss.ui.bean.JournalDossierResultList;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.th.bean.JournalDossierForm;
import fr.dila.ss.ui.th.bean.JournalSearchForm;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.validators.annot.SwId;
import fr.dila.st.ui.validators.annot.SwRequired;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "JournalAjax")
public class SsJournalAjax extends SolonWebObject {
    public static final String DOSSIER_ID = "dossierId";
    public static final String DOSSIER_LINK_ID = "dossierLinkId";
    public static final String JOURNAL_LIST_FORM = "journalListForm";
    public static final String JOURNAL_SEARCH_FORM = "journalSearchForm";

    public SsJournalAjax() {
        super();
    }

    @Path("/journalDossier")
    @POST
    public ThTemplate getJournalDossier(
        @SwRequired @SwId @FormParam(DOSSIER_ID) String dossierID,
        @SwId @FormParam(DOSSIER_LINK_ID) String dossierLinkID,
        @SwBeanParam JournalSearchForm journal,
        @SwBeanParam JournalDossierForm resultForm
    ) {
        checkAccessRight(context, dossierID);

        ThTemplate template = getMyTemplate();
        context.putInContextData(JOURNAL_LIST_FORM, resultForm);
        context.putInContextData(JOURNAL_SEARCH_FORM, journal);
        context.putInContextData(DOSSIER_LINK_ID, dossierLinkID);
        context.putInContextData(DOSSIER_ID, dossierID);

        Map<String, Object> otherParameter = new HashMap<>();
        otherParameter.put(DOSSIER_ID, dossierID);
        template.getData().put(STTemplateConstants.OTHER_PARAMETER, otherParameter);

        context.setCurrentDocument(dossierID);
        JournalDossierResultList resultListe = getJournalUIService().getJournalDTO(context);

        template.setContext(context);
        template.getData().put(STTemplateConstants.RESULT_LIST, resultListe);
        template.getData().put(STTemplateConstants.LST_COLONNES, resultListe.getListeColonnes(resultForm));
        template.getData().put(STTemplateConstants.RESULT_FORM, resultForm);
        template.getData().put(STTemplateConstants.DATA_URL, "/dossier/" + dossierID + "/journal");
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/journal/journalDossier");
        return template;
    }

    protected void buildContext(SpecificContext context, String dossierID) {
        context.setCurrentDocument(dossierID);
    }

    protected void checkAccessRight(SpecificContext context, String dossierID) {
        buildContext(context, dossierID);
        if (context.getAction(SSActionEnum.TAB_DOSSIER_JOURNAL) == null) {
            throw new STAuthorizationException("Accès refusé au journal");
        }
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/table/dossierJournalTable", getMyContext());
    }
}
