package retrying.retryingcallable;

import java.util.Objects;
import java.util.function.Predicate;

public class TypedDelegatingPredicate <T extends Throwable> implements Predicate<Throwable> {
    private Class<T> clazz;
    private Predicate<T> delegate;

    /**
     * @param clazz The type matching the supplied delegate {@link Predicate<T>} implementation.
     * @param delegate The delegate to call when the runtime-supplied {@link Throwable} is an
     *                 instance of type clazz ({@link T}).
     */
    public TypedDelegatingPredicate(Class<T> clazz, Predicate<T> delegate) {
        this.clazz = clazz;
        this.delegate = delegate;
    }

    @Override
    public boolean test(Throwable throwable) {
        return clazz.isAssignableFrom(throwable.getClass())
                && delegate.test(clazz.cast(throwable));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clazz,this.delegate);
    }

    public boolean equals(Object other) {
        if (!(other instanceof TypedDelegatingPredicate)) {
            return false;
        }
        TypedDelegatingPredicate that = (TypedDelegatingPredicate) other;
        return this.clazz.equals(that.clazz)
                && this.delegate.equals(that.delegate);
    }

    public String toString() {
        return "TypedDelegatingPredicate[clazz="+clazz.getName()+",delegate="+delegate+"]";
    }
}
