package fr.sword.naiad.nuxeo.ufnxql.core.query.mapper;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.IterableQueryResult;

public class NullMapper implements Mapper<Object> {
	/**
	 * Constructeur par défaut.
	 */
	public NullMapper() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object doMapping(IterableQueryResult resultSet) throws NuxeoException {
		return null;
	}
}
