package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.core.enumeration.OperatorEnum;
import fr.dila.ss.core.enumeration.TypeChampEnum;
import fr.dila.ss.ui.bean.RequeteExperteDTO;
import fr.dila.ss.ui.bean.RequeteExperteForm;
import fr.dila.ss.ui.bean.RequeteLigneDTO;
import fr.dila.ss.ui.jaxrs.webobject.page.SSRequeteExperte;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.core.requete.recherchechamp.Parametre;
import fr.dila.st.core.requete.recherchechamp.RechercheChampService;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.validators.annot.SwId;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxRequeteExperte")
public class SSRequeteExperteAjax extends SolonWebObject implements SSRequeteExperte {
    private static final String PARAMETERS = "parameters";

    private static final String TYPE_CHAMP = "typeChamp";

    private static final String CHAMP = "champ";

    private static final String REQUETE_EXPERTE_TABLE_FRAGMENT_NAME = "fragments/components/requeteExperteTable";

    protected static final String REQUETES_NAME = "requetes";

    protected static final String POSTE_ETAPE = "POSTE_ETAPE";

    private static final String IDENTIFIANT_POSTE = "IDENTIFIANT_POSTE";

    public SSRequeteExperteAjax() {
        super();
    }

    @POST
    @Path("add")
    public ThTemplate addLineToTable(@SwBeanParam RequeteExperteForm requete) {
        ThTemplate template = new AjaxLayoutThTemplate(REQUETE_EXPERTE_TABLE_FRAGMENT_NAME, context);

        RequeteExperteDTO dto = UserSessionHelper.getUserSessionParameter(
            context,
            getDtoSessionKey(context),
            RequeteExperteDTO.class
        );
        RequeteLigneDTO newLine = getLigneFormFrom(requete);

        STPostesService postesService = STServiceLocator.getSTPostesService();
        if (
            newLine.getChamp() != null &&
            Arrays.asList(POSTE_ETAPE, IDENTIFIANT_POSTE).contains(newLine.getChamp().getName())
        ) {
            newLine.setValue(
                newLine.getValue().stream().map(postesService::prefixPosteId).collect(Collectors.toList())
            );
        }

        if (dto == null) {
            dto = new RequeteExperteDTO();
        }
        dto.getRequetes().add(newLine);

        UserSessionHelper.putUserSessionParameter(context, getDtoSessionKey(context), dto);

        Map<String, Object> map = new HashMap<>();
        map.put(REQUETES_NAME, dto.getRequetes());
        template.setData(map);
        return template;
    }

    @GET
    @Path("select-champ")
    public ThTemplate getChoixChamp(@QueryParam("id") @SwId("[A-Z_]+") String id) {
        ThTemplate template = new AjaxLayoutThTemplate("fragments/components/requeteExperteChamp", context);

        RechercheChampService champService = STServiceLocator.getRechercheChampService();

        ChampDescriptor champ = champService.getChamp(getContribName(), id);
        TypeChampEnum typeChamp = TypeChampEnum.valueOf(champ.getTypeChamp());
        Map<String, Serializable> parameters = champ
            .getParametres()
            .stream()
            .collect(Collectors.toMap(Parametre::getName, Parametre::getValue));

        Map<String, Object> map = new HashMap<>();
        map.put(CHAMP, champ);
        map.put(TYPE_CHAMP, typeChamp);
        map.put(PARAMETERS, parameters);
        template.setData(map);
        return template;
    }

    private RequeteLigneDTO getLigneFormFrom(RequeteExperteForm form) {
        RequeteLigneDTO ligne = new RequeteLigneDTO();
        RechercheChampService champService = STServiceLocator.getRechercheChampService();
        ligne.setOperator(OperatorEnum.getByOperator(form.getOperator()));
        ligne.setChamp(champService.getChamp(getContribName(), form.getChamp()));
        ligne.setValue(form.getValue());
        ligne.setDisplayValue(form.getDisplayValue());
        ligne.setAndOr(form.getAndOr());
        return ligne;
    }

    @GET
    @Path("remove")
    public ThTemplate removeLine(@QueryParam("order") int order) {
        ThTemplate template = new AjaxLayoutThTemplate(REQUETE_EXPERTE_TABLE_FRAGMENT_NAME, context);

        RequeteExperteDTO dto = UserSessionHelper.getUserSessionParameter(
            context,
            getDtoSessionKey(context),
            RequeteExperteDTO.class
        );
        dto.getRequetes().remove(order);

        //Pour eviter qu'une requete ne commence par et/ou
        if (order == 0 && CollectionUtils.isNotEmpty(dto.getRequetes()) && dto.getRequetes().get(0) != null) {
            dto.getRequetes().get(0).setAndOr(null);
        }

        // Si la liste devient vide, on traite le cas comme une réinitialisation.
        if (CollectionUtils.isEmpty(dto.getRequetes())) {
            UserSessionHelper.clearUserSessionParameter(context, getDtoSessionKey(context));
        } else {
            UserSessionHelper.putUserSessionParameter(context, getDtoSessionKey(context), dto);
        }
        UserSessionHelper.clearUserSessionParameter(context, getResultsSessionKey(context));

        Map<String, Object> map = new HashMap<>();
        map.put(REQUETES_NAME, dto.getRequetes());
        template.setData(map);
        return template;
    }

    @GET
    @Path("move")
    public ThTemplate moveLine(@QueryParam("order") int order, @QueryParam("direction") int direction) {
        ThTemplate template = new AjaxLayoutThTemplate(REQUETE_EXPERTE_TABLE_FRAGMENT_NAME, context);

        RequeteExperteDTO dto = UserSessionHelper.getUserSessionParameter(
            context,
            getDtoSessionKey(context),
            RequeteExperteDTO.class
        );
        Collections.swap(dto.getRequetes(), order, order + direction);

        //Pour eviter qu'une requete ne commence par et/ou
        if (order == 0 || order + direction == 0) {
            String oldAndOr = dto.getRequetes().get(order).getAndOr();
            dto.getRequetes().get(order).setAndOr(dto.getRequetes().get(order + direction).getAndOr());
            dto.getRequetes().get(order + direction).setAndOr(oldAndOr);
        }

        UserSessionHelper.putUserSessionParameter(context, getDtoSessionKey(context), dto);
        UserSessionHelper.clearUserSessionParameter(context, getResultsSessionKey(context));

        Map<String, Object> map = new HashMap<>();
        map.put(REQUETES_NAME, dto.getRequetes());
        template.setData(map);
        return template;
    }

    @GET
    @Path("reinit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reinitRequete() {
        UserSessionHelper.clearUserSessionParameter(context, getDtoSessionKey(context));
        UserSessionHelper.clearUserSessionParameter(context, getResultsSessionKey(context));
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    protected String getContribName() {
        throw new UnsupportedOperationException("Implemented in app-specific classes");
    }
}
