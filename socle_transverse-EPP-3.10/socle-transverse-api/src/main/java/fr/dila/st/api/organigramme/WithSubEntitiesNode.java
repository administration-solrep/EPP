package fr.dila.st.api.organigramme;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

public interface WithSubEntitiesNode extends OrganigrammeNode {

	/**
	 * Retourne la liste des sous-entities.
	 * 
	 * @return Liste des sous-postes
	 * @throws ClientException
	 */
	List<EntiteNode> getSubEntitesList() throws ClientException;

	/**
	 * Renseigne la liste des sous-entities.
	 * 
	 * @param subPostesList
	 *            Liste des sous-postes
	 */
	void setSubEntitesList(List<EntiteNode> subEntiteList);

}
