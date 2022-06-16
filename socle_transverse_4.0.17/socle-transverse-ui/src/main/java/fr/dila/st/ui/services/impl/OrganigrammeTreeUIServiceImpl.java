package fr.dila.st.ui.services.impl;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.organigramme.AlphabeticalAndBdcOrderComparator;
import fr.dila.st.core.organigramme.UserNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.organigramme.NodeDeletionProblemsExcelGenerator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.WebEngine;

public class OrganigrammeTreeUIServiceImpl implements OrganigrammeTreeUIService {
    private static final STLogger LOG = STLogFactory.getLog(OrganigrammeTreeUIServiceImpl.class);
    public static final String SELECTED_NODE_FOR_COPY_KEY = "selectedNodeForCopy";
    public static final String IS_NODE_SELECTED_FOR_COPY_KEY = "isNodeSelectedForCopy";
    public static final String ORGANIGRAMME_NODE_ID_KEY = "nodeId";

    public static final String SHOW_DEACTIVATED_KEY = "showDeactivated";
    public static final String ACTIVATE_POSTE_FILTER_KEY = "activatePosteFilter";

    public static final String ORGANIGRAMME_TREE_KEY = "organigrammeTree";
    public static final String SELECTED_NODE_KEY = "selectedNode";

    public static final String ORGANIGRAMME_TYPE_KEY = "orgType";

    /** Pour récupérer l'id du set de noeuds ouverts */
    public static final String OPEN_NODES_ID_KEY = "openNodesId";

    /** Identifiant du set de noeuds ouverts de l'organigramme de l'adminisatration */
    public static final String ORGANIGRAMME_TREE_OPEN_NODES_KEY = "organigrammeTree_openNodes";

    @SuppressWarnings("unchecked")
    @Override
    public List<OrganigrammeElementDTO> getOrganigramme(SpecificContext context) {
        boolean showDeactivated = BooleanUtils.toBooleanDefaultIfNull(
            context.getFromContextData(STContextDataKey.SHOW_DEACTIVATED),
            false
        );

        UserSessionHelper.putUserSessionParameter(context, SHOW_DEACTIVATED_KEY, showDeactivated);

        String openNodesId = context.getFromContextData(OPEN_NODES_ID_KEY);
        Set<String> openNodes = null;
        if (openNodesId != null) {
            openNodes = UserSessionHelper.getUserSessionParameter(context, openNodesId, Set.class);
        }
        if (openNodes == null) {
            // Si aucun set de noeuds ouverts n'a été trouvé, ouvrir le gouvernement courant
            openNodes =
                STServiceLocator
                    .getOrganigrammeService()
                    .getRootNodes()
                    .stream()
                    .map(OrganigrammeNode::getId)
                    .collect(Collectors.toSet());

            UserSessionHelper.putUserSessionParameter(context, openNodesId, openNodes);
        }
        return loadTree(context.getSession(), showDeactivated, openNodes);
    }

    /**
     * Charge l'arbre contenant l'organigramme
     *
     * @param showDeactivedNode
     * @param loadActions
     *            chargement des actions ?
     * @param openNodes
     * @return
     */
    protected List<OrganigrammeElementDTO> loadTree(
        CoreSession session,
        boolean showDeactivedNode,
        Set<String> openNodes
    ) {
        List<OrganigrammeElementDTO> rootNodes = new ArrayList<>();

        List<? extends OrganigrammeNode> baseGroups = STServiceLocator.getOrganigrammeService().getRootNodes();

        Date today = new Date();
        String ministereId = null;

        for (OrganigrammeNode groupNode : baseGroups) {
            if (groupNode.getDateFin() == null || today.compareTo(groupNode.getDateFin()) <= 0 || showDeactivedNode) {
                ministereId = groupNode.getId();
                OrganigrammeElementDTO treeNode = new OrganigrammeElementDTO(session, groupNode, ministereId);

                addSubGroups(session, treeNode, showDeactivedNode, openNodes);

                rootNodes.add(treeNode);
            }
        }

        return rootNodes;
    }

    /**
     * Ajoute les sous-groupes d'un noeud
     *
     * @param treeNode
     * @param showDeactivedNode
     * @param openNodes
     */
    protected void addSubGroups(
        CoreSession session,
        OrganigrammeElementDTO treeNode,
        boolean showDeactivedNode,
        Set<String> openNodes
    ) {
        List<OrganigrammeNode> subGroups = null;

        // Efface les enfants existant
        treeNode.getChilds().clear();

        OrganigrammeNode group = treeNode.getOrganigrammeNode();

        if (group != null) {
            if (openNodes.contains(treeNode.getCompleteKey())) {
                treeNode.setIsOpen(true);
                subGroups =
                    STServiceLocator.getOrganigrammeService().getChildrenList(session, group, showDeactivedNode);
            } else {
                OrganigrammeNode node = STServiceLocator
                    .getOrganigrammeService()
                    .getFirstChild(group, session, showDeactivedNode);
                treeNode.setIsLastLevel(node == null);
            }

            if (subGroups != null && !subGroups.isEmpty()) {
                // Tri sur la liste pour classer les Unites Structurelles et
                // postes par ordre alphabetique et suivant la possession d'un bdc
                Collections.sort(subGroups, new AlphabeticalAndBdcOrderComparator());

                List<OrganigrammeElementDTO> children = new ArrayList<>();
                for (OrganigrammeNode childGroup : subGroups) {
                    String ministereId = treeNode.getMinistereId();
                    if (childGroup instanceof EntiteNode) {
                        ministereId = childGroup.getId();
                    }

                    OrganigrammeElementDTO child = createOrganigrammeElementDTO(
                        session,
                        childGroup,
                        ministereId,
                        treeNode
                    );

                    addSubGroups(session, child, showDeactivedNode, openNodes);

                    children.add(child);
                }
                treeNode.setChilds(children);
            }

            if (group instanceof PosteNode) {
                addUsers(treeNode, showDeactivedNode);
                treeNode.setIsLastLevel(treeNode.getChilds().isEmpty());
            }
        }
    }

    // Méthode surchargé dans EPG
    protected OrganigrammeElementDTO createOrganigrammeElementDTO(
        CoreSession session,
        OrganigrammeNode childGroup,
        String ministereId,
        OrganigrammeElementDTO treeNode
    ) {
        return new OrganigrammeElementDTO(session, childGroup, ministereId, treeNode);
    }

    @Override
    public OrganigrammeNode findNodeHavingIdAndChildType(String idParent, OrganigrammeType curType) {
        List<OrganigrammeType> lstPotentialParentType = curType.getLstPotentialParent();
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();

        return lstPotentialParentType
            .stream()
            .map(parentType -> organigrammeService.getOrganigrammeNodeById(idParent, parentType))
            .filter(Objects::nonNull)
            .map(OrganigrammeNode.class::cast)
            .findFirst()
            .orElse(null);
    }

    /**
     * Ajoute les utilisateurs d'un noeud
     *
     * @param treeNode
     * @param showDeactivedNode
     */
    protected void addUsers(OrganigrammeElementDTO treeNode, boolean showDeactivedNode) {
        List<String> userList = null;

        OrganigrammeNode posteNode = treeNode.getOrganigrammeNode();

        userList = ((PosteNode) posteNode).getMembers();

        List<OrganigrammeElementDTO> children = new ArrayList<>();
        List<String> usersLastName = new ArrayList<>();

        final UserManager userManager = STServiceLocator.getUserManager();
        for (String userId : userList) {
            DocumentModel user = userManager.getUserModel(userId);
            if (user == null) {
                LOG.debug(
                    STLogEnumImpl.FAIL_GET_USER_TEC,
                    String.format(
                        "Utilisateur <%s> défini dans un poste, mais non existant dans la branche utilisateurs",
                        userId
                    )
                );
                continue;
            }

            STUser stUser = user.getAdapter(STUser.class);
            if (stUser.isDeleted()) {
                continue;
            }
            if (stUser.isActive() || showDeactivedNode) {
                UserNode userNode = new UserNodeImpl();
                String lastName = " ";

                if (!StringUtils.isEmpty(stUser.getLastName())) {
                    lastName = stUser.getLastName();
                    usersLastName.add(lastName);
                }

                Collections.sort(usersLastName);

                userNode.setActive(stUser.isActive());
                userNode.setLabel(stUser.getFullName());
                userNode.setId(userId);
                if (usersLastName.indexOf(lastName) >= 0) {
                    children.add(usersLastName.indexOf(lastName), new OrganigrammeElementDTO(userNode));
                } else {
                    LOG.warn(
                        STLogEnumImpl.FAIL_GET_USER_TEC,
                        String.format(
                            "L'utilisateur %s a des données corrompues, veuillez le modifier/supprimmer.",
                            stUser.getUsername()
                        )
                    );
                }
            }
        }
        treeNode.setChilds(children);
    }

    @Override
    public void setActiveState(SpecificContext context) {
        String sID = context.getFromContextData(ORGANIGRAMME_NODE_ID_KEY);
        OrganigrammeType type = context.getFromContextData(ORGANIGRAMME_TYPE_KEY);

        OrganigrammeService orgService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode orgNode = orgService.getOrganigrammeNodeById(sID, type);
        if (!orgNode.isActive()) {
            orgService.enableNodeFromDn(context.getSession(), sID, type);
            switch (orgNode.getType()) {
                case UNITE_STRUCTURELLE:
                    context
                        .getMessageQueue()
                        .addSuccessToQueue(ResourceHelper.getString("organigramme.succes.uniteStructurelle.activate"));
                    break;
                case MINISTERE:
                    context
                        .getMessageQueue()
                        .addSuccessToQueue(ResourceHelper.getString("organigramme.succes.ministere.activate"));
                    break;
                case DIRECTION:
                    context
                        .getMessageQueue()
                        .addSuccessToQueue(ResourceHelper.getString("organigramme.succes.direction.activate"));
                    break;
                default:
                    context
                        .getMessageQueue()
                        .addSuccessToQueue(ResourceHelper.getString("organigramme.succes.poste.activate"));
                    break;
            }

            // On recharge notre objet pour la mise à jour
            orgNode = orgService.getOrganigrammeNodeById(sID, type);
        } else {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("organigramme.error.node.activation", type, sID));
        }
        if (!orgNode.isActive()) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("organigramme.error.node.fdr"));
        }
    }

    @Override
    public void setInactiveState(SpecificContext context) {
        String sID = context.getFromContextData(ORGANIGRAMME_NODE_ID_KEY);
        OrganigrammeType type = context.getFromContextData(ORGANIGRAMME_TYPE_KEY);

        OrganigrammeService orgService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode orgNode = orgService.getOrganigrammeNodeById(sID, type);
        if (orgNode.isActive()) {
            try {
                orgService.disableNodeFromDn(context.getSession(), sID, type);
                switch (orgNode.getType()) {
                    case UNITE_STRUCTURELLE:
                        context
                            .getMessageQueue()
                            .addSuccessToQueue(
                                ResourceHelper.getString("organigramme.succes.uniteStructurelle.deactivate")
                            );
                        break;
                    case MINISTERE:
                        context
                            .getMessageQueue()
                            .addSuccessToQueue(ResourceHelper.getString("organigramme.succes.ministere.deactivate"));
                        break;
                    case DIRECTION:
                        context
                            .getMessageQueue()
                            .addSuccessToQueue(ResourceHelper.getString("organigramme.succes.direction.deactivate"));
                        break;
                    default:
                        context
                            .getMessageQueue()
                            .addSuccessToQueue(ResourceHelper.getString("organigramme.succes.poste.deactivate"));
                        break;
                }
            } catch (NuxeoException e) {
                LOG.error(context.getSession(), STLogEnumImpl.FAIL_UPDATE_POSTE_TEC, e);
                // Exception durant la désactivation => on met un message
                context.getMessageQueue().addErrorToQueue(e.getLocalizedMessage());
            }
        } else {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("organigramme.error.node.deactivation", type, sID));
        }
    }

    @Override
    public String deleteNode(OrganigrammeType type, String id, SpecificContext context) {
        // Récupération de l'organigrammeNode à partir des informations

        OrganigrammeService orgService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode node = orgService.getOrganigrammeNodeById(id, type);
        if (node == null) {
            // Noeud non trouvé !
            throw new IllegalArgumentException("Noeud non trouvé: " + type + " " + id);
        }

        Object obj = STServiceLocator.getOrganigrammeService().deleteFromDn(context.getSession(), node, true);
        if (obj instanceof NodeDeletionProblemsExcelGenerator) {
            // Il y a au moins une erreur, on peut générer un fichier Excel contenant le
            // rapport
            File file = ((NodeDeletionProblemsExcelGenerator) obj).saveToDisk();
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("organigramme.error.node.delete"));
            return file.getName();
        } else {
            // Aucune erreur => le noeud a pu être supprimé !
            context.getMessageQueue().addSuccessToQueue(ResourceHelper.getString("organigramme.success.node.delete"));
            return null;
        }
    }

    @Override
    public void copyNode(SpecificContext context, String itemId, String type) {
        OrganigrammeService orgService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode selectedNodeModel = orgService.getOrganigrammeNodeById(itemId, OrganigrammeType.getEnum(type));
        if (selectedNodeModel != null) {
            WebEngine.getActiveContext().getUserSession().put(SELECTED_NODE_FOR_COPY_KEY, selectedNodeModel);
            context
                .getMessageQueue()
                .addInfoToQueue(
                    ResourceHelper.getString("organigramme.succes.node.copy", selectedNodeModel.getLabel())
                );
        } else {
            context.getMessageQueue().addWarnToQueue(ResourceHelper.getString("organigramme.error.node.copy"));
        }
    }

    @Override
    public void pasteNodeWithoutUser(SpecificContext context, String itemId, String type) {
        OrganigrammeService orgService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode selectedNodeModel = orgService.getOrganigrammeNodeById(itemId, OrganigrammeType.getEnum(type));
        OrganigrammeNode selectedNodeForCopy = UserSessionHelper.getUserSessionParameter(
            context,
            SELECTED_NODE_FOR_COPY_KEY,
            OrganigrammeNode.class
        );
        try {
            STServiceLocator
                .getOrganigrammeService()
                .copyNodeWithoutUser(context.getSession(), selectedNodeForCopy, selectedNodeModel);
            context
                .getMessageQueue()
                .addInfoToQueue(
                    ResourceHelper.getString(
                        "organigramme.succes.node.duplication.withoutuser",
                        selectedNodeForCopy.getLabel()
                    )
                );
        } catch (NuxeoException e) {
            context.getMessageQueue().addWarnToQueue(e.getMessage());
            LOG.debug(context.getSession(), STLogEnumImpl.FAIL_UPDATE_POSTE_TEC, selectedNodeForCopy, e);
        } catch (Exception e) {
            context.getMessageQueue().addWarnToQueue(ResourceHelper.getString("organigramme.warn.node.duplication"));
            LOG.error(context.getSession(), STLogEnumImpl.FAIL_UPDATE_POSTE_TEC, selectedNodeForCopy, e);
        }
        resetNodeModel();
    }

    @Override
    public void pasteNodeWithUsers(SpecificContext context, String itemId, String type) {
        OrganigrammeService orgService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode selectedNodeModel = orgService.getOrganigrammeNodeById(itemId, OrganigrammeType.getEnum(type));
        OrganigrammeNode selectedNodeForCopy = (OrganigrammeNode) WebEngine
            .getActiveContext()
            .getUserSession()
            .get(SELECTED_NODE_FOR_COPY_KEY);
        try {
            STServiceLocator
                .getOrganigrammeService()
                .copyNodeWithUsers(context.getSession(), selectedNodeForCopy, selectedNodeModel);
            context
                .getMessageQueue()
                .addInfoToQueue(
                    ResourceHelper.getString(
                        "organigramme.succes.node.duplication.withuser",
                        selectedNodeForCopy.getLabel()
                    )
                );
        } catch (NuxeoException e) {
            context.getMessageQueue().addWarnToQueue(e.getMessage());
            LOG.debug(context.getSession(), STLogEnumImpl.FAIL_UPDATE_POSTE_TEC, selectedNodeForCopy, e);
        } catch (Exception e) {
            context.getMessageQueue().addWarnToQueue(ResourceHelper.getString("organigramme.warn.node.duplication"));
            LOG.error(context.getSession(), STLogEnumImpl.FAIL_UPDATE_POSTE_TEC, selectedNodeForCopy, e);
        }
        resetNodeModel();
    }

    /**
     * Met a null les documents en cours de création ou d'édition
     */
    private void resetNodeModel() {
        WebEngine.getActiveContext().getUserSession().remove(SELECTED_NODE_FOR_COPY_KEY);
    }
}
