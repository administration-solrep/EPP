package fr.dila.ss.ui.jaxrs.webobject.page.dossier;

import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.ss.ui.th.model.SSLayoutThTemplate;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "SubstitutionFDR")
public class SSSubstitutionFDR extends SolonWebObject {
    private static final String URL_BREADCRUMB = "/dossier/";
    private static final int LIST_MODELE_ORDER = Breadcrumb.SUBTITLE_ORDER + 1;

    public SSSubstitutionFDR() {
        super();
    }

    public ThTemplate buildTemplateListModeleSubstitution(ModeleFDRList lstResults, String intitule)
        throws IllegalAccessException, InstantiationException {
        ThTemplate template = getMyTemplate(context);
        DocumentModel dossierDoc = context.getCurrentDocument();
        context.setNavigationContextTitle(
            new Breadcrumb(
                ResourceHelper.getString("fdr.substituer.liste.title.navigation"),
                URL_BREADCRUMB + dossierDoc.getId() + "/substitution/liste#main_content",
                LIST_MODELE_ORDER,
                context.getWebcontext().getRequest()
            )
        );
        template.setName("pages/fdr/listeModeleFdrSubstitution");
        template.setContext(context);

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, lstResults);
        map.put(STTemplateConstants.RESULT_FORM, new ModeleFDRListForm());
        map.put(STTemplateConstants.LST_COLONNES, lstResults.getListeColonnes());
        map.put(STTemplateConstants.INTITULE, intitule);
        map.put(STTemplateConstants.ID_DOSSIER, dossierDoc.getId());
        if (context.getNavigationContext().size() > 1) {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        } else {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, "");
        }
        map.put(STTemplateConstants.DATA_URL, URL_BREADCRUMB + dossierDoc.getId() + "/substitution/liste");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/dossier/" + dossierDoc.getId() + "/substitution/liste");
        template.setData(map);

        return template;
    }

    public ThTemplate buildTemplateSubstitutionConsult(DocumentModel dossierDoc, ModeleFdrForm modeleForm)
        throws IllegalAccessException, InstantiationException {
        ThTemplate template = getMyTemplate(context);
        template.setName("pages/admin/modele/consultModeleFDR");
        template.setContext(context);

        context.setNavigationContextTitle(
            new Breadcrumb(
                String.format("Consultation du modèle %s", modeleForm.getIntitule()),
                URL_BREADCRUMB + dossierDoc.getId() + "/substitution/consult?idModele=" + modeleForm.getId(),
                LIST_MODELE_ORDER + 1
            )
        );

        context.putInContextData(SSContextDataKey.MODELE_FORM, modeleForm);

        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.ID_DOSSIER, dossierDoc.getId());
        map.put(STTemplateConstants.ID_MODELE, modeleForm.getId());
        map.put(SSTemplateConstants.TYPE_ETAPE, SSUIServiceLocator.getSSSelectValueUIService().getRoutingTaskTypes());
        map.put(SSTemplateConstants.MODELE_FORM, modeleForm);
        map.put(
            SSTemplateConstants.MODELE_LEFT_ACTIONS,
            context.getActions(SSActionCategory.SUBSTITUTION_ACTIONS_LEFT)
        );
        map.put(
            SSTemplateConstants.MODELE_RIGHT_ACTIONS,
            context.getActions(SSActionCategory.SUBSTITUTION_ACTIONS_RIGHT)
        );

        if (context.getNavigationContext().size() > 1) {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        } else {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, "");
        }

        template.setData(map);
        return template;
    }

    protected ThTemplate getMyTemplate(SpecificContext context) throws IllegalAccessException, InstantiationException {
        @SuppressWarnings("unchecked")
        Class<ThTemplate> oldTemplate = (Class<ThTemplate>) context
            .getWebcontext()
            .getUserSession()
            .get(SpecificContext.LAST_TEMPLATE);
        if (oldTemplate == ThTemplate.class) {
            return new SSLayoutThTemplate();
        } else {
            return oldTemplate.newInstance();
        }
    }

    protected String getValiderSubstitutionUrl(List<Breadcrumb> navigationContext) {
        String url = "/travail";
        if (CollectionUtils.isNotEmpty(navigationContext)) {
            url = navigationContext.get(navigationContext.size() - 3).getUrl();
        }
        return url;
    }
}
