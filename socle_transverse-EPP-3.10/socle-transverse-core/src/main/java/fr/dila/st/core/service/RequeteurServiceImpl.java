package fr.dila.st.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.api.requeteur.RequeteExperte;
import fr.dila.st.api.requeteur.RequeteurFunctionSolver;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.api.service.RequeteurService;
import fr.dila.st.core.descriptor.RequeteurFunctionSolverDescriptor;
import fr.dila.st.core.query.QueryUtils;
import fr.dila.st.core.requeteur.RequeteurFunctionSolverHelper;

/**
 * 
 * L'implementation du service requeteur. Il permet de faire des recherches à partir d'un document nuxeo. (de type
 * SmartFolder)
 * 
 * @author JGZ
 * 
 */
public class RequeteurServiceImpl extends DefaultComponent implements RequeteurService {

	private static final Log				LOGGER		= LogFactory.getLog(RequeteurServiceImpl.class);

	public static final String				NOW			= "NOW";

	public static final String				CONTRIBUTOR	= "CONTRIBUTOR";

	public static final String				SOLVER_EP	= "solvers";

	private List<RequeteurFunctionSolver>	solvers;

	public RequeteurServiceImpl() {
		super();
		solvers = new ArrayList<RequeteurFunctionSolver>();
	}

	@Override
	public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
		if (SOLVER_EP.equals(extensionPoint)) {
			RequeteurFunctionSolverDescriptor desc = (RequeteurFunctionSolverDescriptor) contribution;
			RequeteurFunctionSolver solver = null;
			try {
				solver = desc.getKlass().newInstance();
			} catch (InstantiationException e) {
				LOGGER.error("Solver de classe " + desc.getKlass() + "non trouvé");
				throw new IllegalArgumentException("Assembler de classe " + desc.getKlass() + "non trouvé");
			} catch (IllegalAccessException e) {
				LOGGER.error("Solver de classe " + desc.getKlass() + "non trouvé");
				throw new IllegalArgumentException("Problème d'accès à l'assembler de classe " + desc.getKlass());
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Utilisation de l'objet solver de classe = " + solver.getClass());
			}
			solvers.add(solver);
		}
	}

	@Override
	public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
		// solvers ne contient pas d'objet RequeteurFunctionSolverDescriptor
		// if (SOLVER_EP.equals(extensionPoint)) {
		// solvers.remove((RequeteurFunctionSolverDescriptor) contribution);
		// }
	}

	@Override
	public List<DocumentModel> query(CoreSession session, RequeteExperte requeteExperte) throws ClientException {
		String query = getPattern(session, requeteExperte);
		DocumentRef[] refs = QueryUtils.doUFNXQLQueryForIds(session, query, null);
		return session.getDocuments(refs);
	}

	@Override
	public Long countResults(CoreSession session, RequeteExperte requeteExperte) throws ClientException {
		Map<String, Object> env = new HashMap<String, Object>();
		env.put(CONTRIBUTOR, requeteExperte.getLastContributor());
		String query = getPattern(session, requeteExperte, env);
		return QueryUtils.doCountQuery(session, query);
	}

	@Override
	public String getPattern(CoreSession session, RequeteExperte requeteExperte) {
		String query = getPattern(session, requeteExperte, getEnv());
		LOGGER.info("Requete : " + query);
		return query;
	}

	private Map<String, Object> getEnv() {
		Map<String, Object> env = new HashMap<String, Object>();
		env.put(NOW, new DateTime());
		return env;
	}

	@Override
	public String getPattern(CoreSession session, RequeteExperte requeteExperte, Map<String, Object> env) {
		return getPattern(session, requeteExperte.getWhereClause(), env);
	}

	public String getPattern(CoreSession session, String queryPart, Map<String, Object> env) {
		JointureService jointureService = STServiceLocator.getJointureService();
		QueryAssembler assembler = jointureService.getDefaultQueryAssembler();
		return getPattern(session, assembler, queryPart, env);
	}

	@Override
	public String getPattern(CoreSession session, String queryPart) {
		return getPattern(session, queryPart, getEnv());
	}

	public String getPattern(CoreSession session, QueryAssembler assembler, String queryPart, Map<String, Object> env) {
		String fullquery = QueryUtils.getFullQuery(assembler, queryPart);
		fullquery = resolveKeywords(session, fullquery, env);
		return fullquery;
	}

	@Override
	public String getPattern(CoreSession session, QueryAssembler assembler, String queryPart) {
		return getPattern(session, assembler, queryPart, getEnv());
	}

	/**
	 * Cherche les mots-clés et remplace par l'expression voulue.
	 * 
	 * @param query
	 * @param env
	 * @return la requête complète avec les post-traitements pour la résolution des mot-clés.
	 */
	protected String resolveKeywords(CoreSession session, String query, Map<String, Object> env) {
		for (RequeteurFunctionSolver solver : getSolvers()) {
			query = RequeteurFunctionSolverHelper.apply(solver, session, query, env);
		}
		return query;
	}

	/**
	 * Cherche les mots-clés et remplace par l'expression voulue.
	 * 
	 * @param query
	 * @param env
	 * @return la requête complète avec les post-traitements pour la résolution des mot-clés.
	 */
	@Override
	public String resolveKeywords(CoreSession session, String query) {
		for (RequeteurFunctionSolver solver : getSolvers()) {
			query = RequeteurFunctionSolverHelper.apply(solver, session, query, getEnv());
		}
		return query;
	}

	public void setSolvers(List<RequeteurFunctionSolver> solvers) {
		this.solvers = solvers;
	}

	public List<RequeteurFunctionSolver> getSolvers() {
		return solvers;
	}

}
