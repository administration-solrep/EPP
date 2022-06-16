package fr.dila.st.core.service;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.EntityManager;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Default component that initialize persistence provider
 *
 */
public class AbstractPersistenceDefaultComponent extends DefaultComponent {
    private final String persistenceProviderName;
    private PersistenceProvider persistenceProvider;

    public AbstractPersistenceDefaultComponent() {
        this("sword-provider");
    }

    public AbstractPersistenceDefaultComponent(String persistenceProviderName) {
        super();
        this.persistenceProviderName = persistenceProviderName;
    }

    @Override
    public int getApplicationStartedOrder() {
        DefaultComponent component = (DefaultComponent) Framework
            .getRuntime()
            .getComponent("org.nuxeo.ecm.core.persistence.PersistenceComponent");
        if (component == null) {
            return 1;
        } else {
            return component.getApplicationStartedOrder() + 1;
        }
    }

    @Override
    public void start(ComponentContext context) {
        activatePersistenceProvider();
    }

    @Override
    public void deactivate(ComponentContext context) {
        try {
            persistenceProvider.closePersistenceUnit();
        } finally {
            persistenceProvider = null;
        }
        super.deactivate(context);
    }

    /**
     * Recuperation du persistence provider
     *
     * @return
     */
    public PersistenceProvider getOrCreatePersistenceProvider() {
        if (persistenceProvider == null) {
            activatePersistenceProvider();
        }
        return persistenceProvider;
    }

    protected void activatePersistenceProvider() {
        Thread thread = Thread.currentThread();
        ClassLoader last = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
            PersistenceProviderFactory persistenceProviderFactory = Framework.getService(
                PersistenceProviderFactory.class
            );
            persistenceProvider = persistenceProviderFactory.newProvider(persistenceProviderName);
            persistenceProvider.openPersistenceUnit();
        } finally {
            thread.setContextClassLoader(last);
        }
    }

    protected <T> T apply(boolean needActivateSession, Function<EntityManager, T> function) {
        return getOrCreatePersistenceProvider().run(Boolean.valueOf(needActivateSession), function::apply);
    }

    protected void accept(boolean needActivateSession, Consumer<EntityManager> consumer) {
        getOrCreatePersistenceProvider().run(Boolean.valueOf(needActivateSession), consumer::accept);
    }

    protected void transactionAccept(boolean needActivateSession, Consumer<EntityManager> consumer) {
        TransactionHelper.runInTransaction(() -> accept(needActivateSession, consumer));
    }

    protected <T> T transactionApply(boolean needActivateSession, Function<EntityManager, T> function) {
        return TransactionHelper.runInTransaction(() -> apply(needActivateSession, function));
    }
}
