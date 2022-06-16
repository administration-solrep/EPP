package fr.dila.st.core.groupcomputer;

import fr.dila.st.core.user.STPrincipalImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.platform.computedgroups.AbstractGroupComputer;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;

/**
 * Ce calculateur de groupe injecte dans le principal les groupes correspondant aux fonctions unitaires (associées aux
 * profils).
 *
 * @author jtremeaux
 */
public class BaseFunctionGroupComputer extends AbstractGroupComputer {
    private static final Log LOG = LogFactory.getLog(BaseFunctionGroupComputer.class);

    /**
     * Default constructor
     */
    public BaseFunctionGroupComputer() {
        super();
    }

    @Override
    public List<String> getGroupsForUser(NuxeoPrincipalImpl nuxeoPrincipal) {
        if (nuxeoPrincipal == null) {
            return Collections.emptyList();
        }

        // Récupère les groupes définis dans l'annuaire (= profils) de l'utilisateur
        List<String> groupList = nuxeoPrincipal.getGroups();

        try {
            List<String> newGroupList = new ArrayList<String>(groupList);
            if (!(nuxeoPrincipal instanceof STPrincipalImpl)) {
                throw new Exception("Le principal doit être du type STPrincipalImpl");
            }

            // Récupère les fonctions unitaires de l'utilisateur à partir du principal
            STPrincipalImpl stPrincipal = (STPrincipalImpl) nuxeoPrincipal;
            Set<String> baseFunctionSet = stPrincipal.getBaseFunctionSet();

            // Injecte les fonctions unitaires dans les groupes virtuels de l'utilisateur
            newGroupList.addAll(baseFunctionSet);

            return newGroupList;
        } catch (Exception e) {
            LOG.error("Impossible d'associer les groupes grille de fonction à l'utilisateur connecté.", e);
            return groupList;
        }
    }

    /**
     * Returns an empty list for efficiency
     */
    public List<String> getAllGroupIds() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getGroupMembers(String groupName) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getParentsGroupNames(String groupName) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getSubGroupsNames(String groupName) {
        return Collections.emptyList();
    }

    /**
     * Retourne faux: aucune fonction unitaire ne doit être vue comme un groupe.
     */
    @Override
    public boolean hasGroup(String name) {
        return false;
    }
}
