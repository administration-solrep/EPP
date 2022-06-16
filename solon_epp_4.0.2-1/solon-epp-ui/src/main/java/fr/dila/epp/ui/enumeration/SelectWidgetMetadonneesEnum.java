package fr.dila.epp.ui.enumeration;

import static fr.dila.st.ui.enums.WidgetTypeEnum.INPUT_TEXT;
import static fr.dila.st.ui.enums.WidgetTypeEnum.RADIO;
import static fr.dila.st.ui.enums.WidgetTypeEnum.SELECT;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppMetaConstant;
import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.st.ui.enums.WidgetTypeEnum;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum SelectWidgetMetadonneesEnum {
    /* ******************* META TYPE SELECT ONE DIRECTORY **************** */
    META_TYPE_BOOLEAN(SolonEppMetaConstant.META_TYPE_BOOLEAN, RADIO, SolonEppVocabularyConstant.BOOLEAN_VOCABULARY),
    META_TYPE_TYPE_ACTE(
        SolonEppMetaConstant.META_TYPE_TYPE_ACTE,
        SELECT,
        SolonEppVocabularyConstant.TYPE_ACTE_VOCABULARY
    ),
    META_TYPE_TYPE_LOI(SolonEppMetaConstant.META_TYPE_TYPE_LOI, SELECT, SolonEppVocabularyConstant.TYPE_LOI_VOCABULARY),
    META_TYPE_RESULTAT_CMP(
        SolonEppMetaConstant.META_TYPE_RESULTAT_CMP,
        SELECT,
        SolonEppVocabularyConstant.RESULTAT_CMP_VOCABULARY
    ),
    META_TYPE_ATTRIBUTION_COMMISSION(
        SolonEppMetaConstant.META_TYPE_ATTRIBUTION_COMMISSION,
        SELECT,
        SolonEppVocabularyConstant.ATTRIBUTION_COMMISSION_VOCABULARY
    ),
    META_TYPE_SORT_ADOPTION(
        SolonEppMetaConstant.META_TYPE_SORT_ADOPTION,
        SELECT,
        SolonEppVocabularyConstant.SORT_ADOPTION_VOCABULARY
    ),
    META_TYPE_SENS_AVIS(
        SolonEppMetaConstant.META_TYPE_SENS_AVIS,
        SELECT,
        SolonEppVocabularyConstant.SENS_AVIS_VOCABULARY
    ),
    META_TYPE_RAPPORT_PARLEMENT(
        SolonEppMetaConstant.META_TYPE_RAPPORT_PARLEMENT,
        SELECT,
        SolonEppVocabularyConstant.RAPPORT_PARLEMENT_VOCABULARY
    ),
    META_TYPE_NATURE_RAPPORT(
        SolonEppMetaConstant.META_TYPE_NATURE_RAPPORT,
        SELECT,
        SolonEppVocabularyConstant.NATURE_RAPPORT_VOCABULARY
    ),
    META_TYPE_NATURE_LOI(
        SolonEppMetaConstant.META_TYPE_NATURE_LOI,
        SELECT,
        SolonEppVocabularyConstant.NATURE_LOI_VOCABULARY
    ),
    META_TYPE_MOTIF_IRRECEVABILITE(
        SolonEppMetaConstant.META_TYPE_MOTIF_IRRECEVABILITE,
        SELECT,
        SolonEppVocabularyConstant.MOTIF_IRRECEVABILITE_VOCABULARY
    ),
    META_TYPE_DECISION_PROC_ACC(
        SolonEppMetaConstant.META_TYPE_DECISION_PROC_ACC,
        SELECT,
        SolonEppVocabularyConstant.DECISION_PROC_ACC
    ),
    META_TYPE_RUBRIQUE(
        SolonEppMetaConstant.META_TYPE_RUBRIQUE,
        SELECT,
        SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY
    ),

    /* ****************** META TYPE TABLE REF ******************************* */
    META_TYPE_MANDAT(SolonEppMetaConstant.META_TYPE_MANDAT, INPUT_TEXT, SolonEppConstant.IDENTITE_DOC_TYPE),
    META_TYPE_ORGANISME(SolonEppMetaConstant.META_TYPE_ORGANISME, INPUT_TEXT, SolonEppConstant.ORGANISME_DOC_TYPE);

    private WidgetTypeEnum widgetType;
    private String propertyType;
    private List<String> properties;

    SelectWidgetMetadonneesEnum(String propertyType, WidgetTypeEnum widgetType, String... properties) {
        this.propertyType = propertyType;
        this.widgetType = widgetType;
        this.properties = Arrays.asList(properties);
    }

    public static boolean containsProperty(String property) {
        return Stream.of(values()).anyMatch(swme -> swme.getPropertyType().equals(property));
    }

    public static SelectWidgetMetadonneesEnum getEnumFromProperty(String property) {
        return Stream.of(values()).filter(swme -> swme.getPropertyType().equals(property)).findFirst().orElse(null);
    }

    public String getPropertyType() {
        return propertyType;
    }

    public List<String> getProperties() {
        return properties;
    }

    public WidgetTypeEnum getWidgetType() {
        return widgetType;
    }
}
