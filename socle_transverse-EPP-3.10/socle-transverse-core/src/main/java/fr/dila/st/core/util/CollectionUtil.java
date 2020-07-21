package fr.dila.st.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Classe utilitaire sur les collections.
 * 
 * @author arolin
 */
public final class CollectionUtil {

	/**
	 * utility class
	 */
	private CollectionUtil() {
		// do nothing
	}

	/**
	 * Renvoie un objet de type List<T> triée à partir d'une Collection<T>.
	 * 
	 * @param <T>
	 * @param collection
	 *            Collection<T>
	 * @return
	 */
	public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> collection) {
		List<T> list = new ArrayList<T>(collection);
		java.util.Collections.sort(list);
		return list;
	}

	/**
	 * renvoie true si la collection 1 contient au moins un element de la collection 2
	 * 
	 * @param <T>
	 * @param collection1
	 * @param collection2
	 * @return
	 */
	public static <T> boolean containsOneElement(Collection<T> collection1, Collection<T> collection2) {
		if (collection1 == null || collection2 == null) {
			return false;
		}
		for (T t : collection1) {
			if (collection2.contains(t)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Renvoie true si la collection est null ou vide.
	 * 
	 * @param collection
	 * @return <code>true</code> si la collection est <code>null</code> ou une
	 *         collection vide.
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Renvoie true si la liste n'est ni null ni vide.
	 * 
	 * @param list
	 * @return <code>true</code> si la liste n'est pas <code>null</code> et si
	 *         elle n'est pas une liste vide.
	 */
	public static boolean isNotEmpty(List<?> list) {
		return !isEmpty(list);
	}
}
