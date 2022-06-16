package fr.sword.naiad.nuxeo.ufnxql.core.query.mapper;

import java.io.Serializable;
import java.util.Map;

import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * 
 * Transforme une Map de donnée en un objet d'un type donné
 *
 * @param <T> le type d'objet généré
 */
public interface RowMapper<T> {
	
	T doMapping(Map<String, Serializable> rowData) throws NuxeoException;
	
}
