package fr.dila.st.api.organigramme;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

public interface WithSubPosteNode extends OrganigrammeNode {

	/**
	 * Retourne la liste des sous-postes.
	 * 
	 * @return Liste des sous-postes
	 * @throws ClientException
	 */
	List<PosteNode> getSubPostesList() throws ClientException;

	/**
	 * Renseigne la liste des sous-postes.
	 * 
	 * @param subPostesList
	 *            Liste des sous-postes
	 */
	void setSubPostesList(List<PosteNode> subPostesList);
}
