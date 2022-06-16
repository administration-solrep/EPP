package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Batch de désactivation des utilisateurs et d'information de l'administrateur.
 *
 * @author arn
 */
public class UserDesactivationBatchListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    protected static final STLogger LOGGER = STLogFactory.getLog(UserDesactivationBatchListener.class);
    private long nbUserDesactivated = 0;

    public UserDesactivationBatchListener() {
        super(LOGGER, SSEventConstant.USER_DESACTIVATION_BATCH_EVENT);
    }

    @Override
    protected void processEvent(final CoreSession session, final Event event) {
        Long startTime = System.currentTimeMillis();
        LOGGER.info(session, SSLogEnumImpl.INIT_B_DEACTIVATE_USERS_TEC);
        // Récupération des utilisateurs à désactiver
        List<String> userNameInfoList = new ArrayList<>();
        try {
            userNameInfoList = getUsersToDisable(session);
        } catch (NuxeoException e1) {
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_USER_TEC, e1);
            ++errorCount;
        }

        final STParametreService paramService = STServiceLocator.getSTParametreService();

        // envoi du mail à l'administrateur
        if (!userNameInfoList.isEmpty()) {
            final ProfileService profileService = STServiceLocator.getProfileService();
            final STMailService mailService = STServiceLocator.getSTMailService();
            List<STUser> users = new ArrayList<>();
            try {
                users =
                    profileService.getUsersFromBaseFunction(STBaseFunctionConstant.ADMIN_FONCTIONNEL_EMAIL_RECEIVER);
            } catch (NuxeoException ce) {
                LOGGER.error(session, STLogEnumImpl.FAIL_GET_USER_TEC, ce);
                ++errorCount;
            }
            // on récupère le message et l'objet du mail
            StringBuilder text = new StringBuilder();
            String object = null;
            try {
                text.append(
                    paramService.getParametreValue(
                        session,
                        STParametreConstant.NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_TEXT
                    )
                );
                // on ajoute la liste des utilisateurs dans le message
                text.append(StringUtils.join(userNameInfoList, ", "));
                object =
                    paramService.getParametreValue(
                        session,
                        STParametreConstant.NOTIFICATION_MAIL_USER_DECONNEXION_INFORMATION_DELAI_OBJET
                    );
            } catch (NuxeoException ce) {
                LOGGER.error(session, STLogEnumImpl.FAIL_GET_PARAM_TEC, ce);
            }
            // envoi du mail
            if (object != null && text != null && !users.isEmpty()) {
                try {
                    mailService.sendMailToUserList(users, object, text.toString());
                } catch (NuxeoException ce) {
                    LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, ce);
                    ++errorCount;
                }
            } else {
                LOGGER.warn(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, "Objet, message ou liste destinataires vide");
            }
        }
        Long endTime = System.currentTimeMillis();
        SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Nombre d'utilisateurs désactivés",
                nbUserDesactivated,
                endTime - startTime
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, SSLogEnumImpl.END_B_DEACTIVATE_USERS_TEC);
    }

    /**
     * Méthode renvoyant une liste des noms des utilisateurs à désactiver suite à une non connexion depuis un temps prédéfini dans les paramètres de l'application
     *
     * @param session
     * @return userNameInfoList
     *
     */
    protected List<String> getUsersToDisable(CoreSession session) {
        // initialisation de la liste des utilisateurs
        final List<String> userNameInfoList = new ArrayList<>();

        // date de connexion en dessous de laquelle on supprime l'utilisateur
        final Calendar delaiMaxDesactivation = new GregorianCalendar();
        // date de connexion en dessous de laquelle on informe l'administrateur
        final Calendar delaiMaxInformation = new GregorianCalendar();
        final STParametreService paramService = STServiceLocator.getSTParametreService();

        // on récupère le paramètre sur la date maximal de déconnexion pour supprimer un utilisateur en mois
        String delaiDesactivation = paramService.getParametreValue(
            session,
            STParametreConstant.USER_DECONNEXION_DESACTIVATION_DELAI
        );
        // calcul de la date au dessous de laquelle on déverrouille les documents
        delaiMaxDesactivation.add(Calendar.MONTH, -Integer.parseInt(delaiDesactivation));

        // on récupère le paramètre sur la date maximal de déconnexion pour informer l'administrateur en mois
        String delaiInformation = paramService.getParametreValue(
            session,
            STParametreConstant.USER_DECONNEXION_INFORMATION_DELAI
        );
        // calcul de la date au dessous de laquelle on informe l'administrateur
        delaiMaxInformation.add(Calendar.MONTH, -Integer.parseInt(delaiInformation));

        final UserManager userManager = STServiceLocator.getUserManager();
        Map<String, Serializable> filter = new HashMap<>();
        final DocumentModelList userModelList = userManager.searchUsers(filter, null);

        UnrestrictedSessionRunner unrestrictedSession = new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                for (DocumentModel userDocModel : userModelList) {
                    STUser user = userDocModel.getAdapter(STUser.class);
                    Calendar dateLastConnection = user.getDateDerniereConnexion();
                    String userDocId = userDocModel.getId();
                    if (dateLastConnection == null) {
                        LOGGER.debug(
                            session,
                            SSLogEnumImpl.PROCESS_B_DEACTIVATE_USERS_TEC,
                            "utilisateur jamais connecte : " + userDocId
                        );
                    } else if (delaiMaxDesactivation.compareTo(dateLastConnection) > 0) {
                        // suppression de l'utilisateur
                        LOGGER.info(session, STLogEnumImpl.DEL_USER_TEC, userDocModel);
                        userManager.deleteUser(user.getDocument());
                        ++nbUserDesactivated;
                        session.save();
                    } else if (
                        delaiMaxInformation.compareTo(dateLastConnection) > 0 && StringUtils.isNotEmpty(userDocId)
                    ) {
                        userNameInfoList.add(userDocModel.getId());
                    }
                    commitAndRestartTransaction(session, true);
                }
            }
        };
        unrestrictedSession.run();
        return userNameInfoList;
    }
}
