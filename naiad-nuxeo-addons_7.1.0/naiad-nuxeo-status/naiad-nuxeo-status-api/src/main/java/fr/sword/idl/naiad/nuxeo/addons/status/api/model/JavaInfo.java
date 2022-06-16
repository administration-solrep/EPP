package fr.sword.idl.naiad.nuxeo.addons.status.api.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class JavaInfo {

    @JsonProperty
    private String runtimeName;

    @JsonProperty
    private String runtimeVersion;

    @JsonProperty
    private String vmName;

    @JsonProperty
    private String vendor;

    @JsonProperty
    private String memHeapsize;

    @JsonProperty
    private String memUsed;

    @JsonProperty
    private String memFree;

    @JsonProperty
    private String memMaxSize;

    public String getRuntimeName() {
        return runtimeName;
    }

    public void setRuntimeName(final String runtimeName) {
        this.runtimeName = runtimeName;
    }

    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    public void setRuntimeVersion(final String runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(final String vmName) {
        this.vmName = vmName;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }

    public String getMemHeapsize() {
        return memHeapsize;
    }

    public void setMemHeapsize(final String memHeapsize) {
        this.memHeapsize = memHeapsize;
    }

    public String getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(final String memUsed) {
        this.memUsed = memUsed;
    }

    public String getMemFree() {
        return memFree;
    }

    public void setMemFree(final String memFree) {
        this.memFree = memFree;
    }

    public String getMemMaxSize() {
        return memMaxSize;
    }

    public void setMemMaxSize(final String memMaxSize) {
        this.memMaxSize = memMaxSize;
    }

}
