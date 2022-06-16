package fr.dila.st.core.feature;

import java.lang.reflect.Field;
import org.mockito.configuration.IMockitoConfiguration;
import org.mockito.internal.configuration.GlobalConfiguration;
import org.nuxeo.runtime.mockito.MockitoFeature;

public class SolonMockitoFeature extends MockitoFeature {

    @Override
    protected void cleanupThread()
        throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field f = GlobalConfiguration.class.getDeclaredField("GLOBAL_CONFIGURATION");
        f.setAccessible(true);
        ThreadLocal<IMockitoConfiguration> holder = (ThreadLocal<IMockitoConfiguration>) f.get(null);
        holder.remove();
    }
}
