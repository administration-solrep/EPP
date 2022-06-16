package fr.dila.epp.ui.enumeration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Test;

public class SuggestionTypeTest {
    private static final Map<String, SuggestionType> mapSuggestType = Arrays
        .stream(SuggestionType.values())
        .collect(Collectors.toMap(SuggestionType::getValue, Function.identity()));

    @Test
    public void fromValue() {
        mapSuggestType
            .entrySet()
            .stream()
            .forEach(
                suggestion -> assertThat(SuggestionType.fromValue(suggestion.getKey())).isEqualTo(suggestion.getValue())
            );
    }

    @Test
    public void getValue() {
        mapSuggestType
            .entrySet()
            .stream()
            .forEach(suggestion -> assertThat(suggestion.getValue().getValue()).isEqualTo(suggestion.getKey()));
    }
}
