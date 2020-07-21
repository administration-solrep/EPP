package fr.dila.st.core.organigramme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.NorDirection;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.api.service.organigramme.STUsAndDirectionService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.StringUtil;

/**
 * Représentation d'un noeud unité structurelle de l'organigramme.
 * 
 * @author Fabio Esposito
 */
@Entity(name = "UniteStructurelleNode")
@Table(name = "UNITE_STRUCTURELLE")
public class UniteStructurelleNodeImpl extends OrganigrammeNodeImpl implements UniteStructurelleNode {

	/**
	 * 
	 */
	private static final long				serialVersionUID	= -8153545559801388966L;

	@Column(name = "ID_PARENT_ENTITE")
	private String							parentEntiteId;

	@Column(name = "ID_PARENT_UNITE")
	private String							parentUniteId;

	@Column(name = "ID_PARENT_INSTITUTION")
	private String							parentInstitId;

	@Column(name = "TYPE")
	private String							type;

	@Column(name = "NOR_DIRECTION")
	protected String						norDirectionMember;

	@Transient
	private List<UniteStructurelleNode>		lstUniteEnfant;

	@Transient
	private List<PosteNode>					lstPosteEnfant;

	@Transient
	protected List<UniteStructurelleNode>	lstUniteParent		= new ArrayList<UniteStructurelleNode>();

	@Transient
	protected List<EntiteNode>				lstEntiteParent		= new ArrayList<EntiteNode>();

	@Transient
	private List<InstitutionNode>			lstInstitParent		= new ArrayList<InstitutionNode>();

	@Transient
	protected List<String>					parentUnitIds		= new ArrayList<String>();

	@Transient
	protected List<String>					parentEntiteIds		= new ArrayList<String>();

	@Transient
	private List<String>					parentInstitIds		= new ArrayList<String>();

	public UniteStructurelleNodeImpl() {
	}

	public UniteStructurelleNodeImpl(UniteStructurelleNode node) throws ClientException {
		super(node);
		this.type = node.getType().getValue();
		this.parentEntiteId = node.getParentEntiteId();
		this.parentUniteId = node.getParentUniteId();
		this.lstPosteEnfant = node.getSubPostesList();
		this.lstUniteEnfant = node.getSubUnitesStructurellesList();
		setNorDirectionList(node.getNorDirectionList());
		getParentEntiteIds();
		getParentUnitIds();
	}

	@Override
	public OrganigrammeType getType() {
		if (STSchemaConstant.ORGANIGRAMME_TYPE_MINISTERE.equals(type)) {
			return OrganigrammeType.MINISTERE;
		} else if (STSchemaConstant.ORGANIGRAMME_TYPE_DIRECTION.equals(type)) {
			return OrganigrammeType.DIRECTION;
		} else {
			return OrganigrammeType.UNITE_STRUCTURELLE;
		}
	}

	@Override
	public void setType(OrganigrammeType type) {
		if (type != null) {
			switch (type) {
				case DIRECTION:
					this.type = OrganigrammeType.DIRECTION.getValue();
					break;
				case UNITE_STRUCTURELLE:
					this.type = OrganigrammeType.UNITE_STRUCTURELLE.getValue();
					break;

				default:
					this.type = "";
					break;
			}
		} else {
			this.type = "";
		}
	}

	@Override
	public List<UniteStructurelleNode> getSubUnitesStructurellesList() throws ClientException {
		if (lstUniteEnfant == null) {
			lstUniteEnfant = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleEnfant(getId(),
					getType());
		}
		return lstUniteEnfant;
	}

	@Override
	public void setSubUnitesStructurellesList(List<UniteStructurelleNode> subUnitesStructurellesList) {
		lstUniteEnfant = subUnitesStructurellesList;
	}

	@Override
	public List<PosteNode> getSubPostesList() throws ClientException {
		if (lstPosteEnfant == null) {
			lstPosteEnfant = STServiceLocator.getSTPostesService().getPosteNodeEnfant(getId(), getType());
		}
		return lstPosteEnfant;
	}

	@Override
	public void setSubPostesList(List<PosteNode> subPostesList) {
		lstPosteEnfant = subPostesList;
	}

	@Override
	public List<UniteStructurelleNode> getUniteStructurelleParentList() throws ClientException {
		lstUniteParent.clear();
		final STUsAndDirectionService dirService = STServiceLocator.getSTUsAndDirectionService();
		for (String idParent : getParentUnitIds()) {
			lstUniteParent.add(dirService.getUniteStructurelleNode(idParent));
		}
		return lstUniteParent;
	}

	@Override
	public void setUniteStructurelleParentList(List<UniteStructurelleNode> parentList) {
		this.lstUniteParent = parentList;
	}

	@Override
	public List<EntiteNode> getEntiteParentList() throws ClientException {
		lstEntiteParent.clear();
		final STMinisteresService minService = STServiceLocator.getSTMinisteresService();
		for (String idParent : getParentEntiteIds()) {
			lstEntiteParent.add(minService.getEntiteNode(idParent));
		}
		return lstEntiteParent;
	}

	@Override
	public void setEntiteParentList(List<EntiteNode> parentList) {
		this.lstEntiteParent = parentList;
	}

	@Override
	public int getParentListSize() throws ClientException {
		int size = 0;
		List<UniteStructurelleNode> usParent = getUniteStructurelleParentList();
		if (usParent != null) {
			size += usParent.size();
		}
		List<EntiteNode> entiteParent = getEntiteParentList();
		if (entiteParent != null) {
			size += entiteParent.size();
		}
		return size;
	}

	@Override
	public void setParentList(List<OrganigrammeNode> parentList) {
		parentEntiteId = "";
		parentUniteId = "";
		parentInstitId = "";

		for (OrganigrammeNode parentNode : parentList) {
			if (parentNode instanceof UniteStructurelleNode) {
				lstUniteParent.add((UniteStructurelleNode) parentNode);
				if (StringUtil.isBlank(parentUniteId)) {
					parentUniteId = parentNode.getId();
				} else {
					parentUniteId = parentUniteId + ";" + parentNode.getId();
				}
			} else if (parentNode instanceof EntiteNode) {
				lstEntiteParent.add((EntiteNode) parentNode);
				if (StringUtil.isBlank(parentEntiteId)) {
					parentEntiteId = parentNode.getId();
				} else {
					parentEntiteId = parentEntiteId + ";" + parentNode.getId();
				}
			} else if (parentNode instanceof InstitutionNode) {
				lstInstitParent.add((InstitutionNode) parentNode);
				if (StringUtil.isBlank(parentInstitId)) {
					parentInstitId = parentNode.getId();
				} else {
					parentInstitId = parentInstitId + ";" + parentNode.getId();
				}
			}
		}
	}

	@Override
	public boolean containsBDC() throws ClientException {
		if (lstPosteEnfant == null) {
			getSubPostesList();
		}
		for (PosteNode poste : lstPosteEnfant) {
			if (poste.isPosteBdc() && !poste.getDeleted()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setTypeValue(String type) {
		this.type = type;
	}

	@Override
	public String getTypeValue() {
		return type;
	}

	@Override
	public String getParentId() {
		if (parentEntiteId != null) {
			return parentEntiteId;
		} else {
			return parentUniteId;
		}
	}

	@Override
	public List<String> getParentUnitIds() throws ClientException {
		if (StringUtil.isNotBlank(parentUniteId)) {
			if (parentUnitIds == null || parentUnitIds.isEmpty()) {
				String[] arrayParent = parentUniteId.split(";");
				parentUnitIds = new ArrayList<String>(Arrays.asList(arrayParent));
			}
		}
		return parentUnitIds;
	}

	@Override
	public void setParentUnitIds(List<String> list) {
		this.parentUnitIds = list;
		if (list != null && !list.isEmpty()) {
			parentUniteId = StringUtil.join(list, ";", "");
		} else {
			parentUniteId = "";
		}
	}

	@Override
	public List<String> getParentEntiteIds() throws ClientException {
		if (StringUtil.isNotBlank(parentEntiteId)) {
			if (parentEntiteIds == null || parentEntiteIds.isEmpty()) {
				String[] arrayParent = parentEntiteId.split(";");
				parentEntiteIds = new ArrayList<String>(Arrays.asList(arrayParent));
			}
		}
		return parentEntiteIds;
	}

	@Override
	public void setParentEntiteIds(List<String> parentEntiteIds) {
		this.parentEntiteIds = parentEntiteIds;
		if (parentEntiteIds != null && !parentEntiteIds.isEmpty()) {
			parentEntiteId = StringUtil.join(parentEntiteIds, ";", "");
		} else {
			parentEntiteId = "";
		}
	}

	@Override
	public List<NorDirection> getNorDirectionList() {
		List<NorDirection> norRefList = new ArrayList<NorDirection>();
		if (norDirectionMember != null) {
			List<String> norRefStringList = Arrays.asList(norDirectionMember.split(";"));
			if (norRefStringList != null && !norRefStringList.isEmpty()) {
				for (String norRefString : norRefStringList) {
					if (StringUtil.isNotBlank(norRefString)) {
						norRefList.add(new NorDirection(norRefString));
					}
				}
			}
		}
		return norRefList;
	}

	@Override
	public void setNorDirectionList(List<NorDirection> norDirectionList) {
		StringBuilder builder = new StringBuilder();
		for (NorDirection norRef : norDirectionList) {
			builder.append(norRef.getNorJSON());
			builder.append(";");
		}
		norDirectionMember = builder.toString();
	}

	@Override
	public String getNorDirectionForMinistereId(String ministereId) {
		List<NorDirection> norRefList = getNorDirectionList();
		if (StringUtil.isNotEmpty(ministereId)) {
			for (NorDirection norRef : norRefList) {
				if (ministereId.equals(norRef.getId())) {
					return norRef.getNor();
				}
			}
		}
		return "";
	}

	@Override
	public String getNorDirection(String ministereId) {
		List<NorDirection> norRefList = getNorDirectionList();
		if (StringUtil.isNotEmpty(ministereId)) {
			for (NorDirection norRef : norRefList) {
				if (ministereId.equals(norRef.getId())) {
					return norRef.getNor();
				}
			}
			if (norRefList != null && !norRefList.isEmpty()) {
				return norRefList.get(0).getNor();
			}
		}
		return "";
	}

	@Override
	public void setNorDirectionForMinistereId(String ministereId, String nor) {
		// On met à jour la liste des nor direction existante
		List<NorDirection> norRefList = getNorDirectionList();
		// On regarde d'abord si l'entrée pour le ministère id existe déjà
		// pour pouvoir mettre à jour la lettre
		for (NorDirection norRef : norRefList) {
			if (ministereId.equals(norRef.getId())) {
				norRef.setNor(nor);
				setNorDirectionList(norRefList);
				return;
			}
		}

		// Sinon on ajoute la nouvelle entrée à la liste
		norRefList.add(new NorDirection(ministereId, nor));
		setNorDirectionList(norRefList);
	}

	@Override
	public List<InstitutionNode> getInstitutionParentList() throws ClientException {
		if (lstInstitParent == null || lstInstitParent.isEmpty()) {
			final OrganigrammeService orgaService = STServiceLocator.getOrganigrammeService();
			for (String idParent : getParentInstitIds()) {
				lstInstitParent.add((InstitutionNode) orgaService.getOrganigrammeNodeById(idParent,
						OrganigrammeType.INSTITUTION));
			}
		}
		return lstInstitParent;
	}

	@Override
	public void setInstitutionParentList(List<InstitutionNode> instututionParentList) {
		lstInstitParent = instututionParentList;
	}

	@Override
	public List<String> getParentInstitIds() {
		if (StringUtil.isNotBlank(parentInstitId)) {
			if (parentInstitIds == null || parentInstitIds.isEmpty()) {
				String[] arrayParent = parentInstitId.split(";");
				parentInstitIds = new ArrayList<String>(Arrays.asList(arrayParent));
			}
		}
		return parentInstitIds;
	}

	@Override
	public void setParentInstitIds(List<String> list) {
		this.parentInstitIds = list;
		if (list != null && !list.isEmpty()) {
			parentInstitId = StringUtil.join(list, ";", "");
		} else {
			parentInstitId = "";
		}
	}

	@Override
	public String getParentEntiteId() {
		return parentEntiteId;
	}

	@Override
	public void setParentEntiteId(String parentEntiteId) {
		this.parentEntiteId = parentEntiteId;
	}

	@Override
	public String getParentUniteId() {
		return parentUniteId;
	}

	@Override
	public void setParentUniteId(String parentUniteId) {
		this.parentUniteId = parentUniteId;
	}
}
