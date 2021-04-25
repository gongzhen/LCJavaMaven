package gzhttpclient;

import java.lang.ref.Reference;
import java.util.concurrent.CompletableFuture;

//6 Create GZHttpClientFacade object extends GZHttpClient
final class GZHttpClientFacade extends GZHttpClient {
    final GZHttpClientImpl impl;

    GZHttpClientFacade(GZHttpClientImpl impl) {
        this.impl = impl;
    }

    @Override
    public <T> CompletableFuture<GZHttpResponse<T>> sendAsync(GZHttpRequest request, GZHttpResponse.BodyHandler<T> responseBodyHandler) {
        CompletableFuture var3;
        try {
            var3 = this.impl.sendAsync(request, responseBodyHandler);
        } finally {
            Reference.reachabilityFence(this);
        }
        return var3;
    }

    @Override
    public <T> CompletableFuture<GZHttpResponse<T>> sendAsync(GZHttpRequest var1, GZHttpResponse.BodyHandler<T> var2, GZHttpResponse.PushPromiseHandler<T> var3) {
        return null;
    }
}
