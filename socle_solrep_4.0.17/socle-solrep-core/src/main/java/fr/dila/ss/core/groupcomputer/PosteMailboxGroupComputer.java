package fr.dila.ss.core.groupcomputer;

import fr.dila.cm.security.CaseManagementSecurityConstants;
import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.core.user.STPrincipalImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.platform.computedgroups.AbstractGroupComputer;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;

/**
 * Ce calculateur de groupe injecte dans le principal les groupes correspondant aux mailbox postes.
 *
 * @author jtremeaux
 */
public class PosteMailboxGroupComputer extends AbstractGroupComputer {
    private static final Log LOG = LogFactory.getLog(PosteMailboxGroupComputer.class);

    /**
     * Default constructor
     */
    public PosteMailboxGroupComputer() {
        super();
    }

    @Override
    public List<String> getGroupsForUser(NuxeoPrincipalImpl nuxeoPrincipal) {
        if (nuxeoPrincipal == null) {
            return Collections.emptyList();
        }

        // Récupère les groupes actuels du principal
        final List<String> groupList = nuxeoPrincipal.getGroups();

        try {
            final List<String> newGroupList = new ArrayList<String>(groupList);
            // Injecte les groupes donnant accès aux mailbox postes
            if (!(nuxeoPrincipal instanceof STPrincipalImpl)) {
                throw new Exception("Le principal doit être du type STPrincipalImpl");
            }
            final STPrincipalImpl stPrincipal = (STPrincipalImpl) nuxeoPrincipal;
            final Set<String> posteMailboxGroupSet = getPosteMailboxGroupSet(stPrincipal);
            newGroupList.addAll(posteMailboxGroupSet);

            return newGroupList;
        } catch (Exception e) {
            LOG.error("Impossible d'associer les groupes postes à l'utilisateur connecté.", e);
            return groupList;
        }
    }

    /**
     * Recherche l'ensemble des mailbox postes d' un utilisateur, puis construit et retourne les groupes permettant
     * d'accéder à ces mailbox postes.
     *
     * @param principal
     *            Principal
     * @return Ensemble des groupes
     * @throws DirectoryException
     *             DirectoryException
     * @throws ClientException
     *             ClientException
     */
    private Set<String> getPosteMailboxGroupSet(STPrincipalImpl stPrincipal) {
        // Récupère la liste des postes de l'utilisateur
        final Set<String> posteIdSet = stPrincipal.getPosteIdSet();
        final Set<String> groupSet = new HashSet<String>();
        final MailboxPosteService mailboxPosteService = SSServiceLocator.getMailboxPosteService();
        for (String posteId : posteIdSet) {
            // Donne accès aux mailbox poste de l'utilisateur
            String mailboxId = mailboxPosteService.getPosteMailboxId(posteId);
            groupSet.add(mailboxId);

            // Donne accès aux Case de l'utilisateur
            mailboxId = CaseManagementSecurityConstants.MAILBOX_PREFIX + mailboxId;
            groupSet.add(mailboxId);
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

    //	/**
    //	 * Returns an empty list as mailboxes are not searchable
    //	 */
    //	@Override
    //	public List<String> searchGroups(Map<String, Serializable> filter, HashSet<String> fulltext) {
    //		return Collections.emptyList();
    //	}

    @Override
    public List<String> getGroupMembers(String groupName) {
        return Collections.emptyList();
    }
}
