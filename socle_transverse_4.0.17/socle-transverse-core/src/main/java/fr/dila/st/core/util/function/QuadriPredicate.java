package fr.dila.st.core.util.function;

/**
 * Extension de l'interface fonctionelle {@link java.util.function.Predicate} à 4 paramètres en entrée
 *
 * @see FourConsumer
 * @author tlombard
 *
 */
@FunctionalInterface
public interface QuadriPredicate<T, U, V, W> {
    boolean test(T t, U u, V v, W w);
}
