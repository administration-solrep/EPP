package fr.sword.idl.naiad.nuxeo.addons.status.core.utils;

import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusInfo;

public abstract class AbstractParamStatusInfo implements StatusInfo {

    protected String params;

    public void setParams(final String params) {
        this.params = params;
    }

}
