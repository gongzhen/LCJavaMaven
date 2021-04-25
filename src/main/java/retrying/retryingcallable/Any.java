package retrying.retryingcallable;

import java.util.function.Predicate;

/**
 * {@link Predicate<Throwable>} implementation which returns true for all {@link Throwable}s which
 * are instances of {@link Exception} or any subclass.
 */
public final class Any implements Predicate<Throwable> {

    public static final Any INSTANCE = new Any();

    /**
     * There should only be one instance of this.
     */
    private Any() {}

    @Override
    public boolean test(Throwable throwable) {
        return throwable instanceof Exception;
    }

    @Override
    public int hashCode() {
        return Any.class.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof Any;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName();
    }
}
