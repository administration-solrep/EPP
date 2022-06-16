package fr.dila.st.core.status;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.core.utils.AbstractParamStatusInfo;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandAvailability;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;

public class DefaultCommandStatusInfo extends AbstractParamStatusInfo implements CommandStatusInfo {
    private static final Log LOG = LogFactory.getLog(DefaultCommandStatusInfo.class);

    @Override
    public Object getStatusInfo() {
        final ResultInfo resultInfo = new ResultInfo();

        CommandLineExecutorService cles = ServiceUtil.getRequiredService(CommandLineExecutorService.class);
        CommandAvailability ca = cles.getCommandAvailability(params);
        if (!ca.isAvailable()) {
            setKo(resultInfo, String.format("Commande [%s] introuvable", params));
            return resultInfo;
        }

        try {
            String testCommand = getTestCommand();
            if (testCommand != null) {
                checkCommand(resultInfo, getTestCommand());
            }
        } catch (Exception e) {
            setKo(resultInfo, e.getMessage());
            LOG.trace(e);
        }

        return resultInfo;
    }

    protected String getTestCommand() {
        return null;
    }
}
