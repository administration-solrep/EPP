package fr.dila.st.core.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class STAuthorizationExceptionTest {

    @Test
    public void testConstructor() {
        String resourceName = "export des utilisateurs";
        STAuthorizationException exception = new STAuthorizationException(resourceName);
        assertThat(exception.getMessage()).isEqualTo("Accès à la ressource non autorisé : " + resourceName);
    }
}
