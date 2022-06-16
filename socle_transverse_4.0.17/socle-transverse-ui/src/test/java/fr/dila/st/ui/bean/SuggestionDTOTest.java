package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class SuggestionDTOTest {

    @Test
    public void testConstructor() {
        SuggestionDTO suggestion = new SuggestionDTO();
        assertNotNull(suggestion);
        assertNull(suggestion.getKey());
        assertNull(suggestion.getLabel());

        suggestion = new SuggestionDTO("TestKey", "TestLabel");
        assertNotNull(suggestion);
        assertEquals("TestKey", suggestion.getKey());
        assertEquals("TestLabel", suggestion.getLabel());
    }

    @Test
    public void testSetter() {
        SuggestionDTO suggestion = new SuggestionDTO();

        suggestion.setKey("TestKey");
        assertEquals("TestKey", suggestion.getKey());

        suggestion.setLabel("TestLabel");
        assertEquals("TestLabel", suggestion.getLabel());
    }
}
