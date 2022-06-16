package fr.dila.st.ui.jaxrs.webobject.ajax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.services.actions.suggestion.SuggestionHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "RechercheAjax")
public class STRechercheAjax extends SolonWebObject {
    public static final String PROFIL_KEY = "Profils";
    public static final String AUTEUR_KEY = "auteur";

    protected Map<String, Function<String, List<String>>> suggestionFunctions = new HashMap<>();

    public STRechercheAjax() {
        this.suggestionFunctions.put(
                PROFIL_KEY,
                input ->
                    SuggestionHelper.limitSuggestions(
                        STServiceLocator
                            .getSTDirectoryService()
                            .getSuggestions(input, STConstant.ORGANIGRAMME_PROFILE_DIR, "groupname")
                            .stream()
                            .filter(
                                profil ->
                                    BooleanUtils.isFalse(
                                        context.getFromContextData(STContextDataKey.ACTIVATE_FILTER)
                                    ) ||
                                    STActionsServiceLocator
                                        .getProfileSuggestionActionService()
                                        .filterProfilToDisplay(context.getSession().getPrincipal(), profil)
                            )
                    )
            );
        this.suggestionFunctions.put(
                AUTEUR_KEY,
                input -> STUIServiceLocator.getNomAuteurSuggestionProviderService().getSuggestions(input, context)
            );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/suggestions")
    public String getSuggestions(
        @QueryParam("input") String input,
        @QueryParam("typeSelection") String typeSelection,
        @QueryParam("activateFilter") boolean activateFilter,
        @QueryParam("selectionFilter") String selectionFilter
    )
        throws JsonProcessingException {
        if (StringUtils.isNotBlank(input)) {
            context.putInContextData(STContextDataKey.INPUT, input);
        }
        if (StringUtils.isNotBlank(typeSelection)) {
            context.putInContextData(STContextDataKey.TYPE_SELECTION, typeSelection);
        }
        if (StringUtils.isNotBlank(selectionFilter)) {
            context.putInContextData(STContextDataKey.SELECTION_FILTER, selectionFilter);
        }

        if (!this.suggestionFunctions.containsKey(typeSelection)) {
            return "[]";
        }

        context.putInContextData(STContextDataKey.ACTIVATE_FILTER, activateFilter);

        List<String> list = this.suggestionFunctions.get(typeSelection).apply(input);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(list);
    }
}
