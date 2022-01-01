package guices.gzguice;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.Errors;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

interface GZState {
    static final GZState NONE = new GZState() {
        public GZState parent() {
            throw new UnsupportedOperationException();
        }

        public <T> BindingImpl<T> getExplicitBinding(Key<T> key) {
            return null;
        }

        public Map<Key<?>, Binding<?>> getExplicitBindingsThisLevel() {
            throw new UnsupportedOperationException();
        }

        public void putBinding(Key<?> key, BindingImpl<?> binding) {
            throw new UnsupportedOperationException();
        }

        public Scope getScope(Class<? extends Annotation> scopingAnnotation) {
            return null;
        }

        public void putAnnotation(Class<? extends Annotation> annotationType, Scope scope) {
            throw new UnsupportedOperationException();
        }

        public void addConverter(TypeConverterBinding typeConverterBinding) {
            throw new UnsupportedOperationException();
        }

        public TypeConverterBinding getConverter(String stringValue, TypeLiteral<?> type, Errors errors,
                                                 Object source) {
            throw new UnsupportedOperationException();
        }

        public List<TypeConverterBinding> getConvertersThisLevel() {
            return ImmutableList.of();
        }

        /*if[AOP]*/
        public void addMethodAspect(GZMethodAspect methodAspect) {
            throw new UnsupportedOperationException();
        }

        public ImmutableList<GZMethodAspect> getMethodAspects() {
            return ImmutableList.of();
        }
        /*end[AOP]*/

        public void addTypeListener(TypeListenerBinding typeListenerBinding) {
            throw new UnsupportedOperationException();
        }

        public List<TypeListenerBinding> getTypeListenerBindings() {
            return ImmutableList.of();
        }

        public void blacklist(Key<?> key) {
        }

        public boolean isBlacklisted(Key<?> key) {
            return true;
        }

        public Object lock() {
            throw new UnsupportedOperationException();
        }

        public Map<Class<? extends Annotation>, Scope> getScopes() {
            return ImmutableMap.of();
        }
    };

    GZState parent();

    /** Gets a binding which was specified explicitly in a module, or null. */
    <T> BindingImpl<T> getExplicitBinding(Key<T> key);

    /** Returns the explicit bindings at this level only. */
    Map<Key<?>, Binding<?>> getExplicitBindingsThisLevel();

    void putBinding(Key<?> key, BindingImpl<?> binding);

    /** Returns the matching scope, or null. */
    Scope getScope(Class<? extends Annotation> scopingAnnotation);

    void putAnnotation(Class<? extends Annotation> annotationType, Scope scope);

    void addConverter(TypeConverterBinding typeConverterBinding);

    /** Returns the matching converter for {@code type}, or null if none match. */
    TypeConverterBinding getConverter(
            String stringValue, TypeLiteral<?> type, Errors errors, Object source);

    /** Returns all converters at this level only. */
    List<TypeConverterBinding> getConvertersThisLevel();

    /*if[AOP]*/
    void addMethodAspect(GZMethodAspect methodAspect);

    ImmutableList<GZMethodAspect> getMethodAspects();
    /*end[AOP]*/

    void addTypeListener(TypeListenerBinding typeListenerBinding);

    List<TypeListenerBinding> getTypeListenerBindings();

    /**
     * Forbids the corresponding injector from creating a binding to {@code key}. Child injectors
     * blacklist their bound keys on their parent injectors to prevent just-in-time bindings on the
     * parent injector that would conflict.
     */
    void blacklist(Key<?> key);

    /**
     * Returns true if {@code key} is forbidden from being bound in this injector. This indicates that
     * one of this injector's descendent's has bound the key.
     */
    boolean isBlacklisted(Key<?> key);

    /**
     * Returns the shared lock for all injector data. This is a low-granularity, high-contention lock
     * to be used when reading mutable data (ie. just-in-time bindings, and binding blacklists).
     */
    Object lock();

    /**
     * Returns all the scope bindings at this level and parent levels.
     */
    Map<Class<? extends Annotation>, Scope> getScopes();
}
