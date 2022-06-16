package fr.dila.st.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class FullTextUtilTest {

    @Test
    public void testPrepareSearch() {
        assertThat(FullTextUtil.prepareSearch(null)).isEmpty();
        assertThat(FullTextUtil.prepareSearch("")).isEmpty();
        assertThat(FullTextUtil.prepareSearch("*")).isEmpty();
        assertThat(FullTextUtil.prepareSearch("* * **")).isEmpty();
        assertThat(FullTextUtil.prepareSearch("*gg ")).isEqualTo("gg");
        assertThat(FullTextUtil.prepareSearch("-gg ")).isEqualTo("gg");
        assertThat(FullTextUtil.prepareSearch("test test1 test2")).isEqualTo("test test1 test2");
        assertThat(FullTextUtil.prepareSearch("test* - -test1* *test2-")).isEqualTo("test test1 test2");
    }

    @Test
    public void testPreparePrefixSearch() {
        assertThat(FullTextUtil.preparePrefixSearch(null)).isEmpty();
        assertThat(FullTextUtil.preparePrefixSearch("")).isEmpty();
        assertThat(FullTextUtil.preparePrefixSearch("*")).isEmpty();
        assertThat(FullTextUtil.preparePrefixSearch("* * **")).isEmpty();
        assertThat(FullTextUtil.preparePrefixSearch("*gg ")).isEqualTo("gg*");
        assertThat(FullTextUtil.preparePrefixSearch("test test1 test2")).isEqualTo("test* test1* test2*");
    }
}
