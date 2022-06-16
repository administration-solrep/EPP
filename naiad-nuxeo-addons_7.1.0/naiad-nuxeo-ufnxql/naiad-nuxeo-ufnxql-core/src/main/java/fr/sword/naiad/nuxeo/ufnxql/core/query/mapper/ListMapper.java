package fr.sword.naiad.nuxeo.ufnxql.core.query.mapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.IterableQueryResult;

/**
 * Map a result set to a list of objet
 * 
 * @author spesnel
 * 
 * @param <T>
 *            type d'objet
 */
public class ListMapper<T> implements Mapper<List<T>> {
	private final RowMapper<T> rowMapper;

	public ListMapper(RowMapper<T> rowMapper) {
		this.rowMapper = rowMapper;
	}

	@Override
	public List<T> doMapping(IterableQueryResult resultSet) throws NuxeoException {
		List<T> result = new ArrayList<T>();
		Iterator<Map<String, Serializable>> iterator = resultSet.iterator();

		while (iterator.hasNext()) {
			Map<String, Serializable> next = iterator.next();
			result.add(rowMapper.doMapping(next));
		}

		return result;
	}
}
