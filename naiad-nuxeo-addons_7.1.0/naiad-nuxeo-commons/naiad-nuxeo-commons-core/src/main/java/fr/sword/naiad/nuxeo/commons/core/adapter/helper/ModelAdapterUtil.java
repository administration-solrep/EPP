package fr.sword.naiad.nuxeo.commons.core.adapter.helper;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Classe utilitaire de gestion des ModelAdapter&lt;T&gt;.
 *
 * @author fmh
 */
public final class ModelAdapterUtil {
	/**
	 * Constructeur privé.
	 */
	private ModelAdapterUtil() {
		// Classe utilitaire
	}

	/**
	 * Convertit une liste d'adapteurs en une liste de modèles.
	 * 
	 * @param adapters
	 * @param arguments
	 * @return la liste de modèles
	 * @throws NuxeoException
	 * @throws IllegalArgumentException
	 */
	public static <T> List<T> convertToModel(Class<T> modelClass, List<? extends ModelAdapter<T>> adapters, Object... arguments)
			throws NuxeoException/*, IllegalArgumentException*/ {
		final List<T> models = new ArrayList<T>();

		if (adapters != null) {
			for (ModelAdapter<T> adapter : adapters) {
				models.add(adapter.convertToModel(modelClass, arguments));
			}
		}

		return models;
	}

	/**
	 * Vérifie que la liste d'arguments utilisée pour la conversion est correcte.
	 * 
	 * @param arguments
	 * @param classes
	 * @throws IllegalArgumentException
	 */
	public static void checkArguments(Object[] arguments, Class<?>... classes) /*throws IllegalArgumentException*/ {
		final int argumentsLength = ArrayUtils.getLength(arguments);
		final int classesLength = ArrayUtils.getLength(classes);
		if (argumentsLength != classesLength) {
			throw new IllegalArgumentException(String.format("Expected %d arguments, %d given.", classesLength, argumentsLength));
		}

		if (classes != null) {
			for (int i = 0; i < classes.length; ++i) {
				if (arguments[i] == null || classes[i] == null || !classes[i].isInstance(arguments[i])) {
					throw new IllegalArgumentException(String.format("Expected argument %d to be a %s, %s given.", i + 1, classes[i], arguments[i]));
				}
			}
		}
	}
}
