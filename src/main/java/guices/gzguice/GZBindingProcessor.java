package guices.gzguice;

import com.google.inject.internal.Errors;

final class GZBindingProcessor {

    private final GZInitializer initializer;

    GZBindingProcessor(Errors errors, GZInitializer initializer) {
        this.initializer = initializer;
    }
}
