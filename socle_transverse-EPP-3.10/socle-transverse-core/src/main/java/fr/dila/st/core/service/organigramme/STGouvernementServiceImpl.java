package fr.dila.st.core.service.organigramme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.persistence.PersistenceProvider;
import org.nuxeo.ecm.core.persistence.PersistenceProviderFactory;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.core.organigramme.GouvernementNodeImpl;
import fr.dila.st.core.service.STServiceLocator;

/**
 * 
 */
public class STGouvernementServiceImpl extends DefaultComponent implements STGouvernementService {
	private static volatile PersistenceProvider	persistenceProvider;

	private static final String					CUR_GOUVERNEMENT_QUERY	= "SELECT g FROM GouvernementNode g WHERE g.dateFin is null OR g.dateFin > :curDate ORDER BY g.dateDebut ASC";
	private static final String					ALL_GOUVERNEMENT_QUERY	= "SELECT g FROM GouvernementNode g ORDER BY dateDebut ASC";

	@SuppressWarnings("unchecked")
	@Override
	public List<GouvernementNode> getGouvernementList() {
		try {
			return getOrCreatePersistenceProvider().run(true, new PersistenceProvider.RunCallback<List<GouvernementNode>>() {
				@Override
				public List<GouvernementNode> runWith(EntityManager em) throws ClientException {
					Query query = em.createQuery(ALL_GOUVERNEMENT_QUERY);
					List<GouvernementNode> gvtList = query.getResultList();
					return gvtList;
				}
			});
		} catch (ClientException e) {
			throw new RuntimeException("Erreur de récupération de la liste des gouvernements", e);
		}
	}

	@Override
	public GouvernementNode getCurrentGouvernement() {
		try {
			return getOrCreatePersistenceProvider().run(true, new PersistenceProvider.RunCallback<GouvernementNode>() {
				@Override
				public GouvernementNode runWith(EntityManager em) throws ClientException {
					Query query = em.createQuery(CUR_GOUVERNEMENT_QUERY);
					query.setParameter("curDate", Calendar.getInstance());

					return (GouvernementNode) query.getResultList().get(0);
				}
			});
		} catch (ClientException e) {
			throw new RuntimeException("Erreur de récupération du gouvernement courant", e);
		}
	}

	@Override
	public GouvernementNode getGouvernement(String gouvernementId) throws ClientException {
		return (GouvernementNode) STServiceLocator.getOrganigrammeService().getOrganigrammeNodeById(gouvernementId,
				OrganigrammeType.GOUVERNEMENT);
	}

	@Override
	public GouvernementNode getBareGouvernementModel() {

		return new GouvernementNodeImpl();

	}

	@Override
	public void createGouvernement(GouvernementNode newGouvernement) throws ClientException {

		STServiceLocator.getOrganigrammeService().createNode(newGouvernement);
	}

	@Override
	public void updateGouvernement(GouvernementNode gouvernement) throws ClientException {

		STServiceLocator.getOrganigrammeService().updateNode(gouvernement, Boolean.TRUE);
	}

	@Override
	public void setDateNewGvt(String currentGouvernement, String nextGouvernement) throws ClientException {

		GouvernementNode currentGouvNode = getGouvernement(currentGouvernement);
		GouvernementNode nextGouvNode = getGouvernement(nextGouvernement);

		// mise à jour de la date fin à 23h59
		Calendar cal = Calendar.getInstance();
		cal.setTime(nextGouvNode.getDateDebut());
		cal.add(Calendar.SECOND, -1);
		currentGouvNode.setDateFin(cal.getTime());

		List<OrganigrammeNode> nodesToUpdate = new ArrayList<OrganigrammeNode>();
		nodesToUpdate.add(currentGouvNode);
		nodesToUpdate.add(nextGouvNode);
		STServiceLocator.getOrganigrammeService().updateNodes(nodesToUpdate, false);

	}

	private static PersistenceProvider getOrCreatePersistenceProvider() {
		if (persistenceProvider == null) {
			synchronized (STGouvernementServiceImpl.class) {
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
			synchronized (STGouvernementServiceImpl.class) {
				if (persistenceProvider != null) {
					persistenceProvider.closePersistenceUnit();
					persistenceProvider = null;
				}
			}
		}
	}

	@Override
	public void deactivate(ComponentContext context) throws Exception {
		deactivatePersistenceProvider();
	}

}
