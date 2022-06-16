package fr.sword.naiad.nuxeo.ufnxql.core.query.mapper;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.IterableQueryResult;

import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker;

/**
 * Extrait un resultat de comptage
 * 
 * Attend une map d'une ligne contenant un champ FlexibleQueryMaker.COL_COUNT
 * 
 * @author spesnel
 */
public class CountMapper implements Mapper<Long> {
	private static final String ERROR_MSG_COL_MISSING_FMT = "Result map should contains '%s' (probably 'AS %s' missing)";

	/**
	 * Constructeur par défaut.
	 */
	public CountMapper() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long doMapping(IterableQueryResult resultSet) throws NuxeoException {
		Long count = -1L;
		Iterator<Map<String, Serializable>> iterator = resultSet.iterator();

		if (iterator.hasNext()) {
			Map<String, Serializable> next = iterator.next();
			Serializable value = next.get(FlexibleQueryMaker.COL_COUNT);

			if (value == null) {
				throw new NuxeoException(String.format(ERROR_MSG_COL_MISSING_FMT, FlexibleQueryMaker.COL_COUNT,
						FlexibleQueryMaker.COL_COUNT));
			}

			count = (Long) value;
		}

		return count;
	}
}
