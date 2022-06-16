package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteDisplayService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteStepProcessService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.StepProcessInit;
import java.util.Map;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 *
 *
 */
public class FeuilleRouteComponent extends DefaultComponent {
    public static final String STEP_PROCESS_XP = "stepProcess";

    public static final String PERSISTER_XP = "persister";

    private FeuilleRouteServiceImpl feuilleRouteService;

    private FeuilleRouteDisplayServiceImpl feuilleRouteDisplayService;

    private FeuilleRouteStepProcessServiceImpl feuilleRouteStepProcessService;

    public FeuilleRouteComponent() {}

    @Override
    public void activate(ComponentContext context) {
        feuilleRouteService = new FeuilleRouteServiceImpl();
        feuilleRouteDisplayService = new FeuilleRouteDisplayServiceImpl();
        feuilleRouteStepProcessService = new FeuilleRouteStepProcessServiceImpl();
    }

    @Override
    public void deactivate(ComponentContext context) {
        feuilleRouteService = null;
        feuilleRouteDisplayService = null;
        feuilleRouteStepProcessService = null;
    }

    @Override
    public <T> T getAdapter(Class<T> clazz) {
        if (clazz.isAssignableFrom(FeuilleRouteService.class)) {
            return clazz.cast(feuilleRouteService);
        } else if (clazz.isAssignableFrom(FeuilleRouteDisplayService.class)) {
            return clazz.cast(feuilleRouteDisplayService);
        } else if (clazz.isAssignableFrom(FeuilleRouteStepProcessService.class)) {
            return clazz.cast(feuilleRouteStepProcessService);
        }
        return null;
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (STEP_PROCESS_XP.equals(extensionPoint)) {
            StepProcessDescriptor desc = (StepProcessDescriptor) contribution;
            try {
                Map<String, String> params = desc.getParametersAsMap();
                StepProcessInit stepProcess = desc.getStepProcessClazz().newInstance();
                stepProcess.init(params);
                feuilleRouteStepProcessService.register(desc.getDocumentType(), stepProcess);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new NuxeoException("Fail to instanciate class : " + desc.getStepProcessClazz().getName());
            }
        } else if (PERSISTER_XP.equals(extensionPoint)) {
            PersisterDescriptor des = (PersisterDescriptor) contribution;
            try {
                feuilleRouteService.setPersister(des.getKlass().newInstance());
            } catch (IllegalAccessException | InstantiationException e) {
                throw new NuxeoException("Faield to init persister", e);
            }
        }
    }
}
