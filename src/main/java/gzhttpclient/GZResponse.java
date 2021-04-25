package gzhttpclient;

class GZResponse {
    final GZHttpHeaders headers;
    final int statusCode;
    final GZHttpRequestImpl request;
    final GZExchange<?> exchange;
    final GZHttpClient.GZVersion version;
    final boolean isConnectResponse;

    GZResponse(GZHttpRequestImpl req, GZExchange<?> exchange, GZHttpHeaders headers, GZHttpConnection connection, int statusCode, GZHttpClient.GZVersion version, boolean isConnectResponse) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.request = req;
        this.exchange = exchange;
        this.version = version;
        this.isConnectResponse = isConnectResponse;
    }

    GZHttpRequestImpl request() {
        return this.request;
    }

    GZHttpClient.GZVersion version() {
        return this.version;
    }

    GZHttpHeaders headers() {
        return this.headers;
    }

    int statusCode() {
        return this.statusCode;
    }

}
