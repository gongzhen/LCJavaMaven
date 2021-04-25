package guices.gzguice;

import com.google.inject.ConfigurationException;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.*;
import com.google.inject.internal.util.ImmutableList;
import com.google.inject.internal.util.Lists;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.TypeListenerBinding;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

final class GZMembersInjectorStore {
//    private final InjectorImpl injector;
//    private final ImmutableList<TypeListenerBinding> typeListenerBindings;
//
//    private final FailableCache<TypeLiteral<?>, MembersInjectorImpl<?>> cache
//            = new FailableCache<TypeLiteral<?>, MembersInjectorImpl<?>>() {
//        @Override protected MembersInjectorImpl<?> create(TypeLiteral<?> type, Errors errors)
//                throws ErrorsException {
//            return createWithListeners(type, errors);
//        }
//    };
//
//    MembersInjectorStore(InjectorImpl injector,
//                         List<TypeListenerBinding> typeListenerBindings) {
//        this.injector = injector;
//        this.typeListenerBindings = ImmutableList.copyOf(typeListenerBindings);
//    }
//
//    /**
//     * Returns true if any type listeners are installed. Other code may take shortcuts when there
//     * aren't any type listeners.
//     */
//    public boolean hasTypeListeners() {
//        return !typeListenerBindings.isEmpty();
//    }

    /**
     * Returns a new complete members injector with injection listeners registered.
     */
    @SuppressWarnings("unchecked") // the MembersInjector type always agrees with the passed type
    public <T> GZMembersInjectorImpl<T> get(TypeLiteral<T> key, Errors errors) throws ErrorsException {
//        return (GZMembersInjectorImpl<T>) cache.get(key, errors);
        return null;
    }

//    /**
//     * Purges a type literal from the cache. Use this only if the type is not actually valid for
//     * binding and needs to be purged. (See issue 319 and
//     * ImplicitBindingTest#testCircularJitBindingsLeaveNoResidue and
//     * #testInstancesRequestingProvidersForThemselvesWithChildInjectors for examples of when this is
//     * necessary.)
//     *
//     * Returns true if the type was stored in the cache, false otherwise.
//     */
//    boolean remove(TypeLiteral<?> type) {
//        return cache.remove(type);
//    }
//
//    /**
//     * Creates a new members injector and attaches both injection listeners and method aspects.
//     */
//    private <T> MembersInjectorImpl<T> createWithListeners(TypeLiteral<T> type, Errors errors)
//            throws ErrorsException {
//        int numErrorsBefore = errors.size();
//
//        Set<InjectionPoint> injectionPoints;
//        try {
//            injectionPoints = InjectionPoint.forInstanceMethodsAndFields(type);
//        } catch (ConfigurationException e) {
//            errors.merge(e.getErrorMessages());
//            injectionPoints = e.getPartialValue();
//        }
//        ImmutableList<SingleMemberInjector> injectors = getInjectors(injectionPoints, errors);
//        errors.throwIfNewErrors(numErrorsBefore);
//
//        EncounterImpl<T> encounter = new EncounterImpl<T>(errors, injector.lookups);
//        for (TypeListenerBinding typeListener : typeListenerBindings) {
//            if (typeListener.getTypeMatcher().matches(type)) {
//                try {
//                    typeListener.getListener().hear(type, encounter);
//                } catch (RuntimeException e) {
//                    errors.errorNotifyingTypeListener(typeListener, type, e);
//                }
//            }
//        }
//        encounter.invalidate();
//        errors.throwIfNewErrors(numErrorsBefore);
//
//        return new MembersInjectorImpl<T>(injector, type, encounter, injectors);
//    }
//
//    /**
//     * Returns the injectors for the specified injection points.
//     */
//    ImmutableList<SingleMemberInjector> getInjectors(
//            Set<InjectionPoint> injectionPoints, Errors errors) {
//        List<SingleMemberInjector> injectors = Lists.newArrayList();
//        for (InjectionPoint injectionPoint : injectionPoints) {
//            try {
//                Errors errorsForMember = injectionPoint.isOptional()
//                        ? new Errors(injectionPoint)
//                        : errors.withSource(injectionPoint);
//                SingleMemberInjector injector = injectionPoint.getMember() instanceof Field
//                        ? new SingleFieldInjector(this.injector, injectionPoint, errorsForMember)
//                        : new SingleMethodInjector(this.injector, injectionPoint, errorsForMember);
//                injectors.add(injector);
//            } catch (ErrorsException ignoredForNow) {
//                // ignored for now
//            }
//        }
//        return ImmutableList.copyOf(injectors);
//    }
}
