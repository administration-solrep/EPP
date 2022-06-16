package fr.dila.st.core.event.work;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class StepDTO implements Serializable {
    private static final long serialVersionUID = -472796991869666694L;

    private String posteId;
    private LinkedList<String> stepIds;

    public StepDTO(String posteId, List<String> stepIds) {
        super();
        this.posteId = posteId;
        this.stepIds = new LinkedList<>(stepIds);
    }

    public String getPosteId() {
        return posteId;
    }

    public void setPosteId(String posteId) {
        this.posteId = posteId;
    }

    public LinkedList<String> getStepIds() {
        return stepIds;
    }

    public void setStepIds(LinkedList<String> stepIds) {
        this.stepIds = stepIds;
    }
}
