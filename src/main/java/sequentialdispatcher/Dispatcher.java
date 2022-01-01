package sequentialdispatcher;

import java.util.concurrent.CompletableFuture;

public interface Dispatcher<DISPATCH_RESULT> {

    CompletableFuture<DISPATCH_RESULT> dispatch();

    interface Listener<TASK_INPUT, TASK_RESULT> {
        void onDispatch(TASK_INPUT var1);

        void onCompletion(TASK_INPUT var1, TASK_RESULT var2);

        void onFailure(TASK_INPUT var1, Throwable var2);

        void onTimeoue(TASK_INPUT var1);
    }

    @FunctionalInterface
    interface InterruptableWorker<TASK_INPUT, TASK_RESULT> {
        TASK_RESULT execute(TASK_INPUT var1) throws InterruptedException;
    }

    @FunctionalInterface
    interface Worker<TASK_INPUT, TASK_RESULT> {
        CompletableFuture<TASK_RESULT> execute(TASK_INPUT var1);
    }
}
