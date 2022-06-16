package fr.dila.ss.ui.services.impl;

import static fr.dila.st.core.service.STServiceLocator.getOrganigrammeService;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.core.enumeration.StatutModeleFDR;
import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.LockUtils;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;

public abstract class SSAbstractModeleFdrFicheUIService<S extends ModeleFdrForm, T extends SSFeuilleRoute> {

    public S getModeleFdrForm(SpecificContext context, S form) {
        CoreSession session = context.getSession();
        DocumentModel doc = context.getCurrentDocument();

        if (
            !LockUtils.isLocked(session, doc.getRef()) &&
            SSActionsServiceLocator.getModeleFeuilleRouteActionService().canUserLockRoute(context) &&
            FeuilleRouteElement.ElementLifeCycleState.draft.name().equals(doc.getCurrentLifeCycleState()) &&
            !doc.getAdapter(SSFeuilleRoute.class).isDemandeValidation()
        ) {
            session.setLock(doc.getRef());
        }
        return convertDocToModeleForm(context, doc, form);
    }

    public S consultModeleSubstitution(SpecificContext context, S form) {
        DocumentModel doc = context.getCurrentDocument();
        return convertDocToModeleForm(context, doc, form);
    }

    protected S convertDocToModeleForm(SpecificContext context, DocumentModel doc, S modeleForm) {
        T modele = getFeuilleRouteAdapter(doc);

        modeleForm.setId(doc.getId());
        modeleForm.setIntitule(modele.getTitle());
        modeleForm.setIdMinistere(modele.getMinistere());
        modeleForm.setLibelleMinistere(
            StringUtils.isNotEmpty(modele.getMinistere())
                ? getOrganigrammeService()
                    .getOrganigrammeNodeById(modele.getMinistere(), OrganigrammeType.MINISTERE)
                    .getLabel()
                : ""
        );
        if (modeleForm.getIdMinistere() != null) {
            modeleForm.setMapMinistere(
                Collections.singletonMap(
                    modeleForm.getIdMinistere(),
                    STServiceLocator
                        .getOrganigrammeService()
                        .getOrganigrammeNodeById(modeleForm.getIdMinistere(), OrganigrammeType.MINISTERE)
                        .getLabel()
                )
            );
        }
        modeleForm.setModeleParDefaut(modele.isFeuilleRouteDefaut());
        modeleForm.setDescription(modele.getDescription());
        modeleForm.setEtat(StatutModeleFDR.getStatutFromDoc(doc));
        CoreSession session = context.getSession();
        modeleForm.setIsLock(LockUtils.isLocked(session, doc.getRef()));
        modeleForm.setIsLockByCurrentUser(LockUtils.isLockedByCurrentUser(session, doc.getRef()));
        modeleForm.setFdrDto(getFeuileRouteModele(context));
        return modeleForm;
    }

    public FdrDTO getFeuileRouteModele(SpecificContext context) {
        context.putInContextData(STContextDataKey.ID, context.getCurrentDocument().getId());

        FdrDTO dto = new FdrDTO();
        dto.setTable(SSUIServiceLocator.getSSFeuilleRouteUIService().getFeuilleRouteDTO(context));
        dto.buildColonnesFdrModele();

        dto.setTabActions(context.getActions(SSActionCategory.MODELE_FDR_STEP_ACTIONS));
        return dto;
    }

    public FdrDTO getFeuileRouteModeleSubstitution(SpecificContext context) {
        context.putInContextData(STContextDataKey.ID, context.getCurrentDocument().getId());

        FdrDTO dto = new FdrDTO();
        dto.setTable(SSUIServiceLocator.getSSFeuilleRouteUIService().getFeuilleRouteDTO(context));
        dto.buildColonnesFdrModeleSubstitution();
        return dto;
    }

    public void updateModele(SpecificContext context, S modeleForm) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();

        CoreSession session = context.getSession();
        DocumentModel doc = session.getDocument(new IdRef(modeleForm.getId()));

        if (modeleAction.canUserModifyRoute(context)) {
            context.setCurrentDocument(doc);
            T route = convertFormToFeuilleRoute(modeleForm, getFeuilleRouteAdapter(doc));
            context.setCurrentDocument(route.getDocument());
            modeleAction.updateDocument(context);
            if (context.getMessageQueue().getErrorQueue().isEmpty()) {
                context
                    .getMessageQueue()
                    .addSuccessToQueue(ResourceHelper.getString("admin.modele.message.success.saveForm"));
            }
        } else {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("admin.modele.message.error.right"));
        }
    }

    public S createModele(SpecificContext context, S modeleForm) {
        ModeleFeuilleRouteActionService modeleAction = SSActionsServiceLocator.getModeleFeuilleRouteActionService();

        CoreSession session = context.getSession();

        if (modeleAction.canUserCreateRoute(context)) {
            DocumentModel modele = modeleAction.initFeuilleRoute(session, modeleForm, session.getPrincipal().getName());
            T repModele = convertFormToFeuilleRoute(modeleForm, getFeuilleRouteAdapter(modele));
            context.setCurrentDocument(repModele.getDocument());
            DocumentModel newRouteDoc = modeleAction.createDocument(context);
            if (context.getMessageQueue().getErrorQueue().isEmpty()) {
                modeleForm.setId(newRouteDoc.getId());
                context
                    .getMessageQueue()
                    .addSuccessToQueue(ResourceHelper.getString("admin.modele.message.success.createModele"));
            }
        } else {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("admin.modele.message.error.right"));
        }

        return modeleForm;
    }

    protected T convertFormToFeuilleRoute(S modeleForm, T modele) {
        DocumentModel doc = modele.getDocument();
        MapDoc2Bean.beanToDoc(modeleForm, doc);
        return getFeuilleRouteAdapter(doc);
    }

    @SuppressWarnings("unchecked")
    protected final T getFeuilleRouteAdapter(DocumentModel doc) {
        return (T) doc.getAdapter(SSFeuilleRoute.class);
    }
}
