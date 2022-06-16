package fr.dila.st.core.event.batch;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;

/**
 * Batch de rappel d'expiration du mot de passe. La récupération du délai est propre à chaque application ainsi que la
 * récupération des listes utilisateurs
 *
 */
public abstract class AbstractDailyReminderChangePasswordListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(AbstractDailyReminderChangePasswordListener.class);

    public AbstractDailyReminderChangePasswordListener() {
        super(LOGGER, STEventConstant.SEND_DAILY_REMIND_CHANGE_PASS_EVENT);
    }

    @Override
    protected void processEvent(final CoreSession session, final Event event) {
        LOGGER.info(session, STLogEnumImpl.INIT_B_DAILY_MAIL_CHANGE_PASS_TEC);
        final long startTime = Calendar.getInstance().getTimeInMillis();
        int nbMailSent = 0;

        try {
            final STMailService mailService = STServiceLocator.getSTMailService();
            final STParametreService parametreService = STServiceLocator.getSTParametreService();

            final Set<STUser> userListToRemind = getUsersList(session);
            final String mailObjet = parametreService.getParametreValue(
                session,
                STParametreConstant.OBJET_MAIL_PREVENANCE_RENOUVELLEMENT_MOT_DE_PASSE
            );
            final String mailTexte = parametreService.getParametreValue(
                session,
                STParametreConstant.TEXTE_MAIL_PREVENANCE_RENOUVELLEMENT_MOT_DE_PASSE
            );
            final Map<String, Object> mailTexteMap = new HashMap<String, Object>();
            // Envoi des mails aux utilisateurs concernés
            for (STUser user : userListToRemind) {
                mailTexteMap.put("jours_restants", getDelayForUser(session, user));
                mailTexteMap.put("user_login", user.getUsername());
                try {
                    mailService.sendTemplateMail(user.getEmail(), mailObjet, mailTexte, mailTexteMap);
                    ++nbMailSent;
                } catch (NuxeoException exc) {
                    String message = "Identifiant : " + user.getUsername() + " - " + exc.getMessage();
                    LOGGER.error(session, STLogEnumImpl.FAIL_SEND_MAIL_TEC, message, exc);
                    ++errorCount;
                }
            }
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_B_DAILY_MAIL_CHANGE_PASS_TEC, exc);
            ++errorCount;
        }

        final long endTime = Calendar.getInstance().getTimeInMillis();
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Nombre de méls envoyé(s)",
                nbMailSent,
                endTime - startTime
            );
        } catch (Exception exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, exc);
        }
        LOGGER.info(session, STLogEnumImpl.END_B_DAILY_MAIL_CHANGE_PASS_TEC, nbMailSent + " mél envoyé(s).");
    }

    /**
     * Récupère la liste des utilisateurs à notifier via le service de profilUtilisateur
     *
     * @param session
     * @return
     * @throws ClientException
     */
    protected abstract Set<STUser> getUsersList(final CoreSession session);

    /**
     * Récupère le délai restant pour un utilisateur
     *
     * @param session
     * @param user
     * @return
     * @throws ClientException
     */
    protected abstract int getDelayForUser(final CoreSession session, final STUser user);
}
