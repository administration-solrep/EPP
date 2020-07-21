package fr.dila.st.api.organigramme;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.OrganigrammeNode;

/**
 * Représentation d'un noeud institution de l'organigramme.
 * 
 * @author jtremeaux
 */
public interface InstitutionNode extends OrganigrammeNode {

	List<UniteStructurelleNode> getSubUnitesStructurellesList() throws ClientException;

	void setSubUnitesStructurellesList(List<UniteStructurelleNode> subUnitesStructurellesList);

	List<PosteNode> getSubPostesList() throws ClientException;

	void setSubPostesList(List<PosteNode> subPostesList);
}
