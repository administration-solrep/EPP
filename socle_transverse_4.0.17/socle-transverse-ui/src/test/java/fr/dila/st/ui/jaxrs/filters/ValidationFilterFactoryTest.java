package fr.dila.st.ui.jaxrs.filters;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.jersey.api.model.AbstractMethod;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.validators.annot.SwId;
import fr.dila.st.ui.validators.annot.SwRegex;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ValidationFilterFactoryTest {
    private static ValidationFilterFactory factory = new ValidationFilterFactory();
    private AbstractMethod method;

    @Before
    public void setup() {
        method = Mockito.mock(AbstractMethod.class);
    }

    @Test
    public void testCreateWithoutParameter() throws NoSuchMethodException, SecurityException {
        Mockito.when(method.getMethod()).thenReturn(this.getClass().getDeclaredMethod("methodWithoutParameter"));

        assertThat(factory.create(method)).isNullOrEmpty();
    }

    @Test
    public void testCreateWithParameterWithoutValidator() throws NoSuchMethodException, SecurityException {
        Mockito
            .when(method.getMethod())
            .thenReturn(
                this.getClass()
                    .getDeclaredMethod("methodWithParameterWithoutConstraint", String.class, String.class, String.class)
            );

        assertThat(factory.create(method)).isNullOrEmpty();
    }

    @Test
    public void testCreateWithParameterWithIdValidator() throws NoSuchMethodException, SecurityException {
        Mockito
            .when(method.getMethod())
            .thenReturn(
                this.getClass()
                    .getDeclaredMethod("methodWithParameterWithIdConstraint", String.class, String.class, String.class)
            );

        assertThat(factory.create(method)).isNotEmpty();
    }

    @Test
    public void testCreateWithParameterWithRegexValidator() throws NoSuchMethodException, SecurityException {
        Mockito
            .when(method.getMethod())
            .thenReturn(
                this.getClass()
                    .getDeclaredMethod(
                        "methodWithParameterWithRegexConstraint",
                        String.class,
                        String.class,
                        String.class
                    )
            );

        assertThat(factory.create(method)).isNotEmpty();
    }

    @SuppressWarnings("unused")
    private void methodWithoutParameter() {
        //NOP
    }

    @SuppressWarnings("unused")
    private void methodWithParameterWithoutConstraint(
        @FormParam("param1") String param1,
        @QueryParam("param2") String param2,
        @SwBeanParam String param3
    ) {
        //NOP
    }

    @SuppressWarnings("unused")
    private void methodWithParameterWithIdConstraint(
        @SwId @FormParam("param1") String param1,
        @QueryParam("param2") String param2,
        @SwBeanParam String param3
    ) {
        //NOP
    }

    @SuppressWarnings("unused")
    private void methodWithParameterWithRegexConstraint(
        @FormParam("param1") String param1,
        @SwRegex("") @QueryParam("param2") String param2,
        @SwBeanParam String param3
    ) {
        //NOP
    }
}
