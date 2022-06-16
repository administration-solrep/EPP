package fr.dila.epp.ui.services.impl;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.MESSAGE_LIST_FORM;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.RAPID_SEARCH_FORM;
import static fr.dila.epp.ui.services.actions.impl.CorbeilleActionServiceImpl.MESSAGE_CRITERIA_FROM_RECHERCHE;
import static fr.dila.st.ui.enums.STContextDataKey.ID;

import fr.dila.epp.ui.bean.MessageList;
import fr.dila.epp.ui.contentview.CorbeillePageProvider;
import fr.dila.epp.ui.helper.MessageListHelper;
import fr.dila.epp.ui.services.MessageListUIService;
import fr.dila.epp.ui.services.actions.CorbeilleActionService;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.epp.ui.th.bean.MessageListForm;
import fr.dila.epp.ui.th.bean.RapidSearchForm;
import fr.dila.solonepp.api.dao.criteria.MessageCriteria;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class MessageListUIServiceImpl implements MessageListUIService {
    private static final STLogger LOG = STLogFactory.getLog(MessageListUIServiceImpl.class);

    @Override
    public MessageList getMessageListForCorbeille(SpecificContext context) {
        MessageListForm messageListForm = context.getFromContextData(MESSAGE_LIST_FORM);

        if (messageListForm == null) {
            messageListForm = new MessageListForm();
        }

        RapidSearchForm rapidSearchForm = context.getFromContextData(RAPID_SEARCH_FORM);
        if (rapidSearchForm != null) {
            context.putInContextData(
                MESSAGE_CRITERIA_FROM_RECHERCHE,
                buildMessageCriteriaFromRecherche(rapidSearchForm)
            );
        }

        CorbeilleActionService corbeilleActionService = SolonEppActionsServiceLocator.getCorbeilleActionService();
        String query = corbeilleActionService.getMessageListQueryString(context);
        List<Object> paramList = corbeilleActionService.getMessageListQueryParameter(context);
        paramList.replaceAll(
            o ->
                o instanceof GregorianCalendar
                    ? String.format(
                        "TIMESTAMP '%s'",
                        SolonDateConverter.DATETIME_DASH_REVERSE_T_SECOND_COLON_Z.format((GregorianCalendar) o, true)
                    )
                    : o instanceof String ? "'" + o + "'" : o
        );

        CorbeillePageProvider provider = messageListForm.getPageProvider(
            context.getSession(),
            "corbeillePageProvider",
            paramList
        );
        provider.getDefinition().setPattern(query);

        List<Map<String, Serializable>> docList = provider.getCurrentPage();

        LOG.debug(
            EppLogEnumImpl.FAIL_GET_MESSAGE_TEC,
            provider.getResultsCount() + " messages trouvés dans cette corbeille"
        );

        MessageList messageList = MessageListHelper.buildMessageList(
            docList,
            messageListForm,
            (int) provider.getResultsCount()
        );

        messageList.setTitre(getCurrentCorbeille(context.getFromContextData(ID), context));

        return messageList;
    }

    private String getCurrentCorbeille(String corbeilleId, SpecificContext context) {
        EppPrincipal principal = (EppPrincipal) context.getSession().getPrincipal();
        List<CorbeilleNode> lstCorbeilles = SolonEppServiceLocator
            .getCorbeilleTypeService()
            .getCorbeilleInstitutionTree(principal.getInstitutionId());

        return findCorbeille(lstCorbeilles, corbeilleId);
    }

    private String findCorbeille(List<CorbeilleNode> lstCorbeilles, String corbeilleId) {
        String corbeilleLabel = "";
        for (CorbeilleNode corbeille : lstCorbeilles) {
            if (corbeille.getName().equals(corbeilleId)) {
                return corbeille.getLabel();
            } else {
                if (CollectionUtils.isNotEmpty(corbeille.getCorbeilleNodeList())) {
                    String sousLabel = findCorbeille(corbeille.getCorbeilleNodeList(), corbeilleId);
                    if (StringUtils.isNotBlank(sousLabel)) {
                        return corbeille.getLabel() + " : " + sousLabel;
                    }
                }
            }
        }
        return corbeilleLabel;
    }

    private MessageCriteria buildMessageCriteriaFromRecherche(RapidSearchForm rapidSearchForm) {
        MessageCriteria messageCriteria = new MessageCriteria();
        messageCriteria.setCheckReadPermission(true);
        messageCriteria.setCorbeilleMessageType(true);

        if (CollectionUtils.isNotEmpty(rapidSearchForm.getTypeCommunication())) {
            messageCriteria.setEvenementTypeIn(rapidSearchForm.getTypeCommunication());
        }

        if (StringUtils.isNotBlank(rapidSearchForm.getIdDossier())) {
            messageCriteria.setDossierId(rapidSearchForm.getIdDossier());
        }

        if (StringUtils.isNotBlank(rapidSearchForm.getObjetDossier())) {
            messageCriteria.setVersionObjetLike(rapidSearchForm.getObjetDossier());
        }

        if (StringUtils.isNotBlank(rapidSearchForm.getIdCommunication())) {
            messageCriteria.setEvenementId(rapidSearchForm.getIdCommunication());
        }

        if (StringUtils.isNotBlank(rapidSearchForm.getDateDebut())) {
            messageCriteria.setVersionHorodatageMin(
                SolonDateConverter.DATE_SLASH.parseToCalendar(rapidSearchForm.getDateDebut())
            );
        }

        if (StringUtils.isNotBlank(rapidSearchForm.getDateFin())) {
            messageCriteria.setVersionHorodatageMax(
                SolonDateConverter.DATE_SLASH.parseToCalendar(rapidSearchForm.getDateFin())
            );
            messageCriteria.getVersionHorodatageMax().add(Calendar.DAY_OF_MONTH, 1);
        }

        if (CollectionUtils.isNotEmpty(rapidSearchForm.getEmetteur())) {
            messageCriteria.setEvenementEmetteurIn(rapidSearchForm.getEmetteur());
        }

        if (CollectionUtils.isNotEmpty(rapidSearchForm.getDestinataire())) {
            messageCriteria.setEvenementDestinataireIn(rapidSearchForm.getDestinataire());
        }

        if (CollectionUtils.isNotEmpty(rapidSearchForm.getCopie())) {
            messageCriteria.setEvenementDestinataireCopieIn(rapidSearchForm.getCopie());
        }

        return messageCriteria;
    }
}
