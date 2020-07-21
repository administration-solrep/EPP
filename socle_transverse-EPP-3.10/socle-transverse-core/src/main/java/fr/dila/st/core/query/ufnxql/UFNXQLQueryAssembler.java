package fr.dila.st.core.query.ufnxql;

import static fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler.Emplacement.AFTER_WHERE;
import static fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler.Emplacement.BEFORE_WHERE;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.core.jointure.CorrespondenceDescriptor;
import fr.dila.st.core.jointure.DependencyDescriptor;

/**
 * Forme une requête NXSQL valide à partir d'une chaîne de caractère représentant la clause WHERE de la requête.
 * 
 * @author jgomez
 * 
 */

public abstract class UFNXQLQueryAssembler implements QueryAssembler {

	/**
	 * L'emplacement du bout de requête de la correspondance par rapport à la clause WHERE.
	 * 
	 * @author admin
	 * 
	 */
	public enum Emplacement {
		BEFORE_WHERE, AFTER_WHERE;

		public List<String> filter(List<CorrespondenceDescriptor> correspondances) {
			List<String> results = new ArrayList<String>();
			for (CorrespondenceDescriptor c : correspondances) {
				if (c.getEmplacement() == this) {
					results.add(c.getQueryPart());
				}
			}
			return results;
		}

		public String extractQueryPart(List<CorrespondenceDescriptor> correspondances) {
			List<String> queryParts = this.filter(correspondances);
			return StringUtils.join(queryParts, StringUtils.EMPTY);
		}

	}

	private String								whereClause;
	protected List<CorrespondenceDescriptor>	correspondences;
	private Boolean								isDefault;

	public UFNXQLQueryAssembler() {
		this.correspondences = new ArrayList<CorrespondenceDescriptor>();
		this.isDefault = false;
	}

	public List<CorrespondenceDescriptor> getCorrespondences() {
		return this.correspondences;
	}

	@Override
	public void setWhereClause(String clause) {
		this.whereClause = clause;
	}

	@Override
	public String getFullQuery() {
		if (StringUtils.isBlank(whereClause)) {
			return getAllResultsQuery();
		}
		String resultQuery = new String(whereClause);
		List<CorrespondenceDescriptor> usefulCorrespondences = get_useful_correspondences(whereClause);
		resultQuery = transformQuery(resultQuery, usefulCorrespondences);
		return resultQuery;
	}

	/**
	 * La requête à faire s'il n'y a rien dans la clause WHERE. Cette requête doit renvoyer tous les résultats.
	 * 
	 * @return
	 */
	public abstract String getAllResultsQuery();

	/**
	 * Construit les dépendances entre les différentes correspondances (Exemple: une jointure sur la question permet
	 * ensuite d'obtenir une réponse)
	 * 
	 * @param prefix
	 * @param neededPrefix
	 */
	private void build_dependence(String prefix, String neededPrefix) {
		List<CorrespondenceDescriptor> target_correspondences = new ArrayList<CorrespondenceDescriptor>();
		for (CorrespondenceDescriptor c : correspondences) {
			if (c.getDocPrefix().equals(prefix)) {
				target_correspondences.add(c);
			}
		}
		for (CorrespondenceDescriptor c : target_correspondences) {
			for (CorrespondenceDescriptor potential_dep : correspondences) {
				if (potential_dep.getDocPrefix().equals(neededPrefix)) {
					c.addDependency(potential_dep);
				}
			}
		}
	}

	/**
	 * Ajoute une correspondance à l'assembler
	 * 
	 * @param le
	 *            descripteur de la correspondance
	 */
	public void add(CorrespondenceDescriptor c) {
		this.correspondences.add(c);
	}

	/**
	 * Construit une dépendance.
	 * 
	 * @param le
	 *            descripteur de la dépendance.
	 */
	public void build(DependencyDescriptor d) {
		this.build_dependence(d.getPrefix(), d.getNeed());
	}

	/**
	 * Retourne la requête UFNXQL à partir de la liste des correspondances
	 * 
	 * @param query
	 * @param correspondances
	 * @return la requête UFNXQL.
	 */
	public String transformQuery(String query, List<CorrespondenceDescriptor> correspondances) {
		String before_part_query = BEFORE_WHERE.extractQueryPart(correspondances);
		String after_part_query = AFTER_WHERE.extractQueryPart(correspondances);
		if (!StringUtils.isBlank(after_part_query)) {
			after_part_query = " " + after_part_query;
		}
		String result_query = before_part_query + " WHERE ((" + whereClause + ")" + after_part_query + ")";
		return result_query;
	}

	public List<CorrespondenceDescriptor> get_useful_correspondences(String whereClause) {
		List<CorrespondenceDescriptor> results = new ArrayList<CorrespondenceDescriptor>();
		for (CorrespondenceDescriptor c : correspondences) {
			if (whereClause.contains(c.getDocPrefix())) {
				for (CorrespondenceDescriptor dependency : c.getDependencies()) {
					if (!results.contains(dependency)) {
						results.add(dependency);
					}
				}
				if (!results.contains(c)) {
					results.add(c);
				}
			}
		}
		return results;
	}

	public String getWhereClause() {
		return whereClause;
	}

	@Override
	public Boolean getIsDefault() {
		return isDefault;
	}

	@Override
	public void setIsDefault(Boolean isDefault) {
		if (isDefault == null) {
			this.isDefault = false;
		} else {
			this.isDefault = isDefault;
		}
	}
}
