package fr.dila.st.core.operation.document;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;

import com.google.common.base.Joiner;

import fr.dila.st.api.constant.STOrganigrammeConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.core.organigramme.UniteStructurelleNodeImpl;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.core.util.StringUtil;

public abstract class RepriseLDAP {

	protected static final Log						LOG						= LogFactory.getLog(RepriseLDAP.class);
	@Context
	protected CoreSession							session;

	protected static volatile PersistenceProvider	persistenceProvider;

	protected Map<String, List<String>>				mapEnfantPstParentUst	= new TreeMap<String, List<String>>();
	protected Map<String, List<String>>				mapEnfantUstParentUst	= new TreeMap<String, List<String>>();

	protected Map<String, List<String>>				mapEnfantPstParentEnt	= new TreeMap<String, List<String>>();
	protected Map<String, List<String>>				mapEnfantUstParentEnt	= new TreeMap<String, List<String>>();

	protected Map<String, String>					mapEnfantEntParentGvt	= new TreeMap<String, String>();

	@OperationMethod
	public abstract void run() throws Exception;

	protected void reprise(EntityManager manager) {

		List<GouvernementNode> allGouvs = getAllGouvernements();
		if (allGouvs != null) {
			LOG.info("Reprise des gouvernements");
			for (GouvernementNode gouv : allGouvs) {
				LOG.info("Migration du gouvernement ayant l'identifiant : " + gouv.getId());
				manager.persist(gouv);
			}
			manager.flush();
			LOG.info("Fin de reprise des gouvernements");
		}

		List<EntiteNode> allMins = getAllEntites();
		if (allMins != null) {
			LOG.info("Reprise des ministères");
			for (EntiteNode ent : allMins) {
				LOG.info("Migration du ministère ayant l'identifiant : " + ent.getId());

				if (mapEnfantEntParentGvt.containsKey(ent.getId())) {
					String parentId = mapEnfantEntParentGvt.get(ent.getId());

					LOG.info("On a bien un parent qui est " + parentId);
					ent.setParentGouvernement(parentId);
				}

				manager.persist(ent);
			}
			manager.flush();
			LOG.info("Fin de reprise des ministères");
		}

		LOG.info("Reprise des unités structurelles");
		Map<String, UniteStructurelleNode> allUnits = getAllUnitesAndDirections();
		for (Entry<String, UniteStructurelleNode> entry : allUnits.entrySet()) {

			UniteStructurelleNode unit = entry.getValue();
			LOG.info("Migration de l'unité ayant l'identifiant : " + unit.getId());

			if (mapEnfantUstParentEnt.containsKey(unit.getId())) {
				List<String> parentIDs = mapEnfantUstParentEnt.get(unit.getId());
				Joiner join = Joiner.on(";").skipNulls();
				LOG.info("On a bien un parent qui est un ministère " + parentIDs + " pour l'élément " + unit.getId());
				unit.setParentEntiteId(join.join(parentIDs));

			}

			if (mapEnfantUstParentUst.containsKey(unit.getId())) {

				List<String> parentIDs = mapEnfantUstParentUst.get(unit.getId());

				for (String parentId : parentIDs) {

					UniteStructurelleNode unitParent = manager.find(UniteStructurelleNodeImpl.class, parentId);

					String prefix = (StringUtil.isNotBlank(unit.getParentUniteId()) ? unit.getParentUniteId() + ";"
							: "");

					LOG.info("On a bien un parent qui est une unité " + parentId + " pour l'élément " + unit.getId());
					if (unitParent != null) {
						unit.setParentUniteId(prefix + parentId);
					} else {
						saveUniteParents(parentId, allUnits, manager);
						unit.setParentUniteId(prefix + parentId);
					}
				}
			}

			UniteStructurelleNode unitParent = manager.find(UniteStructurelleNodeImpl.class, unit.getId());
			if (unitParent == null) {
				manager.persist(unit);
				manager.flush();
			}
		}
		LOG.info("Fin de reprise des unités structurelles");

		LOG.info("Reprise des postes");
		List<PosteNode> allPostes = getAllPostes();
		for (PosteNode poste : allPostes) {

			LOG.info("Migration du poste ayant l'identifiant : " + poste.getId());

			if (mapEnfantPstParentEnt.containsKey(poste.getId())) {
				List<String> parentIDs = mapEnfantPstParentEnt.get(poste.getId());
				Joiner join = Joiner.on(";").skipNulls();
				LOG.info("On a bien un parent qui est un ministère " + parentIDs + " pour l'élément " + poste.getId());
				poste.setParentEntiteId(join.join(parentIDs));

			}

			if (mapEnfantPstParentUst.containsKey(poste.getId())) {
				List<String> parentIDs = mapEnfantPstParentUst.get(poste.getId());
				Joiner join = Joiner.on(";").skipNulls();
				LOG.info("On a bien un parent qui est une unité " + parentIDs + " pour l'élément " + poste.getId());
				poste.setParentUniteId(join.join(parentIDs));
			}
			manager.persist(poste);
			manager.flush();
		}

		LOG.info("Fin de la reprise");
	}

	private void saveUniteParents(String firstParentId, Map<String, UniteStructurelleNode> allUnits,
			EntityManager manager) {
		UniteStructurelleNode unit = allUnits.get(firstParentId);
		LOG.info("Migration de l'unité parent ayant l'identifiant : " + unit.getId());

		if (unit != null) {
			if (mapEnfantUstParentEnt.containsKey(unit.getId())) {
				List<String> parentIDs = mapEnfantUstParentEnt.get(unit.getId());
				Joiner join = Joiner.on(";").skipNulls();
				LOG.info("On a bien un parent qui est un ministère " + parentIDs + " pour l'élément " + unit.getId());
				unit.setParentEntiteId(join.join(parentIDs));

			}
			if (mapEnfantUstParentUst.containsKey(unit.getId())) {
				List<String> parentIDs = mapEnfantUstParentUst.get(unit.getId());

				for (String parentId : parentIDs) {

					UniteStructurelleNode unitParent = manager.find(UniteStructurelleNodeImpl.class, parentId);

					String prefix = (StringUtil.isNotBlank(unit.getParentUniteId()) ? unit.getParentUniteId() + ";"
							: "");

					LOG.info("On a bien un parent qui est une unité " + parentId + " pour l'élément " + unit.getId());
					if (unitParent != null) {
						unit.setParentUniteId(prefix + parentId);
					} else {
						saveUniteParents(parentId, allUnits, manager);
						unit.setParentUniteId(prefix + parentId);
					}
				}
			}

			manager.persist(unit);
			manager.flush();
		}

	}

	/**
	 * Permet de récupérer l'intégralité des gouvernements sans les liens enfants
	 * 
	 * @return
	 */
	protected abstract List<GouvernementNode> getAllGouvernements();

	/**
	 * Permet de récupérer l'intégralité des ministères sans le lien vers les parents
	 * 
	 * @return
	 */
	protected abstract List<EntiteNode> getAllEntites();

	/**
	 * Permet de récupérer la liste de l'intégralité des Unités et des directions sans les liens des parents
	 * 
	 * @return
	 */
	protected abstract Map<String, UniteStructurelleNode> getAllUnitesAndDirections();

	/**
	 * Permet de récupérer les postes mais on ne mappe pas encore avec les parents
	 * 
	 * @return
	 */
	protected abstract List<PosteNode> getAllPostes();

	protected void mapOrganigrammeData(OrganigrammeNode node, String schema, DocumentModel loadedGroup) {

		node.setId(PropertyUtil.getStringProperty(loadedGroup, schema, STOrganigrammeConstant.GROUPNAME_PROPERTY));
		node.setDateDebut(PropertyUtil.getCalendarProperty(loadedGroup, schema,
				STOrganigrammeConstant.DATE_DEBUT_PROPERTY));
		node.setDateFin(PropertyUtil.getCalendarProperty(loadedGroup, schema, STOrganigrammeConstant.DATE_FIN_PROPERTY));
		String deleted = PropertyUtil.getStringProperty(loadedGroup, schema, "deleted");
		node.setDeleted(Boolean.parseBoolean(deleted));
		node.setLabel(PropertyUtil.getStringProperty(loadedGroup, schema, STOrganigrammeConstant.LABEL_PROPERTY));
		node.setLockDate(PropertyUtil.getCalendarProperty(loadedGroup, schema,
				STSchemaConstant.ORGANIGRAMME_LOCK_DATE_PROPERTY));
		node.setLockUserName(PropertyUtil.getStringProperty(loadedGroup, schema,
				STSchemaConstant.ORGANIGRAMME_LOCK_USER_NAME_PROPERTY));
		node.setFunctionRead(PropertyUtil.getStringListProperty(loadedGroup, schema,
				STOrganigrammeConstant.FUNCTION_READ_PROPERTY));
	}

	protected static PersistenceProvider getOrCreatePersistenceProvider() {
		if (persistenceProvider == null) {
			synchronized (RepriseLDAP.class) {
				if (persistenceProvider == null) {
					activatePersistenceProvider();
				}
			}
		}
		return persistenceProvider;
	}

	private static void activatePersistenceProvider() {
		Thread thread = Thread.currentThread();
		ClassLoader last = thread.getContextClassLoader();
		try {
			thread.setContextClassLoader(PersistenceProvider.class.getClassLoader());
			PersistenceProviderFactory persistenceProviderFactory = Framework
					.getLocalService(PersistenceProviderFactory.class);
			persistenceProvider = persistenceProviderFactory.newProvider("organigramme-provider");
			persistenceProvider.openPersistenceUnit();
		} finally {
			thread.setContextClassLoader(last);
		}
	}

	private static void deactivatePersistenceProvider() {
		if (persistenceProvider != null) {
			synchronized (RepriseLDAP.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	public void deactivate(ComponentContext context) throws Exception {
		deactivatePersistenceProvider();
	}
}
