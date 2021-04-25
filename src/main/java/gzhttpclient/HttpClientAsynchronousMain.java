package gzhttpclient;

import helper.PrintUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HttpClientAsynchronousMain {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();


    private static final GZHttpClient gzHttpClient = GZHttpClient.newBuilder()
            .version(GZHttpClient.GZVersion.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();


    public static void main(String[] args) {

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://httpbin.org/anything"))
                .setHeader("User-Agent", "Java 11")
                .build();

//        GZHttpRequest gzHttpRequest = GZHttpRequest.newBuilder()
//                .GET()
//                .uri(URI.create("https://httpbin.org/anything"))
//                .setHeader("User-Agent", "Java 11")
//                .build();
//
//        CompletableFuture<HttpResponse<String>> response = httpClient
//                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
//        try {
//            String result = response.thenApply(response1 -> {
//                return response1.body();
//            }).get(5, TimeUnit.SECONDS);
//            PrintUtils.printString("response: " + result);
//        } catch (InterruptedException e) {
//        } catch (TimeoutException e) {
//        } catch (ExecutionException e) {
//        }

//        gzHttpClient.sendAsync()

    }
}
