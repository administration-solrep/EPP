package fr.dila.st.ui.jaxrs.provider;

import static fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl.LOG_EXCEPTION_TEC;
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.utils.ValidatorUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

enum PropertyTypeConverter {
    ARRAY_LIST(field -> ArrayList.class.isAssignableFrom(field.getType()), PropertyTypeConverter::convertValuesToList),
    BOOLEAN(
        field -> Boolean.class.equals(field.getType()) || boolean.class.equals(field.getType()),
        (field, valuesToConvert) ->
            validateAndConvertValues(
                field,
                valuesToConvert,
                valueToConvert -> BooleanUtils.toBooleanObject(valueToConvert, "true", "false", null)
            )
    ),
    CALENDAR(
        field -> Calendar.class.equals(field.getType()),
        (field, valuesToConvert) ->
            validateAndConvertValues(field, valuesToConvert, SolonDateConverter.DATE_SLASH::parseToCalendarOrNull)
    ),
    ENUM(
        field -> field.getType().isEnum(),
        (field, valuesToConvert) ->
            validateAndConvertValues(
                field,
                valuesToConvert,
                valueToConvert -> convertValueToEnum(field, valueToConvert)
            )
    ),
    STRING(
        field -> String.class.equals(field.getType()),
        (field, valuesToConvert) -> validateAndConvertValue(field, valuesToConvert.get(0), v -> v)
    ),
    OTHER(PropertyTypeConverter::validateAndConvertValueWithObjectMapper);

    private static final STLogger LOGGER = STLogFactory.getLog(PropertyTypeConverter.class);
    private static final String REQUEST_BAD_PARAM_ERROR_MESSAGE = "request.bad.param";
    private static final String CONVERSION_WITH_FIELD_ERROR_MESSAGE =
        "Une erreur s'est produite lors de la conversion de la valeur [%s] pour le champ [%s]";

    private final Predicate<Field> propertyTypeCondition;
    private final BiFunction<Field, List<String>, Object> converterFunction;

    PropertyTypeConverter(BiFunction<Field, List<String>, Object> converterFunction) {
        this(null, converterFunction);
    }

    PropertyTypeConverter(
        Predicate<Field> propertyTypeCondition,
        BiFunction<Field, List<String>, Object> converterFunction
    ) {
        this.propertyTypeCondition = propertyTypeCondition;
        this.converterFunction = converterFunction;
    }

    public Predicate<Field> getPropertyTypeCondition() {
        return propertyTypeCondition;
    }

    public BiFunction<Field, List<String>, Object> getConverterFunction() {
        return converterFunction;
    }

    public Object convertFieldValue(Field field, List<String> values) {
        return getConverterFunction().apply(field, values);
    }

    public static PropertyTypeConverter fromField(Field field) {
        return Stream
            .of(values())
            .filter(
                convertor ->
                    convertor.getPropertyTypeCondition() != null && convertor.getPropertyTypeCondition().test(field)
            )
            .findFirst()
            .orElse(OTHER);
    }

    private static Object convertValuesToList(Field field, List<String> valuesToConvert) {
        ParameterizedType listType = (ParameterizedType) field.getGenericType();
        Class<?> clazz = (Class<?>) listType.getActualTypeArguments()[0];
        if (clazz.equals(String.class)) {
            return valuesToConvert
                .stream()
                .map(value -> validateAndConvertValue(field, value, v -> v))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        } else {
            return valuesToConvert
                .stream()
                .map(value -> validateAndConvertValueWithObjectMapper(field, clazz, value))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }
    }

    private static Object validateAndConvertValueWithObjectMapper(Field field, List<String> valuesToConvert) {
        return validateAndConvertValueWithObjectMapper(field, valuesToConvert.get(0));
    }

    private static Object validateAndConvertValueWithObjectMapper(Field field, String valueToConvert) {
        return validateAndConvertValueWithObjectMapper(field, field.getType(), valueToConvert);
    }

    private static Object validateAndConvertValueWithObjectMapper(
        Field field,
        Class<?> valueType,
        String valueToConvert
    ) {
        try {
            return validateAndConvertValueWithObjectMapper(valueType, valueToConvert);
        } catch (IOException e) {
            LOGGER.error(LOG_EXCEPTION_TEC, String.format(CONVERSION_WITH_FIELD_ERROR_MESSAGE, valueToConvert, field));
            throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE, e);
        }
    }

    private static Object validateAndConvertValueWithObjectMapper(Class<?> valueType, String valueToConvert)
        throws IOException {
        Object entity = null;

        if (StringUtils.isNotBlank(valueToConvert)) {
            ObjectMapper objectMapper = new ObjectMapper();
            entity = objectMapper.readValue(valueToConvert, valueType);

            if (!ValidatorUtils.isValidEntity(entity)) {
                LOGGER.error(
                    LOG_EXCEPTION_TEC,
                    ResourceHelper.getString(
                        "back.validation.error.invalid.entity",
                        entity.getClass().getSimpleName(),
                        valueToConvert
                    )
                );
                throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE);
            }
        }

        return entity;
    }

    private static Object validateAndConvertValues(
        Field field,
        List<String> valuesToConvert,
        Function<String, Object> convertor
    ) {
        String valueToConvert = valuesToConvert.get(0);
        try {
            return validateAndConvertValue(field, valueToConvert, convertor);
        } catch (IllegalArgumentException e) {
            LOGGER.error(LOG_EXCEPTION_TEC, String.format(CONVERSION_WITH_FIELD_ERROR_MESSAGE, valueToConvert, field));
            throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE, e);
        }
    }

    private static Object validateAndConvertValue(
        Field field,
        String valueToConvert,
        Function<String, Object> convertor
    ) {
        validateField(field, valueToConvert);
        return convertor.apply(valueToConvert);
    }

    private static void validateField(Field field, String value) {
        if (!ValidatorUtils.isValidField(field, value)) {
            LOGGER.error(
                LOG_EXCEPTION_TEC,
                ResourceHelper.getString("back.validation.error.invalid.field", field.getName(), value)
            );
            throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE);
        }
    }

    private static Object convertValueToEnum(Field field, String valueToConvert) {
        try {
            Method fromValue = field.getType().getDeclaredMethod("fromValue", String.class);
            return fromValue.invoke(null, valueToConvert);
        } catch (ReflectiveOperationException e) {
            throw new NuxeoException(
                String.format(CONVERSION_WITH_FIELD_ERROR_MESSAGE, valueToConvert, field) +
                " vers l'enum de type " +
                field.getType() +
                " avec la méthode statique fromValue",
                e,
                SC_BAD_REQUEST
            );
        }
    }
}
