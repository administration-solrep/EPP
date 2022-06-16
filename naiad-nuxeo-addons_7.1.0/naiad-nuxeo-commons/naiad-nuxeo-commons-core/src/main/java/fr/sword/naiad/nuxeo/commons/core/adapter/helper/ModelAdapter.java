package fr.sword.naiad.nuxeo.commons.core.adapter.helper;

import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Interface spécifiant qu'un adapteur peut être converti vers un modèle de type T.
 *
 * @author fmh
 */
public interface ModelAdapter<T> {
	
	/**
	 * Conversion de l'objet vers le type T
	 * @param modelClass class du model
	 * @param arguments argument
	 * @return une instance de type T
	 * @throws NuxeoException
	 */
	T convertToModel(Class<T> modelClass, Object... arguments) throws NuxeoException;
}
