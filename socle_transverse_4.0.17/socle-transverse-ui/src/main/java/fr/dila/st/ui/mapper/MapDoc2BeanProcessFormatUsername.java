package fr.dila.st.ui.mapper;

import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.function.Function;

public class MapDoc2BeanProcessFormatUsername implements MapDoc2BeanProcessFormat {

    public MapDoc2BeanProcessFormatUsername() {
        // Nothing to do
    }

    @Override
    public Function<Serializable, Serializable> formatter() {
        return username -> STServiceLocator.getSTUserService().getUserFullName((String) username);
    }
}
