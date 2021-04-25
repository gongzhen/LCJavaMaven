package gzhttpclient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

final class GZExchange<T> {
    final GZHttpRequestImpl request;
    final GZHttpClientImpl client;
    final Executor parentExecutor;
    volatile CompletableFuture<Void> bodyIgnored;
    volatile GZExchangeImpl<T> exchImpl;

    GZExchange(GZHttpRequestImpl request, GZMultiExchange<T> multi) {
        this.request = request;
        this.client = multi.client();
        this.parentExecutor = multi.executor;
    }


    GZHttpClientImpl client() {
        return this.client;
    }

    public GZHttpRequestImpl request() {
        return this.request;
    }

    public CompletableFuture<GZResponse> responseAsync() {
        return this.responseAsyncImpl(null);
    }

    public CompletableFuture<Void> ignoreBody() {
        return this.bodyIgnored;
    }

    public CompletableFuture<T> readBodyAsync(GZHttpResponse.BodyHandler<T> handler) {
        return this.exchImpl.readBodyAsync(handler, false, this.parentExecutor).whenComplete((r, t) -> {
            this.exchImpl.completed();
        });
    }

    CompletableFuture<GZResponse> responseAsyncImpl(GZHttpConnection connection) {
        return this.responseAsyncImpl0(connection);
    }

    CompletableFuture<GZResponse> responseAsyncImpl0(GZHttpConnection connection) {
        Function after407Check;
        after407Check = this::sendRequestBody;

        Function<GZExchangeImpl<T>, CompletableFuture<GZResponse>> afterExch407Check = (ex) -> {
            return ex.sendHeadersAsync().handle((r, t) -> {
                return this.checkFor407(r, t, after407Check);
            }).thenCompose(t -> t);
        };

        return this.establishExchange(connection).handle((r, t) -> {
            return this.checkFor407(r, t, afterExch407Check);
        }).thenCompose(Function.identity());
    }


    private CompletableFuture<GZResponse> sendRequestBody(Object ex) {
        GZExchangeImpl<T> exb = (GZExchangeImpl)ex;
        CompletableFuture<GZResponse> cf = exb
                .sendBodyAsync()
                .thenCompose((exIm) -> {
                    return exIm.getResponseAsync(this.parentExecutor);
                });
        return cf;
    }

    private CompletableFuture<GZResponse> checkFor407(GZExchangeImpl<T> ex,
                                                      Throwable t,
                                                      Function<GZExchangeImpl<T>, CompletableFuture<GZResponse>> andThen) {
        return (CompletableFuture)andThen.apply(ex);
    }

    private CompletableFuture<? extends GZExchangeImpl<T>> establishExchange(GZHttpConnection connection) {
        CompletableFuture<? extends GZExchangeImpl<T>> cf = GZExchangeImpl.get(this, connection);
        synchronized (this) {

        }

        CompletableFuture<? extends  GZExchangeImpl<T>> res = cf.whenComplete((r, x) -> {

        });

        return res.thenCompose((eimpl) -> {
            return GZMinimalFuture.completedFuture(eimpl);
        });
    }
}
