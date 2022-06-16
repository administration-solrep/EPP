package fr.sword.idl.naiad.nuxeo.addons.status.core.utils.fs;

import java.io.File;

public class WriteFSStatus extends FSStatus {

    @Override
    public boolean getTestFor(final File folder) {
        return folder.canWrite();
    }

}
