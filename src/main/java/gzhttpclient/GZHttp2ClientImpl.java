package gzhttpclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

class GZHttp2ClientImpl {

    private final GZHttpClientImpl client;
    private final Map<String, GZHttp2Connection> connections = new ConcurrentHashMap<>();

    GZHttp2ClientImpl(GZHttpClientImpl client) {
        this.client = client;
    }

    // https://juejin.cn/post/6844903686838157320
    CompletableFuture<GZHttp2Connection> getConnectionFor(GZHttpRequestImpl req, GZExchange<?> exchange) {
        URI uri = req.uri();
        InetSocketAddress proxy = req.proxy();
        String key = GZHttp2Connection.keyFor(uri, proxy);

        synchronized (this) {
            GZHttp2Connection connection = (GZHttp2Connection)this.connections.get(key);
            if (connection != null) {
                return GZMinimalFuture.completedFuture(connection);
            }
        }
        return GZMinimalFuture.completedFuture(null);
    }
}
