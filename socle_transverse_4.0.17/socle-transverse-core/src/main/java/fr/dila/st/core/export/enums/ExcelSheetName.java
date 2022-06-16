package fr.dila.st.core.export.enums;

import java.util.List;

public interface ExcelSheetName {
    String getLabel();

    List<ExcelHeader> getHeaders();

    default int getHeadersSize() {
        return getHeaders().size();
    }

    default String[] getHeadersLabels() {
        return getHeaders().stream().map(ExcelHeader::getLabel).toArray(String[]::new);
    }

    default boolean isPdfLandscape() {
        return false;
    }

    /**
     * If set, some columns width in the pdf will be fixed.
     * Also their text content will be wrapped and their height set accordingly.
     */
    default Double[] getPdfDynamicColumnWidths() {
        return new Double[getHeadersSize()];
    }

    default String[] getHeadersLabelKeys() {
        return getHeaders().stream().map(ExcelHeader::getLabelKey).toArray(String[]::new);
    }
}
