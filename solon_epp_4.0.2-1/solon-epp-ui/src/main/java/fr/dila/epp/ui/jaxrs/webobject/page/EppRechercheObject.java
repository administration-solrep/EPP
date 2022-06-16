package fr.dila.epp.ui.jaxrs.webobject.page;

import fr.dila.epp.ui.bean.RechercheDynamique;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.th.model.EppRechercheTemplate;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "AppliRecherche")
public class EppRechercheObject extends SolonWebObject {

    public EppRechercheObject() {
        super();
    }

    @GET
    @Path("{categorieEvent}")
    public ThTemplate getRecherche(@PathParam("categorieEvent") String categorieEvent) {
        template.setName("pages/recherche");
        context.removeNavigationContextTitle();
        template.setContext(context);

        context.setNavigationContextTitle(
            new Breadcrumb(
                STServiceLocator
                    .getVocabularyService()
                    .getEntryLabel(SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_VOCABULARY, categorieEvent),
                "/recherche/" + categorieEvent,
                Breadcrumb.TITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );
        context.putInContextData(STContextDataKey.ID, categorieEvent);

        RechercheDynamique dto = SolonEppUIServiceLocator.getRechercheUIService().getRechercheDynamique(context);
        template.getData().put(STTemplateConstants.LST_WIDGETS, dto.getLstWidgets());
        template.getData().put(STTemplateConstants.DISPLAY_TABLE, false);
        template.getData().put("categorie", categorieEvent);
        template
            .getData()
            .put(
                STTemplateConstants.TITRE,
                STServiceLocator
                    .getVocabularyService()
                    .getEntryLabel(SolonEppVocabularyConstant.CATEGORIE_EVENEMENT_VOCABULARY, categorieEvent)
            );

        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new EppRechercheTemplate();
    }
}
