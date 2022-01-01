package helper;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Set;

public abstract class GZAbstractSet<E> extends AbstractCollection<E> implements Set<E> {
    protected GZAbstractSet() {}

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Set)) {
            return false;
        }

        Collection<?> c = (Collection<?>) o;
        if (c.size() != size()) {
            return false;
        }

        try {
            return containsAll(c);
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return super.removeAll(c);
    }
}
