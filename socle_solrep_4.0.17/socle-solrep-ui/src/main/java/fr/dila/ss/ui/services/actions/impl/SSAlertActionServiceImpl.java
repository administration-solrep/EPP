package fr.dila.ss.ui.services.actions.impl;

import fr.dila.ss.ui.bean.AlerteForm;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.actions.SSAlertActionService;
import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STAlertService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.services.actions.impl.STAlertActionServiceImpl;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class SSAlertActionServiceImpl extends STAlertActionServiceImpl implements SSAlertActionService {
    private static final STLogger LOGGER = STLogFactory.getLog(SSAlertActionServiceImpl.class);

    @Override
    public AlerteForm getNewAlertFromRequeteExperte(SpecificContext context) {
        DocumentModel requeteDoc = context.getCurrentDocument();
        AlerteForm alert = new AlerteForm();
        if (requeteDoc != null) {
            alert.setIsActivated(true);
            alert.setIdRequete(requeteDoc.getId());
            String requeteTitle = "requête";
            try {
                requeteTitle = requeteDoc.getTitle();
            } catch (NuxeoException ce) {
                LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_GET_INFORMATION_TEC, requeteDoc, ce);
            }
            alert.setTitre("Alerte sur " + requeteTitle);
        }
        return alert;
    }

    @Override
    public String saveAlert(SpecificContext context) {
        AlerteForm form = context.getFromContextData(SSContextDataKey.ALERTE_FORM);
        String result = StringUtils.EMPTY;
        try {
            CoreSession session = context.getSession();

            boolean isCreation = Objects.isNull(context.getCurrentDocument());

            result =
                Optional
                    .ofNullable(context.getCurrentDocument())
                    .map(doc -> updateAlerte(session, doc, form))
                    .orElseGet(() -> createAlerte(session, form));

            context
                .getMessageQueue()
                .addToastSuccess(isCreation ? "suivi.alerte.creation.success" : "suivi.alerte.modification.success");
        } catch (Exception e) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("suivi.erreur.creation.alerte"));
            LOGGER.error(
                context.getSession(),
                STLogEnumImpl.FAIL_UPDATE_ALERT_TEC,
                ResourceHelper.getString("suivi.erreur.creation.alerte"),
                e
            );
        }

        return result;
    }

    private static String createAlerte(CoreSession session, AlerteForm form) {
        String parentPath = STServiceLocator
            .getUserWorkspaceService()
            .getCurrentUserPersonalWorkspace(session)
            .getPathAsString();

        DocumentModel doc = session.createDocumentModel(
            parentPath,
            form.getTitre(),
            STAlertConstant.ALERT_DOCUMENT_TYPE
        );

        MapDoc2Bean.beanToDoc(form, doc);
        doc = session.createDocument(doc);
        Alert alert = doc.getAdapter(Alert.class);
        alert.setIsActivated(true);
        return session.saveDocument(doc).getId();
    }

    private static String updateAlerte(CoreSession session, DocumentModel doc, AlerteForm form) {
        MapDoc2Bean.beanToDoc(form, doc);
        return session.saveDocument(doc).getId();
    }

    @Override
    public void delete(SpecificContext context) {
        final STAlertService alertService = STServiceLocator.getAlertService();
        if (!alertService.deleteAlert(context.getSession(), context.getCurrentDocument())) {
            LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_DEL_ALERT_FONC);
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("alert.error.alertDeletion"));
        } else {
            context.getMessageQueue().addToastSuccess(ResourceHelper.getString("suivi.alerte.supprimee"));
        }
    }

    @Override
    public AlerteForm getCurrentAlerteForm(SpecificContext context) {
        AlerteForm form = MapDoc2Bean.docToBean(context.getCurrentDocument(), AlerteForm.class);
        form.setMapDestinataires(fetchUserLabels(form.getDestinataires()));
        return form;
    }

    private Map<String, String> fetchUserLabels(List<String> userIds) {
        return userIds
            .stream()
            .collect(
                Collectors.toMap(Function.identity(), STServiceLocator.getSTUserService()::getUserFullNameWithUsername)
            );
    }
}
