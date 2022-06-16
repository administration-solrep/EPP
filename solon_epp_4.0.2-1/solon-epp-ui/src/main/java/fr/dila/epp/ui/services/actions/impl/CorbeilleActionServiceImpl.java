package fr.dila.epp.ui.services.actions.impl;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_MESSAGE;
import static fr.dila.st.ui.enums.STContextDataKey.ID;

import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.services.actions.CorbeilleActionService;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.solonepp.core.dao.MessageDao;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.VersionSelectDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class CorbeilleActionServiceImpl implements CorbeilleActionService {
    public static final String MESSAGE_CRITERIA_FROM_RECHERCHE = "messageCriteriaFromRecherche";
    public static final String EXTEND_MESSAGE = "extendMessage";

    private static final String PIECE_JOINTE_ERREUR_RECUPERATION = "piece.jointe.erreur.recuperation";

    private static final STLogger LOGGER = STLogFactory.getLog(CorbeilleActionServiceImpl.class);

    @Override
    public String getMessageListQueryString(SpecificContext context) {
        MessageDao messageDao = getMessageListDao(context);
        return messageDao.getQueryString();
    }

    @Override
    public List<Object> getMessageListQueryParameter(SpecificContext context) {
        MessageDao messageDao = getMessageListDao(context);
        return messageDao.getParamList();
    }

    /**
     * Retourne le message dao.
     *
     * @return MessageDao
     */
    private MessageDao getMessageListDao(SpecificContext context) {
        MessageCriteria messageCriteria = null;
        MessageCriteria messageCriteriaFromRecherche = context.getFromContextData(MESSAGE_CRITERIA_FROM_RECHERCHE);
        boolean extendMessage = context.containsKeyInContextData(EXTEND_MESSAGE)
            ? context.getFromContextData(EXTEND_MESSAGE)
            : false;
        String corbeilleId = context.getFromContextData(ID);
        if (messageCriteriaFromRecherche == null) {
            messageCriteria = new MessageCriteria();
            messageCriteria.setCheckReadPermission(true);
            messageCriteria.setCorbeilleMessageType(true);

            if (extendMessage) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.add(Calendar.DAY_OF_MONTH, -20);
                messageCriteria.setDateTraitementMin(cal);
            }
        } else {
            messageCriteria = messageCriteriaFromRecherche;
        }
        // on met l'id de la corbeille sélectionnée
        if (corbeilleId != null) {
            messageCriteria.setCorbeille(corbeilleId);
        } else {
            context.getMessageQueue().addInfoToQueue("Vous devez sélectionner une corbeille");
        }
        messageCriteria.setJoinEvenementForSorting(true);
        messageCriteria.setJoinVersionForSorting(true);

        return new MessageDao(context.getSession(), messageCriteria);
    }

    @Override
    public List<VersionSelectDTO> getallVersions(SpecificContext context) {
        Message currentMessage = context.getFromContextData(CURRENT_MESSAGE);
        return SolonEppServiceLocator
            .getVersionService()
            .findVersionSelectionnable(
                context.getSession(),
                context.getCurrentDocument(),
                currentMessage.getMessageType()
            )
            .stream()
            .map(
                apiDTO ->
                    new VersionSelectDTO(
                        apiDTO.getTitle(),
                        apiDTO.getId(),
                        apiDTO.isEtatRejete(),
                        apiDTO.isAccuserReception(),
                        apiDTO.getDateArAsString()
                    )
            )
            .collect(Collectors.toList());
    }

    @Override
    public DocumentModel getSelectedVersion(SpecificContext context) {
        String versionNum = context.getFromContextData(EppContextDataKey.VERSION_ID);
        CoreSession session = context.getSession();
        if (StringUtils.isNotBlank(versionNum)) {
            return SolonEppServiceLocator.getVersionService().findVersionByUIID(session, versionNum);
        }

        // On récupère la dernière version action du document lorsqu'on affiche un nouveau document
        Message currentMessage = context.getFromContextData(CURRENT_MESSAGE);
        DocumentModel evenementDoc = context.getCurrentDocument();
        return SolonEppServiceLocator
            .getVersionService()
            .getVersionActive(session, evenementDoc, currentMessage.getMessageType());
    }

    @Override
    public Set<String> getListTypePieceJointe(SpecificContext context) {
        return getMapTypePieceJointe(context).keySet();
    }

    @Override
    public Map<String, PieceJointeDescriptor> getMapTypePieceJointe(SpecificContext context) {
        DocumentModel evenementDoc = context.getCurrentDocument();
        try {
            Evenement evenement = evenementDoc.getAdapter(Evenement.class);
            return SolonEppServiceLocator
                .getEvenementTypeService()
                .getEvenementType(evenement.getTypeEvenement())
                .getOrderedPieceJointe();
        } catch (Exception e) {
            String message = ResourceHelper.getString(PIECE_JOINTE_ERREUR_RECUPERATION);
            LOGGER.error(context.getSession(), STLogEnumImpl.FAIL_GET_PIECE_JOINTE_TEC, message, e);
            context.getMessageQueue().addWarnToQueue(message);
        }
        return Collections.emptyMap();
    }
}
