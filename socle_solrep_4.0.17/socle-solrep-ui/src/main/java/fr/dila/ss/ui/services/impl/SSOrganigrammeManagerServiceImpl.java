package fr.dila.ss.ui.services.impl;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSOrganigrammeManagerService;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.bean.TreeElementDTO;
import fr.dila.st.ui.enums.ActionCategory;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.impl.AbstractOrganigrammeManagerService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ActionContext;
import org.nuxeo.ecm.platform.actions.ELActionContext;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.runtime.api.Framework;

public class SSOrganigrammeManagerServiceImpl
    extends AbstractOrganigrammeManagerService
    implements SSOrganigrammeManagerService {
    private static final String ALLOW_UPDATE_ORGANIGRAMME_KEY = "allowUpdateOrganigramme";
    public static final String SELECTED_NODE_FOR_COPY_KEY = "selectedNodeForCopy";
    public static final String IS_NODE_SELECTED_FOR_COPY_KEY = "isNodeSelectedForCopy";

    public static final String ORGANIGRAMME_BASE_ACTIONS_KEY = "organigrammeBaseActions";
    public static final String ORGANIGRAMME_MAIN_ACTIONS_KEY = "organigrammeMainActions";

    @Override
    public List<OrganigrammeElementDTO> getOrganigramme(boolean loadActions, SpecificContext context) {
        OrganigrammeTreeUIService service = STUIServiceLocator.getOrganigrammeTreeService();
        List<OrganigrammeElementDTO> organigramme = service.getOrganigramme(context);

        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);

        if (loadActions) {
            // Charger les données additionnelles aux éléments de l'organigramme
            for (OrganigrammeElementDTO element : organigramme) {
                loadAdditionalData(element, loadActions, context);
            }
        }

        return organigramme;
    }

    /**
     * Renseigne les données additionnelles à un élément de l'organigramme et
     * ses fils
     *
     * @param loadActions
     */
    private void loadAdditionalData(OrganigrammeElementDTO element, boolean loadActions, SpecificContext context) {
        OrganigrammeNode organigrammeNode = element.getOrganigrammeNode();
        CoreSession session = context.getSession();

        if (loadActions && organigrammeNode != null) {
            element.setActions(loadActions(element, element.getMinistereId(), context));
        }

        if (organigrammeNode instanceof EntiteNode) {
            element.setAllowAdd(allowAddMin((SSPrincipal) session.getPrincipal(), element.getMinistereId()));
        } else if (organigrammeNode instanceof PosteNode) {
            element.setAllowAdd(allowAddPoste((SSPrincipal) session.getPrincipal(), element.getMinistereId()));
        }

        for (TreeElementDTO child : element.getChilds()) {
            loadAdditionalData((OrganigrammeElementDTO) child, loadActions, context);
        }
    }

    private List<Action> loadActions(OrganigrammeElementDTO node, String ministereId, SpecificContext context) {
        context.putInContextData(STContextDataKey.ORGANIGRAMME_NODE, node);
        context.putInContextData(
            ALLOW_UPDATE_ORGANIGRAMME_KEY,
            isAllowUpdateOrganigramme(context.getSession(), ministereId)
        );
        context.putInContextData(IS_NODE_SELECTED_FOR_COPY_KEY, isNodeSelectedForCopy());
        context.putInContextData("isPosteWebservice", isPosteWebservice(node));
        context.putInContextData("isPoste", isPoste(node));

        return context.getActions(STActionCategory.ORGANIGRAMME_ACTIONS);
    }

    @Override
    public boolean isNodeSelectedForCopy() {
        return getCopiedOrganigrammeNode() != null;
    }

    @Override
    public OrganigrammeNode getCopiedOrganigrammeNode() {
        return (OrganigrammeNode) WebEngine.getActiveContext().getUserSession().get(SELECTED_NODE_FOR_COPY_KEY);
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
        if (selectionType.contains(OrganigrammeType.GOUVERNEMENT.getValue())) {
            directoryNames.add(OrganigrammeType.GOUVERNEMENT);
        }
        if (selectionType.contains(OrganigrammeType.MINISTERE.getValue())) {
            directoryNames.add(OrganigrammeType.MINISTERE);
        }
        if (
            selectionType.contains(OrganigrammeType.DIRECTION.getValue()) ||
            selectionType.contains(OrganigrammeType.UNITE_STRUCTURELLE.getValue())
        ) {
            directoryNames.add(OrganigrammeType.UNITE_STRUCTURELLE);
        }

        List<SuggestionDTO> suggestions = new ArrayList<>();
        if (!directoryNames.isEmpty()) {
            suggestions =
                getOrganigrammeSuggestions(session, input, selectionType, directoryNames, activatePosteFilter);
        }

        return suggestions;
    }

    private List<SuggestionDTO> getOrganigrammeSuggestions(
        CoreSession session,
        String input,
        List<String> selectionType,
        List<OrganigrammeType> directoryName,
        boolean activatePosteFilter
    ) {
        String pattern = input;
        List<SuggestionDTO> out = new ArrayList<>();
        List<OrganigrammeNode> results = STServiceLocator
            .getOrganigrammeService()
            .getOrganigrameLikeLabels(pattern, directoryName);
        final STMinisteresService ministeresService = STServiceLocator.getSTMinisteresService();
        for (OrganigrammeNode node : results) {
            if (node.getDeleted() || !node.isActive()) {
                continue;
            }

            // Cas des postes, une vérification est faite
            if ((directoryName.contains(OrganigrammeType.POSTE)) && activatePosteFilter) {
                List<EntiteNode> entiteNodes = ministeresService.getMinistereParentFromPoste(node.getId());
                boolean allowed = false;
                for (EntiteNode entiteNode : entiteNodes) {
                    if (entiteNode != null && allowAddPoste((SSPrincipal) session.getPrincipal(), entiteNode.getId())) {
                        allowed = true;
                    }
                }
                if (!allowed) {
                    continue;
                }
            }

            // Cas du ministère, vérification du droit
            if (
                directoryName.contains(OrganigrammeType.MINISTERE) &&
                activatePosteFilter &&
                !allowAddMin((SSPrincipal) session.getPrincipal(), node.getId())
            ) {
                continue;
            }

            // Ajout du node aux suggestions
            out.add(
                new SuggestionDTO(
                    selectionType.contains(MAILBOX_TYPE) ? getMailboxIdFromPosteId(node.getId()) : node.getId(),
                    node.getLabel()
                )
            );
        }
        return out;
    }

    /**
     * Test si l'utilisateur peut ajouter un ministère en fonction de son ministère
     *
     * @param ssPrincipal
     * @param ministereId
     * @return
     */
    protected boolean allowAddMin(SSPrincipal ssPrincipal, String ministereId) {
        return (
            ssPrincipal != null &&
            (
                ssPrincipal.isMemberOf(STBaseFunctionConstant.ALLOW_ADD_POSTE_ALL_MINISTERE) ||
                isUserInMinistere(ssPrincipal, ministereId)
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
    protected boolean allowAddPoste(SSPrincipal ssPrincipal, String ministereId) {
        return (
            ssPrincipal != null &&
            (
                ssPrincipal.isMemberOf(STBaseFunctionConstant.ALLOW_ADD_POSTE_ALL_MINISTERE) ||
                isUserInMinistere(ssPrincipal, ministereId)
            )
        );
    }

    /**
     * Récupère l'id de la mailbox à partir de l'id d'un poste
     *
     * @param posteId
     * @return
     */
    protected String getMailboxIdFromPosteId(String posteId) {
        return posteId != null ? SSServiceLocator.getMailboxPosteService().getPosteMailboxId(posteId) : null;
    }

    private boolean isUserInMinistere(final SSPrincipal ssPrincipal, final String ministereId) {
        Set<String> minIdSet = ssPrincipal.getMinistereIdSet();
        return ministereId != null && minIdSet != null && minIdSet.contains(ministereId);
    }

    @Override
    public boolean isAllowUpdateOrganigramme(CoreSession session, String ministereId) {
        NuxeoPrincipal ssPrincipal = session.getPrincipal();

        if (ssPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_UPDATER)) {
            return true;
        }
        if (ssPrincipal.isMemberOf(STBaseFunctionConstant.ORGANIGRAMME_MINISTERE_UPDATER)) {
            return isUserInMinistere((SSPrincipal) ssPrincipal, ministereId);
        }
        return false;
    }

    protected boolean isPosteWebservice(OrganigrammeElementDTO node) {
        return (
            node != null &&
            OrganigrammeType.isPoste(node.getType()) &&
            OrganigrammeType.isMinistere(node.getParent().getType())
        );
    }

    protected boolean isPoste(OrganigrammeElementDTO node) {
        return (
            node != null &&
            (
                OrganigrammeType.isPoste(node.getType()) &&
                (
                    OrganigrammeType.isUniteStructurelle(node.getParent().getType()) ||
                    OrganigrammeType.isDirection(node.getParent().getType())
                )
            )
        );
    }

    @Override
    public List<Action> loadBaseAdminOrganigrammeActions(CoreSession session) {
        return loadOrganigrammeActions(SSActionCategory.ORGANIGRAMME_BASE_ACTIONS, session);
    }

    @Override
    public List<Action> loadMainAdminOrganigrammeActions(CoreSession session) {
        return loadOrganigrammeActions(SSActionCategory.ORGANIGRAMME_MAIN_ACTIONS, session);
    }

    private List<Action> loadOrganigrammeActions(ActionCategory category, CoreSession session) {
        final ActionManager actionService = Framework.getService(ActionManager.class);
        return actionService.getActions(category.getName(), createActionContext(session));
    }

    private ActionContext createActionContext(CoreSession session) {
        CoreSession coreSession = WebEngine.getActiveContext().getCoreSession();
        ActionContext actionContext = new ELActionContext();

        actionContext.setDocumentManager(coreSession);
        if (coreSession != null) {
            actionContext.setCurrentPrincipal(coreSession.getPrincipal());
        }

        actionContext.putLocalVariable(ALLOW_UPDATE_ORGANIGRAMME_KEY, isAllowUpdateOrganigramme(session, null));

        return actionContext;
    }

    @Override
    public void computeOrganigrammeActions(SpecificContext context) {
        OrganigrammeElementDTO node = context.getFromContextData(STContextDataKey.ORGANIGRAMME_NODE);
        if (node != null) {
            context.putInContextData(STContextDataKey.ORGANIGRAMME_NODE, node);
            List<EntiteNode> lstMinParents = STServiceLocator
                .getSTMinisteresService()
                .getMinisteresParents(node.getOrganigrammeNode());
            if (
                OrganigrammeType.MINISTERE.getValue().equals(node.getType()) ||
                OrganigrammeType.GOUVERNEMENT.getValue().equals(node.getType()) ||
                lstMinParents.stream().map(EntiteNode::getId).anyMatch(minId -> minId.equals(node.getMinistereId()))
            ) {
                if (!context.containsKeyInContextData(ALLOW_UPDATE_ORGANIGRAMME_KEY)) {
                    context.putInContextData(
                        ALLOW_UPDATE_ORGANIGRAMME_KEY,
                        isAllowUpdateOrganigramme(context.getSession(), node.getMinistereId())
                    );
                }
                context.putInContextData("isPosteWebservice", isPosteWebservice(node));
                context.putInContextData("isPoste", isPoste(node));
                context.putInContextData(IS_NODE_SELECTED_FOR_COPY_KEY, isNodeSelectedForCopy());
            }
        } else {
            context.putInContextData(
                ALLOW_UPDATE_ORGANIGRAMME_KEY,
                isAllowUpdateOrganigramme(context.getSession(), null)
            );
        }
        if (isNodeSelectedForCopy()) {
            OrganigrammeType copiedNodeType = getCopiedOrganigrammeNode().getType();
            context.putInContextData(SSContextDataKey.IS_POSTE_COPIED, OrganigrammeType.POSTE.equals(copiedNodeType));
            context.putInContextData(
                SSContextDataKey.IS_DIRECTION_COPIED,
                OrganigrammeType.DIRECTION.equals(copiedNodeType)
            );
            context.putInContextData(
                SSContextDataKey.IS_MINISTERE_COPIED,
                OrganigrammeType.MINISTERE.equals(copiedNodeType)
            );
            context.putInContextData(
                SSContextDataKey.IS_US_COPIED,
                OrganigrammeType.UNITE_STRUCTURELLE.equals(copiedNodeType)
            );
        } else {
            context.putInContextData(SSContextDataKey.IS_POSTE_COPIED, false);
            context.putInContextData(SSContextDataKey.IS_DIRECTION_COPIED, false);
            context.putInContextData(SSContextDataKey.IS_MINISTERE_COPIED, false);
            context.putInContextData(SSContextDataKey.IS_US_COPIED, false);
        }
    }
}
