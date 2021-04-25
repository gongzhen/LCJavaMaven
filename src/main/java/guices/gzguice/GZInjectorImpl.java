package guices.gzguice;

import com.google.inject.*;
import com.google.inject.Module;
import com.google.inject.internal.*;
import com.google.inject.internal.util.*;
import com.google.inject.internal.util.Objects;
import com.google.inject.spi.*;
import com.google.inject.util.Providers;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

final class GZInjectorImpl implements GZInjector {

    public static final TypeLiteral<String> STRING_TYPE = TypeLiteral.get(String.class);

    @Override
    public void injectMembers(Object instance) {

    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return null;
    }

    /** some limitations on what just in time bindings are allowed. */
    enum JitLimitation {
        /** does not allow just in time bindings */
        NO_JIT,
        /** allows existing just in time bindings, but does not allow new ones */
        EXISTING_JIT,
        /** allows existing just in time bindings & allows new ones to be created */
        NEW_OR_EXISTING_JIT,
    }

    final GZState state;
    final GZInjectorImpl parent;
    final GZInjectorImpl.BindingsMultimap bindingsMultimap = new GZInjectorImpl.BindingsMultimap();
    final GZInternalInjectorCreator.GZInjectorOptions options;

    /** Just-in-time binding cache. Guarded by state.lock() */
    final Map<Key<?>, BindingImpl<?>> jitBindings = Maps.newHashMap();

    GZLookups lookups = new GZDeferredLookups(this);

    GZInjectorImpl(@Nullable GZInjectorImpl parent, GZState state, GZInternalInjectorCreator.GZInjectorOptions injectorOptions) {
        this.parent = parent;
        this.state = state;
        this.options = injectorOptions;

        if (parent != null) {
            localContext = parent.localContext;
        } else {
            localContext = new ThreadLocal<Object[]>() {
                protected Object[] initialValue() {
                    return new Object[1];
                }
            };
        }
    }

    /** Indexes bindings by type. */
    void index() {
        for (Binding<?> binding : state.getExplicitBindingsThisLevel().values()) {
            index(binding);
        }
    }

    <T> void index(Binding<T> binding) {
        bindingsMultimap.put(binding.getKey().getTypeLiteral(), binding);
    }

    public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> type) {
        return bindingsMultimap.getAll(type);
    }

    /** Returns the binding for {@code key} */
    public <T> BindingImpl<T> getBinding(Key<T> key) {
        Errors errors = new Errors(key);
        try {
            BindingImpl<T> result = getBindingOrThrow(key, errors, GZInjectorImpl.JitLimitation.EXISTING_JIT);
            errors.throwConfigurationExceptionIfErrorsExist();
            return result;
        } catch (ErrorsException e) {
            throw new ConfigurationException(errors.merge(e.getErrors()).getMessages());
        }
    }

    public <T> BindingImpl<T> getExistingBinding(Key<T> key) {
        // Check explicit bindings, i.e. bindings created by modules.
        BindingImpl<T> explicitBinding = state.getExplicitBinding(key);
        if (explicitBinding != null) {
            return explicitBinding;
        }
        synchronized (state.lock()) {
            // See if any jit bindings have been created for this key.
            for (GZInjectorImpl injector = this; injector != null; injector = injector.parent) {
                @SuppressWarnings("unchecked")
                BindingImpl<T> jitBinding = (BindingImpl<T>) injector.jitBindings.get(key);
                if(jitBinding != null) {
                    return jitBinding;
                }
            }
        }

        // If Key is a Provider, we have to see if the type it is providing exists,
        // and, if so, we have to create the binding for the provider.
        if(isProvider(key)) {
            try {
                // This is safe because isProvider above ensures that T is a Provider<?>
                @SuppressWarnings("unchecked")
                Key<?> providedKey = (Key<?>)getProvidedKey((Key)key, new Errors());
                if(getExistingBinding(providedKey) != null) {
                    return getBinding(key);
                }
            } catch(ErrorsException e) {
                throw new ConfigurationException(e.getErrors().getMessages());
            }
        }

        // No existing binding exists.
        return null;
    }

    /**
     * Gets a binding implementation.  First, it check to see if the parent has a binding.  If the
     * parent has a binding and the binding is scoped, it will use that binding.  Otherwise, this
     * checks for an explicit binding. If no explicit binding is found, it looks for a just-in-time
     * binding.
     */
    <T> BindingImpl<T> getBindingOrThrow(Key<T> key, Errors errors, GZInjectorImpl.JitLimitation jitType)
            throws ErrorsException {
        // Check explicit bindings, i.e. bindings created by modules.
        BindingImpl<T> binding = state.getExplicitBinding(key);
        if (binding != null) {
            return binding;
        }

        // Look for an on-demand binding.
        return getJustInTimeBinding(key, errors, jitType);
    }

    public <T> Binding<T> getBinding(Class<T> type) {
        return getBinding(Key.get(type));
    }

    public GZInjector getParent() {
        return parent;
    }

    public Injector createChildInjector(Iterable<? extends GZModule> modules) {
        return new GZInternalInjectorCreator()
                .parentInjector(this)
                .addModules(modules)
                .build();
    }

    public Injector createChildInjector(GZModule... modules) {
        return createChildInjector(ImmutableList.of(modules));
    }

    /**
     * Returns a just-in-time binding for {@code key}, creating it if necessary.
     *
     * @throws ErrorsException if the binding could not be created.
     */
    private <T> BindingImpl<T> getJustInTimeBinding(Key<T> key, Errors errors, GZInjectorImpl.JitLimitation jitType)
            throws ErrorsException {


        boolean jitOverride = isProvider(key) || isTypeLiteral(key);
        if(options.jitDisabled && jitType == GZInjectorImpl.JitLimitation.NO_JIT && !jitOverride) {
            throw errors.jitDisabled(key).toException();
        }

        synchronized (state.lock()) {
            // first try to find a JIT binding that we've already created
            for (GZInjectorImpl injector = this; injector != null; injector = injector.parent) {
                @SuppressWarnings("unchecked") // we only store bindings that match their key
                        BindingImpl<T> binding = (BindingImpl<T>) injector.jitBindings.get(key);

                if (binding != null) {
                    return binding;
                }
            }

            if(options.jitDisabled && jitType != GZInjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT && !jitOverride) {
                throw errors.jitDisabled(key).toException();
            } else {
//                return createJustInTimeBindingRecursive(key, errors);
                return null;
            }
        }
    }

    /** Returns true if the key type is Provider (but not a subclass of Provider). */
    private static boolean isProvider(Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(Provider.class);
    }

    private static boolean isTypeLiteral(Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(TypeLiteral.class);
    }

    private static <T> Key<T> getProvidedKey(Key<Provider<T>> key, Errors errors) throws ErrorsException {
        Type providerType = key.getTypeLiteral().getType();

        // If the Provider has no type parameter (raw Provider)...
        if (!(providerType instanceof ParameterizedType)) {
            throw errors.cannotInjectRawProvider().toException();
        }

        Type entryType = ((ParameterizedType) providerType).getActualTypeArguments()[0];

        @SuppressWarnings("unchecked") // safe because T came from Key<Provider<T>>
                Key<T> providedKey = (Key<T>) key.ofType(entryType);
        return providedKey;
    }

    /** Returns true if the key type is MembersInjector (but not a subclass of MembersInjector). */
    private static boolean isMembersInjector(Key<?> key) {
        return key.getTypeLiteral().getRawType().equals(MembersInjector.class)
                && !(key.getAnnotationType() != null);
    }

//    private <T> BindingImpl<MembersInjector<T>> createMembersInjectorBinding(
//            Key<MembersInjector<T>> key, Errors errors) throws ErrorsException {
//        Type membersInjectorType = key.getTypeLiteral().getType();
//        if (!(membersInjectorType instanceof ParameterizedType)) {
//            throw errors.cannotInjectRawMembersInjector().toException();
//        }
//
//        @SuppressWarnings("unchecked") // safe because T came from Key<MembersInjector<T>>
//                TypeLiteral<T> instanceType = (TypeLiteral<T>) TypeLiteral.get(
//                ((ParameterizedType) membersInjectorType).getActualTypeArguments()[0]);
//        MembersInjector<T> membersInjector = membersInjectorStore.get(instanceType, errors);
//
//        GZInternalFactory<MembersInjector<T>> factory = new GZConstantFactory<MembersInjector<T>>(
//                Initializables.of(membersInjector));
//
//
//        return new InstanceBindingImpl<MembersInjector<T>>(this, key, SourceProvider.UNKNOWN_SOURCE,
//                factory, ImmutableSet.<InjectionPoint>of(), membersInjector);
//    }

    /**
     * Creates a synthetic binding to {@code Provider<T>}, i.e. a binding to the provider from
     * {@code Binding<T>}.
     */
    private <T> GZBindingImpl<Provider<T>> createProviderBinding(Key<Provider<T>> key, Errors errors)
            throws ErrorsException {
        Key<T> providedKey = getProvidedKey(key, errors);
        BindingImpl<T> delegate = getBindingOrThrow(providedKey, errors, GZInjectorImpl.JitLimitation.NO_JIT);
        return new GZInjectorImpl.ProviderBindingImpl<T>(this, key, delegate);
    }

    private static class ProviderBindingImpl<T> extends GZBindingImpl<Provider<T>>
            implements ProviderBinding<Provider<T>>, HasDependencies {
        final BindingImpl<T> providedBinding;

        ProviderBindingImpl(GZInjectorImpl injector, Key<Provider<T>> key, Binding<T> providedBinding) {
            super(injector, key, providedBinding.getSource(), createInternalFactory(providedBinding),
                    Scoping.UNSCOPED);
            this.providedBinding = (BindingImpl<T>) providedBinding;
        }

        static <T> GZInternalFactory<Provider<T>> createInternalFactory(Binding<T> providedBinding) {
            final Provider<T> provider = providedBinding.getProvider();
            return new GZInternalFactory<Provider<T>>() {
                public Provider<T> get(Errors errors, GZInternalContext context, Dependency dependency, boolean linked) {
                    return provider;
                }
            };
        }

        public Key<? extends T> getProvidedKey() {
            return providedBinding.getKey();
        }

        public <V> V acceptTargetVisitor(BindingTargetVisitor<? super Provider<T>, V> visitor) {
            return visitor.visit(this);
        }

        public void applyTo(Binder binder) {
            throw new UnsupportedOperationException("This element represents a synthetic binding.");
        }

        @Override public String toString() {
            return new ToStringBuilder(ProviderBinding.class)
                    .add("key", getKey())
                    .add("providedKey", getProvidedKey())
                    .toString();
        }

        public Set<Dependency<?>> getDependencies() {
            return ImmutableSet.<Dependency<?>>of(Dependency.get(getProvidedKey()));
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof GZInjectorImpl.ProviderBindingImpl) {
                GZInjectorImpl.ProviderBindingImpl<?> o = (GZInjectorImpl.ProviderBindingImpl<?>)obj;
                return getKey().equals(o.getKey())
                        && getScoping().equals(o.getScoping())
                        && Objects.equal(providedBinding, o.providedBinding);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getKey(), getScoping(), providedBinding);
        }
    }

    /**
     * Converts a constant string binding to the required type.
     *
     * @return the binding if it could be resolved, or null if the binding doesn't exist
     * @throws com.google.inject.internal.ErrorsException if there was an error resolving the binding
     */
    private <T> GZBindingImpl<T> convertConstantStringBinding(Key<T> key, Errors errors)
            throws ErrorsException {
        // Find a constant string binding.
        Key<String> stringKey = key.ofType(STRING_TYPE);
        BindingImpl<String> stringBinding = state.getExplicitBinding(stringKey);
        if (stringBinding == null || !stringBinding.isConstant()) {
            return null;
        }

        String stringValue = stringBinding.getProvider().get();
        Object source = stringBinding.getSource();

        // Find a matching type converter.
        TypeLiteral<T> type = key.getTypeLiteral();
        TypeConverterBinding typeConverterBinding = state.getConverter(stringValue, type, errors, source);

        if (typeConverterBinding == null) {
            // No converter can handle the given type.
            return null;
        }

        // Try to convert the string. A failed conversion results in an error.
        try {
            @SuppressWarnings("unchecked") // This cast is safe because we double check below.
                    T converted = (T) typeConverterBinding.getTypeConverter().convert(stringValue, type);

            if (converted == null) {
                throw errors.converterReturnedNull(stringValue, source, type, typeConverterBinding)
                        .toException();
            }

            if (!type.getRawType().isInstance(converted)) {
                throw errors.conversionTypeError(stringValue, source, type, typeConverterBinding, converted)
                        .toException();
            }

            return new GZInjectorImpl.ConvertedConstantBindingImpl<T>(this, key, converted, stringBinding,
                    typeConverterBinding);
        } catch (ErrorsException e) {
            throw e;
        } catch (RuntimeException e) {
            throw errors.conversionError(stringValue, source, type, typeConverterBinding, e)
                    .toException();
        }
    }

    private static class ConvertedConstantBindingImpl<T>
            extends GZBindingImpl<T> implements ConvertedConstantBinding<T> {
        final T value;
        final Provider<T> provider;
        final Binding<String> originalBinding;
        final TypeConverterBinding typeConverterBinding;

        ConvertedConstantBindingImpl(
                GZInjectorImpl injector, Key<T> key, T value, Binding<String> originalBinding,
                TypeConverterBinding typeConverterBinding) {
            super(injector, key, originalBinding.getSource(),
                    new GZConstantFactory<T>(GZInitializables.of(value)), Scoping.UNSCOPED);
            this.value = value;
            provider = Providers.of(value);
            this.originalBinding = originalBinding;
            this.typeConverterBinding = typeConverterBinding;
        }

        @Override public Provider<T> getProvider() {
            return provider;
        }

        public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
            return visitor.visit(this);
        }

        public T getValue() {
            return value;
        }

        public TypeConverterBinding getTypeConverterBinding() {
            return typeConverterBinding;
        }

        public Key<String> getSourceKey() {
            return originalBinding.getKey();
        }

        public Set<Dependency<?>> getDependencies() {
            return ImmutableSet.<Dependency<?>>of(Dependency.get(getSourceKey()));
        }

        public void applyTo(Binder binder) {
            throw new UnsupportedOperationException("This element represents a synthetic binding.");
        }

        @Override public String toString() {
            return new ToStringBuilder(ConvertedConstantBinding.class)
                    .add("key", getKey())
                    .add("sourceKey", getSourceKey())
                    .add("value", value)
                    .toString();
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof GZInjectorImpl.ConvertedConstantBindingImpl) {
                GZInjectorImpl.ConvertedConstantBindingImpl<?> o = (GZInjectorImpl.ConvertedConstantBindingImpl<?>)obj;
                return getKey().equals(o.getKey())
                        && getScoping().equals(o.getScoping())
                        && Objects.equal(value, o.value);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getKey(), getScoping(), value);
        }
    }

//    <T> void initializeBinding(BindingImpl<T> binding, Errors errors) throws ErrorsException {
//        if (binding instanceof ConstructorBindingImpl<?>) {
//            ((ConstructorBindingImpl) binding).initialize(this, errors);
//        }
//    }

//    <T> void initializeJitBinding(BindingImpl<T> binding, Errors errors) throws ErrorsException {
//        // Put the partially constructed binding in the map a little early. This enables us to handle
//        // circular dependencies. Example: FooImpl -> BarImpl -> FooImpl.
//        // Note: We don't need to synchronize on state.lock() during injector creation.
//        if (binding instanceof ConstructorBindingImpl<?>) {
//            Key<T> key = binding.getKey();
//            jitBindings.put(key, binding);
//            boolean successful = false;
//            ConstructorBindingImpl cb = (ConstructorBindingImpl)binding;
//            try {
//                cb.initialize(this, errors);
//                successful = true;
//            } finally {
//                if (!successful) {
//                    // We do not pass cb.getInternalConstructor as the second parameter
//                    // so that cached exceptions while constructing it get stored.
//                    // See TypeListenerTest#testTypeListenerThrows
//                    removeFailedJitBinding(key, null);
//                    cleanup(binding, new HashSet<Key>());
//                }
//            }
//        }
//    }

    /**
     * Iterates through the binding's dependencies to clean up any stray bindings that were leftover
     * from a failed JIT binding. This is required because the bindings are eagerly &
     * optimistically added to allow circular dependency support, so dependencies may pass where they
     * should have failed.
     */
//    private boolean cleanup(BindingImpl<?> binding, Set<Key> encountered) {
//        boolean bindingFailed = false;
//        Set<Dependency<?>> deps = getInternalDependencies(binding);
//        for(Dependency dep : deps) {
//            Key<?> depKey = dep.getKey();
//            InjectionPoint ip = dep.getInjectionPoint();
//            if(encountered.add(depKey)) { // only check if we haven't looked at this key yet
//                BindingImpl depBinding = jitBindings.get(depKey);
//                if(depBinding != null) { // if the binding still exists, validate
//                    boolean failed = cleanup(depBinding, encountered); // if children fail, we fail
//                    if(depBinding instanceof ConstructorBindingImpl) {
//                        ConstructorBindingImpl ctorBinding = (ConstructorBindingImpl)depBinding;
//                        ip = ctorBinding.getInternalConstructor();
//                        if(!ctorBinding.isInitialized()) {
//                            failed = true;
//                        }
//                    }
//                    if(failed) {
//                        removeFailedJitBinding(depKey, ip);
//                        bindingFailed = true;
//                    }
//                } else if(state.getExplicitBinding(depKey) == null) {
//                    // ignore keys if they were explicitly bound, but if neither JIT
//                    // nor explicit, it's also invalid & should let parent know.
//                    bindingFailed = true;
//                }
//            }
//        }
//        return bindingFailed;
//    }

    /** Cleans up any state that may have been cached when constructing the JIT binding. */
//    private void removeFailedJitBinding(Key<?> key, InjectionPoint ip) {
//        jitBindings.remove(key);
//        membersInjectorStore.remove(key.getTypeLiteral());
//        if(ip != null) {
//            constructors.remove(ip);
//        }
//    }

    /** Safely gets the dependencies of possibly not initialized bindings. */
    @SuppressWarnings("unchecked")
//    private Set<Dependency<?>> getInternalDependencies(BindingImpl<?> binding) {
//        if(binding instanceof ConstructorBindingImpl) {
//            return ((ConstructorBindingImpl)binding).getInternalDependencies();
//        } else if(binding instanceof HasDependencies) {
//            return ((HasDependencies)binding).getDependencies();
//        } else {
//            return ImmutableSet.of();
//        }
//    }

    /**
     * Creates a binding for an injectable type with the given scope. Looks for a scope on the type if
     * none is specified.
     */
//    <T> BindingImpl<T> createUninitializedBinding(Key<T> key, Scoping scoping, Object source,
//                                                  Errors errors, boolean jitBinding) throws ErrorsException {
//        Class<?> rawType = key.getTypeLiteral().getRawType();
//
//        // Don't try to inject arrays, or enums.
//        if (rawType.isArray() || rawType.isEnum()) {
//            throw errors.missingImplementation(key).toException();
//        }
//
//        // Handle TypeLiteral<T> by binding the inner type
//        if (rawType == TypeLiteral.class) {
//            @SuppressWarnings("unchecked") // we have to fudge the inner type as Object
//                    BindingImpl<T> binding = (BindingImpl<T>) createTypeLiteralBinding(
//                    (Key<TypeLiteral<Object>>) key, errors);
//            return binding;
//        }
//
//        // Handle @ImplementedBy
//        ImplementedBy implementedBy = rawType.getAnnotation(ImplementedBy.class);
//        if (implementedBy != null) {
//            Annotations.checkForMisplacedScopeAnnotations(rawType, source, errors);
//            return createImplementedByBinding(key, scoping, implementedBy, errors);
//        }
//
//        // Handle @ProvidedBy.
//        ProvidedBy providedBy = rawType.getAnnotation(ProvidedBy.class);
//        if (providedBy != null) {
//            Annotations.checkForMisplacedScopeAnnotations(rawType, source, errors);
//            return createProvidedByBinding(key, scoping, providedBy, errors);
//        }
//
//
//        return ConstructorBindingImpl.create(this, key, null, source, scoping, errors, jitBinding && options.jitDisabled);
//    }

    /**
     * Converts a binding for a {@code Key<TypeLiteral<T>>} to the value {@code TypeLiteral<T>}. It's
     * a bit awkward because we have to pull out the inner type in the type literal.
     */
//    private <T> BindingImpl<TypeLiteral<T>> createTypeLiteralBinding(
//            Key<TypeLiteral<T>> key, Errors errors) throws ErrorsException {
//        Type typeLiteralType = key.getTypeLiteral().getType();
//        if (!(typeLiteralType instanceof ParameterizedType)) {
//            throw errors.cannotInjectRawTypeLiteral().toException();
//        }
//
//        ParameterizedType parameterizedType = (ParameterizedType) typeLiteralType;
//        Type innerType = parameterizedType.getActualTypeArguments()[0];
//
//        // this is unforunate. We don't support building TypeLiterals for type variable like 'T'. If
//        // this proves problematic, we can probably fix TypeLiteral to support type variables
//        if (!(innerType instanceof Class)
//                && !(innerType instanceof GenericArrayType)
//                && !(innerType instanceof ParameterizedType)) {
//            throw errors.cannotInjectTypeLiteralOf(innerType).toException();
//        }
//
//        @SuppressWarnings("unchecked") // by definition, innerType == T, so this is safe
//                TypeLiteral<T> value = (TypeLiteral<T>) TypeLiteral.get(innerType);
//        InternalFactory<TypeLiteral<T>> factory = new ConstantFactory<TypeLiteral<T>>(
//                Initializables.of(value));
//        return new InstanceBindingImpl<TypeLiteral<T>>(this, key, SourceProvider.UNKNOWN_SOURCE,
//                factory, ImmutableSet.<InjectionPoint>of(), value);
//    }

    /** Creates a binding for a type annotated with @ProvidedBy. */
//    <T> BindingImpl<T> createProvidedByBinding(Key<T> key, Scoping scoping,
//                                               ProvidedBy providedBy, Errors errors) throws ErrorsException {
//        final Class<?> rawType = key.getTypeLiteral().getRawType();
//        final Class<? extends Provider<?>> providerType = providedBy.value();
//
//        // Make sure it's not the same type. TODO: Can we check for deeper loops?
//        if (providerType == rawType) {
//            throw errors.recursiveProviderType().toException();
//        }
//
//        // Assume the provider provides an appropriate type. We double check at runtime.
//        @SuppressWarnings("unchecked")
//        final Key<? extends Provider<T>> providerKey
//                = (Key<? extends Provider<T>>) Key.get(providerType);
//        final BindingImpl<? extends Provider<?>> providerBinding
//                = getBindingOrThrow(providerKey, errors, GZInjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
//
//        InternalFactory<T> internalFactory = new InternalFactory<T>() {
//            public T get(Errors errors, InternalContext context, Dependency dependency, boolean linked)
//                    throws ErrorsException {
//                errors = errors.withSource(providerKey);
//                Provider<?> provider = providerBinding.getInternalFactory().get(
//                        errors, context, dependency, true);
//                try {
//                    Object o = provider.get();
//                    if (o != null && !rawType.isInstance(o)) {
//                        throw errors.subtypeNotProvided(providerType, rawType).toException();
//                    }
//                    @SuppressWarnings("unchecked") // protected by isInstance() check above
//                            T t = (T) o;
//                    return t;
//                } catch (RuntimeException e) {
//                    throw errors.errorInProvider(e).toException();
//                }
//            }
//        };
//
//        Object source = rawType;
//        return new LinkedProviderBindingImpl<T>(
//                this,
//                key,
//                source,
//                Scoping.<T>scope(key, this, internalFactory, source, scoping),
//                scoping,
//                providerKey);
//    }

//    /** Creates a binding for a type annotated with @ImplementedBy. */
//    private <T> BindingImpl<T> createImplementedByBinding(Key<T> key, Scoping scoping,
//                                                          ImplementedBy implementedBy, Errors errors)
//            throws ErrorsException {
//        Class<?> rawType = key.getTypeLiteral().getRawType();
//        Class<?> implementationType = implementedBy.value();
//
//        // Make sure it's not the same type. TODO: Can we check for deeper cycles?
//        if (implementationType == rawType) {
//            throw errors.recursiveImplementationType().toException();
//        }
//
//        // Make sure implementationType extends type.
//        if (!rawType.isAssignableFrom(implementationType)) {
//            throw errors.notASubtype(implementationType, rawType).toException();
//        }
//
//        @SuppressWarnings("unchecked") // After the preceding check, this cast is safe.
//                Class<? extends T> subclass = (Class<? extends T>) implementationType;
//
//        // Look up the target binding.
//        final Key<? extends T> targetKey = Key.get(subclass);
//        final BindingImpl<? extends T> targetBinding = getBindingOrThrow(targetKey, errors, GZInjectorImpl.JitLimitation.NEW_OR_EXISTING_JIT);
//
//        InternalFactory<T> internalFactory = new InternalFactory<T>() {
//            public T get(Errors errors, InternalContext context, Dependency<?> dependency, boolean linked)
//                    throws ErrorsException {
//                return targetBinding.getInternalFactory().get(
//                        errors.withSource(targetKey), context, dependency, true);
//            }
//        };
//
//        Object source = rawType;
//        return new LinkedBindingImpl<T>(
//                this,
//                key,
//                source,
//                Scoping.<T>scope(key, this, internalFactory, source, scoping),
//                scoping,
//                targetKey);
//    }

    /**
     * Attempts to create a just-in-time binding for {@code key} in the root injector, falling back to
     * other ancestor injectors until this injector is tried.
     */
//    private <T> BindingImpl<T> createJustInTimeBindingRecursive(Key<T> key, Errors errors)
//            throws ErrorsException {
//        // ask the parent to create the JIT binding
//        if (parent != null && !parent.options.jitDisabled) {
//            try {
//                return parent.createJustInTimeBindingRecursive(key, new Errors());
//            } catch (ErrorsException ignored) {
//            }
//        }
//
//        if (state.isBlacklisted(key)) {
//            throw errors.childBindingAlreadySet(key).toException();
//        }
//
//        BindingImpl<T> binding = createJustInTimeBinding(key, errors);
//        state.parent().blacklist(key);
//        jitBindings.put(key, binding);
//        return binding;
//    }

    /**
     * Returns a new just-in-time binding created by resolving {@code key}. The strategies used to
     * create just-in-time bindings are:
     * <ol>
     *   <li>Internalizing Providers. If the requested binding is for {@code Provider<T>}, we delegate
     *     to the binding for {@code T}.
     *   <li>Converting constants.
     *   <li>ImplementedBy and ProvidedBy annotations. Only for unannotated keys.
     *   <li>The constructor of the raw type. Only for unannotated keys.
     * </ol>
     *
     * @throws com.google.inject.internal.ErrorsException if the binding cannot be created.
     */
//    private <T> BindingImpl<T> createJustInTimeBinding(Key<T> key, Errors errors)
//            throws ErrorsException {
//        int numErrorsBefore = errors.size();
//
//        if (state.isBlacklisted(key)) {
//            throw errors.childBindingAlreadySet(key).toException();
//        }
//
//        // Handle cases where T is a Provider<?>.
//        if (isProvider(key)) {
//            // These casts are safe. We know T extends Provider<X> and that given Key<Provider<X>>,
//            // createProviderBinding() will return BindingImpl<Provider<X>>.
//            @SuppressWarnings("unchecked")
//            BindingImpl<T> binding = (BindingImpl<T>) createProviderBinding((Key) key, errors);
//            return binding;
//        }
//
//        // Handle cases where T is a MembersInjector<?>
//        if (isMembersInjector(key)) {
//            // These casts are safe. T extends MembersInjector<X> and that given Key<MembersInjector<X>>,
//            // createMembersInjectorBinding() will return BindingImpl<MembersInjector<X>>.
//            @SuppressWarnings("unchecked")
//            BindingImpl<T> binding = (BindingImpl<T>) createMembersInjectorBinding((Key) key, errors);
//            return binding;
//        }
//
//        // Try to convert a constant string binding to the requested type.
//        BindingImpl<T> convertedBinding = convertConstantStringBinding(key, errors);
//        if (convertedBinding != null) {
//            return convertedBinding;
//        }
//
//        // If the key has an annotation...
//        if (key.getAnnotationType() != null) {
//            // Look for a binding without annotation attributes or return null.
//            if (key.hasAttributes()) {
//                try {
//                    Errors ignored = new Errors();
//                    return getBindingOrThrow(key.withoutAttributes(), ignored, GZInjectorImpl.JitLimitation.NO_JIT);
//                } catch (ErrorsException ignored) {
//                    // throw with a more appropriate message below
//                }
//            }
//            throw errors.missingImplementation(key).toException();
//        }
//
//        Object source = key.getTypeLiteral().getRawType();
//        BindingImpl<T> binding = createUninitializedBinding(key, Scoping.UNSCOPED, source, errors, true);
//        errors.throwIfNewErrors(numErrorsBefore);
//        initializeJitBinding(binding, errors);
//        return binding;
//    }

//    <T> GZInternalFactory<? extends T> getInternalFactory(Key<T> key, Errors errors, GZInjectorImpl.JitLimitation jitType)
//            throws ErrorsException {
//        return getBindingOrThrow(key, errors, jitType).getInternalFactory();
//    }

    public Map<Key<?>, Binding<?>> getBindings() {
        return state.getExplicitBindingsThisLevel();
    }

    public Map<Key<?>, Binding<?>> getAllBindings() {
        synchronized (state.lock()) {
            return new ImmutableMap.Builder<Key<?>, Binding<?>>()
                    .putAll(state.getExplicitBindingsThisLevel())
                    .putAll(jitBindings)
                    .build();
        }
    }

    public Map<Class<? extends Annotation>, Scope> getScopeBindings() {
        return Collections.unmodifiableMap(state.getScopes());
    }

    public List<TypeConverterBinding> getTypeConverterBindings() {
        return Collections.unmodifiableList(state.getConvertersThisLevel());
    }

    private static class BindingsMultimap {
        final Map<TypeLiteral<?>, List<Binding<?>>> multimap = Maps.newHashMap();

        <T> void put(TypeLiteral<T> type, Binding<T> binding) {
            List<Binding<?>> bindingsForType = multimap.get(type);
            if (bindingsForType == null) {
                bindingsForType = Lists.newArrayList();
                multimap.put(type, bindingsForType);
            }
            bindingsForType.add(binding);
        }


        @SuppressWarnings("unchecked") // safe because we only put matching entries into the map
        <T> List<Binding<T>> getAll(TypeLiteral<T> type) {
            List<Binding<?>> bindings = multimap.get(type);
            return bindings != null
                    ? Collections.<Binding<T>>unmodifiableList((List) multimap.get(type))
                    : ImmutableList.<Binding<T>>of();
        }
    }

    /**
     * Returns parameter injectors, or {@code null} if there are no parameters.
     */
//    SingleParameterInjector<?>[] getParametersInjectors(
//            List<Dependency<?>> parameters, Errors errors) throws ErrorsException {
//        if (parameters.isEmpty()) {
//            return null;
//        }
//
//        int numErrorsBefore = errors.size();
//        SingleParameterInjector<?>[] result = new SingleParameterInjector<?>[parameters.size()];
//        int i = 0;
//        for (Dependency<?> parameter : parameters) {
//            try {
//                result[i++] = createParameterInjector(parameter, errors.withSource(parameter));
//            } catch (ErrorsException rethrownBelow) {
//                // rethrown below
//            }
//        }
//
//        errors.throwIfNewErrors(numErrorsBefore);
//        return result;
//    }

//    <T> SingleParameterInjector<T> createParameterInjector(final Dependency<T> dependency,
//                                                           final Errors errors) throws ErrorsException {
//        InternalFactory<? extends T> factory = getInternalFactory(dependency.getKey(), errors, GZInjectorImpl.JitLimitation.NO_JIT);
//        return new SingleParameterInjector<T>(dependency, factory);
//    }
//
//    /** Invokes a method. */
//    interface MethodInvoker {
//        Object invoke(Object target, Object... parameters)
//                throws IllegalAccessException, InvocationTargetException;
//    }
//
//    /** Cached constructor injectors for each type */
//    final ConstructorInjectorStore constructors = new ConstructorInjectorStore(this);
//
//    /** Cached field and method injectors for each type. */
    GZMembersInjectorStore membersInjectorStore;

    @SuppressWarnings("unchecked") // the members injector type is consistent with instance's type
//    public void injectMembers(Object instance) {
//        MembersInjector membersInjector = getMembersInjector(instance.getClass());
//        membersInjector.injectMembers(instance);
//    }

//    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> typeLiteral) {
//        Errors errors = new Errors(typeLiteral);
//        try {
//            return membersInjectorStore.get(typeLiteral, errors);
//        } catch (ErrorsException e) {
//            throw new ConfigurationException(errors.merge(e.getErrors()).getMessages());
//        }
//    }

//    public <T> MembersInjector<T> getMembersInjector(Class<T> type) {
//        return getMembersInjector(TypeLiteral.get(type));
//    }

//    public <T> Provider<T> getProvider(Class<T> type) {
//        return getProvider(Key.get(type));
//    }

//    <T> Provider<T> getProviderOrThrow(final Key<T> key, Errors errors) throws ErrorsException {
//
//
//        final GZInternalFactory<? extends T> factory = getInternalFactory(key, errors, GZInjectorImpl.JitLimitation.NO_JIT);
//        final Dependency<T> dependency = Dependency.get(key);
//
//        return new Provider<T>() {
//            public T get() {
//                final Errors errors = new Errors(dependency);
//                try {
//                    T t = callInContext(new ContextualCallable<T>() {
//                        public T call(InternalContext context) throws ErrorsException {
//                            Dependency previous = context.setDependency(dependency);
//                            try {
//                                return factory.get(errors, context, dependency, false);
//                            } finally {
//                                context.setDependency(previous);
//                            }
//                        }
//                    });
//                    errors.throwIfNewErrors(0);
//                    return t;
//                } catch (ErrorsException e) {
//                    throw new ProvisionException(errors.merge(e.getErrors()).getMessages());
//                }
//            }
//
//            @Override public String toString() {
//                return factory.toString();
//            }
//        };
//    }

//    public <T> Provider<T> getProvider(final Key<T> key) {
//        Errors errors = new Errors(key);
//        try {
//            Provider<T> result = getProviderOrThrow(key, errors);
//            errors.throwIfNewErrors(0);
//            return result;
//        } catch (ErrorsException e) {
//            throw new ConfigurationException(errors.merge(e.getErrors()).getMessages());
//        }
//    }

//    public <T> T getInstance(Key<T> key) {
//        return getProvider(key).get();
//    }
//
//    public <T> T getInstance(Class<T> type) {
//        return getProvider(type).get();
//    }
//
    final ThreadLocal<Object[]> localContext;

    /** Looks up thread local context. Creates (and removes) a new context if necessary. */
//    <T> T callInContext(ContextualCallable<T> callable) throws ErrorsException {
//        Object[] reference = localContext.get();
//        if (reference[0] == null) {
//            reference[0] = new InternalContext();
//            try {
//                return callable.call((InternalContext)reference[0]);
//            } finally {
//                // Only clear the context if this call created it.
//                reference[0] = null;
//            }
//        } else {
//            // Someone else will clean up this context.
//            return callable.call((InternalContext)reference[0]);
//        }
//    }

    public String toString() {
        return new ToStringBuilder(Injector.class)
                .add("bindings", state.getExplicitBindingsThisLevel().values())
                .toString();
    }
}
