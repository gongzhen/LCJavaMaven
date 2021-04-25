package lambda;

import java.util.Objects;

@FunctionalInterface
public interface GZPredicate<T> {

    boolean test(T t);

    default GZPredicate<T> and(GZPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    default GZPredicate<T> negate() {
        return (t) -> !test(t);
    }

    default GZPredicate<T> or(GZPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }

    static <T> GZPredicate<T> isEqual(Object targetRef) {
        return (null == targetRef) ? Objects::isNull : object -> targetRef.equals(object);
    }
 }
