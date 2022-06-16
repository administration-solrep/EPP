package fr.dila.epp.ui.services.impl;

import fr.dila.epp.ui.services.EPPOrganigrammeManagerService;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.bean.TreeElementDTO;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.impl.AbstractOrganigrammeManagerService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;

public class EPPOrganigrammeManagerServiceImpl
    extends AbstractOrganigrammeManagerService
    implements EPPOrganigrammeManagerService {
    private static final String ALLOW_UPDATE_ORGANIGRAMME_KEY = "allowUpdateOrganigramme";

    @Override
    public List<OrganigrammeElementDTO> getOrganigramme(boolean loadActions, SpecificContext context) {
        OrganigrammeTreeUIService service = STUIServiceLocator.getOrganigrammeTreeService();
        List<OrganigrammeElementDTO> organigramme = service.getOrganigramme(context);

        if (loadActions) {
            // Charger les données additionnelles aux éléments de l'organigramme
            for (OrganigrammeElementDTO element : organigramme) {
                loadAdditionalData(element, loadActions, context);
                // On force les premiers éléments en mode parent
                element.setIsLastLevel(false);
                if (element.getChilds() == null) {
                    element.setChilds(new ArrayList<>());
                }
            }
        }

        return organigramme;
    }

    private boolean isUserAllowedToEditOrganigramme(CoreSession session, OrganigrammeNode curElem) {
        EppPrincipal eppPrincipal = (EppPrincipal) session.getPrincipal();
        List<OrganigrammeNode> lstParents = SolonEppServiceLocator.getOrganigrammeService().getFullParentList(curElem);

        List<String> lstInstitParentIds;

        if (lstParents != null) {
            lstInstitParentIds =
                lstParents
                    .stream()
                    .filter(org -> org.getType() == OrganigrammeType.INSTITUTION)
                    .map(OrganigrammeNode::getId)
                    .collect(Collectors.toList());
        } else {
            lstInstitParentIds = new ArrayList<>();
            if (curElem instanceof InstitutionNode) {
                lstInstitParentIds.add(curElem.getId());
            }
        }

        return (
            eppPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_UPDATER) &&
            CollectionUtils.containsAny(eppPrincipal.getInstitutionIdSet(), lstInstitParentIds)
        );
    }

    /**
     * Renseigne les données additionnelles à un élément de l'organigramme et ses fils
     *
     * @param loadActions
     */
    private void loadAdditionalData(OrganigrammeElementDTO element, boolean loadActions, SpecificContext context) {
        OrganigrammeNode organigrammeNode = element.getOrganigrammeNode();
        CoreSession session = context.getSession();

        if (loadActions && organigrammeNode != null) {
            element.setActions(loadActions(element, context));
        }

        if (organigrammeNode instanceof EntiteNode) {
            element.setAllowAdd(allowAddEntite((EppPrincipal) session.getPrincipal(), element.getMinistereId()));
        } else if (organigrammeNode instanceof PosteNode) {
            element.setAllowAdd(allowAddPoste((EppPrincipal) session.getPrincipal(), element.getMinistereId()));
        }

        for (TreeElementDTO child : element.getChilds()) {
            loadAdditionalData((OrganigrammeElementDTO) child, loadActions, context);
        }
    }

    private List<Action> loadActions(OrganigrammeElementDTO node, SpecificContext context) {
        setActionData(node, context);
        return context.getActions(STActionCategory.ORGANIGRAMME_ACTIONS);
    }

    private void setActionData(OrganigrammeElementDTO node, SpecificContext context) {
        context.putInContextData(STContextDataKey.ORGANIGRAMME_NODE, node);
        context.putInContextData(
            ALLOW_UPDATE_ORGANIGRAMME_KEY,
            isUserAllowedToEditOrganigramme(context.getSession(), node.getOrganigrammeNode())
        );
        context.putInContextData("isPosteWS", isPosteWebservice(node));
        context.putInContextData("isPoste", isPoste(node));
    }

    @Override
    public void computeOrganigrammeActions(SpecificContext context) {
        OrganigrammeElementDTO node = context.getFromContextData(STContextDataKey.ORGANIGRAMME_NODE);

        if (node.getOrganigrammeNode().getType() == OrganigrammeType.POSTE) {
            PosteNode poste = (PosteNode) node.getOrganigrammeNode();
            String parentId =
                (
                    CollectionUtils.isNotEmpty(poste.getParentInstitIds())
                        ? poste.getParentInstitIds().get(0)
                        : poste.getParentUniteId()
                );

            OrganigrammeNode parent = STUIServiceLocator
                .getOrganigrammeTreeService()
                .findNodeHavingIdAndChildType(parentId, OrganigrammeType.POSTE);
            node.setParent(new OrganigrammeElementDTO(context.getSession(), parent));
        }

        setActionData(node, context);
    }

    @Override
    protected List<SuggestionDTO> getSuggestionsFromDirectoryNames(
        CoreSession session,
        String input,
        List<String> selectionType,
        boolean activatePosteFilter
    ) {
        List<OrganigrammeType> directoryNames = new ArrayList<>();

        if (selectionType.contains(OrganigrammeType.POSTE.getValue()) || selectionType.contains(MAILBOX_TYPE)) {
            directoryNames.add(OrganigrammeType.POSTE);
        }
        if (selectionType.contains(OrganigrammeType.INSTITUTION.getValue())) {
            directoryNames.add(OrganigrammeType.INSTITUTION);
        }
        if (
            selectionType.contains(OrganigrammeType.DIRECTION.getValue()) ||
            selectionType.contains(OrganigrammeType.UNITE_STRUCTURELLE.getValue())
        ) {
            directoryNames.add(OrganigrammeType.UNITE_STRUCTURELLE);
        }

        List<SuggestionDTO> suggestions = new ArrayList<>();
        if (!directoryNames.isEmpty()) {
            suggestions = getOrganigrammeSuggestions(session, input, directoryNames, activatePosteFilter);
        }

        return suggestions;
    }

    private List<SuggestionDTO> getOrganigrammeSuggestions(
        CoreSession session,
        String input,
        List<OrganigrammeType> directoryNames,
        boolean activatePosteFilter
    ) {
        List<OrganigrammeNode> results = STServiceLocator
            .getOrganigrammeService()
            .getOrganigrameLikeLabels(input, directoryNames);

        OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();

        if (directoryNames.contains(OrganigrammeType.POSTE) && activatePosteFilter) {
            return results
                .stream()
                .filter(
                    node ->
                        organigrammeService
                            .getInstitutionParentFromPoste(node.getId())
                            .stream()
                            .anyMatch(inst -> allowAddEntite((EppPrincipal) session.getPrincipal(), inst.getId()))
                )
                .map(node -> new SuggestionDTO(node.getId(), node.getLabel()))
                .collect(Collectors.toList());
        } else {
            return results
                .stream()
                .filter(node -> !node.getDeleted() && node.isActive())
                .map(node -> new SuggestionDTO(node.getId(), node.getLabel()))
                .collect(Collectors.toList());
        }
    }

    /**
     * Test si l'utilisateur peut ajouter un poste en fonction de son ministère
     *
     * @param principal
     * @param ministereId
     * @return
     */
    protected boolean allowAddEntite(EppPrincipal principal, String entiteId) {
        return (
            principal != null &&
            (
                principal.isMemberOf(STBaseFunctionConstant.ALLOW_ADD_POSTE_ALL_MINISTERE) ||
                isUserInInstit(principal, entiteId)
            )
        );
    }

    /**
     * Test si l'utilisateur peut ajouter un poste en fonction de son ministère
     *
     * @param ssPrincipal
     * @param ministereId
     * @return
     */
    protected boolean allowAddPoste(EppPrincipal ssPrincipal, String ministereId) {
        return (
            ssPrincipal != null &&
            (
                ssPrincipal.isMemberOf(STBaseFunctionConstant.ALLOW_ADD_POSTE_ALL_MINISTERE) ||
                isUserInInstit(ssPrincipal, ministereId)
            )
        );
    }

    protected boolean isUserInInstit(EppPrincipal eppPrincipal, String ministereId) {
        return eppPrincipal.getInstitutionIdSet() != null && eppPrincipal.getInstitutionIdSet().contains(ministereId);
    }

    private boolean isPosteWebservice(OrganigrammeElementDTO node) {
        return (
            OrganigrammeType.POSTE.getValue().equals(node.getType()) &&
            OrganigrammeType.INSTITUTION.getValue().equals(node.getParent().getType())
        );
    }

    private boolean isPoste(OrganigrammeElementDTO node) {
        return (
            OrganigrammeType.POSTE.getValue().equals(node.getType()) &&
            (
                OrganigrammeType.UNITE_STRUCTURELLE.getValue().equals(node.getParent().getType()) ||
                OrganigrammeType.DIRECTION.getValue().equals(node.getParent().getType())
            )
        );
    }
}
