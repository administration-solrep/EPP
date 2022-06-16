package fr.dila.st.ui.helper;

import java.util.HashMap;
import java.util.Map;

public final class MapHelper {

    private MapHelper() {
        //Utility class
    }

    public static <K, V> Map<K, V> copy(Map<K, V> map) {
        return map != null ? new HashMap<>(map) : null;
    }
}
