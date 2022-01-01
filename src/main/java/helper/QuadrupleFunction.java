package helper;

import java.util.Objects;
import java.util.function.Function;

public interface QuadrupleFunction<A, B, C, D, R> {
    R apply(A a, B b, C c, D d);

    default <V> QuadrupleFunction<A, B, C, D, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (a, b, c, d) -> {
            return after.apply(this.apply(a, b, c, d));
        };
    }
}
