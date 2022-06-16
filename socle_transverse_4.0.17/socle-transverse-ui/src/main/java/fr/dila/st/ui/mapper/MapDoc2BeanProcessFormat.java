package fr.dila.st.ui.mapper;

import java.io.Serializable;
import java.util.function.Function;

public interface MapDoc2BeanProcessFormat extends MapDoc2BeanProcess {
    Function<Serializable, Serializable> formatter();
}
