package fr.dila.st.core.groupcomputer;

import fr.dila.cm.security.CaseManagementSecurityConstants;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.service.STServiceLocator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.platform.computedgroups.AbstractGroupComputer;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Ce calculateur de groupe injecte dans le principal les groupes correspondant aux mailbox personelles.
 *
 * @author jtremeaux
 */
public class PersonalMailboxGroupComputer extends AbstractGroupComputer {
    private static final Log LOG = LogFactory.getLog(PersonalMailboxGroupComputer.class);

    /**
     * Default constructor
     */
    public PersonalMailboxGroupComputer() {
        super();
    }

    @Override
    public List<String> getGroupsForUser(NuxeoPrincipalImpl nuxeoPrincipal) {
        if (nuxeoPrincipal == null) {
            return Collections.emptyList();
        }

        // Récupère les groupes actuels du principal
        List<String> groupList = nuxeoPrincipal.getGroups();

        try {
            List<String> newGroupList = new ArrayList<String>(groupList);
            // Injecte les groupes donnant accès aux mailbox personnelles
            Set<String> persoMailboxGroupSet = getPersoMailboxGroupSet(nuxeoPrincipal);
            newGroupList.addAll(persoMailboxGroupSet);

            return newGroupList;
        } catch (Exception e) {
            LOG.error("Impossible d'associer les groupes mailbox personnelles à l'utilisateur connecté.", e);
            return groupList;
        }
    }

    /**
     * Recherche l'ensemble des mailbox personnelles d' un utilisateur, puis construit et retourne les groupes
     * permettant d'accéder à ces mailbox personnelles.
     *
     * @param principal
     *            Principal
     * @return Ensemble des groupes
     * @throws DirectoryException
     *             DirectoryException
     * @throws ClientException
     *             ClientException
     */
    private Set<String> getPersoMailboxGroupSet(NuxeoPrincipalImpl nuxeoPrincipal) {
        // Récupère l'utilisateur
        UserManager userManager = STServiceLocator.getUserManager();
        DocumentModel userModel = userManager.getUserModel(nuxeoPrincipal.getName());
        if (userModel == null) {
            LOG.debug(String.format("No User by that name. Maybe a wrong id or virtual user"));
            return Collections.emptySet();
        }

        // Injecte les groupes pour les mailbox personnelles de l'utilisateur
        MailboxService mailboxService = STServiceLocator.getMailboxService();
        Set<String> groupSet = new HashSet<String>();
        String mailboxId = mailboxService.getUserPersonalMailboxId(nuxeoPrincipal.getName());
        String groupId = CaseManagementSecurityConstants.MAILBOX_PREFIX + mailboxId;
        groupSet.add(groupId);

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

    @Override
    public List<String> getGroupMembers(String groupName) {
        return Collections.emptyList();
    }
}
