package fr.dila.st.core.export.enums;

public enum STExcelHeader implements ExcelHeader {
    DATE_DEBUT("date.debut"),
    MEL,
    NOM,
    POSTE,
    PRENOM,
    TELEPHONE,
    TITRE,
    UTILISATEUR;

    private final String specificLabelKey;

    STExcelHeader() {
        this(null);
    }

    STExcelHeader(String specificLabelKey) {
        this.specificLabelKey = specificLabelKey;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getSpecificLabelKey() {
        return specificLabelKey;
    }
}
