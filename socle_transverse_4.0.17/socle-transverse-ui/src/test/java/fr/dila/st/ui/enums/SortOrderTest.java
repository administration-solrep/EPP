package fr.dila.st.ui.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class SortOrderTest {

    @Test
    public void fromValue() {
        assertThat(SortOrder.fromValue("asc")).isEqualTo(SortOrder.ASC);
        assertThat(SortOrder.fromValue("desc")).isEqualTo(SortOrder.DESC);
    }

    @Test
    public void fromValueWithInvalidValue() {
        assertThat(SortOrder.fromValue("ASC")).isNull();
        assertThat(SortOrder.fromValue(null)).isNull();
    }

    @Test
    public void isAscending() {
        assertThat(SortOrder.isAscending(null)).isFalse();
        assertThat(SortOrder.isAscending(SortOrder.DESC)).isFalse();
        assertThat(SortOrder.isAscending(SortOrder.ASC)).isTrue();
    }
}
