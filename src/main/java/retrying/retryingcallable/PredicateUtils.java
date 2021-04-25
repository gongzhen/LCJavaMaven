package retrying.retryingcallable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateUtils {
    /**
     * Constructs a new, more generic {@link Predicate<Throwable>} which will either delegate to the
     * supplied {@link Predicate<T>} when the input {@link Throwable} is an instance of the supplied
     * {@link Class<T>} or will immediately return false when the input {@link Throwable} is NOT
     * an instance of the supplied {@link Class<T>}.
     *
     * @param clazz any inputs supplied to the returned predicate which are an instance of clazz
     *              will be passed along to the delegate, otherwise false will be returned
     *              without calling the delegate.
     * @param delegate delegate to use if and only if the input is an instance of the first arg.
     * @param <T> The type of {@link Throwable} which the supplied {@link Predicate<T>} can handle.
     * @return a new, more generic {@link Predicate<Throwable>} which will either delegate to the
     * supplied {@link Predicate<T>} when the input {@link Throwable} is an instance of the supplied
     * {@link Class<T>} or will immediately return false when the input {@link Throwable} is NOT
     * an instance of the supplied {@link Class<T>}.
     */
    public static <T extends Throwable> Predicate<Throwable> generify(
            final Class<T> clazz,
            Predicate<T> delegate) {
        return new TypedDelegatingPredicate<>(clazz,delegate);
    }

    /**
     * Constructs a new, more generic {@link Predicate<Throwable>} exactly like
     * {@link PredicateUtils#generify(Class, Predicate)}, but deduces the type information
     * from the delegate when possible and throws {@link IllegalArgumentException} when not
     * possible (because of type-erasure issues).
     *
     * As of JDK8, there are 4 ways to create predicates. Two work with this method, and two do not.
     * <ul>
     *     <li>OK: Predicate creation via strongly class implementing <pre>Predicate<MyException></pre></li>
     *     <li>OK: Predicate creation via anonymous inner class implementing <pre>Predicate<MyException></pre></li>
     *     <li>NOT OK: Predicate creation via Lambda Function</li>
     *     <li>NOT OK: Predicate creation via non-static method reference</li>
     * </ul>
     *
     * @see PredicateUtils#generify(Class, Predicate)
     * @param delegate delegate to use if and only if the input {@link Throwable} is an instance of
     *                 the type matching the delegate predicate implementation.
     * @param <T> The type of {@link Throwable} which the given {@link Predicate} can handle.
     * @return a new, more generic {@link Predicate<Throwable>} which will either delegate to the
     * supplied predicate if the type matches, or will immediately return false when the input
     * {@link Throwable} type does not match the implmenting predicate.
     */
    public static <T extends Throwable> Predicate<Throwable> generify(Predicate<T> delegate) {
        String errorFormat = "The Throwable type could not be determined for the given predicate. "
                + "Consider using wrap(final Class<T> clazz, Predicate<T> predicate)... "
                + "(predicate=%s)";
        Type iface = delegate.getClass().getGenericInterfaces()[0];
        if (iface instanceof Class) {
            throw new IllegalArgumentException(String.format(errorFormat,delegate));
        }
        Type type = ((ParameterizedType)iface).getActualTypeArguments()[0];
        try {
            return generify((Class<T>)Class.forName(type.getTypeName()),delegate);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format(errorFormat,delegate));
        }
    }

    public static <T extends Throwable> Predicate<Throwable> isInstance(Class<T> clazz) {
        return new InstanceOf(clazz);
    }

    /**
     * Converts the supplied var-arg array of {@link Throwable} classes into {@link InstanceOf}
     * predicates. There will be one {@link InstanceOf} in the resulting collection per supplied
     * class.
     * @param throwableClasses to be convert into {@link InstanceOf} representations.
     * @return a {@link Collection} of {@link InstanceOf} instances representing each supplied
     * {@link Throwable} class.
     */
    public static Collection<Predicate<Throwable>> isInstanceFilters(Class<? extends Throwable>...throwableClasses) {
        return isInstanceFilters(Arrays.asList(throwableClasses));
    }

    /**
     * Converts the supplied collection of {@link Throwable} classes into {@link InstanceOf}
     * predicates. There will be one {@link InstanceOf} in the resulting collection per supplied
     * class.
     * @param throwableClasses to be convert into {@link InstanceOf} representations.
     * @return a {@link Collection} of {@link InstanceOf} instances representing each supplied
     * {@link Throwable} class.
     */
    public static Collection<Predicate<Throwable>> isInstanceFilters(Collection<Class<? extends Throwable>> throwableClasses) {
        return throwableClasses
                .stream()
                .map(t -> new InstanceOf<>(t))
                .collect(Collectors.toList());
    }

    /**
     * Wraps the supplied array of {@link Throwable} classes with {@link InstanceOf} instances, then
     * merges them with new instances of each of the supplied array of {@link Predicate<Throwable>}
     * classes.
     *
     * The intent of this function is to produce runtime-instances of predicates required by
     * RetryHelpersLib from annotation constants (arrays of classes supplied via annotations and
     * used by builders)
     *
     * NOTE: There is a special case where {@link Any} will be automatically removed if another
     * throwable OR predicate is supplied to either arg. This special case occurs because the
     * default behavior is to retry on any Exception, but if the library consumer scopes-down to
     * a more specific set of exceptions, then {@link Any} would scope-back-up to include all
     * exceptions in retry attempts.
     *
     * @param throwables which will be converted to {@link InstanceOf} instances prior to merge
     * @param predicates which will be instantiated and then merged with the {@link InstanceOf}
     *                instances
     * @return a merged collection of predicates produced by combining the instances created from
     * the two input collections.
     */
    public static Collection<Predicate<Throwable>> wrapAndMerge(
            Class<? extends Throwable>[] throwables,
            Class<Predicate<Throwable>>[] predicates) {
        List<Predicate<Throwable>> merged = new ArrayList<>(throwables.length+predicates.length);
        for (Class<? extends Throwable> throwable : throwables) {
            merged.add(new InstanceOf(throwable));
        }
        boolean anyExceptionIncluded = false;
        for (Class<Predicate<Throwable>> predicateClass : predicates) {
            try {
                if (predicateClass.equals(Any.class)) {
                    anyExceptionIncluded = true;
                    // Do not instantiate, 'Any' will be included below if there are no other filters.
                } else {
                    merged.add(predicateClass.newInstance());
                }
            } catch (Exception e) {
                throw new IllegalStateException("Unable to instantiate Predicate: " + predicateClass, e);
            }
        }
        if (anyExceptionIncluded && merged.isEmpty()) {
            // Only add the default filter if there are no other filters specified.
            merged.add(Any.INSTANCE);
        }
        return merged;
    }
}
