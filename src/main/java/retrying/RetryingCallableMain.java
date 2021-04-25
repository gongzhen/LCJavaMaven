package retrying;

import helper.PrintUtils;
//import org.apache.log4j.BasicConfigurator;
//import org.slf4j.LoggerFactory;
import retrying.retryingcallable.ExponentialBackoffRetryPolicy;
import retrying.retryingcallable.RetryPolicy;
import retrying.retryingcallable.RetryingCallable;

import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
//import org.slf4j.Logger;

public class RetryingCallableMain {

//    private static final Logger LOG = LoggerFactory.getLogger(RetryingCallableMain.class);
    Map<String, String> map = new HashMap<>();

    private HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static String urlEndpoint = "https://postman-echo.com/get";
    private static int count = 1;

    public CompletableFuture<String> getRequest() throws Exception {
        try {
            URI uri = URI.create(urlEndpoint + "?foo1=bar1&foo2=bar2");
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .uri(uri)
                    .build();
            CompletableFuture<String> response = httpClient
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    //.thenApply(response1 -> response1.statusCode());
                    //.thenApply(HttpResponse::statusCode)
                    .thenApply(new Function<HttpResponse<String>, String>() {
                        @Override
                        public String apply(HttpResponse<String> response) {
                            PrintUtils.printString(">>>>>>:" + count);
                            return response.body();
                        }
                    });
//            if (count != 3) {
//                throw new Exception("Exception=>count != " + count);
//            }
//            count++;
//            PrintUtils.printString("xxxxxx:" + count);
            return response;
        } catch (Exception e) {
            throw e;
        }
    }

    public CompletableFuture<String> getString() throws Exception {
        if (count == 4) {
            PrintUtils.printString("getString count: " + count);
            return CompletableFuture.completedFuture(String.format("count=%d", count));
        } else {
            PrintUtils.printString("exception throw: " + count);
            throw new Exception(String.format("############ exception count is %d", count++));
        }
    }

    public static void main(String[] args) throws Exception {
//        BasicConfigurator.configure();
        RetryingCallableMain obj =   new RetryingCallableMain();
        RetryPolicy retryPolicy = new ExponentialBackoffRetryPolicy.Builder()
                .withImmediatelyUnrecoverableThrowables(UnknownHostException.class)
                .withFixedDelay(1000)
                .withMaxAttempts(1)
                .build();
        Callable<CompletableFuture<String>> callable = () -> {
//            return obj.getRequest();
            return obj.getString();
        };

        RetryingCallable<CompletableFuture<String>> retryingCallable = new RetryingCallable<>(callable, retryPolicy);

        CompletableFuture<String> res;
        try {
            res = retryingCallable.call();
            PrintUtils.printString("\nres: " + res.get());
        } catch (Exception ex) {
            PrintUtils.printString("\nPrintUtils RetryingCallable ex:" + ex.getMessage());
        }
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {

        }
        PrintUtils.printString("Main End.");
    }
}

/**
 * retry is 3
 * exception throw: 1
 * 0 [main] INFO retrying.retryingcallable.RetryingCallable  - Transient throwable caught when calling retrying.RetryingCallableMain$$Lambda$76/0x0000000800153840@5bd03f44 after 1 attempts.  Will retry again in 1000 milliseconds.
 * exception throw: 2
 * 1006 [main] INFO retrying.retryingcallable.RetryingCallable  - Transient throwable caught when calling retrying.RetryingCallableMain$$Lambda$76/0x0000000800153840@5bd03f44 after 2 attempts.  Will retry again in 1000 milliseconds.
 * exception throw: 3
 * 2012 [main] DEBUG retrying.retryingcallable.ExponentialBackoffRetryPolicy  - Returning negative nextDelayMillis due to 3 attempts out of 3.
 *
 * PrintUtils RetryingCallable ex:############ exception count is 3
 * 2016 [main] DEBUG retrying.RetryingCallableMain  - >>>>>>>>>>>>>>>>>>>>>>>>>>>>
 * java.lang.Exception: ############ exception count is 3
 * 	at retrying.RetryingCallableMain.getString(RetryingCallableMain.java:66)
 * 	at retrying.RetryingCallableMain.lambda$main$0(RetryingCallableMain.java:80)
 * 	at retrying.retryingcallable.RetryingCallable.call(RetryingCallable.java:75)
 * 	at retrying.RetryingCallableMain.main(RetryingCallableMain.java:87)
 * Main End.
 *
 * Process finished with exit code 0
 *
 * retry is 4
 * exception throw: 1
 * 0 [main] INFO retrying.retryingcallable.RetryingCallable  - Transient throwable caught when calling retrying.RetryingCallableMain$$Lambda$76/0x0000000800156040@470f1802 after 1 attempts.  Will retry again in 1000 milliseconds.
 * exception throw: 2
 * 1003 [main] INFO retrying.retryingcallable.RetryingCallable  - Transient throwable caught when calling retrying.RetryingCallableMain$$Lambda$76/0x0000000800156040@470f1802 after 2 attempts.  Will retry again in 1000 milliseconds.
 * exception throw: 3
 * 2008 [main] INFO retrying.retryingcallable.RetryingCallable  - Transient throwable caught when calling retrying.RetryingCallableMain$$Lambda$76/0x0000000800156040@470f1802 after 3 attempts.  Will retry again in 1000 milliseconds.
 * getString count: 4
 *
 * res: count=4
 * Main End.
 */
