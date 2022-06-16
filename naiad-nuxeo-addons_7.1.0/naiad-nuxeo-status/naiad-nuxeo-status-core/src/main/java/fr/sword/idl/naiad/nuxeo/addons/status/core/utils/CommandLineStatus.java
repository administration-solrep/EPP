package fr.sword.idl.naiad.nuxeo.addons.status.core.utils;

import org.nuxeo.ecm.platform.commandline.executor.api.CommandAvailability;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

public class CommandLineStatus extends AbstractParamStatusInfo {

    @Override
    public Object getStatusInfo() {
        final ResultInfo resultInfo = new ResultInfo();
        try {
            final CommandLineExecutorService commandService = ServiceUtil
                    .getRequiredService(CommandLineExecutorService.class);

            final CommandAvailability cmdAvail = commandService.getCommandAvailability(params);
            if (!cmdAvail.isAvailable()) {
                resultInfo.setStatut(ResultEnum.KO);
                resultInfo.setDescription(params + " non disponible");
            }

        } catch (final Exception e) {
            resultInfo.setStatut(ResultEnum.KO);
        }

        return resultInfo;
    }

}
