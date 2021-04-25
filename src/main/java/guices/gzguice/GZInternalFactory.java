package guices.gzguice;

import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;

import com.google.inject.spi.Dependency;

public interface GZInternalFactory<T> {
    /**
     * Creates an object to be injected.
     * @param context of this injection
     * @param linked true if getting as a result of a linked binding
     *
     * @throws com.google.inject.internal.ErrorsException if a value cannot be provided
     * @return instance to be injected
     */
    T get(Errors errors, GZInternalContext context, Dependency<?> dependency, boolean linked)
            throws ErrorsException;
}
