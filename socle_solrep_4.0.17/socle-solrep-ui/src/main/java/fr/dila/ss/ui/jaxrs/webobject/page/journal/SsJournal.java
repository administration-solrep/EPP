package fr.dila.ss.ui.jaxrs.webobject.page.journal;

import static fr.dila.ss.ui.enums.SSActionCategory.DOSSIER_TAB_ACTIONS;
import static fr.dila.ss.ui.enums.SSContextDataKey.OTHER_PARAMETER;
import static fr.dila.ss.ui.jaxrs.webobject.ajax.journal.SsJournalAjax.DOSSIER_ID;
import static fr.dila.ss.ui.jaxrs.webobject.ajax.journal.SsJournalAjax.JOURNAL_LIST_FORM;
import static fr.dila.ss.ui.jaxrs.webobject.ajax.journal.SsJournalAjax.JOURNAL_SEARCH_FORM;
import static fr.dila.st.core.util.ObjectHelper.requireNonNullElseGet;

import fr.dila.ss.ui.bean.JournalDossierResultList;
import fr.dila.ss.ui.th.bean.JournalDossierForm;
import fr.dila.ss.ui.th.bean.JournalSearchForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliDossierJournal")
public class SsJournal extends AbstractSsJournal {

    public SsJournal() {
        super();
    }

    @GET
    public ThTemplate getJournal(@PathParam("id") String dossierID, @SwBeanParam JournalSearchForm journalForm) {
        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        Map<String, Object> otherParameter = new HashMap<>();
        otherParameter.put(DOSSIER_ID, dossierID);
        template.getData().put(OTHER_PARAMETER.getName(), otherParameter);

        template.getData().put("tabActions", context.getActions(DOSSIER_TAB_ACTIONS));

        context.setCurrentDocument(dossierID);

        journalForm = requireNonNullElseGet(journalForm, JournalSearchForm::new);
        JournalDossierForm resultForm = new JournalDossierForm();
        context.putInContextData(JOURNAL_SEARCH_FORM, journalForm);
        context.putInContextData(JOURNAL_LIST_FORM, resultForm);

        JournalDossierResultList resultListe = getJournalUIService().getJournalDTO(context);

        if (journalForm.getUtilisateurKey() != null) {
            journalForm.setMapUtilisateur(
                Collections.singletonMap(
                    journalForm.getUtilisateurKey(),
                    STServiceLocator.getSTUserService().getUserFullNameWithUsername(journalForm.getUtilisateurKey())
                )
            );
        }

        template.getData().put(SSTemplateConstants.JOURNAL_FORM, journalForm);
        template.getData().put(STTemplateConstants.RESULT_LIST, resultListe);
        template.getData().put(STTemplateConstants.LST_COLONNES, resultListe.getListeColonnes(resultForm));
        template.getData().put("categories", getCategories());
        template.getData().put(STTemplateConstants.RESULT_FORM, resultForm);
        template.getData().put(STTemplateConstants.DATA_URL, "/dossier/" + dossierID + "/journal");
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/journal/journalDossier");
        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveJournal() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), null).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/journal/journal", getMyContext());
    }
}
