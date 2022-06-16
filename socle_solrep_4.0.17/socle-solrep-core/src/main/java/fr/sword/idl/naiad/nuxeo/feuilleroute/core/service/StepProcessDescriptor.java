package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.StepProcessInit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 *
 */
@XObject("stepProcess")
public class StepProcessDescriptor {
    @XNode("@documentType")
    private String documentType;

    @XNode("@clazz")
    private Class<? extends StepProcessInit> stepProcessClazz;

    @XNodeList(value = "parameter", type = ArrayList.class, componentType = ParamDescriptor.class)
    private List<ParamDescriptor> parameters;

    public StepProcessDescriptor() {
        // do nothing
    }

    public String getDocumentType() {
        return documentType;
    }

    public Class<? extends StepProcessInit> getStepProcessClazz() {
        return stepProcessClazz;
    }

    public void setStepProcessClazz(Class<? extends StepProcessInit> stepProcessClazz) {
        this.stepProcessClazz = stepProcessClazz;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public List<ParamDescriptor> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParamDescriptor> parameters) {
        this.parameters = parameters;
    }

    public Map<String, String> getParametersAsMap() {
        Map<String, String> params = new HashMap<>();
        if (parameters != null) {
            for (ParamDescriptor pdesc : parameters) {
                params.put(pdesc.getName(), pdesc.getValue());
            }
        }
        return params;
    }
}
