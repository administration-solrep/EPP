package fr.dila.ss.core.listener;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import javax.servlet.http.HttpSessionEvent;
import org.apache.commons.lang3.time.DateUtils;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.web.common.session.NuxeoHttpSessionMonitor;
import org.nuxeo.ecm.platform.web.common.session.NuxeoSessionListener;
import org.nuxeo.ecm.platform.web.common.session.SessionInfo;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.transaction.TransactionHelper;

public class SSSessionListener extends NuxeoSessionListener {
    protected static final String UNAUTH_USER = "UNAUTH_USER";
    private static final int DELAY_BETWEEN_ACCESS_AND_CONNECTION = -5;
    private static final STLogger LOGGER = STLogFactory.getLog(SSSessionListener.class);

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Récupération de la session en transmettant un loginName par défaut pour identifié si c'est une session connectée ou non.
        SessionInfo si = NuxeoHttpSessionMonitor.instance().associatedUser(se.getSession(), UNAUTH_USER);

        // Si on n'a pas récupéré le faux login transmis c'est que nous sommes sur une session connectée
        // sinon on est sur une session non-connectée donc on fait rien
        if (!UNAUTH_USER.equals(si.getLoginName())) {
            LOGGER.info(
                STLogEnumImpl.LOG_INFO_TEC,
                String.format(
                    "Invalidation de la session de l'utilisateur %s suite à son inactivité.",
                    si.getLoginName()
                )
            );

            Framework.doPrivileged(
                () -> {
                    TransactionHelper.runInNewTransaction(
                        () -> {
                            RepositoryManager repoManager = STServiceLocator.getRepositoryManager();
                            CoreInstance.doPrivileged(
                                repoManager.getDefaultRepositoryName(),
                                session -> {
                                    STUserService userService = STServiceLocator.getSTUserService();
                                    final UserManager userManager = STServiceLocator.getUserManager();
                                    STUser stUser = userService.getUser(si.getLoginName());

                                    // On récupère la date de dernière connexion de l'utilisateur
                                    Calendar lastConnection = stUser.getDateDerniereConnexion();

                                    // On soustrait le temps de traitement de la connexion pour éviter d'avoir le dernier accès avant la connexion
                                    lastConnection.add(Calendar.SECOND, DELAY_BETWEEN_ACCESS_AND_CONNECTION);

                                    // On teste si la date de dernier accès est égale ou supérieur à la dernière date de connexion
                                    // Si c'est le cas c'est que nous sommes sur la plus récente des sessions de l'utilisateur
                                    // Cell-ci étant invalidée nous pouvons identifer l'utilisateur déconnecté
                                    // Sinon l'utilisateur peut avoir ouvert une session sur un autre navigateur ou sur un autre serveur tomcat donc on ne fait rien
                                    if (DateUtils.toCalendar(si.getLastAccessDate()).compareTo(lastConnection) >= 0) {
                                        //On modifie le document pour identifier que l'utilisateur est déconnecté au niveau de la supervision
                                        stUser.setLogout(true);
                                        userManager.updateUser(stUser.getDocument());

                                        LOGGER.info(
                                            STLogEnumImpl.LOG_INFO_TEC,
                                            String.format(
                                                "La session invalidée de l'utilisateur %s semble la dernière active. Déconnexion de l'utilisateur au niveau de la supervision.",
                                                si.getLoginName()
                                            )
                                        );
                                    }
                                }
                            );
                        }
                    );
                }
            );
        }

        super.sessionCreated(se);
    }
}
