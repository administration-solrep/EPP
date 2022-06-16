package fr.dila.ss.core.enumeration;

import fr.dila.ss.core.util.NXQLUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OperatorEnum {
    EGAL("=", "label.operateur.requeteur.egal", OperatorEnum.DISPLAY_1, values -> "'" + values.get(0) + "'"),
    ENTRE(
        "BETWEEN",
        "label.operateur.requeteur.entre",
        OperatorEnum.DISPLAY_2,
        values ->
            NXQLUtils.convertToTimestamp(values.get(0), false) +
            " AND " +
            NXQLUtils.convertToTimestamp(values.get(1), true)
    ),
    PAS_ENTRE(
        "NOT BETWEEN",
        "label.operateur.requeteur.pas.entre",
        OperatorEnum.DISPLAY_2,
        values ->
            NXQLUtils.convertToTimestamp(values.get(0), false) +
            " AND " +
            NXQLUtils.convertToTimestamp(values.get(1), true)
    ),
    PLUS_PETIT(
        "<",
        "label.operateur.requeteur.plus.petit",
        OperatorEnum.DISPLAY_1,
        values -> NXQLUtils.convertToTimestamp(values.get(0), false)
    ),
    PLUS_GRAND(
        ">",
        "label.operateur.requeteur.plus.grand",
        OperatorEnum.DISPLAY_1,
        values -> NXQLUtils.convertToTimestamp(values.get(0), false)
    ),
    DANS("IN", "label.operateur.requeteur.dans", OperatorEnum.DISPLAY_X, getListValueFunction()),
    PAS_DANS("NOT IN", "label.operateur.requeteur.pas.dans", OperatorEnum.DISPLAY_X, getListValueFunction()),
    PLUS_PETIT_EGAL(
        "<=",
        "label.operateur.requeteur.plus.petit.egal",
        OperatorEnum.DISPLAY_1,
        values -> NXQLUtils.convertToTimestamp(values.get(0), false)
    ),
    PLUS_GRAND_EGAL(
        ">=",
        "label.operateur.requeteur.plus.grand.egal",
        OperatorEnum.DISPLAY_1,
        values -> NXQLUtils.convertToTimestamp(values.get(0), false)
    ),
    VIDE("== NULL", "label.operateur.requeteur.vide", OperatorEnum.DISPLAY_0, null),
    PAS_VIDE("!= NULL", "label.operateur.requeteur.pas.vide", OperatorEnum.DISPLAY_0, null),
    CONTIENT(
        "CONTAINS",
        "label.operateur.requeteur.contient",
        OperatorEnum.DISPLAY_1,
        values -> "'" + values.get(0) + "'"
    ),
    COMME("LIKE", "label.operateur.requeteur.comme", OperatorEnum.DISPLAY_1, values -> "'" + values.get(0) + "'"),
    PAS_COMME(
        "NOT LIKE",
        "label.operateur.requeteur.pas.comme",
        OperatorEnum.DISPLAY_1,
        values -> "'" + values.get(0) + "'"
    ),
    DIFFERENT("!=", "label.operateur.requeteur.different", OperatorEnum.DISPLAY_1, values -> "'" + values.get(0) + "'");

    private static final List<OperatorEnum> RANGE_OPERATORS = Arrays.asList(
        ENTRE,
        PAS_ENTRE,
        PLUS_PETIT,
        PLUS_GRAND,
        PLUS_PETIT_EGAL,
        PLUS_GRAND_EGAL
    );

    public static final String DISPLAY_0 = "displayNone";
    public static final String DISPLAY_1 = "displayOne";
    public static final String DISPLAY_2 = "displayTwo";
    public static final String DISPLAY_X = "displayMulti";

    private final String operator;
    private final String label;
    private final String display;
    private final Function<List<String>, String> displayFunction;

    OperatorEnum(String operator, String label, String display, Function<List<String>, String> displayFunction) {
        this.operator = operator;
        this.label = label;
        this.display = display;
        this.displayFunction = displayFunction;
    }

    public String getOperator() {
        return operator;
    }

    public String getLabel() {
        return label;
    }

    public String getDisplay() {
        return display;
    }

    public boolean isRangeOperator() {
        return RANGE_OPERATORS.contains(this);
    }

    public Function<List<String>, String> getDisplayFunction() {
        return displayFunction;
    }

    public static OperatorEnum getByOperator(String operator) {
        return Stream.of(values()).filter(o -> o.getOperator().equals(operator)).findFirst().orElse(null);
    }

    private static Function<List<String>, String> getListValueFunction() {
        return values ->
            Optional
                .ofNullable(values)
                .map(v -> v.stream().map(value -> "'" + value + "'").collect(Collectors.joining(",", "(", ")")))
                .orElse("('')");
    }
}
