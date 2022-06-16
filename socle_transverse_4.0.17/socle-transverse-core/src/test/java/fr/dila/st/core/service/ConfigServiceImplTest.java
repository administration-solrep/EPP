package fr.dila.st.core.service;

import static fr.dila.st.core.service.ConfigServiceImpl.UNDEFINED_PARAMETER_ERROR_MESSAGE;
import static fr.dila.st.core.service.STServiceLocator.getConfigService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.test.STFeature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test du service de configuration de l'application.
 *
 * @author jtremeaux
 */
@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
public class ConfigServiceImplTest {
    private ConfigService service;

    @Before
    public void setUp() {
        service = getConfigService();
    }

    @Test
    public void shouldGetValue() {
        assertThat(service.getValue("app.some.string.value")).isEqualTo("someName");
    }

    @Test
    public void shouldGetExistingValueWithDefaultValue() {
        String parameterName = "app.some.string.value";
        String parameterValue = "someValue";
        System.setProperty(parameterName, parameterValue);

        assertThat(service.getValue(parameterName, "test default value")).isEqualTo(parameterValue);
    }

    @Test
    public void shouldGetDefaultValue() {
        String defaultValue = "test default value";
        assertThat(service.getValue("undefined.parameter", defaultValue)).isEqualTo(defaultValue);
    }

    @Test
    public void shouldGetTrueValue() {
        assertThat(service.getBooleanValue("app.some.true.value")).isTrue();
    }

    @Test
    public void shouldGetFalseValue() {
        assertThat(service.getBooleanValue("app.some.false.value")).isFalse();
    }

    @Test
    public void shouldGetIntegerValue() {
        assertThat(service.getIntegerValue("app.some.integer.value")).isEqualTo(42);
    }

    @Test
    public void shouldGetDoubleValue() {
        assertThat(service.getDoubleValue("app.some.double.value")).isEqualTo(1.234);
    }

    @Test
    public void getValueShouldFailForUndefinedParameter() {
        String parameterName = "undefined.parameter";

        Throwable throwable = catchThrowable(() -> service.getValue(parameterName));
        assertThat(throwable)
            .isInstanceOf(RuntimeException.class)
            .hasMessage(UNDEFINED_PARAMETER_ERROR_MESSAGE, parameterName);
    }
}
