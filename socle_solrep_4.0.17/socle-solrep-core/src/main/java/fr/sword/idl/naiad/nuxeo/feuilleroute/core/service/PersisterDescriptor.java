package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRoutePersister;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 *
 */
@XObject("persister")
public class PersisterDescriptor {
    @XNode("@class")
    private Class<FeuilleRoutePersister> klass;

    public PersisterDescriptor() {
        // don othing
    }

    public Class<FeuilleRoutePersister> getKlass() {
        return klass;
    }
}
