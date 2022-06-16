package fr.dila.st.ui.th.bean.mapper;

import static fr.dila.st.api.organigramme.OrganigrammeType.MINISTERE;
import static java.util.stream.Collectors.toList;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.th.bean.PosteWsForm;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public final class MapFormToNode {

    private MapFormToNode() {}

    /**
     * Met à jour PosteNode à partir des données du formulaire PosteWsForm
     *
     * @param node
     *            bean to update (back)
     * @param posteWsForm
     *            bean (front)
     * @return
     * @throws ParseException
     */
    public static void posteWsFormToPosteNode(PosteNode node, PosteWsForm posteWsForm) throws ParseException {
        node.setPosteWs(true);
        node.setLabel(posteWsForm.getLibelle());
        node.setWsUrl(posteWsForm.getUrlWs());
        node.setWsUser(posteWsForm.getUtilisateurWs());
        node.setWsPassword(posteWsForm.getMdpWs());
        node.setWsKeyAlias(posteWsForm.getKeystore());
        if (posteWsForm.getDateDebut() != null) {
            node.setDateDebut(SolonDateConverter.DATE_SLASH.parseToDateOrNull(posteWsForm.getDateDebut()));
        }
        List<String> ministeres = posteWsForm
            .getMinisteres()
            .stream()
            .filter(StringUtils::isNotBlank)
            .collect(toList());
        if (CollectionUtils.isNotEmpty(ministeres)) {
            OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
            List<EntiteNode> entites = ministeres
                .stream()
                .map(m -> (EntiteNode) organigrammeService.getOrganigrammeNodeById(m, MINISTERE))
                .filter(Objects::nonNull)
                .collect(toList());
            node.setEntiteParentList(entites);
            node.setParentEntiteIds(entites.stream().map(EntiteNode::getId).collect(toList()));
        }

        List<String> instit = posteWsForm.getInstitutions().stream().filter(StringUtils::isNotBlank).collect(toList());
        if (CollectionUtils.isNotEmpty(instit)) {
            OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
            List<InstitutionNode> institutions = instit
                .stream()
                .map(
                    m -> (InstitutionNode) organigrammeService.getOrganigrammeNodeById(m, OrganigrammeType.INSTITUTION)
                )
                .filter(Objects::nonNull)
                .collect(toList());
            node.setInstitutionParentList(institutions);
            node.setParentInstitIds(institutions.stream().map(InstitutionNode::getId).collect(toList()));
        }
    }

    /**
     * Converti un PosteNode en PosteWsForm
     *
     * @param node
     *            bean (back) to convert
     * @return
     */
    public static void convertPosteNodeToPosteWsForm(PosteNode node, PosteWsForm posteWsForm) {
        posteWsForm.setId(node.getId());
        posteWsForm.setLibelle(node.getLabel());
        posteWsForm.setUrlWs(node.getWsUrl());
        posteWsForm.setUtilisateurWs(node.getWsUser());
        posteWsForm.setMdpWs(node.getWsPassword());
        posteWsForm.setKeystore(node.getWsKeyAlias());
        if (Objects.nonNull(node.getDateDebut())) {
            posteWsForm.setDateDebut(SolonDateConverter.DATE_SLASH.format(node.getDateDebut()));
        }
        List<EntiteNode> parents = node.getEntiteParentList();
        if (CollectionUtils.isNotEmpty(parents)) {
            HashMap<String, String> mapMin = new HashMap<>(
                parents.stream().collect(Collectors.toMap(EntiteNode::getId, EntiteNode::getLabel))
            );
            posteWsForm.setMapMinisteres(mapMin);
            posteWsForm.setMinisteres(new ArrayList<>(mapMin.keySet()));
        }

        List<InstitutionNode> instits = node.getInstitutionParentList();
        if (CollectionUtils.isNotEmpty(instits)) {
            HashMap<String, String> mapInst = new HashMap<>(
                instits.stream().collect(Collectors.toMap(InstitutionNode::getId, InstitutionNode::getLabel))
            );
            posteWsForm.setMapInstitution(mapInst);
            posteWsForm.setInstitutions(new ArrayList<>(mapInst.keySet()));
        }
    }
}
