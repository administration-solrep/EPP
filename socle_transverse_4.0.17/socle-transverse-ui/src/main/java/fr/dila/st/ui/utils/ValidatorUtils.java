package fr.dila.st.ui.utils;

import com.sun.jersey.api.model.Parameter;
import fr.dila.st.ui.validators.SwNotNullValidator;
import fr.dila.st.ui.validators.SwValidator;
import fr.dila.st.ui.validators.annot.SwConstraint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.NuxeoException;

public final class ValidatorUtils {

    private ValidatorUtils() {
        // Utility class
    }

    public static boolean isValidEntity(Object entity) {
        AtomicReference<Boolean> isValid = new AtomicReference<>(true);
        if (entity != null) {
            List<Field> annotatedFields = getConstraintFields(entity.getClass());
            annotatedFields.forEach(f -> isValid.set(isValid.get() && isValidField(f, entity)));
        }
        return isValid.get();
    }

    public static boolean isValidField(Field field, Object entity) {
        return isValidField(field, getFieldValue(field, entity));
    }

    public static boolean isValidField(Field field, String value) {
        return isValid(field.getAnnotations(), value);
    }

    public static boolean isValid(Annotation[] annots, String value) {
        return Arrays
            .stream(annots)
            .filter(ValidatorUtils::isSwConstraintAnnotation)
            .map(ValidatorUtils::getValidator)
            .allMatch(validator -> validator.isValid(value));
    }

    public static List<Field> getConstraintFields(Class<?> clazz) {
        return Arrays
            .stream(clazz.getDeclaredFields())
            .filter(ValidatorUtils::isFieldWithConstraint)
            .collect(Collectors.toList());
    }

    public static boolean isFieldWithConstraint(Field field) {
        return isParameterWithConstraint(field.getAnnotations());
    }

    public static boolean isValidParameter(Parameter param, String value) {
        return isValid(param.getAnnotations(), value);
    }

    public static boolean isParameterWithConstraint(Annotation[] annots) {
        return Arrays.stream(annots).anyMatch(ValidatorUtils::isSwConstraintAnnotation);
    }

    private static <T extends Annotation> SwValidator<T> getValidator(T a) {
        SwConstraint constraint = a.annotationType().getAnnotation(SwConstraint.class);

        try {
            SwValidator<T> validator;
            if (constraint != null) {
                validator = (SwValidator<T>) constraint.validatedBy().newInstance();
            } else {
                validator = (SwValidator<T>) SwNotNullValidator.class.newInstance();
            }
            validator.initialize(a);
            return validator;
        } catch (InstantiationException | IllegalAccessException exception) {
            throw new NuxeoException(String.format("Validateur non trouvé pour la class %s", a.getClass()), exception);
        }
    }

    private static String getFieldValue(Field field, Object entity) {
        String value = null;

        field.setAccessible(true);
        try {
            Object fieldValue = field.get(entity);
            if (fieldValue != null) {
                value = fieldValue.toString();
            }
        } catch (IllegalArgumentException | IllegalAccessException exception) {
            throw new NuxeoException(
                String.format(
                    "Impossible de récupérer la valeur du champ %s de l'objet %s",
                    field.getName(),
                    entity.getClass()
                ),
                exception
            );
        }
        field.setAccessible(false);

        return value;
    }

    private static boolean isSwConstraintAnnotation(Annotation annotation) {
        return annotation.annotationType().getAnnotation(SwConstraint.class) != null;
    }
}
