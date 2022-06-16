package fr.dila.ss.ui.bean.fdr;

import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.platform.actions.Action;

public class ContainerDTO {
    private ContainerDTO parent;
    private boolean isParallel = false;
    private Integer level;
    private Integer position;
    private Integer nbChilds = 0;
    private List<Action> actions = new ArrayList<>();
    private Integer depth;
    private String id;

    public ContainerDTO() {
        super();
    }

    public ContainerDTO getParent() {
        return parent;
    }

    public void setParent(ContainerDTO parent) {
        this.parent = parent;
    }

    public boolean getIsParallel() {
        return isParallel;
    }

    public void setIsParallel(boolean isParallel) {
        this.isParallel = isParallel;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getNbChilds() {
        return nbChilds;
    }

    public void setNbChilds(Integer nbChilds) {
        this.nbChilds = nbChilds;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
