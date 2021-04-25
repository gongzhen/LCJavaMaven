package gzhttpclient;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import gzhttpclient.GZHttpResponse.BodyHandler;
// 1: Create abstract class HttpClient
// newBuilder() will return a object implements Builder interface.
// GZHttpClientBuilderImpl implement Builder.
public abstract class GZHttpClient {
    protected GZHttpClient() {
    }

    public static GZHttpClient.Builder newBuilder() {
        return new GZHttpClientBuilderImpl();
    }

    public static enum GZRedirect {

    }

    public static enum GZVersion {
        HTTP_1_1,
        HTTP_2;

        private GZVersion() {

        }
    }

    public abstract <T> CompletableFuture<GZHttpResponse<T>> sendAsync(GZHttpRequest var1, BodyHandler<T> var2);

    public abstract <T> CompletableFuture<GZHttpResponse<T>> sendAsync(GZHttpRequest var1, BodyHandler<T> var2, GZHttpResponse.PushPromiseHandler<T> var3);

    // 2: Create interface Builder: build(), version, connectTimeout.
    // GZHttpClientBuilderImpl will implement Builder interface.
    public interface Builder {
        GZHttpClient.Builder connectTimeout(Duration var1);

        GZHttpClient.Builder version(GZHttpClient.GZVersion var1);

        GZHttpClient build();
    }

}
