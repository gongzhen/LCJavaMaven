package gzhttpclient;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public final class GZMinimalFuture<T> extends CompletableFuture<T> {
    private static final AtomicLong TOKENS = new AtomicLong();
    private final long id;

    public static <U> GZMinimalFuture<U> completedFuture(U value) {
        GZMinimalFuture<U> f = new GZMinimalFuture<>();
        f.complete(value);
        return f;
    }

    public static <U> CompletableFuture<U> failedFuture(Throwable throwable) {
        Objects.requireNonNull(throwable);
        GZMinimalFuture<U> f = new GZMinimalFuture<>();
        f.completeExceptionally(throwable);
        return f;
    }

    public GZMinimalFuture() {
        this.id = TOKENS.incrementAndGet();
    }


}
