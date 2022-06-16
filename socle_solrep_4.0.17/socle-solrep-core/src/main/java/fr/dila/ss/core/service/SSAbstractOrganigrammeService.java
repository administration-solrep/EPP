package fr.dila.ss.core.service;

import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.api.service.SSFeuilleRouteService;
import fr.dila.ss.api.service.SSOrganigrammeService;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem;
import fr.dila.st.api.service.organigramme.OrganigrammeNodeDeletionProblem.ProblemType;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.service.organigramme.OrganigrammeServiceImpl;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.naiad.nuxeo.commons.core.util.StringUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation du service de gestion de l'organigramme.
 *
 * @author FEO
 */
public abstract class SSAbstractOrganigrammeService extends OrganigrammeServiceImpl implements SSOrganigrammeService {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -2392698015083550568L;

    /**
     * Default constructor
     */
    public SSAbstractOrganigrammeService() {
        super();
    }

    /**
     * Retourne un élément d'identification du dossier dans le cadre de l'export du
     * fichier Excel de validation pré-suppression de noeud de l'organigramme.
     *
     * @param dossierId l'identifiant du dossier.
     * @param session   un objet CoreSession
     * @return une chaine de caractères permettant d'identifier le dossier.
     */
    protected abstract String getDossierIdentification(String dossierId, CoreSession session);

    /**
     * Retourne un élément d'identification de la feuille de route dans le cadre de
     * l'export du fichier Excel de validation pré-suppression de noeud de
     * l'organigramme.
     *
     * @param fdr     la feuille de route
     * @param session un objet CoreSession
     * @return une chaine de caractères permettant d'identifier la feuille de route.
     */
    protected abstract String getFeuilleRouteIdentification(FeuilleRoute fdr, CoreSession session);

    /**
     * Retourne l'élément d'identification des utilisateurs bloquant la suppression
     * d'un noeud de l'organigramme.
     *
     * @param users
     * @return renvoie une liste des identifiants des utilisateurs concernés
     */
    private String getUserIdentification(Collection<STUser> users) {
        List<String> userLogins = new ArrayList<>();

        users.forEach(u -> userLogins.add(u.getUsername()));

        return StringUtil.join(userLogins, ",");
    }

    /**
     * Renvoie une chaine décrivant la liste des directions et ministères de
     * rattachement du poste indiqué en paramètre.
     *
     * @param poste
     * @return
     */
    private String getDirectionsAndMinisteresRattachement(PosteNode poste) {
        List<String> nodesList = new ArrayList<>();

        // Récupération des directions et ministères de rattachement du poste
        poste.getUniteStructurelleParentList().forEach(us -> nodesList.add(us.getLabel()));
        poste.getEntiteParentList().forEach(en -> nodesList.add(en.getLabel()));

        return StringUtil.join(nodesList, ",");
    }

    @Override
    public Set<OrganigrammeNodeDeletionProblem> validateDeleteNode(CoreSession coreSession, OrganigrammeNode node) {
        List<OrganigrammeNode> nodeList = getChildrenList(null, node, Boolean.TRUE);

        Set<OrganigrammeNodeDeletionProblem> problems = new HashSet<>();

        // Analyse si c'est un poste
        if (node instanceof PosteNode) {
            problems.addAll(validateDeletePosteNode(coreSession, node));
        }

        // Analyse si c'est une unité structurelle
        if (node instanceof UniteStructurelleNode) {
            problems.addAll(validateDeleteUniteStructurelleNode(coreSession, (UniteStructurelleNode) node));
        }

        // Analyse des noeuds enfants
        for (OrganigrammeNode childNode : nodeList) {
            problems.addAll(validateDeleteNode(coreSession, childNode));
        }

        return problems;
    }

    protected Collection<OrganigrammeNodeDeletionProblem> validateDeletePosteNode(
        CoreSession coreSession,
        OrganigrammeNode node
    ) {
        Collection<OrganigrammeNodeDeletionProblem> problems = new ArrayList<>();
        PosteNode posteNode = (PosteNode) node;
        String posteInfo = getDirectionsAndMinisteresRattachement(posteNode);

        problems.addAll(validateDeletePosteRouteSteps(coreSession, node, posteInfo));
        problems.addAll(validateDeletePosteUsers(posteNode, posteInfo));

        return problems;
    }

    /**
     * Valide la possibilité de supprimer l'unité structurelle en paramètre. Une
     * direction n'est pas supprimable si :
     * <ul>
     * <li>il s'agit d'une direction pilote d'au moins une QE</li>
     * <li>il s'agit d'une direction pilote d'au moins une feuille de route</li>
     * </ul>
     *
     * @param coreSession la session
     * @param node        un noeud UniteStructurelle
     * @return la liste des problèmes rencontrés avec cette unité structurelle
     */
    protected Collection<OrganigrammeNodeDeletionProblem> validateDeleteUniteStructurelleNode(
        CoreSession coreSession,
        UniteStructurelleNode node
    ) {
        Collection<OrganigrammeNodeDeletionProblem> problems = new ArrayList<>();

        if (node.getType() == OrganigrammeType.DIRECTION) {
            problems.addAll(validateDeleteDirectionDossiers(node, coreSession));
            problems.addAll(validateDeleteDirectionFeuillesRoute(node, coreSession));
        }
        return problems;
    }

    /**
     * Valide la possibilité de suppression d'une direction en regard des dossiers
     * qui y sont potentiellement rattachés.
     *
     * @param coreSession
     * @return
     */
    protected abstract Collection<OrganigrammeNodeDeletionProblem> validateDeleteDirectionDossiers(
        UniteStructurelleNode node,
        CoreSession coreSession
    );

    /**
     * Valide la possibilité de suppression d'une direction en regard des feuilles
     * de route qui y sont potentiellement rattachés. Une direction n'est pas
     * supprimable si au moins une feuille de route est rattachée a cette direction
     * en tant que pilote.
     *
     * @param coreSession
     * @return
     */
    protected Collection<OrganigrammeNodeDeletionProblem> validateDeleteDirectionFeuillesRoute(
        UniteStructurelleNode node,
        CoreSession coreSession
    ) {
        Collection<OrganigrammeNodeDeletionProblem> problems = new ArrayList<>();

        FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        List<DocumentModel> listeDocumentAttacheEntite = feuilleRouteModelService.getFdrModelFromMinistereAndDirection(
            coreSession,
            null,
            node.getId(),
            true
        );

        if (CollectionUtils.isNotEmpty(listeDocumentAttacheEntite)) {
            for (DocumentModel docModel : listeDocumentAttacheEntite) {
                FeuilleRoute fdr = docModel.getAdapter(FeuilleRoute.class);
                OrganigrammeNodeDeletionProblem problem = new OrganigrammeNodeDeletionProblem(
                    ProblemType.MODELE_FDR_ATTACHED_TO_DIRECTION,
                    node.getLabel()
                );
                problem.setBlockingObjIdentification(getFeuilleRouteIdentification(fdr, coreSession));

                problems.add(problem);
            }
        }

        return problems;
    }

    private Set<OrganigrammeNodeDeletionProblem> validateDeletePosteUsers(PosteNode posteNode, String posteInfo) {
        Set<OrganigrammeNodeDeletionProblem> problems = new HashSet<>();
        Collection<STUser> users = STServiceLocator.getSTPostesService().getUsersHavingOnePosteOnly(posteNode);
        if (!users.isEmpty()) {
            OrganigrammeNodeDeletionProblem problem = new OrganigrammeNodeDeletionProblem(
                ProblemType.USER_IS_LINKED_TO_POSTE,
                posteNode.getLabel()
            );
            problem.setPosteInfo(posteInfo);
            problem.setBlockingObjIdentification(getUserIdentification(users));

            problems.add(problem);
        }

        return problems;
    }

    private Collection<OrganigrammeNodeDeletionProblem> validateDeletePosteRouteSteps(
        CoreSession coreSession,
        OrganigrammeNode node,
        String posteInfo
    ) {
        Collection<OrganigrammeNodeDeletionProblem> problems = new ArrayList<>();
        SSFeuilleRouteService fdrService = SSServiceLocator.getSSFeuilleRouteService();

        Collection<DocumentModel> routes = fdrService.getFeuilleRouteWithActiveOrFutureRouteStepsForPosteId(
            coreSession,
            node.getId()
        );
        for (DocumentModel routeDoc : routes) {
            OrganigrammeNodeDeletionProblem problem = null;

            // Instance ou modèle ?
            FeuilleRoute feuilleRoute = routeDoc.getAdapter(FeuilleRoute.class);
            List<String> attachedDossierIds = feuilleRoute.getAttachedDocuments();

            // Si la feuille de route est rattachée à un dossier, c'est une instance
            if (feuilleRoute.isFeuilleRouteInstance()) {
                // Un dossier est rattaché à la feuille de route, c'est une instance
                if (CollectionUtils.isNotEmpty(attachedDossierIds)) {
                    problem =
                        new OrganigrammeNodeDeletionProblem(
                            ProblemType.INSTANCE_FDR_ATTACHED_TO_POSTE,
                            node.getLabel()
                        );
                    String dossierId = attachedDossierIds.get(0);
                    problem.setBlockingObjIdentification(getDossierIdentification(dossierId, coreSession));
                }
            } else {
                // C'est un modèle de feuille de route
                problem =
                    new OrganigrammeNodeDeletionProblem(ProblemType.MODELE_FDR_ATTACHED_TO_POSTE, node.getLabel());
                problem.setBlockingObjIdentification(getFeuilleRouteIdentification(feuilleRoute, coreSession));
            }

            if (problem != null) {
                problem.setPosteInfo(posteInfo);
                problems.add(problem);
            }
        }

        return problems;
    }

    /**
     * Check si le déplacement des éléments est possible. La migration est autorisée
     * si les deux noeuds sont de même type et si le type est soit EntiteNode, soit
     * UniteStructurelleNode, soit PosteNode.
     *
     * @param nodeToCopy
     * @param destinationNode
     * @return
     */
    protected boolean isMigrateAllowed(OrganigrammeNode nodeToCopy, OrganigrammeNode destinationNode) {
        return (
            (nodeToCopy instanceof EntiteNode && destinationNode instanceof EntiteNode) ||
            (nodeToCopy instanceof UniteStructurelleNode && destinationNode instanceof UniteStructurelleNode) ||
            (nodeToCopy instanceof PosteNode && destinationNode instanceof PosteNode)
        );
    }

    /**
     * Déplace les sous éléments de l'élement nodeToMove dans l'élément destinationNode. note : les éléments fils de
     * l'ancien noeud pointe vers le nouveau noeud du coup pas besoin de parcourir récursivement les éléments fils.
     *
     * @param coreSession
     * @param nodeToMove
     * @param destinationNode
     * @param oldAndNewIdTable
     * @param withUsers
     * @
     */
    protected void migrateNodeChildrenToDestinationNode(OrganigrammeNode nodeToMove, OrganigrammeNode destinationNode) {
        // migration du noeud
        if (nodeToMove instanceof PosteNode) {
            // déplacement des utilisateurs si les noeuds sont de type poste.
            List<String> actualMembers = ((PosteNode) destinationNode).getMembers();
            if (actualMembers == null) {
                actualMembers = new ArrayList<>();
            }
            List<String> membersToAdd = ((PosteNode) nodeToMove).getMembers();
            if (membersToAdd != null && !membersToAdd.isEmpty()) {
                actualMembers.addAll(membersToAdd);
                // ajout des utilisateurs dans le nouveau noeud et enregistrement
                ((PosteNode) destinationNode).setMembers(actualMembers);
                updateNode(destinationNode, true);
            }
            // suppression des utilisateurs dans l'ancien noeud et enregistrement
            ((PosteNode) nodeToMove).setMembers(null);
            updateNode(nodeToMove, true);
        } else if (nodeToMove instanceof UniteStructurelleNode) {
            // déplacement des postes et uniteStructurelle si les noeuds sont de type unités structurelle.

            // déplacement des postes enfants : on les rattache à la bonne unité
            List<PosteNode> posteToAdd = ((UniteStructurelleNode) nodeToMove).getSubPostesList();
            for (PosteNode posteEnfant : posteToAdd) {
                List<String> unitesParents = posteEnfant.getParentUnitIds();
                unitesParents.remove(nodeToMove.getId());
                unitesParents.add(destinationNode.getId());

                posteEnfant.setParentUnitIds(unitesParents);
            }

            updateNodes(posteToAdd, true);

            // déplacement des unités structurelles
            List<UniteStructurelleNode> uniteStructurelleToAdd =
                ((UniteStructurelleNode) nodeToMove).getSubUnitesStructurellesList();
            for (UniteStructurelleNode uniteEnfant : uniteStructurelleToAdd) {
                List<String> unitesParents = uniteEnfant.getParentUnitIds();

                unitesParents.remove(nodeToMove.getId());
                unitesParents.add(destinationNode.getId());

                uniteEnfant.setParentUnitIds(unitesParents);
            }

            // enregistrement des nouvelles unites structurelles et postes
            updateNodes(uniteStructurelleToAdd, true);
        } else if (nodeToMove instanceof EntiteNode) {
            // déplacement des postes et uniteStructurelle si les noeuds sont de type unités structurelle.

            // déplacement des postes
            List<PosteNode> posteToAdd = ((EntiteNode) nodeToMove).getSubPostesList();
            for (PosteNode posteEnfant : posteToAdd) {
                List<String> entitesParents = posteEnfant.getParentEntiteIds();
                entitesParents.remove(nodeToMove.getId());
                entitesParents.add(destinationNode.getId());

                posteEnfant.setParentEntiteIds(entitesParents);
            }

            updateNodes(posteToAdd, true);

            // déplacement des unités structurelles
            List<UniteStructurelleNode> uniteStructurelleToAdd =
                ((EntiteNode) nodeToMove).getSubUnitesStructurellesList();
            for (UniteStructurelleNode uniteEnfant : uniteStructurelleToAdd) {
                List<String> entitesParents = new ArrayList<>(uniteEnfant.getParentEntiteIds());

                entitesParents.remove(nodeToMove.getId());
                entitesParents.add(destinationNode.getId());

                uniteEnfant.setParentEntiteIds(entitesParents);
            }

            // enregistrement des nouvelles unites structurelles et postes
            updateNodes(uniteStructurelleToAdd, true);
        }
    }

    @Override
    public OrganigrammeNode getOrganigrammeNodeById(String nodeId) {
        // L'ordre de recherche se fait volontairement du type le plus "haut" dans la
        // hiérarchie vers le type le plus "bas" mais l'id devrait être unique parmi
        // tous les types de noeuds.
        return (OrganigrammeNode) Stream
            .of(
                OrganigrammeType.GOUVERNEMENT,
                OrganigrammeType.MINISTERE,
                OrganigrammeType.UNITE_STRUCTURELLE,
                OrganigrammeType.DIRECTION,
                OrganigrammeType.POSTE
            )
            .map(type -> getOrganigrammeNodeById(nodeId, type))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
