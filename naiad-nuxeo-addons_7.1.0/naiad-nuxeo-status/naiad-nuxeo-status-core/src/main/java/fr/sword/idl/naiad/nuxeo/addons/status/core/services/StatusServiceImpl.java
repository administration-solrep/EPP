package fr.sword.idl.naiad.nuxeo.addons.status.core.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusService;
import fr.sword.idl.naiad.nuxeo.addons.status.core.descriptor.StatusDescriptor;
import fr.sword.idl.naiad.nuxeo.addons.status.core.utils.AbstractParamStatusInfo;

public class StatusServiceImpl extends DefaultComponent implements StatusService {

    private static final Log LOGGER = LogFactory.getLog(StatusServiceImpl.class);

    Map<String, StatusDescriptor> descriptors = new HashMap<String, StatusDescriptor>();

    @Override
    public void registerContribution(final Object contribution, final String extensionPoint,
            final ComponentInstance contributor) {
        if ("statusConfig".equals(extensionPoint) && contribution instanceof StatusDescriptor) {
            final StatusDescriptor descriptor = (StatusDescriptor) contribution;
            descriptors.put(descriptor.getName(), descriptor);
        }
    }

    @Override
    public Map<String, Object> getStatus() {
        final Map<String, Object> statusInfo = new TreeMap<String, Object>();

        for (final Entry<String, StatusDescriptor> descriptorEntry : descriptors.entrySet()) {
            final StatusDescriptor descriptor = descriptorEntry.getValue();
            if (descriptor.getEnabled()) {
                final String clazz = descriptor.getClazz();

                try {
                    final Class<?> targetClass = Class.forName(clazz);
                    final Object object = targetClass.getConstructor().newInstance();
                    if (object instanceof StatusInfo) {
                        if (object instanceof AbstractParamStatusInfo) {
                            ((AbstractParamStatusInfo) object).setParams(descriptor.getParams());
                        }
                        final Object result = ((StatusInfo) object).getStatusInfo();
                        if (result instanceof ResultInfo) {
                            if (object instanceof AbstractParamStatusInfo) {
                                ((ResultInfo) result).setParams(descriptor.getParams());
                            }
                        }
                        statusInfo.put(descriptor.getName(), result);
                    } else {
                        statusInfo.put(descriptor.getName(), "Incorrect implementation : interface is StatusInfo ! ");
                    }

                } catch (final Exception e) {
                    LOGGER.error("Fail to get method ", e);
                    statusInfo.put(descriptor.getName(), "Erreur : " + e.getMessage());
                }
            }

        }

        return statusInfo;
    }

}
