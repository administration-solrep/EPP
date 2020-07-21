package fr.dila.st.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;

import fr.dila.st.api.constant.STAclConstant;
import fr.dila.st.api.service.SecurityService;

/**
 * Implémentation du service de sécurité du socle transverse.
 * 
 * @author jtremeaux
 */
public class SecurityServiceImpl implements SecurityService {

	private static final Log	LOG	= LogFactory.getLog(SecurityServiceImpl.class);

	/**
	 * Default constructor
	 */
	public SecurityServiceImpl() {
		// do nothing
	}

	@Override
	public void addAceToAcp(ACP acp, String aclName, String group, String privilege) throws ClientException {
		addAceToAcp(acp, aclName, group, privilege, true);
	}

	@Override
	public void removeAceToAcp(ACP acp, String aclName, String group, String privilege, boolean grant)
			throws ClientException {
		ACL acl = acp.getOrCreateACL(aclName);
		acl.remove(new ACE(group, privilege, grant));
	}

	@Override
	public void removeAceToAcp(DocumentModel doc, String aclName, String group, String privilege)
			throws ClientException {
		ACP acp = doc.getACP();
		ACL acl = acp.getACL(aclName);
		if (acl == null) {
			// si l'acl n'existe pas, on n'as pas besoin desupprimer l'ACE
			// on signale dans les logs que l'on a pas trouvé l'Acl
			LOG.info("L'acl '" + aclName + "' n'existe pas : l'ACE '" + group + "' n'as donc pas été supprimé.");
			return;
		}
		acl.remove(new ACE(group, privilege, true));
	}

	@Override
	public void addAceToAcp(ACP acp, String aclName, String group, String privilege, boolean grant)
			throws ClientException {
		ACL acl = acp.getOrCreateACL(aclName);
		acl.add(new ACE(group, privilege, grant));
	}

	@Override
	public void addAceToAcl(DocumentModel doc, String aclName, String group, String privilege) throws ClientException {
		ACP acp = doc.getACP();
		ACL acl = acp.getOrCreateACL(aclName);
		acl.add(new ACE(group, privilege, true));
		doc.setACP(acp, true);
	}

	@Override
	public void addAceToSecurityAcl(DocumentModel doc, String baseFunction, String privilege) throws ClientException {
		addAceToAcl(doc, STAclConstant.ACL_SECURITY, baseFunction, privilege);
	}

	@Override
	public void addAceToSecurityAcl(CoreSession session, DocumentModel doc, String baseFunction, String privilege)
			throws ClientException {
		ACP acp = doc.getACP();
		ACL functionAcl = acp.getOrCreateACL(STAclConstant.ACL_SECURITY);
		functionAcl.add(new ACE(baseFunction, privilege, true));
		acp.addACL(functionAcl);
		session.setACP(doc.getRef(), acp, true);
		session.save();
	}

	@Override
	public void addAceToAcpInFirstPosition(ACP acp, String aclName, String group, String privilege, boolean grant)
			throws ClientException {
		// on vérifie que l'acl n'existe pas dans les ACP
		ACL acl = acp.getACL(aclName);
		if (acl != null) {
			// pour pouvoir insérer la nouvelle acl, elle ne doit pas déjà être présente dans le DocumentModel.
			throw new ClientException("l'ACL '" + aclName
					+ "' existe déjà dans les ACP ! On ne peut pas l'insérer en première position !");
		}
		acl = new ACLImpl(aclName);
		acl.add(new ACE(group, privilege, grant));
		acp.addACL(0, acl);
	}

}
