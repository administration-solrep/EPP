package fr.dila.st.core.util;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;
import org.apache.commons.lang3.ObjectUtils;

public class ObjectHelper {

    private ObjectHelper() {
        // Utility class
    }

    /**
     * Returns the first argument if it is non-{@code null} and
     * otherwise returns the non-{@code null} second argument.
     *
     * @param obj an object
     * @param defaultObj a non-{@code null} object to return if the first argument
     *                   is {@code null}
     * @param <T> the type of the reference
     * @return the first argument if it is non-{@code null} and
     *        otherwise the second argument if it is non-{@code null}
     * @throws NullPointerException if both {@code obj} is null and
     *        {@code defaultObj} is {@code null}
     * @since 9
     *         Disponible dans java 9
     */
    public static <T> T requireNonNullElse(T obj, T defaultObj) {
        return (obj != null) ? obj : requireNonNull(defaultObj, "defaultObj");
    }

    /**
     * Returns the first argument if it is non-{@code null} and otherwise
     * returns the non-{@code null} value of {@code supplier.get()}.
     *
     * @param obj
     *            an object
     * @param supplier
     *            of a non-{@code null} object to return if the first argument
     *            is {@code null}
     * @param <T>
     *            the type of the first argument and return type
     * @return the first argument if it is non-{@code null} and otherwise the
     *         value from {@code supplier.get()} if it is non-{@code null}
     * @throws NullPointerException
     *             if both {@code obj} is null and either the {@code supplier}
     *             is {@code null} or the {@code supplier.get()} value is
     *             {@code null}
     * @since 9
     *
     *        Disponible dans java 9
     */
    public static <T> T requireNonNullElseGet(T obj, Supplier<? extends T> supplier) {
        return (obj != null) ? obj : requireNonNull(requireNonNull(supplier, "supplier").get(), "supplier.get()");
    }

    /**
     * Checks if all values in the given array are {@code null}.
     *
     * <p>
     * If all the values are {@code null} or the array is {@code null}
     * or empty, then {@code true} is returned, otherwise {@code false} is returned.
     * </p>
     *
     * <pre>
     * ObjectUtils.allNull(*)                = false
     * ObjectUtils.allNull(*, null)          = false
     * ObjectUtils.allNull(null, *)          = false
     * ObjectUtils.allNull(null, null, *, *) = false
     * ObjectUtils.allNull(null)             = true
     * ObjectUtils.allNull(null, null)       = true
     * </pre>
     *
     * @param values  the values to test, may be {@code null} or empty
     * @return {@code true} if all values in the array are {@code null}s,
     * {@code false} if there is at least one non-null value in the array.
     * @since 3.11
     */
    public static boolean allNull(final Object... values) {
        return !ObjectUtils.anyNotNull(values);
    }
}
