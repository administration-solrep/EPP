package fr.dila.st.api.organigramme;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Représentation d'un noeud unité structurelle de l'organigramme.
 * 
 * @author Fabio Esposito
 */
public interface UniteStructurelleNode extends OrganigrammeNode, WithSubPosteNode, WithSubUSNode {

	/**
	 * Retourne la liste des entités parentes.
	 * 
	 * @return Liste des entités parentes
	 * @throws ClientException
	 */
	List<EntiteNode> getEntiteParentList() throws ClientException;

	/**
	 * Renseigne la liste des entités parentes.
	 * 
	 * @param entiteParentList
	 *            Liste des entités parentes
	 */
	void setEntiteParentList(List<EntiteNode> entiteParentList);

	/**
	 * Retourne la liste des unités structurelles parentes.
	 * 
	 * @return Liste des unités structurelles parentes
	 * @throws ClientException
	 */
	List<UniteStructurelleNode> getUniteStructurelleParentList() throws ClientException;

	/**
	 * Renseigne la liste des unités structurelles parentes.
	 * 
	 * @param parentList
	 *            Liste des unités structurelles parentes
	 */
	void setUniteStructurelleParentList(List<UniteStructurelleNode> parentList);

	/**
	 * Indique si cette unité structurelle contient le poste de BDC.
	 * 
	 * @return vrai si un poste de bdc est contenu dans cette unité structurelle.
	 * @throws ClientException
	 */
	boolean containsBDC() throws ClientException;

	void setType(OrganigrammeType type);

	void setTypeValue(String type);

	String getTypeValue();

	String getParentId();

	String getParentEntiteId();

	void setParentEntiteId(String parentEntiteId);

	String getParentUniteId();

	void setParentUniteId(String parentUniteId);

	void setParentEntiteIds(List<String> list);

	List<String> getParentEntiteIds() throws ClientException;

	void setParentUnitIds(List<String> list);

	List<String> getParentUnitIds() throws ClientException;

	List<NorDirection> getNorDirectionList();

	void setNorDirectionList(List<NorDirection> norDirectionList);

	String getNorDirectionForMinistereId(String ministereId);

	String getNorDirection(String ministereId);

	void setNorDirectionForMinistereId(String ministereId, String nor);

	List<InstitutionNode> getInstitutionParentList() throws ClientException;

	void setInstitutionParentList(List<InstitutionNode> instututionParentList);

	List<String> getParentInstitIds();

	void setParentInstitIds(List<String> list);

}
