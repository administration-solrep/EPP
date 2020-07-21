package fr.dila.solonepp.core.operation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;

import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.persistence.PersistenceProvider.RunCallback;
import org.nuxeo.ecm.directory.ldap.LDAPSession;

import com.google.common.collect.Lists;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STOrganigrammeConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.InstitutionNode;
import fr.dila.st.api.organigramme.LDAPSessionContainer;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.operation.document.RepriseLDAP;
import fr.dila.st.core.organigramme.InstitutionNodeImpl;
import fr.dila.st.core.organigramme.LDAPSessionContainerImpl;
import fr.dila.st.core.organigramme.PosteNodeImpl;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.SessionUtil;

@Operation(id = RepriseOrganigrammeOperation.ID, label = "RepriseOrganigramme")
public class RepriseOrganigrammeOperation extends RepriseLDAP {
	public static final String			ID						= "SolonEpp.Organigramme.Reprise";
	protected Map<String, List<String>>	mapEnfantInstitParent	= new TreeMap<String, List<String>>();

	@OperationMethod
	public void run() throws Exception {
		LOG.info("-------------------------------------------------------------------------------");
		LOG.info("Début opération " + ID);

		try {
			getOrCreatePersistenceProvider().run(true, new RunCallback<Void>() {

				@Override
				public Void runWith(EntityManager manager) throws ClientException {
					reprise(manager);
					return null;
				}
			});

		} catch (Exception e) {
			LOG.error("Une erreur est survenue lors de la reprise " + e.getMessage(), e);
		}

		if (session == null) {
			session = SessionUtil.getCoreSession();
		}
		// Crée automatiquement les Mailbox des institutions
		final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator
				.getMailboxInstitutionService();
		final MailboxService mailboxService = STServiceLocator.getMailboxService();
		mailboxInstitutionService.createAllMailboxInstitution(session, mailboxService.getMailboxRoot(session));
		SessionUtil.close(session);
		LOG.info("Fin opération " + ID);
	}

	@Override
	protected List<GouvernementNode> getAllGouvernements() {
		// Pas de gouvernements dans EPP
		return null;
	}

	@Override
	protected List<EntiteNode> getAllEntites() {
		// Pas de ministères dans EPP
		return null;
	}

	@Override
	protected Map<String, UniteStructurelleNode> getAllUnitesAndDirections() {
		Map<String, UniteStructurelleNode> mapUnites = new TreeMap<String, UniteStructurelleNode>();

		LDAPSessionContainer ldapSessionContainer = new LDAPSessionContainerImpl();
		try {
			LDAPSession session = ldapSessionContainer.getSessionUniteStructurelle();

			Map<String, Serializable> filter = new HashMap<String, Serializable>();
			Map<String, String> orderBy = new HashMap<String, String>();
			orderBy.put("groupName", "asc");
			List<DocumentModel> entries = session.query(filter, null, orderBy);

			for (DocumentModel group : entries) {
				DocumentModel loadedGroup = session.getEntry((String) group.getProperty(session.getDirectory()
						.getSchema(), STConstant.LDAP_GROUP_NAME_PROPERTY));
				UniteStructurelleNode node = new UniteStructurelleNodeImpl();

				mapOrganigrammeData(node, STSchemaConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_SCHEMA, loadedGroup);

				String type = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_SCHEMA, "type");

				node.setTypeValue(type);

				List<String> childrenPoste = PropertyUtil.getStringListProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_SCHEMA, "subPostes");
				List<String> childrenUnite = PropertyUtil.getStringListProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_UNITE_STRUCTURELLE_SCHEMA, "subUnitesStructurelles");

				for (String child : childrenUnite) {
					if (!child.equals("cn=emptyRef")) {
						if (!mapEnfantUstParentUst.containsKey(child)) {
							mapEnfantUstParentUst.put(child, Lists.newArrayList(node.getId()));
						} else {
							mapEnfantUstParentUst.get(child).add(node.getId());
						}
					}
				}

				for (String child : childrenPoste) {
					if (!child.equals("cn=emptyRef")) {
						if (!mapEnfantPstParentUst.containsKey(child)) {
							mapEnfantPstParentUst.put(child, Lists.newArrayList(node.getId()));
						} else {
							mapEnfantPstParentUst.get(child).add(node.getId());
						}
					}
				}

				if (mapEnfantInstitParent.containsKey(node.getId())) {
					List<String> lstInstitParent = mapEnfantInstitParent.get(node.getId());
					node.setParentInstitIds(lstInstitParent);
				}

				mapUnites.put(node.getId(), node);
			}

		} catch (Exception e) {
			LOG.error("Erreur lors de la récupération des unités", e);

		} finally {
			try {
				ldapSessionContainer.closeAll();
			} catch (ClientException e) {
				LOG.error("Erreur lors de la fermeture de la connection au LDAP", e);
			}
		}

		return mapUnites;
	}

	@Override
	protected List<PosteNode> getAllPostes() {
		List<PosteNode> lstPostes = new ArrayList<PosteNode>();
		LDAPSessionContainer ldapSessionContainer = new LDAPSessionContainerImpl();
		try {
			LDAPSession session = ldapSessionContainer.getSessionPoste();

			Map<String, Serializable> filter = new HashMap<String, Serializable>();
			Map<String, String> orderBy = new HashMap<String, String>();
			orderBy.put("groupName", "asc");
			List<DocumentModel> entries = session.query(filter, null, orderBy);

			for (DocumentModel group : entries) {
				DocumentModel loadedGroup = session.getEntry((String) group.getProperty(session.getDirectory()
						.getSchema(), STConstant.LDAP_GROUP_NAME_PROPERTY));
				PosteNode node = new PosteNodeImpl();

				mapOrganigrammeData(node, STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, loadedGroup);
				String chargeMissionSGG = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.CHARGE_MISSION_SGG_PROPERTY);

				node.setChargeMissionSGG(Boolean.parseBoolean(chargeMissionSGG));

				String conseillerPM = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.CONSEILLER_PM_PROPERTY);

				node.setConseillerPM(Boolean.parseBoolean(conseillerPM));

				String posteBdc = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.POSTE_BDC_PROPERTY);

				node.setPosteBdc(Boolean.parseBoolean(posteBdc));

				String posteWs = PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.POSTE_WS_PROPERTY);

				node.setPosteWs(Boolean.parseBoolean(posteWs));

				node.setWsKeyAlias(PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.WS_KEY_ALIAS_PROPERTY));

				node.setWsPassword(PropertyUtil.getStringProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.WS_PWD_PROPERTY));

				node.setWsUrl(PropertyUtil.getStringProperty(loadedGroup, STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA,
						STOrganigrammeConstant.WS_URL_PROPERTY));

				node.setWsUser(PropertyUtil.getStringProperty(loadedGroup, STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA,
						STOrganigrammeConstant.WS_USR_PROPERTY));

				List<String> children = PropertyUtil.getStringListProperty(loadedGroup,
						STSchemaConstant.ORGANIGRAMME_POSTE_SCHEMA, STOrganigrammeConstant.MEMBERS_PROPERTY);

				if (children != null && !children.isEmpty()) {
					node.setMembers(children);
				}

				if (mapEnfantInstitParent.containsKey(node.getId())) {
					List<String> lstInstitParent = mapEnfantInstitParent.get(node.getId());
					node.setParentInstitIds(lstInstitParent);
				}

				lstPostes.add(node);
			}

		} catch (Exception e) {
			LOG.error("Erreur lors de la récupération des postes", e);

		} finally {
			try {
				ldapSessionContainer.closeAll();
			} catch (ClientException e) {
				LOG.error("Erreur lors de la fermeture de la connection au LDAP", e);
			}
		}

		return lstPostes;
	}

	private List<InstitutionNode> getAllInstitutions() {
		List<InstitutionNode> lstGouvs = new ArrayList<InstitutionNode>();
		LDAPSessionContainer ldapSessionContainer = new LDAPSessionContainerImpl();
		try {
			LDAPSession session = ldapSessionContainer.getSession(SolonEppConstant.ORGANIGRAMME_INSTITUTION_DIR);

			Map<String, Serializable> filter = new HashMap<String, Serializable>();
			Map<String, String> orderBy = new HashMap<String, String>();
			orderBy.put("groupName", "asc");
			List<DocumentModel> entries = session.query(filter, null, orderBy);

			for (DocumentModel group : entries) {
				InstitutionNode node = new InstitutionNodeImpl();
				DocumentModel loadedGroup = session.getEntry((String) group.getProperty(session.getDirectory()
						.getSchema(), STConstant.LDAP_GROUP_NAME_PROPERTY));

				node.setLabel(PropertyUtil.getStringProperty(loadedGroup, SolonEppSchemaConstant.INSTITUTION_SCHEMA,
						"label"));
				node.setId(PropertyUtil.getStringProperty(loadedGroup, SolonEppSchemaConstant.INSTITUTION_SCHEMA,
						"groupName"));

				node.setLockUserName(PropertyUtil.getStringProperty(loadedGroup,
						SolonEppSchemaConstant.INSTITUTION_SCHEMA,
						STSchemaConstant.ORGANIGRAMME_LOCK_USER_NAME_PROPERTY));

				node.setLockDate(PropertyUtil.getCalendarProperty(loadedGroup,
						SolonEppSchemaConstant.INSTITUTION_SCHEMA, STSchemaConstant.ORGANIGRAMME_LOCK_DATE_PROPERTY));

				List<String> children = PropertyUtil.getStringListProperty(loadedGroup,
						SolonEppSchemaConstant.INSTITUTION_SCHEMA, "subUnitesStructurelles");

				for (String child : children) {

					if (!child.equals("cn=emptyRef")) {

						if (!mapEnfantInstitParent.containsKey(child)) {
							mapEnfantInstitParent.put(child, Lists.newArrayList(node.getId()));
						} else {
							mapEnfantInstitParent.get(child).add(node.getId());
						}
					}
				}

				children = PropertyUtil.getStringListProperty(loadedGroup, SolonEppSchemaConstant.INSTITUTION_SCHEMA,
						"subPostes");

				for (String child : children) {

					if (!child.equals("cn=emptyRef")) {

						if (!mapEnfantInstitParent.containsKey(child)) {
							mapEnfantInstitParent.put(child, Lists.newArrayList(node.getId()));
						} else {
							mapEnfantInstitParent.get(child).add(node.getId());
						}
					}
				}

				lstGouvs.add(node);
			}

		} catch (Exception e) {
			LOG.error("Erreur lors de la récupération des institutions", e);

		} finally {
			try {
				ldapSessionContainer.closeAll();
			} catch (ClientException e) {
				LOG.error("Erreur lors de la fermeture de la connection au LDAP", e);
			}
		}

		return lstGouvs;
	}

	@Override
	protected void reprise(EntityManager manager) {

		LOG.info("Reprise des institutions");
		List<InstitutionNode> allInstits = getAllInstitutions();

		for (InstitutionNode inst : allInstits) {
			LOG.info("Migration de l'institution ayant l'identifiant : " + inst.getId());
			manager.persist(inst);
		}
		manager.flush();
		LOG.info("Fin de reprise des institutions");
		super.reprise(manager);
	}

}
