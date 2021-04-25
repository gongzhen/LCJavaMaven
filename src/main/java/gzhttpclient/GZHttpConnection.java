package gzhttpclient;

import java.io.Closeable;
import java.net.InetSocketAddress;

abstract class GZHttpConnection implements Closeable {
    final InetSocketAddress address;
    private final GZHttpClientImpl client;
    GZHttpConnection(InetSocketAddress address, GZHttpClientImpl client) {
        this.address = address;
        this.client = client;
    }
}
