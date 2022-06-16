package fr.dila.st.core.proxy;

import fr.dila.st.core.logger.AbstractLogger;
import fr.dila.st.core.logger.ChronoLog;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ChronoLogServiceProxy<T> implements InvocationHandler {
    private T service;

    public ChronoLogServiceProxy(T service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.nanoTime();
        ChronoLog.getInstance().enter(method);
        Throwable thrown = null;
        try {
            return method.invoke(service, args);
        } catch (Exception ex) {
            //On vérifie s'il existe une cause à l'exception car en utilisant l'invocation, les exceptions réelles sont wrappées par une Exception d'invocation
            if (ex.getCause() != null) {
                thrown = ex.getCause();
                throw ex.getCause();
            } else {
                thrown = ex;
                throw ex;
            }
        } finally {
            long elapsed = AbstractLogger.getDurationInMs(start);
            ChronoLog.getInstance().exit(method, elapsed, thrown);
        }
    }

    public T getTarget() {
        return service;
    }

    @SuppressWarnings("unchecked")
    public static <T extends I, I> T wrap(T service, Class<I> serviceinterface) {
        ClassLoader cl = service.getClass().getClassLoader();
        return (T) Proxy.newProxyInstance(cl, new Class[] { serviceinterface }, new ChronoLogServiceProxy<>(service));
    }

    @SuppressWarnings("unchecked")
    public static <T> T extractTarget(Object proxy) {
        InvocationHandler handler = Proxy.getInvocationHandler(proxy);
        if (handler instanceof ChronoLogServiceProxy<?>) {
            return ((ChronoLogServiceProxy<T>) handler).getTarget();
        }
        return null;
    }
}
