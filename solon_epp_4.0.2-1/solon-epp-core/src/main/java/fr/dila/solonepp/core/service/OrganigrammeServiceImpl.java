package fr.dila.solonepp.core.service;

import com.google.common.collect.Sets;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.organigramme.InstitutionNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Query;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Implémentation du service de gestion de l'organigramme de SOLON EPP.
 *
 * @author jtremeaux
 */
public class OrganigrammeServiceImpl
    extends fr.dila.st.core.service.organigramme.OrganigrammeServiceImpl
    implements OrganigrammeService {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3245722454324718854L;
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(OrganigrammeServiceImpl.class);

    private static final String ALL_INSTIT_QUERY = "Select i From InstitutionNode i";

    @Override
    public InstitutionNode getInstitution(final String institutionId) {
        return apply(
            true,
            em -> {
                return em.find(InstitutionNodeImpl.class, institutionId);
            }
        );
    }

    @Override
    public List<InstitutionNode> getInstitutionParentFromPoste(String posteId) {
        PosteNode poste = (PosteNode) getOrganigrammeNodeById(posteId, OrganigrammeType.POSTE);
        List<InstitutionNode> parents = new ArrayList<>();

        getInstitutionParent(poste, parents);

        return parents;
    }

    /**
     * Remonte recursivement l'organigramme (poste / unités structurelles) jusqu'à un noeud de type institution.
     *
     * @param node
     *            Noeud de l'organigramme (poste ou unité structurelle)
     * @param resultList
     *            Liste des institutions (construite par effet de bord)
     */
    private void getInstitutionParent(OrganigrammeNode node, List<InstitutionNode> resultList) {
        // Remonte les unités structurelles parentes
        List<OrganigrammeNode> parentList = getParentList(node);
        for (OrganigrammeNode parentNode : parentList) {
            if (parentNode == null) {
                LOGGER.warn(null, STLogEnumImpl.FAIL_GET_ENTITE_NODE_TEC);
                continue;
            }

            if (parentNode instanceof InstitutionNode) {
                resultList.add((InstitutionNode) parentNode);
            } else if (parentNode instanceof UniteStructurelleNode) {
                getInstitutionParent(parentNode, resultList);
            }
        }
    }

    @Override
    public List<OrganigrammeNode> getChildrenList(
        CoreSession coreSession,
        OrganigrammeNode node,
        Boolean showDeactivedNode
    ) {
        if (node instanceof InstitutionNode) {
            return getChildrenListInstitutionNode((InstitutionNode) node, coreSession, showDeactivedNode);
        } else {
            return super.getChildrenList(coreSession, node, showDeactivedNode);
        }
    }

    /**
     * Liste des enfants d'une {@link InstitutionNode}
     *
     * @param institutionNode
     * @param ldapSessionContainer
     * @param coreSession
     * @return
     */
    protected List<OrganigrammeNode> getChildrenListInstitutionNode(
        InstitutionNode institutionNode,
        CoreSession coreSession,
        Boolean showDeactivedNode
    ) {
        List<OrganigrammeNode> childrenList = new ArrayList<>();
        List<UniteStructurelleNode> usChildren = institutionNode.getSubUnitesStructurellesList();
        List<PosteNode> posteChildren = institutionNode.getSubPostesList();

        if (showDeactivedNode) {
            childrenList.addAll(usChildren);
            childrenList.addAll(posteChildren);
        } else {
            Date today = new Date();
            for (UniteStructurelleNode ust : usChildren) {
                if (ust.getDateFin() == null || ust.getDateFin().compareTo(today) > 0) {
                    childrenList.add(ust);
                }
            }
            for (PosteNode posteNode : posteChildren) {
                if (posteNode.getDateFin() == null || posteNode.getDateFin().compareTo(today) > 0) {
                    childrenList.add(posteNode);
                }
            }
        }
        return childrenList;
    }

    @Override
    public List<OrganigrammeNode> getParentList(OrganigrammeNode node) {
        if (node instanceof PosteNode) {
            return getParentListPosteNodeEPP((PosteNode) node);
        } else if (node instanceof UniteStructurelleNode) {
            return getParentListUniteStructurelleNodeEPP((UniteStructurelleNode) node);
        } else if (node instanceof InstitutionNode) {
            return null;
        } else {
            return super.getParentList(node);
        }
    }

    @Override
    public List<OrganigrammeNode> getFullParentList(OrganigrammeNode node) {
        List<OrganigrammeNode> parents = new ArrayList<>();
        if (node instanceof PosteNode) {
            List<OrganigrammeNode> tmpParents = getParentListPosteNodeEPP((PosteNode) node);
            tmpParents.stream().forEach(parent -> parents.addAll(getFullParentList(parent)));
            parents.addAll(tmpParents);
        } else if (node instanceof UniteStructurelleNode) {
            List<OrganigrammeNode> tmpParents = getParentListUniteStructurelleNodeEPP((UniteStructurelleNode) node);
            tmpParents.stream().forEach(parent -> parents.addAll(getFullParentList(parent)));
            parents.addAll(tmpParents);
        } else if (node instanceof InstitutionNode) {
            parents.add(node);
        }

        return parents;
    }

    /**
     * liste des parents d'un {@link UniteStructurelleNode}
     *
     * @param uniteStructurelleNode
     * @param ldapSessionContainer
     * @return
     */
    private List<OrganigrammeNode> getParentListUniteStructurelleNodeEPP(UniteStructurelleNode uniteStructurelleNode) {
        List<OrganigrammeNode> parentList = new ArrayList<>();
        List<UniteStructurelleNode> uniteStructurelleParentList = uniteStructurelleNode.getUniteStructurelleParentList();
        List<InstitutionNode> institutionParentList = uniteStructurelleNode.getInstitutionParentList();
        parentList.addAll(uniteStructurelleParentList);

        parentList.addAll(institutionParentList);

        return parentList;
    }

    /**
     * liste des parents d'un {@link PosteNode}
     *
     * @param posteNode
     * @param ldapSessionContainer
     * @return
     */
    private List<OrganigrammeNode> getParentListPosteNodeEPP(PosteNode posteNode) {
        List<OrganigrammeNode> parentList = new ArrayList<>();
        List<UniteStructurelleNode> uniteStructurelleParentList = posteNode.getUniteStructurelleParentList();
        List<InstitutionNode> institutionParentList = posteNode.getInstitutionParentList();

        parentList.addAll(uniteStructurelleParentList);
        parentList.addAll(institutionParentList);

        return parentList;
    }

    @Override
    public List<InstitutionNode> getAllInstitutions() {
        return apply(
            true,
            em -> {
                Query query = em.createQuery(ALL_INSTIT_QUERY);
                @SuppressWarnings("unchecked")
                List<InstitutionNode> nodes = query.getResultList();
                return nodes;
            }
        );
    }

    @Override
    public List<STUser> getUserFromInstitution(String institutionId) {
        OrganigrammeNode institutionNode = getInstitution(institutionId);
        return getUsersInSubNode(institutionNode);
    }

    @Override
    public List<PosteNode> getPosteFromInstitution(String institutionId) {
        InstitutionNode institutionNode = getInstitution(institutionId);
        return institutionNode.getSubPostesList();
    }

    /**
     * Recherche tous les utilisateurs appartenant au noeud spécifié (descend récursivement dans les sous-noeuds de
     * l'arbre).
     *
     * @param nodeParent
     *            Noeud à recherche
     * @param ldapSessionContainer
     *            Conteneur de session LDAP
     * @return Liste de postes
     */
    protected List<PosteNode> getPosteInSubNode(OrganigrammeNode nodeParent) {
        List<PosteNode> list = new ArrayList<>();

        List<OrganigrammeNode> childrenList = getChildrenList(null, nodeParent, Boolean.TRUE);
        for (OrganigrammeNode node : childrenList) {
            if (node instanceof PosteNode) {
                list.add((PosteNode) node);
            } else {
                list.addAll(getPosteInSubNode(node));
            }
        }

        return list;
    }

    @Override
    public List<STUser> getUserFromInstitutionAndBaseFunction(String institutionId, String baseFunctionId) {
        final ProfileService profileService = STServiceLocator.getProfileService();
        Set<STUser> user1Set = new HashSet<>(profileService.getUsersFromBaseFunction(baseFunctionId));
        Set<STUser> user2Set = new HashSet<>(getUserFromInstitution(institutionId));
        return new ArrayList<>(Sets.intersection(user1Set, user2Set));
    }

    @Override
    public List<? extends OrganigrammeNode> getRootNodes() {
        return getAllInstitutions();
    }
}
