package fr.dila.st.core.operation;

import fr.dila.st.core.util.ObjectHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum STApplication {
    ANY,
    EPG,
    EPP,
    REPONSES;

    private static final Map<String, STApplication> STRING_TO_ENUM = new HashMap<>();

    static {
        Stream.of(STApplication.values()).forEach(e -> STRING_TO_ENUM.put(e.name().toLowerCase(), e));
    }

    public static STApplication fromString(String value) {
        return ObjectHelper.requireNonNullElse(STRING_TO_ENUM.get(value), ANY);
    }
}
