package fr.dila.st.web.administration.organigramme;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.core.util.DateUtil;

/**
 * Classe reprensentant un noeud d'organigramme. Utilisée pour l'affichage du RichTree
 * 
 * @author Fabio Esposito
 * 
 */
public class OrganigrammeDataTreeNode {

	private static final int				PRIME_HASH	= 31;
	private OrganigrammeNode				organigrammeNode;
	private UserNode						currentUser;
	private List<UserNode>					lstUserNode;
	private String							ministereId;
	private List<OrganigrammeDataTreeNode>	children;
	private OrganigrammeDataTreeNode		parent;

	private Boolean							loaded		= false;
	private Boolean							opened		= false;

	public OrganigrammeDataTreeNode(OrganigrammeNode organigrammeNode) {
		children = new ArrayList<OrganigrammeDataTreeNode>();
		this.organigrammeNode = organigrammeNode;
	}

	public OrganigrammeDataTreeNode(UserNode organigrammeNode) {
		children = new ArrayList<OrganigrammeDataTreeNode>();
		currentUser = organigrammeNode;
		lstUserNode = new ArrayList<UserNode>();
		lstUserNode.add(organigrammeNode);
	}

	public OrganigrammeDataTreeNode(OrganigrammeNode organigrammeNode, String ministereId) {
		this(organigrammeNode);
		this.ministereId = ministereId;
	}

	public OrganigrammeDataTreeNode(OrganigrammeNode organigrammeNode, String ministereId,
			OrganigrammeDataTreeNode parent) {
		this(organigrammeNode, ministereId);
		this.parent = parent;
	}

	public Boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(Boolean loaded) {
		this.loaded = loaded;
	}

	public Boolean isOpened() {
		return opened;
	}

	public void setOpened(Boolean isOpened) {
		opened = isOpened;
	}

	/**
	 * @return
	 * @see fr.dila.st.api.organigramme.OrganigrammeNode#getLabel()
	 */
	public String getLabel() {
		// Pour gérer le cas des utilisateurs et des noeuds de l'organigramme
		if (organigrammeNode != null) {
			return organigrammeNode.getLabel();
		} else {
			return currentUser.getLabel();
		}
	}

	/**
	 * @return
	 * @see fr.dila.st.api.organigramme.OrganigrammeNode#getTypeValue()
	 */
	public String getTypeValue() {
		// Pour gérer le cas des utilisateurs et des noeuds de l'organigramme
		if (organigrammeNode != null) {
			return organigrammeNode.getType().getValue();
		} else {
			return currentUser.getType().getValue();
		}
	}

	/**
	 * @return
	 * @see fr.dila.st.api.organigramme.OrganigrammeNode#getId()
	 */
	public String getId() {
		// Pour gérer le cas des utilisateurs et des noeuds de l'organigramme
		if (organigrammeNode != null) {
			return organigrammeNode.getId();
		} else {
			return currentUser.getId();
		}
	}

	/**
	 * @return
	 * @see fr.dila.st.api.organigramme.OrganigrammeNode#isActive()
	 */
	public boolean isActive() {
		// Pour gérer le cas des utilisateurs et des noeuds de l'organigramme
		if (organigrammeNode != null) {
			return organigrammeNode.isActive();
		} else {
			return currentUser.isActive();
		}
	}

	/**
	 * @return the ministereId
	 */
	public String getMinistereId() {
		return ministereId;
	}

	/**
	 * @param ministereId
	 *            the ministereId to set
	 */
	public void setMinistereId(String ministereId) {
		this.ministereId = ministereId;
	}

	/**
	 * @return the organigrammeNode
	 */
	public OrganigrammeNode getNode() {
		return organigrammeNode;
	}

	/**
	 * @param organigrammeNode
	 *            the organigrammeNode to set
	 */
	public void setNode(OrganigrammeNode organigrammeNode) {
		this.organigrammeNode = organigrammeNode;
	}

	public List<OrganigrammeDataTreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<OrganigrammeDataTreeNode> children) {
		this.children = children;
	}

	public void addChild(OrganigrammeDataTreeNode node) {
		children.add(node);
	}

	/**
	 * @return the parent
	 */
	public OrganigrammeDataTreeNode getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParents(OrganigrammeDataTreeNode parent) {
		this.parent = parent;
	}

	public String getLockUserName() {
		// Pour gérer le cas des utilisateurs et des noeuds de l'organigramme
		if (organigrammeNode != null) {
			return organigrammeNode.getLockUserName();
		} else {
			return "";
		}
	}

	public String getLockDate() {
		if (organigrammeNode != null) {
			return convertDateForClient(organigrammeNode.getLockDate());
		} else {
			return "";
		}
	}

	public String getDateDebut() {
		if (organigrammeNode != null) {
			return convertDateForClient(organigrammeNode.getDateDebut());
		} else {
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		int result = 1;
		result = PRIME_HASH * result + (organigrammeNode == null ? 0 : organigrammeNode.getId().hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof OrganigrammeDataTreeNode)) {
			return false;
		}
		OrganigrammeDataTreeNode other = (OrganigrammeDataTreeNode) obj;
		if (organigrammeNode == null) {
			if (other.organigrammeNode != null) {
				return false;
			}
		} else if (!organigrammeNode.getId().equals(other.organigrammeNode.getId())) {
			return false;
		}
		return true;
	}

	public String convertDateForClient(Date date) {
		if (date == null) {
			return "";
		} else {
			return DateUtil.simpleDateFormat("dd MMMMM yyyy").format(date);
		}
	}

	public List<UserNode> getLstUserNode() {
		return lstUserNode;
	}

	public void setLstUserNode(List<UserNode> lstUserNode) {
		this.lstUserNode = lstUserNode;
	}

}
