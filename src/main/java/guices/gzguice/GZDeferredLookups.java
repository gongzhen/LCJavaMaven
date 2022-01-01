package guices.gzguice;

import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Errors;
import com.google.inject.spi.Element;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.ProviderLookup;

import java.util.List;

final class GZDeferredLookups implements GZLookups {
    private final GZInjectorImpl injector;
    private final List<Element> lookups = Lists.newArrayList();

    public GZDeferredLookups(GZInjectorImpl injector) {
        this.injector = injector;
    }

    /**
     * Initialize the specified lookups, either immediately or when the injector is created.
     */
    void initialize(Errors errors) {
        // injector.lookups = injector;
        // new LookupProcessor(errors).process(injector, lookups);
    }

    public <T> Provider<T> getProvider(Key<T> key) {
        ProviderLookup<T> lookup = new ProviderLookup<T>(key, key);
        lookups.add(lookup);
        return lookup.getProvider();
    }

    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> type) {
        MembersInjectorLookup<T> lookup = new MembersInjectorLookup<T>(type, type);
        lookups.add(lookup);
        return lookup.getMembersInjector();
    }
}
