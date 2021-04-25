package lambda;

import java.util.Objects;

// https://www.cnblogs.com/webor2006/p/8204591.html
@FunctionalInterface
public interface GZFunction<T, R> {
    R apply(T t);

    default <V> GZFunction<V, R> compose(GZFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        // 1: before.apply(v)
        // 2: apply(before.apply(v))
        return (V v) -> apply(before.apply(v));
    }

    default <V> GZFunction<T, V>andThen(GZFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        // 1: apply(v)
        // 2: after.apply(apply(v))
        return (T v) -> after.apply(apply(v));
    }

    static <T> GZFunction<T, T> identity() {
        return t -> t;
    }
}
