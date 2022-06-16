package fr.dila.st.ui.bean;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class ConfigDTO {
    private final String name;
    private final String environmentName;
    private final boolean production;
    private final String color;
    private final String backgroundColor;
    private final String version;

    public ConfigDTO(String name, String environmentName, String color, String backgroundColor, String version) {
        this.name = name;
        this.environmentName = environmentName;
        production = isEmpty(this.environmentName);
        this.color = color;
        this.backgroundColor = backgroundColor;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public boolean isProduction() {
        return production;
    }

    public String getColor() {
        return color;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getVersion() {
        return version;
    }
}
