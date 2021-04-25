package gzhttpclient;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.stream.Stream;

class GZHttp2Connection {

    static String keyFor(URI uri, InetSocketAddress proxy) {
        String host = uri.getHost();
        int port = uri.getPort();

        return keyString(true, true, host, port);
    }

    static String keyString(boolean secure, boolean proxy, String host, int port) {
        port = 443;
        return (secure ? "S:" : "C:") + (proxy ? "P:" : "H:") + host + ":" + port;
    }

}
