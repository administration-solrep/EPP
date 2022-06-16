package fr.sword.naiad.nuxeo.commons.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;

import fr.sword.naiad.nuxeo.commons.core.constant.AclConstant;
import fr.sword.naiad.nuxeo.commons.core.descriptor.AceDescriptor;

/**
 * Manipulation des acls local d'un document
 * 
 * @author SPL
 * 
 */
public final class AclUtil {

    /**
     * Utility class
     */
    private AclUtil() {
        // do nothing
    }

    /**
     * Crée l'acl local en complétant un ensemble de droits spécifiques par le droit : everything pour le groupe administrators et le droit nécessaire à bloquer l'héritage (everyone:everything:false)
     * si blockInherited est vrai
     * 
     * @param specificRights
     *            droits à positionner
     * @param blockInherited
     *            mettre à vrai pour bloquer l'héritage
     * @return acl local
     */
    public static ACL completedAclLocal(final List<ACE> specificRights, final boolean blockInherited) {
        final ACL acl = new ACLImpl(AclConstant.ACL_LOCAL);

        acl.add(new ACE(AclConstant.GROUP_ADMINISTRATORS, SecurityConstants.EVERYTHING, true));

        acl.addAll(specificRights);

        if (blockInherited) {
            acl.add(new ACE(SecurityConstants.EVERYONE, SecurityConstants.EVERYTHING, false));
        }

        return acl;
    }

    /**
     * Remplace la configuration de droit d'un document par la configuration porté par acl
     * 
     * @param session
     *            la session
     * @param ref
     *            la reference du document à modifier
     * @param acl
     *            la nouvelle configuration
     * @throws NuxeoException
     */
    public static void replaceAcl(final CoreSession session, final DocumentRef ref, final ACL acl)
            throws NuxeoException {
        final ACP acp = new ACPImpl();
        acp.addACL(acl);
        session.setACP(ref, acp, false);
    }

    /**
     * Modifie la configuration de droit d'un document en ajoutant des ace a la liste "local"
     * 
     * @param session
     *            la session nuxeo
     * @param docRef
     *            document à modifier
     * @param ace
     *            liste d'ace a ajouter
     * @param after
     *            ajoute les ace à la fin de la liste si vrai, au debut si faux
     * @throws NuxeoException
     */
    public static void addAceToLocal(final CoreSession session, final DocumentRef docRef, final List<ACE> ace,
            final boolean after) throws NuxeoException {
        final ACP acp = session.getACP(docRef);

        final ACL curAcl = acp.getOrCreateACL(AclConstant.ACL_LOCAL);
        if (after) {
            curAcl.addAll(ace);
        } else {
            final ACL acl = new ACLImpl(AclConstant.ACL_LOCAL);
            acl.addAll(ace);
            acl.addAll(curAcl);
            acp.addACL(acl);
        }
        session.setACP(docRef, acp, false);
    }

    public static List<ACE> aceDescrToAce(final List<AceDescriptor> aceDescr) {
        if (aceDescr == null) {
            return null;
        }
        if (aceDescr.isEmpty()) {
            return Collections.emptyList();
        }
        final List<ACE> aceList = new ArrayList<ACE>();
        for (final AceDescriptor descr : aceDescr) {
            aceList.add(new ACE(descr.getPrincipal(), descr.getPermission(), descr.isGranted()));
        }
        return aceList;
    }

    public static ACL cleanupACL(final ACL acl) {
        final ACE[] aces = acl.getACEs();
        if (aces != null) {
            final Map<String, ACE> cache = new LinkedHashMap<String, ACE>();
            for (final ACE ace : aces) {
                cache.put(ace.getUsername() + ace.getPermission() + String.valueOf(ace.isGranted()), ace);
            }

            final Collection<ACE> finalAces = cache.values();
            acl.setACEs(finalAces.toArray(new ACE[finalAces.size()]));
        }
        return acl;
    }

}
