package fr.sword.idl.naiad.nuxeo.addons.status.core.utils;

import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusInfo;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

public class UserDirectoryStatus implements StatusInfo {

    @Override
    public Object getStatusInfo() {
        final ResultInfo resultInfo = new ResultInfo();

        try {
            final UserManager userManager = ServiceUtil.getRequiredService(UserManager.class);
            userManager.getUserModel("#test#sword#");
        } catch (final Exception e) {
            resultInfo.setStatut(ResultEnum.KO);
            resultInfo.setDescription(e.getMessage());
        }

        return resultInfo;
    }

}
