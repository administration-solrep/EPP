package fr.sword.idl.naiad.nuxeo.addons.status.api.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class ApplicationInfo {

    @JsonProperty
    private String version;

    @JsonProperty
    private String application;

    @JsonProperty
    private String upTime;

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(final String application) {
        this.application = application;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(final String upTime) {
        this.upTime = upTime;
    }

}
