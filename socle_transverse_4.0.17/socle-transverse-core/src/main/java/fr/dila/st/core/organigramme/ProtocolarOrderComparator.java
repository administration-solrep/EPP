package fr.dila.st.core.organigramme;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparateur sur les ordres protocolaires.
 *
 */
public class ProtocolarOrderComparator implements Comparator<OrganigrammeNode>, Serializable {
    /**
     * serial verions UID
     */
    private static final long serialVersionUID = -2146156524031231978L;

    /**
     * default constructor
     */
    public ProtocolarOrderComparator() {
        // do nothing
    }

    @Override
    public int compare(OrganigrammeNode node1, OrganigrammeNode node2) {
        int result = 0;
        if (node1 instanceof EntiteNode && node2 instanceof EntiteNode) {
            EntiteNode entite1 = (EntiteNode) node1;
            EntiteNode entite2 = (EntiteNode) node2;
            if (entite1.getOrdre() == null && entite2.getOrdre() == null) {
                result = 0;
            } else if (entite1.getOrdre() == null) {
                result = -1;
            } else if (entite2.getOrdre() == null) {
                result = 1;
            } else {
                result = entite1.getOrdre().compareTo(entite2.getOrdre());
            }
        } else if (node1 instanceof EntiteNode && node2 instanceof UniteStructurelleNode) {
            result = -1;
        } else if (node1 instanceof UniteStructurelleNode && node2 instanceof EntiteNode) {
            result = 1;
        }
        return result;
    }
}
