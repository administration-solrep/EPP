package fr.dila.st.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public abstract class AbstractTestSortableEnum<E extends Enum<E>> {

    protected abstract Class<E> getEnumClass();

    @Test
    public void enumConstantsShouldBeSorted() {
        List<String> names = Stream.of(getEnumClass().getEnumConstants()).map(Enum::name).collect(Collectors.toList());

        assertThat(names).isSorted();
    }
}
