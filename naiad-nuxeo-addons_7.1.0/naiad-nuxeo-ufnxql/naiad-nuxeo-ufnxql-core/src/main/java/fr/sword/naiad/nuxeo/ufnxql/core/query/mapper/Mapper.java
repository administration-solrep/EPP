package fr.sword.naiad.nuxeo.ufnxql.core.query.mapper;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.IterableQueryResult;

/**
 * Interface décrivant les mapper convertissant le résultat d'une requête en un type donné.
 * 
 * @author SPL
 */
public interface Mapper<T> {
	T doMapping(IterableQueryResult resultSet) throws NuxeoException;
}
