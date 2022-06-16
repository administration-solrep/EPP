package fr.dila.st.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;
import org.junit.Test;

public class ObjectHelperTest {

    @Test
    public void testRequireNonNullElseGet() {
        // Given
        Object obj = new Object();

        // When
        Object objectGot = ObjectHelper.requireNonNullElseGet(obj, Object::new);

        // Then
        assertThat(objectGot).isEqualTo(obj);
    }

    @Test
    public void testRequireNonNullElseGetOnNullObject() {
        // Given
        Object obj = null;
        Object obj2 = new Object();

        @SuppressWarnings("unchecked")
        Supplier<Object> supplier = mock(Supplier.class);
        when(supplier.get()).thenReturn(obj2);

        // When
        Object objectGot = ObjectHelper.requireNonNullElseGet(obj, supplier);

        // Then
        assertThat(objectGot).isEqualTo(obj2);
    }

    @Test
    public void testAllNull() {
        assertThat(ObjectHelper.allNull(null, null, null)).isTrue();
    }

    @Test
    public void testNotAllNull() {
        assertThat(ObjectHelper.allNull(null, new Object(), null)).isFalse();
    }
}
