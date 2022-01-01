package completablefuture;

import helper.PrintUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class CompletableAsyncTest1 {

    public static void main(String[] args) {
        CompletableFuture<Double> completableFuture = getFutureObject();
        PrintUtils.printString("line 11: ");
        completableFuture.whenComplete((doubleValue, throwable) -> {
            PrintUtils.printString("line 13: whenComplete:" + doubleValue);
        });

        completableFuture.whenCompleteAsync((doubleValue, throwable) -> {
            PrintUtils.printString("line 16: whenCompleteAsync:" + doubleValue);
        });
        completableFuture.whenCompleteAsync((doubleValue, throwable) -> {
            PrintUtils.printString("line 19: whenCompleteAsync:" + doubleValue);
        }, Executors.newSingleThreadExecutor());
        PrintUtils.printString("line 21: ");
    }

    static CompletableFuture<Double> getFutureObject() {
        CompletableFuture<Double> completableFuture = new CompletableFuture<>();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
            completableFuture.complete(999.99);
        }).start();
        return completableFuture;
    }
}
