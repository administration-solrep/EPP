package fr.dila.st.core.util;

import java.util.List;
import java.util.regex.Pattern;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RegexUtilTest {
    private static final Pattern PATTERN = Pattern.compile("[abc]");

    /**
     * Test avec une limite volontairement large -> on doit retourner toutes les occurrences.
     */
    @Test
    public void test_listMatch() {
        List<String> actual = RegexUtils.listMatches(PATTERN, "abracadabra", 100);

        Assertions.assertThat(actual).containsExactly("a", "b", "a", "c", "a", "a", "b", "a");
    }

    /**
     * Test avec une limite basse-> on doit retourner seulement les premières occurrences.
     */
    @Test
    public void test_listMatch_limited() {
        List<String> actual = RegexUtils.listMatches(PATTERN, "abracadabra", 4);

        Assertions.assertThat(actual).containsExactly("a", "b", "a", "c");
    }
}
