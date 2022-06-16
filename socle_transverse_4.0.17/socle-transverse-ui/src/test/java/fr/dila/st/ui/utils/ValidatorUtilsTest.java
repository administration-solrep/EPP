package fr.dila.st.ui.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import fr.dila.st.ui.utils.model.ValidatorTestDTO;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.Test;
import org.nuxeo.ecm.core.api.NuxeoException;

public class ValidatorUtilsTest {
    private static final String FIELD_ID_BRANCHE = "idBranche";
    private static final String FIELD_TYPE_AJOUT = "typeAjout";
    private static final String FIELD_TYPE_CREATION = "typeCreation";
    private static final String FIELD_LIST_VALUES = "listValues";
    private static final String FIELD_LENGTH = "length";

    @Test
    public void testIsValidEntity() {
        ValidatorTestDTO entity = new ValidatorTestDTO();
        entity.setIdBranche("test");

        assertThat(ValidatorUtils.isValidEntity(entity)).isTrue();

        assertThat(ValidatorUtils.isValidEntity(new ValidatorTestDTO())).isFalse();
    }

    @Test
    public void testIsValidFieldWithError() {
        ValidatorTestDTO entity = new ValidatorTestDTO();

        Field field = ValidatorUtils.getConstraintFields(entity.getClass()).get(0);

        Integer differentEntity = 10;

        Throwable throwable = catchThrowable(() -> ValidatorUtils.isValidField(field, differentEntity));

        assertThat(throwable)
            .isInstanceOf(NuxeoException.class)
            .hasMessage(
                "Impossible de récupérer la valeur du champ %s de l'objet %s",
                field.getName(),
                differentEntity.getClass()
            );
    }

    @Test
    public void testIsFieldWithConstraint() throws NoSuchFieldException, SecurityException {
        assertTrue(ValidatorUtils.isFieldWithConstraint(ValidatorTestDTO.class.getDeclaredField(FIELD_ID_BRANCHE)));
        assertFalse(ValidatorUtils.isFieldWithConstraint(ValidatorTestDTO.class.getDeclaredField(FIELD_TYPE_AJOUT)));
        assertFalse(ValidatorUtils.isFieldWithConstraint(ValidatorTestDTO.class.getDeclaredField(FIELD_TYPE_CREATION)));
    }

    @Test
    public void testGetConstraintFields() {
        List<Field> listConstraintField = ValidatorUtils.getConstraintFields(ValidatorTestDTO.class);
        assertThat(listConstraintField)
            .extracting(Field::getName, Field::getType)
            .containsExactly(
                tuple(FIELD_ID_BRANCHE, String.class),
                tuple(FIELD_LIST_VALUES, String.class),
                tuple(FIELD_LENGTH, String.class)
            );
    }

    @Test
    public void testIsValidAnnotationSwListValues() {
        ValidatorTestDTO entity = new ValidatorTestDTO();

        Annotation[] annotations = ValidatorUtils.getConstraintFields(entity.getClass()).get(1).getAnnotations();

        assertThat(ValidatorUtils.isValid(annotations, "25")).isTrue();
        assertThat(ValidatorUtils.isValid(annotations, "3")).isFalse();
    }

    @Test
    public void testIsValidAnnotationSwLength() {
        ValidatorTestDTO entity = new ValidatorTestDTO();

        Annotation[] annotations = ValidatorUtils.getConstraintFields(entity.getClass()).get(2).getAnnotations();

        assertThat(ValidatorUtils.isValid(annotations, "testok")).isTrue();
        assertThat(ValidatorUtils.isValid(annotations, "test-invalid")).isFalse();
    }
}
