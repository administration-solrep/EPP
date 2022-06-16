package fr.dila.ss.core.export.enums;

import fr.dila.st.core.export.enums.ExcelHeader;

public enum SSExcelHeader implements ExcelHeader {
    CATEGORIE,
    COMMENTAIRE,
    DATE,
    DATE_CONNEXION("date.connexion"),
    DATE_DERNIERE_CONNEXION("date.derniere.connexion"),
    DATE_FIN("date.fin"),
    DIRECTION,
    DOSSIER,
    MESSAGE,
    MINISTERE,
    STATUT;

    private final String specificLabelKey;

    SSExcelHeader() {
        this(null);
    }

    SSExcelHeader(String specificLabelKey) {
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
