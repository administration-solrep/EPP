package fr.dila.epp.ui.jaxrs.webobject.ajax;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.JSON_SEARCH;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.MESSAGE_LIST_FORM;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import fr.dila.epp.ui.bean.MessageList;
import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.enumeration.SuggestionType;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.th.bean.MessageListForm;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.validators.annot.SwNotEmpty;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "RechercheAjax")
public class EppRechercheAjax extends SolonWebObject {
    public static final String RECHERCHE_RESULT_TITLE = "recherche.result.title";
    public static final String RECHERCHE_RESULT_NUMBER_RESULTS = "recherche.result.numberResults";

    @SuppressWarnings("rawtypes")
    @POST
    @Path("resultats")
    public ThTemplate doRecherche(@FormParam("search") String jsonSearch) {
        template.setContext(context);

        Gson gson = new Gson();
        Map<String, Object> mapSearch = jsonSearch != null ? gson.fromJson(jsonSearch, Map.class) : new HashMap<>();

        String categorie = MapUtils.isNotEmpty(mapSearch)
            ? (String) mapSearch.get("categorie")
            : "PROCEDURE_LEGISLATIVE";

        if (template.getData() != null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        if (jsonSearch != null) {
            UserSessionHelper.putUserSessionParameter(context, EppContextDataKey.JSON_SEARCH + categorie, jsonSearch);
        } else {
            if (
                UserSessionHelper.getUserSessionParameter(
                    context,
                    EppContextDataKey.JSON_SEARCH + categorie,
                    String.class
                ) !=
                null
            ) {
                mapSearch =
                    gson.fromJson(
                        UserSessionHelper.getUserSessionParameter(
                            context,
                            EppContextDataKey.JSON_SEARCH + categorie,
                            String.class
                        ),
                        Map.class
                    );
            }
        }

        MessageListForm msgform = new MessageListForm(jsonSearch);
        context.putInContextData(JSON_SEARCH, jsonSearch);
        context.putInContextData(MESSAGE_LIST_FORM, msgform);
        context.putInContextData(EppContextDataKey.MAP_SEARCH, mapSearch != null ? mapSearch : new HashMap<>());
        MessageList lstResults = SolonEppUIServiceLocator.getRechercheUIService().getResultatsRecherche(context);

        template.getData().put(STTemplateConstants.DISPLAY_TABLE, lstResults.getNbTotal() > 0);
        template.getData().put(STTemplateConstants.NB_RESULTATS, lstResults.getNbTotal());
        template.getData().put(STTemplateConstants.TITRE, ResourceHelper.getString(RECHERCHE_RESULT_TITLE));
        template
            .getData()
            .put(
                STTemplateConstants.SOUS_TITRE,
                lstResults.getNbTotal() + ResourceHelper.getString(RECHERCHE_RESULT_NUMBER_RESULTS)
            );
        template.getData().put(STTemplateConstants.RESULT_LIST, lstResults);
        template.getData().put(STTemplateConstants.LST_COLONNES, lstResults.getListeColones(msgform));
        template.getData().put(STTemplateConstants.RESULT_FORM, msgform);
        template.getData().put(STTemplateConstants.DATA_URL, "/recherche");
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/recherche/resultats");

        return template;
    }

    @GET
    @Path("suggestions")
    public String getSuggestions(
        @QueryParam("input") String inputValue,
        @SwNotEmpty @QueryParam("typeSelection") String typeSelection,
        @QueryParam("inclure") String inclure,
        @QueryParam("emetteur") String emetteur
    )
        throws JsonProcessingException {
        SpecificContext context = new SpecificContext();

        context.putInContextData(EppContextDataKey.INPUT, inputValue);
        context.putInContextData(EppContextDataKey.FULL_TABLEREF, BooleanUtils.toBoolean(inclure));
        context.putInContextData(EppContextDataKey.EMETTEUR, emetteur);

        List<SuggestionDTO> list = SuggestionType.fromValue(typeSelection).getSuggestions(context);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(list);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        ThTemplate myTemplate = new AjaxLayoutThTemplate("fragments/recherche/resultatsRecherche", getMyContext());
        myTemplate.setData(new HashMap<>());
        return myTemplate;
    }
}
