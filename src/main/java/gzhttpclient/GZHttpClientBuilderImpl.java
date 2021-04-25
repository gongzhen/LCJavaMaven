package gzhttpclient;

import gzhttpclient.GZHttpClient.Builder;
import gzhttpclient.GZHttpClient.GZVersion;
import helper.GZPreconditions;

import java.time.Duration;
import java.util.concurrent.Executor;

//3: GZHttpClientBuilderImpl implement Builder
// GZHttpClientBuilderImpl stores version, connectTimout
public class GZHttpClientBuilderImpl implements Builder {

    GZVersion version;
    Duration connectTimeout;
    Executor executor;

    public GZHttpClientBuilderImpl() {
    }

    @Override
    public GZHttpClientBuilderImpl version(GZVersion version) {
        GZPreconditions.checkNotNull(version);
        this.version = version;
        return this;
    }

    @Override
    public GZHttpClientBuilderImpl connectTimeout(Duration duration) {
        GZPreconditions.checkNotNull(duration);
        if (!duration.isNegative() && !Duration.ZERO.equals(duration)) {
            this.connectTimeout = duration;
            return this;
        } else {
            throw new IllegalArgumentException("Invalid duration: " + duration);
        }
    }

    @Override
    public GZHttpClient build() {
        // return GZHttpClientFacade which extends from GZHttpClient.
        return GZHttpClientImpl.create(this);
    }

}
