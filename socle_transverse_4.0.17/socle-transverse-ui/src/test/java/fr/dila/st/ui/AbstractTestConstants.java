package fr.dila.st.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import fr.dila.st.ui.th.constants.STTemplateConstants;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTestConstants<T> {
    private List<Field> constants;

    @Before
    public void setUp() {
        constants = getConstantFromClass(getConstantClass());
    }

    @Test
    public void constantsShouldBeSorted() {
        List<String> constantNames = constants.stream().map(Field::getName).collect(Collectors.toList());
        assertThat(constantNames).isSorted();
    }

    @Test
    public void constantsShouldBeNamedCorrectly() {
        assertThat(constants).allSatisfy(AbstractTestConstants::verifyConstantName);
    }

    protected abstract Class<T> getConstantClass();

    protected List<Field> getConstants() {
        return constants;
    }

    protected static List<Field> getConstantFromClass(Class<?> className) {
        return Stream
            .of(className.getDeclaredFields())
            .filter(AbstractTestConstants::isConstantField)
            .collect(Collectors.toList());
    }

    protected static String getConstantValue(Field constant) {
        try {
            return constant.get(null).toString();
        } catch (IllegalAccessException e) {
            fail("une erreur s'est produite lors de la récupération de la valeur de la constante : " + constant, e);
            return null;
        }
    }

    protected void verifyDuplicatesWithSTTemplateConstants() {
        verifyDuplicatesWithClass(STTemplateConstants.class);
    }

    protected void verifyDuplicatesWithClasses(Class<?>... classNames) {
        Stream.of(classNames).forEach(this::verifyDuplicatesWithClass);
    }

    protected void verifyDuplicatesWithClass(Class<?> className) {
        List<String> allConstants = Stream
            .concat(getConstantFromClass(className).stream(), getConstants().stream())
            .map(AbstractTestConstants::getConstantValue)
            .collect(Collectors.toList());
        assertThat(allConstants)
            .as("Il y a des constantes en double avec celles de la classe %s", className.getSimpleName())
            .doesNotHaveDuplicates();
    }

    private static boolean isConstantField(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
    }

    private static String camelCaseToSnakeUppercase(String camelCase) {
        String[] camelCaseParts = StringUtils.splitByCharacterTypeCamelCase(camelCase);
        return StringUtils.join(Stream.of(camelCaseParts).map(String::toUpperCase).collect(Collectors.toList()), "_");
    }

    private static void verifyConstantName(Field constant) {
        assertThat(constant.getName()).isEqualTo(camelCaseToSnakeUppercase(getConstantValue(constant)));
    }
}
