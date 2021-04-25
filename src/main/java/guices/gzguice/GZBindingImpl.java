package guices.gzguice;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.Scoping;
import com.google.inject.internal.util.ToStringBuilder;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.InstanceBinding;

public abstract class GZBindingImpl<T> implements Binding<T> {
    private final GZInjectorImpl injector;
    private final Key<T> key;
    private final Object source;
    private final Scoping scoping;
    private final GZInternalFactory<? extends T> internalFactory;

    public GZBindingImpl(GZInjectorImpl injector, Key<T> key, Object source,
                       GZInternalFactory<? extends T> internalFactory, Scoping scoping) {
        this.injector = injector;
        this.key = key;
        this.source = source;
        this.internalFactory = internalFactory;
        this.scoping = scoping;
    }

    protected GZBindingImpl(Object source, Key<T> key, Scoping scoping) {
        this.internalFactory = null;
        this.injector = null;
        this.source = source;
        this.key = key;
        this.scoping = scoping;
    }

    public Key<T> getKey() {
        return key;
    }

    public Object getSource() {
        return source;
    }

    private volatile Provider<T> provider;

    public Provider<T> getProvider() {
        if (provider == null) {
            if (injector == null) {
                throw new UnsupportedOperationException("getProvider() not supported for module bindings");
            }

//            provider = injector.getProvider(key);
        }
        return provider;
    }

    public GZInternalFactory<? extends T> getInternalFactory() {
        return internalFactory;
    }

    public Scoping getScoping() {
        return scoping;
    }

    /**
     * Is this a constant binding? This returns true for constant bindings as
     * well as toInstance() bindings.
     */
    public boolean isConstant() {
        return this instanceof InstanceBinding;
    }

    public <V> V acceptVisitor(ElementVisitor<V> visitor) {
        return visitor.visit(this);
    }

    public <V> V acceptScopingVisitor(BindingScopingVisitor<V> visitor) {
        return scoping.acceptVisitor(visitor);
    }

    protected BindingImpl<T> withScoping(Scoping scoping) {
        throw new AssertionError();
    }

    protected BindingImpl<T> withKey(Key<T> key) {
        throw new AssertionError();
    }

    @Override public String toString() {
        return new ToStringBuilder(Binding.class)
                .add("key", key)
                .add("scope", scoping)
                .add("source", source)
                .toString();
    }

    public GZInjectorImpl getInjector() {
        return injector;
    }
}
