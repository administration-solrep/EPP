package fr.dila.st.api.organigramme;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

public interface WithSubUSNode extends OrganigrammeNode {

	/**
	 * Retourne la liste des sous-unités structurelles.
	 * 
	 * @return Liste des sous-unités structurelles
	 * @throws ClientException
	 */
	List<UniteStructurelleNode> getSubUnitesStructurellesList() throws ClientException;

	/**
	 * Renseigne la liste des sous-unités structurelles.
	 * 
	 * @param subUnitesStructurellesList
	 *            Liste des sous-unités structurelles
	 */
	void setSubUnitesStructurellesList(List<UniteStructurelleNode> subUnitesStructurellesList);

}
