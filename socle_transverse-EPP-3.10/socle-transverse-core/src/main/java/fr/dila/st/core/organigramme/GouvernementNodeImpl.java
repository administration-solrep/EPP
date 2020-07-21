package fr.dila.st.core.organigramme;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Représentation d'un neoud de l'organigramme.
 * 
 * @author Fabio Esposito
 */
@Entity(name = "GouvernementNode")
@Table(name = "GOUVERNEMENT")
public class GouvernementNodeImpl extends OrganigrammeNodeImpl implements GouvernementNode {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8081139781962677564L;
	@Transient
	List<EntiteNode>			lstMinistere;

	public GouvernementNodeImpl() {
		super();
	}

	public GouvernementNodeImpl(GouvernementNode node) throws ClientException {
		super(node);
		this.lstMinistere = node.getSubEntitesList();

	}

	@Override
	public OrganigrammeType getType() {
		return OrganigrammeType.GOUVERNEMENT;
	}

	@Override
	public int getParentListSize() {
		return 0;
	}

	@Override
	public void setParentList(List<OrganigrammeNode> parentList) {
		// Les gouvernements n'ont pas de noeud parent
	}

	@Override
	public List<EntiteNode> getSubEntitesList() throws ClientException {
		if (lstMinistere == null) {
			lstMinistere = STServiceLocator.getSTMinisteresService().getEntiteNodeEnfant(getId());
		}
		return lstMinistere;
	}

	@Override
	public void setSubEntitesList(List<EntiteNode> subEntitesList) {
		lstMinistere = subEntitesList;
	}

}
