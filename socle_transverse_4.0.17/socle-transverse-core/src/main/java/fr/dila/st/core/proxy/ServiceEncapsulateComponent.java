package fr.dila.st.core.proxy;

import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Abstract Default component used to encapsulate service
 * @author SPL
 *
 * @param <I>
 * @param <S>
 */
public class ServiceEncapsulateComponent<I, S extends I> extends DefaultComponent {
    private final Class<I> serviceClazz;
    private I service;
    private S serviceImpl;

    /**
     * Default constructor
     */
    protected ServiceEncapsulateComponent(Class<I> serviceClazz, S serviceImpl) {
        this.serviceClazz = serviceClazz;
        setServiceImpl(serviceImpl);
    }

    @Override
    public <T> T getAdapter(Class<T> clazz) {
        if (clazz.isAssignableFrom(serviceClazz)) {
            return clazz.cast(service);
        }
        return super.getAdapter(clazz);
    }

    protected final S getServiceImpl() {
        return serviceImpl;
    }

    protected final void setServiceImpl(S serviceImpl) {
        this.serviceImpl = serviceImpl;
        if (serviceImpl == null) {
            this.service = null;
        } else {
            this.service = (I) ChronoLogServiceProxy.wrap(serviceImpl, serviceClazz);
        }
    }
}
