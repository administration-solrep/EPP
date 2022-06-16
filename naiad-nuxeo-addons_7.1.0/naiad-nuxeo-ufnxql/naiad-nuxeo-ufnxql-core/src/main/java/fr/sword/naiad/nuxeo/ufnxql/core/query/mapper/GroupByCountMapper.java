package fr.sword.naiad.nuxeo.ufnxql.core.query.mapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;

/**
 * Map un résultat de requête contenant une ou plusieurs lignes formées d'un champ "id" et d'un champ "count".
 * 
 * Typiquement : SELECT doc.ecm:parentId AS id, COUNT(doc.ecm:uuid) AS count FROM Document WHERE ... GROUP BY
 * doc.ecm:parentId
 * 
 * @author fmh
 */
public class GroupByCountMapper implements Mapper<Map<String, Long>> {
	/**
	 * Constructeur par défaut.
	 */
	public GroupByCountMapper() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Long> doMapping(IterableQueryResult resultSet) throws NuxeoException {
		Map<String, Long> counts = new HashMap<String, Long>();
		Iterator<Map<String, Serializable>> iterator = resultSet.iterator();

		while (iterator.hasNext()) {
			Map<String, Serializable> next = iterator.next();
			counts.put((String) next.get(FlexibleQueryMaker.COL_ID),
					Long.parseLong((String) next.get(FlexibleQueryMaker.COL_COUNT)));
		}

		return counts;
	}
}
