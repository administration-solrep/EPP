package fr.dila.solonepp.core.groupcomputer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.platform.computedgroups.AbstractGroupComputer;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;

import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.user.STPrincipalImpl;

/**
 * Ce calculateur de groupe injecte dans le principal les groupes calculés pour SOLON EPP.
 *
 * @author jtremeaux
 */
public class SolonEppGroupComputer extends AbstractGroupComputer {

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(SolonEppGroupComputer.class);    

    @Override
    public List<String> getGroupsForUser(NuxeoPrincipalImpl nuxeoPrincipal) throws Exception {
        if (nuxeoPrincipal == null) {
            return Collections.emptyList();
        }
        
        // Récupère les groupes actuels du principal
        final List<String> groupList = nuxeoPrincipal.getGroups();
        try {
            List<String> newGroupList = new ArrayList<String>(groupList);
            if (!(nuxeoPrincipal instanceof STPrincipalImpl)) {
                throw new Exception("Le principal doit être du type STPrincipalImpl");
            }
//            STPrincipalImpl stPrincipal = (STPrincipalImpl) nuxeoPrincipal;
            
            // Injecte les groupes donnant accès aux dossiers
//            newGroupList.addAll(getDossierMesureNominativeGroupSet(stPrincipal));
//            newGroupList.addAll(getDossierAdminMinUpdaterGroupSet(stPrincipal));
//            newGroupList.addAll(getDossierDistributionMinistereReaderGroupSet(stPrincipal));
//            newGroupList.addAll(getDossierDistributionDirectionReaderGroupSet(stPrincipal));
//            newGroupList.addAll(getDossierRattachementMinistereReaderGroupSet(stPrincipal));
//            newGroupList.addAll(getDossierRattachementDirectionReaderGroupSet(stPrincipal));
//            newGroupList.addAll(getIndexationMinUpdaterGroupSet(stPrincipal));
//            newGroupList.addAll(getIndexationMinPubliUpdaterGroupSet(stPrincipal));
//            newGroupList.addAll(getIndexationDirUpdaterGroupSet(stPrincipal));
//            newGroupList.addAll(getIndexationDirPubliUpdaterGroupSet(stPrincipal));
            
            return newGroupList;
        } catch (Exception e) {
            LOGGER.error(null, EppLogEnumImpl.FAIL_ASSOCIATE_EPP_GROUPS_TO_CONNECTED_USER_TEC,e) ;            
            return groupList;
        }
    }

    @Override
    public List<String> getParentsGroupNames(String groupName) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public List<String> getSubGroupsNames(String groupName) throws Exception {
        return Collections.emptyList();
    }

    /**
     * Retourne faux: aucune fonction unitaire ne doit être vue comme un groupe.
     */
    @Override
    public boolean hasGroup(String name) throws Exception {
        return false;
    }

    /**
     * Returns an empty list for efficiency
     */
    @Override
    public List<String> getAllGroupIds() throws Exception {
        return Collections.emptyList();
    }

    /**
     * Returns an empty list as mailboxes are not searchable
     */
    @Override
    public List<String> searchGroups(Map<String, Serializable> filter,
            HashSet<String> fulltext) throws Exception {
        return Collections.emptyList();
    }

    @Override
    public List<String> getGroupMembers(String groupName) throws Exception {
        return Collections.emptyList();
    }
}
