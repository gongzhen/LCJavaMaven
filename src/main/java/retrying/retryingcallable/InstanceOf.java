package retrying.retryingcallable;

import java.util.function.Predicate;

public class InstanceOf <T extends Throwable> implements Predicate<Throwable> {

    private final Class<T> clazz;

    /**
     * @param clazz Any {@link Throwable} instances (or subclass instances) of this class will yield
     * true when passed to {@link InstanceOf#test(Throwable)}, and false otherwise.
     */
    public InstanceOf(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean test(Throwable throwable) {
        return clazz.isAssignableFrom(throwable.getClass());
    }

    public int hashCode() {
        return clazz.hashCode();
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof InstanceOf
                && this.clazz.equals(((InstanceOf) that).clazz);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "["+clazz.getCanonicalName()+"]";
    }
}

