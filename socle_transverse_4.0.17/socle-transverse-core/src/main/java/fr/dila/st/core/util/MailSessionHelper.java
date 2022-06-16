package fr.dila.st.core.util;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import javax.mail.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.platform.ec.notification.service.NotificationService;
import org.nuxeo.ecm.platform.notification.api.NotificationManager;
import org.nuxeo.runtime.api.Framework;

public final class MailSessionHelper {
    private static final STLogger LOGGER = STLogFactory.getLog(MailSessionHelper.class);

    private static Boolean javaMailNotAvailable = null;

    private MailSessionHelper() {}

    /**
     * Teste si la contrib mail est chargée dans le mailService nuxeo
     *
     * @return true si la contrib est ok
     *
     */
    public static boolean isMailSessionConfigured() {
        if (javaMailNotAvailable == null) {
            getMailSession();
        }
        return !javaMailNotAvailable;
    }

    /**
     * Gets the session from the JNDI.
     */
    public static Session getMailSession() {
        if (BooleanUtils.isTrue(javaMailNotAvailable)) {
            return null;
        }
        Session session = null;
        // First, try to get the session from JNDI, as would be done under J2EE.
        try {
            NotificationService service = (NotificationService) Framework.getService(NotificationManager.class);
            if (service != null) {
                InitialContext ic = new InitialContext();
                session = (Session) ic.lookup(service.getMailSessionJndiName());
                javaMailNotAvailable = false;
            } else {
                javaMailNotAvailable = true;
            }
        } catch (NamingException ex) {
            LOGGER.warn(STLogEnumImpl.SEND_MAIL_TEC, ex);
            javaMailNotAvailable = true;
        }

        return session;
    }
}
