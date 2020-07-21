package fr.dila.st.core.organigramme;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Représentation d'un noeud institution de l'organigramme.
 * 
 */
@Entity(name = "InstitutionNode")
@Table(name = "INSTITUTION")
public class InstitutionNodeImpl extends OrganigrammeNodeImpl implements InstitutionNode {

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -8852006330859582986L;

	@Transient
	private List<UniteStructurelleNode>	lstUniteEnfant;

	@Transient
	private List<PosteNode>				lstPosteEnfant;

	public InstitutionNodeImpl() {

	}

	/**
	 * Constructeur de EntiteNodeImpl.
	 * 
	 * @param node
	 *            Modèle de document
	 */
	public InstitutionNodeImpl(InstitutionNode node) {
		super(node);
	}

	@Override
	public void setParentList(List<OrganigrammeNode> parentList) {
		// NOP
	}

	@Override
	public int getParentListSize() {
		return 0;
	}

	@Override
	public OrganigrammeType getType() {
		return OrganigrammeType.INSTITUTION;
	}

	@Override
	public List<UniteStructurelleNode> getSubUnitesStructurellesList() throws ClientException {
		if (lstUniteEnfant == null) {
			lstUniteEnfant = STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleEnfant(this.getId(),
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
			lstPosteEnfant = STServiceLocator.getSTPostesService().getPosteNodeEnfant(this.getId(), getType());
		}
		return lstPosteEnfant;
	}

	@Override
	public void setSubPostesList(List<PosteNode> subPostesList) {
		lstPosteEnfant = subPostesList;
	}

	@Override
	public boolean isActive() {
		return this.getDateFin() == null || this.getDateFin().compareTo(new Date()) > 0;
	}
}
