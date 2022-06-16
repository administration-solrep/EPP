package fr.dila.ss.api.constant;

/**
 * Colonnes du fichier d'import/export de gouvernement, avec leur index.
 *
 * @author tlombard
 */
public enum SSInjGvtColumnsEnum {
    INJ_COL_NOR(0),
    INJ_COL_OPS(1),
    INJ_COL_A_CREER_SOLON(2),
    INJ_COL_OPR(3),
    INJ_COL_A_CREER_REP(4),
    INJ_COL_A_MODIFIER_SOLON(5),
    INJ_COL_LIB_COURT(6),
    INJ_COL_LIB_LONG(7),
    INJ_COL_ENTETE(8),
    INJ_COL_CIVILITE(9),
    INJ_COL_PRENOM(10),
    INJ_COL_NOM(11),
    INJ_COL_PRENOM_NOM(12),
    INJ_COL_DATE_DEB(13),
    INJ_COL_DATE_FIN(14),
    INJ_COL_NOR_EPP(15),
    INJ_COL_NOUV_ENTITE_EPP(16),
    INJ_COL_A_MODIFIER_REP(17),
    INJ_COL_IDENTIFIANT_REP(18);

    private final int index;

    SSInjGvtColumnsEnum(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
