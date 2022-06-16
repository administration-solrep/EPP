package fr.dila.ss.core.enumeration;

import static fr.dila.ss.core.enumeration.OperatorEnum.COMME;
import static fr.dila.ss.core.enumeration.OperatorEnum.CONTIENT;
import static fr.dila.ss.core.enumeration.OperatorEnum.DANS;
import static fr.dila.ss.core.enumeration.OperatorEnum.DIFFERENT;
import static fr.dila.ss.core.enumeration.OperatorEnum.EGAL;
import static fr.dila.ss.core.enumeration.OperatorEnum.ENTRE;
import static fr.dila.ss.core.enumeration.OperatorEnum.PAS_COMME;
import static fr.dila.ss.core.enumeration.OperatorEnum.PAS_DANS;
import static fr.dila.ss.core.enumeration.OperatorEnum.PAS_ENTRE;
import static fr.dila.ss.core.enumeration.OperatorEnum.PAS_VIDE;
import static fr.dila.ss.core.enumeration.OperatorEnum.PLUS_GRAND;
import static fr.dila.ss.core.enumeration.OperatorEnum.PLUS_GRAND_EGAL;
import static fr.dila.ss.core.enumeration.OperatorEnum.PLUS_PETIT;
import static fr.dila.ss.core.enumeration.OperatorEnum.PLUS_PETIT_EGAL;
import static fr.dila.ss.core.enumeration.OperatorEnum.VIDE;

import java.util.Arrays;
import java.util.List;

public enum TypeChampEnum {
    TEXT("text", TypeChampEnum.SIMPLE_INPUT_PATH, Arrays.asList(EGAL)),
    TEXT_AUTOCOMPLETE("text_autocomplete", TypeChampEnum.MULTIPLE_SELECT_PATH, Arrays.asList(EGAL)),
    DATES("dates", TypeChampEnum.DATE_INPUT_PATH, Arrays.asList(ENTRE, PAS_ENTRE, PLUS_PETIT, PLUS_GRAND)),
    ORGANIGRAMME(TypeChampEnum.ORGANIGRAMME_TEXT, TypeChampEnum.ORGANIGRAMME_INPUT_PATH, Arrays.asList(EGAL)),
    ORGANIGRAMME_VIDE(
        TypeChampEnum.ORGANIGRAMME_TEXT,
        TypeChampEnum.ORGANIGRAMME_INPUT_PATH,
        Arrays.asList(EGAL, VIDE, PAS_VIDE)
    ),
    ORGANIGRAMME_VIDE_ID(
        TypeChampEnum.ORGANIGRAMME_TEXT,
        TypeChampEnum.ORGANIGRAMME_INPUT_PATH,
        Arrays.asList(EGAL, VIDE, PAS_VIDE)
    ),
    SIMPLE_SELECT("simple_select", TypeChampEnum.SIMPLE_SELECT_PATH, Arrays.asList(EGAL)),
    MULTIPLE_SELECT("multiple_select", TypeChampEnum.MULTIPLE_SELECT_PATH, Arrays.asList(DANS, PAS_DANS)),
    SIMPLE_SELECT_BOOLEAN("simple_select_boolean", TypeChampEnum.SIMPLE_SELECT_PATH, Arrays.asList(EGAL)),
    DATES_ES(
        "dates",
        TypeChampEnum.DATE_INPUT_PATH,
        Arrays.asList(ENTRE, PAS_ENTRE, PLUS_PETIT, PLUS_GRAND, PLUS_PETIT_EGAL, PLUS_GRAND_EGAL, VIDE, PAS_VIDE)
    ),
    TEXT_ES(
        "text",
        TypeChampEnum.SIMPLE_INPUT_PATH,
        Arrays.asList(COMME, PAS_COMME, EGAL, DIFFERENT, VIDE, PAS_VIDE, CONTIENT)
    ),
    TEXT_COMME("text", TypeChampEnum.SIMPLE_INPUT_PATH, Arrays.asList(COMME, PAS_COMME)),
    SIMPLE_SELECT_ES("simple_select", TypeChampEnum.SIMPLE_SELECT_PATH, Arrays.asList(EGAL, VIDE, PAS_VIDE)),
    SIMPLE_SELECT_BOOLEAN_ES(
        "simple_select_boolean",
        TypeChampEnum.SIMPLE_SELECT_PATH,
        Arrays.asList(EGAL, VIDE, PAS_VIDE)
    );

    private static final String ORGANIGRAMME_TEXT = "organigramme";

    private final String label;
    private final String pathToFragment;
    private final List<OperatorEnum> availableOperators;

    TypeChampEnum(String label, String pathToFragment, List<OperatorEnum> availableOperators) {
        this.label = label;
        this.pathToFragment = pathToFragment;
        this.availableOperators = availableOperators;
    }

    private static final String SIMPLE_INPUT_PATH = "fragments/components/formblocks/simple-input";
    private static final String DATE_INPUT_PATH = "fragments/components/date-picker-requete";
    private static final String ORGANIGRAMME_INPUT_PATH = "fragments/components/organigrammeSelectAutocomplete";
    private static final String SIMPLE_SELECT_PATH = "fragments/components/formblocks/simple-select";
    private static final String MULTIPLE_SELECT_PATH = "fragments/components/formblocks/autocomplete-select";

    public String getLabel() {
        return label;
    }

    public String getPathToFragment() {
        return pathToFragment;
    }

    public List<OperatorEnum> getAvailableOperators() {
        return availableOperators;
    }
}
