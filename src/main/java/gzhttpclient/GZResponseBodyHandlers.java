package gzhttpclient;

import gzhttpclient.GZHttpResponse.PushPromiseHandler;
import gzhttpclient.GZHttpResponse.BodyHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public final class GZResponseBodyHandlers {
    private GZResponseBodyHandlers() {

    }

    public static class PushPromisesHandlerWithMap<T> implements PushPromiseHandler<T> {
        private final ConcurrentMap<GZHttpRequest, CompletableFuture<GZHttpResponse<T>>> pushPromisesMap;
        private final Function<GZHttpRequest, BodyHandler<T>> pushPromiseHandler;

        public PushPromisesHandlerWithMap(Function<GZHttpRequest, BodyHandler<T>> pushPromiseHandler,
                                          ConcurrentMap<GZHttpRequest, CompletableFuture<GZHttpResponse<T>>> pushPromisesMap) {
            this.pushPromisesMap = pushPromisesMap;
            this.pushPromiseHandler = pushPromiseHandler;
        }

        @Override
        public void applyPushPromise(GZHttpRequest initiatingRequest,
                                     GZHttpRequest pushRequest,
                                     Function<BodyHandler<T>, CompletableFuture<GZHttpResponse<T>>> acceptor) {

        }
    }
}
