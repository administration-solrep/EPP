package fr.dila.st.api.descriptor.parlement;

import java.io.Serializable;

public interface DefaultValue extends Serializable {
    void setType(String type);

    String getType();

    void setSource(String source);

    String getSource();

    void setValue(String value);

    String getValue();

    void setDestination(String destination);

    String getDestination();

    String getConditionnelValue();

    void setConditionnelValue(String conditionnelValue);
}
