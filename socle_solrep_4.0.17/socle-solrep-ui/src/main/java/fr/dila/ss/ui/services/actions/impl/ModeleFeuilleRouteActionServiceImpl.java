package fr.dila.ss.ui.services.actions.impl;

import static fr.dila.ss.core.service.SSServiceLocator.getFeuilleRouteModelService;
import static fr.dila.st.core.util.ResourceHelper.getString;
import static fr.dila.st.ui.services.actions.STActionsServiceLocator.getSTLockActionService;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.criteria.SubstitutionCriteria;
import fr.dila.ss.api.domain.feuilleroute.StepFolder;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.groupcomputer.MinistereGroupeHelper;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.th.bean.ModeleFdrEtapeSupprimeForm;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.schema.DublincoreSchemaUtils;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.services.actions.impl.STLockActionServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement.ElementLifeCycleState;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Actions permettant de gérer un modèle de feuille de route dans le socle
 * transverse.
 *
 * @author jtremeaux
 */
public class ModeleFeuilleRouteActionServiceImpl implements ModeleFeuilleRouteActionService {
    private static final STLogger LOG = STLogFactory.getLog(ModeleFeuilleRouteActionServiceImpl.class);

    public static final String MESSAGE_ROUTE_NOT_LOCKED = "feedback.casemanagement.document.route.not.locked";
    public static final String MESSAGE_ROUTE_ALREADY_LOCKED = "feedback.casemanagement.document.route.already.locked";

    @Override
    public boolean canUserReadRoute(SpecificContext context) {
        // Les administrateurs fonctionnels ont le droit de lecture sur tous les
        // modèles
        final NuxeoPrincipal principal = context.getSession().getPrincipal();
        if (principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)) {
            return true;
        }

        /*
         * Les administrateurs ministériels ont le droit de lecture sur les
         * modèles de leur ministère, et sur les modèles communs (ministère non
         * affecté)
         */
        DocumentModel doc = context.getCurrentDocument();
        final SSFeuilleRoute feuilleRoute = doc.getAdapter(SSFeuilleRoute.class);
        final String ministere = feuilleRoute.getMinistere();
        final String groupMinistere = MinistereGroupeHelper.ministereidToGroup(ministere);

        return StringUtils.isBlank(ministere) || principal.isMemberOf(groupMinistere);
    }

    @Override
    public boolean canUserCreateRoute(SpecificContext context) {
        return context.getAction(SSActionEnum.MODELE_ACTION_CREER_MODELE) != null;
    }

    @Override
    public boolean canUserModifyRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();

        // Les modèles en demande de validation ou validés sont non modifiables
        // Si je n'est pas moi-même le verrou sur le document -> non modifiable
        final SSFeuilleRoute feuilleRoute = doc.getAdapter(SSFeuilleRoute.class);
        if (
            (feuilleRoute.isDraft() && feuilleRoute.isDemandeValidation()) ||
            feuilleRoute.isValidated() ||
            feuilleRoute.isReady() ||
            !LockUtils.isLockedByCurrentUser(session, doc.getRef())
        ) {
            return false;
        }

        // Les administrateurs fonctionnels ont le droit d'écriture sur tous les
        // modèles
        final NuxeoPrincipal principal = session.getPrincipal();
        if (!principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)) {
            // Les administrateurs ministériels ont le droit d'écriture sur les
            // modèles de leur ministère
            final String ministere = feuilleRoute.getMinistere();
            final String groupMinistere = MinistereGroupeHelper.ministereidToGroup(ministere);

            if (
                StringUtils.isBlank(ministere) ||
                !principal.isMemberOf(groupMinistere) ||
                !principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER)
            ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canUserLockRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();

        // Les administrateurs fonctionnels ont le droit d'écriture sur tous les
        // modèles
        final NuxeoPrincipal principal = session.getPrincipal();
        if (!principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)) {
            // Les administrateurs ministériels ont le droit d'écriture sur les
            // modèles de leur ministère
            final SSFeuilleRoute feuilleRoute = doc.getAdapter(SSFeuilleRoute.class);
            final String ministere = feuilleRoute.getMinistere();
            final String groupMinistere = MinistereGroupeHelper.ministereidToGroup(ministere);

            if (
                StringUtils.isBlank(ministere) ||
                !principal.isMemberOf(groupMinistere) ||
                !principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER)
            ) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canUserDeleteRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();

        // Les modèles verrouillés par un autre utilisateur sont non
        // supprimables

        if (
            LockUtils.isLockedByCurrentUser(session, doc.getRef()) ||
            getSTLockActionService().getCanLockDoc(doc, session)
        ) {
            final SSFeuilleRoute feuilleRoute = doc.getAdapter(SSFeuilleRoute.class);
            final String ministere = feuilleRoute.getMinistere();
            final String groupMinistere = MinistereGroupeHelper.ministereidToGroup(ministere);

            // Les administrateurs fonctionnels ont le droit d'écriture sur tous les modèles
            // Les administrateurs ministériels ont le droit d'écriture sur les modèles de leur ministère
            final NuxeoPrincipal principal = session.getPrincipal();
            if (
                principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR) ||
                isAdminMinisteriel(context) &&
                principal.isMemberOf(groupMinistere)
            ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canUserLibererVerrou(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();

        // Verifie si le document est verrouillé
        if (!LockUtils.isLockedByAnotherUser(session, doc.getRef())) {
            return false;
        }

        // Les administrateurs fonctionnels ont le droit de déverrouiller tous
        // les documents
        final NuxeoPrincipal principal = session.getPrincipal();
        return principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
    }

    @Override
    public boolean canCreateRoute(SpecificContext context) {
        return isAdminFonctionnel(context);
    }

    @Override
    public boolean isAdminFonctionnel(SpecificContext context) {
        CoreSession session = context.getSession();
        final NuxeoPrincipal principal = session.getPrincipal();
        return principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
    }

    @Override
    public boolean isFeuilleDeRouteCreeParAdminFonctionnel(SpecificContext context) {
        DocumentModel doc = context.getCurrentDocument();
        String feuilleDeRouteCreateur = DublincoreSchemaUtils.getCreator(doc);

        UserManager userManager = STServiceLocator.getUserManager();
        SSPrincipal principal = (SSPrincipal) userManager.getPrincipal(feuilleDeRouteCreateur);

        if (principal == null) {
            return false;
        } else {
            return principal.isMemberOf(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME);
        }
    }

    @Override
    public boolean isAdminMinisteriel(SpecificContext context) {
        final NuxeoPrincipal principal = context.getSession().getPrincipal();
        return principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER);
    }

    @Override
    public boolean canRequestValidateRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (!LockUtils.isLockedByCurrentUser(session, doc.getRef())) {
            return false;
        }

        final SSFeuilleRoute feuilleRoute = getRelatedRoute(session, doc);
        final String lifeCycleState = feuilleRoute.getDocument().getCurrentLifeCycleState();
        boolean isDraft = ElementLifeCycleState.draft.name().equals(lifeCycleState);
        boolean demandeValidation = feuilleRoute.isDemandeValidation();
        final NuxeoPrincipal principal = context.getSession().getPrincipal();
        boolean isAdminMinisteriel =
            principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER) &&
            !principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
        final String ministere = feuilleRoute.getMinistere();

        return isDraft && !demandeValidation && isAdminMinisteriel && !StringUtils.isBlank(ministere);
    }

    @Override
    public boolean canCancelRequestValidateRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (LockUtils.isLockedByAnotherUser(session, doc.getRef())) {
            return false;
        }

        final SSFeuilleRoute feuilleRoute = getRelatedRoute(session, doc);
        final String lifeCycleState = feuilleRoute.getDocument().getCurrentLifeCycleState();
        boolean isDraft = ElementLifeCycleState.draft.name().equals(lifeCycleState);
        boolean demandeValidation = feuilleRoute.isDemandeValidation();
        final NuxeoPrincipal principal = context.getSession().getPrincipal();
        boolean isAdminMinisteriel =
            principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER) &&
            !principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);

        return isDraft && demandeValidation && isAdminMinisteriel;
    }

    @Override
    public boolean canValidateRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel currentDoc = context.getCurrentDocument();

        final SSFeuilleRoute feuilleRoute = getRelatedRoute(session, currentDoc);
        DocumentModel doc = feuilleRoute.getDocument();

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (LockUtils.isLockedByAnotherUser(session, doc.getRef())) {
            return false;
        }

        final String lifeCycleState = doc.getCurrentLifeCycleState();
        boolean isDraft = ElementLifeCycleState.draft.name().equals(lifeCycleState);
        final NuxeoPrincipal principal = context.getSession().getPrincipal();
        boolean isAdminFonctionnel = principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);

        return isDraft && isAdminFonctionnel;
    }

    @Override
    public boolean canRefuseValidateRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel currentDoc = context.getCurrentDocument();

        final SSFeuilleRoute feuilleRoute = getRelatedRoute(session, currentDoc);
        DocumentModel doc = feuilleRoute.getDocument();

        // Les modèles verrouillés par un autre utilisateur sont non modifiables
        if (LockUtils.isLockedByAnotherUser(session, doc.getRef())) {
            return false;
        }

        final String lifeCycleState = doc.getCurrentLifeCycleState();
        boolean isDraft = ElementLifeCycleState.draft.name().equals(lifeCycleState);
        final NuxeoPrincipal principal = context.getSession().getPrincipal();
        boolean isAdminFonctionnel = principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
        boolean isDemandeValidation = feuilleRoute.isDemandeValidation();

        return isDraft && isAdminFonctionnel && isDemandeValidation;
    }

    @Override
    public boolean canInvalidateRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel currentDoc = context.getCurrentDocument();

        final SSFeuilleRoute feuilleRoute = getRelatedRoute(session, currentDoc);
        DocumentModel doc = feuilleRoute.getDocument();

        // Seul les modèles que l'utilisateur peut verrouiller sont modifiables
        if (LockUtils.isLockedByAnotherUser(session, doc.getRef())) {
            context.getMessageQueue().addWarnToQueue(getString(STLockActionServiceImpl.ALREADY_LOCK_ERROR_MSG));
            return false;
        }

        // On peut invalider un modèle de feuille de route uniquement à l'état validé
        final String lifeCycleState = doc.getCurrentLifeCycleState();
        if (ElementLifeCycleState.validated.name().equals(lifeCycleState)) {
            // Seul les modèles non verrouillé par un autre utilisater son modifiable
            if (!LockUtils.isLockedByAnotherUser(session, doc.getRef())) {
                // L'administrateur fonctionnel peut invalider un modèle
                final NuxeoPrincipal principal = context.getSession().getPrincipal();
                if (principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR)) {
                    return true;
                }
                // l'administrateur ministériel ne peut modifier que les feuilles de
                // route affecté à ses ministères
                final String ministere = feuilleRoute.getMinistere();
                final String groupMinistere = MinistereGroupeHelper.ministereidToGroup(ministere);
                return (
                    !StringUtils.isBlank(ministere) &&
                    principal.isMemberOf(groupMinistere) &&
                    principal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER)
                );
            } else {
                context.getMessageQueue().addWarnToQueue(getString(STLockActionServiceImpl.ALREADY_LOCK_ERROR_MSG));
            }
            return false;
        }
        return false;
    }

    @Override
    public SSFeuilleRoute getRelatedRoute(CoreSession session, DocumentModel doc) {
        // Retourne le document chargé si c'est une feuille de route
        SSFeuilleRoute relatedRoute = doc.getAdapter(SSFeuilleRoute.class);
        if (relatedRoute != null) {
            return relatedRoute;
        }

        // Si l'elément chargé est un élément de feuille de route, remonte à la
        // route
        FeuilleRouteElement relatedRouteElement = doc.getAdapter(FeuilleRouteElement.class);
        if (relatedRouteElement != null) {
            FeuilleRoute documentRoute = relatedRouteElement.getFeuilleRoute(session);
            if (documentRoute == null) {
                return null;
            }
            return documentRoute.getDocument().getAdapter(SSFeuilleRoute.class);
        }

        return null;
    }

    @Override
    public void libererVerrou(SpecificContext context) {
        DocumentModel currentDoc = context.getCurrentDocument();

        final STLockService stLockService = STServiceLocator.getSTLockService();

        try {
            stLockService.unlockDocUnrestricted(context.getSession(), currentDoc);
        } catch (NuxeoException e) {
            context.getMessageQueue().addWarnToQueue(getString("lock.action.unlock.error"));
            LOG.warn(STLogEnumImpl.FAIL_UNLOCK_DOC_TEC, e);
            return;
        }

        // Affiche un message d'information
        context.getMessageQueue().addInfoToQueue("lock.action.unlock.success");
    }

    @Override
    public void deleteModele(SpecificContext context) {
        DocumentModel doc = context.getCurrentDocument();
        CoreSession session = context.getSession();
        String modelName = doc.getTitle();
        session.removeDocument(doc.getRef());
        context.getMessageQueue().addInfoToQueue("admin.modele.action.delete.success");

        // Journaliser l'action
        STServiceLocator
            .getJournalService()
            .journaliserActionAdministration(
                session,
                SSEventConstant.DELETE_MODELE_FDR_EVENT,
                getString("admin.modele.message.suppression.journal", modelName)
            );
    }

    @Override
    public void requestValidateRouteModel(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();
        SSFeuilleRoute currentRouteModel = getRelatedRoute(session, doc);

        // Vérifie si le document est verrouillé par l'utilisateur en cours
        if (!LockUtils.isLockedByCurrentUser(session, doc.getRef())) {
            context.getMessageQueue().addWarnToQueue(getString(STLockActionServiceImpl.LOCK_LOST_ERROR_MSG));
            return;
        }

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, session);
        } catch (FeuilleRouteAlreadyLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_ALREADY_LOCKED));
            return;
        }
        try {
            documentRoutingService.requestValidateRouteModel(currentRouteModel, session, true);
        } catch (FeuilleRouteNotLockedException e) {
            context.getMessageQueue().addInfoToQueue(getString(MESSAGE_ROUTE_NOT_LOCKED));
            return;
        }

        try {
            documentRoutingService.sendValidationMail(session, currentRouteModel);
        } catch (Exception e) {
            context.getMessageQueue().addWarnToQueue(getString("admin.modele.action.requestValidation.mail.failed"));
        }

        documentRoutingService.unlockDocumentRoute(currentRouteModel, session);

        // Affiche un message d'information
        context.getMessageQueue().addInfoToQueue(getString("admin.modele.action.requestValidation.success"));
    }

    @Override
    public void cancelRequestValidateRouteModel(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel currentDocument = context.getCurrentDocument();
        SSFeuilleRoute currentRouteModel = getRelatedRoute(session, currentDocument);

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, session);
        } catch (FeuilleRouteAlreadyLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_ALREADY_LOCKED));
            return;
        }
        try {
            documentRoutingService.requestValidateRouteModel(currentRouteModel, session, false);
        } catch (FeuilleRouteNotLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_NOT_LOCKED));
            return;
        }
        documentRoutingService.unlockDocumentRoute(currentRouteModel, session);

        // Affiche un message d'information
        context.getMessageQueue().addInfoToQueue(getString("admin.modele.action.cancelRequestValidation.success"));
    }

    @Override
    public void validateRouteModel(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();
        SSFeuilleRoute currentRouteModel = getRelatedRoute(session, doc);

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, session);
        } catch (FeuilleRouteAlreadyLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_ALREADY_LOCKED));
            return;
        }
        try {
            documentRoutingService.validateRouteModel(currentRouteModel, session);
        } catch (FeuilleRouteNotLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_NOT_LOCKED));
            return;
        }

        // Affiche un message d'information
        context.getMessageQueue().addSuccessToQueue(getString("admin.modele.action.validated.success"));
    }

    @Override
    public void refuseValidateRouteModel(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();
        SSFeuilleRoute currentRouteModel = getRelatedRoute(session, doc);

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, session);
        } catch (FeuilleRouteAlreadyLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_ALREADY_LOCKED));
            return;
        }
        try {
            documentRoutingService.requestValidateRouteModel(currentRouteModel, session, false);
        } catch (FeuilleRouteNotLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_NOT_LOCKED));
            return;
        }

        // Affiche un message d'information
        context.getMessageQueue().addInfoToQueue(getString("admin.modele.action.refuseValidation.success"));
    }

    @Override
    public void invalidateRouteModel(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();
        SSFeuilleRoute currentRouteModel = getRelatedRoute(session, doc);

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            documentRoutingService.lockDocumentRoute(currentRouteModel, session);
        } catch (FeuilleRouteAlreadyLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_ALREADY_LOCKED));
            return;
        }
        try {
            documentRoutingService.invalidateRouteModel(currentRouteModel, session);
        } catch (FeuilleRouteNotLockedException e) {
            context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_NOT_LOCKED));
            return;
        }

        // Affiche un message d'information
        context.getMessageQueue().addInfoToQueue(getString("admin.modele.action.invalidated.success"));
    }

    @Override
    public DocumentModel duplicateRouteModel(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();

        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        final List<String> groups = ssPrincipal.getGroups();
        boolean isAdminFonctionnel = groups.contains(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_VALIDATOR);
        String ministere = null;
        if (!isAdminFonctionnel) {
            // Affecte la nouvelle feuille de route au premier ministère de cet
            // administrateur ministériel
            Set<String> ministereSet = ssPrincipal.getMinistereIdSet();
            if (ministereSet.isEmpty()) {
                throw new NuxeoException("Aucun ministère défini pour cet administrateur ministériel");
            }
            ministere = ministereSet.iterator().next();
        }

        FeuilleRoute newFeuilleRoute = documentRoutingService.duplicateRouteModel(session, doc, ministere);
        DocumentModel newDoc = newFeuilleRoute.getDocument();

        // Affiche un message d'information
        context.getMessageQueue().addInfoToQueue("admin.modele.message.dupliquer.success");

        return newDoc;
    }

    @Override
    public String getContentViewCriteriaSubstitution(SpecificContext context) {
        SubstitutionCriteria criteria = context.getFromContextData(SSContextDataKey.SUBSTITUTION_CRITERIA);

        StringBuilder str = new StringBuilder("fdr.")
            .append(STSchemaConstant.ECM_SCHEMA_PREFIX)
            .append(":")
            .append(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE)
            .append(" = '")
            .append(ElementLifeCycleState.validated.name())
            .append("'");
        if (CollectionUtils.isNotEmpty(criteria.getListIdMinistereAttributaire())) {
            str
                .append("AND fdr.")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA_PREFIX)
                .append(":")
                .append(SSFeuilleRouteConstant.FEUILLE_ROUTE_MINISTERE_PROPERTY)
                .append(" IN ")
                .append(
                    criteria.getListIdMinistereAttributaire().stream().collect(Collectors.joining("','", "('", "')"))
                );
        }
        return str.toString();
    }

    @Override
    public DocumentModel initFeuilleRoute(CoreSession session, ModeleFdrForm form, String creatorName) {
        String fdrName = form.getIntitule();
        DocumentModel docModel = session.createDocumentModel(
            SSConstant.FDR_FOLDER_PATH,
            form.getIntitule().replace("/", ""),
            SSConstant.FEUILLE_ROUTE_DOCUMENT_TYPE
        );
        DublincoreSchemaUtils.setTitle(docModel, fdrName);

        return docModel;
    }

    @Override
    public DocumentModel createDocument(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel newDocument = context.getCurrentDocument();

        SSFeuilleRoute route = newDocument.getAdapter(SSFeuilleRoute.class);

        // Nettoyage des données en entrée
        if (StringUtils.isBlank(route.getMinistere())) {
            route.setMinistere(null);
        }

        if (checkUniciteData(context, newDocument)) {
            return session.createDocument(route.getDocument());
        }
        return null;
    }

    protected boolean checkUniciteData(SpecificContext context, DocumentModel doc) {
        // Vérifie l'unicité de l'intitulé de modèle de feuille de route par
        // ministère
        final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        if (!feuilleRouteModelService.isIntituleUnique(context.getSession(), doc.getAdapter(SSFeuilleRoute.class))) {
            context.getMessageQueue().addErrorToQueue(getString("admin.modele.message.unicite.intitule.error"));
            return false;
        }
        return true;
    }

    @Override
    public DocumentModel updateDocument(SpecificContext context) {
        CoreSession session = context.getSession();
        DocumentModel changeableDocument = context.getCurrentDocument();

        SSFeuilleRoute route = changeableDocument.getAdapter(SSFeuilleRoute.class);

        // Vérifie si le document est verrouillé par l'utilisateur en cours
        if (!LockUtils.isLockedByCurrentUser(session, changeableDocument.getRef())) {
            context.getMessageQueue().addWarnToQueue(getString(STLockActionServiceImpl.LOCK_LOST_ERROR_MSG));
            return null;
        }

        // Nettoyage des données en entrée
        if (StringUtils.isBlank(route.getMinistere())) {
            route.setMinistere(null);
        }

        // Vérifie l'unicité de l'intitulé de modèle de feuille de route par
        // ministère
        final FeuilleRouteModelService feuilleRouteModelService = SSServiceLocator.getFeuilleRouteModelService();
        if (!feuilleRouteModelService.isIntituleUnique(session, route)) {
            context.getMessageQueue().addWarnToQueue(getString("admin.modele.message.unicite.intitule.error"));
            return null;
        }

        return session.saveDocument(route.getDocument());
    }

    @Override
    public String getFeuilleRouteModelFolderId(CoreSession session) {
        return getFeuilleRouteModelService().getFeuilleRouteModelFolder(session).getId();
    }

    @Override
    public boolean isAccessAuthorized(CoreSession session) {
        SSPrincipal ssPrincipal = (SSPrincipal) session.getPrincipal();
        return (
            ssPrincipal.isAdministrator() ||
            ssPrincipal.isMemberOf(STBaseFunctionConstant.FEUILLE_DE_ROUTE_MODEL_UDPATER)
        );
    }

    @Override
    public void deleteMultipleStepsFromRoute(SpecificContext context) {
        CoreSession session = context.getSession();
        List<String> idModeles = context.getFromContextData(SSContextDataKey.ID_MODELES);
        String etapeType = context.getFromContextData(SSContextDataKey.TYPE_ETAPE);
        String etapePoste = context.getFromContextData(SSContextDataKey.ID_POSTE);

        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        try {
            for (String idModele : idModeles) {
                DocumentModel docModel = session.getDocument(new IdRef(idModele));

                List<DocumentModel> listStepASupprimer = getStepsToDelete(session, etapeType, etapePoste, docModel);

                if (!listStepASupprimer.isEmpty()) {
                    FeuilleRouteElement relatedRouteElement = docModel.getAdapter(FeuilleRouteElement.class);
                    SSFeuilleRoute documentRoute = relatedRouteElement.getFeuilleRoute(session);
                    try {
                        final STLockService stLockService = STServiceLocator.getSTLockService();
                        if (LockUtils.isLockedByAnotherUser(session, docModel.getRef())) {
                            stLockService.unlockDocUnrestricted(session, docModel);
                        }
                        if (!LockUtils.isLocked(session, docModel.getRef())) {
                            stLockService.lockDoc(session, docModel);
                        }
                    } catch (NuxeoException ce) {
                        // là on met une erreur dans les logs et on passe à la fdr suivante
                        final String message = "Impossible de verrouiller la feuille de route " + docModel.getId();
                        context.getMessageQueue().addErrorToQueue(message);
                        LOG.error(STLogEnumImpl.FAIL_LOG_TEC, ce);
                        break;
                    }

                    removeSteps(context, session, documentRoutingService, documentRoute, listStepASupprimer);

                    // Message de succès les étapes sont supprimé.
                    context.getMessageQueue().addSuccessToQueue(getString("modeleFDR.suppression.etape.success"));
                    // Passage de la feuille de route modifiée à l'état
                    // brouillon
                    try {
                        if (documentRoute.isValidated()) {
                            documentRoutingService.invalidateRouteModel(documentRoute, session);
                        }
                    } catch (FeuilleRouteNotLockedException e) {
                        context.getMessageQueue().addWarnToQueue(getString(MESSAGE_ROUTE_NOT_LOCKED));
                        LOG.warn(STLogEnumImpl.FAIL_UPDATE_FDR, e);
                        break;
                    }
                    try {
                        documentRoutingService.unlockDocumentRoute(documentRoute, session);
                    } catch (NuxeoException ex) {
                        LOG.error(STLogEnumImpl.FAIL_UNLOCK_DOC_TEC, ex);
                        break;
                    }
                }
            }
        } catch (NuxeoException e) {
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            LOG.error(STLogEnumImpl.FAIL_DEL_STEP_TEC, e);
        }
    }

    private List<DocumentModel> getStepsToDelete(
        CoreSession session,
        String etapeType,
        String etapePoste,
        DocumentModel docModel
    ) {
        // Résupérer les étapes de la fdr
        List<DocumentModel> allElementsFDR = getStepsFromModele(session, docModel);
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        List<DocumentModel> listStepASupprimer = new ArrayList<>();
        for (final DocumentModel stepModel : allElementsFDR) {
            // On vérifie avant tout que l'étape de FDR correspond bien à la FDR courante
            // Permet de corriger des instabilités lors de la recherche d'étapes de la FDR
            SSRouteStep step = stepModel.getAdapter(SSRouteStep.class);
            if (step != null && step.getFeuilleRoute(session).getDocument().getId().equals(docModel.getId())) {
                String posteId = mailboxPosteService.getPosteIdFromMailboxId(step.getDistributionMailboxId());
                if (posteId.equals(etapePoste) && step.getType().equals(etapeType)) {
                    // Ajout de l'étape concernée à la liste de
                    // suppression
                    listStepASupprimer.add(stepModel);
                }
            }
        }
        return listStepASupprimer;
    }

    /**
     * Permet de supprimer l'ensemble des étapes listé dans la feuille de route courante
     *
     */
    private void removeSteps(
        SpecificContext context,
        CoreSession session,
        final DocumentRoutingService documentRoutingService,
        SSFeuilleRoute documentRoute,
        List<DocumentModel> listStepASupprimer
    ) {
        try {
            listStepASupprimer.forEach(
                stepDelete ->
                    documentRoutingService.removeRouteElement(stepDelete.getAdapter(FeuilleRouteElement.class), session)
            );
        } catch (NuxeoException e) {
            context.getMessageQueue().addErrorToQueue(e.getMessage());
            LOG.error(STLogEnumImpl.FAIL_DEL_STEP_TEC, e);
            try {
                documentRoutingService.unlockDocumentRoute(documentRoute, session);
            } catch (NuxeoException ex) {
                LOG.error(STLogEnumImpl.FAIL_UNLOCK_DOC_TEC, ex);
                return;
            }
            throw e;
        }
    }

    @Override
    public List<ModeleFdrEtapeSupprimeForm> listStepsToDelete(SpecificContext context) {
        CoreSession session = context.getSession();
        List<String> idModeles = context.getFromContextData(SSContextDataKey.ID_MODELES);
        String etapeType = context.getFromContextData(SSContextDataKey.TYPE_ETAPE);
        String etapePoste = context.getFromContextData(SSContextDataKey.ID_POSTE);

        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();

        List<ModeleFdrEtapeSupprimeForm> listModeleFdrEtapeSupprForm = new ArrayList<>();

        for (String idModele : idModeles) {
            DocumentModel docModel = session.getDocument(new IdRef(idModele));
            // De là on va essayer de récupérer la feuille de route
            // correspondante
            FeuilleRouteElement relatedRouteElement = docModel.getAdapter(FeuilleRouteElement.class);
            FeuilleRoute documentRoute = relatedRouteElement.getFeuilleRoute(session);
            // Résupérer les étapes de la fdr
            List<DocumentModel> allElementsFDR = getStepsFromModele(session, docModel);

            int nbStepToDeleteInFdr = 0;
            boolean isOneStepParallel = false;

            for (final DocumentModel stepModel : allElementsFDR) {
                // On vérifie avant tout que l'étape de FDR correspond bien à la
                // FDR courante
                // Permet de corriger des instabilités lors de la recherche
                // d'étapes de la FDR
                SSRouteStep step = stepModel.getAdapter(SSRouteStep.class);
                if (step != null && step.getFeuilleRoute(session).getDocument().getId().equals(docModel.getId())) {
                    String posteId = mailboxPosteService.getPosteIdFromMailboxId(step.getDistributionMailboxId());
                    if (posteId.equals(etapePoste) && step.getType().equals(etapeType)) {
                        // Ajout de l'étape concernée à la liste de suppression
                        nbStepToDeleteInFdr++;
                        if (isStepInPrallelBranch(context, stepModel)) {
                            isOneStepParallel = true;
                        }
                    }
                }
            }

            if (nbStepToDeleteInFdr > 0) {
                ModeleFdrEtapeSupprimeForm modeleFdrEtapeSupprimeForm = new ModeleFdrEtapeSupprimeForm();
                modeleFdrEtapeSupprimeForm.setIntituleModele(documentRoute.getName());
                modeleFdrEtapeSupprimeForm.setNbEtapesASupprimer(nbStepToDeleteInFdr);
                modeleFdrEtapeSupprimeForm.setIsParallele(isOneStepParallel);
                listModeleFdrEtapeSupprForm.add(modeleFdrEtapeSupprimeForm);
            }
        }
        return listModeleFdrEtapeSupprForm;
    }

    private boolean isStepInPrallelBranch(SpecificContext context, DocumentModel stepDoc) {
        CoreSession session = context.getSession();
        DocumentModel parentDoc = session.getDocument(stepDoc.getParentRef());
        if (FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER.equals(parentDoc.getType())) {
            StepFolder folder = parentDoc.getAdapter(StepFolder.class);
            if (folder.isParallel()) {
                return true;
            } else {
                return isStepInPrallelBranch(context, parentDoc);
            }
        }
        return false;
    }

    private List<DocumentModel> getStepsFromModele(CoreSession session, DocumentModel docModel) {
        // Ensuite on récupère les étapes de la feuille de route
        final DocumentRoutingService documentRoutingService = SSServiceLocator.getDocumentRoutingService();
        // Nouvelle méthode pour rechercher toutes les étapes du modèle de
        // FDR
        final SSFeuilleRoute feuilleRoute = docModel.getAdapter(SSFeuilleRoute.class);
        final List<RouteTableElement> listRouteTableElement = documentRoutingService.getFeuilleRouteElements(
            feuilleRoute,
            session
        );
        // Construction du tableau qui contient les étapes de FDR
        return listRouteTableElement
            .stream()
            .filter(routeTableElement -> !routeTableElement.getDocument().isFolder())
            .map(routeTableElement -> routeTableElement.getDocument().getAdapter(SSRouteStep.class))
            .map(FeuilleRouteElement::getDocument)
            .collect(Collectors.toList());
    }
}
