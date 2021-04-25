package gzhttpclient;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

abstract class GZExchangeImpl<T> {

    final GZExchange<T> exchange;

    GZExchangeImpl(GZExchange<T> e) {
        this.exchange = e;
    }

    static <U> CompletableFuture<? extends GZExchangeImpl<U>> get(GZExchange<U> exchange, GZHttpConnection connection) {
        //Ignore http2
        return createHttp1Exchange(exchange, connection);
    }

    private static <T> CompletableFuture<GZHttp1Exchange<T>> createHttp1Exchange(GZExchange<T> ex, GZHttpConnection as) {
        return GZMinimalFuture.completedFuture(new GZHttp1Exchange<>(ex, as));
    }

    abstract  CompletableFuture<GZExchangeImpl<T>> sendBodyAsync();

    abstract CompletableFuture<GZResponse> getResponseAsync(Executor var1);

    abstract CompletableFuture<GZExchangeImpl<T>> sendHeadersAsync();

    abstract CompletableFuture<T> readBodyAsync(GZHttpResponse.BodyHandler<T> var1, boolean var2, Executor var3);

    abstract void completed();
}
