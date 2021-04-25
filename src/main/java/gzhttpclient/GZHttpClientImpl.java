package gzhttpclient;

import gzhttpclient.GZHttpResponse.BodyHandler;
import gzhttpclient.GZHttpResponse.PushPromiseHandler;

import java.net.ProxySelector;
import java.security.AccessControlContext;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

//4 GZHttpClientImpl will extends GZHttpClient with implementation
// create() will return GZHttpClientFacade.
final class GZHttpClientImpl extends GZHttpClient {

    private final ProxySelector proxySelector = null;
    private final GZHttpClientImpl.DelegatingExecutor delegatingExecutor;
    private final GZHttp2ClientImpl client2;
    private final GZHttpClientImpl.SelectorManager selmgr;
    private final GZFilterFactory filters;

    static GZHttpClientFacade create(GZHttpClientBuilderImpl builder) {
        GZHttpClientImpl.SingleFacadeFactory facadeFactory = new GZHttpClientImpl.SingleFacadeFactory();
        GZHttpClientImpl impl = new GZHttpClientImpl(builder, facadeFactory);

        // @todo
        // impl.start();

        return facadeFactory.facade;
    }

    private GZHttpClientImpl(GZHttpClientBuilderImpl builder, GZHttpClientImpl.SingleFacadeFactory facadeFactory) {
        this.client2 = new GZHttp2ClientImpl(this);
        this.filters = new GZFilterFactory();
        Executor ex = builder.executor;
        this.delegatingExecutor = new GZHttpClientImpl.DelegatingExecutor(this::isSelectorThread, ex);
        this.selmgr = new GZHttpClientImpl.SelectorManager(this);
    }

    private void start() {

    }

    boolean isSelectorThread() {
        return Thread.currentThread() == this.selmgr;
    }

    GZHttp2ClientImpl client2() {
        return this.client2;
    }

    final LinkedList<GZHeaderFilter> filterChain() {
        return this.filters.getFilterChain();
    }

    final GZHttpClientImpl.DelegatingExecutor theExecutor() {
        return this.delegatingExecutor;
    }

    @Override
    public <T> CompletableFuture<GZHttpResponse<T>> sendAsync(GZHttpRequest userRequest,
                                                              BodyHandler<T> responseHandler) {
        return this.sendAsync(userRequest, responseHandler, null);
    }

    @Override
    public <T> CompletableFuture<GZHttpResponse<T>> sendAsync(GZHttpRequest userRequest,
                                                              BodyHandler<T> responseHandler,
                                                              PushPromiseHandler<T> pushPromiseHandler) {
        return this.sendAsync(userRequest, responseHandler, null, null);
    }

    private <T> CompletableFuture<GZHttpResponse<T>> sendAsync(GZHttpRequest userRequest,
                                                               BodyHandler<T> responseHandler,
                                                               GZHttpResponse.PushPromiseHandler<T> pushPromiseHandler,
                                                               Executor exchangeExecutor) {
        Objects.requireNonNull(userRequest);
        Objects.requireNonNull(responseHandler);
        AccessControlContext acc = null;

        GZHttpRequestImpl requestImpl = new GZHttpRequestImpl(userRequest, null);

        // ignore CONNECT
        Executor executor = exchangeExecutor == null ? this.delegatingExecutor : exchangeExecutor;
        GZMultiExchange<T> mex = new GZMultiExchange(userRequest, requestImpl, this, responseHandler, pushPromiseHandler, acc);
        CompletableFuture<GZHttpResponse<T>> res = mex.responseAsync(executor).whenComplete((b, t) -> {

        });
        return res;
    }

    // 5: SingleFacadeFactory has GZHttpClientFacade object(facade)
    private static final class SingleFacadeFactory {
        GZHttpClientFacade facade;
        private SingleFacadeFactory() {

        }

        GZHttpClientFacade createFacade(GZHttpClientImpl impl) {
            assert this.facade == null;
            return this.facade = new GZHttpClientFacade(impl);
        }
    }

    static final class DelegatingExecutor implements Executor {
        private final GZBooleanSupplier isInSelectorThread;
        private final Executor delegate;

        DelegatingExecutor(GZBooleanSupplier isInSelectorThread, Executor delegate) {
            this.isInSelectorThread = isInSelectorThread;
            this.delegate = delegate;
        }

        @Override
        public void execute(Runnable runnable) {
            if (this.isInSelectorThread.getAsBoolean()) {
                this.delegate.execute(runnable);
            } else {
                runnable.run();
            }
        }
    }

    private static final class SelectorManager extends Thread {
        SelectorManager(GZHttpClientImpl ref) {

        }
    }
}
