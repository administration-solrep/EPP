package fr.dila.st.core.util;

import java.util.function.BinaryOperator;

public final class StreamUtils {

    private StreamUtils() {
        // Utility class
    }

    public static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }
}
