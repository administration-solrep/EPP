package fr.dla.st.rest.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AbstractJetonWSTest {
    private static final Logger LOG = mock(Logger.class);

    @Test
    public void shouldGetJeton() {
        assertThat(getWS().getJeton("5")).isEqualTo("5");
        verify(LOG, never()).error(Mockito.anyString(), Mockito.any(NumberFormatException.class));
    }

    @Test
    public void shouldGetNullIfInvalidJeton() {
        assertThat(getWS().getJeton("f5")).isNull();
        verify(LOG).error(Mockito.anyString(), Mockito.any(NumberFormatException.class));
    }

    private AbstractJetonWS getWS() {
        AbstractJetonWS ws = new AbstractJetonWS() {

            @Override
            protected Logger getLogger() {
                return LOG;
            }
        };
        return ws;
    }
}
