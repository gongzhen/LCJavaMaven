package lambda;

import java.util.Objects;

@FunctionalInterface
public interface GZBiFuction<T, U, R> {
    R apply(T t, U u);

    default <V> GZBiFuction<T, U, V> andThen(GZFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        // 1: apply t and u and get result.
        // 2: function will apply the result from biFunction.
        return (T t, U u) -> after.apply(apply(t, u));
    }
}
