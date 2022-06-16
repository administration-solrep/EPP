package fr.dila.st.ui.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author user
 *
 */
public class TreeElementDTO {
    private List<? extends TreeElementDTO> childs = new ArrayList<>();
    private String label;
    private String key;
    private String completeKey;
    private String action;
    private String link;
    private Boolean isOpen = false;
    private Boolean isLastLevel = false;
    private Boolean isBold = true;

    public TreeElementDTO() {}

    public TreeElementDTO(String label, String key, String completeKey, String link, Boolean isLastLevel) {
        this.label = label;
        this.key = key;
        this.completeKey = completeKey;
        this.link = link;
        this.isLastLevel = isLastLevel;
    }

    public List<? extends TreeElementDTO> getChilds() {
        return childs;
    }

    public void setChilds(List<? extends TreeElementDTO> childs) {
        this.childs = childs;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCompleteKey() {
        return completeKey;
    }

    public void setCompleteKey(String completeKey) {
        this.completeKey = completeKey;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Boolean getIsLastLevel() {
        return isLastLevel;
    }

    public void setIsLastLevel(Boolean isLastLevel) {
        this.isLastLevel = isLastLevel;
    }

    public Boolean getIsBold() {
        return isBold;
    }

    public void setIsBold(Boolean isBold) {
        this.isBold = isBold;
    }
}
