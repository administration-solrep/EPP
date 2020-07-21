package fr.dila.st.api.organigramme;

/**
 * Représentation d'un noeud gouvernement de l'organigramme.
 * 
 * @author Fabio Esposito
 */
public interface GouvernementNode extends OrganigrammeNode, WithSubEntitiesNode {

	/**
	 * Le gouvernement est le prochain gouvernement, date début > date du jour
	 * 
	 * @return true si date début > date du jour
	 */
	boolean isNext();

}
