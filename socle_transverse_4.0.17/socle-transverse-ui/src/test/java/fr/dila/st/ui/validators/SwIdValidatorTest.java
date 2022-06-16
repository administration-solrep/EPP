package fr.dila.st.ui.validators;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.ui.validators.annot.SwId;
import org.junit.Before;
import org.junit.Test;

public class SwIdValidatorTest {
    private SwIdValidator validator;

    @Before
    public void setUp() throws Exception {
        validator = new SwIdValidator();

        validator.initialize(SwValidatorTest.class.getField("id").getAnnotation(SwId.class));
    }

    @Test
    public void testIsValidWithValidId() {
        assertThat(validator.isValid("reer.ETREGEF-29")).isTrue();
    }

    @Test
    public void testIsValidWithInvalidId() {
        assertThat(validator.isValid("@reer.ETREGEF-29*")).isFalse();
    }

    private class SwValidatorTest {
        @SwId
        public String id;
    }
}
