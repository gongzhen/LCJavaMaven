package gzhttpclient;

import gzhttpclient.GZHttpRequest.Builder;
import gzhttpclient.GZHttpClient.GZVersion;
import gzhttpclient.GZHttpRequest.GZBodyPublisher;
import helper.Utils;

import java.net.URI;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class GZHttpRequestBuilderImpl implements Builder {

    private GZHttpHeadersBuilder headersBuilder;
    private URI uri;
    private String method;
    private boolean expectContinue;
    private volatile Optional<GZVersion> version;
    private Duration duration;
    private GZBodyPublisher bodyPublisher;

    public GZHttpRequestBuilderImpl(URI uri) {
        Objects.requireNonNull(uri);
        checkURI(uri);
        this.uri = uri;
        this.headersBuilder = new GZHttpHeadersBuilder();
        this.method = "GET";
        this.version = Optional.empty();
    }

    public GZHttpRequestBuilderImpl() {
        this.headersBuilder = new GZHttpHeadersBuilder();
        this.method = "GET";
        this.version = Optional.empty();
    }

    static void checkURI(URI uri) {
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw new IllegalArgumentException("URI with undefined schema");
        } else {
            scheme = scheme.toLowerCase(Locale.US);
            if (!scheme.equals("https") && !scheme.equals("http")) {
                throw new IllegalArgumentException(String.format("invalid URI scheme %s", new Object[]{scheme}));
            } else if (uri.getHost() == null) {
                throw new IllegalArgumentException(String.format("unsupported URI %s", new Object[]{uri}));
            }
        }
    }

    private void checkNameAndValue(String name, String value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull("value", "value");
        if (!Utils.isValidName(name)) {
            throw new IllegalArgumentException(String.format("invalid header name: \"%s\"", new Object[]{name}));
        } else if (!Utils.isValidValue(value)) {
            throw new IllegalArgumentException(String.format("invalid header value: \"%s\"", new Object[]{value}));
        }
    }

    @Override
    public Builder uri(URI var1) {
        return null;
    }

    @Override
    public Builder expectContinue(boolean var1) {
        return null;
    }

    @Override
    public Builder version(GZVersion var1) {
        return null;
    }

    @Override
    public Builder header(String var1, String var2) {
        return null;
    }

    @Override
    public Builder headers(String... var1) {
        return null;
    }

    @Override
    public Builder timeout(Duration var1) {
        return null;
    }

    @Override
    public Builder setHeader(String name, String value) {
        this.checkNameAndValue(name, value);
        this.headersBuilder.setHeader(name, value);
        return this;
    }

    @Override
    public Builder GET() {
        return this.method0("get", null);
    }

    @Override
    public Builder POST(GZBodyPublisher var1) {
        return null;
    }

    @Override
    public Builder PUT(GZBodyPublisher var1) {
        return null;
    }

    @Override
    public Builder DELETE() {
        return null;
    }

    @Override
    public Builder method(String var1, GZBodyPublisher var2) {
        return null;
    }

    @Override
    public GZHttpRequest build() {
        return new GZImmutableHttpRequest(this);
    }

    @Override
    public Builder copy() {
        return null;
    }

    GZHttpHeadersBuilder headersBuilder() {
        return this.headersBuilder;
    }

    URI uri() {
        return this.uri;
    }

    String method() {
        return this.method;
    }

    GZBodyPublisher bodyPublisher() {
        return this.bodyPublisher;
    }

    Optional<GZVersion> version() {
        return this.version;
    }

    private Builder method0(String method, GZBodyPublisher body) {
        assert method != null;

        assert !method.equals("");

        this.method = method;
        this.bodyPublisher = body;
        return this;
    }
}
