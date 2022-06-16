package fr.dila.st.api.constant;

import static fr.dila.st.api.constant.MediaType.APPLICATION_ZIP;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class MediaTypeTest {

    @Test
    public void isEqualToMimeType() {
        assertThat(APPLICATION_ZIP.isEqualToMimeType("application/zip")).isTrue();
        assertThat(APPLICATION_ZIP.isEqualToMimeType("bad_mime_type")).isFalse();
    }
}
