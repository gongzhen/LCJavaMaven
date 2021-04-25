package gzhttpclient;

import gzhttpclient.GZHttpResponse.BodyHandler;
import gzhttpclient.GZHttpResponse.PushPromiseHandler;

import java.net.http.HttpResponse;
import java.security.AccessControlContext;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;


class GZMultiExchange<T> {
    private final GZHttpRequest userRequest;
    private final GZHttpRequestImpl request;
    final GZHttpClientImpl client;
    final BodyHandler<T> responseHandler;
    final AccessControlContext acc;
    GZExchange<T> exchange;
    final GZHttpClientImpl.DelegatingExecutor executor;
    private final LinkedList<GZHeaderFilter> fiters;
    volatile GZHttpResponse<T> response = null;

    GZMultiExchange(GZHttpRequest userRequest,
                    GZHttpRequestImpl requestImpl,
                    GZHttpClientImpl client,
                    BodyHandler<T> responseHandler,
                    PushPromiseHandler<T> pushPromiseHandler,
                    AccessControlContext acc) {
        this.userRequest = userRequest;
        this.request = requestImpl;
        this.client = client;
        this.responseHandler = responseHandler;
        this.acc = acc;
        this.exchange = new GZExchange<>(this.request, this);
        this.executor = client.theExecutor();
        this.fiters = client.filterChain();
    }

    synchronized GZExchange<T> getExchange() {
        return this.exchange;
    }

    GZHttpClientImpl client() {
        return this.client;
    }

    public CompletableFuture<GZHttpResponse<T>> responseAsync(Executor executor) {
        CompletableFuture<Void> start = new GZMinimalFuture<>();
        CompletableFuture<GZHttpResponse<T>> cf = this.responseAsync0(start);
        return cf;
    }

    private CompletableFuture<GZHttpResponse<T>> responseAsync0(CompletableFuture<Void> start) {
        return start.thenCompose((v) -> {
            return this.responseAsyncImpl();
        }).thenCompose((r) -> {
            GZExchange<T> exch = this.getExchange();
            return exch.readBodyAsync(this.responseHandler).thenApply((body) -> {
               this.response = new GZHttpResponseImpl(r.request(), r, this.response, body, exch);
               return this.response;
            });
        });
    }

    private CompletableFuture<GZResponse> responseAsyncImpl() {
        CompletableFuture<GZResponse> cf;
        // @todo implement retry attemtps

        GZExchange<T> exch = this.getExchange();
        cf = exch.responseAsync().thenCompose((response) -> {
            return exch.ignoreBody().handle((r, t) -> {
                return this.responseAsyncImpl();
            }).thenCompose(Function.identity());
        }).handle((response, ex) -> {
            return this.responseAsyncImpl();
        }).thenCompose(Function.identity());
        return cf;
    }
}
