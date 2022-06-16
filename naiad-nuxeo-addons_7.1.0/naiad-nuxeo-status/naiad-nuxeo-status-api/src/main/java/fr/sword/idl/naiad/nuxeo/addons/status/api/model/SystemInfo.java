package fr.sword.idl.naiad.nuxeo.addons.status.api.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class SystemInfo {

    @JsonProperty
    private String osName;

    @JsonProperty
    private String osArch;

    @JsonProperty
    private String osVersion;

    @JsonProperty
    private String availableProcessors;

    @JsonProperty
    private String language;

    @JsonProperty
    private String memMaxSize;

    @JsonProperty
    private String memFree;

    @JsonProperty
    private String memTotal;

    public String getMemMaxSize() {
        return memMaxSize;
    }

    public void setMemMaxSize(final String memMaxSize) {
        this.memMaxSize = memMaxSize;
    }

    public String getMemFree() {
        return memFree;
    }

    public void setMemFree(final String memFree) {
        this.memFree = memFree;
    }

    public String getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(final String memTotal) {
        this.memTotal = memTotal;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(final String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(final String osArch) {
        this.osArch = osArch;
    }

    public String getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(final String availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(final String osVersion) {
        this.osVersion = osVersion;
    }

}
