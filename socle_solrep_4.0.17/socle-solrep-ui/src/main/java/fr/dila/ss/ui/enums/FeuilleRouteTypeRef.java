package fr.dila.ss.ui.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum FeuilleRouteTypeRef {
    ETAPE("etape"),
    BRANCH("branch");

    private static final Map<String, FeuilleRouteTypeRef> STRING_TO_ENUM = new HashMap<>();

    private String frontValue;

    static {
        Stream.of(FeuilleRouteTypeRef.values()).forEach(e -> STRING_TO_ENUM.put(e.getFrontValue(), e));
    }

    FeuilleRouteTypeRef(String fromValue) {
        this.frontValue = fromValue;
    }

    public String getFrontValue() {
        return this.frontValue;
    }

    public static FeuilleRouteTypeRef fromString(String frontValue) {
        return STRING_TO_ENUM.get(frontValue);
    }
}
