package gzhttpclient;


import javax.net.ssl.SSLSession;
import gzhttpclient.GZHttpClient.GZVersion;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Flow;
import java.util.function.Function;
import gzhttpclient.GZResponseBodyHandlers.PushPromisesHandlerWithMap;

public interface GZHttpResponse<T> {
    int statusCode();

    GZHttpRequest request();

    Optional<GZHttpResponse<T>> previousResponse();

    GZHttpHeaders headers();

    T body();

    Optional<SSLSession> sslSession();

    URI uri();

    GZVersion version();

    public interface BodySubscriber<T> extends Flow.Subscriber<List<ByteBuffer>> {
        CompletionStage<T> getBody();
    }

    public interface PushPromiseHandler<T> {
        void applyPushPromise(GZHttpRequest var1,
                              GZHttpRequest var2,
                              Function<BodyHandler<T>, CompletableFuture<GZHttpResponse<T>>> var3);


        static <T> GZHttpResponse.PushPromiseHandler<T> of(Function<GZHttpRequest, GZHttpResponse.BodyHandler<T>> pushPromiseHandler,
                                                           ConcurrentMap<GZHttpRequest, CompletableFuture<GZHttpResponse<T>>>pushPromisesMap) {
            return new PushPromisesHandlerWithMap(pushPromiseHandler, pushPromisesMap);
        }
    }

    @FunctionalInterface
    public interface BodyHandler<T> {
        GZHttpResponse<T> apply(GZHttpResponse.ResponseInfo var1);
    }

    public interface ResponseInfo {
        int statusCode();

        GZHttpHeaders headers();

        GZVersion version();
    }
}
