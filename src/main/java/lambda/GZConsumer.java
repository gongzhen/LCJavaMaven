package lambda;

@FunctionalInterface
public interface GZConsumer<T> {
    void accept(T t);
}
