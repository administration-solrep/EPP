package fr.dila.solonepp.core.groupcomputer;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.factory.STLogFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.platform.computedgroups.AbstractGroupComputer;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;

/**
 * Ce calculateur de groupe injecte dans le principal les groupes correspondant aux institutions.
 *
 * @author jtremeaux
 */
public class InstitutionGroupComputer extends AbstractGroupComputer {
    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(InstitutionGroupComputer.class);

    @Override
    public List<String> getGroupsForUser(NuxeoPrincipalImpl nuxeoPrincipal) {
        if (nuxeoPrincipal == null) {
            return Collections.emptyList();
        }

        // Récupère les groupes actuels du principal
        List<String> groupList = nuxeoPrincipal.getGroups();

        try {
            List<String> newGroupList = new ArrayList<String>(groupList);

            // Injecte les groupes correspondant aux institutions
            Set<String> institutionGroupSet = getInstitutionGroupSet((EppPrincipal) nuxeoPrincipal);
            newGroupList.addAll(institutionGroupSet);

            return newGroupList;
        } catch (Exception e) {
            LOGGER.error(null, EppLogEnumImpl.FAIL_ASSOCIATE_INSTITUTION_GROUPS_TO_CONNECTED_USER_TEC, e);
            return groupList;
        }
    }

    /**
     * Recherche l'ensemble des institutions d' un utilisateur, puis construit et retourne les groupes correspondant à
     * ces institutions.
     *
     * @param principal
     *            Principal
     * @return Ensemble des groupes
     * @throws DirectoryException
     *             DirectoryException
     */
    private Set<String> getInstitutionGroupSet(EppPrincipal principal) {
        // Récupère la liste des postes de l'utilisateur
        final OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
        Set<String> posteIdSet = principal.getPosteIdSet();
        Set<String> groupSet = new HashSet<String>();
        for (String posteId : posteIdSet) {
            // Recherche l'ensemble des institutions associés au poste
            List<InstitutionNode> institutionNodeList = organigrammeService.getInstitutionParentFromPoste(posteId);
            for (OrganigrammeNode institutionNode : institutionNodeList) {
                final String institutionId = institutionNode.getId();
                final String groupName = SolonEppConstant.INSTITUTION_PREFIX + institutionId;
                groupSet.add(groupName);
            }
        }

        return groupSet;
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

    /**
     * Returns an empty list for efficiency
     */
    @Override
    public List<String> getAllGroupIds() {
        return Collections.emptyList();
    }

    /**
     * Returns an empty list as mailboxes are not searchable
     */
    @Override
    public List<String> searchGroups(Map<String, Serializable> filter, Set<String> fulltext) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getGroupMembers(String groupName) {
        return Collections.emptyList();
    }
}
