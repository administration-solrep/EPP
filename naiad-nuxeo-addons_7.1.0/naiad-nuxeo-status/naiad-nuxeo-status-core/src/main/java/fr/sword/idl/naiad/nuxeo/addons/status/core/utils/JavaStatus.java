package fr.sword.idl.naiad.nuxeo.addons.status.core.utils;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.JavaInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusInfo;

public class JavaStatus implements StatusInfo {

    @Override
    public Object getStatusInfo() {
        final JavaInfo javaInfo = new JavaInfo();

        javaInfo.setRuntimeName(System.getProperty("java.runtime.name"));
        javaInfo.setRuntimeVersion(System.getProperty("java.runtime.version"));
        javaInfo.setVmName(System.getProperty("java.vm.version"));
        javaInfo.setVendor(System.getProperty("java.vendor"));
        javaInfo.setMemHeapsize(String.valueOf(Runtime.getRuntime().totalMemory() / (1024 * 1024)) + "MB");
        javaInfo.setMemUsed(String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
                / (1024 * 1024))
                + "MB");
        javaInfo.setMemFree(String.valueOf(Runtime.getRuntime().freeMemory() / (1024 * 1024)) + "MB");
        javaInfo.setMemMaxSize(String.valueOf(Runtime.getRuntime().maxMemory() / (1024 * 1024)) + "MB");

        return javaInfo;
    }

}
