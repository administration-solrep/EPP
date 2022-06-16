package fr.dila.st.core.logger;

import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.ThreadContext;

/**
 * Chrono Log
 * @author SPL
 *
 */
public final class ChronoLog {
    private static final Log LOG = LogFactory.getLog(ChronoLog.class);

    private static final String SENS = "log_sens";
    private static final String STATUS = "log_status";
    private static final String TEMPSEXEC_ID = "log_temps_exec";
    private static final String INFO_ADD = "log_comp";

    private static final ChronoLog INSTANCE = new ChronoLog();

    private ChronoLog() {
        super();
    }

    public static ChronoLog getInstance() {
        return INSTANCE;
    }

    public void enter(Method method) {
        enter(methodName(method));
    }

    public void enter(String name) {
        ThreadContext.put(SENS, "IN");
        ThreadContext.put(STATUS, "OK");
        ThreadContext.put(TEMPSEXEC_ID, "");
        ThreadContext.put(INFO_ADD, "");
        LOG.debug("START:" + name);
    }

    public void exit(Method method, long elapsedMs) {
        exit(methodName(method), elapsedMs, true, "");
    }

    public void exit(Method method, long elapsedMs, Throwable throwable) {
        if (throwable == null) {
            exit(methodName(method), elapsedMs, true, "");
        } else {
            exit(methodName(method), elapsedMs, false, throwable.getMessage());
        }
    }

    public void exit(String name, long elapsedMs, boolean ok, String infoAdd) {
        ThreadContext.put(SENS, "OUT");
        ThreadContext.put(STATUS, ok ? "OK" : "KO");
        ThreadContext.put(TEMPSEXEC_ID, String.valueOf(elapsedMs));
        ThreadContext.put(INFO_ADD, infoAdd == null ? "" : infoAdd);
        LOG.debug("END:" + name);
    }

    private String methodName(Method method) {
        return method.getDeclaringClass().getCanonicalName() + "." + method.getName();
    }
}
