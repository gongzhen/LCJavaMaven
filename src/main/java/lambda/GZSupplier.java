package lambda;

@FunctionalInterface
public interface GZSupplier<T> {

    T get();
}
