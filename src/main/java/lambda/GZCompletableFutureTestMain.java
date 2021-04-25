package lambda;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class GZCompletableFutureTestMain<T> {

    private static class GZExchangeImpl<T> {
        public GZExchangeImpl() {
            System.out.println("Constructor GZExchangeImpl");
        }

        public CompletableFuture<GZExchangeImpl<T>> sendHeadersAsync() {
            return CompletableFuture.supplyAsync(() -> {
                return new GZExchangeImpl<>();
            });
        }
    }

    private static class GZResponse {
        public GZResponse() {
            System.out.println("Constructor GZResponse");
        }
    }

    public CompletableFuture<GZResponse> checkFor407(GZExchangeImpl<T> ex,
                                                     Throwable t,
                                                     Function<GZExchangeImpl<T>, CompletableFuture<GZResponse>> andThen) {
        return( (CompletableFuture)andThen.apply(ex)).thenCompose(Function.identity());
    }

//    public Object checkFor407(GZExchangeImpl<T> ex, Throwable t, Function<GZExchangeImpl<T>, CompletableFuture<GZResponse>> andThen) {
//        return andThen.apply(ex);
//    }

    public CompletableFuture<GZExchangeImpl<T>> sendHeadersAsync() {
        // https://colobu.com/2016/02/29/Java-CompletableFuture/
        return CompletableFuture.supplyAsync(() -> {
            return new GZExchangeImpl<T>();
        });
    }

    private CompletableFuture<GZResponse> sendRequestBody(Object ex) {
        return CompletableFuture.<GZResponse>supplyAsync(() -> {
           return new GZResponse();
        });
    }

    public void test() {
        Function after407Check = this::sendRequestBody;

        Function<GZExchangeImpl<T>, CompletableFuture<GZResponse>> afterExch407Check = (ex) -> {
            return ex.sendHeadersAsync().handle((r, t) -> {
                return this.checkFor407(r, t, after407Check);
            }).thenCompose(t -> t);
        };
    }

    public static void main(String[] args) {
        GZCompletableFutureTestMain gz = new GZCompletableFutureTestMain();
        gz.test();
    }
}
