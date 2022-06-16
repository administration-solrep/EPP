package fr.sword.naiad.nuxeo.ufnxql.core.query.mapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;

/**
 * Mapper produisant une table de hachage id -&gt; parentId.
 * 
 * Nécessite les champs "id" et "parentId".
 * 
 * @author fmh
 */
public class ParentIdMapper implements Mapper<Map<String, String>> {
	private static final String ERROR_MSG_COL_MISSING_FMT = "Result map must contain columns '%s' and '%s'";

	/**
	 * Constructeur par défaut.
	 */
	public ParentIdMapper() {
		super();
	}

	@Override
	public Map<String, String> doMapping(IterableQueryResult resultSet) throws NuxeoException {
		Map<String, String> ids = new HashMap<String, String>();
		Iterator<Map<String, Serializable>> iterator = resultSet.iterator();

		while (iterator.hasNext()) {
			Map<String, Serializable> next = iterator.next();
			Serializable docId = next.get(FlexibleQueryMaker.COL_ID);
			Serializable parentId = next.get(FlexibleQueryMaker.COL_PARENT_ID);

			if (docId == null || parentId == null) {
				throw new NuxeoException(String.format(ERROR_MSG_COL_MISSING_FMT, FlexibleQueryMaker.COL_ID,
						FlexibleQueryMaker.COL_PARENT_ID));
			}

			ids.put((String) docId, (String) parentId);
		}

		return ids;
	}
}
