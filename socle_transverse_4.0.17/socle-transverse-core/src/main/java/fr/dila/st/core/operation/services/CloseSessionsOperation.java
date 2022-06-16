package fr.dila.st.core.operation.services;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 *
 * @author adelamotte
 *
 */
@Operation(
    id = CloseSessionsOperation.ID,
    category = "Services",
    label = "Close Sessions",
    description = "Close all tomcat sessions"
)
public class CloseSessionsOperation {
    public static final String ID = "Services.CloseSessions";

    private static final String EXPIRATION_OPERATION = "expireSession";
    private static final String LIST_SESSIONS_OPERATION = "listSessionIds";

    private static final STLogger LOGGER = STLogFactory.getLog(CloseSessionsOperation.class);

    @Context
    protected CoreSession session;

    @Context
    protected OperationContext context;

    @OperationMethod
    public void run() {
        ConfigService configService = STServiceLocator.getConfigService();

        String tomcatNamespace = configService.getValue(STConfigConstants.SOLON_TOMCAT_NAMESPACE);

        if (tomcatNamespace != null) {
            expireAllSessions(tomcatNamespace);
        } else {
            LOGGER.error(session, STLogEnumImpl.FAIL_GET_PROPERTY_TEC, STConfigConstants.SOLON_TOMCAT_NAMESPACE);
        }
    }

    /**
     * Expiring all Tomcat sessions
     * @param tomcatNamespace the tomcat jvm namespace for app
     *
     */
    private void expireAllSessions(String tomcatNamespace) {
        ObjectName objectName = getObjectName(tomcatNamespace);

        if (objectName != null) {
            List<String> sessionList = getListSessionIds(objectName);

            if (sessionList != null) {
                LOGGER.info(
                    session,
                    STLogEnumImpl.LOG_INFO_TEC,
                    "*** AVANT expiration Total HttpSessions: " + sessionList.size()
                );

                sessionList
                    .stream()
                    .forEach(
                        sid -> {
                            try {
                                ManagementFactory
                                    .getPlatformMBeanServer()
                                    .invoke(
                                        objectName,
                                        EXPIRATION_OPERATION,
                                        new String[] { sid },
                                        new String[] { String.class.getName() }
                                    );
                            } catch (InstanceNotFoundException | ReflectionException | MBeanException e) {
                                LOGGER.error(session, STLogEnumImpl.LOG_EXCEPTION_TEC, e);
                            }
                        }
                    );

                sessionList = getListSessionIds(objectName);
                if (sessionList != null) {
                    LOGGER.info(
                        session,
                        STLogEnumImpl.LOG_INFO_TEC,
                        "*** APRES expiration Total HttpSessions: " + sessionList.size()
                    );
                }
            }
        }
    }

    /**
     * Return list of active Sessions
     *
     * @param objectName
     * @return active sessions
     */
    private List<String> getListSessionIds(ObjectName objectName) {
        List<String> sessionCollector = null;
        try {
            String sessionIdList = (String) ManagementFactory
                .getPlatformMBeanServer()
                .invoke(objectName, LIST_SESSIONS_OPERATION, null, null);

            sessionCollector = getTokensWithCollection(sessionIdList);
        } catch (InstanceNotFoundException | ReflectionException | MBeanException e) {
            LOGGER.error(session, STLogEnumImpl.LOG_EXCEPTION_TEC, e.getMessage());
        }

        return sessionCollector;
    }

    /**
     * Convert token string to collection of string
     *
     * @param str
     * @return session list
     */
    private List<String> getTokensWithCollection(String str) {
        return Collections
            .list(new StringTokenizer(str, " "))
            .stream()
            .map(String.class::cast)
            .map(String::trim)
            .collect(Collectors.toList());
    }

    /**
     * Get Object Name from tomcat namespace
     * @param tomcatNamespce
     * @return object name
     */
    private ObjectName getObjectName(String tomcatNamespace) {
        ObjectName objectName = null;
        try {
            objectName = new ObjectName(tomcatNamespace);
        } catch (MalformedObjectNameException e) {
            LOGGER.error(session, STLogEnumImpl.LOG_EXCEPTION_TEC, e.getMessage());
        }

        return objectName;
    }
}
