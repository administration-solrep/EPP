package fr.sword.idl.naiad.nuxeo.addons.status.core.utils.fs;

import java.io.File;

import org.nuxeo.runtime.api.Framework;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum;
import fr.sword.idl.naiad.nuxeo.addons.status.core.utils.AbstractParamStatusInfo;

public abstract class FSStatus extends AbstractParamStatusInfo {

    @Override
    public Object getStatusInfo() {
        final ResultInfo resultInfo = new ResultInfo();
        File folder = null;
        try {
            final String path = Framework.getProperty(params, params);

            folder = new File(path.toString());
            if (!folder.exists() || !getTestFor(folder)) {
                resultInfo.setStatut(ResultEnum.KO);
                resultInfo.setDescription(path + " non inscriptible");
            }

        } catch (final Exception e) {
            resultInfo.setStatut(ResultEnum.KO);
        }

        return resultInfo;
    }

    public abstract boolean getTestFor(File folder);

}
