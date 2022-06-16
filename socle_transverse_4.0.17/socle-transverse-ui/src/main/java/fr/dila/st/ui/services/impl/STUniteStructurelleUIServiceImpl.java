package fr.dila.st.ui.services.impl;

import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static fr.dila.st.ui.enums.STContextDataKey.UNITE_STRUCTURELLE_FORM;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.NorDirection;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.services.STUniteStructurelleUIService;
import fr.dila.st.ui.th.bean.EntiteNorDirection;
import fr.dila.st.ui.th.bean.UniteStructurelleForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

public class STUniteStructurelleUIServiceImpl
    extends STOrganigrammeUIServiceImpl<UniteStructurelleNode>
    implements STUniteStructurelleUIService {

    protected STUsAndDirectionService getSTUsAndDirectionService() {
        return STServiceLocator.getSTUsAndDirectionService();
    }

    @Override
    public UniteStructurelleForm getUniteStructurelleForm(SpecificContext context) {
        String identifiant = context.getFromContextData(ID);
        UniteStructurelleNode uniteStructurelleNode = getOrganigrammeService()
            .getOrganigrammeNodeById(identifiant, OrganigrammeType.UNITE_STRUCTURELLE);
        if (uniteStructurelleNode == null) {
            // cas impossible à l'heure actuel -> si un jour une différence existe entre Direction et US le code sera
            // existant
            uniteStructurelleNode =
                getOrganigrammeService().getOrganigrammeNodeById(identifiant, OrganigrammeType.DIRECTION);
        }

        lockOrganigrammeNode(context, uniteStructurelleNode);

        return convertUniteStructurelleNodeToUniteStructurelleForm(uniteStructurelleNode);
    }

    @Override
    public void createUniteStructurelle(SpecificContext context) {
        UniteStructurelleForm uniteStructurelleForm = context.getFromContextData(UNITE_STRUCTURELLE_FORM);

        UniteStructurelleNode uniteStructurelleNode;
        // convertir l'objet UniteStructurelleForm -> UniteStructurelleNode
        uniteStructurelleNode = convertUniteStructurelleFormToUniteStructurelleNode(uniteStructurelleForm);
        if (
            CollectionUtils.isEmpty(uniteStructurelleNode.getEntiteParentList()) &&
            CollectionUtils.isEmpty(uniteStructurelleNode.getInstitutionParentList()) &&
            CollectionUtils.isEmpty(uniteStructurelleNode.getUniteStructurelleParentList())
        ) {
            // Message
            context.getMessageQueue().addErrorToQueue("organigramme.error.uniteStructurelle.sans.parent");
        } else {
            // Créé la nouvelle Unite Structurelle
            if (getOrganigrammeService().checkUniqueLabel(uniteStructurelleNode)) {
                getSTUsAndDirectionService().createUniteStructurelle(uniteStructurelleNode);
                context.getMessageQueue().addSuccessToQueue("organigramme.succes.create.uniteStructurelle");
            } else {
                // Message erreur
                context.getMessageQueue().addErrorToQueue("organigramme.error.create.element.same.name");
            }
        }
    }

    private boolean hasError(UniteStructurelleNode usNode, SpecificContext context) {
        boolean error = false;
        if (
            CollectionUtils.isEmpty(usNode.getEntiteParentList()) &&
            CollectionUtils.isEmpty(usNode.getInstitutionParentList()) &&
            CollectionUtils.isEmpty(usNode.getUniteStructurelleParentList())
        ) {
            // Message
            context.getMessageQueue().addErrorToQueue("organigramme.error.uniteStructurelle.sans.parent");
            error = true;
        } else if (!getOrganigrammeService().checkUniqueLabel(usNode)) {
            context.getMessageQueue().addErrorToQueue("organigramme.error.create.element.same.name");
            error = true;
        } else if (usNode.getParentUnitIds().stream().anyMatch(unitParentId -> unitParentId.equals(usNode.getId()))) {
            context.getMessageQueue().addErrorToQueue("organigramme.error.create.element.parentself");
            error = true;
        }

        return error;
    }

    @Override
    public void updateUniteStructurelle(SpecificContext context) {
        UniteStructurelleForm uniteStructurelleForm = context.getFromContextData(UNITE_STRUCTURELLE_FORM);
        String identifiant = uniteStructurelleForm.getIdentifiant();
        UniteStructurelleNode uniteStructurelleNode = null;

        if (OrganigrammeType.UNITE_STRUCTURELLE.toString().equals(uniteStructurelleForm.getType())) {
            uniteStructurelleNode =
                getOrganigrammeService().getOrganigrammeNodeById(identifiant, OrganigrammeType.UNITE_STRUCTURELLE);
        } else {
            uniteStructurelleNode =
                getOrganigrammeService().getOrganigrammeNodeById(identifiant, OrganigrammeType.DIRECTION);
        }

        try {
            updateUniteStructurelleNode(uniteStructurelleNode, uniteStructurelleForm);

            // Update l'Unite Structurelle
            if (!hasError(uniteStructurelleNode, context)) {
                performNodeUpdate(
                    context,
                    uniteStructurelleNode,
                    getSTUsAndDirectionService()::updateUniteStructurelle,
                    "organigramme.succes.update.uniteStructurelle"
                );
            }
        } catch (Exception e) {
            throw new NuxeoException(
                "Erreur lors de la mise à jour de l'unité structurelle : " + uniteStructurelleNode.getLabel(),
                e
            );
        }
    }

    /**
     * Converti le SwBean UniteStructurelleForm en UniteStructurelleNode
     *
     * @param form
     *            bean (front)
     * @return
     */
    public static UniteStructurelleNode convertUniteStructurelleFormToUniteStructurelleNode(
        UniteStructurelleForm form
    ) {
        UniteStructurelleNodeImpl uniteStructurelleNode = new UniteStructurelleNodeImpl();
        return updateUniteStructurelleNode(uniteStructurelleNode, form);
    }

    private static <T extends OrganigrammeNode> List<T> getListNodeDetails(
        List<String> nodeIds,
        OrganigrammeType type
    ) {
        List<T> nodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(nodeIds)) {
            OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();

            nodeIds.forEach(id -> nodes.add(organigrammeService.getOrganigrammeNodeById(id, type)));
        }

        return nodes;
    }

    /**
     * Met à jour UniteStructurelleNode à partir des données du formulaire
     *
     * @param node
     *            bean to update (back)
     * @param form
     *            bean (front)
     * @return
     * @throws ParseException
     */
    public static UniteStructurelleNode updateUniteStructurelleNode(
        UniteStructurelleNode node,
        UniteStructurelleForm form
    ) {
        node.setLabel(form.getLibelle());
        node.setType(OrganigrammeType.getEnum(form.getType()));
        if (form.getDateDebut() != null) {
            node.setDateDebut(SolonDateConverter.DATE_SLASH.parseToDateOrNull(form.getDateDebut()));
        }
        if (form.getDateFin() != null) {
            node.setDateFin(SolonDateConverter.DATE_SLASH.parseToDateOrNull(form.getDateFin()));
        }

        // Gestion des ministères de rattachement
        node.setParentList(getListNodeDetails(form.getMinisteresRatachement(), OrganigrammeType.MINISTERE));
        node.setParentEntiteIds(form.getMinisteresRatachement());

        node.setInstitutionParentList(getListNodeDetails(form.getInstitutions(), OrganigrammeType.INSTITUTION));
        node.setParentInstitIds(form.getInstitutions());

        // Gestion des unités structurelles de rattachement
        node.setUniteStructurelleParentList(
            getListNodeDetails(form.getUnitesStructurellesRattachement(), OrganigrammeType.UNITE_STRUCTURELLE)
        );
        node.setParentUnitIds(form.getUnitesStructurellesRattachement());

        if (CollectionUtils.isNotEmpty(form.getKeyNors())) {
            node.setNorDirectionList(
                form
                    .getKeyNors()
                    .stream()
                    .map(keyNor -> new NorDirection(keyNor.getId(), keyNor.getNor()))
                    .collect(Collectors.toCollection(ArrayList::new))
            );
        }

        return node;
    }

    /**
     * Converti une UniteStructurelleNode en UniteStructurelleForm
     *
     * @param node
     *            bean (back) to convert
     * @return
     */
    public static UniteStructurelleForm convertUniteStructurelleNodeToUniteStructurelleForm(
        UniteStructurelleNode node
    ) {
        UniteStructurelleForm form = new UniteStructurelleForm();

        if (node != null) {
            form.setIdentifiant(node.getId());
            form.setType(node.getTypeValue());
            form.setLibelle(node.getLabel());
            if (node.getDateDebut() != null) {
                form.setDateDebut(SolonDateConverter.DATE_SLASH.format(node.getDateDebut()));
            }
            if (node.getDateFin() != null) {
                form.setDateFin(SolonDateConverter.DATE_SLASH.format(node.getDateFin()));
            }

            // Gestion des ministères de rattachement
            HashMap<String, String> mapMinistere = new HashMap<>();
            ArrayList<String> lstMinisteres = new ArrayList<>();
            for (EntiteNode ministere : node.getEntiteParentList()) {
                mapMinistere.put(ministere.getId(), ministere.getLabel());
                lstMinisteres.add(ministere.getId());
            }
            form.setMapMinisteresRatachement(mapMinistere);
            form.setMinisteresRatachement(lstMinisteres);

            HashMap<String, String> mapInstit = new HashMap<>();
            ArrayList<String> lstInstit = new ArrayList<>();
            for (InstitutionNode instit : node.getInstitutionParentList()) {
                mapInstit.put(instit.getId(), instit.getLabel());
                lstInstit.add(instit.getId());
            }
            form.setMapInstitution(mapInstit);
            form.setInstitutions(lstInstit);

            // Gestion des unités structurelles de rattachement
            HashMap<String, String> mapUnites = new HashMap<>();
            ArrayList<String> lstUnites = new ArrayList<>();
            for (UniteStructurelleNode uniteStructurelleNode : node.getUniteStructurelleParentList()) {
                mapUnites.put(uniteStructurelleNode.getId(), uniteStructurelleNode.getLabel());
                lstUnites.add(uniteStructurelleNode.getId());
            }
            form.setMapUnitesStructurellesRattachement(mapUnites);
            form.setUnitesStructurellesRattachement(lstUnites);

            if (CollectionUtils.isNotEmpty(node.getNorDirectionList())) {
                form.setKeyNors(
                    node
                        .getNorDirectionList()
                        .stream()
                        .map(norDirection -> new EntiteNorDirection(norDirection.getId(), norDirection.getNor()))
                        .collect(Collectors.toCollection(ArrayList::new))
                );
            }
        }

        return form;
    }
}
