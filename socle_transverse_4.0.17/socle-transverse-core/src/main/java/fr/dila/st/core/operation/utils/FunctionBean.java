package fr.dila.st.core.operation.utils;

import java.util.ArrayList;
import java.util.List;

public class FunctionBean {

    public FunctionBean(String groupname, String description, List<String> parentGroups) {
        this.groupname = groupname;
        this.description = description;
        this.parentGroups = parentGroups;
    }

    private String groupname;
    private String description;
    private List<String> parentGroups = new ArrayList<>();

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getParentGroups() {
        return parentGroups;
    }

    public void setParentGroups(List<String> parentGroups) {
        this.parentGroups = parentGroups;
    }
}
