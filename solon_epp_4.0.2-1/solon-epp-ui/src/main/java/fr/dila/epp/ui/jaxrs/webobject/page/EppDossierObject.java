package fr.dila.epp.ui.jaxrs.webobject.page;

import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static java.util.Optional.ofNullable;

import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.services.EvenementUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.th.constants.EppTemplateConstants;
import fr.dila.epp.ui.th.model.EppCorbeilleTemplate;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.Onglet;
import fr.dila.st.ui.bean.OngletConteneur;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliDossier")
public class EppDossierObject extends SolonWebObject {

    @GET
    public ThTemplate doHome() {
        ThTemplate template = getMyTemplate();
        template.setName("pages/espaceTravailHome");
        context.removeNavigationContextTitle();
        template.setContext(context);

        return template;
    }

    protected OngletConteneur actionsToTabs(List<Action> actions, String current) {
        OngletConteneur conteneur = new OngletConteneur();
        List<Onglet> onglets = new ArrayList<>();
        for (Action action : actions) {
            Onglet onglet = new Onglet();
            onglet.setLabel(action.getLabel());
            onglet.setId((String) action.getProperties().get("name"));
            if (onglet.getId().equals(current)) {
                onglet.setFragmentFile((String) action.getProperties().get("fragmentFile"));
                onglet.setFragmentName((String) action.getProperties().get("fragmentName"));
                onglet.setIsActif(true);
            }
            onglet.setScript((String) action.getProperties().get("script"));
            onglets.add(onglet);
        }
        conteneur.setOnglets(onglets);
        return conteneur;
    }

    @Path("{id}/{tab}")
    public Object getDossier(
        @PathParam("id") String id,
        @PathParam("tab") String tab,
        @QueryParam("version") String versionNum
    ) {
        template.setName("pages/communication/consult");
        template.setContext(context);

        // On indique en context l'onglet courant
        context.getContextData().put("currentTab", tab);

        context.putInContextData(ID, id);
        context.putInContextData(EppContextDataKey.VERSION_ID, versionNum);
        EvenementUIService evenementUIService = SolonEppUIServiceLocator.getEvenementUIService();
        evenementUIService.consulterEvenement(context);
        String typeEvenement = context.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement();

        String title = String.format(
            "Communication : %s",
            ofNullable(SolonEppServiceLocator.getEvenementTypeService())
                .map(service -> service.getEvenementType(typeEvenement))
                .map(EvenementTypeDescriptor::getLabel)
                .orElse(typeEvenement)
        );
        context.setNavigationContextTitle(
            new Breadcrumb(
                title,
                "/dossier/" + id + "/" + tab,
                Breadcrumb.SUBTITLE_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );

        Map<String, Object> map = new HashMap<>();

        map.put(EppTemplateConstants.DOSSIER_ID, id);
        map.put(STTemplateConstants.TITRE, title);
        map.put(EppTemplateConstants.ETAT_MESSAGE, evenementUIService.getEtatMessage(context));
        map.put(EppTemplateConstants.ETAT_EVENEMENT, evenementUIService.getEtatEvenement(context));
        List<Action> actions = context.getActions(STActionCategory.VIEW_ACTION_LIST);
        map.put("myTabs", actionsToTabs(actions, tab));
        map.put(EppTemplateConstants.ID_COMM, context.getCurrentDocument().getId());

        if (context.getNavigationContext().size() > 1) {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        } else {
            context.removeNavigationContextTitle();
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, "");
        }

        template.setData(map);

        return newObject("EppDossier" + StringUtils.capitalize(tab), context, template);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new EppCorbeilleTemplate();
    }
}
