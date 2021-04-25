package gzhttpclient;

import com.google.inject.Provider;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;

class GZHttpResponseImpl<T> implements GZHttpResponse<T>, Provider {

    final int responseCode;
    final GZExchange<T> exchange;
    final GZHttpRequest initialRequest;
    final Optional<GZHttpResponse<T>> previousResponse;
    final GZHttpHeaders headers;
    final URI uri;
    final GZHttpClient.GZVersion version;
    final Stream<T> stream;
    final T body;

    public GZHttpResponseImpl(GZHttpRequest initialRequest, GZResponse response, GZHttpResponse<T> previousResponse, T body, GZExchange<T> exch) {
        this.responseCode = response.statusCode();
        this.exchange = exch;
        this.initialRequest = initialRequest;
        this.previousResponse = Optional.ofNullable(previousResponse);
        this.headers = response.headers();
        this.uri = response.request().uri();
        this.version = response.version();
        this.stream = null;
        this.body = body;
    }
    @Override
    public Object get() {
        return null;
    }

    @Override
    public int statusCode() {
        return 0;
    }

    @Override
    public GZHttpRequest request() {
        return null;
    }

    @Override
    public Optional<GZHttpResponse<T>> previousResponse() {
        return Optional.empty();
    }

    @Override
    public GZHttpHeaders headers() {
        return null;
    }

    @Override
    public T body() {
        return null;
    }

    @Override
    public Optional<SSLSession> sslSession() {
        return Optional.empty();
    }

    @Override
    public URI uri() {
        return null;
    }

    @Override
    public GZHttpClient.GZVersion version() {
        return null;
    }
}
