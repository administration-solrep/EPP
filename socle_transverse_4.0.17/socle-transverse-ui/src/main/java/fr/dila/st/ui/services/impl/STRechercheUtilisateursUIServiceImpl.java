package fr.dila.st.ui.services.impl;

import static fr.dila.st.ui.enums.STContextDataKey.USERS_LIST_FORM;
import static fr.dila.st.ui.services.STUIServiceLocator.getSTUserManagerUIService;
import static fr.dila.st.ui.services.STUIServiceLocator.getSTUtilisateursUIService;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STRechercheExportEventConstants;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.requeteur.RequeteurRechercheUtilisateurs;
import fr.dila.st.core.requeteur.RequeteurRechercheUtilisateurs.Builder;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.BlobUtils;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STRechercheUtilisateursUIService;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.th.bean.MailForm;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UserSearchForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.enums.AlertType;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.automation.server.jaxrs.batch.Batch;
import org.nuxeo.ecm.automation.server.jaxrs.batch.BatchManager;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.impl.blob.AbstractBlob;
import org.nuxeo.ecm.core.event.impl.InlineEventContext;
import org.nuxeo.ecm.platform.usermanager.UserManager;

public class STRechercheUtilisateursUIServiceImpl implements STRechercheUtilisateursUIService {
    private static final STLogger LOGGER = STLogFactory.getLog(STRechercheUtilisateursUIServiceImpl.class);

    /* Comparator for sorting the list by DateConnexion */
    private static final Comparator<UserForm> ORDER_BY_DATE_CONNEXION = (user1, user2) -> {
        Calendar date1 = user1.getDateConnexion();
        Calendar date2 = user2.getDateConnexion();

        if (date1 == null) {
            return -1;
        } else if (date2 == null) {
            return 1;
        } else {
            return date1.compareTo(date2);
        }
    };

    /* Comparator for sorting the list by Ministeres */
    private static final Comparator<UserForm> ORDER_BY_MINISTERES = (user1, user2) -> {
        String ministeres1 = user1.getMinisteres();
        String ministeres2 = user2.getMinisteres();

        if (ministeres1 == null) {
            return -1;
        } else if (ministeres2 == null) {
            return 1;
        } else {
            return ministeres1.compareTo(ministeres2);
        }
    };

    /**
     * Nom du paramètre de session permettant de stocker la liste des identifiants des utilisateurs à qui on va envoyer un email.
     */
    public static final String SESSION_PARAM_RECIPIENT_IDS = "recipient_ids";
    public static final String OK_SEND_MAIL = "mail.envoi.effectuee";

    @Override
    public STUsersList searchUsers(SpecificContext context) {
        RequeteurRechercheUtilisateurs requeteur = getUserRequeteur(context);
        Boolean fullUser = context.getFromContextData(STContextDataKey.GET_FULL_USER);
        List<DocumentModel> userDocList = requeteur.execute();

        STUsersList usersList = new STUsersList();
        List<UserForm> userFormList = getUserFormListe(context, userDocList, fullUser);
        sortUserFormListIfNeeded(context, userFormList);
        usersList.setListe(userFormList);
        return usersList;
    }

    private List<UserForm> sortUserFormListIfNeeded(SpecificContext context, List<UserForm> userFormList) {
        UsersListForm form = context.getFromContextData(USERS_LIST_FORM);
        if (
            context
                .getSession()
                .getPrincipal()
                .isMemberOf(STBaseFunctionConstant.DATE_DERNIERE_CONNEXION_USER_FROM_MINISTERE_VIEW)
        ) {
            List<SortInfo> sortInfos = Optional
                .ofNullable(form)
                .map(UsersListForm::getSortInfosForRequeteur)
                .orElseGet(Collections::emptyList);

            // Pas de tri multiple pour les utilisateurs
            SortInfo sortInfo = sortInfos.get(0);
            if (sortInfo.getSortColumn().equals(STSchemaConstant.USER_DATE_DERNIERE_CONNEXION)) {
                if (sortInfo.getSortAscending()) {
                    Collections.sort(userFormList, ORDER_BY_DATE_CONNEXION);
                } else {
                    Collections.sort(userFormList, ORDER_BY_DATE_CONNEXION.reversed());
                }
            }
        }
        sortByMinisteres(form,userFormList);
        return userFormList;
    }

    private void sortByMinisteres(UsersListForm form,List<UserForm> userFormList){
        SortOrder ministeresSort = form.getMinisteres();
        if(ministeresSort != null){
            if(ministeresSort.getValue().equals("asc")){
                Collections.sort(userFormList, ORDER_BY_MINISTERES);
            } else{
                Collections.sort(userFormList, ORDER_BY_MINISTERES.reversed());
            }
        }
    }

    protected List<UserForm> getUserFormListe(
        SpecificContext context,
        List<DocumentModel> userDocList,
        Boolean fullUser
    ) {
        return userDocList
            .stream()
            .map(doc -> getSTUtilisateursUIService().mapDocToUserForm(doc, context, BooleanUtils.toBoolean(fullUser)))
            .collect(Collectors.toList());
    }

    @Override
    public RequeteurRechercheUtilisateurs getUserRequeteur(SpecificContext context) {
        UserSearchForm userSearchForm = context.getFromContextData(STContextDataKey.USER_SEARCH_FORM);
        Objects.requireNonNull(
            userSearchForm,
            "Le formulaire de recherche utilisateur doit être présent dans le context"
        );

        UsersListForm form = context.getFromContextData(USERS_LIST_FORM);
        List<SortInfo> sortInfos = Optional
            .ofNullable(form)
            .map(UsersListForm::getSortInfosForRequeteur)
            .orElseGet(Collections::emptyList);

        Builder builder = getUserRequeteurBuilder(userSearchForm);
        builder.sortInfos(sortInfos);
        return builder.build();
    }

    private Builder getUserRequeteurBuilder(UserSearchForm userSearchForm) {
        return new Builder()
            .username(userSearchForm.getUtilisateur())
            .firstName(userSearchForm.getPrenom())
            .lastName(userSearchForm.getNom())
            .email(userSearchForm.getMel())
            .telephoneNumber(userSearchForm.getTelephone())
            .ministeres(userSearchForm.getMinisteres())
            .postes(userSearchForm.getPostes())
            .groups(userSearchForm.getProfils())
            .directions(userSearchForm.getUnitesStructurelles())
            .dateDebut(userSearchForm.getDateCreationDebut())
            .dateDebutMax(userSearchForm.getDateCreationFin())
            .dateFin(userSearchForm.getDateExpirationDebut())
            .dateFinMax(userSearchForm.getDateExpirationFin());
    }

    @Override
    public void createExportExcel(SpecificContext context) {
        ArrayList<DocumentModel> users = context.getFromContextData(STContextDataKey.USERS);

        if (CollectionUtils.isNotEmpty(users)) {
            CoreSession session = context.getSession();
            final InlineEventContext eventContext = new InlineEventContext(
                session,
                session.getPrincipal(),
                ImmutableMap.of(STRechercheExportEventConstants.PARAM_DOCUMENT_MODEL_LIST, users)
            );
            STServiceLocator
                .getEventProducer()
                .fireEvent(eventContext.newEvent(STRechercheExportEventConstants.EXPORT_USER_SEARCH_EVENT));
        }
    }

    @Override
    public void deleteUsers(SpecificContext context) {
        Collection<String> userIds = context.getFromContextData(STContextDataKey.IDS);
        List<DocumentModel> users = STServiceLocator.getSTUserSearchService().getUsersFromIds(userIds);

        STUserManagerUIService stUserManagerActionService = getSTUserManagerUIService();
        List<DocumentModel> deletableUsers = users
            .stream()
            .filter(
                u -> {
                    context.setCurrentDocument(u);
                    return stUserManagerActionService.getAllowDeleteUser(context);
                }
            )
            .collect(Collectors.toList());
        users.removeAll(deletableUsers);

        UserManager userManager = STServiceLocator.getUserManager();

        List<String> notDeletedUsersId = users
            .stream()
            .map(u -> u.getAdapter(STUser.class).getUsername())
            .collect(Collectors.toList());
        deletableUsers.forEach(userManager::deleteUser);

        List<String> deletedUsersId = deletableUsers
            .stream()
            .map(u -> u.getAdapter(STUser.class).getUsername())
            .collect(Collectors.toList());
        deletedUsersId.removeAll(notDeletedUsersId);

        if (deletedUsersId.isEmpty() && notDeletedUsersId.isEmpty()) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("recherche.userSearch.delete.empty"));
        } else if (deletedUsersId.isEmpty() && !notDeletedUsersId.isEmpty()) {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("recherche.userSearch.delete.insufficientPermission"));
        } else if (!deletedUsersId.isEmpty() && notDeletedUsersId.isEmpty()) {
            context
                .getMessageQueue()
                .addSuccessToQueue(ResourceHelper.getString("recherche.userSearch.delete.success"));
        } else {
            context
                .getMessageQueue()
                .addWarnToQueue(
                    ResourceHelper.getString(
                        "recherche.userSearch.delete.imcompleteDeletion",
                        notDeletedUsersId,
                        deletedUsersId
                    )
                );
        }
    }

    @Override
    public void envoyerMail(SpecificContext context) {
        @SuppressWarnings("unchecked")
        List<String> userList = UserSessionHelper.getUserSessionParameter(
            context,
            SESSION_PARAM_RECIPIENT_IDS,
            List.class
        );

        MailForm mailForm = context.getFromContextData(STContextDataKey.MAIL_FORM);

        // PJ
        BatchManager batchManager = ServiceUtil.getRequiredService(BatchManager.class);
        Batch batch = batchManager.getBatch(mailForm.getUploadBatchId());
        List<AbstractBlob> blobs = new ArrayList<>();
        if (batch != null) {
            // Il y a un ou des fichiers joints on envoie les datasources
            List<Blob> files = batch.getBlobs();

            for (Blob blob : files) {
                try {
                    blobs.add(BlobUtils.toByteArrayBlob(blob));
                } catch (IOException e) {
                    LOGGER.warn(STLogEnumImpl.FAIL_GET_FILE_TEC, e);
                }
            }
        }

        List<STUser> recipients = userList
            .stream()
            .map(s -> STServiceLocator.getUserManager().getUserModel(s).getAdapter(STUser.class))
            .collect(Collectors.toList());

        if (!recipients.isEmpty()) {
            STServiceLocator
                .getSTMailService()
                .sendMailToUserListAsBCC(
                    recipients,
                    mailForm.getObjet(),
                    mailForm.getMessage(),
                    blobs,
                    mailForm.getExpediteur()
                );
        }

        context.getMessageQueue().addMessageToQueue(ResourceHelper.getString(OK_SEND_MAIL), AlertType.TOAST_INFO);
    }

    public DocumentModelList setUsersList(List<DocumentModel> userListPr) {
        DocumentModelList userListRes = new DocumentModelListImpl();
        for (DocumentModel documentModel : userListPr) {
            userListRes.add(documentModel);
        }
        return userListRes;
    }
}
