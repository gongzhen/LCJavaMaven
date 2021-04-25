package gzhttpclient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.util.Objects;

public class GZHttpRequestImpl extends GZHttpRequest {

    private final String method;
    private final URI uri;
    private volatile Proxy proxy;

    public GZHttpRequestImpl(GZHttpRequest request, ProxySelector ps) {
        String method = request.method();
        this.method = method == null ? "GET" : method;
        URI requestURI = (URI) Objects.requireNonNull(request.uri());
        this.uri = requestURI;
        this.proxy = null;
    }

    public String method() {
        return this.method;
    }

    public URI uri() {
        return this.uri;
    }

    InetSocketAddress proxy() {
        return (InetSocketAddress)this.proxy.address();
    }

}
