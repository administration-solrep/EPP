package fr.sword.idl.naiad.nuxeo.addons.status.core.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.nuxeo.runtime.api.Framework;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ApplicationInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusInfo;

public class ApplicationStatus implements StatusInfo {

    @Override
    public Object getStatusInfo() {
        final ApplicationInfo applicationInfo = new ApplicationInfo();

        applicationInfo.setApplication(Framework.getProperty("org.nuxeo.ecm.product.name"));
        applicationInfo.setVersion(Framework.getProperty("org.nuxeo.ecm.product.version"));
        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        final long ut = bean.getUptime();
        long uts = ut / 1000;

        final StringBuffer sb = new StringBuffer();

        final long nbDays = uts / (24 * 3600);

        if (nbDays > 0) {
            sb.append(nbDays + "d ");
            uts = uts % (24 * 3600);
        }

        final long nbHours = uts / 3600;
        sb.append(nbHours + "h ");
        uts = uts % 3600;

        final long nbMin = uts / 60;
        sb.append(nbMin + "m ");
        uts = uts % 60;

        sb.append(uts + "s");

        applicationInfo.setUpTime(sb.toString());

        return applicationInfo;
    }

}
