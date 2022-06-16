package fr.dila.cm.core.service;

import fr.dila.cm.core.persister.CaseItemInCasePersister;
import fr.dila.cm.service.CaseDistributionService;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * @author Anahide Tchertchian
 */
public class CaseManagementComponent extends DefaultComponent {
    private CaseDistributionServiceImpl distributionService;

    @Override
    public void activate(ComponentContext context) {
        super.activate(context);
        this.distributionService = new CaseDistributionServiceImpl();
        this.distributionService.setPersister(new CaseItemInCasePersister());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter(Class<T> adapter) {
        if (adapter.isAssignableFrom(CaseDistributionService.class)) {
            return (T) distributionService;
        }
        return null;
    }
}
