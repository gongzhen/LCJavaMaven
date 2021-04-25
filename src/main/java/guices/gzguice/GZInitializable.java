package guices.gzguice;

import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;

interface GZInitializable<T> {
    /**
     * Ensures the reference is initialized, then returns it.
     */
    T get(Errors errors) throws ErrorsException;
}
