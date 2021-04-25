package guices.gzguice;

import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

public interface GZLookups {
    <T> Provider<T> getProvider(Key<T> key);

    <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> type);
}
