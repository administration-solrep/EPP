package fr.dila.st.core.export.enums;

import fr.dila.st.core.util.ResourceHelper;
import java.util.Optional;

public interface ExcelHeader {
    String getName();

    String getSpecificLabelKey();

    default String getLabelKey() {
        return getLabelKeyPrefix() + Optional.ofNullable(getSpecificLabelKey()).orElse(getName().toLowerCase());
    }

    default String getLabel() {
        return ResourceHelper.getString(getLabelKey());
    }

    default String getLabelKeyPrefix() {
        return "export.header.";
    }
}
