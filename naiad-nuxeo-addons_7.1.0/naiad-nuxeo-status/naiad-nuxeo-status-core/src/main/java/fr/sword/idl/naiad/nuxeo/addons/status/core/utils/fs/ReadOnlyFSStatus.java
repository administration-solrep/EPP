package fr.sword.idl.naiad.nuxeo.addons.status.core.utils.fs;

import java.io.File;

public class ReadOnlyFSStatus extends FSStatus {

    @Override
    public boolean getTestFor(final File folder) {
        return folder.canRead();
    }

}
