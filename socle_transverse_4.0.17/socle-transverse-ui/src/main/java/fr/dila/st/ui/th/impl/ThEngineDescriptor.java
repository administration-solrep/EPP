package fr.dila.st.ui.th.impl;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("engine")
public class ThEngineDescriptor {
    @XNode("cacheEnabled")
    private boolean cacheEnabled = false;

    public ThEngineDescriptor() {
        // do nothing
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
}
