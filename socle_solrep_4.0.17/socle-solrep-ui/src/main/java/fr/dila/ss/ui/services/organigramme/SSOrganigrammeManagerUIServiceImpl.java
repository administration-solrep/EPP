package fr.dila.ss.ui.services.organigramme;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.organigramme.ProtocolarOrderComparator;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * ActionBean de gestion de l'organigramme
 *
 * @author FEO
 */
public class SSOrganigrammeManagerUIServiceImpl implements SSOrganigrammeManagerUIService {
    public static final String ORGANIGRAMME_VIEW = "view_organigramme";
    public static final String ORGANIGRAMME_SCHEMA = "organigramme";
    public static final String MULTI = "MULTI";
    public static final String LIST = "LIST";
    public static final String SINGLE = "SINGLE";
    public static final String USER_TYPE = "USER_TYPE";
    public static final String MIN_TYPE = "MIN_TYPE";
    public static final String UST_TYPE = "UST_TYPE";
    public static final String GVT_TYPE = "GVT_TYPE";
    public static final String DIR_TYPE = "DIR_TYPE";
    public static final String USER_MAIL_TYPE = "USER_MAIL_TYPE";
    public static final String POSTE_TYPE = "POSTE_TYPE";
    public static final String DIR_AND_UST_TYPE = "DIR_AND_UST_TYPE";
    public static final String MAILBOX_TYPE = "MAILBOX_TYPE";
    public static final String NEW_TIMBRE_EMPTY_VALUE = "empty_value";
    public static final String NEW_TIMBRE_UNCHANGED_ENTITY = "unchanged_entity";
    public static final String NEW_TIMBRE_DEACTIVATE_ENTITY = "deactivate_entity";
    public static final String PREFIXED_ID_TYPE = "PREFIXED";

    /**
     * Logger.
     */
    private static final STLogger LOG = STLogFactory.getLog(SSOrganigrammeManagerUIServiceImpl.class);

    /**
     * Default constructor
     */
    public SSOrganigrammeManagerUIServiceImpl() {
        // do nothing
    }

    public String getMailboxIdFromPosteId(String posteId) {
        if (posteId != null) {
            return SSServiceLocator.getMailboxPosteService().getPosteMailboxId(posteId);
        }
        return null;
    }

    public Boolean contains(String selectionType, String type) {
        if (selectionType != null && type != null) {
            return selectionType.contains(type);
        }
        return false;
    }

    public String getOrganigrammeNodeLabel(String selectionType, String id) {
        String node = "";
        if (MIN_TYPE.equals(selectionType)) {
            EntiteNode minist = STServiceLocator.getSTMinisteresService().getEntiteNode(id);
            if (minist != null) {
                node = minist.getLabel();
            }
        } else if (DIR_TYPE.equals(selectionType) || DIR_AND_UST_TYPE.equals(selectionType)) {
            UniteStructurelleNode unit = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(id);
            if (unit != null) {
                node = unit.getLabel();
            }
        } else if (POSTE_TYPE.equals(selectionType)) {
            PosteNode poste = STServiceLocator.getSTPostesService().getPoste(id);
            if (poste != null) {
                node = poste.getLabel();
            }
        } else if (GVT_TYPE.equals(selectionType)) {
            GouvernementNode gvt = STServiceLocator.getSTGouvernementService().getGouvernement(id);
            if (gvt != null) {
                node = gvt.getLabel();
            }
        } else if (USER_TYPE.equals(selectionType)) {
            UserNode user = STServiceLocator.getOrganigrammeService().getUserNode(id);
            if (user != null) {
                node = user.getLabel();
            }
        } else {
            throw new NuxeoException("No support for selectionType : [" + selectionType + "]");
        }
        return node;
    }

    public OrganigrammeNode getNewGouvernement() {
        return STServiceLocator.getSTGouvernementService().getBareGouvernementModel();
    }

    public OrganigrammeNode getNewEntite(String gvtNodeId) {
        OrganigrammeNode selectedNodeModel = STServiceLocator.getSTMinisteresService().getBareEntiteModel();
        EntiteNode entiteNode = (EntiteNode) selectedNodeModel;
        // ajout du parent
        GouvernementNode gouvernementNode = STServiceLocator.getSTGouvernementService().getGouvernement(gvtNodeId);
        List<OrganigrammeNode> parentList = new ArrayList<>();
        parentList.add(gouvernementNode);
        entiteNode.setParentList(parentList);
        entiteNode.setParentGouvernement(gouvernementNode.getId());
        return selectedNodeModel;
    }

    public OrganigrammeNode getNewPoste(String parentNodeId, String parentNodeType) {
        OrganigrammeNode selectedNodeModel = STServiceLocator.getSTPostesService().getBarePosteModel();
        PosteNode posteNode = (PosteNode) selectedNodeModel;

        if (OrganigrammeType.MINISTERE.equals(OrganigrammeType.getEnum(parentNodeType))) {
            List<EntiteNode> parentList = new ArrayList<>();
            EntiteNode organigrammeNode = STServiceLocator.getSTMinisteresService().getEntiteNode(parentNodeId);
            parentList.add(organigrammeNode);
            posteNode.setEntiteParentList(parentList);
            posteNode.setParentEntiteId(organigrammeNode.getId());
        } else if (
            OrganigrammeType.DIRECTION.equals(OrganigrammeType.getEnum(parentNodeType)) ||
            OrganigrammeType.UNITE_STRUCTURELLE.equals(OrganigrammeType.getEnum(parentNodeType))
        ) {
            List<UniteStructurelleNode> parentList = new ArrayList<>();
            UniteStructurelleNode organigrammeNode = STServiceLocator
                .getSTUsAndDirectionService()
                .getUniteStructurelleNode(parentNodeId);
            parentList.add(organigrammeNode);
            posteNode.setUniteStructurelleParentList(parentList);
            posteNode.setParentUniteId(organigrammeNode.getId());
        }
        return selectedNodeModel;
    }

    public UniteStructurelleNode getNewUniteStructurelle(String parentNodeId, String parentNodeType) {
        UniteStructurelleNode selectedNodeModel = STServiceLocator
            .getSTUsAndDirectionService()
            .getBareUniteStructurelleModel();
        UniteStructurelleNode ustNode = selectedNodeModel;
        OrganigrammeNode organigrammeNode;
        // ajout du parent

        if (OrganigrammeType.MINISTERE.equals(OrganigrammeType.getEnum(parentNodeType))) {
            List<EntiteNode> parentList = new ArrayList<>();
            organigrammeNode = STServiceLocator.getSTMinisteresService().getEntiteNode(parentNodeId);
            parentList.add((EntiteNode) organigrammeNode);
            ustNode.setEntiteParentList(parentList);
            ustNode.setParentEntiteId(organigrammeNode.getId());
        } else if (
            OrganigrammeType.DIRECTION.equals(OrganigrammeType.getEnum(parentNodeType)) ||
            OrganigrammeType.UNITE_STRUCTURELLE.equals(OrganigrammeType.getEnum(parentNodeType))
        ) {
            List<UniteStructurelleNode> parentList = new ArrayList<>();
            organigrammeNode = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(parentNodeId);
            parentList.add((UniteStructurelleNode) organigrammeNode);
            ustNode.setUniteStructurelleParentList(parentList);
            ustNode.setParentUniteId(organigrammeNode.getId());
        }
        return selectedNodeModel;
    }

    public boolean checkUniteStructurelleLockedByCurrentUser(SSPrincipal ssPrincipal, OrganigrammeNode nodeToUpdate) {
        final OrganigrammeNode node = STServiceLocator
            .getSTUsAndDirectionService()
            .getUniteStructurelleNode(nodeToUpdate.getId());
        return (node != null && isCurrentUserUnlocker(ssPrincipal, node.getLockUserName()));
    }

    public boolean allowUpdateOrganigramme(SSPrincipal ssPrincipal, String ministereId) {
        if (ssPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_UPDATER)) {
            return true;
        }
        if (ssPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_MINISTERE_UPDATER)) {
            return isUserInMinistere(ssPrincipal, ministereId);
        }
        return false;
    }

    public boolean allowAddPoste(SSPrincipal ssPrincipal, String ministereId) {
        if (ssPrincipal.isMemberOf(STBaseFunctionConstant.ALLOW_ADD_POSTE_ALL_MINISTERE)) {
            return true;
        }
        return isUserInMinistere(ssPrincipal, ministereId);
    }

    public boolean allowAddNode(SSPrincipal ssPrincipal, String ministereId) {
        if (ssPrincipal.isMemberOf(STBaseFunctionConstant.PROFIL_SGG)) {
            return true;
        }
        return isUserInMinistere(ssPrincipal, ministereId);
    }

    private boolean isUserInMinistere(final SSPrincipal ssPrincipal, final String ministereId) {
        Set<String> minIdSet = ssPrincipal.getMinistereIdSet();
        return (ministereId != null && minIdSet.contains(ministereId));
    }

    public List<OrganigrammeNode> getCurrentGouvernementEntite(String currentGouvernement, CoreSession session) {
        List<OrganigrammeNode> entiteList = new ArrayList<>();
        if (currentGouvernement != null) {
            GouvernementNode currentGouvernementNode = STServiceLocator
                .getSTGouvernementService()
                .getGouvernement(currentGouvernement);
            List<OrganigrammeNode> gvtChildren = STServiceLocator
                .getOrganigrammeService()
                .getChildrenList(session, currentGouvernementNode, Boolean.TRUE);
            for (OrganigrammeNode child : gvtChildren) {
                if (child.isActive()) {
                    entiteList.add(child);
                }
            }
        }
        return entiteList;
    }

    public boolean lockOrganigrammeNode(OrganigrammeNode node, CoreSession session) {
        final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        LOG.debug(STLogEnumImpl.MIGRATE_ENTITE_TEC, "Lock organigramme node : " + node.getLabel());
        Boolean result = false;
        if (organigrammeService.lockOrganigrammeNode(session, node)) {
            result = true;
        }
        return result;
    }

    public boolean unlockOrganigrammeNode(OrganigrammeNode node) {
        final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        LOG.debug(STLogEnumImpl.MIGRATE_ENTITE_TEC, "Unlock organigramme node : " + node.getLabel());
        Boolean result = false;
        if (organigrammeService.unlockOrganigrammeNode(node)) {
            result = true;
        }
        return result;
    }

    public boolean isCurrentUserUnlocker(SSPrincipal ssPrincipal, String locker) {
        return (
            locker != null &&
            !locker.isEmpty() &&
            locker.equals(ssPrincipal.getName()) ||
            ssPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_ADMIN_UNLOCKER)
        );
    }

    public List<EntiteNode> getCurrentMinisteres() {
        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();

        return ministeresService.getCurrentMinisteres();
    }

    public List<EntiteNode> getSortedCurrentMinisteres() {
        List<EntiteNode> currentMinisteres = getCurrentMinisteres();
        Collections.sort(currentMinisteres, new ProtocolarOrderComparator());
        return currentMinisteres;
    }
}
