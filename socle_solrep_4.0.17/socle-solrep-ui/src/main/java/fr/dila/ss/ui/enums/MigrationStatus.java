package fr.dila.ss.ui.enums;

import fr.dila.ss.api.constant.SSConstant;
import fr.dila.ss.api.migration.MigrationLoggerModel;
import java.util.stream.Stream;

public enum MigrationStatus {
    EN_COURS(SSConstant.EN_COURS_STATUS, "historique.migration.en.cours", "icon icon--clock"),
    TERMINEE(SSConstant.TERMINEE_STATUS, "historique.migration.terminee", "icon icon--check-circle"),
    FAILED(SSConstant.FAILED_STATUS, "historique.migration.echouee", "icon icon--times-circle");

    private String key;
    private String labelKey;
    private String icon;

    MigrationStatus(String key, String labelKey, String icon) {
        this.key = key;
        this.labelKey = labelKey;
        this.icon = icon;
    }

    public static MigrationStatus getFrom(MigrationLoggerModel migrationLoggerModel) {
        return Stream
            .of(values())
            .filter(status -> status.name().equals(migrationLoggerModel.getStatus()))
            .findFirst()
            .orElse(null);
    }

    public String getKey() {
        return key;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public String getIcon() {
        return icon;
    }
}
