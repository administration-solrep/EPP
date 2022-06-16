package fr.dila.ss.ui.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum FeuilleRouteEtapeOrder {
    BEFORE("Ajouter avant"),
    AFTER("Ajouter après"),
    IN("Ajouter dans le conteneur");

    private static final Map<String, FeuilleRouteEtapeOrder> STRING_TO_ENUM = new HashMap<>();

    private String frontValue;

    static {
        Stream.of(FeuilleRouteEtapeOrder.values()).forEach(e -> STRING_TO_ENUM.put(e.getFrontValue(), e));
    }

    FeuilleRouteEtapeOrder(String fromValue) {
        this.frontValue = fromValue;
    }

    public String getFrontValue() {
        return this.frontValue;
    }

    public static FeuilleRouteEtapeOrder fromString(String frontValue) {
        return STRING_TO_ENUM.get(frontValue);
    }
}
