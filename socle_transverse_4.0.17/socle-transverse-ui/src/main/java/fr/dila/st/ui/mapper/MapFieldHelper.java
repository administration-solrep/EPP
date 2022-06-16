package fr.dila.st.ui.mapper;

import fr.dila.st.ui.annot.NxProp;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MapFieldHelper {
    private static final Map<Class<?>, List<MapField>> CACHE = new HashMap<>();

    private MapFieldHelper() {
        // do nothing
    }

    public static List<MapField> getMapFields(Class<?> clazz) {
        List<MapField> fields = CACHE.get(clazz);
        if (fields == null) {
            fields = extractMapFields(clazz);
            CACHE.put(clazz, fields);
        }
        return fields;
    }

    public static List<MapField> extractMapFields(Class<?> clazz) {
        List<MapField> fields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            NxProp annot = field.getAnnotation(NxProp.class);
            if (annot != null) {
                field.setAccessible(true);
                fields.add(new MapField(field, annot));
            }
        }
        if (clazz.getSuperclass() != null) {
            fields.addAll(getMapFields(clazz.getSuperclass()));
        }
        return fields;
    }
}
