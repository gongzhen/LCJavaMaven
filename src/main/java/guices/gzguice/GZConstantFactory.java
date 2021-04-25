package guices.gzguice;

import com.google.inject.internal.*;
import com.google.inject.internal.util.ToStringBuilder;
import com.google.inject.spi.Dependency;

final class GZConstantFactory<T> implements GZInternalFactory<T> {
    private final GZInitializable<T> initializable;

    public GZConstantFactory(GZInitializable<T> initializable) {
        this.initializable = initializable;
    }

    public T get(Errors errors, GZInternalContext context, Dependency dependency, boolean linked)
            throws ErrorsException {
        return initializable.get(errors);
    }

    public String toString() {
        return new ToStringBuilder(GZConstantFactory.class)
                .add("value", initializable)
                .toString();
    }
}
