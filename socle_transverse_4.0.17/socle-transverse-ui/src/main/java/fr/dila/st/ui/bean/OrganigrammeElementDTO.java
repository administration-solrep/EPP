package fr.dila.st.ui.bean;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.UserNode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;

public class OrganigrammeElementDTO extends TreeElementDTO {
    protected OrganigrammeNode organigrammeNode;
    protected UserNode currentUser;
    protected List<UserNode> lstUserNode;
    protected String ministereId;
    protected OrganigrammeElementDTO parent;
    protected String type;
    protected Boolean isActive = true;
    protected Date lockDate;
    protected String lockUserName;
    protected Boolean isPosteCE;
    /**
     * Actions disponibles portant sur ce noeud.
     */
    private List<Action> actions;

    private Boolean allowAdd = false;

    public OrganigrammeElementDTO(CoreSession session, OrganigrammeNode organigrammeNode) {
        this(session, organigrammeNode, null);
    }

    public OrganigrammeElementDTO(UserNode userNode) {
        setChilds(new ArrayList<>());
        setCurrentUser(userNode);
        lstUserNode = new ArrayList<>();
        lstUserNode.add(userNode);
        setIsLastLevel(true);
    }

    public OrganigrammeElementDTO(CoreSession session, OrganigrammeNode organigrammeNode, String ministereId) {
        //Nécessaire avant car pour les directions on doit connaitre le parent courant
        setMinistereId(ministereId);

        setChilds(new ArrayList<>());
        setOrganigrammeNode(session, organigrammeNode);
    }

    public OrganigrammeElementDTO(
        CoreSession session,
        OrganigrammeNode organigrammeNode,
        String ministereId,
        OrganigrammeElementDTO parent
    ) {
        this(session, organigrammeNode, ministereId);
        setParent(parent);
    }

    public OrganigrammeNode getOrganigrammeNode() {
        return organigrammeNode;
    }

    public void setOrganigrammeNode(CoreSession session, OrganigrammeNode organigrammeNode) {
        this.organigrammeNode = organigrammeNode;
        if (organigrammeNode != null) {
            setKey(organigrammeNode.getId());
            setCompleteKey(getKey());
            setLabel(organigrammeNode.getLabelWithNor(ministereId));
            setType(organigrammeNode.getType().getValue());
            setIsActive(organigrammeNode.isActive());
            setLockDate(organigrammeNode.getLockDate());
            setLockUserName(organigrammeNode.getLockUserName());
        } else {
            setKey("");
            setCompleteKey("");
            setLabel("");
            setType("");
            setIsActive(true);
            setLockDate(null);
            setLockUserName("");
        }
    }

    public UserNode getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserNode currentUser) {
        this.currentUser = currentUser;
        if (currentUser != null) {
            setKey(currentUser.getId());
            setLabel(currentUser.getLabel());
            setType(currentUser.getType().getValue());
            setIsActive(currentUser.isActive());
        } else {
            setKey("");
            setLabel("");
            setType("");
            setIsActive(true);
        }
    }

    public List<UserNode> getLstUserNode() {
        return lstUserNode;
    }

    public void setLstUserNode(List<UserNode> lstUserNode) {
        this.lstUserNode = lstUserNode;
    }

    public String getMinistereId() {
        return ministereId;
    }

    public void setMinistereId(String ministereId) {
        this.ministereId = ministereId;
    }

    public OrganigrammeElementDTO getParent() {
        return parent;
    }

    public void setParent(OrganigrammeElementDTO parent) {
        this.parent = parent;
        setCompleteKey(String.format("%s__%s", parent.getCompleteKey(), getKey()));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Date getLockDate() {
        return lockDate;
    }

    public void setLockDate(Date lockDate) {
        this.lockDate = lockDate;
    }

    public String getLockUserName() {
        return lockUserName;
    }

    public void setLockUserName(String lockUserName) {
        this.lockUserName = lockUserName;
    }

    public Boolean getAllowAdd() {
        return allowAdd;
    }

    public void setAllowAdd(Boolean allowAdd) {
        this.allowAdd = allowAdd;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Boolean getIsPosteCE() {
        return isPosteCE;
    }

    public void setIsPosteCE(Boolean isPosteCE) {
        this.isPosteCE = isPosteCE;
    }
}
