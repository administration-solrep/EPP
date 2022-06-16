package fr.dila.ss.core.event;

import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.event.AbstractFilterEventListener;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;

/**
 *
 * @author feo
 * @author sly
 */
public class MailNotificationListener extends AbstractFilterEventListener<EventContext> {
    /**
     * Logger.
     */
    private static final Log LOG = LogFactory.getLog(MailNotificationListener.class);

    public MailNotificationListener() {
        super(STEventConstant.SEND_MAIL_NOTIFICATION);
    }

    @Override
    protected final void doHandleEvent(final Event event, final EventContext ctx) {
        try {
            // Traite uniquement les documents de type sendMailNotification

            final String mailboxId = (String) ctx.getProperty(STEventConstant.SEND_MAIL_NOTIFICATION_MAILBOX_ID);
            final String texte = (String) ctx.getProperty(STEventConstant.SEND_MAIL_NOTIFICATION_TEXTE);
            final String objet = (String) ctx.getProperty(STEventConstant.SEND_MAIL_NOTIFICATION_OBJET);
            final String numeroQuestion = (String) ctx.getProperty("numero_question");

            HashMap<String, Object> params = new HashMap<String, Object>();
            if (numeroQuestion != null) {
                params.put("numero_question", numeroQuestion);
            }

            // Détermine la liste des utilisateurs du poste
            final String posteId = SSServiceLocator.getMailboxPosteService().getPosteIdFromMailboxId(mailboxId);
            final PosteNode posteNode = STServiceLocator.getSTPostesService().getPoste(posteId);
            final List<STUser> userList = posteNode.getUserList();

            final List<String> userMails = new ArrayList<String>();
            for (STUser user : userList) {
                final String mail = user.getEmail();
                if (mail != null) {
                    userMails.add(mail);
                }
            }

            // Envoie un mail à chaque utilisateur
            STServiceLocator.getSTMailService().sendTemplateMail(userMails, objet, texte, params);
        } catch (NuxeoException e) {
            LOG.error("MailNotificationListener Error : " + e, e);
        }
    }
}
