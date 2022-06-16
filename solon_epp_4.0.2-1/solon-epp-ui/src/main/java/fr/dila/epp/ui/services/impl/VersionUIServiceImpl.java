package fr.dila.epp.ui.services.impl;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_MESSAGE;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_VERSION;
import static fr.dila.st.ui.enums.STContextDataKey.ID;

import fr.dila.epp.ui.services.VersionUIService;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.service.VersionActionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public class VersionUIServiceImpl implements VersionUIService {
    private static final STLogger LOGGER = STLogFactory.getLog(VersionUIService.class);

    @Override
    public List<String> getActionList(SpecificContext context) {
        VersionActionService versionActionService = SolonEppServiceLocator.getVersionActionService();

        DocumentModel evenmentDoc = context.getCurrentDocument();
        Message message = context.getFromContextData(CURRENT_MESSAGE);
        Version version = context.getFromContextData(CURRENT_VERSION);
        List<String> actionStringList = new ArrayList<>();
        if (message != null && version != null) {
            actionStringList =
                versionActionService.findActionPossible(
                    context.getSession(),
                    evenmentDoc,
                    version.getDocument(),
                    message.getMessageType(),
                    message.getEtatMessage(),
                    true
                );
        }

        return actionStringList;
    }

    @Override
    public boolean isActionPossible(SpecificContext context) {
        VersionActionService versionActionService = SolonEppServiceLocator.getVersionActionService();

        DocumentModel evenmentDoc = context.getCurrentDocument();
        Message message = context.getFromContextData(CURRENT_MESSAGE);
        Version version = context.getFromContextData(CURRENT_VERSION);
        List<String> actionStringList;
        if (message == null || version == null) {
            LOGGER.warn(context.getSession(), EppLogEnumImpl.FAIL_GET_VERSION_FONC);
            LOGGER.warn(context.getSession(), EppLogEnumImpl.FAIL_GET_MESSAGE_FONC);
            return false;
        } else {
            actionStringList =
                versionActionService.findActionPossible(
                    context.getSession(),
                    evenmentDoc,
                    version.getDocument(),
                    message.getMessageType(),
                    message.getEtatMessage(),
                    true
                );
        }
        return actionStringList.contains(context.getFromContextData(ID));
    }

    @Override
    public String getConfirmMessageAccepter(SpecificContext context) {
        Version version = context.getFromContextData(CURRENT_VERSION);
        if (
            version != null &&
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(
                version.getModeCreation()
            )
        ) {
            return "label.version.confirm.annulation";
        } else if (
            version != null &&
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE.equals(
                version.getModeCreation()
            )
        ) {
            return "label.version.confirm.rectification";
        }
        return "";
    }

    @Override
    public String getConfirmMessageRejeter(SpecificContext context) {
        Version version = context.getFromContextData(CURRENT_VERSION);
        if (
            version != null &&
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(
                version.getModeCreation()
            )
        ) {
            return "label.version.rejeter.annulation";
        } else if (
            version != null &&
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE.equals(
                version.getModeCreation()
            )
        ) {
            return "label.version.rejeter.rectification";
        }
        return "";
    }

    @Override
    public String getConfirmMessageAbandonner(SpecificContext context) {
        Version version = context.getFromContextData(CURRENT_VERSION);
        if (
            version != null &&
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_ANNULATION_VALUE.equals(
                version.getModeCreation()
            )
        ) {
            return "label.version.confirm.abandonner.annulation";
        } else if (
            version != null &&
            SolonEppSchemaConstant.VERSION_MODE_CREATION_PUBLIE_DEMANDE_RECTIFICATION_VALUE.equals(
                version.getModeCreation()
            )
        ) {
            return "label.version.confirm.abandonner.rectification";
        }
        return "";
    }
}
