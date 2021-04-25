package gzhttpclient;

import helper.Utils;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import gzhttpclient.GZHttpClient.GZVersion;

final class GZImmutableHttpRequest extends GZHttpRequest {
    private final String method;
    private final URI uri;
    private final GZHttpHeaders headers;
    private final Optional<GZBodyPublisher> requestPublisher;
    private final Optional<GZVersion> version;


    GZImmutableHttpRequest(GZHttpRequestBuilderImpl builder) {
        this.method = Objects.requireNonNull(builder.method());
        this.uri = Objects.requireNonNull(builder.uri());
        this.headers = GZHttpHeaders.of(builder.headersBuilder().map(), Utils.ALLOWED_HEADERS);
        this.requestPublisher = Optional.ofNullable(builder.bodyPublisher());
        this.version = Objects.requireNonNull(builder.version());
    }

    public String method() {
        return this.method;
    }

    public URI uri() {
        return this.uri;
    }

    public GZHttpHeaders headers() {
        return this.headers;
    }

    public Optional<GZBodyPublisher> bodyPublisher() {
        return this.requestPublisher;
    }

}
