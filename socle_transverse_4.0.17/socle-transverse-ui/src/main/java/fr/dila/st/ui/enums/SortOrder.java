package fr.dila.st.ui.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum SortOrder {
    ASC,
    DESC;

    private static final Map<String, SortOrder> STRING_TO_ENUM = new HashMap<>(2);

    static {
        Stream.of(values()).forEach(e -> STRING_TO_ENUM.put(e.getValue(), e));
    }

    public String getValue() {
        return name().toLowerCase();
    }

    public static SortOrder fromValue(String value) {
        return STRING_TO_ENUM.get(value);
    }

    public static boolean isAscending(SortOrder sortOrder) {
        return ASC == sortOrder;
    }

    public static SortOrder fromAscending(boolean isAscending) {
        return isAscending ? ASC : DESC;
    }
}
