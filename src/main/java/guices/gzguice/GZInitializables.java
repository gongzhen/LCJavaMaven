package guices.gzguice;

import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;

final class GZInitializables {
    /**
     * Returns an initializable for an instance that requires no initialization.
     */
    static <T> GZInitializable<T> of(final T instance) {
        return new GZInitializable<T>() {
            public T get(Errors errors) throws ErrorsException {
                return instance;
            }

            @Override public String toString() {
                return String.valueOf(instance);
            }
        };
    }
}
