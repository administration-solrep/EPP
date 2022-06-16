package fr.dila.st.ui.jaxrs.webobject.pages.admin.organigramme;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUniteStructurelleUIService;
import fr.dila.st.ui.th.bean.EntiteNorDirection;
import fr.dila.st.ui.th.bean.OrganigrammeNorDirectionForm;
import fr.dila.st.ui.th.bean.OrganigrammeNorDirectionsForm;
import fr.dila.st.ui.th.bean.UniteStructurelleForm;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.STAdminTemplate;
import fr.dila.st.ui.th.model.STUtilisateurTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.validators.annot.SwRequired;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammeUniteStructurelle")
public class STOrganigrammeUniteStructurelle extends SolonWebObject implements SharedBetweenAdminAndUser {
    public static final String CUR_MIN = "curMin";
    private static final String US_FORM = "uniteStructurelleForm";
    private static final String NAVIGATION_TITLE_CREATION = "Création d'unité structurelle";
    private static final String NAVIGATION_TITLE_MODIFICATION = "Modification d'unité structurelle";

    private static final ImmutableMap<String, String> US_TYPE_OPTIONS = ImmutableMap.of(
        "DIR",
        "organigramme.unite.structurelle.type.direction",
        "OTHER",
        "organigramme.unite.structurelle.type.autre"
    );

    public STOrganigrammeUniteStructurelle() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new STAdminTemplate();
    }

    private void setOrganigrammeNodeInContext(String id, String ministereId) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(id, OrganigrammeType.MINISTERE);

        if (node == null) {
            node = organigrammeService.getOrganigrammeNodeById(id, OrganigrammeType.UNITE_STRUCTURELLE);
            if (node == null) {
                node = organigrammeService.getOrganigrammeNodeById(id, OrganigrammeType.INSTITUTION);
            }
        }
        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(context.getSession(), node, ministereId)
        );
    }

    @GET
    @Path("creation")
    public ThTemplate getUniteStructurelleCreation(
        @SwRequired @QueryParam("idParent") String idParent,
        @QueryParam(CUR_MIN) String ministereId
    ) {
        String[] ids = idParent.split(";");
        setOrganigrammeNodeInContext(ids[0], ministereId);

        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.CREATE_UNITE_STRUCTURELLE, "/admin/organigramme/unitestructurelle/creation");

        ThTemplate template = getMySharedTemplate();

        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_CREATION,
                "/admin/organigramme/unitestructurelle/creation",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        template.setName("pages/organigramme/editUniteStructurelle");
        template.setContext(context);

        UniteStructurelleForm uniteStructurelleForm = context.getFromContextData(
            STContextDataKey.UNITE_STRUCTURELLE_FORM
        );
        // Cas du réaffichage après erreur
        if (uniteStructurelleForm == null) {
            uniteStructurelleForm = new UniteStructurelleForm();
        }

        // Récupérer le ministère (ou l'unité structurelle) à partir du param

        HashMap<String, String> mapMinParent = new HashMap<>();
        HashMap<String, String> mapInstParent = new HashMap<>();
        HashMap<String, String> mapUsParent = new HashMap<>();
        OrganigrammeTreeUIService treeService = STUIServiceLocator.getOrganigrammeTreeService();

        for (String id : idParent.split(";")) {
            OrganigrammeNode parentNode = treeService.findNodeHavingIdAndChildType(
                id,
                OrganigrammeType.UNITE_STRUCTURELLE
            );

            if (parentNode != null) {
                OrganigrammeType typeOrganigramme = parentNode.getType();
                switch (typeOrganigramme) {
                    case INSTITUTION:
                        mapInstParent.put(parentNode.getId(), parentNode.getLabel());
                        break;
                    case MINISTERE:
                        mapMinParent.put(parentNode.getId(), parentNode.getLabel());
                        // Dans le cas où le nom existe déjà (Il ne faut pas rajouté une entité nor dans le formulaire qui est déjà présente)
                        if (!containsParentNode(uniteStructurelleForm.getKeyNors(), parentNode.getId())) {
                            uniteStructurelleForm
                                .getKeyNors()
                                .add(new EntiteNorDirection(parentNode.getId(), StringUtils.EMPTY));
                        }
                        break;
                    case UNITE_STRUCTURELLE:
                    case DIRECTION:
                        mapUsParent.put(parentNode.getId(), parentNode.getLabel());
                        break;
                    default:
                        break;
                }
            }
        }
        uniteStructurelleForm.setMapMinisteresRatachement(mapMinParent);
        uniteStructurelleForm.setMapUnitesStructurellesRattachement(mapUsParent);
        uniteStructurelleForm.setMapInstitution(mapInstParent);

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(US_FORM, uniteStructurelleForm);
        map.put("typeUniteStructurelleOptions", US_TYPE_OPTIONS);
        map.put(CUR_MIN, ministereId);
        template.setData(map);

        return template;
    }

    private boolean containsParentNode(List<EntiteNorDirection> keyNors, String idParent) {
        return (
            CollectionUtils.isNotEmpty(keyNors) &&
            keyNors.stream().anyMatch(key -> Objects.equals(idParent, key.getId()))
        );
    }

    @GET
    @Path("modification")
    public ThTemplate getUniteStructurelleModification(
        @QueryParam("idUniteStructurelle") String idUniteStructurelle,
        @QueryParam(CUR_MIN) String ministereId
    ) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode uniteStructurelle = organigrammeService.getOrganigrammeNodeById(
            idUniteStructurelle,
            OrganigrammeType.UNITE_STRUCTURELLE
        );
        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(context.getSession(), uniteStructurelle, ministereId)
        );

        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.MODIFY_UNITE_STRUCTURELLE, "/admin/organigramme/unitestructurelle/modification");

        ThTemplate template = getMySharedTemplate();
        context.setNavigationContextTitle(
            new Breadcrumb(
                NAVIGATION_TITLE_MODIFICATION,
                "/admin/organigramme/unitestructurelle/modification",
                STOrganigramme.ORGANIGRAMME_ORDER + 1
            )
        );
        template.setName("pages/organigramme/editUniteStructurelle");
        template.setContext(context);

        UniteStructurelleForm uniteStructurelleForm = context.getFromContextData(
            STContextDataKey.UNITE_STRUCTURELLE_FORM
        );

        // Cas du réaffichage après erreur
        if (uniteStructurelleForm != null) {
            // récuếrer les nodes US et Ministere
            OrganigrammeNode parentNode;

            for (String idParentInst : uniteStructurelleForm.getInstitutions()) {
                parentNode = organigrammeService.getOrganigrammeNodeById(idParentInst, OrganigrammeType.INSTITUTION);
                uniteStructurelleForm.getMapInstitution().put(parentNode.getId(), parentNode.getLabel());
            }
            for (String idParentMin : uniteStructurelleForm.getMinisteresRatachement()) {
                parentNode = organigrammeService.getOrganigrammeNodeById(idParentMin, OrganigrammeType.MINISTERE);
                uniteStructurelleForm.getMapMinisteresRatachement().put(parentNode.getId(), parentNode.getLabel());
            }
            for (String idParentUS : uniteStructurelleForm.getUnitesStructurellesRattachement()) {
                parentNode =
                    organigrammeService.getOrganigrammeNodeById(idParentUS, OrganigrammeType.UNITE_STRUCTURELLE);
                uniteStructurelleForm
                    .getMapUnitesStructurellesRattachement()
                    .put(parentNode.getId(), parentNode.getLabel());
            }
        } else {
            // Set contextData
            context.putInContextData(STContextDataKey.ID, idUniteStructurelle);
            STUniteStructurelleUIService uniteStructurelleUIService = STUIServiceLocator.getSTUniteStructurelleUIService();
            uniteStructurelleForm = uniteStructurelleUIService.getUniteStructurelleForm(context);

            initialisationNors(uniteStructurelleForm);
        }

        // Je renvoie mon formulaire en sortie
        Map<String, Object> map = new HashMap<>();
        map.put(US_FORM, uniteStructurelleForm);
        map.put("typeUniteStructurelleOptions", US_TYPE_OPTIONS);
        map.put(CUR_MIN, ministereId);
        template.setData(map);

        return template;
    }

    private void initialisationNors(UniteStructurelleForm uniteStructurelleForm) {
        // On précharge le formulaire manquant pour les nors
        if (uniteStructurelleForm.getKeyNors().size() < uniteStructurelleForm.getMapMinisteresRatachement().size()) {
            for (String entryMinId : uniteStructurelleForm.getMapMinisteresRatachement().keySet()) {
                if (
                    uniteStructurelleForm
                        .getKeyNors()
                        .stream()
                        .noneMatch(entiteNor -> Objects.equals(entiteNor.getId(), entryMinId))
                ) {
                    uniteStructurelleForm.getKeyNors().add(new EntiteNorDirection(entryMinId, StringUtils.EMPTY));
                }
            }
        } else if (
            uniteStructurelleForm.getKeyNors().size() > uniteStructurelleForm.getMapMinisteresRatachement().size()
        ) {
            final Set<String> minIdSet = uniteStructurelleForm.getMapMinisteresRatachement().keySet();
            uniteStructurelleForm.setKeyNors(
                uniteStructurelleForm
                    .getKeyNors()
                    .stream()
                    .filter(direction -> minIdSet.contains(direction.getId()))
                    .collect(Collectors.toList())
            );
        }
    }

    private static List<EntiteNorDirection> mapFlatLstNors(List<String> lstNors) {
        return lstNors
            .stream()
            .map(STOrganigrammeUniteStructurelle::createEntiteNorDirectionFromNor)
            .collect(Collectors.toList());
    }

    private static EntiteNorDirection createEntiteNorDirectionFromNor(String nor) {
        if (StringUtils.isNotEmpty(nor) && nor.contains(":")) {
            String[] minNorArray = nor.split(":");
            return new EntiteNorDirection(minNorArray[0], minNorArray.length >= 2 ? minNorArray[1] : StringUtils.EMPTY);
        } else {
            throw new STValidationException("organigramme.direction.missingdef");
        }
    }

    @POST
    @Produces("text/html;charset=UTF-8")
    @Path("sauvegarde")
    public Object saveOrUpdateUniteStructurelle(
        @SwBeanParam UniteStructurelleForm uniteStructurelleForm,
        @FormParam(CUR_MIN) String minParent
    ) {
        STUniteStructurelleUIService uniteStructurelleUIService = STUIServiceLocator.getSTUniteStructurelleUIService();
        String identifiant = uniteStructurelleForm.getIdentifiant();
        if (CollectionUtils.isNotEmpty(uniteStructurelleForm.getLstNors())) {
            uniteStructurelleForm.setKeyNors(mapFlatLstNors(uniteStructurelleForm.getLstNors()));
        }

        boolean isCreation = StringUtils.isBlank(identifiant);

        context.putInContextData(STContextDataKey.UNITE_STRUCTURELLE_FORM, uniteStructurelleForm);

        if (isCreation) {
            uniteStructurelleUIService.createUniteStructurelle(context);
        } else {
            uniteStructurelleUIService.updateUniteStructurelle(context);
        }

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
            return redirect(STOrganigramme.ORGANIGRAMME_URL);
        } else {
            // Concaténer les ids parent en une chaine de carractère pour le
            // passer en paramètre de création
            List<String> listParent = new ArrayList<>();
            listParent.addAll(uniteStructurelleForm.getUnitesStructurellesRattachement());
            listParent.addAll(uniteStructurelleForm.getMinisteresRatachement());
            listParent.addAll(uniteStructurelleForm.getInstitutions());
            String idParents = String.join(";", listParent);

            return isCreation
                ? getUniteStructurelleCreation(idParents, minParent)
                : getUniteStructurelleModification(identifiant, minParent);
        }
    }

    @GET
    @Produces("text/html;charset=UTF-8")
    @Path("ajouterNorDirection")
    public ThTemplate addNorDirection(@QueryParam("idParent") String idParent, @QueryParam("value") String value) {
        AjaxLayoutThTemplate ajaxTemplate = new AjaxLayoutThTemplate(
            "fragments/components/input-nor-direction",
            context
        );

        OrganigrammeNode node = STUIServiceLocator
            .getOrganigrammeTreeService()
            .findNodeHavingIdAndChildType(idParent, OrganigrammeType.DIRECTION);
        String minId = "empty";
        String minLabel = "Min. inconnu";
        if (node != null) {
            minId = node.getId();
            minLabel = node.getLabel();
        }
        Map<String, String> mapEntiteLabel = Collections.singletonMap(minId, minLabel);

        // CheckMarx on ne réutilise pas directement notre valeur
        String norValue = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(value) && value.length() == 1) {
            norValue = value;
        }

        ajaxTemplate.getData().put("norDir", new EntiteNorDirection(minId, norValue));

        ajaxTemplate.getData().put("mapEntiteLabel", mapEntiteLabel);

        return ajaxTemplate;
    }

    @POST
    @Produces("text/html;charset=UTF-8")
    @Path("ajouterNorDirections")
    public ThTemplate addNorDirections(@SwBeanParam OrganigrammeNorDirectionsForm organigrammeNorDirectionForms) {
        AjaxLayoutThTemplate ajaxTemplate = new AjaxLayoutThTemplate(
            "fragments/components/input-nor-directions",
            context
        );
        List<EntiteNorDirection> entiteNorDirections = new ArrayList<>();
        Map<String, String> mapEntiteLabel = new HashMap<>();
        if (CollectionUtils.isNotEmpty(organigrammeNorDirectionForms.getNorDirections())) {
            for (OrganigrammeNorDirectionForm norDirection : organigrammeNorDirectionForms.getNorDirections()) {
                OrganigrammeNode node = STUIServiceLocator
                    .getOrganigrammeTreeService()
                    .findNodeHavingIdAndChildType(norDirection.getIdParent(), OrganigrammeType.DIRECTION);
                String minId = "empty";
                String minLabel = "Min. inconnu";
                if (node != null) {
                    minId = node.getId();
                    minLabel = node.getLabel();
                }

                mapEntiteLabel.put(minId, minLabel);

                // CheckMarx on ne réutilise pas directement notre valeur
                String value = norDirection.getNor();

                String norValue = StringUtils.EMPTY;
                if (StringUtils.isNotBlank(value) && value.length() == 1) {
                    norValue = value;
                }
                entiteNorDirections.add(new EntiteNorDirection(minId, norValue));
            }
        }
        ajaxTemplate.getData().put("norDirs", entiteNorDirections);
        ajaxTemplate.getData().put("mapEntiteLabel", mapEntiteLabel);

        return ajaxTemplate;
    }

    @Override
    public ThTemplate getMyAdminTemplate() {
        return new STAdminTemplate();
    }

    @Override
    public ThTemplate getMyUserTemplate() {
        return new STUtilisateurTemplate();
    }
}
