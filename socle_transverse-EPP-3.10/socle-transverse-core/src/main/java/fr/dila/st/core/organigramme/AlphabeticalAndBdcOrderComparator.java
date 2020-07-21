package fr.dila.st.core.organigramme;

import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;

public class AlphabeticalAndBdcOrderComparator implements Comparator<OrganigrammeNode> {

	private static final Log	LOGGER	= LogFactory.getLog(AlphabeticalAndBdcOrderComparator.class);

	/**
	 * Default constructor
	 */
	public AlphabeticalAndBdcOrderComparator() {
		// do nothing
	}

	@Override
	/**
	 * classe permettant la comparaison entre deux noeuds d'un organigramme afin de savoir
	 * s'ils sont deux noeuds de même niveau lequel vient avant l'autre sachant 
	 * qu'on veut : ordre alphabétique, et le noeud comportant un bdc arrive en premier
	 * 
	 * @return 0 si égalité, -1 si node1 précède node2, 1 si node2 précède node1 
	 */
	public int compare(OrganigrammeNode node1, OrganigrammeNode node2) {
		if (node1 instanceof EntiteNode && node2 instanceof EntiteNode) {
			EntiteNode entityNode1 = (EntiteNode) node1;
			EntiteNode entityNode2 = (EntiteNode) node2;
			// Ordre protocolaire pour les entites
			if (entityNode1.getOrdre() == null && entityNode2.getOrdre() == null) {
				return 0;
			} else if (entityNode1.getOrdre() == null) {
				return -1;
			} else if (entityNode2.getOrdre() == null) {
				return 1;
			} else {
				return entityNode1.getOrdre().compareTo(entityNode2.getOrdre());
			}
		} else if (node1 instanceof PosteNode && node2 instanceof PosteNode) {
			PosteNode posteNode1 = (PosteNode) node1;
			PosteNode posteNode2 = (PosteNode) node2;

			Boolean poste1isBDC = posteNode1.isPosteBdc() && !posteNode1.getDeleted();
			Boolean poste2isBDC = posteNode2.isPosteBdc() && !posteNode2.getDeleted();

			if (poste1isBDC && poste2isBDC) {
				if (posteNode1.getLabel() == null && posteNode2.getLabel() == null) {
					return 0;
				} else if (posteNode1.getLabel() == null) {
					return 1;
				} else if (posteNode2.getLabel() == null) {
					return -1;
				} else {
					return posteNode1.getLabel().compareTo(posteNode2.getLabel());
				}
			} else if (poste1isBDC) {
				return -1;
			} else if (poste2isBDC) {
				return 1;
			} else {
				if (posteNode1.getLabel() == null && posteNode2.getLabel() == null) {
					return 0;
				} else if (posteNode1.getLabel() == null) {
					return 1;
				} else if (posteNode2.getLabel() == null) {
					return -1;
				} else {
					return posteNode1.getLabel().compareTo(posteNode2.getLabel());
				}
			}
		} else if (node1 instanceof UniteStructurelleNode && node2 instanceof UniteStructurelleNode) {
			UniteStructurelleNode uniteStructNode1 = (UniteStructurelleNode) node1;
			UniteStructurelleNode uniteStructNode2 = (UniteStructurelleNode) node2;

			Boolean usNode1ContainsBDC = false;
			try {
				usNode1ContainsBDC = uniteStructNode1.containsBDC();
			} catch (ClientException e) {
				LOGGER.warn("Erreur UniteStructurelle u1 - méthode containsBDC / " + e.getMessage());
			}
			Boolean usNode2ContainsBDC = false;
			try {
				usNode2ContainsBDC = uniteStructNode2.containsBDC();
			} catch (ClientException e) {
				LOGGER.warn("Erreur UniteStructurelle u2 - méthode containsBDC / " + e.getMessage());
			}

			if (usNode1ContainsBDC && usNode2ContainsBDC) {
				if (uniteStructNode1.getLabel() == null && uniteStructNode2.getLabel() == null) {
					return 0;
				} else if (uniteStructNode1.getLabel() == null) {
					return 1;
				} else if (uniteStructNode2.getLabel() == null) {
					return -1;
				} else {
					return uniteStructNode1.getLabel().compareTo(uniteStructNode2.getLabel());
				}
			} else if (usNode1ContainsBDC) {
				return -1;
			} else if (usNode2ContainsBDC) {
				return 1;
			} else {
				if (uniteStructNode1.getLabel() == null && uniteStructNode2.getLabel() == null) {
					return 0;
				} else if (uniteStructNode1.getLabel() == null) {
					return 1;
				} else if (uniteStructNode2.getLabel() == null) {
					return -1;
				} else {
					return uniteStructNode1.getLabel().compareTo(uniteStructNode2.getLabel());
				}
			}
		} else if (node1 instanceof EntiteNode && node2 instanceof PosteNode) {
			return -1;
		} else if (node1 instanceof EntiteNode && node2 instanceof UniteStructurelleNode) {
			return -1;
		} else if (node1 instanceof UniteStructurelleNode && node2 instanceof EntiteNode) {
			return 1;
		} else if (node1 instanceof UniteStructurelleNode && node2 instanceof PosteNode) {
			return -1;
		} else if (node1 instanceof PosteNode && node2 instanceof UniteStructurelleNode) {
			return 1;
		} else if (node1 instanceof PosteNode && node2 instanceof EntiteNode) {
			return 1;
		} else {
			return 0;
		}
	}
}
