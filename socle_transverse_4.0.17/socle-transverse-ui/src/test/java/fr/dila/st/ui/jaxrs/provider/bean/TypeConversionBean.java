package fr.dila.st.ui.jaxrs.provider.bean;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.validators.annot.SwRequired;
import javax.ws.rs.FormParam;

@SwBean
public class TypeConversionBean {
    @FormParam("booleanValue")
    private Boolean booleanValue;

    @FormParam("integerValue")
    private Integer integerValue;

    @FormParam("longValue")
    private Integer longValue;

    @SwRequired
    @FormParam("requiredValue")
    private Integer requiredValue;

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public Integer getLongValue() {
        return longValue;
    }

    public void setLongValue(Integer longValue) {
        this.longValue = longValue;
    }

    public Integer getRequiredValue() {
        return requiredValue;
    }

    public void setRequiredValue(Integer requiredValue) {
        this.requiredValue = requiredValue;
    }
}
