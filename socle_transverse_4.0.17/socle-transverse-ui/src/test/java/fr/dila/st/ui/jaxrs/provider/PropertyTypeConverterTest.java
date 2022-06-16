package fr.dila.st.ui.jaxrs.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.google.common.collect.ImmutableList;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.ui.validators.annot.SwLength;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class PropertyTypeConverterTest {
    private static final String REQUEST_BAD_PARAM_ERROR_MESSAGE = "Requête invalide";

    @Test
    public void testFromField() {
        Field[] fields = TestConvertor1.class.getFields();

        List<PropertyTypeConverter> results = Stream
            .of(fields)
            .map(PropertyTypeConverter::fromField)
            .collect(Collectors.toList());

        assertThat(results)
            .containsExactly(
                PropertyTypeConverter.BOOLEAN,
                PropertyTypeConverter.STRING,
                PropertyTypeConverter.CALENDAR,
                PropertyTypeConverter.OTHER,
                PropertyTypeConverter.ARRAY_LIST
            );
    }

    @Test
    public void testConvertListStringValues() throws NoSuchFieldException {
        String value1ToConvert = "test1";
        String value2ToConvert = "test2";
        Field field = TestConvertor1.class.getField("testArrayList");

        Object result = PropertyTypeConverter.ARRAY_LIST.convertFieldValue(
            field,
            ImmutableList.of(value1ToConvert, value2ToConvert)
        );

        assertThat(result).isInstanceOf(ArrayList.class).asList().containsExactly(value1ToConvert, value2ToConvert);
    }

    @Test
    public void testConvertListValuesWithError() throws NoSuchFieldException {
        String valueToConvert = "test";
        Field field = TestConvertor2.class.getField("testArrayList");

        Throwable throwable = catchThrowable(
            () -> PropertyTypeConverter.ARRAY_LIST.convertFieldValue(field, Collections.singletonList(valueToConvert))
        );

        assertThat(throwable)
            .isExactlyInstanceOf(STValidationException.class)
            .hasMessage(REQUEST_BAD_PARAM_ERROR_MESSAGE);
    }

    @Test
    public void testConvertStringWithInvalidValue() throws NoSuchFieldException {
        String valueToConvert = "test value too long";
        Field field = TestConvertor1.class.getField("testString");

        Throwable throwable = catchThrowable(
            () -> PropertyTypeConverter.STRING.convertFieldValue(field, Collections.singletonList(valueToConvert))
        );

        assertThat(throwable)
            .isExactlyInstanceOf(STValidationException.class)
            .hasMessage(REQUEST_BAD_PARAM_ERROR_MESSAGE);
    }

    public class TestConvertor1 {
        public Boolean testBoolean;

        @SwLength(max = 5)
        public String testString;

        public Calendar testCalendar;
        public Map<String, String> testMap;
        public ArrayList<String> testArrayList;
    }

    public class TestConvertor2 {
        public ArrayList<TestConvertor1> testArrayList;

        public TestConvertor2(ArrayList<TestConvertor1> testArrayList) {
            this.testArrayList = testArrayList;
        }
    }
}
