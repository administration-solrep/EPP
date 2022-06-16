package fr.sword.idl.naiad.nuxeo.feuilleroute.api.model;

import java.util.ArrayList;
import java.util.List;

public class StepGroup extends Elem {
    private String type;

    private List<Elem> children = new ArrayList<>();

    public StepGroup() {
        super();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Elem> getChildren() {
        return children;
    }

    public void setChildren(List<Elem> children) {
        this.children = children;
    }
}
