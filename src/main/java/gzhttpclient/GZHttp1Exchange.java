package gzhttpclient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

class GZHttp1Exchange<T> extends GZExchangeImpl<T> {

    GZHttp1Exchange(GZExchange<T> exchange, GZHttpConnection connection) {
        super(exchange);

    }

    @Override
    CompletableFuture<GZExchangeImpl<T>> sendBodyAsync() {
        return null;
    }

    @Override
    CompletableFuture<GZResponse> getResponseAsync(Executor var1) {
        return null;
    }

    @Override
    CompletableFuture<GZExchangeImpl<T>> sendHeadersAsync() {
        return null;
    }

    @Override
    CompletableFuture<T> readBodyAsync(GZHttpResponse.BodyHandler<T> var1, boolean var2, Executor var3) {
        return null;
    }

    @Override
    void completed() {

    }
}
