package fr.dila.st.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Classe utilitaire sur les collections.
 *
 * @author arolin
 */
public final class CollectionHelper {

    /**
     * utility class
     */
    private CollectionHelper() {
        // do nothing
    }

    /**
     * Renvoie une {@link List} triée à partir d'une Collection dans l'ordre croissant selon l'ordre naturel de ses éléments.
     * @param collection la collection à trier
     * @param <T> le type des éléments dans la collection qui doit implémenter l'interface {@link Comparable}
     * @return une liste triée à partir d'une Collection dans l'ordre croissant selon l'ordre naturel de ses éléments
     */
    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> collection) {
        List<T> list = new ArrayList<>(collection);
        Collections.sort(list);
        return list;
    }

    /**
     * Renvoie une {@link List} triée à partir d'une Collection en utilisant le comparateur donné.
     * @param collection la collection à trier
     * @param comparator le comparateur utilisé pour trier les éléments de la liste
     * @param <T> le type des éléments dans la collection
     * @param <U> le type du comparateur qui doit implémenter l'interface {@link Comparator}
     * @return une liste triée à partir d'une Collection en utilisant le comparateur donné
     */
    public static <T, U extends Comparator<T>> List<T> asSortedList(Collection<T> collection, U comparator) {
        List<T> list = new ArrayList<>(collection);
        list.sort(comparator);
        return list;
    }
}
