package gzhttpclient;

import java.net.URI;
import java.net.http.HttpClient;
import gzhttpclient.GZHttpClient.GZVersion;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.Flow.Publisher;

public abstract class GZHttpRequest {
    protected GZHttpRequest() {
    }

    public abstract String method();

    public static GZHttpRequest.Builder newBuilder() {
        return new GZHttpRequestBuilderImpl();
    }

    public static GZHttpRequest.Builder newBuilder(URI uri) {
        return new GZHttpRequestBuilderImpl(uri);
    }

    public interface GZBodyPublisher extends Publisher<ByteBuffer> {
        long contentLength();
    }

    public abstract URI uri();

    public interface Builder {
        GZHttpRequest.Builder uri(URI var1);

        GZHttpRequest.Builder expectContinue(boolean var1);

        GZHttpRequest.Builder version(GZVersion var1);

        GZHttpRequest.Builder header(String var1, String var2);

        GZHttpRequest.Builder headers(String... var1);

        GZHttpRequest.Builder timeout(Duration var1);

        GZHttpRequest.Builder setHeader(String var1, String var2);

        GZHttpRequest.Builder GET();

        GZHttpRequest.Builder POST(GZBodyPublisher var1);

        GZHttpRequest.Builder PUT(GZBodyPublisher var1);

        GZHttpRequest.Builder DELETE();

        GZHttpRequest.Builder method(String var1, GZBodyPublisher var2);

        GZHttpRequest build();

        GZHttpRequest.Builder copy();

    }
}
