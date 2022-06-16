package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwListValues;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwListValuesValidator implements SwValidator<SwListValues> {
    private List<String> values = new ArrayList<>();

    public SwListValuesValidator() {}

    public SwListValuesValidator(List<String> values) {
        this.values = values;
    }

    @Override
    public void initialize(SwListValues constraintAnnotation) {
        values = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(String value) {
        return value == null || values.contains(value);
    }
}
