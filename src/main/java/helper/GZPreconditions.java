package helper;

import annotations.gzannotations.GZNonNull;
import com.google.inject.internal.Nullable;

public final class GZPreconditions {

    private GZPreconditions() {

    }

    static class TestClass {

    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T extends Object> T checkNotNull(@GZNonNull T reference, @Nullable Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    @GZNonNull
    public static <T extends TestClass> T testCheckNotNull(T reference, @Nullable Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
}
