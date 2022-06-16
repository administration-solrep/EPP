package fr.sword.idl.naiad.nuxeo.addons.status.core.utils;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.SystemInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusInfo;

public class SystemStatus implements StatusInfo {

    @Override
    public Object getStatusInfo() {
        final SystemInfo systemInfo = new SystemInfo();

        systemInfo.setOsArch(System.getProperty("os.arch"));
        systemInfo.setOsName(System.getProperty("os.name"));
        systemInfo.setOsVersion(System.getProperty("os.version"));
        systemInfo.setLanguage(System.getProperty("user.language"));

        systemInfo.setAvailableProcessors(String.valueOf(Runtime.getRuntime().availableProcessors()));
        systemInfo.setMemMaxSize(String.valueOf(Runtime.getRuntime().maxMemory() / (1024 * 1024)) + "MB");
        systemInfo.setMemFree(String.valueOf(Runtime.getRuntime().freeMemory() / (1024 * 1024)) + "MB");
        systemInfo.setMemTotal(String.valueOf(Runtime.getRuntime().totalMemory() / (1024 * 1024)) + "MB");

        return systemInfo;
    }

}
