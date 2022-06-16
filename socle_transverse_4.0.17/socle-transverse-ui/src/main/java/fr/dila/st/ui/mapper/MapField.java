package fr.dila.st.ui.mapper;

import fr.dila.st.ui.annot.NxProp;
import java.lang.reflect.Field;

public class MapField {
    private final Field field;
    private final NxProp nxprop;
    private final boolean systemprop;

    public MapField(Field field, NxProp nxprop) {
        this.field = field;
        this.nxprop = nxprop;
        this.systemprop = nxprop.xpath().startsWith("ecm:");
    }

    public Field getField() {
        return field;
    }

    public NxProp getNxprop() {
        return nxprop;
    }

    public boolean isSystemprop() {
        return systemprop;
    }
}
